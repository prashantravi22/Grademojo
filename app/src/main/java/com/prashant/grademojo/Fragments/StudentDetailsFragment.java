package com.prashant.grademojo.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.prashant.grademojo.Helpers.RestHelper.OnVollyJsonObjectResponse;
import com.prashant.grademojo.Helpers.VolleyHelper;
import com.prashant.grademojo.LocalDbManager;
import com.prashant.grademojo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class StudentDetailsFragment extends Fragment {



    View view;
    TextView tx_id,tx_roll,tx_name,tx_gender,tx_att,tx_perf;
    VolleyHelper volleyHelper;
    public static String id;
    Bundle bundle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_student_details, container, false);

        volleyHelper= VolleyHelper.getRestHelper(getActivity());
        getActivity().setTitle("Student Detail");
        tx_id=(TextView)view.findViewById(R.id.stud_id);
        tx_roll=(TextView)view.findViewById(R.id.roll);
        tx_name=(TextView)view.findViewById(R.id.full_name);
        tx_gender=(TextView)view.findViewById(R.id.gender);
        tx_att=(TextView)view.findViewById(R.id.attendance);
        tx_perf=(TextView)view.findViewById(R.id.performance);
        bundle = getArguments();
        if(bundle!=null) {

            id = bundle.getString("id");
            String name = bundle.getString("name");
            String roll = bundle.getString("roll");
            String gender = bundle.getString("gender");
            tx_id.setText(id);
            tx_name.setText(name);
            tx_roll.setText(roll);
            tx_gender.setText(gender);
        }
        else {
            Snackbar.make(view, "something went wrong!!", Snackbar.LENGTH_LONG).show();
        }
        try {
            fetchDetails();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    public void fetchDetails() throws JSONException {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
        Map<String, String> headerData = new HashMap<>();
        headerData.put("Content-Type", "application/json");
        JSONObject postData = new JSONObject();
        postData.put("access_token", LocalDbManager.getToken(getContext().getApplicationContext()));
        postData.put("student_id",id);
        String studFetchURL = "https://www.grademojo.com/api/interview/student/info";
        volleyHelper.postRequest(studFetchURL, headerData, postData, new OnVollyJsonObjectResponse() {
            @Override
            public void onJsonObjectSuccess(JSONObject response) {
                  pDialog.hide();

                try {
                    JSONObject jsonObject=response.getJSONObject("data");
                    JSONObject jsonObject1=jsonObject.getJSONObject("attendance");
                    String s;
                    s = (String) jsonObject1.get("avg");

                    JSONObject jsonObject2=jsonObject.getJSONObject("performance");
                    String s1;
                    s1 = (String) jsonObject2.get("avg");
                    System.out.println("*********8 "+ s + " "+s1);

                    tx_att.setText(s);
                    tx_perf.setText(s1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onJsonObjectError(VolleyError error) {
                Snackbar.make(view, error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });


    }



}
