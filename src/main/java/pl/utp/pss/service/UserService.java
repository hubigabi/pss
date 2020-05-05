package pl.utp.pss.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.utp.pss.model.Delegation;
import pl.utp.pss.model.Role;
import pl.utp.pss.model.User;
import pl.utp.pss.repository.DelegationRepository;
import pl.utp.pss.repository.RoleRepository;
import pl.utp.pss.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private DelegationRepository delegationRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       DelegationRepository delegationRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.delegationRepository = delegationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Order.desc("id")));
    }

    public User getUser(long id) {
        return userRepository.findById(id).get();
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUserById(long id) {
        roleRepository.deleteFrom_ROLE_USER_ByUserId(id);
        delegationRepository.deleteFrom_DELEGATION_ByUserId(id);
        userRepository.deleteById(id);
    }

    public void deleteUserWithoutDelegationById(long id) {
        User user = userRepository.findById(id).get();

        List<Delegation> delegations = delegationRepository.findAllByUser(user)
                .stream()
                .map(delegation -> {
                    delegation.removeUser(user);
                    return delegation;
                })
                .collect(Collectors.toList());
        delegationRepository.saveAll(delegations);

        List<Role> roles = roleRepository.findAllByUsersContains(user)
                .stream()
                .map(role -> {
                    role.removeUser(user);
                    return role;
                }).collect(Collectors.toList());
        roleRepository.saveAll(roles);

        userRepository.deleteById(id);
    }

    public void changePassword(long id, String password) {
        User user = userRepository.findById(id).get();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public List<User> findAllByEmail(String email) {
        return userRepository.findAllByEmail(email);
    }
}
