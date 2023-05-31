package org.beru.coltecnuspazserver.ui.model;

import android.os.Build;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jcraft.jsch.SftpProgressMonitor;

import org.beru.coltecnuspazserver.ui.controller.FirstFragment;

public class ProgressMonitor implements SftpProgressMonitor {
    private ProgressBar progressBar;
    public ProgressMonitor(ProgressBar progressBar){
        this.progressBar = progressBar;
    }
    @Override
    public void init(int op, String src, String dest, long max) {
        FirstFragment.instance.getActivity().runOnUiThread(() -> {
            FirstFragment.instance.back.setEnabled(false);
            progressBar.setMax((int)max);
            progressBar.setProgress(0);
        });
    }
    int progress;
    @Override
    public boolean count(long count) {
        progress += (int) count;
        FirstFragment.instance.getActivity().runOnUiThread(() -> {
            progressBar.setProgress(progress);
        });
        return true;
    }

    @Override
    public void end() {
        FirstFragment.instance.getActivity().runOnUiThread(() -> {
            progressBar.setProgress(0);
            FirstFragment.instance.back.setEnabled(true);
            Toast.makeText(FirstFragment.instance.getContext(), "Successful", Toast.LENGTH_SHORT).show();
            ConnectorSSH.instance.reconnect();
            FirstFragment.instance.updateView();
        });
    }
}
