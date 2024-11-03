package comp4350.recipe_shop_app_version.Other;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import comp4350.recipe_shop_app_version.Activity.SearchActivity;

public class ImageRequestTask implements Runnable{
    int position;
    String urlString;
    Activity activity;
    public ImageRequestTask(int pos, String url, Activity act){
        position = pos;
        urlString = url;
        activity = act;
    }//constructor

    @Override
    public void run() {
        String code = "";
        String error = "";
        String method = "GET";
        Scanner es;
        Bitmap image = null;

        try {
            System.out.println(urlString);
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.setRequestProperty("Accept", "image/png, image/jpg, image/jpeg");
            urlConnection.connect();

            code = String.valueOf(urlConnection.getResponseCode());

            //error
            if(urlConnection.getResponseCode() >= 400){
                es = new Scanner(urlConnection.getErrorStream());
                while (es.hasNextLine()) {
                    error = es.nextLine();
                }
                es.close();
            }
            //no error
            else{
                InputStream is = urlConnection.getInputStream();
                image = BitmapFactory.decodeStream(is);
                is.close();
            }

            urlConnection.disconnect();
        }catch(Exception e){
            e.printStackTrace();
        }
        //System.out.println(code);
        System.out.println(error);
        if(image != null) {
            //System.out.println("loadImage");
            ((SearchActivity) activity).loadImage(position, image);
        }
    }//run
}
