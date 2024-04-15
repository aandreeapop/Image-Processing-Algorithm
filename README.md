# Image Processing Algorithm
## Pop Andreea - March 2023


## Overview
This Scala code provides functions for image processing using the PPM (Portable Pixmap) file format. The code focuses on functional programming principles and implements various image manipulation tasks.

## Usage
The functions provided in `Solution.scala` can be used to perform the following operations on images:
1. Vertical concatenation of two images.
2. Horizontal concatenation of two images.
3. Rotation of images in multiples of 90 degrees.
4. Edge detection using the Sobel operator.
5. Generating and coloring a Pascal triangle modulo M.

## File Structure
- `Solution.scala`: Contains the implementation of image processing functions.
- `util`: Directory containing utility files.
  - `Pixel.scala`: Defines the `Pixel` class to represent RGB values.
  - `Util.scala`: Provides utility functions for image processing.

## Functions
### 1. verticalConcat(image1: Image, image2: Image): Image
   - Concatenates two images vertically, placing the first image above the second.

### 2. horizontalConcat(image1: Image, image2: Image): Image
   - Concatenates two images horizontally, placing the first image to the left of the second.

### 3. rotate(image: Image, degrees: Integer): Image
   - Rotates the given image by the specified number of degrees in a clockwise direction.

### 4. edgeDetection(image: Image, threshold: Double): Image
   - Detects edges in the given image using the Sobel operator and applies a threshold to generate a binary edge map.

### 5. moduloPascal(M: Integer, pickColor: Integer => Pixel, size: Integer): Image
   - Generates a Pascal triangle modulo M and colors each value according to the provided color picking function.

## Input and Output Format
- The input images are represented as lists of characters, following the PPM file format.
- Output images are also represented as lists of characters in the PPM file format.
