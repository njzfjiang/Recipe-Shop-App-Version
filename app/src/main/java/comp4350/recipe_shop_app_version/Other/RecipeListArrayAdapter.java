package comp4350.recipe_shop_app_version.Other;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
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
    private ArrayList<Boolean> visible;
    public RecipeListArrayAdapter(Context cont, int res, List<ArrayList> obj, Activity act){
        super(cont,res,obj.get(0));
        context = cont;
        resource = res;
        objects = obj;
        layoutInflater = LayoutInflater.from(context);
        activity = act;
        visible = new ArrayList<>();
        for(int i=0;i<objects.get(0).size();i++){
            visible.add(true);
        }
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
            if(json != null){
                if(json.has("recipe")){
                    System.out.println("Edamame Recipe");
                    recipeName.setText(json.getJSONObject("recipe").get("label").toString());
                    System.out.println("name " + position + " set");
                }
                else if(json.has("find_recipe")){
                    System.out.println("Shop Recipe");
                    recipeName.setText(json.getJSONObject("find_recipe").get("title").toString());
                    System.out.println("name " + position + " set");
                }

                if(image != null){
                    imageView.setImageBitmap(image);
                    System.out.println("image " + position + " set");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(visible.get(position)){
            convertView.setLayoutParams(new AbsListView.LayoutParams(-1,-2));
            convertView.setVisibility(View.VISIBLE);
        }
        else{
            convertView.setLayoutParams(new AbsListView.LayoutParams(-1,1));
            convertView.setVisibility(View.GONE);
        }
        return convertView;
    }//getView


    public void setImage(int pos, Bitmap img){
        objects.get(1).set(pos, img);
        notifyDataSetChanged();
    }//setImage

    public void setRecipe(int pos, JSONObject recipe){
        objects.get(0).set(pos, recipe);
        notifyDataSetChanged();
    }//setImage

    public JSONObject getRecipe(int pos){
        return (JSONObject) objects.get(0).get(pos);
    }//getRecipe

    public void setVisibility(int pos, Boolean bool){
        visible.set(pos, bool);
        notifyDataSetChanged();
    }

    public ArrayList<Boolean> getVisibility(){
        return visible;
    }//getVisibility
}
