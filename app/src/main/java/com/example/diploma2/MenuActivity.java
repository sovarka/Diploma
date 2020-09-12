package com.example.diploma2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.snackbar.Snackbar;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

public class MenuActivity extends Activity {
    private static final String TABLE_TRIP_DETAILS = "trip_details";
    Button rem;
    Button route;
    Button find_tickets;
    TextView name, start, end;
    SharedPreferences sPref;
    Intent intent;
    Context ctx;
    final String SAVED_TRIP = "saved_trip";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //привязываем название, даты поездки к text_view
        name = (TextView) findViewById(R.id.menu_name);
        name.getBackground().setAlpha(160);
        start = (TextView) findViewById(R.id.menu_start);
        end = (TextView) findViewById(R.id.menu_end);

        DBHelper db = new DBHelper(this);

        long id = db.GetMaxIDTrip();
        ArrayList<HashMap<String, String>> tripList = db.GetTripByTripId(id);
        HashMap<String, String> hm = new HashMap<String, String>();
        hm = tripList.get(tripList.size() - 1);
        String str_name = hm.get("name");
        String str_date_start = hm.get("date_start");
        String str_date_end = hm.get("date_end");

        ArrayList<HashMap<String, String>> routeList = db.GetRouteDetails();
        if (routeList.size() > 0) {
            String str_date_start1 = routeList.get(0).get("date1");
            String str_date_end1 = routeList.get(routeList.size() - 1).get("date2");
            if (!str_date_start.equals(str_date_start1) && !str_date_end.equals(str_date_end1)) {
                int i = db.UpdateTripDetails(str_date_start1, str_date_end1, (int)id);
                str_date_start = str_date_start1;
                str_date_end = str_date_end1;
                Snackbar.make(name, "Start and end dates of the trip were updated", Snackbar.LENGTH_LONG)
                        .show();
            }
            else if (!str_date_start.equals(str_date_start1)) {
                int i = db.UpdateTripDetails(str_date_start1, str_date_end, (int)id);
                str_date_start = str_date_start1;
                Snackbar.make(name, "Start date of the trip were updated", Snackbar.LENGTH_LONG)
                        .show();
            }
            else if (!str_date_end.equals(str_date_end1)) {
                int i = db.UpdateTripDetails(str_date_start, str_date_end1, (int)id);
                str_date_end = str_date_end1;
                Snackbar.make(name, "End date of the trip were updated", Snackbar.LENGTH_LONG)
                        .show();
            }
        }

        //устанавливаем значения из базы данных
        if (!str_date_start.equals("")) {
            start.setText(str_date_start);
        }
        if (!str_date_end.equals("")) {
            end.setText(str_date_end);
        }
        name.setText(str_name);




        //инициализируем кнопку удаления поездки, присваиваем обработчик
        rem = (Button) findViewById(R.id.remove);
        View.OnClickListener oclBtnRemove = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //уведомляем систему об удалении поездки
                sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.remove(SAVED_TRIP);
                ed.commit();
                //очистим маршрут
                DBHelper db = new DBHelper(MenuActivity.this);
                ArrayList<HashMap<String, String>> routeList = db.GetRouteDetails();
                if (routeList.size() > 0) {
                    Log.d("MyLog_size_of_route", String.valueOf(routeList.size()));
                    for (int i = 0; i < routeList.size(); i++) {
                        db.DeleteRouteByPos(String.valueOf(i));
                    }
                }
                intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };
        rem.setOnClickListener(oclBtnRemove);

        route = (Button) findViewById(R.id.btn_route);
        View.OnClickListener oclBtnRoute =  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MenuActivity.this, RouteActivity.class);
                startActivity(intent);
            }
        };
        route.setOnClickListener(oclBtnRoute);

        Button cities = (Button) findViewById(R.id.btn_cities);
        View.OnClickListener oclBtnMap = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MenuActivity.this, CitiesActivity.class);
                startActivity(intent);
            }
        };
        cities.setOnClickListener(oclBtnMap);

        find_tickets = (Button) findViewById(R.id.btn_find_tickets);
        View.OnClickListener oclBtnTickets = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MenuActivity.this, FindTicketsActivity.class);
                startActivity(intent);
            }
        };
        find_tickets.setOnClickListener(oclBtnTickets);
    }

    //отключаем работу кнопки Назад
    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
