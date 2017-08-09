# CameraPreviewCrop_OCR
This is a simple app that can Preview the camera,crop the image ,recognize text,and fetch the ID card number.

This app can only run correctly on device which screen is 10.1 inches,because I don't know how to let an APP suit with every size of screen....,and after install the app,you should go to Setting - Applicaion ,and manually give the app permission ,include storage and camera.

In this project,I use SurfaceView to show the preview of camera,and make a custom view called "TouchView",
which can change its size if you touch and drag it,this view is used to set AOI (Area of Interest),
and then crop the image from the origin image data (a byte[],which is YUVImage format).
    Finally,send the cropped image to TessTwo OCR engine,get back a result string,and use regular expression to fetch the Identity card 
number.
