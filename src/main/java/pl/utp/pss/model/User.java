package pl.utp.pss.model;

import lombok.*;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"roles", "delegations"})
@ToString(exclude = {"roles", "delegations"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String companyName;

    @NotNull
    private String companyAddress;

    @NotNull
    private String companyNip;

    @NotNull
    private String name;

    @NotNull
    private String lastName;

    @NotNull
    private String email;

    @NotNull
    private String password;

    private boolean status;
    private LocalDate registrationDate;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    @JoinTable(
            name = "User_Role",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id") }
    )
    private Set<Role> roles = new HashSet<>();


    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Delegation> delegations = new HashSet<>();


    public User(@NotNull String companyName, @NotNull String companyAddress,
                @NotNull String companyNip, @NotNull String name, @NotNull String lastName,
                @NotNull String email, @NotNull String password) {
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyNip = companyNip;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;

        this.status = true;
        this.registrationDate = LocalDate.now();
    }

    public void addRole(Role role){
        this.roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(Role role){
        this.roles.remove(role);
        role.getUsers().remove(this);
    }

    public void addDelegation(Delegation delegation){
        this.delegations.add(delegation);
        delegation.setUser(this);
    }

    public void removeDelegation(Delegation delegation){
        this.delegations.remove(delegation);
        delegation.setUser(null);
    }
}