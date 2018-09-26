package com.example.tom.apptripudacity.Widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.example.tom.apptripudacity.Data.PlaceContract;
import com.example.tom.apptripudacity.R;

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }

    class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{

        Context mContext;
        Cursor mCursor;

        public ListRemoteViewsFactory(Context context){
            this.mContext = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            Uri queryUri = PlaceContract.PlaceEntry.CONTENT_URI;
            if (mCursor != null) mCursor.close();
            mCursor = mContext.getContentResolver().query(
                    queryUri,
                    null,
                    null,
                    null,
                    null
            );

        }

        @Override
        public void onDestroy() {
            mCursor.close();
        }

        @Override
        public int getCount() {
            if(mCursor != null) return mCursor.getCount();
            return 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            if(mCursor == null || mCursor.getCount() == 0) return null;
            mCursor.moveToPosition(i);

            int nameIndex = mCursor.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_NAME);
            int placeidIndex = mCursor.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PLACE_ID);

            String name = mCursor.getString(nameIndex);
            String placeId = mCursor.getString(placeidIndex);

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.place_widget);
            views.setTextViewText(R.id.tv_widget_item, name);

            Bundle extras = new Bundle();
            extras.putString(PlaceContract.PlaceEntry.COLUMN_PLACE_ID, placeId);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.tv_widget_item, fillInIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
