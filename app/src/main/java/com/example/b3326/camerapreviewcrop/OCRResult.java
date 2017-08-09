package com.example.b3326.camerapreviewcrop;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OCRResult extends AppCompatActivity {
    protected static ProgressDialog progressBar;
    static TessBaseAPI mTess;//Tess API reference
    static String datapath = ""; //path to folder containing language data file
    static TextView OCRTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocrresult);
        Run();
        OCRTextView = (TextView) findViewById(R.id.OCRTextView);
        OCRTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        ImageView imageView=findViewById(R.id.imageView);

        byte[] b =getIntent().getByteArrayExtra("Image");
        Bitmap bitmap= BitmapFactory.decodeByteArray(b,0,b.length);
        progressBar = new ProgressDialog(this);

        new OCR().execute(bitmap);


        //Log.d("WillyCheng","Bitmap's height = "+bitmap.getHeight());
        imageView.setImageBitmap(bitmap);
        imageView.invalidate();

    }

    public void Run() {

        datapath = getFilesDir() + "/tesseract/";
        //initialize Tesseract API
        String lang = "eng";
        mTess = new TessBaseAPI();
        checkFile(new File(datapath + "tessdata/"));
        mTess.init(datapath, lang);
    }

    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if (dir.exists()) {
            String datafilepath = datapath + "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }
    private void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";
            //get access to AssetManager
            AssetManager assetManager = getAssets();
            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);
            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
