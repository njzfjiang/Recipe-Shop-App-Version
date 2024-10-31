package comp4350.recipe_shop_app_version.Other;

import android.app.Activity;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import comp4350.recipe_shop_app_version.Activity.LoginActivity;
import comp4350.recipe_shop_app_version.Activity.RegisterActivity;

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

        if(params.get(0).equals("login")){
            urlString += "/api/login?username=";
            urlString += Services.username;
            urlString += "&password=";
            urlString += Services.password;

            data = request(urlString);

            int end = data.lastIndexOf("\"");
            int start = data.lastIndexOf("\"", end-1)+1;
            String message = data.substring(start, end);
            System.out.println(message);

            if(message.equalsIgnoreCase("Login successful!")) {
                System.out.println("loginSuccess");
                ((LoginActivity) activity).loginSuccess();
            }
            else{
                System.out.println("loginFail");
                ((LoginActivity)activity).loginFail();
            }
        }//login

        else if(params.get(0).equals("register")){
            urlString += "/api/register";
            String[] params = {"POST", "{\n\"username\":\"",Services.username, "\"," +
                    "\n\"password\":\"",Services.password, "\"," +
                    "\n\"confirmPassword\":\"",Services.confirm,"\"\n}"};

            data = request(urlString, params);

            int start = data.indexOf("message");
            start = data.indexOf(":",start);
            start = data.indexOf("\"",start)+1;
            int end = data.indexOf("\"", start);
            String message = data.substring(start, end);
            System.out.println(message);

            if(message.equalsIgnoreCase("User registered successfully!")) {
                System.out.println("registerSuccess");
                ((RegisterActivity) activity).registerSuccess();
            }
            else{
                System.out.println("registerFail");
                ((RegisterActivity)activity).registerFail();
            }
        }//register

        else if(params.get(0).equals("user-exists")){
            urlString += "/api/user-exist?username=";
            urlString += Services.username;

            data = request(urlString);

            int end = data.lastIndexOf("}");
            int start = data.lastIndexOf(":", end-1)+1;
            String message = data.substring(start, end);
            System.out.println(message);

            if(message.equalsIgnoreCase("true")) {
                System.out.println("userExists");
                ((RegisterActivity)activity).userExists();
            }
            else{
                System.out.println("userNotExists");
                ((RegisterActivity)activity).userNotExists();
            }
        }//user-exists
    }//run

    private String request(String urlString){
        String response = "";
        Scanner is,es;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            //not 200
            if(urlConnection.getResponseCode() != 200){
                System.out.println("1");
                es = new Scanner(urlConnection.getErrorStream());
                while (es.hasNextLine()) {
                    response = es.nextLine();
                }
                es.close();
            }
            //200
            else{
                System.out.println("2");
                is = new Scanner(urlConnection.getInputStream());
                while(is.hasNextLine()){
                    response = is.nextLine();
                }
                is.close();
            }

            //System.out.println(response);
            urlConnection.disconnect();
        }catch(FileNotFoundException fnf){
            fnf.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println(response);
        return response;
    }//request

    private String request(String urlString, String[] params){
        String response = "";
        Scanner is, es;
        String body = "";
        for (int i=1;i<params.length;i++) {
            body += params[i];
        }
        System.out.println(body);

        try {
            byte[] reqBody = body.getBytes();
            int byteLength = reqBody.length;
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(params[0]);
            urlConnection.setRequestProperty("Content-Length", Integer.toString(body.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStream os = urlConnection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            osw.write(body);
            osw.flush();
            osw.close();
            os.close();

            //not 201
            if(urlConnection.getResponseCode() != 201){
                System.out.println("1");
                es = new Scanner(urlConnection.getErrorStream());
                while (es.hasNextLine()) {
                    response = es.nextLine();
                }
                es.close();
            }
            //201
            else{
                System.out.println("2");
                is = new Scanner(urlConnection.getInputStream());
                while(is.hasNextLine()){
                    response = is.nextLine();
                }
                is.close();
            }

            //System.out.println(data);
            urlConnection.disconnect();
        }catch(FileNotFoundException fnf){
            fnf.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println(response);
        return response;
    }//request
}
