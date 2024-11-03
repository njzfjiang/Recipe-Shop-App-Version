package comp4350.recipe_shop_app_version.Other;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import comp4350.recipe_shop_app_version.Activity.SearchActivity;
import comp4350.recipe_shop_app_version.R;

public class RecipeListArrayAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<ArrayList> objects;
    private LayoutInflater layoutInflater;
    private Activity activity;
    public RecipeListArrayAdapter(Context cont, int res, List<ArrayList> obj, Activity act){
        super(cont,res,obj.get(0));
        context = cont;
        resource = res;
        objects = obj;
        layoutInflater = LayoutInflater.from(context);
        activity = act;
    }//constructor

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        JSONObject json = (JSONObject) objects.get(0).get(position);
        Bitmap image = (Bitmap) objects.get(1).get(position);

        if(convertView == null){
            convertView = layoutInflater.inflate(resource,parent,false);
        }

        TextView recipeName = convertView.findViewById(R.id.recipeName);
        ImageView imageView = convertView.findViewById(R.id.recipeImage);

        try{
            recipeName.setText(json.getJSONObject("recipe").get("label").toString());
            if(image != null){
                imageView.setImageBitmap(image);
                //imageView.setImageBitmap(Bitmap.createScaledBitmap(image, imageView.getWidth(), imageView.getHeight(), false));
                System.out.println("image " + position + " set");
            }
            else{
                //System.out.println("null image");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }//getView


    public void setImage(int pos, Bitmap img){
        objects.get(1).set(pos, img);
        notifyDataSetChanged();
    }//setImage
}
