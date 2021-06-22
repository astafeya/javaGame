package org.example.model;

import lombok.Data;

@Data
public class GameObject {
    private int x;
    private int y;
    private String color;

    public GameObject() {
        color = "#ffffff";
    }
}
