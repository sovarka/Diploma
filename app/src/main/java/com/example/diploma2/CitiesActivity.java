package com.example.diploma2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CitiesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities_main);
        TextView warn = findViewById(R.id.fill_out_the_route);
        DBHelper db = new DBHelper(this);
        ArrayList<HashMap<String, String>> routeList = db.GetRouteDetails();
        if (routeList.size() > 0) {
            warn.setVisibility(View.GONE);
        }
        if (routeList.size() == 1) {
            //будем вносить сюда hashmap с новыми значения
            final ArrayList<HashMap<String, String>> list1 = new ArrayList<HashMap<String, String>>();

            //город начала путешествия
            HashMap<String, String> hm1 = new HashMap<String, String>();
            String city1 = routeList.get(0).get("city1");
            String date1 = routeList.get(0).get("date1");
            String label1 = "You start your trip from this city";
            hm1.put("city", city1);
            hm1.put("from_date", date1);
            hm1.put("to_date", "");
            hm1.put("label", label1);
            hm1.put("divider", "");
            hm1.put("duration", "");
            list1.add(hm1);

            //город конца путешествия
            HashMap<String, String> hm2 = new HashMap<String, String>();
            String city2 = routeList.get(0).get("city2");
            String date2 = routeList.get(0).get("date2");
            String label2 = "You end your trip in this city";
            hm2.put("city", city2);
            hm2.put("from_date", date2);
            hm2.put("to_date", "");
            hm2.put("label", label2);
            hm2.put("divider", "");
            hm2.put("duration", "");
            list1.add(hm2);

            //создаем адаптер
            ListView lv = (ListView) findViewById(R.id.list_city1);
            ListAdapter adapter = new SimpleAdapter(CitiesActivity.this, list1, R.layout.item_city_main,
                    new String[]{"city", "from_date", "label", "divider", "duration"},
                    new int[]{R.id.name_of_city, R.id.from_date, R.id.label_city, R.id.divider_date, R.id.duration});
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Log.d("MyLog", "itemClick: position = " + position + ", id = "
                            + id);
                    Intent intent = new Intent(CitiesActivity.this, CityDetails.class);
                    //передаем город
                    intent.putExtra("city", list1.get(position).get("city"));
                    //передаем тип города
                    if (position == 0) {
                        intent.putExtra("type", "start");
                    }
                    else {
                        intent.putExtra("type", "end");
                    }
                    //передаем дату
                    String pos1 = String.valueOf(position);
                    intent.putExtra("pos1", pos1);
                    intent.putExtra("from_date", list1.get(position).get("from_date"));
                    startActivity(intent);
                }
            });
        }

        if (routeList.size() > 1) {
            //сюда будем добавлять hashmap
            final ArrayList<HashMap<String, String>> list1 = new ArrayList<HashMap<String, String>>();
            //hm1 - для первого города, hm - промежуточный, hm2 - последний
            HashMap<String, String> hm1 = new HashMap<String, String>();
            HashMap<String, String> hm2 = new HashMap<String, String>();
            final int size = routeList.size();
            //заполняем hm1 и hm2
            final String city1 = routeList.get(0).get("city1");
            final String city2 = routeList.get(size - 1).get("city2");
            String date1 = routeList.get(0).get("date1");
            String date2 = routeList.get(size - 1).get("date2");
            //если начинаем и заканчиваем в одном городе
            if (city1.equals(city2)) {
                 String label1 = "You start and end your trip in this city";
                 hm1.put("city", city1);
                 hm1.put("from_date", date1);
                 hm1.put("to_date", date2);
                 hm1.put("label", label1);
                 hm1.put("divider", "     /    ");
                 hm1.put("duration", "");
            }
            //города начала и конца разные
            else {
                String label1 = "You start your trip from this city";
                String label2 = "You end your trip in this city";

                hm1.put("city", city1);
                hm1.put("from_date", date1);
                hm1.put("to_date", "");
                hm1.put("label", label1);
                hm1.put("divider", "");
                hm1.put("duration", "");

                hm2.put("city", city2);
                hm2.put("from_date", date2);
                hm2.put("to_date", "");
                hm2.put("label", label2);
                hm2.put("divider", "");
                hm2.put("duration", "");
            }
            //добавляем первый элемент в лист
            list1.add(hm1);

            //заполним  промежуточные города
            for (int i = 0; i < size - 1; i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                String city = routeList.get(i).get("city2");
                String from_date = routeList.get(i).get("date2");
                String to_date = routeList.get(i + 1).get("date1");
                String label = "";
                String divider = "     —    ";
                String duration = "";

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date str_from = sdf.parse(from_date);
                    Date str_to = sdf.parse(to_date);
                    long delt = str_to.getTime() - str_from.getTime();
                    delt = delt / 86400000;
                    String res = String.valueOf(delt + 1);
                    Log.d("MyLog_duration", res);
                    duration = res + " days";
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                hm.put("city", city);
                hm.put("from_date", from_date);
                hm.put("to_date", to_date);
                hm.put("label", label);
                hm.put("divider", divider);
                hm.put("duration", duration);
                list1.add(hm);

            }

            //добавим последний элемент в лист
            if (!city1.equals(city2)) {
                list1.add(hm2);
            }
            //создаем адаптер
            ListView lv = (ListView) findViewById(R.id.list_city1);
            ListAdapter adapter = new SimpleAdapter(CitiesActivity.this, list1, R.layout.item_city_main,
                    new String[]{"city", "from_date", "to_date", "label", "divider", "duration"},
                    new int[]{R.id.name_of_city, R.id.from_date, R.id.to_date, R.id.label_city, R.id.divider_date, R.id.duration});
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    String st_id = String.valueOf(id);
                    String st_pos = String.valueOf(position);
                    Log.d("MyLogKKK", "id = " + st_id + "pos = " + st_pos);

                    String label1 = "You start your trip from this city";
                    String label2 = "You end your trip in this city";
                    Log.d("MyLog", "itemClick: position = " + position + ", id = "
                            + id);
                    Intent intent = new Intent(CitiesActivity.this, CityDetails.class);
                    //передаем город
                    intent.putExtra("city", list1.get(position).get("city"));
                    //передаем тип города
                    if (city1.equals(city2) && (position == 0)) {
                        intent.putExtra("type", "start_and_end");
                    }
                    else if (position == 0) {
                        intent.putExtra("type", "start");
                    }
                    else if (position == list1.size() - 1 && list1.get(position).get("label").equals(label2)) {
                        intent.putExtra("type", "end");
                    }
                    else {
                        intent.putExtra("type", "middle");
                    }
                    //передаем даты
                    String pos1 = String.valueOf(position);
                    intent.putExtra("pos1", pos1);
                    intent.putExtra("from_date", list1.get(position).get("from_date"));
                    intent.putExtra("to_date", list1.get(position).get("to_date"));
                    startActivity(intent);

                }
            });

        }
    }
}
