package com.example.diploma2;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class RouteActivity extends Activity {
    private static final int CM_DELETE_ID = 1;
    Intent intent;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_main);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        final DBHelper db = new DBHelper(this);
        ArrayList<HashMap<String, String>> routeList = db.GetRouteDetails();

        //пустая ли страница, если нет, то затереть запись
        text = (TextView) findViewById(R.id.create_your_route);
        text.getBackground().setAlpha(160);
        if (routeList.size() > 0) {
            text.setVisibility(View.GONE);
        }


        //создаем адаптер
        ListView lv = (ListView) findViewById(R.id.list_route);
        ListAdapter adapter = new SimpleAdapter(RouteActivity.this, routeList, R.layout.item_route_main,
                new String[]{"city1", "city2", "date1", "date2", "time1", "time2"},
                new int[]{R.id.city1, R.id.city2, R.id.date1, R.id.date2, R.id.time1, R.id.time2});
        lv.setAdapter(adapter);

        // добавляем контекстное меню к списку
        registerForContextMenu(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d("MyLog", "itemClick: position = " + position + ", id = "
                        + id);
                intent = new Intent(RouteActivity.this, OneItemRoute.class);
                intent.putExtra("pos", String.valueOf(position));
                startActivity(intent);

            }
        });


        //определяем кнопку добавления элемента маршрута
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(RouteActivity.this, NewRouteActivity.class);
                startActivity(intent);
            }
        });

        //определяем кнопку удаления элемента маршрута
        FloatingActionButton fab1 = findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteDaysNotes();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ArrayList<HashMap<String, String>> myList = db.GetRouteDetails();
                long size = myList.size();
                db.DeleteRouteByPos(String.valueOf(size - 1));
                db.DeleteNotesByPos(String.valueOf(size - 1));


                //создаем адаптер, чтобы обновить список маршрута
                ListView lv = (ListView) findViewById(R.id.list_route);
                ArrayList<HashMap<String, String>> routeList = db.GetRouteDetails();
                ListAdapter adapter = new SimpleAdapter(RouteActivity.this, routeList, R.layout.item_route_main,
                        new String[]{"city1", "city2", "date1", "date2", "time1", "time2"},
                        new int[]{R.id.city1, R.id.city2, R.id.date1, R.id.date2, R.id.time1, R.id.time2});
                lv.setAdapter(adapter);
                if (routeList.size() == 0) {
                    text.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    public void deleteDaysNotes() throws ParseException {
        DBHelper db = new DBHelper(RouteActivity.this);
        ArrayList<HashMap<String, String>> routeList = db.GetRouteDetails();
        int size = routeList.size();
        HashMap<String, String> hm = routeList.get(size - 1);
        String city1 = hm.get("city1");
        String city2 = hm.get("city2");
        String date1 = hm.get("date1");
        String date2 = hm.get("date2");
        date1 = transformDate(date1);
        date2 = transformDate(date2);
        if (size == 1) {
            String hash1 = city1 + date1;
            String hash2 = city2 + date2;
            db.DeleteDayNotesByHash(hash1);
            db.DeleteDayNotesByHash(hash2);
        }
        else if (size > 1) {
            String last_date = routeList.get(size - 2).get("date2");
            String last_city = city1;
            while (!last_date.equals(date1)) {
                last_date = getNextDay(last_date);
                String hash = last_city + last_date;
                db.DeleteDayNotesByHash(hash);
            }
            String hash = city2 + date2;
            db.DeleteDayNotesByHash(hash);
        }

    }

    public String transformDate(String date1) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        Date str_date1 = format.parse(date1);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(str_date1);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String nextDate = format.format(calendar.getTime());
        Date str_date2 = format.parse(nextDate);
        calendar.setTime(str_date2);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String prevDay = format.format(calendar.getTime());
        return prevDay;
    }

    public String getNextDay(String date1) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String nextDate = "";
        try {
            Date str_date1 = format.parse(date1);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(str_date1);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            nextDate = format.format(calendar.getTime());
            return nextDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return nextDate;
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(RouteActivity.this, MenuActivity.class);
        startActivity(intent);
    }

}
