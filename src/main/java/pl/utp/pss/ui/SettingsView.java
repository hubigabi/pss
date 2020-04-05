package pl.utp.pss.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.utp.pss.model.User;
import pl.utp.pss.service.UserService;

public class SettingsView extends VerticalLayout {

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private User loggedUser;

    @Autowired
    public SettingsView(UserService userService, PasswordEncoder passwordEncoder, long userId) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.loggedUser = userService.getUser(userId);

        setSpacing(true);

        PasswordField currentPasswordField = new PasswordField("Current password");
        PasswordField newPasswordField = new PasswordField("New password");
        PasswordField newPasswordField2 = new PasswordField("Type again new password");

        Button saveButton = new Button("Save");
        saveButton.addClickListener(clickEvent -> {
            User user = userService.getUser(loggedUser.getId());

            if (passwordEncoder.matches(currentPasswordField.getValue(), user.getPassword())) {
                if (newPasswordField.getValue().equals(newPasswordField2.getValue())) {
                    user.setPassword(passwordEncoder.encode(newPasswordField.getValue()));
                    userService.updateUser(user);
                    Notification.show("Password has been changed!",
                            "",
                            Notification.Type.HUMANIZED_MESSAGE);
                    currentPasswordField.setValue("");
                    newPasswordField.setValue("");
                    newPasswordField2.setValue("");
                } else {
                    Notification.show("New passwords are not identical!", "",
                            Notification.Type.ERROR_MESSAGE);
                }
            } else {
                Notification.show("Current password is wrong!", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });

        addComponents(currentPasswordField, newPasswordField, newPasswordField2, saveButton);

    }
}
