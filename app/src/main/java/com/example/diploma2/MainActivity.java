package com.example.diploma2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    SharedPreferences sPref;
    Intent intent;
    Button btn_start;
    TextView warn;
    final String SAVED_TRIP = "saved_trip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //установка полупрозрачного фона для текста
        warn = (TextView) findViewById(R.id.text_empty_trip);

        //есть ли у нас активная поездка
        sPref = getSharedPreferences("MyPref",MODE_PRIVATE);
        String savedTrip = sPref.getString(SAVED_TRIP, "");

        //если есть, то сразу загружаем меню  поездки
        if (!(savedTrip.equals(""))) {
            intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
            Log.d("MyLog", "Active trip exist");
        }
        //иначе
        else {
            DBHelper db = new DBHelper(this);
            long id = db.GetMaxIDTrip();
            db.DeleteTrip(id);
            Log.d("MyLog", "No active trip");
        }

        //определяем кнопку start
        btn_start = (Button) findViewById(R.id.btn_start_trip);
        btn_start.getBackground().setAlpha(160);
        //присваиваем кнопке start обработчик
        View.OnClickListener oclBtnStart = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, NewTripActivity.class);
                startActivity(intent);
            }
        };
        btn_start.setOnClickListener(oclBtnStart);
    }
}
