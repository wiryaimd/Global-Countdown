package com.wiryaimd.globalcountdown.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wiryaimd.globalcountdown.model.LocalCountdownModel;

import java.util.ArrayList;

public class PreferencesManager {

    private static Gson gson = new Gson();

    public static void saveCountdown(Context context, ArrayList<LocalCountdownModel> localcList){
        SharedPreferences sharedPreferences = context.getSharedPreferences("CoutdownTime", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("localcdata", gson.toJson(localcList));
        editor.apply();
    }

    public static ArrayList<LocalCountdownModel> getCountdown(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("CoutdownTime", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("localcdata", "none").equalsIgnoreCase("none")){
            return null;
        }else{
            return gson.fromJson(sharedPreferences.getString("localcdata", "none"), new TypeToken<ArrayList<LocalCountdownModel>>(){}.getType());
        }
    }

    public static void saveUsername(Context context, String username){
        SharedPreferences sharedPreferences = context.getSharedPreferences("CountdownTime", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("usernamedata", username);
        editor.apply();
    }

    public static String getUsername(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("CountdownTime", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("usernamedata", "none").equalsIgnoreCase("none")){
            return null;
        }else{
            return sharedPreferences.getString("usernamedata", "none");
        }
    }

}
