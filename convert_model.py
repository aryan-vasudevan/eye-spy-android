#!/usr/bin/env python3
"""
Script to convert YOLO model to TorchScript format for Android deployment.
"""

import torch
from ultralytics import YOLO
import os

def convert_model_to_torchscript(model_path, output_path=None):
    """
    Convert a YOLO model to TorchScript format.
    
    Args:
        model_path (str): Path to the .pt model file
        output_path (str): Path for the output TorchScript file
    """
    try:
        print(f"Loading model from {model_path}...")
        model = YOLO(model_path)
        
        if output_path is None:
            output_path = model_path.replace('.pt', '.torchscript.pt')
        
        print(f"Converting to TorchScript format...")
        model.export(format="torchscript")
        
        print(f"Model converted successfully!")
        print(f"TorchScript model saved to: {output_path}")
        print(f"Please copy this file to: app/src/main/assets/")
        
    except Exception as e:
        print(f"Error converting model: {e}")
        return False
    
    return True

if __name__ == "__main__":
    model_path = "glasses_weights.pt"
    
    if not os.path.exists(model_path):
        print(f"Error: Model file {model_path} not found!")
        print("Please ensure the model file is in the current directory.")
        exit(1)
    
    print("YOLO Model to TorchScript Converter")
    print("=" * 40)
    
    success = convert_model_to_torchscript(model_path)
    
    if success:
        print("\nNext steps:")
        print("1. Copy the converted .torchscript.pt file to app/src/main/assets/")
        print("2. Build and run the Android app")
        print("3. The app will automatically load the model and start detecting glasses")
    else:
        print("\nConversion failed. Please check the error messages above.") 