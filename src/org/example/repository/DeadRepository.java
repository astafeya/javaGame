package org.example.repository;

import org.example.model.DeadListItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeadRepository extends MongoRepository<DeadListItem, String> {
}
