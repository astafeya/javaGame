package org.example.model;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

@Data
public class ChangeDirectionListItem {
    @Id
    private String id;
    @NonNull
    private Integer playerID;
    @NonNull
    private Direction newDirection;
}
