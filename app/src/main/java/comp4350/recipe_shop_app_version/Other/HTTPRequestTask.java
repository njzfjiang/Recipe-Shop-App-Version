package comp4350.recipe_shop_app_version.Other;

import android.app.Activity;
import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

import comp4350.recipe_shop_app_version.Activity.FavoritesActivity;
import comp4350.recipe_shop_app_version.Activity.GroceryActivity;
import comp4350.recipe_shop_app_version.Activity.LoginActivity;
import comp4350.recipe_shop_app_version.Activity.RecipeInfoActivity;
import comp4350.recipe_shop_app_version.Activity.RegisterActivity;
import comp4350.recipe_shop_app_version.Activity.SearchActivity;
import comp4350.recipe_shop_app_version.Activity.SettingsActivity;
import comp4350.recipe_shop_app_version.Activity.UploadActivity;

public class HTTPRequestTask implements Runnable{
    ArrayList<String> params = new ArrayList<>();
    Activity activity;
    public HTTPRequestTask(String[] input, Activity act){
        params.addAll(Arrays.asList(input));
        activity = act;
    }//constructor

    @Override
    public void run(){
        String urlString;
        urlString = "http://" + Services.ip + ":80";
        String data = "";

        //login
        if(params.get(0).equals("login")){
            login(urlString);
        }
        else if(params.get(0).equals("register")){
            register(urlString);
        }
        else if(params.get(0).equals("user-exists")){
            userExists(urlString);
        }
        else if(params.get(0).equals("search")) {
            search(urlString);
        }
        else if(params.get(0).equals("is-favorite")) {
            isFavorite(urlString);
        }
        else if(params.get(0).equals("addFavorite")) {
            addFavorite(urlString);
        }
        else if(params.get(0).equals("deleteFavorite")) {
            deleteFavorite(urlString);
        }
        else if(params.get(0).equals("all-favorites")) {
            getFavorites(urlString);
        }
        else if(params.get(0).equals("recipe")) {
            getRecipe(urlString);
        }
        else if(params.get(0).equals("generate-list")) {
            getList(urlString);
        }
        else if(params.get(0).equals("upload")) {
            upload(urlString);
        }
        else if(params.get(0).equals("all-uploads")) {
            getUploads(urlString);
        }
        else if(params.get(0).equals("shop-recipe")) {
            getShopRecipe(urlString);
        }
        else if(params.get(0).equals("delete-recipe")) {
            deleteRecipe(urlString);
        }
    }//run


    private void login(String urlString){
        urlString += "/api/login";
        //urlString += "/api/login?username=";
        //urlString += Services.username;
        //urlString += "&password=";
        //urlString += Services.password;
        //String[] reqParams = {"GET"};

        String[] reqParams = {"POST", "{\n\"username\":\"",Services.username, "\"," +
                "\n\"password\":\"",Services.password, "\"\n}"};
        String[] response = request(urlString, reqParams);

        if(response[0].equals("200")){
            System.out.println("loginSuccess");
            ((LoginActivity) activity).loginSuccess();
        }
        else if(response[0].equals("401")){
            System.out.println("incorrectPassword");
            ((LoginActivity)activity).loginFail();
        }
        else if(response[0].equals("404")){
            System.out.println("userNotFound");
            ((LoginActivity)activity).loginFail();
        }
        //error / other
        else{
            System.out.println("ERROR!");
            ((LoginActivity)activity).loginFail();
        }
    }//login


    private void register(String urlString){
        urlString += "/api/register";
        String[] reqParams = {"POST", "{\n\"username\":\"",Services.username, "\"," +
                "\n\"password\":\"",Services.password, "\"," +
                "\n\"confirmPassword\":\"",Services.confirm,"\"\n}"};

        String[] response = request(urlString, reqParams);

        if(response[0].equals("201")){
            System.out.println("registerSuccess");
            ((RegisterActivity) activity).registerSuccess();
        }
        else if(response[0].equals("409")){
            System.out.println("usernameTaken");
            ((RegisterActivity) activity).registerFail();
        }
        //error / other
        else{
            System.out.println("ERROR!");
            ((RegisterActivity)activity).registerFail();
        }
    }//register


