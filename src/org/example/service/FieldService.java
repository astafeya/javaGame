package org.example.service;

import org.example.model.*;
import org.example.repository.FieldRepository;
import org.example.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FieldService {
    @Autowired
    private FieldRepository fieldRepository;
    @Autowired
    private PlayerRepository playerRepository;

    public int getWidth() {
        Field field = fieldRepository.findById("game").orElse(null);
        if (field == null) return 0;
        return field.getWidth();
    }

    public int getHeight() {
        Field field = fieldRepository.findById("game").orElse(null);
        if (field == null) return 0;
        return field.getHeight();
    }

    public Tile getTile(int x, int y) {
        Field field = fieldRepository.findById("game").orElse(null);
        if (field == null) {
            createField();
            field = fieldRepository.findById("game").orElse(null);
        }
        Tile tile = field.getTile(x, y);
        return tile;
    }

    public void setTile(Tile tile) {
        Field field = fieldRepository.findById("game").orElse(null);
        if (field == null) return;
        field.setTile(tile);
        fieldRepository.save(field);
    }

    public boolean isTileFree(int x, int y) {
        Tile tile = getTile(x, y);
        if (tile.getOwner() == null && tile.getPossibleOwner() == null)
            return true;
        else return false;
    }

    public Integer getCurrentPlayer(int x, int y) {
        Tile tile = getTile(x, y);
        return tile.getCurrentPlayer();
    }

    public void setCurrentPlayer(int x, int y, Integer playerID) {
        String color;
        if (playerID != null) {
            Player player = playerRepository.findById(playerID).orElse(null);
            if (player == null) return;
            color = player.getColor();
        } else {
            color = "#ffffff";
        }
        Tile tile = getTile(x, y);
        tile.setCurrentPlayerColor(color);
        tile.setCurrentPlayer(playerID);
        setTile(tile);
    }

    public void createField() {
        Field field = new Field(90, 90);
        fieldRepository.insert(field);
    }

    public void deleteField() {
        Field field = fieldRepository.findById("game").orElse(null);
        if (field == null) return;
        fieldRepository.delete(field);
    }

    public Field getSubField(int minX, int maxX, int minY, int maxY) {
        Field field = fieldRepository.findById("game").orElse(null);
        if (field == null) return null;
        Field subField = field.getSubField(minX, maxX, minY, maxY);
        return subField;
    }

    public void setSubField(int minX, int maxX, int minY, int maxY, Field subField) {
        Field field = fieldRepository.findById("game").orElse(null);
        if (field == null) return;
        field.setSubField(minX, maxX, minY, maxY, subField);
        fieldRepository.save(field);
    }

    public void cleanLand(Integer playerID) {
        Player player = playerRepository.findById(playerID).orElse(null);
        if (player == null) return;
        int minX = Math.min(player.getTailMinX(), Math.min(player.getLandMinX(), player.getX())),
            maxX = Math.max(player.getTailMaxX(), Math.max(player.getLandMaxX(), player.getX())),
            minY = Math.min(player.getTailMinY(), Math.min(player.getLandMinY(), player.getY())),
            maxY = Math.max(player.getTailMaxY(), Math.max(player.getLandMaxY(), player.getY())),
            sizeX = maxX - minX + 1,
            sizeY = maxY - minY + 1;
        Field subField = getSubField(minX, maxX, minY, maxY);
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Tile tile = subField.getTile(x, y);
                if (tile.getOwner() != null && tile.getOwner().equals(playerID)
                        || tile.getPossibleOwner() != null && tile.getPossibleOwner().equals(playerID)) {
                    if (tile.getOwner() != null && tile.getOwner().equals(playerID)) tile.setOwner(null);
                    else tile.setPossibleOwner(null);
                    tile = mixColor(tile);
                }
                if (tile.getCurrentPlayer() != null && tile.getCurrentPlayer().equals(playerID)) {
                    tile.setCurrentPlayer(null);
                    tile.setCurrentPlayerColor("#ffffff");
                }
                subField.setTile(tile);
            }
        }
        setSubField(minX, maxX, minY, maxY, subField);
    }

    public Tile mixColor(Tile tile) {
        String owner = "#ffffff",
                possibleOwner = "#ffffff";
        if (tile.getOwner() != null) {
            Player player = playerRepository.findById(tile.getOwner()).orElse(null);
            if (player != null)
                owner = player.getColor();
        }
        if (tile.getPossibleOwner() != null) {
            Player player = playerRepository.findById(tile.getPossibleOwner()).orElse(null);
            if (player != null)
                possibleOwner = player.getColor();
        }
        if (tile.getOwner() != null && tile.getPossibleOwner() == null) {
            tile.setColor(owner);
        } else {
            int[] ownerRGB = getRGB(owner);
            int[] possibleOwnerRGB = getRGB(possibleOwner);
            int r = (ownerRGB[0] + possibleOwnerRGB[0]) / 2;
            int g = (ownerRGB[1] + possibleOwnerRGB[1]) / 2;
            int b = (ownerRGB[2] + possibleOwnerRGB[2]) / 2;
            tile.setColor(String.format("#%02x%02x%02x", r, g, b));
        }
        return tile;
    }

    private int[] getRGB(String srgb) {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = Integer.parseInt(srgb.substring(1 + i * 2, 1 + i * 2 + 2), 16);
        }
        return rgb;
    }
}
