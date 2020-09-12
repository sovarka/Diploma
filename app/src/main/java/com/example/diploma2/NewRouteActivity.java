package com.example.diploma2;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class NewRouteActivity extends Activity {
    Intent intent;
    EditText date1, date2, time1, time2, place1, place2;
    AutoCompleteTextView city1, city2;
    String last_city;
    Boolean both_cities_full = false, dt1_before_dt2 = false;
    Boolean full_date1 = true, full_date2 = true;
    Boolean full_time1 = true, full_time2 = true;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private int h1 = 0, h2 = 0, m1 = 0, m2 = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_route);

        final DBHelper db = new DBHelper(this);
        last_city ="";

        //определяем все поля edit
        city1 = findViewById(R.id.autoEditCity1);
        city2 = findViewById(R.id.autoEditCity2);
        date1 = findViewById(R.id.edit_date1);
        date2 = findViewById(R.id.edit_date2);
        time1 = findViewById(R.id.edit_time1);
        time2 = findViewById(R.id.edit_time2);
        place1 = findViewById(R.id.edit_place1);
        place2 = findViewById(R.id.edit_place2);

        String[] cities = getResources().getStringArray(R.array.array_cities);
        final List<String> citiesList = Arrays.asList(cities);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, citiesList);
        city1.setAdapter(adapter);
        city2.setAdapter(adapter);

        //присваиваем date1 обработчик
        View.OnClickListener ocl_date1 = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //открываем календарь
                callDatePicker(1);
            }
        };
        date1.setOnClickListener(ocl_date1);

        //присваиваем date2 обработчик
        View.OnClickListener ocl_date2 = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //открываем календарь
                callDatePicker(2);
            }
        };
        date2.setOnClickListener(ocl_date2);

        //присваиваем time1 обработчик
        View.OnClickListener ocl_time1 = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //открываем календарь
                callTimePicker(1);
            }
        };
        time1.setOnClickListener(ocl_time1);

        //присваиваем time1 обработчик
        View.OnClickListener ocl_time2 = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //открываем календарь
                callTimePicker(2);
            }
        };
        time2.setOnClickListener(ocl_time2);


        //делаем edit date1 и date2 кликабельными
        date1.setEnabled(true);
        date1.setTextIsSelectable(true);
        date1.setFocusable(false);
        date1.setFocusableInTouchMode(false);
        date2.setEnabled(true);
        date2.setTextIsSelectable(true);
        date2.setFocusable(false);
        date2.setFocusableInTouchMode(false);

        //делаем edit time1 и time2 кликабельными
        time1.setEnabled(true);
        time1.setTextIsSelectable(true);
        time1.setFocusable(false);
        time1.setFocusableInTouchMode(false);
        time2.setEnabled(true);
        time2.setTextIsSelectable(true);
        time2.setFocusable(false);
        time2.setFocusableInTouchMode(false);

        //определяем последний город
        final ArrayList<HashMap<String, String>> routeList = db.GetRouteDetails();
        if (routeList.size() != 0) {
            HashMap<String, String> hm = new HashMap<>();
            hm = routeList.get(routeList.size() - 1);
            last_city = hm.get("city2");
            city1.setFocusable(false);
            city1.setClickable(false);
            city1.setEnabled(false);
            city1.setText(last_city);

        }


        //определяем кнопку save и присваиваем ей обработчик
        final Button save = findViewById(R.id.btn_save_route);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //получаем текст из edit
                String scity1 = city1.getText().toString();
                String scity2 = city2.getText().toString();
                String sdate1 = date1.getText().toString();
                String sdate2 = date2.getText().toString();
                String stime1 = time1.getText().toString();
                String stime2 = time2.getText().toString();
                String splace1 = place1.getText().toString();
                String splace2 = place2.getText().toString();

                Log.d("MyLog", sdate1);
                Log.d("MyLog", sdate2);
                Log.d("MyLog", stime1);
                Log.d("MyLog", stime2);

                boolean checkCity1 = citiesList.contains(scity1);
                boolean checkCity2 = citiesList.contains(scity2);
                Log.d("MyLog", String.valueOf(checkCity1));
                Log.d("MyLog", String.valueOf(checkCity2));

                int size = routeList.size();
                String str_size = String.valueOf(size);

                if (scity1.equals("")||scity2.equals("")||sdate1.equals("")||sdate2.equals("")||
                stime1.equals("")||stime2.equals("")) {
                    Snackbar.make(view, "Please, enter cities, dates and time of departure and arrival", Snackbar.LENGTH_LONG)
                            .show();
                }
                else if (scity1.equals(scity2)){
                    Snackbar.make(view, "Departure city and arrival city can't be the same", Snackbar.LENGTH_LONG)
                            .show();
                }
                else if (!checkCity1) {
                    Snackbar.make(view, "Select departure city from list", Snackbar.LENGTH_LONG)
                            .show();
                    city1.setText("");
                }
                else if (!checkCity2) {
                    Snackbar.make(view, "Select arrival city from list", Snackbar.LENGTH_LONG)
                            .show();
                    city2.setText("");
                }
                else {
                    int correctDates = checkInternalCorrectDates(sdate1, sdate2);
                    switch (correctDates) {
                        case (0):
                            if (size > 0) {
                                boolean correctExt = checkExternalCorrectTimes(sdate1, stime1);
                                if (correctExt) {

                                    try {
                                        insertNotes(scity1, scity2, sdate1, sdate2, size);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    db.insertRouteDetails(scity1, scity2, sdate1, sdate2, stime1, stime2, splace1, splace2, str_size);
                                    db.insertRouteNotes(str_size, "");
                                    intent = new Intent(NewRouteActivity.this, RouteActivity.class);
                                    startActivity(intent);
                                }
                            }
                            else {

                                try {
                                    insertNotes(scity1, scity2, sdate1, sdate2, size);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                db.insertRouteDetails(scity1, scity2, sdate1, sdate2, stime1, stime2, splace1, splace2, str_size);
                                db.insertRouteNotes(str_size, "");
                                intent = new Intent(NewRouteActivity.this, RouteActivity.class);
                                startActivity(intent);
                            }
                            break;
                        case (1):
                            int correctTime = checkInternalCorrectTimes(stime1, stime2);
                            if (correctTime == 0) {
                                if (size > 0) {
                                    boolean correctExt2 = checkExternalCorrectTimes(sdate1, stime1);
                                    if (correctExt2) {
                                        try {
                                            insertNotes(scity1, scity2, sdate1, sdate2, size);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        db.insertRouteDetails(scity1, scity2, sdate1, sdate2, stime1, stime2, splace1, splace2, str_size);
                                        db.insertRouteNotes(str_size, "");
                                        intent = new Intent(NewRouteActivity.this, RouteActivity.class);
                                        startActivity(intent);
                                    }
                                }
                                else {
                                    try {
                                        insertNotes(scity1, scity2, sdate1, sdate2, size);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    db.insertRouteDetails(scity1, scity2, sdate1, sdate2, stime1, stime2, splace1, splace2, str_size);
                                    db.insertRouteNotes(str_size, "");
                                    intent = new Intent(NewRouteActivity.this, RouteActivity.class);
                                    startActivity(intent);
                                }
                            }
                            else {
                                Snackbar.make(view, "Your departure time must be before arrival time", Snackbar.LENGTH_LONG)
                                        .show();
                            }
                            break;
                        case (2):
                            Snackbar.make(view, "Your departure day must be before arrival day", Snackbar.LENGTH_LONG)
                                    .show();

                    }
                }

            }
        });
    }



    //выбор даты
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
                            date1.setText(editTextDateParam);
                        }
                        else {
                            date2.setText(editTextDateParam);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    //выбор времени
    private void callTimePicker(final int flag) {
        // получаем текущее время
        final Calendar cal = Calendar.getInstance();
        mHour = cal.get(Calendar.HOUR_OF_DAY);
        mMinute = cal.get(Calendar.MINUTE);

        // инициализируем диалог выбора времени текущими значениями
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String editTextTimeParam = hourOfDay + " : " + minute;
                        if (flag == 1) {
                            h1 = hourOfDay;
                            m1 = minute;
                            time1.setText(editTextTimeParam);
                        }
                        else {
                            h2 = hourOfDay;
                            m2 = minute;
                            time2.setText(editTextTimeParam);
                        }
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    //проверка дат на корректность
    public int checkInternalCorrectDates(String date1, String date2) {
        int res = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
             Date str_date1 = sdf.parse(date1);
             Date str_date2 = sdf.parse(date2);
            //некорректные даты
            if (str_date1.after(str_date2)) {
                res = 2;
            }
            //одинаковые даты
            if (date1.equals(date2)) {
                res = 1;
            }
            //корректные даты
            if (str_date1.before(str_date2)) {
                res = 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    //проверка времени на корректность
    public int checkInternalCorrectTimes(String time1, String time2) {
        int res = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("HH : mm");
        try {
            Date str_time1 = sdf.parse(time1);
            Date str_time2 = sdf.parse(time2);
            //некорректное время
            if (str_time1.after(str_time2) || str_time1.equals(str_time2)) {
                res = 2;
            }
            //корректное время
            if (str_time1.before(str_time2)) {
                res = 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    //проверка на состыковку с предыдущим пунктом
    public boolean checkExternalCorrectTimes(String now_date, String now_time) {
        boolean res = true;
        DBHelper db = new DBHelper(NewRouteActivity.this);
        ArrayList<HashMap<String, String>> routeList = db.GetRouteDetails();
        HashMap<String, String> hm = routeList.get(routeList.size() - 1);
        String last_date = hm.get("date2");
        String last_time = hm.get("time2");
        String last_city = hm.get("city2");
        int check1 = checkInternalCorrectDates(last_date, now_date);
        switch (check1) {
            case(0):
                //correct dates
                res = true;
                break;
            case(1):
                //equals dates
                int check2= checkInternalCorrectTimes(last_time, now_time);
                if (check2 == 0) {
                    //correct time
                    res = true;
                }
                else {
                    //not correct time
                    res = false;
                    String msg = "Your departure time must be after arrival time in " + last_city + " (" +
                            last_date + ", " + last_time + ")";
                    Toast toast = Toast.makeText(getApplicationContext(),
                            msg, Toast.LENGTH_LONG);
                    toast.show();
                }
                break;
            case(2):
                res = false;
                String msg = "Your departure day must be after arrival day in " + last_city + " (" +
                        last_date + ", " + last_time + ")";
                Toast toast = Toast.makeText(getApplicationContext(),
                        msg, Toast.LENGTH_LONG);
                toast.show();
        }
        return res;
    }

    public void insertNotes(String city1, String city2, String date1, String date2, int size) throws ParseException {
        DBHelper db = new DBHelper(NewRouteActivity.this);
            date1 = transformDate(date1);
            date2 = transformDate(date2);

        //первая запись в маршруте
        if (size == 0) {
            db.insertDayNotes(city1, date1, "");
            db.insertDayNotes(city2, date2, "");
        }
        //запись в машруте 1+
        else {
            ArrayList<HashMap<String, String>> routeList = db.GetRouteDetails();
            HashMap<String, String> hm = routeList.get(routeList.size() - 1);
            String last_date = hm.get("date2");
            last_date = transformDate(last_date);
            String tmp_date = last_date;
            while (!tmp_date.equals(date1)) {
                tmp_date = getNextDay(tmp_date);
                long check = db.checkExistNote(city1, tmp_date);
                if (check == -1) {
                    db.insertDayNotes(city1, tmp_date, "");
                }
            }
            long check1 = db.checkExistNote(city2, date2);
            if (check1 == -1) {
                db.insertDayNotes(city2, date2, "");
            }
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



}