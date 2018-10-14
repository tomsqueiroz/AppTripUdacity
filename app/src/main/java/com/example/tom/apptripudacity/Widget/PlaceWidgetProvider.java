package com.example.tom.apptripudacity.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.RemoteViews;

import com.example.tom.apptripudacity.Activities.DetailsActivity;
import com.example.tom.apptripudacity.Data.AsyncContentService;
import com.example.tom.apptripudacity.Data.PlaceContract;
import com.example.tom.apptripudacity.Interfaces.AsyncTaskDelegate;
import com.example.tom.apptripudacity.Models.Geometry;
import com.example.tom.apptripudacity.Models.Photo;
import com.example.tom.apptripudacity.Models.Result;
import com.example.tom.apptripudacity.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.tom.apptripudacity.Activities.MainActivity.INDEX_ID_GOOGLE;
import static com.example.tom.apptripudacity.Activities.MainActivity.INDEX_LAT;
import static com.example.tom.apptripudacity.Activities.MainActivity.INDEX_LNG;
import static com.example.tom.apptripudacity.Activities.MainActivity.INDEX_NOME;
import static com.example.tom.apptripudacity.Activities.MainActivity.INDEX_PHOTOS;
import static com.example.tom.apptripudacity.Activities.MainActivity.INDEX_RATING;

public class PlaceWidgetProvider extends AppWidgetProvider implements AsyncTaskDelegate{

    private static final int ID_PLACES_LOADER = 24;
    private Context context;
    private static RemoteViews views;
    private AppWidgetManager appWidgetManager;
    private int appWidgetId;

     void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        this.context = context;
        this.appWidgetId = appWidgetId;
        this.appWidgetManager = appWidgetManager;
        views = new RemoteViews(context.getPackageName(), R.layout.place_widget);
         new AsyncContentService(context, this).execute();
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


    @Override
    public void processFinish(Object output) {
        if(views!=null){
            if(((Result) output).getName() != null)
                views.setTextViewText(R.id.tv_widget, ((Result) output).getName());
            else{
                views.setTextViewText(R.id.tv_widget, context.getString(R.string.db_vazio_aviso));
            }
        }
        Intent i = new Intent(context, DetailsActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("resultado", ((Result) output));
        i.putExtra("resultado", b);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
        views.setOnClickPendingIntent(R.id.tv_widget, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
