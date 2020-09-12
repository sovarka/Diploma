package com.example.diploma2;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FindTicketsActivity extends Activity {

    EditText date_fl_dep;
    EditText date_fl_ret;
    Button find_tickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_tickets);

        //находим view по id
        final AutoCompleteTextView autoCompleteTextViewFrom = findViewById(R.id.autoCompleteTextViewFrom);
        final AutoCompleteTextView autoCompleteTextViewTo = findViewById(R.id.autoCompleteTextViewTo);
        date_fl_dep = findViewById(R.id.date_flight_depart);
        date_fl_ret = findViewById(R.id.date_flight_return);
        final EditText passen = findViewById(R.id.amount_passengers);
        find_tickets = findViewById(R.id.btn_find);

        //делаем даты кликабельными
        date_fl_dep.setEnabled(true);
        date_fl_dep.setTextIsSelectable(true);
        date_fl_dep.setFocusable(false);
        date_fl_dep.setFocusableInTouchMode(false);
        date_fl_ret.setEnabled(true);
        date_fl_ret.setTextIsSelectable(true);
        date_fl_ret.setFocusable(false);
        date_fl_ret.setFocusableInTouchMode(false);

        View.OnClickListener oclDateFlDep = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //открываем календарь
                callDatePicker(1);
            }
        };
        date_fl_dep.setOnClickListener(oclDateFlDep);

        View.OnClickListener oclDateFlRet = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //открываем календарь
                callDatePicker(2);
            }
        };
        date_fl_ret.setOnClickListener(oclDateFlRet);



        //получение списка аэропортов
        String[] airports = getResources().getStringArray(R.array.airports_array);
        List<String> airList = Arrays.asList(airports);
        //получение списка кодов
        String[] codeIATA = getResources().getStringArray(R.array.IATA_array);
        List<String> codeList = Arrays.asList(codeIATA);

        //создание пар аэропорт-код
        final HashMap<String, String> pairs = new HashMap<String, String>();
        for (int i = 0; i < airList.size(); i++) {
            pairs.put(airList.get(i), codeList.get(i));
        }


        //выпадающий список
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, airList);
        autoCompleteTextViewFrom.setAdapter(adapter);
        autoCompleteTextViewTo.setAdapter(adapter);

        View.OnClickListener oclBtnFind = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String d1 = date_fl_dep.getText().toString();
                String d2 = date_fl_ret.getText().toString();
                String city1 = autoCompleteTextViewFrom.getText().toString();
                String city2 = autoCompleteTextViewTo.getText().toString();
                String pass = passen.getText().toString();

                boolean checkCorrDates = checkCorrectDates(d1, d2);
                boolean checkFirDate = checkFirstDate(d1);
                boolean checkCorrCities = checkCorrectCities(city1, city2);
                boolean checkCorrPass = checkCorrectPassengers(pass);
                if (!d2.equals("")) {
                    if (!checkCorrDates) {
                        Snackbar.make(find_tickets, "Error: Return date must be after departure date ", Snackbar.LENGTH_LONG)
                                .show();
                    }
                }

                if (!checkFirDate) {
                    Snackbar.make(find_tickets, "Enter departure date", Snackbar.LENGTH_LONG)
                            .show();
                }

                if (!checkCorrCities) {
                    Snackbar.make(find_tickets, "Enter departure and arrival places", Snackbar.LENGTH_LONG)
                            .show();
                }

                if (!checkCorrPass) {
                    Snackbar.make(find_tickets, "Enter number of passengers", Snackbar.LENGTH_LONG)
                            .show();
                }



                if (checkCorrDates && checkFirDate && checkCorrCities && checkCorrPass) {
                    //ищем билеты в гугле
                    String ref = "https://www.aviasales.ru/search/";
                    if (d2.equals("")) {
                        String trans_date1 = transofrmDate(d1);
                        String code1 = pairs.get(city1);
                        String code2 = pairs.get(city2);
                        String search = code1 + trans_date1 + code2 + pass;
                        String uri = ref + search;
                        Log.d("MyLog", uri);
                        Intent intent;
                        intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                    }
                    else {
                        String trans_date1 = transofrmDate(d1);
                        String trans_date2 = transofrmDate(d2);
                        String code1 = pairs.get(city1);
                        String code2 = pairs.get(city2);
                        String search = code1 + trans_date1 + code2 + trans_date2 + pass;
                        String uri = ref + search;
                        Log.d("MyLog", uri);
                        Intent intent;
                        intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                    }

                }
            }
        };
        find_tickets.setOnClickListener(oclBtnFind);


    }

    private void callDatePicker(final int flag) {
        // получаем текущую дату
        final Calendar cal = Calendar.getInstance();
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);

        // инициализируем диалог выбора даты текущими значениями
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String editTextDateParam = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                        if (flag == 1) {
                            date_fl_dep.setText(editTextDateParam);
                        }
                        else {
                            date_fl_ret.setText(editTextDateParam);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private boolean checkCorrectDates(final String date1, final String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        boolean res = true;
        try {
            Date str_date1 = sdf.parse(date1);
            Date str_date2 = sdf.parse(date2);
            if (str_date1.after(str_date2)) {
                res = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    private boolean checkFirstDate(final String date1) {
        boolean res = true;
        if (date1.equals("")) {
            res = false;
        }
        return res;
    }

    private boolean checkCorrectCities(final String city1, final String city2) {
        boolean res = true;
        if (city1.equals("") || city2.equals("")) {
            res = false;
        }
        return res;
    }

    private boolean checkCorrectPassengers(final String pass) {
        boolean res = true;
        if (pass.equals("")) {
            res = false;
        }
        return res;
    }



    private String transofrmDate(final String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String res = "";
        try {
            Date str_date = format.parse(date);
            String day = (String) DateFormat.format("dd",  str_date);
            String month = (String) DateFormat.format("MM", str_date);
            res = day + month;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

}