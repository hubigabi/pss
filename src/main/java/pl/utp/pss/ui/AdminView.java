package pl.utp.pss.ui;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import pl.utp.pss.model.*;
import pl.utp.pss.service.DelegationService;
import pl.utp.pss.service.RoleService;
import pl.utp.pss.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AdminView extends VerticalLayout {

    private UserService userService;
    private DelegationService delegationService;
    private RoleService roleService;
    private User loggedAdmin;

    User chosenUser;

    List<Delegation> delegationList = new ArrayList<>();
    ListDataProvider<Delegation> gridProvider = DataProvider.ofCollection(delegationList);

    List<User> userList;
    ListDataProvider<User> userProvider;

    @Autowired
    public AdminView(UserService userService, DelegationService delegationService, RoleService roleService, long userId) {
        this.userService = userService;
        this.delegationService = delegationService;
        this.roleService = roleService;
        this.loggedAdmin = userService.getUser(userId);

        Grid<Delegation> delegationGrid = new Grid<>();

        Button addButton = new Button("Add");
        addButton.setEnabled(false);

        Button editButton = new Button("Edit");
        editButton.setEnabled(false);

        Button deleteButton = new Button("Delete");
        deleteButton.setEnabled(false);

        Button giveAdminRoleButton = new Button("Admin Role");
        giveAdminRoleButton.setEnabled(false);

        Button deleteUserButton = new Button("Delete user");
        deleteUserButton.setEnabled(false);

        ComboBox<User> userComboBox =
                new ComboBox<>("Select user");

        userList = userService.getAllUsers()
                .stream()
                .sorted(Comparator.comparing(User::getName))
                .collect(Collectors.toList());
        userProvider = DataProvider.ofCollection(userList);
        userComboBox.setDataProvider(userProvider);

        userComboBox.setItemCaptionGenerator(u -> u.getName() + " " + u.getLastName());
        userComboBox.setEmptySelectionAllowed(true);

        userComboBox.addValueChangeListener(event -> {
            if (event.getValue() == null) {
                chosenUser = null;
                delegationList = new ArrayList<>();
                gridProvider = DataProvider.ofCollection(delegationList);
                gridProvider.refreshAll();
                delegationGrid.setDataProvider(gridProvider);

                addButton.setEnabled(false);
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
                giveAdminRoleButton.setEnabled(false);
                deleteUserButton.setEnabled(false);
            } else {
                chosenUser = event.getValue();

                delegationList = delegationService.getAllByUserId(chosenUser.getId());
                gridProvider = DataProvider.ofCollection(delegationList);
                gridProvider.setSortOrder(Delegation::getDateTimeStart,
                        SortDirection.ASCENDING);
                gridProvider.refreshAll();
                delegationGrid.setDataProvider(gridProvider);

                addButton.setEnabled(true);
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
                giveAdminRoleButton.setEnabled(true);
                deleteUserButton.setEnabled(true);
            }
        });

        giveAdminRoleButton.addClickListener(clickEvent -> {
                    if (chosenUser != null) {
                        if (chosenUser.getRoles().stream().map(Role::getRoleName).noneMatch(s -> s.equals("ROLE_ADMIN"))) {
                            Role adminRole = roleService.findAllByRoleName("ROLE_ADMIN").get(0);
                            adminRole.addUser(chosenUser);
                            roleService.updateRole(adminRole);

                            Notification.show("User has been given admin role!", "",
                                    Notification.Type.HUMANIZED_MESSAGE);
                        } else {
                            Notification.show("This user already is admin!", "",
                                    Notification.Type.ERROR_MESSAGE);
                        }

                    } else {
                        Notification.show("Select user to give him admin role!", "",
                                Notification.Type.ERROR_MESSAGE);
                    }
                }
        );

        deleteUserButton.addClickListener(clickEvent -> {
                    if (chosenUser != null) {
                        userService.deleteUserWithoutDelegationById(chosenUser.getId());

                        if (chosenUser.equals(loggedAdmin)) {
                            getUI().getPage().setLocation("/logout");
                        } else {

                            userList.remove(chosenUser);
                            userProvider.refreshAll();
                            userComboBox.setSelectedItem(null);
                            chosenUser = null;

                            addButton.setEnabled(false);
                            editButton.setEnabled(false);
                            deleteButton.setEnabled(false);
                            giveAdminRoleButton.setEnabled(false);
                            deleteUserButton.setEnabled(false);

                            delegationList.clear();
                            gridProvider.refreshAll();

                            Notification.show("User has been deleted!", "",
                                    Notification.Type.HUMANIZED_MESSAGE);
                        }

                    } else {
                        Notification.show("Select user to give him admin role!", "",
                                Notification.Type.ERROR_MESSAGE);
                    }
                }
        );

        HorizontalLayout userHorizontalLayout = new HorizontalLayout();
        userHorizontalLayout.setWidth("100%");
        userHorizontalLayout.setSpacing(true);
        userHorizontalLayout.setMargin(new MarginInfo(false, false, true, false));
        userHorizontalLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
        userHorizontalLayout.addComponents(userComboBox, giveAdminRoleButton, deleteUserButton);
        userHorizontalLayout.setComponentAlignment(giveAdminRoleButton, Alignment.BOTTOM_RIGHT);
        userHorizontalLayout.setComponentAlignment(deleteUserButton, Alignment.BOTTOM_RIGHT);

        delegationGrid.addColumn(Delegation::getStatus).setCaption("Status").setWidth(200);
        delegationGrid.addColumn(Delegation::getDescription).setCaption("Description").setWidth(150);
        delegationGrid.addColumn(Delegation::getDateTimeStart).setCaption("Start date").setWidth(130);
        delegationGrid.addColumn(Delegation::getDateTimeStop).setCaption("Stop date").setWidth(130);
        delegationGrid.addColumn(Delegation::getTravelDietAmount).setCaption("Diet");
        delegationGrid.addColumn(Delegation::getBreakfastNumber).setCaption("Breakfast");
        delegationGrid.addColumn(Delegation::getDinnerNumber).setCaption("Dinner");
        delegationGrid.addColumn(Delegation::getSupperNumber).setCaption("Supper");

        delegationGrid.addColumn(delegation -> {
            String s = delegation.getTransportType().toString();
            s = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
            s = s.replace("_", " ");
            return s;
        }).setCaption("Transport");

        delegationGrid.addColumn(Delegation::getTicketPrice).setCaption("Ticket");

        delegationGrid.addColumn(delegation -> {
            String s = delegation.getAutoCapacity().toString();
            s = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
            s = s.replace("_", " ");
            return s;
        }).setCaption("Auto capacity");

        delegationGrid.addColumn(Delegation::getKm).setCaption("Km");
        delegationGrid.addColumn(Delegation::getAccommodationPrice).setCaption("Accommodation");
        delegationGrid.addColumn(Delegation::getOtherOutlayDesc).setCaption("Other outlay desc");
        delegationGrid.addColumn(Delegation::getOtherOutlayPrice).setCaption("Other outlay");

        delegationGrid.setWidth(1000, Unit.PIXELS);
        delegationGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        delegationGrid.setDataProvider(gridProvider);

        HorizontalLayout actionsHorizontalLayout = new HorizontalLayout();

        deleteButton.setWidth("100");
        deleteButton.addClickListener(clickEvent -> {

                    if (delegationGrid.getSelectedItems().size() == 1) {
                        Delegation delegation = delegationGrid.getSelectedItems().iterator().next();

                        if (LocalDate.now().isBefore(delegation.getDateTimeStart())) {
                            int indexDelegation = delegationList.indexOf(delegation);

                            delegation.removeUser(chosenUser);
                            delegationService.updateDelegation(delegation);
                            delegationService.deleteEmptyDelegation(delegation);

                            delegationList.remove(indexDelegation);
                            gridProvider.refreshAll();

                            Notification.show("Delegation has been successfully deleted.", "",
                                    Notification.Type.HUMANIZED_MESSAGE);
                        } else {
                            Notification.show("This delegation has taken place. You can not delete it!", "",
                                    Notification.Type.ERROR_MESSAGE);
                        }
                    } else {
                        Notification.show("Select delegation to delete!", "",
                                Notification.Type.ERROR_MESSAGE);
                    }
                }
        );

        actionsHorizontalLayout.setMargin(true);
        actionsHorizontalLayout.setSpacing(true);
        actionsHorizontalLayout.addComponents(addButton, editButton, deleteButton);


        HorizontalLayout addDelegationHorizontalLayout1 = new HorizontalLayout();
        addDelegationHorizontalLayout1.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);

        HorizontalLayout addDelegationHorizontalLayout2 = new HorizontalLayout();
        addDelegationHorizontalLayout2.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);

        HorizontalLayout addDelegationHorizontalLayout3 = new HorizontalLayout();
        addDelegationHorizontalLayout3.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);

        TextField descriptionTextField = new TextField("Description");
        DateField dateTimeStartDateField = new DateField("Start date");
        DateField dateTimeStopDateField = new DateField("Stop date");
        TextField travelDietAmountTextField = new TextField("Travel diet amount");
        TextField breakfastNumberTextField = new TextField("Number of breakfasts");
        TextField dinnerNumberTextField = new TextField("Number of dinners");
        TextField supperNumberTextField = new TextField("Number of suppers");

        ComboBox<TransportType> transportTypeComboBox =
                new ComboBox<>("Transport type");
        transportTypeComboBox.setItems(TransportType.values());

        TextField ticketPriceTextField = new TextField("Ticket price");

        ComboBox<AutoCapacity> autoCapacityComboBox =
                new ComboBox<>("Auto capacity");
        autoCapacityComboBox.setItems(AutoCapacity.values());

        TextField kmTextField = new TextField("Km");
        TextField accommodationPriceTextField = new TextField("Accommodation price");
        TextField otherOutlayDescTextField = new TextField("Other outlay desc");
        TextField otherOutlayPriceTextField = new TextField("Other outlay price");

        delegationGrid.addSelectionListener(event -> {
            if (event.getAllSelectedItems().size() == 1) {
                Delegation delegation = event.getFirstSelectedItem().get();

                descriptionTextField.setValue(delegation.getDescription());
                dateTimeStartDateField.setValue(delegation.getDateTimeStart());
                dateTimeStopDateField.setValue(delegation.getDateTimeStop());
                travelDietAmountTextField.setValue(delegation.getTravelDietAmount() + "");
                breakfastNumberTextField.setValue(delegation.getBreakfastNumber() + "");
                dinnerNumberTextField.setValue(delegation.getDinnerNumber() + "");
                supperNumberTextField.setValue(delegation.getSupperNumber() + "");
                transportTypeComboBox.setValue(delegation.getTransportType());
                ticketPriceTextField.setValue(delegation.getTicketPrice() + "");
                autoCapacityComboBox.setValue(delegation.getAutoCapacity());
                kmTextField.setValue(delegation.getKm() + "");
                accommodationPriceTextField.setValue(delegation.getAccommodationPrice() + "");
                otherOutlayDescTextField.setValue(delegation.getOtherOutlayDesc() + "");
                otherOutlayPriceTextField.setValue(delegation.getOtherOutlayPrice() + "");
            }
        });

        addButton.setWidth("100");
        addButton.addClickListener(clickEvent -> {
                    try {
                        Delegation delegation = new Delegation(
                                descriptionTextField.getValue(),
                                dateTimeStartDateField.getValue(),
                                dateTimeStopDateField.getValue(),
                                Double.parseDouble(travelDietAmountTextField.getValue()),
                                Integer.parseInt(breakfastNumberTextField.getValue()),
                                Integer.parseInt(dinnerNumberTextField.getValue()),
                                Integer.parseInt(supperNumberTextField.getValue()),
                                transportTypeComboBox.getValue(),
                                Double.parseDouble(ticketPriceTextField.getValue()),
                                autoCapacityComboBox.getValue(),
                                Double.parseDouble(kmTextField.getValue()),
                                Double.parseDouble(accommodationPriceTextField.getValue()),
                                Double.parseDouble(otherOutlayDescTextField.getValue()),
                                Double.parseDouble(otherOutlayPriceTextField.getValue())
                        );

                        delegation.addUser(chosenUser);

                        delegation = delegationService.createDelegation(delegation);
                        delegationList.add(delegation);
                        gridProvider.refreshAll();

                        Notification.show("Delegation has been successfully added.", "",
                                Notification.Type.HUMANIZED_MESSAGE);
                    } catch (Exception e) {
                        Notification.show("Wrong data to added delegation!", "",
                                Notification.Type.ERROR_MESSAGE);
                        e.printStackTrace();
                    }
                }
        );

        editButton.setWidth("100");
        editButton.addClickListener(clickEvent -> {
                    if (delegationGrid.getSelectedItems().size() == 1) {
                        Delegation delegation = delegationGrid.getSelectedItems().iterator().next();

                        if (LocalDate.now().isBefore(delegation.getDateTimeStart())) {
                            try {
                                delegation.setDescription(descriptionTextField.getValue());
                                delegation.setDateTimeStart(dateTimeStartDateField.getValue());
                                delegation.setDateTimeStop(dateTimeStopDateField.getValue());
                                delegation.setTravelDietAmount(Double.parseDouble(travelDietAmountTextField.getValue()));
                                delegation.setBreakfastNumber(Integer.parseInt(breakfastNumberTextField.getValue()));
                                delegation.setDinnerNumber(Integer.parseInt(dinnerNumberTextField.getValue()));
                                delegation.setSupperNumber(Integer.parseInt(supperNumberTextField.getValue()));
                                delegation.setTransportType(transportTypeComboBox.getValue());
                                delegation.setTicketPrice(Double.parseDouble(ticketPriceTextField.getValue()));
                                delegation.setAutoCapacity(autoCapacityComboBox.getValue());
                                delegation.setKm(Double.parseDouble(kmTextField.getValue()));
                                delegation.setAccommodationPrice(Double.parseDouble(accommodationPriceTextField.getValue()));
                                delegation.setOtherOutlayDesc(Double.parseDouble(otherOutlayDescTextField.getValue()));
                                delegation.setOtherOutlayPrice(Double.parseDouble(otherOutlayPriceTextField.getValue()));

                                delegationList.remove(delegation);
                                delegation = delegationService.updateDelegation(delegation);
                                delegationList.add(delegation);
                                gridProvider.refreshAll();

                                Notification.show("Delegation has been successfully edited.", "",
                                        Notification.Type.HUMANIZED_MESSAGE);

                            } catch (Exception e) {
                                Notification.show("Wrong data to edit delegation!", "",
                                        Notification.Type.ERROR_MESSAGE);
                                e.printStackTrace();
                            }
                        } else {
                            Notification.show("This delegation has taken place. You can not edit it!", "",
                                    Notification.Type.ERROR_MESSAGE);
                        }
                    } else {
                        Notification.show("Select delegation to edit!", "",
                                Notification.Type.ERROR_MESSAGE);
                    }
                }
        );


        addDelegationHorizontalLayout1.addComponents(descriptionTextField, dateTimeStartDateField,
                dateTimeStopDateField, travelDietAmountTextField);

        addDelegationHorizontalLayout2.addComponents(breakfastNumberTextField, dinnerNumberTextField,
                supperNumberTextField, transportTypeComboBox, ticketPriceTextField);

        addDelegationHorizontalLayout3.addComponents(autoCapacityComboBox, kmTextField,
                accommodationPriceTextField, otherOutlayDescTextField, otherOutlayPriceTextField);

        addComponents(userHorizontalLayout, delegationGrid, actionsHorizontalLayout, addDelegationHorizontalLayout1,
                addDelegationHorizontalLayout2, addDelegationHorizontalLayout3);

    }

}