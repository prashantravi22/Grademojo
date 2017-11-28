package com.prashant.grademojo.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.prashant.grademojo.Helpers.RestHelper.OnVollyJsonObjectResponse;
import com.prashant.grademojo.Helpers.VolleyHelper;
import com.prashant.grademojo.LocalDbManager;
import com.prashant.grademojo.R;

import net.gotev.uploadservice.BinaryUploadRequest;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AccessTokenFragment extends Fragment {

View view;
TextView textView1,textView;
Button button,choosebtn,uploadbtn;
VolleyHelper volleyHelper;
Context context;
private String tokenUrl = "https://www.grademojo.com/api/interview/token/access";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.fragment_access_token, container, false);
        context = getActivity();
        getActivity().setTitle("Access Token");
        textView=(TextView)view.findViewById(R.id.access_token);
        if(LocalDbManager.getToken(getContext().getApplicationContext())!=null)
        {
            textView.setText(LocalDbManager.getToken(getContext().getApplicationContext()));
        }

        button=(Button)view.findViewById(R.id.access_btn);
        volleyHelper=VolleyHelper.getRestHelper(context);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchToken();
                Snackbar.make(view, "Token Successfully Access", Snackbar.LENGTH_LONG).show();

            }
        });

        if(LocalDbManager.getToken(getContext().getApplicationContext())=="" || (LocalDbManager.getToken(getContext().getApplicationContext()).equals("") || LocalDbManager.getToken(getContext().getApplicationContext()) == null))
        {
            Snackbar.make(view, "Please Access Token"+LocalDbManager.getToken(getContext().getApplicationContext()), Snackbar.LENGTH_LONG).show();
        }
        else {
            Snackbar.make(view, "Token Already Accessed", Snackbar.LENGTH_LONG).show();
        }
        return view;
    }

    private void fetchToken(){

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
        volleyHelper.postRequest(tokenUrl, new HashMap<String, String>(), new JSONObject(), new OnVollyJsonObjectResponse() {
            @Override
            public void onJsonObjectSuccess(JSONObject response) {
                pDialog.hide();
                try {
                    boolean isSuccess = response.getBoolean("success");
                    if (isSuccess) {
                        JSONObject data = response.getJSONObject("data");
                        String token = data.getString("access_token");
                        textView.setText(token);
                        LocalDbManager.saveToken(getContext().getApplicationContext(), token);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onJsonObjectError(VolleyError error) {
                pDialog.hide();
                Snackbar.make(view, error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }



}
