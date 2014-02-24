package org.n3r.elastic.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BufferedIOUtils {

    public static BufferedReader createFileReader(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) throw new FileNotFoundException();
        return new BufferedReader(new FileReader(file));
    }

    public static BufferedWriter createFileWriter(String filePath, boolean append) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            String parent = file.getParent();
            if (parent != null) new File(parent).mkdirs();
            file.createNewFile();
        }
        return new BufferedWriter(new FileWriter(file, append));
    }

}
