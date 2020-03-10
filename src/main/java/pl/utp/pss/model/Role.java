package pl.utp.pss.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@ToString(exclude = "users")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roleName;

    @ManyToMany(mappedBy = "roles", cascade = CascadeType.ALL)
    private Set<User> users = new HashSet<>();

    public Role(String roleName) {
        this.roleName = roleName;
    }

}