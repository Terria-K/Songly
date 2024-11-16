package com.teuria.songly.notation;

import java.util.HashMap;


public class SimpleNotation {
    private String sectionName;
    
    HashMap<String, String> values;
    
    public SimpleNotation(String sectionName) {
        this.sectionName = sectionName;
        values = new HashMap<>();
    }
    
    public void add(String key, Object value) {
        values.put(key, value.toString());
    }
    
    public int getInt(String key) {
        return Integer.parseInt(values.get(key));
    }
    
    public long getLong(String key) {
        return Long.parseLong(values.get(key));
    }
    
    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(values.get(key));
    }
    
    public String get(String key) {
        return values.get(key);
    }
    
    public int size() {
        return values.size();
    }
    
    public String getSectionName() {
        return this.sectionName;
    }
}
