package com.example.tom.apptripudacity.Activities.Adapters;

/**
 * Created by tom on 27/08/18.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tom.apptripudacity.Activities.Models.Result;
import com.example.tom.apptripudacity.R;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsAdapterViewHolder> {

    private final ResultsAdapterOnClickHandler mClickHandler;
    private Context context;
    private List<Bitmap> bitmapList;

    @NonNull
    @Override
    public ResultsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int resultItemLayout = R.layout.item_main;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(resultItemLayout, parent, shouldAttachToParentImmediately);
        ResultsAdapterViewHolder holder = new ResultsAdapterViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsAdapterViewHolder holder, int position) {
        holder.imageView.setImageBitmap(bitmapList.get(position));
    }

    @Override
    public int getItemCount() {
        if(bitmapList == null) return 0;
        return bitmapList.size();
    }

    public interface ResultsAdapterOnClickHandler{
        void onClick(int position);
    }

    public ResultsAdapter(ResultsAdapterOnClickHandler clickHandler, Context context){
        mClickHandler = clickHandler;
        this.context = context;
    }

    public class ResultsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView imageView;

        public ResultsAdapterViewHolder(View view){
            super(view);
            imageView = (ImageView) view.findViewById(R.id.item_imageView);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPos = getAdapterPosition();
            mClickHandler.onClick(adapterPos);
        }
    }

    public void setBitmapList(List<Bitmap> bitmapList){
        this.bitmapList = bitmapList;
        notifyDataSetChanged();
    }



}
