package com.example.b3326.camerapreviewcrop;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.b3326.camerapreviewcrop.OCRResult.OCRTextView;
import static com.example.b3326.camerapreviewcrop.OCRResult.mTess;


public class OCR extends AsyncTask<Bitmap, Integer , String> {

    String OCRresult = "";
    long pre;
    long aft;
    @Override
    protected void onPreExecute() {
        //pre=System.currentTimeMillis();
        //執行前 設定可以在這邊設定
        super.onPreExecute();
        OCRResult.progressBar.setCancelable(false);
        OCRResult.progressBar.setMessage("Loading...");
        OCRResult.progressBar.show();
    }

    @Override
    protected String doInBackground(Bitmap... bitmap) {
        Bitmap[] bp = bitmap;
        mTess.setImage(bp[0]);
        OCRresult = mTess.getUTF8Text();
        System.out.println(OCRresult);

        return OCRresult;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //執行中 可以在這邊告知使用者進度
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String str) {
        //執行後 完成背景任務
        super.onPostExecute(str);
        OCRTextView.setText(ID(str));
       // aft=System.currentTimeMillis();
      //  Log.d("WillyCheng","OCR Time="+(aft-pre));
        OCRResult.progressBar.dismiss();
    }

    public String ID(String str) {

        String reg = "[A-Z]\\d{9}";
        String Result = "";

        //將規則封裝成物件
        Pattern p = Pattern.compile(reg);

        //讓正則物件與要作用的字串相關聯
        Matcher m = p.matcher(str);
        //System.out.println(m.matches());
        //String類中的matches方法,即使用Pattern和Matcher類的matcher方法
        //只不過String方法封裝後, 使用較簡單, 但功能一致

        //將規則作用到字串上, 並進行符合規則的子串查找
        while (m.find()) {
            //System.out.println(m.group() + " ");  //用於獲取匹配後結果
            Result =m.group();
        }
        return Result;
    }
}


