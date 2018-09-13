package com.example.tom.apptripudacity.Adapters;

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

import com.example.tom.apptripudacity.Models.Result;
import com.example.tom.apptripudacity.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsAdapterViewHolder> {

    private final ResultsAdapterOnClickHandler mClickHandler;
    private Context context;
    private List<Result> results;
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&maxheight=600&key=AIzaSyAlUvgTV9PolnqpyWUQpMd296BGOJQBY3E&photoreference=";

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
        String photoReference = results.get(position).getPhotos().get(0).getPhotoReference();
        Picasso.with(context).load(BASE_URL + photoReference).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        if(results == null) return 0;
        return results.size();
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

    public void setResultList(List<Result> resultList){
        this.results = resultList;
        notifyDataSetChanged();
    }



}
