# CameraPreviewCrop_OCR
This is a simple app that can Preview the camera,crop the image ,recognize text,and fetch the ID card number.

In this project,I use SurfaceView to show the preview of camera,and make a custom view called "TouchView",
which can change its size if you touch and drag it,this view is used to set AOI (Area of Interest),
and then crop the image from the origin image data (a byte[],which is YUVImage format).
    Finally,send the cropped image to TessTwo OCR engine,get back a result string,and use regular expression to fetch the Identity card 
number.