    private void userExists(String urlString){
        urlString += "/api/user-exist?username=";
        urlString += Services.username;
        String[] reqParams = {"GET"};

        String[] response = request(urlString, reqParams);

        if(response[0].equals("200")){
            Boolean exists = true;
            try {
                JSONObject json = new JSONObject(response[1]);
                exists = json.getBoolean("exists");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(exists){
                System.out.println("userExists");
                ((RegisterActivity)activity).userExists();
            }
            else{
                System.out.println("userNotExists");
                ((RegisterActivity)activity).userNotExists();
            }
        }
        //error / error
        else {
            System.out.println("ERROR!");
            ((RegisterActivity) activity).userExists();
        }
    }//userExists


    private void search(String urlString){
        String prefix = "?";
        urlString += "/api/recipe/search";
        if(!params.get(4).isEmpty()) {
            urlString += prefix + "keyword=" + params.get(4).replaceAll(" ", "+");
            prefix = "&";
        }
        if(!params.get(1).isEmpty()) {
            urlString += prefix + "mealType=" + params.get(1);
            prefix = "&";
        }
        if(!params.get(2).equals("0") && !params.get(3).equals("0")) {
            urlString += prefix + "time=" + params.get(2) + "-" + params.get(3);
            prefix = "&";
        }
        String[] reqParams = {"GET"};

        String[] response = request(urlString, reqParams);

        if(response[0].equals("200")){
            System.out.println("searchSuccess");
            ((SearchActivity) activity).searchSuccess(response[1]);
        }
        //error / other
        else {
            System.out.println("ERROR!");
            ((SearchActivity) activity).searchFail();
        }
    }//search

    private void isFavorite(String urlString){
        String prefix = "?";
        urlString += "/api/is-favorite/";
        urlString += params.get(1);
        if(!Services.username.isEmpty()) {
            urlString += prefix + "username=" + Services.username;
            prefix = "&";
        }
        if(params.size() >= 3 && !params.get(2).isEmpty()) {
            urlString += prefix + "source=" + params.get(2).replaceAll(" ", "+");
        }
        String[] reqParams = {"GET"};

        String[] response = request(urlString, reqParams);

        if(response[0].equals("200")){
            Boolean saved = false;
            try {
                JSONObject json = new JSONObject(response[1]);
                saved = json.getBoolean("saved");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(saved){
                System.out.println("favorited");
                ((RecipeInfoActivity)activity).updateFavoriteButtons(saved);
            }
            else{
                System.out.println("notFavorited");
                ((RecipeInfoActivity)activity).updateFavoriteButtons(saved);
            }
        }
        //error / error
        else {
            System.out.println("ERROR!");
            ((RecipeInfoActivity) activity).checkFavorite();
        }
    }//isFavorite


    private void addFavorite(String urlString){
        String prefix = "?";
        urlString += "/api/favorites/";
        urlString += params.get(1);
        if(!Services.username.isEmpty()) {
            urlString += prefix + "username=" + Services.username;
            prefix = "&";
        }
        if(!params.get(2).isEmpty()) {
            urlString += prefix + "title=" + params.get(2).replaceAll(" ", "+");
        }
        if(params.get(3) != null && !params.get(3).isEmpty()) {
            urlString += prefix + "ingredients=" + params.get(3).replaceAll(" ", "+");
        }
        else{
            urlString += prefix + "ingredients=" + "none";
        }
        if(params.get(4) != null && !params.get(4).isEmpty()) {
            urlString += prefix + "source=" + params.get(4).replaceAll(" ", "+");
        }
        System.out.println(urlString);
        String[] reqParams = {"POST"};

        String[] response = request(urlString, reqParams);

        if(response[0].equals("201")){
            System.out.println("addedToFavorites");
            ((RecipeInfoActivity) activity).checkFavorite();
        }
        else if(response[0].equals("409")){
            System.out.println("alreadyAddedToFavorites");
            ((RecipeInfoActivity) activity).checkFavorite();
        }
        //error / error
        else {
            System.out.println("ERROR!");
            ((RecipeInfoActivity) activity).checkFavorite();
        }
    }//addFavorite


    private void deleteFavorite(String urlString){
        String prefix = "?";
        urlString += "/api/favorites/";
        urlString += params.get(1);
        if(!Services.username.isEmpty()) {
            urlString += prefix + "username=" + Services.username;
            prefix = "&";
        }
        if(!params.get(2).isEmpty()) {
            urlString += prefix + "title=" + params.get(2).replaceAll(" ", "+");
        }
        if(params.get(3) != null && !params.get(3).isEmpty()) {
            urlString += prefix + "source=" + params.get(3).replaceAll(" ", "+");
        }
        System.out.println(urlString);
        String[] reqParams = {"DELETE"};

        String[] response = request(urlString, reqParams);

        if(response[0].equals("204")){
            System.out.println("removedFromFavorites");
            ((RecipeInfoActivity) activity).checkFavorite();
        }
        else if(response[0].equals("404")){
            System.out.println("noFavoritedRecipeFound");
            ((RecipeInfoActivity) activity).checkFavorite();
        }
        //error / error
        else {
            System.out.println("ERROR!");
            ((RecipeInfoActivity) activity).checkFavorite();
        }
    }//deleteFavorite


    private void getFavorites(String urlString){
        urlString += "/api/all-favorites/";
        urlString += Services.username;

        String[] reqParams = {"GET"};

        String[] response = request(urlString, reqParams);

        if(response[0].equals("200")){
            System.out.println("getFavoritesSuccess");
            ((FavoritesActivity) activity).getFavoritesSuccess(response[1]);
        }
        else if(response[0].equals("404")){
            System.out.println("getFavoritesFail");
            ((FavoritesActivity) activity).getFavoritesFail();
        }
        //error / other
        else {
            System.out.println("ERROR!");
            ((FavoritesActivity) activity).getFavoritesFail();
        }
    }//search


    private void getRecipe(String urlString){
        urlString += "/api/recipe/";
        urlString += params.get(2);
        System.out.println(urlString);

        String[] reqParams = {"GET"};

        String[] response = request(urlString, reqParams);

        if(response[0].equals("200")){
            System.out.println("getRecipeSuccess");
            if(activity instanceof FavoritesActivity) {
                ((FavoritesActivity) activity).getRecipeSuccess(Integer.parseInt(params.get(1)), response[1]);
            }
            else if(activity instanceof GroceryActivity) {
                ((GroceryActivity) activity).getRecipeSuccess(Integer.parseInt(params.get(1)), response[1]);
            }
        }
        //error / other
        else {
            System.out.println("ERROR!");
            if(activity instanceof FavoritesActivity) {
                ((FavoritesActivity) activity).getRecipeFail(Integer.parseInt(params.get(1)));
            }
            else if(activity instanceof GroceryActivity) {
                ((GroceryActivity) activity).getRecipeFail(Integer.parseInt(params.get(1)));
            }
        }
    }//getRecipe

    private void getList(String urlString){
        urlString += "/api/generate-list/";
        urlString += Services.username;
        System.out.println(urlString);

        String[] reqParams = {"GET"};

        String[] response = request(urlString, reqParams);

        if(response[0].equals("200")){
            System.out.println("getRecipeSuccess");
            ((GroceryActivity) activity).getListSuccess(response[1]);
        }
        else if(response[0].equals("404")){
            System.out.println("getRecipeSuccess");
            ((GroceryActivity) activity).getListFail();
        }
        //error / other
        else {
            System.out.println("ERROR!");
            ((GroceryActivity) activity).getListFail();
        }
    }//getList


    private void upload(String urlString){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] imageBase64 = new byte[0];
        if(Services.recipeImage != null) {
            Services.recipeImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            imageBase64 = byteArrayOutputStream.toByteArray();
        }

        urlString += "/api/recipe/upload";
        String[] reqParams = {"POST", "{\n\t\"title\":\"",params.get(1), "\"," +
                "\n\t\"source\":\"",params.get(2), "\"," +
                "\n\t\"username\":\"",Services.username,"\"," +
                "\n\t\"ingredients\": [\n",params.get(3),"\n\t]," +
                "\n\t\"instructions\":\"",params.get(4),"\"," +
                "\n\t\"image\":\"", Base64.getEncoder().encodeToString(imageBase64),"\"," +
                "\n\t\"privacy\":\"",params.get(5),"\"" +
                "\n}"};

        //System.out.println(Arrays.toString(reqParams));
        String[] response = request(urlString, reqParams);

        if(response[0].equals("201")){
            System.out.println("uploadSuccess");
            ((UploadActivity) activity).uploadSuccess();
        }
        else if(response[0].equals("409")){
            System.out.println("empty upload parameter");
            ((UploadActivity) activity).uploadEmpty(response[1]);
        }
        else if(response[0].equals("404")){
            System.out.println("username doesn't exist");
            ((UploadActivity) activity).uploadFail(response[1]);
        }
        //error / other
        else{
            System.out.println("ERROR!");
            ((UploadActivity)activity).uploadFail(response[1]);
        }
    }//upload

    private void getUploads(String urlString){
        urlString += "/api/all-uploads/";
        urlString += Services.username;
        String[] reqParams = {"GET"};

        String[] response = request(urlString, reqParams);

        if(response[0].equals("200")){
            System.out.println("getUploadsSuccess");
            ((SettingsActivity) activity).getUploadsSuccess(response[1]);
        }
        else if(response[0].equals("404")){
            System.out.println("No uploaded recipes found");
            ((SettingsActivity) activity).getUploadsEmpty(response[1]);
        }
        //error / other
        else{
            System.out.println("ERROR!");
            ((SettingsActivity)activity).getUploadsFail(response[1]);
        }
    }//upload


    private void getShopRecipe(String urlString){
        urlString += "/api/recipe/upload/";
        urlString += params.get(2);
        System.out.println(urlString);

        String[] reqParams = {"GET"};

        String[] response = request(urlString, reqParams);

        if(response[0].equals("200")){
            System.out.println("getShopRecipeSuccess");
            if(activity instanceof SettingsActivity) {
                ((SettingsActivity) activity).getShopRecipeSuccess(Integer.parseInt(params.get(1)), response[1]);
            }
            else if(activity instanceof FavoritesActivity) {
                ((FavoritesActivity) activity).getShopRecipeSuccess(Integer.parseInt(params.get(1)), response[1]);
            }
            else if(activity instanceof GroceryActivity) {
                ((GroceryActivity) activity).getShopRecipeSuccess(Integer.parseInt(params.get(1)), response[1]);
            }
        }
        //error / other
        else {
            System.out.println("ERROR!");
            if(activity instanceof SettingsActivity) {
                ((SettingsActivity) activity).getShopRecipeFail(Integer.parseInt(params.get(1)));
            }
            else if(activity instanceof FavoritesActivity) {
                ((FavoritesActivity) activity).getShopRecipeFail(Integer.parseInt(params.get(1)));
            }
            else if(activity instanceof GroceryActivity) {
                ((GroceryActivity) activity).getShopRecipeFail(Integer.parseInt(params.get(1)));
            }
        }
    }//getShopRecipe


    private void deleteRecipe(String urlString){
        System.out.println("deleteRecipe");
        String prefix = "?";
        urlString += "/api/uploads/";
        urlString += params.get(1);
        if(!Services.username.isEmpty()) {
            urlString += prefix + "username=" + Services.username;
            prefix = "&";
        }
        if(!params.get(2).isEmpty()) {
            urlString += prefix + "title=" + params.get(2).replaceAll(" ", "+");
        }

        System.out.println("deleteRecipe: " + urlString);

        String[] reqParams = {"DELETE"};

        String[] response = request(urlString, reqParams);

        if(response[0].equals("204")){
            System.out.println("removedFromUploads");
            ((RecipeInfoActivity) activity).deleteSuccess();
        }
        else if(response[0].equals("404")){
            System.out.println("noUploadedRecipeFound");
            ((RecipeInfoActivity) activity).deleteFailed();
        }
        //error / error
        else {
            System.out.println("ERROR!");
            ((RecipeInfoActivity) activity).deleteFailed();
        }
    }//deleteFavorite


    private String[] request(String urlString, String[] reqParams){
        String code = "";
        String response = "";
        String method = reqParams[0];
        Scanner is, es;

        String body = "";
        for (int i=1;i<reqParams.length;i++) {
            body += reqParams[i];
        }

        System.out.println(body);

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.setRequestProperty("Content-Length", Integer.toString(body.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/json");

            if(!body.isEmpty()) {
                //set request body
                OutputStream os = urlConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                osw.write(body);
                osw.flush();
                osw.close();
                os.close();
            }
            urlConnection.connect();

            code = String.valueOf(urlConnection.getResponseCode());

            //error
            if(urlConnection.getResponseCode() >= 400){
                es = new Scanner(urlConnection.getErrorStream());
                while (es.hasNextLine()) {
                    response = es.nextLine();
                }
                es.close();
            }
            //no error
            else{
                is = new Scanner(urlConnection.getInputStream());
                while(is.hasNextLine()){
                    response = is.nextLine();
                }
                is.close();
            }

            urlConnection.disconnect();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println(code);
        System.out.println(response);
        return new String[]{code, response};
    }//request
}
