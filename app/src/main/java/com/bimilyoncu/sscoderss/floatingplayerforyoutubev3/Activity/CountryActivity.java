package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.CountryGridViewAdapter;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Database.DatabaseForPlaylists;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.CountryGridItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;

import java.util.ArrayList;
import java.util.List;

public class CountryActivity extends AppCompatActivity {
    private List<CountryGridItem> gridItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);
        (CountryActivity.this).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        (CountryActivity.this).getSupportActionBar().setTitle(CountryActivity.this.getResources().getString(R.string.app_bar_title_trends_countries));
        final GridView myGrid = (GridView) findViewById(R.id.gridview);
        CountryGridViewAdapter adapter = new CountryGridViewAdapter(CountryActivity.this, fillFlags());
        myGrid.setAdapter(adapter);
        myGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(CountryActivity.this, PlaylistActivity.class);
                intent.putExtra("contentType","countryTrendVid");
                intent.putExtra("countryCode",gridItem.get(position).getCountryCode());
                intent.putExtra("countryFlagUrl",gridItem.get(position).getCounrtyFlagUrl());
                intent.putExtra("countryName",gridItem.get(position).getCountryName());
                startActivity(intent);
            }
        });
    }

    private List<CountryGridItem> fillFlags() {
        gridItem = new ArrayList<>();
        DatabaseForPlaylists db = new DatabaseForPlaylists(CountryActivity.this);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        final Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_countries order by countryName asc", null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                new Thread() {
                    public void run() {
                        do {
                            CountryGridItem mItem = new CountryGridItem();
                            mItem.setCounrtyFlagUrl(cur.getString(cur.getColumnIndex("flagUrl")));
                            mItem.setCountryName(cur.getString(cur.getColumnIndex("countryName")));
                            mItem.setCountryCode(cur.getString(cur.getColumnIndex("countryCode")));
                            gridItem.add(mItem);
                        } while (cur.moveToNext());
                    }
                }.start();
            }
        }
        return gridItem;
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
