package org.example.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Player extends GameObject {
    @Id
    private Integer id;
    private Direction direction;

    private int landMinX;
    private int landMaxX;
    private int landMinY;
    private int landMaxY;

    private int tailMinX;
    private int tailMaxX;
    private int tailMinY;
    private int tailMaxY;
}
