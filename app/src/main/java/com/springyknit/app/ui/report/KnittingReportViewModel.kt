package com.springyknit.app.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.springyknit.app.data.model.DrawingData
import com.springyknit.app.data.model.KnittingProject
import com.springyknit.app.data.repository.ProjectRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KnittingReportViewModel(
    private val repository: ProjectRepository,
    private val projectId: Long
) : ViewModel() {

    private val _project = MutableStateFlow<KnittingProject?>(null)
    val project: StateFlow<KnittingProject?> = _project.asStateFlow()

    private val _saveState = MutableStateFlow(SaveState.SAVED)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private var autoSaveJob: Job? = null

    init {
        loadProject()
    }

    private fun loadProject() {
        viewModelScope.launch {
            repository.getProjectByIdFlow(projectId).collect { project ->
                _project.value = project
            }
        }
    }

    fun updatePdfUri(uri: String?) {
        viewModelScope.launch {
            _saveState.value = SaveState.SAVING
            repository.updatePdfUri(projectId, uri)
            scheduleSaveComplete()
        }
    }

    fun updateDrawingData(drawingData: DrawingData) {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            _saveState.value = SaveState.SAVING
            // Debounce: wait a bit before saving to avoid too many writes
            delay(500)
            repository.updateDrawingData(projectId, drawingData)
            scheduleSaveComplete()
        }
    }

    fun updateCurrentPdfPage(page: Int) {
        viewModelScope.launch {
            _saveState.value = SaveState.SAVING
            repository.updateCurrentPdfPage(projectId, page)
            scheduleSaveComplete()
        }
    }

    private fun scheduleSaveComplete() {
        viewModelScope.launch {
            delay(300)
            _saveState.value = SaveState.SAVED
        }
    }

    enum class SaveState {
        SAVING, SAVED
    }
}

class KnittingReportViewModelFactory(
    private val repository: ProjectRepository,
    private val projectId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KnittingReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KnittingReportViewModel(repository, projectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
