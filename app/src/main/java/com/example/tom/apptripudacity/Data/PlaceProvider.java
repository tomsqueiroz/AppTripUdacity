package com.example.tom.apptripudacity.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class PlaceProvider extends ContentProvider {

    public static final int CODE_PLACE = 100;
    public static final int CODE_PLACE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PlaceDbHelper mDbHelper;

    public static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PlaceContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PlaceContract.PATH_PLACE, CODE_PLACE);
        matcher.addURI(authority, PlaceContract.PATH_PLACE + "/#", CODE_PLACE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PlaceDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        switch(sUriMatcher.match(uri)){

            case CODE_PLACE_WITH_ID: {

                String placeId = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{placeId};
                cursor = mDbHelper.getReadableDatabase().query(
                        PlaceContract.PlaceEntry.TABLE_NAME,
                        projection,
                        PlaceContract.PlaceEntry.COLUMN_PLACE_ID + " =? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_PLACE:{
                cursor = mDbHelper.getReadableDatabase().query(
                        PlaceContract.PlaceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if(sUriMatcher.match(uri) != CODE_PLACE_WITH_ID){
            throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }else{

            long id = mDbHelper.getWritableDatabase().insert(
                    PlaceContract.PlaceEntry.TABLE_NAME,
                    null,
                    values
            );

            Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;

        if (null == selection) selection = "1";


        switch (sUriMatcher.match(uri)) {

            case CODE_PLACE:
                numRowsDeleted = mDbHelper.getWritableDatabase().delete(
                        PlaceContract.PlaceEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            case CODE_PLACE_WITH_ID:
                String placeId = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{placeId};

                numRowsDeleted = mDbHelper.getWritableDatabase().delete(
                        PlaceContract.PlaceEntry.TABLE_NAME,
                        PlaceContract.PlaceEntry.COLUMN_PLACE_ID + " =? ",
                        selectionArguments);


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
