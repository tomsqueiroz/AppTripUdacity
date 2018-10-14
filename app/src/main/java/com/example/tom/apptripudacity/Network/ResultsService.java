package com.example.tom.apptripudacity.Network;

import android.content.Context;
import android.os.AsyncTask;

import com.example.tom.apptripudacity.Interfaces.AsyncTaskDelegate;
import com.example.tom.apptripudacity.Interfaces.GetDataService;
import com.example.tom.apptripudacity.Models.Example;
import com.example.tom.apptripudacity.Models.Result;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class ResultsService extends AsyncTask<Map, Void, List<Result>> {

    Context context;
    AsyncTaskDelegate asyncTaskDelegate = null;
    GetDataService service;

    public ResultsService(Context context, AsyncTaskDelegate asyncTaskDelegate){
        this.context = context;
        this.asyncTaskDelegate = asyncTaskDelegate;
    }


    @Override
    protected List<Result> doInBackground(Map... maps) {
        Call<Example> call = service.getNearbyPlaces(maps[0]);
        List<Result> results = null;
        try {
             results = call.execute().body().getResults();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    protected void onPostExecute(List<Result> results) {
        if(asyncTaskDelegate!=null){
            asyncTaskDelegate.processFinish(results);
        }
    }
}
