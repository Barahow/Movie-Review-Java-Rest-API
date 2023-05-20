package dev.barahow.movies.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRole {

    @Id

    private ObjectId id;
    private String name;

    public UserRole(String name) {
        this.name= name;

    }


    public String getName() {
        return name;
    }
}
