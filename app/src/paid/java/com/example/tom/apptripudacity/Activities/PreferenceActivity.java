package com.example.tom.apptripudacity.Activities;


import android.content.ContentResolver;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.tom.apptripudacity.Data.PlaceContract;
import com.example.tom.apptripudacity.R;
import com.google.android.gms.ads.MobileAds;


public class PreferenceActivity extends AppCompatActivity{

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_activity);
        ActionBar actionBar = this.getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }




        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Uri deleteUri = PlaceContract.PlaceEntry.CONTENT_URI;
                        ContentResolver contentResolver = getContentResolver();
                        contentResolver.delete(deleteUri, null, null);
                    }
                });
        builder.setNegativeButton(R.string.dialog_nao, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

            }
        });

        dialog = builder.create();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbarpreference_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        else if(id ==  R.id.action_deleteall) {
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
