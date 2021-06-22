package org.example.repository;

import org.example.model.ChangeDirectionListItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeDirectionRepository extends MongoRepository<ChangeDirectionListItem, String> {
}
