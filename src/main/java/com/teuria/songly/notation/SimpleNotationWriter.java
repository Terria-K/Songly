package com.teuria.songly.notation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class SimpleNotationWriter {
    private final StringBuilder builder;
    
    public SimpleNotationWriter() {
        builder = new StringBuilder();
    }
    
    // I use generics for optimization
    // This will devirtualized function call at compile-time
    public <T> void write(SimpleNotation notation) {
        builder.append("[")
                .append(notation.getSectionName())
                .append("]\n");
        Set<Map.Entry<String, String>> sets = notation.values.entrySet();
        for (Map.Entry<String, String> e : sets) {
            builder.append(e.getKey())
                .append("=")
                .append(e.getValue())
                .append('\n');
        }
    }
    
    public void save(String path) {
        try {   
            FileWriter writer = new FileWriter(path);
            writer.write(builder.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occured. File might not be writeable");
        }
    }
}
