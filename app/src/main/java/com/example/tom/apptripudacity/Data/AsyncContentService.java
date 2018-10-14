package com.example.tom.apptripudacity.Data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.tom.apptripudacity.Interfaces.AsyncTaskDelegate;
import com.example.tom.apptripudacity.Models.Geometry;
import com.example.tom.apptripudacity.Models.Photo;
import com.example.tom.apptripudacity.Models.Result;

import java.util.ArrayList;
import java.util.List;

import static com.example.tom.apptripudacity.Activities.MainActivity.INDEX_ID_GOOGLE;
import static com.example.tom.apptripudacity.Activities.MainActivity.INDEX_LAT;
import static com.example.tom.apptripudacity.Activities.MainActivity.INDEX_LNG;
import static com.example.tom.apptripudacity.Activities.MainActivity.INDEX_NOME;
import static com.example.tom.apptripudacity.Activities.MainActivity.INDEX_PHOTOS;
import static com.example.tom.apptripudacity.Activities.MainActivity.INDEX_RATING;

public class AsyncContentService extends AsyncTask<Void, Void, Result> {

    private Context context;
    private AsyncTaskDelegate asyncTaskDelegate;

    public AsyncContentService(Context context, AsyncTaskDelegate asyncTaskDelegate) {
        this.context = context;
        this.asyncTaskDelegate = asyncTaskDelegate;
    }

    @Override
    protected Result doInBackground(Void... voids) {
        ContentResolver resolver = context.getContentResolver();
        Cursor data = resolver.query(PlaceContract.PlaceEntry.CONTENT_URI, null,null,null,null,null);

        Result result = new Result();
        if (data != null) {
            data.moveToLast();
            if (data.getCount() != 0) {
                result.setName(data.getString(INDEX_NOME));
                Geometry geometry = new Geometry();
                com.example.tom.apptripudacity.Models.Location location = new com.example.tom.apptripudacity.Models.Location();
                location.setLat(data.getDouble(INDEX_LAT));
                location.setLng(data.getDouble(INDEX_LNG));
                geometry.setLocation(location);
                result.setGeometry(geometry);
                result.setPlaceId(data.getString(INDEX_ID_GOOGLE));
                List<Photo> photos = new ArrayList<>();
                Photo photo = new Photo();
                photo.setPhotoReference(data.getString(INDEX_PHOTOS));
                photos.add(photo);
                result.setPhotos(photos);
                result.setRating(data.getFloat(INDEX_RATING));
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Result result) {
        asyncTaskDelegate.processFinish(result);
    }
}
