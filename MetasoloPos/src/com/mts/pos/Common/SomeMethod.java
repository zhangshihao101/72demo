package com.mts.pos.Common;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.Format;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class SomeMethod {
	/**
	 * 将double类型小数点后保存,并且加上“,”
	 */
	public static String get2Double(double a){  
		DecimalFormat df=new DecimalFormat("0.00");  
//		Format fm1=new DecimalFormat("#,###.00");
//		double num1=12345.678;
//		fm1.format(num1);
	    return df.format(a);  
	    
	} 
	public static String getCommaDouble(double b){
		Format fm1=new DecimalFormat("#,##0.00");
		return fm1.format(b);  
	}
	/**
	 * 通过URL取得bitmap
	 */
	public static Bitmap getBitmapFromURL(String src) {
        try {
//            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
//            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
