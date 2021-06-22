package org.example.repository;

import org.example.model.FillNewLandListItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FillNewLandRepository extends MongoRepository<FillNewLandListItem, String> {
}
