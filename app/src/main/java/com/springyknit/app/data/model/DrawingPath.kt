package com.springyknit.app.data.model

import android.graphics.Color

data class DrawingPath(
    val points: List<Point>,
    val color: Int = Color.RED,
    val strokeWidth: Float = 5f
)

data class Point(
    val x: Float,
    val y: Float
)

data class DrawingData(
    val paths: List<DrawingPath> = emptyList(),
    val pdfPageIndex: Int = 0
)
