package com.example.diploma2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;


public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "trip_db";
    //для таблицы детали путешествия
    private static final String TABLE_TRIP_DETAILS = "trip_details";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_START = "date_start";
    private static final String KEY_END = "date_end";
    //для таблицы детали маршрута
    private static final String TABLE_ROUTE_DETAILS = "route_details";
    private static final String KEY_POS = "pos";
    private static final String KEY_CITY1 = "city1";
    private static final String KEY_CITY2 = "city2";
    private static final String KEY_DATE1 = "date1";
    private static final String KEY_DATE2 = "date2";
    private static final String KEY_TIME1 = "time1";
    private static final String KEY_TIME2 = "time2";
    private static final String KEY_PLACE1 = "place1";
    private static final String KEY_PLACE2 = "place2";
    //для таблицы заметок маршрута
    private static final String TABLE_ROUTE_NOTES = "route_notes";
    private static final String KEY_NOTES = "notes";
    //для таблицы заметок дня
    private static final String TABLE_DAY_NOTES = "day_notes";
    private static final String KEY_CITY = "city";
    private static final String KEY_DAY = "day";
    private static final String KEY_HASH = "hash";




    public DBHelper(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        //создание таблицы деталей поездки
        String CREATE_TABLE_TRIP_DETAILS = "CREATE TABLE " + TABLE_TRIP_DETAILS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
                + KEY_START + " TEXT,"
                + KEY_END + " TEXT"+ ")";

        //создание таблицы деталей маршрута
        String CREATE_TABLE_ROUTE_DETAILS = "CREATE TABLE " + TABLE_ROUTE_DETAILS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CITY1 + " TEXT,"
                + KEY_CITY2 + " TEXT,"
                + KEY_DATE1 + " TEXT,"
                + KEY_DATE2 + " TEXT,"
                + KEY_TIME1 + " TEXT,"
                + KEY_TIME2 + " TEXT,"
                + KEY_PLACE1 + " TEXT,"
                + KEY_PLACE2 + " TEXT,"
                + KEY_POS + " TEXT" + ")";
        //создание таблицы заметок маршрута
        String CREATE_TABLE_ROUTE_NOTES = "CREATE TABLE " + TABLE_ROUTE_NOTES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_POS + " TEXT,"
                + KEY_NOTES + " TEXT" + ")";
        //создание таблицы заметок дня
        String CREATE_TABLE_DAY_NOTES = "CREATE TABLE " + TABLE_DAY_NOTES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CITY + " TEXT,"
                + KEY_DAY + " TEXT," + KEY_NOTES + " TEXT," +  KEY_HASH + " TEXT" + ")";


        db.execSQL(CREATE_TABLE_TRIP_DETAILS);
        db.execSQL(CREATE_TABLE_ROUTE_DETAILS);
        db.execSQL(CREATE_TABLE_ROUTE_NOTES);
        db.execSQL(CREATE_TABLE_DAY_NOTES);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTE_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAY_NOTES);
        // Create tables again
        onCreate(db);
    }
    // **** CRUD (Create, Read, Update, Delete) Operations ***** //

    // Adding new Trip Details
    void insertTripDetails(String name, String date_start, String date_end){
        //Get the Data Repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_NAME, name);
        cValues.put(KEY_START, date_start);
        cValues.put(KEY_END, date_end);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_TRIP_DETAILS,null, cValues);
        db.close();
    }


    //Adding new Route Details
    void insertRouteDetails(String city1, String city2, String date1, String date2, String time1, String time2,
    String place1, String place2, String pos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_CITY1, city1);
        cValues.put(KEY_CITY2, city2);
        cValues.put(KEY_DATE1, date1);
        cValues.put(KEY_DATE2, date2);
        cValues.put(KEY_TIME1, time1);
        cValues.put(KEY_TIME2, time2);
        cValues.put(KEY_PLACE1, place1);
        cValues.put(KEY_PLACE2, place2);
        cValues.put(KEY_POS, pos);
        long newRowId = db.insert(TABLE_ROUTE_DETAILS, null, cValues);
        db.close();
    }

    //Adding new Route Note
    void insertRouteNotes(String pos, String notes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_POS, pos);
        cValues.put(KEY_NOTES, notes);
        long newRowId = db.insert(TABLE_ROUTE_NOTES,null, cValues);
        db.close();
    }

    void insertDayNotes(String city, String day, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        String hash = city+day;
        cValues.put(KEY_CITY, city);
        cValues.put(KEY_DAY, day);
        cValues.put(KEY_NOTES, notes);
        cValues.put(KEY_HASH, hash);
        Log.d("MyLog", "New Day Note: city = " + city + " day = " + day + " hash = " + hash);
        long newRowId = db.insert(TABLE_DAY_NOTES,null, cValues);
        db.close();
    }



    // Get Trip Details
    public ArrayList<HashMap<String, String>> GetTripDetails(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> tripList = new ArrayList<>();
        String query = "SELECT name, date_start, date_end FROM "+ TABLE_TRIP_DETAILS;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> trip = new HashMap<>();
            trip.put("name",cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            trip.put("date_start",cursor.getString(cursor.getColumnIndex(KEY_START)));
            trip.put("date_end",cursor.getString(cursor.getColumnIndex(KEY_END)));
            tripList.add(trip);
        }
        return  tripList;
    }

    //Get Route Details
    public ArrayList<HashMap<String, String>> GetRouteDetails(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> routeList = new ArrayList<>();
        String query = "SELECT city1, city2, date1, date2, time1, time2, place1, place2, pos FROM "+ TABLE_ROUTE_DETAILS;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()) {
            HashMap<String, String> route = new HashMap<>();
            route.put("city1", cursor.getString(cursor.getColumnIndex(KEY_CITY1)));
            route.put("city2", cursor.getString(cursor.getColumnIndex(KEY_CITY2)));
            route.put("date1", cursor.getString(cursor.getColumnIndex(KEY_DATE1)));
            route.put("date2", cursor.getString(cursor.getColumnIndex(KEY_DATE2)));
            route.put("time1", cursor.getString(cursor.getColumnIndex(KEY_TIME1)));
            route.put("time2", cursor.getString(cursor.getColumnIndex(KEY_TIME2)));
            route.put("place1", cursor.getString(cursor.getColumnIndex(KEY_PLACE1)));
            route.put("place2", cursor.getString(cursor.getColumnIndex(KEY_PLACE2)));
            route.put("pos", cursor.getString(cursor.getColumnIndex(KEY_POS)));
            routeList.add(route);

        }
        return routeList;
    }

    //Get Route Notes
    public ArrayList<HashMap<String, String>> GetRouteNotes() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> notesList = new ArrayList<>();
        String query = "SELECT pos, notes FROM "+ TABLE_ROUTE_NOTES;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> notes = new HashMap<>();
            notes.put("pos",cursor.getString(cursor.getColumnIndex(KEY_POS)));
            notes.put("notes",cursor.getString(cursor.getColumnIndex(KEY_NOTES)));
            notesList.add(notes);
        }
        return  notesList;
    }


    public long GetMaxIDTrip(){
        SQLiteDatabase db = this.getWritableDatabase();
        long lastId = -1;
        String query = "SELECT * \n" +
                "    FROM    trip_details\n" +
                "    WHERE   id = (SELECT MAX(id)  FROM trip_details);";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor != null && cursor.moveToFirst()) {
            lastId = cursor.getLong(0);
        }
        return lastId;
    }


    //получить минимальный id из таблицы маршрута
    public long GetMinIDRoute(){
        SQLiteDatabase db = this.getWritableDatabase();
        long lastId = -1;
        String query = "SELECT * \n" +
                "    FROM    route_details\n" +
                "    WHERE   id = (SELECT MIN(id)  FROM route_details);";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor != null && cursor.moveToFirst()) {
            lastId = cursor.getLong(0);
        }
        return lastId;
    }

    //получить максимальный id из таблицы маршрута
    public long GetMaxIDRoute(){
        SQLiteDatabase db = this.getWritableDatabase();
        long lastId = -1;
        String query = "SELECT * \n" +
                "    FROM    route_details\n" +
                "    WHERE   id = (SELECT MAX(id)  FROM route_details);";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor != null && cursor.moveToFirst()) {
            lastId = cursor.getLong(0);
        }
        return lastId;
    }


    //
    public long checkExistNote(String city, String day) {
        SQLiteDatabase db = this.getWritableDatabase();
        long lastId = -1;
        String hash = city+day;
        Cursor cursor = db.query(TABLE_DAY_NOTES, new String[]{KEY_CITY, KEY_DAY}, KEY_HASH+ "=?",
                new String[]{hash},null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            lastId = cursor.getLong(0);
        }
        return lastId;
    }

    // Get Trip Details based on tripid
    public ArrayList<HashMap<String, String>> GetTripByTripId(long trip_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> tripList = new ArrayList<>();
        String query = "SELECT name, date_start, date_end FROM "+ TABLE_TRIP_DETAILS;
        Cursor cursor = db.query(TABLE_TRIP_DETAILS, new String[]{KEY_NAME, KEY_START, KEY_END}, KEY_ID+ "=?",
                new String[]{String.valueOf(trip_id)},null, null, null, null);
        if (cursor.moveToNext()){
            HashMap<String,String> trip = new HashMap<>();
            trip.put("name",cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            trip.put("date_start",cursor.getString(cursor.getColumnIndex(KEY_START)));
            trip.put("date_end",cursor.getString(cursor.getColumnIndex(KEY_END)));
            tripList.add(trip);
        }
        return  tripList;
    }



    //Get Route Details based on pos
    public  ArrayList<HashMap<String, String >> GetRouteByPos(String pos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> routeList = new ArrayList<>();
        String query = "SELECT city1, city2, date1, date2, time1, time2, place1, place2 FROM "+ TABLE_ROUTE_DETAILS;
        Cursor cursor = db.query(TABLE_ROUTE_DETAILS, new String[]{KEY_CITY1, KEY_CITY2, KEY_DATE1, KEY_DATE2,
                KEY_TIME1, KEY_TIME2, KEY_PLACE1, KEY_PLACE2}, KEY_POS + "=?", new String[]{pos}, null,
                null, null, null);
        if (cursor.moveToNext()) {
            HashMap<String, String> route = new HashMap<>();
            route.put("city1", cursor.getString(cursor.getColumnIndex(KEY_CITY1)));
            route.put("city2", cursor.getString(cursor.getColumnIndex(KEY_CITY2)));
            route.put("date1", cursor.getString(cursor.getColumnIndex(KEY_DATE1)));
            route.put("date2", cursor.getString(cursor.getColumnIndex(KEY_DATE2)));
            route.put("time1", cursor.getString(cursor.getColumnIndex(KEY_TIME1)));
            route.put("time2", cursor.getString(cursor.getColumnIndex(KEY_TIME2)));
            route.put("place1", cursor.getString(cursor.getColumnIndex(KEY_PLACE1)));
            route.put("place2", cursor.getString(cursor.getColumnIndex(KEY_PLACE2)));
            routeList.add(route);
        }
        return routeList;
    }

    // Get Route Notes by pos
    public ArrayList<HashMap<String, String>> GetRouteNotesByPos(String pos){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> notesList = new ArrayList<>();
        String query = "SELECT pos, notes FROM "+ TABLE_ROUTE_NOTES;
        Cursor cursor = db.query(TABLE_ROUTE_NOTES, new String[]{KEY_POS, KEY_NOTES}, KEY_POS+ "=?",
                new String[]{pos},null, null, null, null);
        if (cursor.moveToNext()){
            HashMap<String,String> notes = new HashMap<>();
            notes.put("pos",cursor.getString(cursor.getColumnIndex(KEY_POS)));
            notes.put("notes",cursor.getString(cursor.getColumnIndex(KEY_NOTES)));
            notesList.add(notes);
        }
        return  notesList;
    }

    public String GetDayNotesByHash(String hash) {
        SQLiteDatabase db = this.getWritableDatabase();
        String notes = "";
        Cursor cursor = db.query(TABLE_DAY_NOTES, new String[]{KEY_NOTES}, KEY_HASH+ "=?",
                new String[]{hash},null, null, null, null);
        if (cursor.moveToNext()){
            notes = cursor.getString(cursor.getColumnIndex(KEY_NOTES));
        }
        return notes;
    }


    // Delete Trip Details
    public void DeleteTrip(long trip_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRIP_DETAILS, KEY_ID+" = ?",new String[]{String.valueOf(trip_id)});
        db.close();
    }

    //Delete Route Details By ID
    public void DeleteRoute(long route_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ROUTE_DETAILS, KEY_ID+" = ?",new String[]{String.valueOf(route_id + 1)});
        db.close();
    }

    //Delete Route Details By Pos
    public void DeleteRouteByPos(String route_pos){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ROUTE_DETAILS, KEY_POS+ " = " + route_pos, null);
        db.close();
    }

    //Delete Notes By Pos
    public void DeleteNotesByPos(String pos){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ROUTE_NOTES, KEY_POS + " = " + pos, null);
        db.close();
    }

    public void DeleteDayNotesByHash(String hash) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DAY_NOTES, KEY_HASH + " = ?", new String[]{hash});
        db.close();
    }

    //Update Route Details By Pos
    public void UpdateRouteDetailsByPos(String city1, String city2, String date1, String date2, String time1, String time2,
                                        String place1, String place2, String pos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_CITY1, city1);
        cValues.put(KEY_CITY2, city2);
        cValues.put(KEY_DATE1, date1);
        cValues.put(KEY_DATE2, date2);
        cValues.put(KEY_TIME1, time1);
        cValues.put(KEY_TIME2, time2);
        cValues.put(KEY_PLACE1, place1);
        cValues.put(KEY_PLACE2, place2);
        cValues.put(KEY_POS, pos);
        int count = db.update(TABLE_ROUTE_DETAILS, cValues, KEY_POS + " = ?",new String[]{pos});
    }

    //Update Route Notes By Pos
    public void UpdateRouteNotes(String pos, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_POS, pos);
        cValues.put(KEY_NOTES, notes);
        int count  = db.update(TABLE_ROUTE_NOTES, cValues, KEY_POS + " = ?", new String[]{pos});
    }


    // Update Trip Details
    public int UpdateTripDetails(String date_start, String date_end, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put(KEY_START, date_start);
        cVals.put(KEY_END, date_end);
        int count = db.update(TABLE_TRIP_DETAILS, cVals, KEY_ID+" = ?",new String[]{String.valueOf(id)});
        return count;
    }

    public void UpdateDayNotes(String city, String day, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cVals = new ContentValues();
        String hash = city + day;
        cVals.put(KEY_CITY, city);
        cVals.put(KEY_DAY, day);
        cVals.put(KEY_NOTES, notes);
        int count = db.update(TABLE_DAY_NOTES, cVals, KEY_HASH + " = ?", new String[]{hash});
    }
}