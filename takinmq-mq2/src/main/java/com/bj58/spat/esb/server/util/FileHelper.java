package com.bj58.spat.esb.server.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {
    public static List<String> getUniqueLibPath(String... dirs) throws IOException {
        List<String> jarList = new ArrayList<String>();
        List<String> fileNameList = new ArrayList<String>();

        for (String dir : dirs) {
            List<File> fileList = FileHelper.getFiles(dir, "rar", "jar", "war", "ear");
            if (fileList != null) {
                for (File file : fileList) {
                    if (!fileNameList.contains(file.getName())) {
                        jarList.add(file.getCanonicalPath());
                        fileNameList.add(file.getName());
                    }
                }
            }
        }

        return jarList;
    }

    public static List<File> getFiles(String dir, String... extension) {
        File f = new File(dir);
        if (!f.isDirectory()) {
            return null;
        }

        List<File> fileList = new ArrayList<File>();
        getFiles(f, fileList, extension);

        return fileList;
    }

    private static void getFiles(File f, List<File> fileList, String... extension) {
        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                getFiles(files[i], fileList, extension);
            } else if (files[i].isFile()) {
                String fileName = files[i].getName().toLowerCase();
                boolean isAdd = false;
                if (extension != null) {
                    for (String ext : extension) {
                        if (fileName.lastIndexOf(ext) == fileName.length() - ext.length()) {
                            isAdd = true;
                            break;
                        }
                    }
                }

                if (isAdd) {
                    fileList.add(files[i]);
                }
            }
        }
    }
}
