package org.beru.coltecnuspazserver.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import org.beru.coltecnuspazserver.R;
import org.beru.coltecnuspazserver.ui.controller.ClientActivity;

public class SavedSessions extends AppCompatActivity {

    public static SavedSessions instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_sessions);

        instance = this;
    }
    public void changeActivity(){
        Intent intent = new Intent(SavedSessions.this, ClientActivity.class);
        startActivity(intent);
    }
}