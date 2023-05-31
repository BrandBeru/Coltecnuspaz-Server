package org.beru.coltecnuspazserver.ui.controller;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.view.*;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.beru.coltecnuspazserver.R;
import org.beru.coltecnuspazserver.databinding.FragmentFirstBinding;
import org.beru.coltecnuspazserver.ui.model.ConnectorSSH;
import org.beru.coltecnuspazserver.ui.model.Datas;
import org.beru.coltecnuspazserver.ui.model.Utils;
import org.beru.coltecnuspazserver.ui.view.FilesInfoAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    ListView viewPane;
    View view;
    FilesInfoAdapter adapter;
    public ProgressBar progressBar;
    public ImageButton back;

    public List<String> filesSelected = new ArrayList<>();

    public static FirstFragment instance;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        adapter = new FilesInfoAdapter(getContext(), ConnectorSSH.instance.getFiles());
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        instance = this;
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        this.view = view;

        progressBar = view.findViewById(R.id.progress);
        back = view.findViewById(R.id.back);

        ConnectorSSH.instance.progressBar = progressBar;

        binding.console.setOnClickListener(view1 -> NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment));
        updateView();
        binding.update.setOnClickListener(view1 -> {
            updateView();
        });

        ClientActivity.instance.actionsChooser = (location) -> {
            File f = new File(location);
            try {
                ConnectorSSH.instance.sendFile(f);
            }catch (Exception e){
                Toast.makeText(ClientActivity.instance.getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Datas.getPaths().size() > 1){
                    Datas.getPaths().remove(Datas.getPaths().size()-1);
                    updateView();
                }
            }
        });

        viewPane.setOnItemClickListener((parent, view12, position, id) -> {
            if(adapter.isDir(position))
                updateFiles(position);
            else
                downloadFile(adapter.getItem(position).getFilename());
            System.out.println("id: "+Datas.path_id);
            Datas.path_id++;
        });
    }
    private void updateFiles(int id){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Opciones de directorio");
        alert.setMessage("Nombre del archivo: "+adapter.getItem(id).getFilename());
        alert.setPositiveButton("Open", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Datas.addPath(adapter.getItem(id).getFilename());
                updateView();
            }
        });
        alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ConnectorSSH.instance.deleteFolder(adapter.getItem(id).getFilename());
                Toast.makeText(getContext(), "Carpeta eliminada", Toast.LENGTH_SHORT).show();
                updateView();
            }
        });
        alert.show();
    }
    public void downloadFile(String name){
        AlertDialog.Builder dialog = new AlertDialog.Builder(FirstFragment.instance.getContext());
        dialog.setTitle("Opciones de archivos");
        dialog.setMessage("Nombre del archivo: "+name);

        String[] actions = {"Download", "Rename", "Delete"};
        RadioGroup rg = new RadioGroup(getContext());
        for(int i = 0; i < actions.length; i++){
            RadioButton rb = new RadioButton(getContext());
            rb.setText(actions[i]);
            rb.setId(i);
            rg.addView(rb);
        }
        dialog.setView(rg);


        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int selected = rg.getCheckedRadioButtonId();
                if (selected==0) {
                    try{
                        grantStoragePermission();
                        StorageManager storage = (StorageManager) getContext().getSystemService(Context.STORAGE_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            ConnectorSSH.instance.downloadFile(name, storage.getPrimaryStorageVolume().getDirectory().getAbsolutePath() + "/Download/" + name);
                        } else
                            ConnectorSSH.instance.downloadFile(name, Environment.getExternalStorageDirectory().getPath() + "/Download/" + name);
                        Toast.makeText(getContext(), "Downloaded successfully", Toast.LENGTH_SHORT).show();
                    }catch(Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else if(selected==1){
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Nombre del archivo");
                    alert.setMessage("Nombre:");

                    final EditText text = new EditText(getContext());
                    text.setText(name);
                    alert.setView(text);

                    alert.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ConnectorSSH.instance.rename(name, text.getText().toString());
                            Toast.makeText(getContext(), "Rename successfully", Toast.LENGTH_SHORT).show();
                            updateView();
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });
                    alert.show();
                }else if(selected==2){
                    ConnectorSSH.instance.deleteFile(name);
                    System.out.println();
                    Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                    updateView();
                }else{
                    Toast.makeText(getContext(), "Nada por hacer", Toast.LENGTH_SHORT).show();
                }
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
    public void updateView(){
        binding.path.setText(Datas.getLastPath());
        adapter = new FilesInfoAdapter(getContext(), ConnectorSSH.instance.getFiles());
        viewPane = view.findViewById(R.id.taskList);
        viewPane.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        grantStoragePermission();
    }
    public void grantStoragePermission(){
        if(!Utils.isPermissionGranted(getContext()))
        {
            new AlertDialog.Builder(getContext())
                    .setTitle("All files permission")
                    .setMessage("Due to android 11 restrictions, this app requires all files permission")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            takePermission();
                        }
                    })
                    .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void takePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 101);
            }
        }else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            },101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
            if(requestCode==10){
                boolean readExt = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if(!readExt)
                    takePermission();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}