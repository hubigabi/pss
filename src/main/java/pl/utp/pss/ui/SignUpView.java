package pl.utp.pss.ui;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.AlignmentInfo;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.utp.pss.model.Role;
import pl.utp.pss.model.User;
import pl.utp.pss.service.RoleService;
import pl.utp.pss.service.UserService;

import java.util.List;

@Title("Sign up")
@SpringUI(path = "")
public class SignUpView extends UI {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    private VerticalLayout root;

    @Override
    protected void init(VaadinRequest request) {

        root = new VerticalLayout();
        root.setWidthFull();

        Button loginButton = new Button("Log in");
        loginButton.setWidth("150");
        loginButton.addClickListener(clickEvent -> {
                    getUI().getPage().setLocation("/login");
                }
        );

        Button signUpButton = new Button("Sign up");
        signUpButton.setWidth("150");
        signUpButton.addClickListener(clickEvent -> {
                    root.removeAllComponents();
                    loadSignUpView();
                }
        );

        root.setSpacing(true);
        root.setMargin(new MarginInfo(true, true, true, true));
        root.addComponents(loginButton, signUpButton);
        root.setComponentAlignment(loginButton, new Alignment(AlignmentInfo.Bits.ALIGNMENT_HORIZONTAL_CENTER));
        root.setComponentAlignment(signUpButton, new Alignment(AlignmentInfo.Bits.ALIGNMENT_HORIZONTAL_CENTER));

        setContent(root);
    }

    private void loadSignUpView() {
        TextField nameTextField = new TextField("Name");
        TextField lastNameTextField = new TextField("Last Name");
        TextField emailTextField = new TextField("Email");
        TextField companyNameTextField = new TextField("Company name");
        TextField companyAddressTextField = new TextField("Company address");
        TextField companyNipTextField = new TextField("Company NIP");

        PasswordField newPasswordField = new PasswordField("New password");
        PasswordField newPasswordField2 = new PasswordField("Type again new password");

        Button registerButton = new Button("Register");
        registerButton.setWidth("150");
        registerButton.addClickListener(clickEvent -> {

                    try {
                        if (userService.findAllByEmail(emailTextField.getValue()).size() == 0) {
                            String regexPassword = "^(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";

                            User user = new User(companyNameTextField.getValue(),
                                    companyAddressTextField.getValue(),
                                    companyNipTextField.getValue(),
                                    nameTextField.getValue(),
                                    lastNameTextField.getValue(),
                                    emailTextField.getValue(),
                                    newPasswordField.getValue()
                            );

                            if (newPasswordField.getValue().equals(newPasswordField2.getValue())) {
                                if (newPasswordField.getValue().matches(regexPassword)) {

                                    user = userService.createUser(user);

                                    List<Role> roles = roleService.findAllByRoleName("Role1");
                                    if (roles.size() > 0) {
                                        Role role = roles.get(0);
                                        role.addUser(user);
                                        user = userService.updateUser(user);
                                    }
                                    Notification.show("User has been registered!",
                                            "",
                                            Notification.Type.HUMANIZED_MESSAGE);

                                    Authentication auth = new UsernamePasswordAuthenticationToken(user,
                                            "", user.getAuthorities());
                                    SecurityContextHolder.getContext().setAuthentication(auth);

                                    getUI().getPage().setLocation("/logged");

                                } else {
                                    Notification.show("Password is not valid!",
                                            "Password has to contain at least:\n" +
                                                    "one lower case, one upper case letter & 8 characters.",
                                            Notification.Type.ERROR_MESSAGE);
                                }
                            } else {
                                Notification.show("Passwords are not identical!", "",
                                        Notification.Type.ERROR_MESSAGE);
                            }
                        } else {
                            Notification.show("This email has already been taken", "",
                                    Notification.Type.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        Notification.show("Wrong data to register user!", "",
                                Notification.Type.ERROR_MESSAGE);
                        e.printStackTrace();
                    }

                }
        );

        root.addComponents(nameTextField, lastNameTextField, emailTextField, companyNameTextField,
                companyAddressTextField, companyNipTextField, newPasswordField, newPasswordField2, registerButton);
        root.setComponentAlignment(nameTextField, new Alignment(AlignmentInfo.Bits.ALIGNMENT_HORIZONTAL_CENTER));
        root.setComponentAlignment(lastNameTextField, new Alignment(AlignmentInfo.Bits.ALIGNMENT_HORIZONTAL_CENTER));
        root.setComponentAlignment(emailTextField, new Alignment(AlignmentInfo.Bits.ALIGNMENT_HORIZONTAL_CENTER));
        root.setComponentAlignment(companyNameTextField, new Alignment(AlignmentInfo.Bits.ALIGNMENT_HORIZONTAL_CENTER));
        root.setComponentAlignment(companyAddressTextField, new Alignment(AlignmentInfo.Bits.ALIGNMENT_HORIZONTAL_CENTER));
        root.setComponentAlignment(companyNipTextField, new Alignment(AlignmentInfo.Bits.ALIGNMENT_HORIZONTAL_CENTER));
        root.setComponentAlignment(newPasswordField, new Alignment(AlignmentInfo.Bits.ALIGNMENT_HORIZONTAL_CENTER));
        root.setComponentAlignment(newPasswordField2, new Alignment(AlignmentInfo.Bits.ALIGNMENT_HORIZONTAL_CENTER));
        root.setComponentAlignment(registerButton, new Alignment(AlignmentInfo.Bits.ALIGNMENT_HORIZONTAL_CENTER));

        root.setSpacing(true);
    }


}
