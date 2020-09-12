package com.example.diploma2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class OneItemRoute extends Activity {
    EditText city1, city2, date1, date2, time1, time2, place1, place2, notes;
    String pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_item_route_activ);

        //установим все edit
        city1 = (EditText) findViewById(R.id.city1);
        city2 = (EditText) findViewById(R.id.city2);
        date1 = (EditText) findViewById(R.id.date1);
        date2 = (EditText) findViewById(R.id.date2);
        time1 = (EditText) findViewById(R.id.time1);
        time2 = (EditText) findViewById(R.id.time2);
        place1 = (EditText) findViewById(R.id.place1);
        place2 = (EditText) findViewById(R.id.place2);
        notes = (EditText) findViewById(R.id.notes);

        //получаем позицию, по которой мы сюда перешли
        Intent intent = getIntent();
        pos = intent.getStringExtra("pos");

        //получаем из базы данных информацию об этом маршруте
        DBHelper db = new DBHelper(this);
        ArrayList<HashMap<String, String>> routeList = db.GetRouteByPos(pos);
        HashMap<String, String> hm = routeList.get(0);
        ArrayList<HashMap<String, String>> notesList = db.GetRouteNotesByPos(pos);
        HashMap<String, String> hm1 = notesList.get(0);

        //устанавливаем текст в нужные поля
        city1.setText(hm.get("city1"));
        city2.setText(hm.get("city2"));
        date1.setText(hm.get("date1"));
        date2.setText(hm.get("date2"));
        time1.setText(hm.get("time1"));
        time2.setText(hm.get("time2"));
        place1.setText(hm.get("place1"));
        place2.setText(hm.get("place2"));
        notes.setText(hm1.get("notes"));



        //делаем неизменяемыми нужные поля
        city1.setFocusable(false);
        city1.setClickable(false);
        city1.setEnabled(false);
        city2.setFocusable(false);
        city2.setClickable(false);
        city2.setEnabled(false);
        time1.setFocusable(false);
        time1.setClickable(false);
        time1.setEnabled(false);
        time2.setFocusable(false);
        time2.setClickable(false);
        time2.setEnabled(false);
        date1.setFocusable(false);
        date1.setClickable(false);
        date1.setEnabled(false);
        date2.setFocusable(false);
        date2.setClickable(false);
        date2.setEnabled(false);


        //привязываем кнопку save changes
        Button save = findViewById(R.id.btn_save_changes_route);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //обновляем запись в базе данных
                String scity1 = city1.getText().toString();
                String scity2 = city2.getText().toString();
                String sdate1 = date1.getText().toString();
                String sdate2 = date2.getText().toString();
                String stime1 = time1.getText().toString();
                String stime2 = time2.getText().toString();
                String splace1 = place1.getText().toString();
                String splace2 = place2.getText().toString();
                String snotes = notes.getText().toString();

                DBHelper db = new DBHelper(OneItemRoute.this);
                db.UpdateRouteDetailsByPos(scity1, scity2, sdate1, sdate2, stime1, stime2, splace1, splace2, pos);
                db.UpdateRouteNotes(pos, snotes);
                Intent intent = new Intent(OneItemRoute.this, RouteActivity.class);
                startActivity(intent);
            }
        });



    }
}
