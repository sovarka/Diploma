package com.example.diploma2;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewTripActivity extends Activity {
    SharedPreferences sPref;
    Button btn_save;
    EditText edit_the_start, edit_the_end, edit_the_name;
    final String SAVED_TRIP = "saved_trip";
    Intent intent;
    private int mYear, mMonth, mDay;
    TextView enter_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        enter_name = (TextView) findViewById(R.id.text_enter_the_name);
        enter_name.getBackground().setAlpha(160);

        //определяем поля edit
        edit_the_name = (EditText) findViewById(R.id.edit_the_name);
        edit_the_name.getBackground().setAlpha(160);
        edit_the_start = (EditText) findViewById(R.id.edit_the_start);
        edit_the_end = (EditText) findViewById(R.id.edit_the_end);
        edit_the_start.setEnabled(true);
        edit_the_start.setTextIsSelectable(true);
        edit_the_start.setFocusable(false);
        edit_the_start.setFocusableInTouchMode(false);
        edit_the_end.setEnabled(true);
        edit_the_end.setTextIsSelectable(true);
        edit_the_end.setFocusable(false);
        edit_the_end.setFocusableInTouchMode(false);
        //определяем кнопку save
        btn_save = (Button) findViewById(R.id.btn_save_trip);

        //присваиваем edit_start обработчик
        View.OnClickListener ocl_edit_start = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //открываем календарь
                callDatePicker(1);
            }
        };
        edit_the_start.setOnClickListener(ocl_edit_start);

        //присваиваем edit_end обработчик
        View.OnClickListener ocl_edit_end = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //открываем календарь
                callDatePicker(2);
            }
        };
        edit_the_end.setOnClickListener(ocl_edit_end);


        //присваиваем кнопке save обработчик
        View.OnClickListener oclBtnSave = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date1 = edit_the_start.getText().toString();
                String date2 = edit_the_end.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                //проверяем поле ввода названия
                if (edit_the_name.getText().toString().equals("")) {
                    Snackbar.make(v, "Enter the name of your trip", Snackbar.LENGTH_LONG)
                            .show();
                }
                //если название есть, проверим даты, если хотя бы одна отсутствует, можемм сохранить поездку
                else {
                    if (edit_the_start.getText().toString().equals("") || edit_the_end.getText().toString().equals("")) {
                        //уведомим систему о существующей поездке
                        Log.d("MyLog", "right dates");
                        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString(SAVED_TRIP, "saved");
                        ed.commit();

                        //запишем данные в базу данных
                        DBHelper db_help = new DBHelper(NewTripActivity.this);
                        String name = edit_the_name.getText().toString();
                        String start = edit_the_start.getText().toString();
                        String end = edit_the_end.getText().toString();
                        db_help.insertTripDetails(name, start, end);


                        //переходим в главное меню
                        intent = new Intent(NewTripActivity.this, MenuActivity.class);
                        startActivity(intent);
                    }
                    //обе даты есть, проверим их корректность
                    else {
                        try {
                            Date str_date1 = sdf.parse(date1);
                            Date str_date2 = sdf.parse(date2);
                            //сравним дату начала  и конца
                            if (str_date1.after(str_date2)) {
                                Log.d("MyLog","date1 after date2");
                                //некорректные даты
                                Snackbar.make(v, "Error: Start date later than end date", Snackbar.LENGTH_LONG)
                                        .show();
                                edit_the_end.setText("");
                            }
                            else {
                                //даты корректны, сохраним поездку, уведомим систему о существующей поездке
                                Log.d("MyLog", "right dates");
                                sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
                                SharedPreferences.Editor ed = sPref.edit();
                                ed.putString(SAVED_TRIP, "saved");
                                ed.commit();

                                //запишем данные в базу данных
                                DBHelper db_help = new DBHelper(NewTripActivity.this);
                                String name = edit_the_name.getText().toString();
                                String start = edit_the_start.getText().toString();
                                String end = edit_the_end.getText().toString();
                                db_help.insertTripDetails(name, start, end);


                                //переходим в главное меню
                                intent = new Intent(NewTripActivity.this, MenuActivity.class);
                                startActivity(intent);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        btn_save.setOnClickListener(oclBtnSave);
    }

    private void callDatePicker(final int flag) {
        // получаем текущую дату
        final Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        // инициализируем диалог выбора даты текущими значениями
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String editTextDateParam = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                        if (flag == 1) {
                            edit_the_start.setText(editTextDateParam);
                        }
                        else {
                            edit_the_end.setText(editTextDateParam);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

}
