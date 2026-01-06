package com.springyknit.app.ui.report

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.springyknit.app.data.model.DrawingData
import com.springyknit.app.data.model.DrawingPath
import com.springyknit.app.data.model.Point

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.RED
        strokeWidth = 8f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }

    private var currentPath = Path()
    private val paths = mutableListOf<Pair<Path, Paint>>()
    private val currentPoints = mutableListOf<Point>()
    private val allDrawingPaths = mutableListOf<DrawingPath>()

    var onDrawingChanged: ((DrawingData) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw all completed paths
        for ((path, pathPaint) in paths) {
            canvas.drawPath(path, pathPaint)
        }

        // Draw current path
        canvas.drawPath(currentPath, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path()
                currentPath.moveTo(x, y)
                currentPoints.clear()
                currentPoints.add(Point(x, y))
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath.lineTo(x, y)
                currentPoints.add(Point(x, y))
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                currentPath.lineTo(x, y)
                currentPoints.add(Point(x, y))

                // Save the path
                val newPaint = Paint(paint)
                paths.add(Pair(Path(currentPath), newPaint))

                // Save drawing path data
                val drawingPath = DrawingPath(
                    points = currentPoints.toList(),
                    color = paint.color,
                    strokeWidth = paint.strokeWidth
                )
                allDrawingPaths.add(drawingPath)

                currentPath = Path()
                currentPoints.clear()
                invalidate()

                // Notify change
                notifyDrawingChanged()
                return true
            }
        }
        return false
    }

    fun clear() {
        paths.clear()
        allDrawingPaths.clear()
        currentPath = Path()
        currentPoints.clear()
        invalidate()
        notifyDrawingChanged()
    }

    fun setDrawingData(drawingData: DrawingData) {
        paths.clear()
        allDrawingPaths.clear()

        for (drawingPath in drawingData.paths) {
            if (drawingPath.points.isEmpty()) continue

            val path = Path()
            val points = drawingPath.points

            path.moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                path.lineTo(points[i].x, points[i].y)
            }

            val pathPaint = Paint().apply {
                color = drawingPath.color
                strokeWidth = drawingPath.strokeWidth
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                isAntiAlias = true
            }

            paths.add(Pair(path, pathPaint))
            allDrawingPaths.add(drawingPath)
        }

        invalidate()
    }

    fun getDrawingData(pdfPageIndex: Int = 0): DrawingData {
        return DrawingData(
            paths = allDrawingPaths.toList(),
            pdfPageIndex = pdfPageIndex
        )
    }

    private fun notifyDrawingChanged() {
        onDrawingChanged?.invoke(getDrawingData())
    }

    fun setStrokeColor(color: Int) {
        paint.color = color
    }

    fun setStrokeWidth(width: Float) {
        paint.strokeWidth = width
    }
}
