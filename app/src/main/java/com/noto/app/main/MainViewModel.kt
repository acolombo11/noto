package com.noto.app.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Layout
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.LibraryListSorting
import com.noto.app.domain.model.SortingOrder
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel(
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val storage: LocalStorage,
) : ViewModel() {

    val libraries = libraryRepository.getLibraries()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val archivedLibraries = libraryRepository.getArchivedLibraries()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val layout = storage.get(Constants.LibraryListLayoutKey)
        .filterNotNull()
        .map { Layout.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Layout.Grid)

    val sorting = storage.get(Constants.LibraryListSortingKey)
        .filterNotNull()
        .map { LibraryListSorting.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, LibraryListSorting.CreationDate)

    val sortingOrder = storage.get(Constants.LibraryListSortingOrderKey)
        .filterNotNull()
        .map { SortingOrder.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, SortingOrder.Descending)

    val isShowNotesCount = storage.get(Constants.ShowNotesCountKey)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    fun countNotes(libraryId: Long): Int = runBlocking {
        noteRepository.countNotesByLibraryId(libraryId)
    }

    fun updateLayout(value: Layout) = viewModelScope.launch {
        storage.put(Constants.LibraryListLayoutKey, value.toString())
    }

    fun updateSorting(value: LibraryListSorting) = viewModelScope.launch {
        storage.put(Constants.LibraryListSortingKey, value.toString())
    }

    fun updateSortingOrder(value: SortingOrder) = viewModelScope.launch {
        storage.put(Constants.LibraryListSortingOrderKey, value.toString())
    }

    fun updateLibraryPosition(library: Library, position: Int) = viewModelScope.launch {
        libraryRepository.updateLibrary(library.copy(position = position))
    }
}