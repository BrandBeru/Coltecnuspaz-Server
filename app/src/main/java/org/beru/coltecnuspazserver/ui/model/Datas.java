package org.beru.coltecnuspazserver.ui.model;

import com.jcraft.jsch.SftpException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Datas {
    private static List<String> paths;

    public static int path_id;
    public static String getLastPath(){
        return getPaths().get(getPaths().size()-1);
    }
    public static void addPath(String newPath){
        getPaths().add(getPaths().get(getPaths().size()-1)+"/"+newPath);
    }
    public static List<String> getPaths() {
        return paths;
    }
    public static void setPaths(List<String> paths) {
        Datas.paths = paths;
    }
}
