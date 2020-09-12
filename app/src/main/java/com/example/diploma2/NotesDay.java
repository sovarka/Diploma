package com.example.diploma2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NotesDay extends Activity {
    EditText editNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_day);

        TextView textTitle = findViewById(R.id.title_notes);
        editNotes = findViewById(R.id.editNotesDay);

        final Bundle arguments = getIntent().getExtras();
        String city = arguments.getString("city");
        String day = arguments.getString("day");
        String pos1 = arguments.getString("pos1");
        String pos2 = arguments.getString("pos2");


        String title = city + ": " + day;
        textTitle.setText(title);

        DBHelper db = new DBHelper(this);
        String hash = city+day;
        String notes = db.GetDayNotesByHash(hash);
        editNotes.setText(notes);
    }

    @Override
    public void onBackPressed() {
        String save_notes = editNotes.getText().toString();
        DBHelper db = new DBHelper(NotesDay.this);
        final Bundle arg = getIntent().getExtras();
        String city = arg.getString("city");
        String day = arg.getString("day");
        db.UpdateDayNotes(city, day, save_notes);
        super.onBackPressed();

    }

}