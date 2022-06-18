package com.noto.app.note

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.UndoRedoDialogFragmentBinding
import com.noto.app.util.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class UndoRedoDialogFragment : BaseDialogFragment() {

    private val viewModel by sharedViewModel<NoteViewModel> { parametersOf(args.folderId, args.noteId) }

    private val args by navArgs<UndoRedoDialogFragmentArgs>()

    private val clipboardManager by lazy { context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager? }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = UndoRedoDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupBaseDialogFragment()
        setupState()
    }

    private fun UndoRedoDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root)
        .apply {
            context?.let { context ->
                tvDialogTitle.text = context.stringResource(if (args.isUndo) R.string.undo_history else R.string.redo_history)
            }
        }

    private fun UndoRedoDialogFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv.itemAnimator = VerticalListItemAnimator()

        if (args.isTitle) {
            if (args.isUndo) {
                val currentText = viewModel.note.value.title
                val items = viewModel.titleHistory.replayCache.subListOld(currentText)
                setupItems(items, currentText) { title ->
                    viewModel.setIsUndoOrRedo()
                    viewModel.setNoteTitle(title)
                    dismiss()
                }
            } else {
                val currentText = viewModel.note.value.title
                val items = viewModel.titleHistory.replayCache.subListNew(currentText)
                setupItems(items, currentText) { title ->
                    viewModel.setIsUndoOrRedo()
                    viewModel.setNoteTitle(title)
                    dismiss()
                }
            }
        } else {
            if (args.isUndo) {
                val currentText = viewModel.note.value.body
                val items = viewModel.bodyHistory.replayCache.subListOld(currentText)
                setupItems(items, currentText) { body ->
                    viewModel.setIsUndoOrRedo()
                    viewModel.setNoteBody(body)
                    dismiss()
                }
            } else {
                val currentText = viewModel.note.value.body
                val items = viewModel.bodyHistory.replayCache.subListNew(currentText)
                setupItems(items, currentText) { body ->
                    viewModel.setIsUndoOrRedo()
                    viewModel.setNoteBody(body)
                    dismiss()
                }
            }
        }
    }

    private fun UndoRedoDialogFragmentBinding.setupItems(items: List<String>, currentItem: String, onClick: (String) -> Unit) {
        rv.withModels {
            items.filter { it.isNotBlank() }.forEach { item ->
                undoRedoItem {
                    id(item)
                    text(item)
                    isSelected(item == currentItem)
                    onClickListener { _ -> onClick(item) }
                    onCopyClickListener { _ ->
                        val clipData = ClipData.newPlainText(viewModel.note.value.title, item)
                        clipboardManager?.setPrimaryClip(clipData)
                        context?.let { context ->
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                                parentFragment?.view?.snackbar(context.stringResource(R.string.text_copied), viewModel.folder.value)
                        }
                        dismiss()
                    }
                }
            }
        }
    }

    private fun List<String>.subListOld(currentText: String): List<String> {
        val indexOfCurrentText = indexOf(currentText)
        return subList(0, indexOfCurrentText + 1)
    }

    private fun List<String>.subListNew(currentText: String): List<String> {
        val indexOfCurrentText = indexOf(currentText)
        return subList(indexOfCurrentText, size)
    }
}