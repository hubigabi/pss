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

        Role r1 = new Role("ROLE_ADMIN");
        Role r2 = new Role("ROLE_USER");

        Delegation d1 = new Delegation("description 1", LocalDate.now(), LocalDate.now().plusDays(5),
                50, 1, 1, 2, TransportType.CAR, 0,
                AutoCapacity.OVER_900, 120.00, 120, 30, 25);
        Delegation d2 = new Delegation("description 2", LocalDate.now().minusDays(1), LocalDate.now().plusDays(3),
                60, 2, 1, 0, TransportType.BUS, 42.00,
                AutoCapacity.NONE, 276.00, 140, 35, 20);
        Delegation d3 = new Delegation("description 3", LocalDate.now().minusDays(4), LocalDate.now().plusDays(2),
                50, 2, 1, 2, TransportType.TRAIN, 34.50,
                AutoCapacity.NONE, 40.0, 100, 45, 40);
        Delegation d4 = new Delegation("description 4", LocalDate.now().minusDays(9), LocalDate.now().minusDays(2),
                60, 1, 1, 1, TransportType.BUS, 25.50,
                AutoCapacity.NONE, 170.0, 120, 60, 70);
        Delegation d5 = new Delegation("description 5", LocalDate.now().minusDays(4), LocalDate.now().plusDays(2),
                70, 1, 2, 1, TransportType.CAR, 0.0,
                AutoCapacity.OVER_900, 370.0, 150, 90, 75);
        Delegation d6 = new Delegation("description 6", LocalDate.now().plusDays(16), LocalDate.now().plusDays(20),
                40, 1, 1, 0, TransportType.CAR, 0.0,
                AutoCapacity.NONE, 270.0, 110, 55, 40);
        Delegation d7 = new Delegation("description 7", LocalDate.now().plusDays(4), LocalDate.now().plusDays(7),
                80, 1, 1, 2, TransportType.TRAIN, 52.00,
                AutoCapacity.NONE, 450.0, 90, 55, 70);
        Delegation d8 = new Delegation("description 8", LocalDate.now().plusDays(2), LocalDate.now().plusDays(6),
                60, 1, 1, 2, TransportType.BUS, 36.00,
                AutoCapacity.NONE, 189.00, 120, 28, 16);
        Delegation d9 = new Delegation("description 9", LocalDate.now().minusDays(7), LocalDate.now().minusDays(5),
                60, 1, 1, 2, TransportType.CAR, 0.0,
                AutoCapacity.OVER_900, 358.0, 90, 35, 40);
        Delegation d10 = new Delegation("description 10", LocalDate.now().minusDays(1), LocalDate.now().plusDays(2),
                70, 1, 1, 1, TransportType.TRAIN, 26.00,
                AutoCapacity.NONE, 140.00, 120, 50, 40);
        Delegation d11 = new Delegation("description 11", LocalDate.now().plusDays(8), LocalDate.now().plusDays(10),
                80, 2, 0, 2, TransportType.CAR, 0.0,
                AutoCapacity.OVER_900, 238.0, 150, 25, 35);
        Delegation d12 = new Delegation("description 12", LocalDate.now().plusDays(12), LocalDate.now().plusDays(15),
                60, 2, 1, 0, TransportType.BUS, 48.00,
                AutoCapacity.NONE, 280.00, 180, 45, 55);

        d1.setStatus(Status.ACCEPTED);
        d4.setStatus(Status.ACCEPTED);
        d5.setStatus(Status.REQUEST_FROM_NOT_ACCEPTED_TO_ACCEPTED);
        d7.setStatus(Status.REQUEST_FROM_NOT_ACCEPTED_TO_ACCEPTED);
        d9.setStatus(Status.REQUEST_FROM_ACCEPTED_TO_NOT_ACCEPTED);
        d10.setStatus(Status.ACCEPTED);

        User u1 = new User("Company Name 1", "Company Address 1", "Company Nip 1",
                "John", "Smith", "JohnSmith@gmail.com", passwordEncoder.encode("JohnSmith"));
        User u2 = new User("Company Name 2", "Company Address 2", "Company Nip 2",
                "John2", "Smith2", "JohnSmith2@gmail.com", passwordEncoder.encode("JohnSmith2"));
        User u3 = new User("Company Name 3", "Company Address 3", "Company Nip 3",
                "Adam", "Johnson", "hubigabi19@gmail.com", passwordEncoder.encode("AdamJohnson"));

        r1.addUser(u3);
        r2.addUser(u1);
        r2.addUser(u2);
        r2.addUser(u3);

        d1.addUser(u1);
        d2.addUser(u1);
        d3.addUser(u3);
        d4.addUser(u3);
        d5.addUser(u1);
        d6.addUser(u1);
        d7.addUser(u1);
        d8.addUser(u3);
        d9.addUser(u2);
        d10.addUser(u2);
        d11.addUser(u2);
        d12.addUser(u2);

        userRepository.saveAll(Arrays.asList(u1, u2, u3));
        roleRepository.saveAll(Arrays.asList(r1, r2));
        delegationRepository.saveAll(Arrays.asList(d1, d2, d3, d4, d5, d6, d7));

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
