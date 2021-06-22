package org.example.repository;

import org.example.model.Field;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldRepository extends MongoRepository <Field, String> {
}
