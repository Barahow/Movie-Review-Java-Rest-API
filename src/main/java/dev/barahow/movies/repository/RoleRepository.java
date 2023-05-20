package dev.barahow.movies.repository;

import dev.barahow.movies.model.UserRole;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends MongoRepository<UserRole, ObjectId> {

    UserRole findByName(String name);
}
