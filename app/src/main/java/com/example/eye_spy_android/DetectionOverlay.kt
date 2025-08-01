package com.example.eye_spy_android

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import android.graphics.Paint
import android.graphics.RectF
import com.example.eye_spy_android.Detection

@Composable
fun DetectionOverlay(
    detections: List<Detection>,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvas = drawContext.canvas.nativeCanvas
        val paint = Paint().apply {
            color = android.graphics.Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        
        val textPaint = Paint().apply {
            color = android.graphics.Color.RED
            textSize = 30f
            style = Paint.Style.FILL
        }
        
        for (detection in detections) {
            val rect = RectF(
                detection.boundingBox.left,
                detection.boundingBox.top,
                detection.boundingBox.right,
                detection.boundingBox.bottom
            )
            
            canvas.drawRect(rect, paint)
            canvas.drawText(
                "Glasses: ${(detection.confidence * 100).toInt()}%",
                detection.boundingBox.left,
                detection.boundingBox.top - 10,
                textPaint
            )
        }
    }
} 