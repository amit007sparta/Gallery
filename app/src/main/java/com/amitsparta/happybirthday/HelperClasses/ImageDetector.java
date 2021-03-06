package com.amitsparta.happybirthday.HelperClasses;

import android.support.annotation.NonNull;

import com.amitsparta.happybirthday.DataFiles.Folder;
import com.amitsparta.happybirthday.DataFiles.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class ImageDetector {

    private ImageDetector() {
    }

    private HashSet<Folder> folderList;

    private static final String[] supportedExtensions = {"jpg", "jpeg", "png", "bmp", "JPG", "JPEG", "PNG", "BMP"};

    public static Boolean checkIfImage(@NonNull File file) {
        String filePath = file.getAbsolutePath();
        for (String extensions : supportedExtensions) {
            if (filePath.endsWith(extensions)) {
                if (file.length() > 50000) {
                    return true;
                } else {
                    return null;
                }
            }
        }
        return false;
    }

    public static ArrayList<Folder> collectImages(String fileName, int mode) {
        return collectImages(new File(fileName), mode);
    }

    public static ArrayList<Folder> collectImages(@NonNull File file, int mode) {

        ImageDetector detector = new ImageDetector();
        detector.folderList = new HashSet<>();
        if (mode == Folder.FOLDER_MODE) {
            detector.collectImages(file);
        } else if (mode == Image.IMAGE_MODE) {
            if (file.getAbsolutePath().equals(Folder.ABSOLUTE_FILE_PATH)) {
                detector.collectImagesOneGO(file);
            } else {
                detector.collectImages(file);
            }
        }
        ArrayList temp = new ArrayList<>(detector.folderList);
        Collections.sort(temp);
        return temp;
    }

    private void collectImagesOneGO(File file) {
        File internalFiles[] = file.listFiles();
        if (internalFiles == null || file.getName().equals("Android") || file.getName().equals(Folder.HIDDEN_FILE_NAME))
            return;
        for (File file1 : internalFiles) {
            Boolean isImage = ImageDetector.checkIfImage(file1);
            if (isImage == null) {
                continue;
            } else if (isImage) {
                addFolderAndImage(file1);
            }
        }
    }

    private void collectImages(@NonNull File file) {
        File internalFiles[] = file.listFiles();
        if (internalFiles == null || file.getName().equals("Android") || file.getName().equals(Folder.HIDDEN_FILE_NAME))
            return;
        for (File file1 : internalFiles) {
            Boolean isImage = ImageDetector.checkIfImage(file1);
            if (isImage == null) {
                continue;
            } else if (isImage) {
                addFolderAndImage(file1);
            } else {
                collectImages(file1);
            }
        }
    }

    private void addFolderAndImage(File file) {
        Folder folder = checkForFolder(file);
        if (folder == null) {
            folder = new Folder(file.getParent());
            folder.add(new Image(file));
            folderList.add(folder);
        } else {
            folder.add(new Image(file));
        }
    }

    private Folder checkForFolder(File file) {
        for (Folder folder : folderList) {
            if (folder.compareFolders(file.getParent())) {
                return folder;
            }
        }
        return null;
    }
}
