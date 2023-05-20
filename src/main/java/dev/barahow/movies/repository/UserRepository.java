package dev.barahow.movies.repository;

import dev.barahow.movies.model.AppUser;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<AppUser, ObjectId> {

    AppUser findByEmailIgnoreCase(String email);

    AppUser findByEmailAndPassword(String username, String password);



    //void updateIsLocked(@Param("email")String email, @Param("isLocked") boolean locked);
}
