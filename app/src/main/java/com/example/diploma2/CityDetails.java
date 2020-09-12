package com.example.diploma2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.commons.io.IOUtils;





import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class CityDetails extends Activity {
    TextView city;
    final static String API_URL = "nominatim.openstreetmap.org";
    Button btn_look_at_map;
    Button btn_find_hotel;
    Button btn_tours;
    Button btn_attractions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_details);


        //устанавливаем название города
        city = (TextView) findViewById(R.id.city);
        final Bundle arguments = getIntent().getExtras();
        final String name_city = arguments.getString("city");
        final String pos1 = arguments.getString("pos1");
        city.setText(name_city);

        //установка кнопок
        btn_find_hotel = (Button) findViewById(R.id.find_hotel);
        btn_look_at_map = (Button) findViewById(R.id.look_at_map);
        btn_tours = (Button) findViewById(R.id.tours);
        btn_attractions = (Button) findViewById(R.id.attractions);

        //установка списка дат
        //
        //
        //
        //

        //получаем тип города
        final String type_city = arguments.getString("type");
        Log.d("MyLog type city", type_city);
        //если это город начала или конца, то дата одна
        if (type_city.equals("start") || type_city.equals("end")) {
            //скроем кнопку find_hotel
            btn_find_hotel.setVisibility(View.GONE);


            final ArrayList<HashMap<String, String>> days = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> hm = new HashMap<String, String>();
            String from_date = arguments.getString("from_date");
            //преобразуем дату в нормальный вид
            try {
                from_date = transformDate(from_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            hm.put("from_date", from_date);
            days.add(hm);
            ListView lv = (ListView) findViewById(R.id.list_days);
            ListAdapter adapter = new SimpleAdapter(CityDetails.this, days, R.layout.item_day,
                    new String[]{"from_date"},
                    new int[]{R.id.day});
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    HashMap<String, String> hm = days.get(position);
                    String day = hm.get("from_date");
                    Intent intent = new Intent(CityDetails.this, NotesDay.class);
                    intent.putExtra("day", day);
                    intent.putExtra("city", name_city);
                    String pos2 = String.valueOf(position);
                    intent.putExtra("pos1", pos1);
                    intent.putExtra("pos2", pos2);
                    startActivity(intent);

                }
            });

        }
        //если город начала и конца совпадают
        if (type_city.equals("start_and_end")) {
            //скроем кнопку find_hotel
            btn_find_hotel.setVisibility(View.GONE);

            final ArrayList<HashMap<String, String>> days = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> hm = new HashMap<String, String>();
            //положим дату начала
            String from_date = arguments.getString("from_date");
            //преобразуем дату
            try {
                from_date = transformDate(from_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            hm.put("date", from_date);
            days.add(hm);
            //положим дату конца
            hm = new HashMap<String, String>();
            String to_date = arguments.getString("to_date");
            //преобразуем дату
            try {
                to_date = transformDate(to_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (!to_date.equals(from_date)) {
                hm.put("date", to_date);
                days.add(hm);
            }


            //создадим адаптер
            ListView lv = (ListView) findViewById(R.id.list_days);
            ListAdapter adapter = new SimpleAdapter(CityDetails.this, days, R.layout.item_day,
                    new String[]{"date"},
                    new int[]{R.id.day});
            lv.setAdapter(adapter);
            //присваиваем обработчик списку
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    HashMap<String, String> hm = days.get(position);
                    String day = hm.get("date");
                    Intent intent = new Intent(CityDetails.this, NotesDay.class);
                    intent.putExtra("day", day);
                    intent.putExtra("city", name_city);
                    String pos2 = String.valueOf(position);
                    intent.putExtra("pos1", pos1);
                    intent.putExtra("pos2", pos2);
                    startActivity(intent);

                }
            });


        }
        //промежуточный город
        if (type_city.equals("middle")) {
            final ArrayList<HashMap<String, String>> days = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> hm;
            String from_date = arguments.getString("from_date");
            String to_date = arguments.getString("to_date");
            //преобразуем даты
            try {
                from_date = transformDate(from_date);
                to_date = transformDate(to_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            hm = new HashMap<String, String>();
            hm.put("date", from_date);
            days.add(hm);

            if (!from_date.equals(to_date)) {
                String day = from_date;
                while (!day.equals(to_date)) {
                    day = getNextDay(day);
                    hm = new HashMap<String, String>();
                    hm.put("date", day);
                    days.add(hm);
                }
            }

            ListView lv = (ListView) findViewById(R.id.list_days);
            ListAdapter adapter = new SimpleAdapter(CityDetails.this, days, R.layout.item_day,
                    new String[]{"date"},
                    new int[]{R.id.day});
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    HashMap<String, String> hm = days.get(position);
                    String day = hm.get("date");
                    Intent intent = new Intent(CityDetails.this, NotesDay.class);
                    intent.putExtra("day", day);
                    intent.putExtra("city", name_city);
                    String pos2 = String.valueOf(position);
                    intent.putExtra("pos1", pos1);
                    intent.putExtra("pos2", pos2);
                    startActivity(intent);

                }
            });

        }

        //
        //
        //
        //
        //
        //обработка кнопки look at map
        View.OnClickListener oclBtnMap = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8)
                {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    final Charset UTF_8 = Charset.forName("UTF-8");

                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("https")
                            .authority(API_URL)
                            .appendPath("search")
                            .appendQueryParameter("city", name_city)
                            .appendQueryParameter("format", "json");

                    try {
                        URL url = new URL(builder.toString());
                        String myUrl = builder.build().toString();
                        Log.d("MyLog_url", myUrl);
                        String page = IOUtils.toString(url, UTF_8);
                        JSONArray data = new JSONArray(page);
                        JSONObject firstItem = (JSONObject) data.get(0);
                        String lat = firstItem.get("lat").toString();
                        String lon = firstItem.get("lon").toString();
                        String geoposition = "geo:" + lat +"," + lon;

                        Intent intent;
                        intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Log.d("MyLog_coord", lat);
                        Log.d("MyLog_coord", lon);
                        intent.setData(Uri.parse(geoposition));
                        startActivity(intent);


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }
        };
        btn_look_at_map.setOnClickListener(oclBtnMap);



        //
        //
        //
        //
        //
        //
        //обработка кнопки find hotel
        View.OnClickListener oclBtnHotel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String from_date = arguments.getString("from_date");
                String to_date = arguments.getString("to_date");
                try {
                    from_date = transformDate(from_date);
                    to_date = transformDate(to_date);
                    from_date = transformDateForAPI(from_date);
                    to_date = transformDateForAPI(to_date);
                    Log.d("MyLog_date", from_date);
                    Log.d("MyLog_date", to_date);

                    String ref1 = "https://www.airbnb.ru/s/";
                    String ref2 = "/homes?tab_id=all_tab&refinement_paths%5B%5D=%2Fhomes&checkin=";
                    String ref3 = "&checkout=";
                    String ref4 = "&source=structured_search_input_header&search_type=search_query&_set_bev_on_new_domain=1590525256_8%2FrZ1fNdu%2Fu7BXbg";

                    String uri = ref1 + name_city + ref2 + from_date + ref3 + to_date + ref4;
                    Log.d("MyLog", uri);

                    Intent intent;
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        };
        btn_find_hotel.setOnClickListener(oclBtnHotel);

        //
        //
        //
        //
        // обработка кнопки tours
        View.OnClickListener oclBtnTours = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ref1 = "https://www.tripadvisor.com/Search?q=";
                String ref2 = "&searchSessionId=84D340037240537C3CC543F615FB715F1590686728012ssid&geo=187147&sid=3011FEEBF31F1A04F1062AACD0632FA41590686743197&blockRedirect=true&ssrc=ac&rf=9";
                String uri = ref1 + name_city + ref2;
                Intent intent;
                intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        };
        btn_tours.setOnClickListener(oclBtnTours);

        //
        //
        //
        //
        //обработка кнопки attractions
        View.OnClickListener oclBtnAttractions = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ref1 = "https://www.tripadvisor.com/Search?q=";
                String ref2 = "&searchSessionId=84D340037240537C3CC543F615FB715F1590686728012ssid&sid=3011FEEBF31F1A04F1062AACD0632FA41590687698497&blockRedirect=true&ssrc=A";
                String uri = ref1 + name_city + ref2;
                Intent intent;
                intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        };
        btn_attractions.setOnClickListener(oclBtnAttractions);



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

    public String transformDateForAPI(String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String res = "";
        try {
            Date str_date = format.parse(date);
            String day = (String) DateFormat.format("dd",  str_date);
            String month = (String) DateFormat.format("MM", str_date);
            String year = (String) DateFormat.format("yyyy", str_date);
            res = year + "-" + month + '-' + day;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }
}
