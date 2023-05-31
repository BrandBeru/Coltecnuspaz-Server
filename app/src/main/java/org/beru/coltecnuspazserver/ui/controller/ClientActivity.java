package org.beru.coltecnuspazserver.ui.controller;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.usage.ExternalStorageStats;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import org.beru.coltecnuspazserver.R;
import org.beru.coltecnuspazserver.databinding.ActivityClientBinding;
import org.beru.coltecnuspazserver.ui.model.ActionsChooser;
import org.beru.coltecnuspazserver.ui.model.ConnectorSSH;
import org.beru.coltecnuspazserver.ui.model.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

public class ClientActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityClientBinding binding;

    public static ClientActivity instance;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        binding = ActivityClientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_client);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser(actionsChooser);
            }
        });
    }
    public ActionsChooser actionsChooser;
    @Override
    public boolean onCreateOptionsMenu(@NonNull @NotNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.files_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    FirstFragment.instance.grantStoragePermission();
                    if(result.getResultCode()==RESULT_OK){
                        Intent data = result.getData();
                        System.out.println("Path?: " + data.getData().getPath());
                        String[] abs = data.getData().getPath().split(":");
                        String dir = abs[0];
                        String path = abs[1];
                        StorageManager storage = (StorageManager) getApplicationContext().getSystemService(Context.STORAGE_SERVICE);
                        System.out.println(data.getData().getPath());
                        if(dir.contains("primary")) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                                actionsChooser.action(storage.getPrimaryStorageVolume().getDirectory().getAbsolutePath() + "/" + path);
                            else
                                actionsChooser.action(Environment.getRootDirectory().getPath() + "/" + path);
                        }else{
                            actionsChooser.action(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path);
                        }
                    }
                }
            }
    );
    private static final int FILE_SELECT_CODE = 0;
    public void showFileChooser(ActionsChooser actionsChooser){
        this.actionsChooser = actionsChooser;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent = Intent.createChooser(intent, "Selecciona un archivo");
        activityResultLauncher.launch(intent);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.create:
                createFolder();
                break;
            case R.id.hiden:
                hideFolder();
                break;
            case R.id.close:
                close();
                break;
            default:
                Toast.makeText(getApplicationContext(), "Unrecognized", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
    private void createFolder(){
        createAlert("Crear Carpeta", "Nombre:");
    }
    public void fun(String name){
        ConnectorSSH.instance.createFolder(name);
        FirstFragment.instance.updateView();
    }
    private void hideFolder(){
        Toast.makeText(getApplicationContext(), "downloadFile", Toast.LENGTH_SHORT).show();
    }
    private void close(){
        Intent intent = new Intent(ClientActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_client);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void createAlert(String title, String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(FirstFragment.instance.getContext());
        dialog.setTitle(title);
        dialog.setMessage(message);

        final EditText text = new EditText(FirstFragment.instance.getContext());
        text.setInputType(InputType.TYPE_CLASS_TEXT);
        dialog.setView(text);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = text.getText().toString();
                fun(name);
                return;
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        dialog.show();
    }
}