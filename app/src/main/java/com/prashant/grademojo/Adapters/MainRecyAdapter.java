package com.prashant.grademojo.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.prashant.grademojo.Model.StudentListModel;
import com.prashant.grademojo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 27-11-2017.
 */

public class MainRecyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<StudentListModel> studentListModels;

    public MainRecyAdapter(Context context, ArrayList<StudentListModel> studentListModels)
    {
        this.context=context;
        this.studentListModels=studentListModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(parent.getContext());
        ViewGroup mainGroup=(ViewGroup)layoutInflater.inflate(R.layout.item_main_row,parent,false);
        RecyclerViewHolder recyclerViewHolder=new RecyclerViewHolder(mainGroup);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        StudentListModel activityModel = studentListModels.get(position);
        RecyclerViewHolder recyclerViewHolder= (RecyclerViewHolder)holder;

            recyclerViewHolder.id.setText(activityModel.getStd_id());
            recyclerViewHolder.roll.setText(activityModel.getRollno());
            recyclerViewHolder.name.setText(activityModel.getName());
            recyclerViewHolder.gender.setText(activityModel.getGender());


    }

    @Override
    public int getItemCount() {
        return studentListModels.size();
    }


    public void setItems(ArrayList<StudentListModel> datas){
        studentListModels = new ArrayList<>(datas);
    }



    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView id;

        public TextView roll;
        public TextView name;
        public TextView gender;



        public RecyclerViewHolder(View itemView) {
            super(itemView);
            this.id=(TextView)itemView.findViewById(R.id.stud_id);
            this.roll=(TextView)itemView.findViewById(R.id.roll);
            this.name=(TextView)itemView.findViewById(R.id.full_name);
            this.gender=(TextView)itemView.findViewById(R.id.gender);
        }
    }
}
