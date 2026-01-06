package com.springyknit.app.data.db

import androidx.room.*
import com.springyknit.app.data.model.KnittingProject
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("SELECT * FROM knitting_projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<KnittingProject>>

    @Query("SELECT * FROM knitting_projects WHERE id = :id")
    suspend fun getProjectById(id: Long): KnittingProject?

    @Query("SELECT * FROM knitting_projects WHERE id = :id")
    fun getProjectByIdFlow(id: Long): Flow<KnittingProject?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: KnittingProject): Long

    @Update
    suspend fun updateProject(project: KnittingProject)

    @Delete
    suspend fun deleteProject(project: KnittingProject)

    @Query("UPDATE knitting_projects SET pdfUri = :pdfUri, updatedAt = :updatedAt WHERE id = :projectId")
    suspend fun updatePdfUri(projectId: Long, pdfUri: String?, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE knitting_projects SET drawingDataJson = :drawingDataJson, updatedAt = :updatedAt WHERE id = :projectId")
    suspend fun updateDrawingData(projectId: Long, drawingDataJson: String?, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE knitting_projects SET currentPdfPage = :page, updatedAt = :updatedAt WHERE id = :projectId")
    suspend fun updateCurrentPdfPage(projectId: Long, page: Int, updatedAt: Long = System.currentTimeMillis())
}
