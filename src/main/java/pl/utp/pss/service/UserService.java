package pl.utp.pss.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.utp.pss.model.Role;
import pl.utp.pss.model.User;
import pl.utp.pss.repository.DelegationRepository;
import pl.utp.pss.repository.RoleRepository;
import pl.utp.pss.repository.UserRepository;

import java.util.List;

@Transactional
@Service
public class UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    DelegationRepository delegationRepository;


    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, DelegationRepository delegationRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.delegationRepository = delegationRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Order.desc("id")));
    }

    public User getUser(long id) {
        return userRepository.findById(id).get();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void deleteUserById(long id) {
        roleRepository.deleteFrom_ROLE_USER_ByUserId(id);
        delegationRepository.deleteFrom_DELEGATION_ByUserId(id);
        userRepository.deleteById(id);
    }

    public void changePassword(long id, String password) {
        User user = userRepository.findById(id).get();
        user.setPassword(password);
        userRepository.save(user);
    }
}
