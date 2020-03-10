package pl.utp.pss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.utp.pss.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
