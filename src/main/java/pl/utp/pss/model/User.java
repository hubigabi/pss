package pl.utp.pss.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import java.util.*;

@Data
@EqualsAndHashCode(exclude = {"roles", "delegations",})
@ToString(exclude = {"roles", "delegations",})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"enabled", "username", "authorities",
        "credentialsNonExpired", "accountNonLocked", "accountNonExpired"})
public class User implements UserDetails {

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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate registrationDate;

    @JsonIgnore
    @ManyToMany(mappedBy = "users",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();


    @JsonIgnore
    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            // orphanRemoval = true,
            fetch = FetchType.EAGER)
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
        }

        System.out.println(authorities);
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}