package com.springyknit.app.data.repository

import com.springyknit.app.data.db.ProjectDao
import com.springyknit.app.data.model.DrawingData
import com.springyknit.app.data.model.KnittingProject
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val projectDao: ProjectDao) {

    private val gson = Gson()

    val allProjects: Flow<List<KnittingProject>> = projectDao.getAllProjects()

    suspend fun getProjectById(id: Long): KnittingProject? {
        return projectDao.getProjectById(id)
    }

    fun getProjectByIdFlow(id: Long): Flow<KnittingProject?> {
        return projectDao.getProjectByIdFlow(id)
    }

    suspend fun createProject(name: String): Long {
        val project = KnittingProject(name = name)
        return projectDao.insertProject(project)
    }

    suspend fun updateProject(project: KnittingProject) {
        projectDao.updateProject(project.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteProject(project: KnittingProject) {
        projectDao.deleteProject(project)
    }

    suspend fun updatePdfUri(projectId: Long, pdfUri: String?) {
        projectDao.updatePdfUri(projectId, pdfUri)
    }

    suspend fun updateDrawingData(projectId: Long, drawingData: DrawingData) {
        val json = gson.toJson(drawingData)
        projectDao.updateDrawingData(projectId, json)
    }

    suspend fun updateCurrentPdfPage(projectId: Long, page: Int) {
        projectDao.updateCurrentPdfPage(projectId, page)
    }
}
