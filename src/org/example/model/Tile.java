package org.example.model;

import lombok.Data;

@Data
public class Tile extends GameObject {
    private Integer owner;
    private Integer possibleOwner;
    private Integer currentPlayer;
    private String currentPlayerColor;

    public Tile(int x, int y) {
        this.setX(x);
        this.setY(y);
        this.setColor("#ffffff");
        owner = possibleOwner = currentPlayer = null;
        currentPlayerColor = "#ffffff";
    }
}
