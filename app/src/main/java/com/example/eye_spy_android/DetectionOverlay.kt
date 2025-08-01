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
import android.graphics.Rect
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
        
        // Box annotator with thickness (similar to supervision BoxAnnotator)
        val boxPaint = Paint().apply {
            color = android.graphics.Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 3f // thickness = 3
            isAntiAlias = true
        }
        
        // Label annotator with custom styling (similar to supervision LabelAnnotator)
        val labelPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 24f // text_scale = 1.0 equivalent
            style = Paint.Style.FILL
            isAntiAlias = true
            isFakeBoldText = true // text_thickness = 2 equivalent
        }
        
        // Background for text (similar to text_padding = 3)
        val textBackgroundPaint = Paint().apply {
            color = android.graphics.Color.argb(180, 0, 0, 0) // Semi-transparent black
            style = Paint.Style.FILL
        }
        
        for (detection in detections) {
            val rect = RectF(
                detection.boundingBox.left,
                detection.boundingBox.top,
                detection.boundingBox.right,
                detection.boundingBox.bottom
            )
            
            // Draw bounding box (similar to box_annotator.annotate)
            canvas.drawRect(rect, boxPaint)
            
            // Create label with confidence percentage (similar to custom label logic)
            val confidence = detection.confidence
            val label = "object ${(confidence * 100).toInt()}%"
            
            // Get bounding box coordinates for text positioning
            val x1 = detection.boundingBox.left
            val y1 = detection.boundingBox.top
            
            // Measure text for background
            val textBounds = android.graphics.Rect()
            labelPaint.getTextBounds(label, 0, label.length, textBounds)
            
            // Draw text background (similar to text_padding)
            val padding = 6f // text_padding = 3 equivalent
            val backgroundRect = RectF(
                x1 - padding,
                y1 - textBounds.height() - padding - 10, // Position above box
                x1 + textBounds.width() + padding,
                y1 - 10
            )
            canvas.drawRect(backgroundRect, textBackgroundPaint)
            
            // Draw label text (similar to cv2.putText)
            canvas.drawText(
                label,
                x1,
                y1 - 10,
                labelPaint
            )
        }
    }
} 