package pl.utp.pss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.utp.pss.model.Role;
import pl.utp.pss.model.User;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAllByUsersContains(User user);

    @Modifying
    @Query(value = "DELETE FROM ROLE_USER  WHERE USER_ID = ?1", nativeQuery = true)
    void deleteFrom_ROLE_USER_ByUserId(long id);

    List<Role> findAllByRoleName(String name);
}
