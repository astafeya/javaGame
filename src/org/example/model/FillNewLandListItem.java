package org.example.model;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

@Data
public class FillNewLandListItem {
    @Id
    private String id;
    @NonNull
    private Integer playerID;
}
