package pl.utp.pss.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.utp.pss.model.Delegation;
import pl.utp.pss.model.User;

import java.util.List;

@Repository
public interface DelegationRepository extends JpaRepository<Delegation, Long> {

    List<Delegation> findAllByUser(User user);
}