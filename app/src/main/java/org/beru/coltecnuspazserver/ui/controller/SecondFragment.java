package org.beru.coltecnuspazserver.ui.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import org.beru.coltecnuspazserver.R;
import org.beru.coltecnuspazserver.databinding.FragmentSecondBinding;
import org.beru.coltecnuspazserver.ui.model.ActionsChooser;
import org.beru.coltecnuspazserver.ui.model.ConnectorSSH;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    private LinearLayout taskList;
    public ProgressBar progressBar;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public List<File> selectedFiles= new ArrayList<>();

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        taskList = view.findViewById(R.id.taskList);
        progressBar = view.findViewById(R.id.progressBar);

        ConnectorSSH.instance.progressBar = progressBar;

//        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(SecondFragment.this)
//                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
//            }
//        });
        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.grades));
        gradeAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        binding.grade.setAdapter(gradeAdapter);

        ClientActivity.instance.actionsChooser = (location) -> {
            File f = new File(location);
            selectedFiles.add(f);
            TextView file = new TextView(getContext());
            file.setText(f.getAbsolutePath());
            Space space = new Space(getContext());
            System.out.println("View???");
            taskList.addView(file);
            taskList.addView(space);
        };
        binding.createfolders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ConnectorSSH.instance.createFolders();
                    Toast.makeText(ClientActivity.instance.getApplicationContext(), "Carpetas asignadas correctamente", Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.uploadAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(selectedFiles.isEmpty()){
                        Toast.makeText(getContext(), R.string.empty, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    binding.uploadAll.setActivated(false);
                    ConnectorSSH.instance.sendFiles(selectedFiles,binding.grade.getSelectedItem().toString(), SecondFragment.this);
                    Toast.makeText(ClientActivity.instance.getApplicationContext(), "Archivos enviados correctamente", Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearList();
            }
        });

    }
    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.removeItem(R.id.create);
        menu.removeItem(R.id.hiden);
    }

    public void clearList(){
        selectedFiles.clear();
        taskList.removeAllViews();
        taskList.removeAllViewsInLayout();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}