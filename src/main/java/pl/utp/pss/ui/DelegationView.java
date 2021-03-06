package pl.utp.pss.ui;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import pl.utp.pss.model.*;
import pl.utp.pss.service.DelegationService;
import pl.utp.pss.service.UserService;

import java.time.LocalDate;
import java.util.List;

public class DelegationView extends VerticalLayout {

    private UserService userService;
    private DelegationService delegationService;
    private User loggedUser;

    @Autowired
    public DelegationView(UserService userService, DelegationService delegationService, long userId) {
        this.userService = userService;
        this.delegationService = delegationService;
        this.loggedUser = userService.getUser(userId);

        Grid<Delegation> delegationGrid = new Grid<>();
        delegationGrid.addColumn(Delegation::getStatus).setCaption("Status").setWidth(200);
        delegationGrid.addColumn(Delegation::getDescription).setCaption("Description");
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

        List<Delegation> delegationList = delegationService.getAllByUserId(loggedUser.getId());
        ListDataProvider<Delegation> provider = DataProvider.ofCollection(delegationList);
        provider.setSortOrder(Delegation::getDateTimeStart,
                SortDirection.ASCENDING);
        delegationGrid.setDataProvider(provider);

        HorizontalLayout actionsHorizontalLayout = new HorizontalLayout();

        Button addButton = new Button("Add");
        addButton.setWidth("100");

        Button editButton = new Button("Edit");
        editButton.setWidth("100");

        Button deleteButton = new Button("Delete");
        deleteButton.setWidth("100");
        deleteButton.addClickListener(clickEvent -> {
                    if (delegationGrid.getSelectedItems().size() == 1) {
                        Delegation delegation = delegationGrid.getSelectedItems().iterator().next();
                        if (!delegation.getStatus().equals(Status.ACCEPTED) &&
                                !delegation.getStatus().equals(Status.REQUEST_FROM_ACCEPTED_TO_NOT_ACCEPTED)) {
                            if (LocalDate.now().isBefore(delegation.getDateTimeStart())) {
                                int indexDelegation = delegationList.indexOf(delegation);

                                delegation.removeUser(loggedUser);
                                delegationService.updateDelegation(delegation);
                                delegationService.deleteEmptyDelegation(delegation);

                                delegationList.remove(indexDelegation);
                                provider.refreshAll();

                                Notification.show("Delegation has been successfully deleted.", "",
                                        Notification.Type.HUMANIZED_MESSAGE);
                            } else {
                                Notification.show("This delegation has taken place. You can not delete it!", "",
                                        Notification.Type.ERROR_MESSAGE);
                            }
                        } else {
                            Notification.show("You can not delete delegation, which was accepted.!", "",
                                    Notification.Type.ERROR_MESSAGE);
                        }
                    } else {
                        Notification.show("Select delegation to delete!", "",
                                Notification.Type.ERROR_MESSAGE);
                    }
                }
        );

        Button requestButton = new Button("Request");
        requestButton.setWidth("150");
        requestButton.setEnabled(false);
        requestButton.addClickListener(clickEvent -> {

            if (delegationGrid.getSelectedItems().size() == 1) {
                Delegation delegationBefore = delegationGrid.getSelectedItems().iterator().next();
                Delegation delegationAfter = delegationService.getDelegation(delegationBefore.getId());

                if (delegationAfter.getStatus().equals(Status.NOT_ACCEPTED)) {
                    delegationAfter.setStatus(Status.REQUEST_FROM_NOT_ACCEPTED_TO_ACCEPTED);
                } else if (delegationAfter.getStatus().equals(Status.ACCEPTED)) {
                    delegationAfter.setStatus(Status.REQUEST_FROM_ACCEPTED_TO_NOT_ACCEPTED);
                } else if (delegationAfter.getStatus().equals(Status.REQUEST_FROM_NOT_ACCEPTED_TO_ACCEPTED)) {
                    delegationAfter.setStatus(Status.NOT_ACCEPTED);
                } else if (delegationAfter.getStatus().equals(Status.REQUEST_FROM_ACCEPTED_TO_NOT_ACCEPTED)) {
                    delegationAfter.setStatus(Status.ACCEPTED);
                }


                delegationAfter = delegationService.updateDelegation(delegationAfter);

                delegationList.remove(delegationBefore);
                delegationList.add(delegationAfter);

                provider.refreshAll();

                Notification.show("The status of the delegation has been successfully changed.", "",
                        Notification.Type.HUMANIZED_MESSAGE);

                delegationGrid.select(delegationAfter);
            } else {
                Notification.show("Select delegation to edit!", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });

        Button generatePDFButton = new Button("Report");
        generatePDFButton.setEnabled(false);
        generatePDFButton.setWidth("100");
        generatePDFButton.addClickListener(clickEvent -> {

            if (delegationGrid.getSelectedItems().size() == 1) {
                Delegation delegation = delegationGrid.getSelectedItems().iterator().next();
                getUI().getPage().open("/api/delegation/report/" + delegation.getId(), "_blank");
            } else {
                Notification.show("Select delegation to edit!", "",
                        Notification.Type.ERROR_MESSAGE);
            }
        });

        actionsHorizontalLayout.setMargin(true);
        actionsHorizontalLayout.setSpacing(true);
        actionsHorizontalLayout.addComponents(addButton, editButton, deleteButton, requestButton, generatePDFButton);


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

                requestButton.setEnabled(true);
                generatePDFButton.setEnabled(true);

                if (delegation.getStatus().equals(Status.ACCEPTED) ||
                        delegation.getStatus().equals(Status.NOT_ACCEPTED)) {
                    requestButton.setCaption("Make request");
                } else if (delegation.getStatus().equals(Status.REQUEST_FROM_ACCEPTED_TO_NOT_ACCEPTED) ||
                        delegation.getStatus().equals(Status.REQUEST_FROM_NOT_ACCEPTED_TO_ACCEPTED)) {
                    requestButton.setCaption("Cancel request");
                }

            }
        });

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

