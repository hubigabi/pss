package pl.utp.pss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.utp.pss.model.User;
import pl.utp.pss.service.RoleService;
import pl.utp.pss.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    UserService userService;
    RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody User user){
        userService.createUser(user);
    }

    @PutMapping("/changePassword")
    public void changePassword(long userId, String newPassword){
        userService.changePassword(userId, newPassword);
    }

    @DeleteMapping("/deleteUserById")
    public void deleteUserById(long userId){
         userService.deleteUserById(userId);
    }

    @GetMapping("/allUsersByRoleName")
    public List<User> getAllUsersByRoleName(String roleName){
        return roleService.getAllUsersByRoleName(roleName);
    }
}
