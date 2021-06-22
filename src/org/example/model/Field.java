package org.example.model;

import org.springframework.data.annotation.Id;

import java.util.Arrays;

public class Field {
    @Id
    private String id;
    private int width;
    private int height;
    private Tile[][] field;

    public Field(int width, int height) {
        id = "game";
        this.width = width;
        this.height = height;
        field = new Tile[this.width][this.height];
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                field[x][y] = new Tile(x, y);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Tile getTile(int x, int y) {
        return field[x][y];
    }

    public void setTile(Tile tile) {
        field[tile.getX()][tile.getY()] = tile;
    }

    public void setTile(Tile tile, int x, int y) {
        field[x][y] = tile;
    }

    public Field getSubField(int minX, int maxX, int minY, int maxY) {
        int sizeX = maxX - minX + 1;
        int sizeY = maxY - minY + 1;
        Field subField = new Field(sizeX, sizeY);
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                Tile tile;
                if ((x + minX) < 0 || (y + minY) < 0 || (x + minX) > 89 || (y + minY) > 89) {
                    tile = null;
                } else {
                    tile = this.getTile(x + minX, y + minY);
                    tile.setX(x);
                    tile.setY(y);
                }
                subField.setTile(tile, x, y);
            }
        }
        return subField;
    }

    public void setSubField(int minX, int maxX, int minY, int maxY, Field subField) {
        int sizeX = maxX - minX + 1;
        int sizeY = maxY - minY + 1;

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                Tile tile = subField.getTile(x, y);
                tile.setX(x + minX);
                tile.setY(y + minY);
                this.setTile(tile);
            }
        }
    }

    public Tile[][] getField() {
        return field;
    }
}
