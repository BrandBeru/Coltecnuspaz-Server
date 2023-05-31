package org.beru.coltecnuspazserver.ui.model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.beru.coltecnuspazserver.ui.controller.ClientActivity;

import java.io.File;

public class Utils {
    public static boolean isPermissionGranted(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            return Environment.isExternalStorageManager();
        else{
            int readExtStorage = ContextCompat.checkSelfPermission( context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            );
            return readExtStorage == PackageManager.PERMISSION_GRANTED;
        }
    }
}
