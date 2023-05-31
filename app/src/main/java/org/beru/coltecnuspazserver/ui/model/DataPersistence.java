package org.beru.coltecnuspazserver.ui.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class DataPersistence {
    private static SharedPreferences preferences;
    public static List<SessionDatas> load(Context context){
        List<SessionDatas> datas = new ArrayList<>();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        SessionDatas data = new SessionDatas(preferences.getString("name",""), preferences.getString("pass",""), preferences.getString("host",""), preferences.getInt("port",0));
        datas.add(data);

        return datas;
    }
    public static void save(Context context, String name, String host, int port, String pass){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name", name);
        editor.putString("host", host);
        editor.putInt("port", port);
        editor.putString("pass", Encrypter.encrypt(pass));
        editor.apply();
    }
}
