package pl.utp.pss.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.utp.pss.model.*;
import pl.utp.pss.repository.DelegationRepository;
import pl.utp.pss.repository.RoleRepository;
import pl.utp.pss.repository.UserRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Arrays;

@Service
public class InitService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private DelegationRepository delegationRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public InitService(UserRepository userRepository, RoleRepository roleRepository,
                       DelegationRepository delegationRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.delegationRepository = delegationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        roleRepository.deleteAllInBatch();
        delegationRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        Role r1 = new Role("Role1");
        Role r2 = new Role("Role2");
        Role r3 = new Role("Role3");

        Delegation d1 = new Delegation("description1", LocalDate.now(), LocalDate.now().plusDays(5),
                50, 1, 1, 2, TransportType.CAR, 0,
                AutoCapacity.OVER_900, 120.00, 120, 30, 25);
        Delegation d2 = new Delegation("description2", LocalDate.now().minusDays(1), LocalDate.now().plusDays(3),
                60, 2, 1, 0, TransportType.CAR, 0,
                AutoCapacity.BELLOW_OR_900, 276.00, 140, 35, 20);
        Delegation d3 = new Delegation("description3", LocalDate.now().minusDays(4), LocalDate.now().plusDays(2),
                50, 1, 1, 1, TransportType.TRAIN, 34.50,
                AutoCapacity.NONE, 0.0, 100, 45, 40);

        User u1 = new User("companyName1", "companyAddress1", "companyNip1",
                "John", "Smith", "JohnSmith@gmail.com", passwordEncoder.encode("JohnSmith"));
        User u2 = new User("companyName2", "companyAddress2", "companyNip1",
                "Adam", "Johnson", "AdamJohnson@gmail.com", passwordEncoder.encode("AdamJohnson"));


        r1.addUser(u1);
        r2.addUser(u1);
        r2.addUser(u2);
        r3.addUser(u2);

        d1.addUser(u1);
        d2.addUser(u1);
        d3.addUser(u2);

        userRepository.saveAll(Arrays.asList(u1, u2));
        roleRepository.saveAll(Arrays.asList(r1, r2, r3));
        delegationRepository.saveAll(Arrays.asList(d1, d2));

        userRepository.findAll().forEach(user -> {
            System.out.println(user);
            System.out.println(user.getRoles());
            System.out.println(user.getDelegations());
        });

        System.out.println("\nDelegation by user name");
        System.out.println("Delegations of John:");
        System.out.println(delegationRepository.findAllByUser(userRepository.findByName("John").get()));

        System.out.println("\nDelegations of Adam:");
        System.out.println(delegationRepository.findAllByUser(userRepository.findByName("Adam").get()));
    }
}
