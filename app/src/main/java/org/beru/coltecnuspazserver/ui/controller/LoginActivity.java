package org.beru.coltecnuspazserver.ui.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.os.Bundle;

import org.beru.coltecnuspazserver.R;
import org.beru.coltecnuspazserver.ui.model.ConnectorSSH;
import org.beru.coltecnuspazserver.ui.model.DataPersistence;
import org.beru.coltecnuspazserver.ui.model.Encrypter;
import org.beru.coltecnuspazserver.ui.model.SessionDatas;
import org.beru.coltecnuspazserver.ui.view.SavedSessionsAdapter;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    ListView listView;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        instance = this;

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        final Button connection_btn = findViewById(R.id.connect);
        final EditText user = findViewById(R.id.user);
        final EditText pass = findViewById(R.id.pass);
        final EditText host = findViewById(R.id.host);
        final EditText port = findViewById(R.id.port);

        listView = (ListView) findViewById(R.id.taskList);

        List<SessionDatas> datas = DataPersistence.load(getApplicationContext());

        SavedSessionsAdapter adapter = new SavedSessionsAdapter(getApplicationContext(), datas);

        if(preferences.getBoolean("saved",false))
            listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SessionDatas data = datas.get(i);
                System.out.println(data.getHost() + ": " + Encrypter.decrypt(data.getPass()));
                try {
                    connect(data.getName(), Encrypter.decrypt(data.getPass()), data.getHost(), data.getPort());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        connection_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String u = user.getText().toString();
                    String ps = pass.getText().toString();
                    String h = host.getText().toString();
                    int p = Integer.parseInt(port.getText().toString());
                    if(!connect(u,ps,h,p))
                        Toast.makeText(getApplicationContext(), "Error al conectarse con el servidor", Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public static LoginActivity instance;
    String u;
    String ps;
    String h;
    int p;
    public boolean connect(String u, String ps, String h, int p) throws Exception {
        this.u = u;
        this.ps = ps;
        this.h = h;
        this.p = p;
        return new ConnectorSSH(h,p,u,ps).connect();
    }
    public void updateView(){
        preferences.edit().putBoolean("saved", true).apply();
        DataPersistence.save(getApplicationContext(), u,h,p, ps);
        Intent intent = new Intent(LoginActivity.this, ClientActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}