                        delegation.addUser(loggedUser);

                        delegation = delegationService.createDelegation(delegation);
                        delegationList.add(delegation);
                        provider.refreshAll();

                        delegationGrid.select(delegation);
                        Notification.show("Delegation has been successfully added.", "",
                                Notification.Type.HUMANIZED_MESSAGE);
                    } catch (Exception e) {
                        Notification.show("Wrong data to added delegation!", "",
                                Notification.Type.ERROR_MESSAGE);
                        e.printStackTrace();
                    }
                }
        );

        editButton.addClickListener(clickEvent -> {
                    if (delegationGrid.getSelectedItems().size() == 1) {
                        Delegation delegationBefore = delegationGrid.getSelectedItems().iterator().next();
                        Delegation delegationAfter = delegationService.getDelegation(delegationBefore.getId());

                        if (!delegationAfter.getStatus().equals(Status.ACCEPTED) &&
                                !delegationAfter.getStatus().equals(Status.REQUEST_FROM_ACCEPTED_TO_NOT_ACCEPTED)) {
                            if (LocalDate.now().isBefore(delegationAfter.getDateTimeStart())) {
                                try {
                                    delegationAfter.setDescription(descriptionTextField.getValue());
                                    delegationAfter.setDateTimeStart(dateTimeStartDateField.getValue());
                                    delegationAfter.setDateTimeStop(dateTimeStopDateField.getValue());
                                    delegationAfter.setTravelDietAmount(Double.parseDouble(travelDietAmountTextField.getValue()));
                                    delegationAfter.setBreakfastNumber(Integer.parseInt(breakfastNumberTextField.getValue()));
                                    delegationAfter.setDinnerNumber(Integer.parseInt(dinnerNumberTextField.getValue()));
                                    delegationAfter.setSupperNumber(Integer.parseInt(supperNumberTextField.getValue()));
                                    delegationAfter.setTransportType(transportTypeComboBox.getValue());
                                    delegationAfter.setTicketPrice(Double.parseDouble(ticketPriceTextField.getValue()));
                                    delegationAfter.setAutoCapacity(autoCapacityComboBox.getValue());
                                    delegationAfter.setKm(Double.parseDouble(kmTextField.getValue()));
                                    delegationAfter.setAccommodationPrice(Double.parseDouble(accommodationPriceTextField.getValue()));
                                    delegationAfter.setOtherOutlayDesc(Double.parseDouble(otherOutlayDescTextField.getValue()));
                                    delegationAfter.setOtherOutlayPrice(Double.parseDouble(otherOutlayPriceTextField.getValue()));

                                    delegationList.remove(delegationBefore);
                                    delegationAfter = delegationService.updateDelegation(delegationAfter);
                                    delegationList.add(delegationAfter);
                                    provider.refreshAll();

                                    delegationGrid.select(delegationAfter);

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
                            Notification.show("You can not edit delegation, which was accepted.!", "",
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

        addComponents(delegationGrid, actionsHorizontalLayout, addDelegationHorizontalLayout1,
                addDelegationHorizontalLayout2, addDelegationHorizontalLayout3);
    }
}