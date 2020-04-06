package pl.utp.pss.ui;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.utp.pss.model.User;
import pl.utp.pss.service.DelegationService;
import pl.utp.pss.service.UserService;


@Title("Delegation")
@SpringUI(path = "/logged")
public class MainView extends UI {

    @Autowired
    private UserService userService;

    @Autowired
    private DelegationService delegationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User loggedUser;

    private VerticalLayout root;
    private HorizontalLayout headHorizontalLayout;
    private HorizontalLayout mainHorizontalLayout;
    private VerticalLayout navBar;
    private VerticalLayout mainView;

    @Override
    protected void init(VaadinRequest request) {
        loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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
        navBar.addComponents(delegationsButton, profileButton, changePasswordButton, signOutButton);

        mainView = new VerticalLayout();
        mainHorizontalLayout.addComponents(navBar, mainView);

        root.addComponents(headHorizontalLayout, mainHorizontalLayout);
        root.setSizeUndefined();
        setContent(root);

    }
}
