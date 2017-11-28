package com.prashant.grademojo.Helpers;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.prashant.grademojo.Helpers.RestHelper.OnVollyJsonObjectResponse;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 26-11-2017.
 */

public class VolleyHelper {
    private static RequestQueue volleyQueue = null;
    private static VolleyHelper _this = null;


    private VolleyHelper(Context context) {
        volleyQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    // get object of this class which is static
    public static VolleyHelper getRestHelper(Context context) {
        if (_this == null) {
            _this = new VolleyHelper(context);
        }
        return _this;
    }

    public void postRequest(String url, final Map<String, String> header, JSONObject postData, final OnVollyJsonObjectResponse onResponse){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("### res" + response.toString());
                onResponse.onJsonObjectSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("## VOLL " + error.getMessage());
                onResponse.onJsonObjectError(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return header;
            }
        };
        _this.volleyQueue.add(request);
    }
}

