package com.noto.app.filtered

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.MenuItemCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.components.*
import com.noto.app.databinding.FilteredFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Font
import com.noto.app.folder.NoteItemModel
import com.noto.app.folder.noteItem
import com.noto.app.util.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FilteredFragment : Fragment() {

    private val viewModel by viewModel<FilteredViewModel> { parametersOf(args.model) }

    private val args by navArgs<FilteredFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FilteredFragmentBinding.inflate(inflater, container, false).withBinding {
        setupMixedTransitions()
        setupState()
        setupListeners()
    }

    private fun FilteredFragmentBinding.setupListeners() {
        tb.setOnClickListener {
            rv.smoothScrollToPosition(0)
        }

        fab.setOnClickListener {
            navController?.navigateSafely(FilteredFragmentDirections.actionFilteredFragmentToSelectFolderDialogFragment(longArrayOf()))
        }

        bab.setNavigationOnClickListener {
            navController?.navigateSafely(FilteredFragmentDirections.actionFilteredFragmentToMainFragment())
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    if (viewModel.isSearchEnabled.value)
                        viewModel.disableSearch()
                    else
                        viewModel.enableSearch()
                    true
                }
                R.id.change_visibility -> {
                    if (viewModel.notesGroupedByFolderVisibility.value.any { it.value } || viewModel.notesGroupedByDateVisibility.value.any { it.value })
                        viewModel.collapseAll()
                    else
                        viewModel.expandAll()
                    true
                }
                else -> false
            }
        }

        bab.setOnSwipeGestureListener {
            navController?.navigateSafely(FilteredFragmentDirections.actionFilteredFragmentToMainFragment())
        }

        activity?.onBackPressedDispatcher?.addCallback {
            when {
                viewModel.isSearchEnabled.value -> viewModel.disableSearch()
                viewModel.quickExit.value -> activity?.finish()
                else -> navController?.navigateSafely(FilteredFragmentDirections.actionFilteredFragmentToMainFragment(exit = true))
            }
        }
    }


    @OptIn(FlowPreview::class)
    private fun FilteredFragmentBinding.setupState() {
        context?.let { context ->
            tvTitle.setTextColor(context.colorResource(args.model.color.toResource()))
            fab.backgroundTintList = context.colorResource(args.model.color.toResource()).toColorStateList()
            tvTitle.text = when (args.model) {
                FilteredItemModel.All -> R.string.all
                FilteredItemModel.Recent -> R.string.recent
                FilteredItemModel.Scheduled -> R.string.scheduled
                FilteredItemModel.Archived -> R.string.archived
            }.let(context::stringResource)
        }
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.itemAnimator = VerticalListItemAnimator()
        tvNotesCount.animationInterpolator = DefaultInterpolator()
        tvNotesCount.typeface = context?.tryLoadingFontResource(R.font.nunito_semibold)
        val layoutManager = rv.layoutManager as LinearLayoutManager
        val savedStateHandle = navController?.currentBackStackEntry?.savedStateHandle

        when (args.model) {
            FilteredItemModel.All, FilteredItemModel.Archived -> {
                combine(
                    viewModel.notesGroupedByFolder,
                    viewModel.notesGroupedByFolderVisibility,
                    viewModel.font,
                    viewModel.searchTerm,
                ) { notes, notesVisibility, font, searchTerm ->
                    setupNotesGroupedByFolder(notes, notesVisibility, font, searchTerm)
                }.launchIn(lifecycleScope)
            }
            FilteredItemModel.Recent, FilteredItemModel.Scheduled -> {
                combine(
                    viewModel.notesGroupedByDate,
                    viewModel.notesGroupedByDateVisibility,
                    viewModel.font,
                    viewModel.searchTerm,
                ) { notes, notesVisibility, font, searchTerm ->
                    setupNotesGroupedByDate(notes, notesVisibility, font, searchTerm)
                }.launchIn(lifecycleScope)
            }
        }

        viewModel.isSearchEnabled
            .onEach { isSearchEnabled -> if (isSearchEnabled) enableSearch() else disableSearch() }
            .launchIn(lifecycleScope)

        etSearch.textAsFlow()
            .asSearchFlow()
            .onEach { searchTerm -> viewModel.setSearchTerm(searchTerm) }
            .launchIn(lifecycleScope)

        val menuItem = bab.menu.findItem(R.id.change_visibility)
        val expandText = context?.stringResource(R.string.expand)
        val collapseText = context?.stringResource(R.string.collapse)

        viewModel.notesGroupedByFolderVisibility
            .onEach { visibility ->
                if (visibility.any { it.value }) {
                    menuItem.icon = context?.drawableResource(R.drawable.ic_round_collapse_24)
                    menuItem.title = collapseText
                    MenuItemCompat.setContentDescription(menuItem, collapseText)
                } else {
                    menuItem.icon = context?.drawableResource(R.drawable.ic_round_expand_24)
                    menuItem.title = expandText
                    MenuItemCompat.setContentDescription(menuItem, expandText)
                }
            }
            .launchIn(lifecycleScope)

        viewModel.notesGroupedByDateVisibility
            .onEach { visibility ->
                if (visibility.any { it.value }) {
                    menuItem.icon = context?.drawableResource(R.drawable.ic_round_collapse_24)
                    menuItem.title = collapseText
                    MenuItemCompat.setContentDescription(menuItem, collapseText)
                } else {
                    menuItem.icon = context?.drawableResource(R.drawable.ic_round_expand_24)
                    menuItem.title = expandText
                    MenuItemCompat.setContentDescription(menuItem, expandText)
                }
            }
            .launchIn(lifecycleScope)

        combine(
            viewModel.isRememberScrollingPosition,
            viewModel.scrollingPosition
        ) { isRememberScrollingPosition, scrollingPosition ->
            layoutManager.postOnAnimation {
                rv.post {
                    if (isRememberScrollingPosition) {
                        layoutManager.scrollToPosition(scrollingPosition)
                    }
                }
            }
        }.launchIn(lifecycleScope)

        rv.scrollPositionAsFlow()
            .debounce(DebounceTimeoutMillis)
            .onEach {
                val scrollingPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (scrollingPosition != -1) viewModel.updateScrollingPosition(scrollingPosition)
            }
            .launchIn(lifecycleScope)

        root.keyboardVisibilityAsFlow()
            .onEach { isVisible ->
                fab.isVisible = !isVisible
                bab.isVisible = !isVisible
                tilSearch.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                    anchorId = if (isVisible) View.NO_ID else fab.id
                    gravity = if (isVisible) Gravity.BOTTOM else Gravity.TOP
                }
            }
            .launchIn(lifecycleScope)

        savedStateHandle?.getLiveData<Long>(Constants.FolderId)
            ?.observe(viewLifecycleOwner) { folderId ->
                if (folderId != null) {
                    lifecycleScope.launch {
                        delay(150) // Wait for the fragment to be destroyed
                        savedStateHandle.remove<Long>(Constants.FolderId)
                        navController?.navigateSafely(
                            FilteredFragmentDirections.actionFilteredFragmentToNoteFragment(
                                folderId,
                                selectedNoteIds = longArrayOf()
                            )
                        )
                    }
                }
            }

        if (isCurrentLocaleArabic()) {
            tvNotesCount.isVisible = false
            tvNotesCountRtl.isVisible = true
        } else {
            tvNotesCount.isVisible = true
            tvNotesCountRtl.isVisible = false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun FilteredFragmentBinding.setupNotesGroupedByDate(
        state: UiState<NotesGroupedByDate>,
        notesVisibility: Map<LocalDate, Boolean>,
        font: Font,
        searchTerm: String,
    ) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator()
            is UiState.Success -> {
                val notes = state.value
                val notesCount = notes.map { it.value.count() }.sum()
                tvNotesCount.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)
                tvNotesCountRtl.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)

                rv.withModels {

                    context?.let { context ->
                        if (notes.values.all { it.isEmpty() }) {
                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(R.string.no_notes_found))
                            }
                        } else {
                            notes.forEach { (date, notes) ->
                                val isVisible = notesVisibility[date] ?: true
                                val noteIds = notes.map { it.second.note.id }.toLongArray()

                                headerItem {
                                    id(date.dayOfYear)
                                    title(date.format())
                                    isVisible(isVisible)
                                    onClickListener { _ -> viewModel.toggleVisibilityForDate(date) }
                                }

                                if (isVisible)
                                    notes.forEach { pair ->
                                        noteItem {
                                            id(pair.second.note.id)
                                            model(pair.second)
                                            font(font)
                                            color(pair.first.color)
                                            searchTerm(searchTerm)
                                            previewSize(pair.first.notePreviewSize)
                                            isShowCreationDate(pair.first.isShowNoteCreationDate)
                                            isShowAccessDate(true)
                                            isManualSorting(false)
                                            onClickListener { _ ->
                                                navController
                                                    ?.navigateSafely(
                                                        FilteredFragmentDirections.actionFilteredFragmentToNoteFragment(
                                                            pair.second.note.folderId,
                                                            noteId = pair.second.note.id,
                                                            selectedNoteIds = noteIds,
                                                        )
                                                    )
                                            }
                                            onLongClickListener { _ ->
                                                navController
                                                    ?.navigateSafely(
                                                        FilteredFragmentDirections.actionFilteredFragmentToNoteDialogFragment(
                                                            pair.second.note.folderId,
                                                            pair.second.note.id,
                                                            R.id.folderFragment,
                                                            selectedNoteIds = noteIds,
                                                        )
                                                    )
                                                true
                                            }
                                            onDragHandleTouchListener { _, _ -> false }
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun FilteredFragmentBinding.setupNotesGroupedByFolder(
        state: UiState<Map<Folder, List<NoteItemModel>>>,
        notesVisibility: Map<Folder, Boolean>,
        font: Font,
        searchTerm: String,
    ) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator()
            is UiState.Success -> {
                val notes = state.value
                val notesCount = notes.map { it.value.count() }.sum()
                tvNotesCount.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)
                tvNotesCountRtl.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)

                rv.withModels {

                    context?.let { context ->
                        if (notes.values.all { it.isEmpty() }) {
                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(R.string.no_notes_found))
                            }
                        } else {
                            notes.forEach { (folder, notes) ->
                                val isVisible = notesVisibility[folder] ?: true
                                val noteIds = notes.map { it.note.id }.toLongArray()

                                headerItem {
                                    id("folder ${folder.id}")
                                    title(folder.getTitle(context))
                                    color(folder.color)
                                    isVisible(isVisible)
                                    onClickListener { _ -> viewModel.toggleVisibilityForFolder(folder.id) }
                                    onCreateClickListener { _ ->
                                        navController?.navigateSafely(
                                            FilteredFragmentDirections.actionFilteredFragmentToNoteFragment(
                                                folder.id,
                                                selectedNoteIds = longArrayOf()
                                            )
                                        )
                                    }
                                    onLongClickListener { _ ->
                                        navController?.navigateSafely(FilteredFragmentDirections.actionFilteredFragmentToFolderFragment(folder.id))
                                        true
                                    }
                                }

                                if (isVisible)
                                    notes.forEach { model ->
                                        noteItem {
                                            id(model.note.id)
                                            model(model)
                                            font(font)
                                            color(folder.color)
                                            searchTerm(searchTerm)
                                            previewSize(folder.notePreviewSize)
                                            isShowCreationDate(folder.isShowNoteCreationDate)
                                            isManualSorting(false)
                                            onClickListener { _ ->
                                                navController
                                                    ?.navigateSafely(
                                                        FilteredFragmentDirections.actionFilteredFragmentToNoteFragment(
                                                            model.note.folderId,
                                                            noteId = model.note.id,
                                                            selectedNoteIds = noteIds,
                                                        )
                                                    )
                                            }
                                            onLongClickListener { _ ->
                                                navController
                                                    ?.navigateSafely(
                                                        FilteredFragmentDirections.actionFilteredFragmentToNoteDialogFragment(
                                                            model.note.folderId,
                                                            model.note.id,
                                                            R.id.folderFragment,
                                                            selectedNoteIds = noteIds,
                                                        )
                                                    )
                                                true
                                            }
                                            onDragHandleTouchListener { _, _ -> false }
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun FilteredFragmentBinding.enableSearch() {
        tilSearch.isVisible = true
        tilSearch.postDelayed({ etSearch.requestFocus() }, DefaultAnimationDuration)
        activity?.showKeyboard(etSearch)
    }

    private fun FilteredFragmentBinding.disableSearch() {
        tilSearch.isVisible = false
        activity?.hideKeyboard(etSearch)
        etSearch.text = null
    }

}