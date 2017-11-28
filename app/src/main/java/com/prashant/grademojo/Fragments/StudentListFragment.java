package com.prashant.grademojo.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.prashant.grademojo.Adapters.MainRecyAdapter;
import com.prashant.grademojo.Helpers.RecytemClick;
import com.prashant.grademojo.Helpers.RestHelper.OnVollyJsonObjectResponse;
import com.prashant.grademojo.Helpers.VolleyHelper;
import com.prashant.grademojo.LocalDbManager;
import com.prashant.grademojo.Model.StudentListModel;
import com.prashant.grademojo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class StudentListFragment extends Fragment {


    RecyclerView act_recyecler;
    MainRecyAdapter mainRecyAdapter;
    ArrayList<StudentListModel> studentListModels;
    VolleyHelper volleyHelper;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_student_list, container, false);
        volleyHelper = VolleyHelper.getRestHelper(getActivity());
        act_recyecler = (RecyclerView) view.findViewById(R.id.act_recycler);
        act_recyecler.setHasFixedSize(true);
        getActivity().setTitle("Student List");
        act_recyecler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        act_recyecler.setItemAnimator(new DefaultItemAnimator());

        act_recyecler.addOnItemTouchListener(new RecytemClick(getActivity(), new RecytemClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Fragment fragment = new StudentDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", studentListModels.get(position).getStd_id());
                bundle.putString("roll", studentListModels.get(position).getRollno());
                bundle.putString("name", studentListModels.get(position).getName());
                bundle.putString("gender", studentListModels.get(position).getGender());
                fragment.setArguments(bundle);
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activity_main, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        }));
        setHasOptionsMenu(true);
        if (LocalDbManager.getToken(getContext().getApplicationContext()) == "" || (LocalDbManager.getToken(getContext().getApplicationContext()).equals("") || LocalDbManager.getToken(getContext().getApplicationContext()) == null)) {
            Snackbar.make(view, "Please Access Token" + LocalDbManager.getToken(getContext().getApplicationContext()), Snackbar.LENGTH_LONG).show();
        } else {
            try {

                fetchstud();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return view;
    }

    public void fetchstud() throws JSONException {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
        Map<String, String> headerData = new HashMap<>();
        headerData.put("Content-Type", "application/json");
        JSONObject postData = new JSONObject();
        postData.put("access_token", LocalDbManager.getToken(getContext().getApplicationContext()));
        final String studFetchURL = "https://www.grademojo.com/api/interview/student/list";
        volleyHelper.postRequest(studFetchURL, headerData, postData, new OnVollyJsonObjectResponse() {
            @Override
            public void onJsonObjectSuccess(JSONObject response) {
                System.out.println("****** res student frag" + response.toString());
                pDialog.hide();
                try {
                    JSONObject jsonObject = response.getJSONObject("data");
                    studentListModels = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONArray("students");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        String id = jsonObject1.getString("student_id");
                        String roll = jsonObject1.getString("roll");
                        String name = jsonObject1.getString("name");
                        String gender = jsonObject1.getString("gender");
                        StudentListModel studentListModel = new StudentListModel();
                        studentListModel.setStd_id(id);
                        studentListModel.setRollno(roll);
                        studentListModel.setName(name);
                        studentListModel.setGender(gender);
                        studentListModels.add(studentListModel);
                    }

                    mainRecyAdapter = new MainRecyAdapter(getActivity(), studentListModels);
                    act_recyecler.setAdapter(mainRecyAdapter);
                    mainRecyAdapter.notifyDataSetChanged();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.stud_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.refresh) {
            try {

                fetchstud();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
