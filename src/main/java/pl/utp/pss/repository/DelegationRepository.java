package pl.utp.pss.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.utp.pss.model.Delegation;
import pl.utp.pss.model.User;

import java.util.List;

@Repository
public interface DelegationRepository extends JpaRepository<Delegation, Long> {

    List<Delegation> findAllByUser(User user);

    @Modifying
    @Query(value = "DELETE FROM DELEGATION  WHERE USER_ID = ?1", nativeQuery = true)
    void deleteFrom_DELEGATION_ByUserId(long id);

    @Modifying
    @Query(value = "DELETE FROM DELEGATION  WHERE ID = ?1", nativeQuery = true)
    void deleteFrom_DELEGATION_ByUserIdaaaaaaaaaaaaaa(long id);

}