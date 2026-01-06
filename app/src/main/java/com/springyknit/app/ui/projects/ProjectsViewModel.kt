package com.springyknit.app.ui.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.springyknit.app.data.model.KnittingProject
import com.springyknit.app.data.repository.ProjectRepository
import com.springyknit.app.util.ProjectNameGenerator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProjectsViewModel(
    private val repository: ProjectRepository
) : ViewModel() {

    val projects: StateFlow<List<KnittingProject>> = repository.allProjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createProject(): Long {
        var projectId = -1L
        viewModelScope.launch {
            val name = ProjectNameGenerator.generate()
            projectId = repository.createProject(name)
        }
        return projectId
    }

    suspend fun createProjectAndGetId(): Long {
        val name = ProjectNameGenerator.generate()
        return repository.createProject(name)
    }

    fun deleteProject(project: KnittingProject) {
        viewModelScope.launch {
            repository.deleteProject(project)
        }
    }
}

class ProjectsViewModelFactory(
    private val repository: ProjectRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProjectsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
