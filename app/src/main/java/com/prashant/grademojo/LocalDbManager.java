package com.prashant.grademojo;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalDbManager {
    private static final String name = "GRADEMOJO";

    public static String getToken(Context context) {
        SharedPreferences localSharedPreferences = context.getSharedPreferences(name, 0);
        boolean bool = localSharedPreferences.contains("token");
        if (bool) {
            return localSharedPreferences.getString("token", "");
        }else return "";
    }

    public static void saveToken(Context context ,String token) {
        SharedPreferences.Editor localEditor = context.getSharedPreferences(name, 0).edit();
        localEditor.putString("token", token);
        localEditor.commit();
        localEditor.apply();
    }
}
