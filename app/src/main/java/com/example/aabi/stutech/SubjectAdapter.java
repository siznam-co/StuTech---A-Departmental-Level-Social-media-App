package com.example.aabi.stutech;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.MyViewHolder> {

    private Context sContext;
    private List<String> sData ;

    public SubjectAdapter(Context sContext, List<String> subjectList) {
        this.sContext = sContext;
        this.sData = subjectList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(sContext).inflate(R.layout.row_subject_item, viewGroup ,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.subjectNameTextView.setText(sData.get(i));
    }

    @Override
    public int getItemCount() {
        return sData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView subjectNameTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            subjectNameTextView = itemView.findViewById(R.id.row_subject_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent subjectToHomeActivity = new Intent(sContext, SubjectActivity.class);
                    int position = getAdapterPosition();

                    subjectToHomeActivity.putExtra("subjectName",sData.get(position));
                    //Toast.makeText(sContext,sData.get(position),Toast.LENGTH_SHORT).show();
                    subjectToHomeActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    sContext.startActivity(subjectToHomeActivity);
                    //Toast.makeText(sContext,"Get lost",Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}
