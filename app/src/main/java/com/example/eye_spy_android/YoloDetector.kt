package com.example.eye_spy_android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File

class YoloDetector(private val context: Context) {
    private var module: Module? = null
    private val inputSize = 640
    private val confidenceThreshold = 0.5f
    private val nmsThreshold = 0.4f
    
    init {
        try {
            module = Module.load(assetFilePath(context, "glasses_weights.pt"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun detect(bitmap: Bitmap): List<Detection> {
        if (module == null) return emptyList()
        
        try {
            val inputTensor = preprocessImage(bitmap)
            val output = module!!.forward(IValue.from(inputTensor))
            return postprocessOutput(output, bitmap.width, bitmap.height)
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
    
    private fun preprocessImage(bitmap: Bitmap): Tensor {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val inputArray = FloatArray(3 * inputSize * inputSize)
        
        for (y in 0 until inputSize) {
            for (x in 0 until inputSize) {
                val pixel = resizedBitmap.getPixel(x, y)
                // Normalize to [0, 1] and convert to RGB
                inputArray[0 * inputSize * inputSize + y * inputSize + x] = (pixel shr 16 and 0xFF) / 255.0f
                inputArray[1 * inputSize * inputSize + y * inputSize + x] = (pixel shr 8 and 0xFF) / 255.0f
                inputArray[2 * inputSize * inputSize + y * inputSize + x] = (pixel and 0xFF) / 255.0f
            }
        }
        
        return Tensor.fromBlob(inputArray, longArrayOf(1, 3, inputSize.toLong(), inputSize.toLong()))
    }
    
    private fun postprocessOutput(output: IValue, originalWidth: Int, originalHeight: Int): List<Detection> {
        val detections = mutableListOf<Detection>()
        
        try {
            val outputTensor = output.toTensor()
            val outputArray = outputTensor.dataAsFloatArray
            val outputShape = outputTensor.shape()
            
            // YOLO output format: [batch, num_detections, 6] where 6 = [x, y, w, h, confidence, class_id]
            // This is a simplified implementation - you may need to adjust based on your specific model
            val numDetections = outputShape[1].toInt()
            val featuresPerDetection = outputShape[2].toInt()
            
            for (i in 0 until numDetections) {
                val baseIndex = i * featuresPerDetection
                
                // Extract detection values (adjust indices based on your model's output format)
                val centerX = outputArray[baseIndex]
                val centerY = outputArray[baseIndex + 1]
                val width = outputArray[baseIndex + 2]
                val height = outputArray[baseIndex + 3]
                val confidence = outputArray[baseIndex + 4]
                val classId = outputArray[baseIndex + 5].toInt()
                
                if (confidence >= confidenceThreshold) {
                    // Convert normalized coordinates to pixel coordinates
                    val left = (centerX - width / 2) * originalWidth
                    val top = (centerY - height / 2) * originalHeight
                    val right = (centerX + width / 2) * originalWidth
                    val bottom = (centerY + height / 2) * originalHeight
                    
                    detections.add(
                        Detection(
                            boundingBox = RectF(left, top, right, bottom),
                            confidence = confidence,
                            classId = classId
                        )
                    )
                }
            }
            
            // Apply Non-Maximum Suppression
            return applyNMS(detections)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return detections
    }
    
    private fun applyNMS(detections: List<Detection>): List<Detection> {
        if (detections.isEmpty()) return emptyList()
        
        val sortedDetections = detections.sortedByDescending { it.confidence }
        val selectedDetections = mutableListOf<Detection>()
        
        for (detection in sortedDetections) {
            var shouldAdd = true
            
            for (selected in selectedDetections) {
                val iou = calculateIoU(detection.boundingBox, selected.boundingBox)
                if (iou > nmsThreshold) {
                    shouldAdd = false
                    break
                }
            }
            
            if (shouldAdd) {
                selectedDetections.add(detection)
            }
        }
        
        return selectedDetections
    }
    
    private fun calculateIoU(box1: RectF, box2: RectF): Float {
        val intersectionLeft = maxOf(box1.left, box2.left)
        val intersectionTop = maxOf(box1.top, box2.top)
        val intersectionRight = minOf(box1.right, box2.right)
        val intersectionBottom = minOf(box1.bottom, box2.bottom)
        
        if (intersectionRight <= intersectionLeft || intersectionBottom <= intersectionTop) {
            return 0f
        }
        
        val intersectionArea = (intersectionRight - intersectionLeft) * (intersectionBottom - intersectionTop)
        val box1Area = (box1.right - box1.left) * (box1.bottom - box1.top)
        val box2Area = (box2.right - box2.left) * (box2.bottom - box2.top)
        val unionArea = box1Area + box2Area - intersectionArea
        
        return intersectionArea / unionArea
    }
    
    private fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        
        context.assets.open(assetName).use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file.absolutePath
    }
} 