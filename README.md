# Eye Spy Android App

This Android app uses a YOLO model to detect glasses in real-time using the device's camera.

## Features

- Real-time camera preview
- Glasses detection using YOLO model
- Live overlay showing detected glasses with confidence scores
- Permission handling for camera access

## Setup Instructions

### 1. Convert Your YOLO Model

Before using the app, you need to convert your `.pt` model to TorchScript format:

```python
import torch
from ultralytics import YOLO

# Load your model
model = YOLO("glasses_weights.pt")

# Export to TorchScript
model.export(format="torchscript")
```

This will create `glasses_weights.torchscript.pt` which is compatible with PyTorch Mobile.

### 2. Replace the Model File

Replace the `glasses_weights.pt` file in `app/src/main/assets/` with your converted `glasses_weights.torchscript.pt` file.

### 3. Build and Run

1. Open the project in Android Studio
2. Sync the project with Gradle files
3. Build and run the app on your device

## Usage

1. Launch the app
2. Grant camera permissions when prompted
3. Tap "Open Camera" to start the live preview
4. The app will automatically detect glasses and display bounding boxes with confidence scores
5. Tap "Back" to return to the home screen

## Model Output Format

The app expects the YOLO model to output detections in the following format:
- Output shape: `[batch, num_detections, 6]`
- Each detection contains: `[center_x, center_y, width, height, confidence, class_id]`
- Coordinates are normalized to `[0, 1]`

If your model has a different output format, you'll need to modify the `postprocessOutput` method in `YoloDetector.kt`.

## Dependencies

- PyTorch Mobile 1.13.1
- CameraX for camera functionality
- Jetpack Compose for UI
- Accompanist Permissions for permission handling

## Troubleshooting

1. **Model loading fails**: Ensure your model is in TorchScript format and placed in the assets folder
2. **No detections**: Check that your model output format matches the expected format
3. **Camera not working**: Ensure camera permissions are granted
4. **App crashes**: Check the logcat for specific error messages

## Customization

- Modify `confidenceThreshold` in `YoloDetector.kt` to adjust detection sensitivity
- Change the overlay colors in `DetectionOverlay.kt`
- Add support for multiple object classes by modifying the detection processing 