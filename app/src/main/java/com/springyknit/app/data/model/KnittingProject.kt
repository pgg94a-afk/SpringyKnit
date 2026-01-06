package com.springyknit.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "knitting_projects")
@TypeConverters(Converters::class)
data class KnittingProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val pdfUri: String? = null,
    val drawingDataJson: String? = null,
    val currentPdfPage: Int = 0
)

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromDrawingData(drawingData: DrawingData?): String? {
        return drawingData?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toDrawingData(json: String?): DrawingData? {
        return json?.let {
            val type = object : TypeToken<DrawingData>() {}.type
            gson.fromJson(it, type)
        }
    }
}
