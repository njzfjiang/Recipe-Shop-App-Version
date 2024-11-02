package comp4350.recipe_shop_app_version.Other;

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

import java.util.List;

import comp4350.recipe_shop_app_version.R;

public class RecipeListArrayAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<JSONObject> objects;
    private LayoutInflater layoutInflater;
    public RecipeListArrayAdapter(Context cont, int res, List<JSONObject> obj){
        super(cont,res,obj);
        context = cont;
        resource = res;
        objects = obj;
        layoutInflater = LayoutInflater.from(context);
        System.out.println("constructor");
    }//constructor

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        System.out.println("getView");
        JSONObject json = objects.get(position);
        System.out.println(json);

        if(convertView == null){
            convertView = layoutInflater.inflate(resource,parent,false);
        }

        TextView recipeName = convertView.findViewById(R.id.recipeName);
        //Bitmap image = BitmapFactory
        //ImageView image = convertView.findViewById(R.id.recipeImage);

        try{
            System.out.println(json.toString());
            System.out.println(json.getJSONObject("recipe").toString());
            System.out.println(json.getJSONObject("recipe").get("label").toString());
            recipeName.setText(json.getJSONObject("recipe").get("label").toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }//getView
}
