package lkeleti.redditclone.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "user_name")
    @NotBlank(message = "Username is required")
    private String userName;

    @NotBlank(message = "Password is required")
    private String password;

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    private boolean enabled;

    public User(String userName, String password, String email, LocalDateTime createdDate, boolean enabled) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.createdDate = createdDate;
        this.enabled = enabled;
    }
}
