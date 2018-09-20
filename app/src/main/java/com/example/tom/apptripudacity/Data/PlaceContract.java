package com.example.tom.apptripudacity.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by tom on 20/09/18.
 */

public class PlaceContract {

    public static final String CONTENT_AUTHORITY = "com.example.tom.apptripudacity";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PLACE = "place";

    public static final class PlaceEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PLACE)
                .build();

        public static final String TABLE_NAME = "place";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PLACE_ID = "place_id";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LNG = "lng";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_PHOTOS = "photos";

        public static Uri buildPlaceUriWithId(String id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(id)
                    .build();
        }
    }
}
