package org.beru.coltecnuspazserver.ui.model;

import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.jcraft.jsch.*;

import org.beru.coltecnuspazserver.R;
import org.beru.coltecnuspazserver.ui.controller.ClientActivity;
import org.beru.coltecnuspazserver.ui.controller.LoginActivity;
import org.beru.coltecnuspazserver.ui.controller.SecondFragment;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ConnectorSSH {

    public Session session;
    public ChannelSftp sftp;
    public static ConnectorSSH instance;

    public ProgressBar progressBar;
    public ConnectorSSH(String host, int port, String user, String pass) throws JSchException{

        session = new JSch().getSession(user, host, port);
        session.setPassword(pass);
        session.setConfig("PreferredAuthentications", "password");
        session.setConfig("StrictHostKeyChecking", "no");

        instance = this;
    }
    public boolean connect() throws Exception{
            Thread t1 = new Thread(() -> {
                try {
                    session.connect();
                } catch (JSchException e) {
                    e.printStackTrace();
                }
            });
            Thread t2 = new Thread(() ->{
                try{
                    sftp.connect();

                    Datas.setPaths(new LinkedList<>());
                    Datas.getPaths().add(sftp.getHome());
                    LoginActivity.instance.updateView();
                }catch (JSchException e){
                    e.printStackTrace();
                } catch (SftpException e) {
                    throw new RuntimeException(e);
                }
            });
            t1.start();t1.join();
            sftp = (ChannelSftp) session.openChannel("sftp");
            t2.start();t2.join();

            return sftp.isConnected();
    }
    public void reconnect(){
        try {
            sftp = (ChannelSftp) session.openChannel("sftp");
        } catch (JSchException e) {
            e.printStackTrace();
        }
        Thread t = new Thread(() -> {
            try {
                sftp.connect();
            } catch (JSchException e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void createFolders(){
        Thread t1 = new Thread(() -> {
            try {
                String[] grades = ClientActivity.instance.getResources().getStringArray(R.array.grades);
                for(String grade : grades){
                    sftp.mkdir(sftp.getHome()+"/"+grade);
                    sftp.mkdir(sftp.getHome()+"/"+grade+"/tareas");
                }
            } catch (SftpException e) {
                e.printStackTrace();
            }
        });
        t1.start();
    }
    public Deque<ChannelSftp.LsEntry> getFiles(){
        Deque<ChannelSftp.LsEntry> filesInfo = new LinkedList<>();
        Thread t1 = new Thread(() -> {
            ChannelSftp.LsEntrySelector selector = new ChannelSftp.LsEntrySelector() {
                @Override
                public int select(ChannelSftp.LsEntry entry) {
                    final String fileName = entry.getFilename();
                    if(fileName.equals(".") || fileName.equals(".."))
                        return CONTINUE;
                    else if(entry.getAttrs().isDir())
                        filesInfo.addFirst(entry);
                    else
                        filesInfo.add(entry);
                    return CONTINUE;
                }
            };
            try {
                sftp.ls(Datas.getLastPath(), selector);
            } catch (SftpException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return filesInfo;
    }
    public void sendFile(File file){
        Thread t1 = new Thread(() -> {
            try {
                System.out.println(Datas.getLastPath()+"/"+file.getName());
                sftp.put(file.getPath(), Datas.getLastPath()+"/"+file.getName(), new ProgressMonitor(progressBar));
            } catch (SftpException e) {
                e.printStackTrace();
            }
        });
        t1.start();
    }
    public void sendFiles(List<File> files, String grade, SecondFragment fragment){
        Thread t1 = new Thread(() -> {
            try {
                for(File file : files){
                    sftp.put(file.getPath(), sftp.getHome()+"/"+grade+"/tareas/"+file.getName(), new ProgressMonitor(fragment.progressBar));
                }
                fragment.getActivity().runOnUiThread(() -> {
                    fragment.clearList();
                });
            } catch (SftpException e) {
                e.printStackTrace();
            }
        });
        t1.start();
    }
    public void createFolder(String name){
        Thread t1 = new Thread(() -> {
            try {
                sftp.mkdir(Datas.getLastPath()+"/"+name);
            } catch (SftpException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void downloadFile(String name, String dst){
        Thread t1 = new Thread(() -> {
            try {
                sftp.get(Datas.getLastPath()+"/"+name, dst, new ProgressMonitor(progressBar));
            } catch (SftpException e) {
                e.printStackTrace();
            }
        });
        t1.start();
    }
    public void rename(String oldName, String newName){
        Thread t1 = new Thread(() -> {
            try {
                sftp.rename(Datas.getLastPath()+"/"+oldName, Datas.getLastPath()+"/"+newName);
            } catch (SftpException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void deleteFile(String name){
        Thread t1 = new Thread(() -> {
            try {
                sftp.rm(Datas.getLastPath()+"/"+name);
            } catch (SftpException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void deleteFolder(String name) {
        Thread t1 = new Thread(() -> {
            try {
                sftp.rmdir(Datas.getLastPath()+"/"+name);
            } catch (SftpException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
