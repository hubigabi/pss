package pl.utp.pss.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.utp.pss.model.Role;
import pl.utp.pss.model.User;
import pl.utp.pss.repository.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll(Sort.by(Sort.Order.desc("id")));
    }

    public Role getRole(long id) {
        return roleRepository.findById(id).get();
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public void updateRole(Role role) {
        roleRepository.save(role);
    }

    public List<User> getAllUsersByRoleName(String roleName) {
        return roleRepository.findAllByRoleName(roleName)
                .stream()
                .flatMap(role -> role.getUsers().stream())
                .collect(Collectors.toList());
    }
}
