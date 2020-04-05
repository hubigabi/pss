package pl.utp.pss.ui;

import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import pl.utp.pss.model.Role;
import pl.utp.pss.model.User;
import pl.utp.pss.service.UserService;

public class ProfileView extends VerticalLayout {

    private UserService userService;

    private User loggedUser;

    private Button editButton;
    private Button saveButton;

    private TextField nameTextField;
    private TextField lastNameTextField;
    private TextField emailTextField;
    private TextField companyNameTextField;
    private TextField companyAddressTextField;
    private TextField companyNipTextField;

    private CheckBox statusCheckbox;
    private DateField registrationDateField;

    @Autowired
    public ProfileView(UserService userService, long userId) {
        this.userService = userService;
        this.loggedUser = userService.getUser(userId);

        setSpacing(true);

        editButton = new Button("Edit");
        editButton.addClickListener(clickEvent ->
                enable(true)
        );

        nameTextField = new TextField("Name", loggedUser.getName());
        lastNameTextField = new TextField("Last Name", loggedUser.getLastName());
        emailTextField = new TextField("Email", loggedUser.getEmail());
        companyNameTextField = new TextField("Company name", loggedUser.getCompanyName());
        companyAddressTextField = new TextField("Company address", loggedUser.getCompanyAddress());
        companyNipTextField = new TextField("Company NIP", loggedUser.getCompanyNip());

        statusCheckbox = new CheckBox("Status", loggedUser.isStatus());
        statusCheckbox.setEnabled(false);

        registrationDateField = new DateField("Registration date", loggedUser.getRegistrationDate());
        registrationDateField.setEnabled(false);

        CheckBoxGroup<String> rolesCheckBoxGroup =
                new CheckBoxGroup<>("Roles");
        rolesCheckBoxGroup.setItems(loggedUser.getRoles().stream()
                .map(Role::getRoleName));

        loggedUser.getRoles().stream()
                .map(Role::getRoleName)
                .forEach(s -> rolesCheckBoxGroup.select(s));

        rolesCheckBoxGroup.setEnabled(false);

        saveButton = new Button("Save");
        saveButton.addClickListener(clickEvent -> {
            User user = userService.getUser(loggedUser.getId());

            user.setName(nameTextField.getValue());
            user.setLastName(lastNameTextField.getValue());
            user.setEmail(emailTextField.getValue());
            user.setCompanyName(companyNameTextField.getValue());
            user.setCompanyAddress(companyAddressTextField.getValue());
            user.setCompanyNip(companyNipTextField.getValue());

            userService.updateUser(user);
            enable(false);
            Notification.show("All changes ha been saved.", "",
                    Notification.Type.HUMANIZED_MESSAGE);
        });
        enable(false);

        addComponents(editButton, nameTextField, lastNameTextField, emailTextField, companyNameTextField,
                companyAddressTextField, companyNipTextField, statusCheckbox, registrationDateField, rolesCheckBoxGroup, saveButton);

    }

    public void enable(boolean value) {
        nameTextField.setEnabled(value);
        lastNameTextField.setEnabled(value);
        emailTextField.setEnabled(value);
        companyNameTextField.setEnabled(value);
        companyAddressTextField.setEnabled(value);
        companyNipTextField.setEnabled(value);
    }

}

