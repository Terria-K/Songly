package com.teuria.songly.models;

import com.teuria.songly.notation.NotationModel;
import com.teuria.songly.notation.SimpleNotation;
import java.util.Base64;


public class ExampleModel implements NotationModel {
    private String name;
    private String section;
    private int numbers;
    
    public ExampleModel(String name, String section, int numbers) {
        this.name = name;
        this.section = section;
        this.numbers = numbers;
    }
    
    public ExampleModel() {}
    
    @Override
    public SimpleNotation write() {
        SimpleNotation notation = new SimpleNotation(Base64.getEncoder().encodeToString((name+"").getBytes()));
        
        notation.add("name", name);
        notation.add("section", section);
        notation.add("numbers", numbers);
        return notation;
    }

    @Override
    public void read(SimpleNotation reader) {
        this.name = reader.get("name");
        this.section = reader.get("section");
        this.numbers = reader.getInt("numbers");
    }
}
