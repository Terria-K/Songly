package com.teuria.songly.notation;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SimpleNotationReader {
    private final HashMap<String, Integer> sections;
    private final ArrayList<SimpleNotation> notations;
    
    public SimpleNotationReader() {
        sections = new HashMap<>();
        notations = new ArrayList<>();
    }
    
    public void loadFile(String path) {
        try {
            FileReader reader = new FileReader(path);
            read(reader);
            
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
    
    private void read(FileReader reader) throws IOException {
        int i;
        boolean sectionOpened = false;
        boolean keyOpened = false;
        String currentKey = "";
        boolean valueOpened = false;
        int currentNotation = 0;
        StringBuilder builder = new StringBuilder();
        while ((i = reader.read()) != -1) {
            char c = (char)i;
            
            if (sectionOpened) {
                if (c == ']') {
                    sectionOpened = false;
                    currentNotation = sections.size();
                    String section = builder.toString();
                    sections.put(section, currentNotation);
                    notations.add(new SimpleNotation(section));
                    builder = new StringBuilder();
                    continue;
                }
                builder.append(c);
                continue;
            }
            
            
            if (Character.isWhitespace(c)) {
                if (valueOpened) {
                    valueOpened = false;
                    String value = builder.toString();
                    notations.get(currentNotation).add(currentKey, value);
                    builder = new StringBuilder();
                    continue;
                }
                continue;
            }
            
            if (valueOpened) {
                builder.append(c);
                continue;
            }
            
            if (keyOpened) {
                if (c == '=') {
                    valueOpened = true;
                    keyOpened = false;
                    currentKey = builder.toString();
                    builder = new StringBuilder();
                    continue;
                }
                builder.append(c);
                continue;
            }
            
            if (Character.isLetterOrDigit(c)) {
                keyOpened = true;
                builder.append(c);
                continue;
            }
            
            if (c == '[') {
                sectionOpened = true;
                continue;
            }
        }
    }
    
    public ArrayList<SimpleNotation> getAll() {
        return notations;
    }
    
    public SimpleNotation get(String key) {
        return notations.get(sections.get(key));
    }
    
    public SimpleNotation get(int index) {
        return notations.get(index);
    }
    
    public int getLength() {
        return sections.size();
    }
}
