package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.*;
import org.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class GameService {
    private static Logger log = LogManager.getLogger(GameService.class.getName());
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private FieldService fieldService;
    @Autowired
    private DeadRepository deadRepository;
    @Autowired
    private ChangeDirectionRepository changeDirectionRepository;
    @Autowired
    private FillNewLandRepository fillNewLandRepository;

    public void createGame() {
        fieldService.createField();
    }

    public void deleteGame() {
        fieldService.deleteField();
    }

    public Integer start() {
        Player player = createPlayer();
        if (playerRepository.findAll().size() == 1) {
            update();
        }
        return player.getId();
    }

    public void update() {
        Thread run = new Thread(new Runnable() {
            @Override
            public void run() {
                while (playerRepository.findAll().size() > 0) {
                    try {
                        Thread.sleep(250);
                        moveAll();
                        deadList();
                        changeDirectionList();
                        fillNewLandList();
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
            }
        });
        run.start();
    }

    //------------------Player------------------

    public Player createPlayer() {
        int x, y, playerID;

        String color = generateRandomColor();
        do {
            x = 1 + (int)(Math.random() * 88);
            y = 1 + (int)(Math.random() * 88);
            log.info("Create player: randomX = " + x + ", randomY = " + y);
        } while (!freeForNewPlayer(x, y));

        Player player = new Player();
        player.setX(x);
        player.setY(y);
        player.setColor(color);

        player.setDirection(Direction.UP);

        player.setTailMinX(x);
        player.setTailMaxX(x);
        player.setTailMinY(y);
        player.setTailMaxY(y);

        player.setLandMinX(x - 1);
        player.setLandMaxX(x + 1);
        player.setLandMinY(y - 1);
        player.setLandMaxY(y + 1);

        playerID = getNewID();
        player.setId(playerID);
        playerRepository.insert(player);


        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                Tile tile = fieldService.getTile(i, j);
                if (i == x && j == y) {
                    tile.setCurrentPlayer(playerID);
                    tile.setCurrentPlayerColor(player.getColor());
                }
                tile.setOwner(playerID);
                tile = fieldService.mixColor(tile);
                fieldService.setTile(tile);
            }
        }

        return player;
    }

    public Integer getNewID() {
        Integer id;
        List<Player> players = playerRepository.findAll();
        if (players.size() > 0) {
            id = players.get(players.size() - 1).getId() + 1;
        } else {
            id = 1;
        }
        return id;
    }

    private String generateRandomColor() {
        Random random = new Random();
        int red = random.nextInt(256),
                green = random.nextInt(256),
                blue = random.nextInt(256);
        return String.format("#%02x%02x%02x", red, green, blue);
    }

    private boolean freeForNewPlayer(int x, int y) {
        boolean isFree = true;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (!fieldService.isTileFree(i, j)) {
                    isFree = false;
                }
            }
        }
        return isFree;
    }

    public void moveAll() {
        List<Player> players = playerRepository.findAll();
        for (int i = 0; i < players.size(); i++) {
            move(players.get(i));
            playerRepository.save(players.get(i));
        }
    }

    public void move(Player player) {
        Integer playerID = player.getId();
        log.info("move: player = " + playerID + ", direction = " +player.getDirection() +", x = " + player.getX() + ", y = " + player.getY());
        boolean onLand = isOnLand(playerID);
        Integer c = fieldService.getCurrentPlayer(player.getX(), player.getY());
        if (c != null && c.equals(playerID)) {
            fieldService.setCurrentPlayer(player.getX(), player.getY(), null);
        }
        switch (player.getDirection()) {
            case UP:
                int newY1 = player.getY() - 1;
                if (newY1 >= 0) {
                    player.setY(newY1);
                    Integer cur = fieldService.getCurrentPlayer(player.getX(), player.getY());
                    if (cur != null && cur < playerID) {
                        deadRepository.insert(new DeadListItem(cur));
                        deadRepository.insert(new DeadListItem(playerID));
                        break;
                    }
                    fieldService.setCurrentPlayer(player.getX(), player.getY(), playerID);
                    break;
                }
                deadRepository.insert(new DeadListItem(player.getId()));
                return;
            case DOWN:
                int newY2 = player.getY() + 1;
                if (newY2 < 90) {
                    player.setY(newY2);
                    Integer cur = fieldService.getCurrentPlayer(player.getX(), player.getY());
                    if (cur != null && cur < playerID) {
                        deadRepository.insert(new DeadListItem(cur));
                        deadRepository.insert(new DeadListItem(playerID));
                        break;
                    }
                    fieldService.setCurrentPlayer(player.getX(), player.getY(), playerID);
                    break;
                }
                deadRepository.insert(new DeadListItem(player.getId()));
                return;
            case LEFT:
                int newX1 = player.getX() - 1;
                if (newX1 >= 0) {
                    player.setX(newX1);
                    Integer cur = fieldService.getCurrentPlayer(player.getX(), player.getY());
                    if (cur != null && cur < playerID) {
                        deadRepository.insert(new DeadListItem(cur));
                        deadRepository.insert(new DeadListItem(playerID));
                        break;
                    }
                    fieldService.setCurrentPlayer(player.getX(), player.getY(), playerID);
                    break;
                }
                deadRepository.insert(new DeadListItem(player.getId()));
                return;
            case RIGHT:
                int newX2 = player.getX() + 1;
                if (newX2 < 90) {
                    player.setX(newX2);
                    Integer cur = fieldService.getCurrentPlayer(player.getX(), player.getY());
                    if (cur != null && cur < playerID) {
                        deadRepository.insert(new DeadListItem(cur));
                        deadRepository.insert(new DeadListItem(playerID));
                        break;
                    }
                    fieldService.setCurrentPlayer(player.getX(), player.getY(), playerID);
                    break;
                }
                deadRepository.insert(new DeadListItem(player.getId()));
                return;
            default: break;
        }
        playerRepository.save(player);
        Tile tile = fieldService.getTile(player.getX(), player.getY());
        if (tile.getPossibleOwner() != null && !tile.getPossibleOwner().equals(playerID)) {
            Player anotherPlayer = playerRepository.findById(tile.getPossibleOwner()).orElse(null);
            if (anotherPlayer != null) {
                deadRepository.insert(new DeadListItem(anotherPlayer.getId()));
            }
        } else if (tile.getPossibleOwner() != null && tile.getPossibleOwner().equals(playerID)) {
            deadRepository.insert(new DeadListItem(player.getId()));
        }
        if (!isOnLand(playerID)) {
            tile.setPossibleOwner(playerID);
            tile = fieldService.mixColor(tile);
            fieldService.setTile(tile);
        }
        if (onLand && isOnLand(playerID)){
            player.setTailMaxX(player.getX());
            player.setTailMinX(player.getX());
            player.setTailMaxY(player.getY());
            player.setTailMinY(player.getY());
        } else if (isOnLand(playerID) && !onLand){
            fillNewLandRepository.insert(new FillNewLandListItem(playerID));
        }
        log.info("move: player = " + playerID + ", x = " + player.getX() + ", y = " + player.getY());
        playerRepository.save(player);
    }

    public void changeDirection(Integer playerID, Direction direction) {
        changeDirectionRepository.insert(new ChangeDirectionListItem(playerID, direction));
    }

    public void changeDirectionList() {
        List<ChangeDirectionListItem> items = changeDirectionRepository.findAll();
        for (int i = 0; i < items.size(); i++) {
            Player player = playerRepository.findById(items.get(i).getPlayerID()).orElse(null);
            if (player != null) {
                Integer playerID = player.getId();
                Direction newDirection = items.get(i).getNewDirection();
                switch (newDirection) {
                    case UP:
                        if (player.getDirection() == Direction.DOWN) return;
                        if (!isOnLand(playerID)) {
                            if (player.getDirection() == Direction.LEFT) {
                                if (player.getTailMinX() > player.getX())
                                    player.setTailMinX(player.getX());
                            } else if (player.getDirection() == Direction.RIGHT) {
                                if (player.getTailMaxX() < player.getX())
                                    player.setTailMaxX(player.getX());
                            }
                        }
                        break;
                    case DOWN:
                        if (player.getDirection() == Direction.UP) return;
                        if (!isOnLand(playerID)) {
                            if (player.getDirection() == Direction.LEFT) {
                                if (player.getTailMinX() > player.getX())
                                    player.setTailMinX(player.getX());
                            } else if (player.getDirection() == Direction.RIGHT) {
                                if (player.getTailMaxX() < player.getX())
                                    player.setTailMaxX(player.getX());
                            }
                        }
                        break;
                    case LEFT:
                        if (player.getDirection() == Direction.RIGHT) return;
                        if (!isOnLand(playerID)) {
                            if (player.getDirection() == Direction.UP) {
                                if (player.getTailMinY() > player.getY())
                                    player.setTailMinY(player.getY());
                            } else if (player.getDirection() == Direction.DOWN) {
                                if (player.getTailMaxY() < player.getY())
                                    player.setTailMaxY(player.getY());
                            }
                        }
                        break;
                    case RIGHT:
                        if (player.getDirection() == Direction.LEFT) return;
                        if (!isOnLand(playerID)) {
                            if (player.getDirection() == Direction.UP) {
                                if (player.getTailMinY() > player.getY())
                                    player.setTailMinY(player.getY());
                            } else if (player.getDirection() == Direction.DOWN) {
                                if (player.getTailMaxY() < player.getY())
                                    player.setTailMaxY(player.getY());
                            }
                        }
                        break;
                    default: break;
                }
                player.setDirection(newDirection);
                playerRepository.save(player);
            }
            changeDirectionRepository.delete(items.get(i));
        }
    }

    public boolean isOnLand(Integer playerID) {
        Player player = playerRepository.findById(playerID).orElse(null);
        if (player == null) return false;
        Tile tile = fieldService.getTile(player.getX(), player.getY());
        if (tile.getOwner() != null && tile.getOwner().equals(playerID))
            return true;
        else
            return false;
    }

    public void fillNewLandList() {
        List<FillNewLandListItem> items = fillNewLandRepository.findAll();
        for (int i = 0; i < items.size(); i++) {
            Player player = playerRepository.findById(items.get(i).getPlayerID()).orElse(null);
            if (player != null) {
                fillingNewLand(player.getId());
            }
            fillNewLandRepository.delete(items.get(i));
        }
    }

    public void deadList() {
        List<DeadListItem> items = deadRepository.findAll();
        for (int i = 0; i < items.size(); i++) {
            Player player = playerRepository.findById(items.get(i).getPlayerID()).orElse(null);
            if (player != null) {
                fieldService.cleanLand(player.getId());
                playerRepository.delete(player);
            }
            deadRepository.delete(items.get(i));
        }
    }

    //------------------Field------------------

    public void fillingNewLand(Integer playerID) {
        Player player = playerRepository.findById(playerID).orElse(null);
        if (player == null) return;
        int minX = (player.getTailMinX() - 1 >= 0)? player.getTailMinX() - 1 : player.getTailMinX(),
            maxX = (player.getTailMaxX() + 1 < fieldService.getWidth())? player.getTailMaxX() + 1 : player.getTailMaxX(),
            minY = (player.getTailMinY() - 1 >= 0)? player.getTailMinY() - 1 : player.getTailMinY(),
            maxY = (player.getTailMaxY() + 1 < fieldService.getHeight())? player.getTailMaxY() + 1 : player.getTailMaxY(),
            sizeX = maxX - minX + 1,
            sizeY = maxY - minY + 1;
        player.setTailMinX(minX);
        player.setTailMaxX(maxX);
        player.setTailMinY(minY);
        player.setTailMaxY(maxY);
        playerRepository.save(player);
        Field subField = fieldService.getSubField(minX, maxX, minY, maxY);
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Tile tile = subField.getTile(x, y);
                if (tile.getPossibleOwner()!= null && tile.getPossibleOwner().equals(playerID)) {
                    tile.setOwner(playerID);
                    tile.setPossibleOwner(null);
                    tile = fieldService.mixColor(tile);
                    subField.setTile(tile);
                }
            }
        }
        int x = sizeX/2, y = sizeY/2;
        boolean in = false;
        do {
            Tile tile = subField.getTile(x, y);
            if (tile.getOwner() == null || tile.getOwner() != null && !tile.getOwner().equals(playerID)) {
                boolean left = false, right = false, up = false, down = false;
                for (int i = x - 1; i >= 0; i--) {
                    Tile iTile = subField.getTile(i, y);
                    if (iTile.getOwner() != null && iTile.getOwner().equals(playerID)) {
                        left = true;
                        break;
                    }
                }
                for (int i = x + 1; i < sizeX; i++) {
                    Tile iTile = subField.getTile(i, y);
                    if (iTile.getOwner() != null && iTile.getOwner().equals(playerID)) {
                        right = true;
                        break;
                    }
                }
                for (int i = y - 1; i >= 0; i--) {
                    Tile iTile = subField.getTile(x, i);
                    if (iTile.getOwner() != null && iTile.getOwner().equals(playerID)) {
                        up = true;
                        break;
                    }
                }
                for (int i = y + 1; i < sizeY; i++) {
                    Tile iTile = subField.getTile(x, i);
                    if (iTile.getOwner() != null && iTile.getOwner().equals(playerID)) {
                        down = true;
                        break;
                    }
                }
                in = left && right && up && down;
            }
            if (!in) {
                x = (int) (Math.random() * sizeX);
                y = (int) (Math.random() * sizeY);
            }
        } while (!in);

        subField = fill(subField, x, y, playerID);
        fieldService.setSubField(minX, maxX, minY, maxY, subField);
    }

    private Field fill(Field subField, int x, int y, Integer playerID) {
        Tile tile = subField.getTile(x, y);
        tile.setOwner(playerID);
        tile = fieldService.mixColor(tile);
        subField.setTile(tile, x, y);
        if (x > 0 && y > 0 && (subField.getTile(x - 1, y - 1).getOwner() == null
                || subField.getTile(x - 1, y - 1).getOwner() != null
                && !subField.getTile(x - 1, y - 1).getOwner().equals(playerID))) {
            subField = fill(subField, x - 1, y - 1, playerID);
        }
        if (x > 0 && (subField.getTile(x - 1, y).getOwner() == null
                || subField.getTile(x - 1, y).getOwner() != null
                && !subField.getTile(x - 1, y).getOwner().equals(playerID))) {
            subField = fill(subField, x - 1, y, playerID);
        }
        if (x > 0 && y < subField.getHeight() - 1 && (subField.getTile(x - 1, y + 1).getOwner() == null
                || subField.getTile(x - 1, y + 1).getOwner() != null
                && !subField.getTile(x - 1, y + 1).getOwner().equals(playerID))) {
            subField = fill(subField, x - 1, y + 1, playerID);
        }
        if (y > 0 && (subField.getTile(x, y - 1).getOwner() == null
                || subField.getTile(x, y - 1).getOwner() != null
                && !subField.getTile(x, y - 1).getOwner().equals(playerID))) {
            subField = fill(subField, x, y - 1, playerID);
        }
        if (y < subField.getHeight()- 1 && (subField.getTile(x, y + 1).getOwner() == null
                || subField.getTile(x, y + 1).getOwner() != null
                && !subField.getTile(x, y + 1).getOwner().equals(playerID))) {
            subField = fill(subField, x, y + 1, playerID);
        }
        if (x < subField.getWidth() - 1 && y > 0 && (subField.getTile(x + 1, y - 1).getOwner() == null
                || subField.getTile(x + 1, y - 1).getOwner() != null
                && !subField.getTile(x + 1, y - 1).getOwner().equals(playerID))) {
            subField = fill(subField, x + 1, y - 1, playerID);
        }
        if (x < subField.getWidth()  - 1 && (subField.getTile(x + 1, y).getOwner() == null
                || subField.getTile(x + 1, y).getOwner() != null
                && !subField.getTile(x + 1, y).getOwner().equals(playerID))) {
            subField = fill(subField, x + 1, y, playerID);
        }
        if (x < subField.getWidth()  - 1 && y < subField.getHeight() - 1
                && (subField.getTile(x + 1, y + 1).getOwner() == null
                || subField.getTile(x + 1, y + 1).getOwner() != null
                && !subField.getTile(x + 1, y + 1).getOwner().equals(playerID))) {
            subField = fill(subField, x + 1, y + 1, playerID);
        }
        return subField;
    }

    public Tile[] getSubFieldByPlayer(Integer playerID) {
        Player player = playerRepository.findById(playerID).orElse(null);
        if (player == null) return null;
        int delta = 10;
        int minX = player.getX() - delta,
                maxX = player.getX() + delta,
                minY = player.getY() - delta,
                maxY = player.getY() + delta;
        Field subField = fieldService.getSubField(minX, maxX, minY, maxY);
        log.info("getSubFieldByPlayer: subfield = " + subField);
        Tile[] tiles = twoToOne(subField);
        return tiles;
    }

    private Tile[] twoToOne(Field field) {
        Tile[][] tiles =  field.getField();
        Tile[] newTiles = new Tile[tiles.length * tiles[0].length];
        int i = 0;
        for (int y = 0; y < tiles[0].length; y++) {
            for (int x = 0; x < tiles.length; x++) {
                newTiles[i] = tiles[x][y];
                i++;
            }
        }
        return newTiles;
    }
}
