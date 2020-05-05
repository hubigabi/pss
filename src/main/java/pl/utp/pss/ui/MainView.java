package pl.utp.pss.ui;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import pl.utp.pss.model.Role;
import pl.utp.pss.model.User;
import pl.utp.pss.service.DelegationService;
import pl.utp.pss.service.RoleService;
import pl.utp.pss.service.UserService;

import java.util.List;
import java.util.Map;


@Title("Delegation")
@SpringUI(path = "/logged")
public class MainView extends UI {

    @Autowired
    private UserService userService;

    @Autowired
    private DelegationService delegationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    private User loggedUser;

    private VerticalLayout root;
    private HorizontalLayout headHorizontalLayout;
    private HorizontalLayout mainHorizontalLayout;
    private VerticalLayout navBar;
    private VerticalLayout mainView;

    @Override
    protected void init(VaadinRequest request) {

        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User) {
            loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } else {

            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Site: " + oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());

            //Logged by Google
            if (oAuth2AuthenticationToken.getAuthorizedClientRegistrationId().equals("google")) {
                Map<String, Object> claims = ((DefaultOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaims();
                String mail = (String) claims.get("email");
                System.out.println("Mail: " + mail);

                List<User> userList = userService.findAllByEmail(mail);
                if (userList.size() == 0) {
                    getUI().getPage().setLocation("/");
                } else {
                    loggedUser = userList.get(0);
                }
            }
            //Logged by Facebook
            else if (oAuth2AuthenticationToken.getAuthorizedClientRegistrationId().equals("facebook")) {
                DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                String mail = (String) defaultOAuth2User.getAttributes().get("email");
                System.out.println("Email: " + mail);

                List<User> userList = userService.findAllByEmail(mail);
                if (userList.size() == 0) {
                    getUI().getPage().setLocation("/");
                } else {
                    loggedUser = userList.get(0);
                }
            }
        }

        root = new VerticalLayout();

        headHorizontalLayout = new HorizontalLayout();
        headHorizontalLayout.addComponent(new Label(loggedUser.getName() + " "
                + loggedUser.getLastName() + ", " + loggedUser.getCompanyName()));

        mainHorizontalLayout = new HorizontalLayout();

        navBar = new VerticalLayout();

        Button delegationsButton = new Button("Delegations");
        delegationsButton.setWidth("140");
        delegationsButton.addClickListener(clickEvent -> {
                    mainView.removeAllComponents();
                    mainView.addComponents(new DelegationView(userService, delegationService, loggedUser.getId()));
                }
        );

        Button profileButton = new Button("Profile");
        profileButton.setWidth("140");
        profileButton.addClickListener(clickEvent -> {
                    mainView.removeAllComponents();
                    mainView.addComponents(new ProfileView(userService, loggedUser.getId()));
                }
        );


        Button changePasswordButton = new Button("Settings");
        changePasswordButton.setWidth("140");
        changePasswordButton.addClickListener(clickEvent -> {
                    mainView.removeAllComponents();
                    mainView.addComponents(new SettingsView(userService, passwordEncoder, loggedUser.getId()));
                }
        );

        Button signOutButton = new Button("Sign out");
        signOutButton.setWidth("140");
        signOutButton.addClickListener(clickEvent -> {
                    getUI().getPage().setLocation("/logout");
                }
        );
        navBar.addComponents(delegationsButton, profileButton, changePasswordButton);

        //FOR ADMIN
        if (loggedUser.getRoles().stream().map(Role::getRoleName).anyMatch(s -> s.equals("ROLE_ADMIN"))) {
            Button adminButton = new Button("Admin");
            adminButton.setWidth("140");
            adminButton.addClickListener(clickEvent -> {
                        mainView.removeAllComponents();
                        mainView.addComponents(new AdminView(userService, delegationService, roleService, loggedUser.getId()));
                    }
            );
            navBar.addComponents(adminButton);
        }

        navBar.addComponent(signOutButton);

        mainView = new VerticalLayout();
        mainHorizontalLayout.addComponents(navBar, mainView);

        root.addComponents(headHorizontalLayout, mainHorizontalLayout);
        root.setSizeUndefined();
        setContent(root);

    }
}
