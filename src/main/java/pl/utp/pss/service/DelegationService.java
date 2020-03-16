package pl.utp.pss.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.utp.pss.model.Delegation;
import pl.utp.pss.model.User;
import pl.utp.pss.repository.DelegationRepository;
import pl.utp.pss.repository.UserRepository;;

import java.util.List;

@Service
public class DelegationService {

    DelegationRepository delegationRepository;
    UserRepository userRepository;

    @Autowired
    public DelegationService(DelegationRepository delegationRepository, UserRepository userRepository) {
        this.delegationRepository = delegationRepository;
        this.userRepository = userRepository;
    }

    public List<Delegation> getAllDelegations() {
        return delegationRepository.findAll(Sort.by(Sort.Order.desc("id")));
    }

    public List<Delegation> getAllByUserId(long id) {
        return delegationRepository.findAllByUser(userRepository.findById(id).get());
    }

    public Delegation getDelegation(long id) {
        return delegationRepository.findById(id).get();
    }

    public Delegation createDelegation(Delegation delegation) {
        return delegationRepository.save(delegation);
    }

    public void updateDelegation(Delegation delegation) {
        delegationRepository.save(delegation);
    }

    public void deleteDelegation(long userId, long delegationId) {
        Delegation delegation = delegationRepository.findById(delegationId).get();

        User user = userRepository.findById(userId).get();
        user.getDelegations().remove(delegation);
        userRepository.save(user);

        delegationRepository.deleteById(delegationId);
    }
}
