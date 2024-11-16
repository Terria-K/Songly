package com.teuria.songly.models;

import com.teuria.songly.notation.NotationModel;
import com.teuria.songly.notation.SimpleNotation;


public class ExampleModel implements NotationModel {
    private String name;
    private String section;
    private int numbers;
    private int id;
    private static int idIncrementor;
    
    public ExampleModel(String name, String section, int numbers) {
        this.name = name;
        this.section = section;
        this.numbers = numbers;
        this.id = idIncrementor++;
    }
    
    public ExampleModel() {}
    
    @Override
    public SimpleNotation write() {
        SimpleNotation notation = new SimpleNotation(id+"");
        
        notation.add("name", name);
        notation.add("section", section);
        notation.add("numbers", numbers);
        return notation;
    }

    @Override
    public void read(SimpleNotation reader) {
        this.id = Integer.parseInt(reader.getSectionName());
        this.name = reader.get("name");
        this.section = reader.get("section");
        this.numbers = reader.getInt("numbers");
    }
}
