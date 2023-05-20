package dev.barahow.movies.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "app_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUser {

    @Id
    private ObjectId id;
    @NotBlank(message = "Name is mandatory")
    private String userName;
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Password is mandatory")
    private String password;

   // @JsonSerialize(using = ToStringSerializer.class)
    @DBRef
    private List<UserRole> roles = new ArrayList<>();

    @DBRef
    private List<Review> reviews;


    @DBRef
    private List<Rating> ratings;

    private boolean isLocked;

    @Transient
    private int failedLoginAttempts;

    @Transient
    private LocalDateTime unLockTime;


    public AppUser(ObjectId id, String userName, String email, String password, List<UserRole> roles) {


    }


    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }


    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;

    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AppUser{")
                .append("id=").append(id)
                .append(", userName='").append(userName).append('\'')
                .append(", email='").append(email).append('\'')
                .append(", password='").append(password).append('\'')
                .append(", roles=[");
        for (UserRole role : roles) {
            sb.append(role.getName()).append(", ");
        }
        if (!roles.isEmpty()) {
            sb.setLength(sb.length() - 2); // Remove the last ", " characters
        }
        sb.append("]}");
        return sb.toString();
    }


}
