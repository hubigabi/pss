package pl.utp.pss.ui;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import pl.utp.pss.model.Delegation;
import pl.utp.pss.model.User;
import pl.utp.pss.service.DelegationService;
import pl.utp.pss.service.UserService;

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
        setSizeFull();

        Grid<Delegation> delegationGrid = new Grid<>();
        delegationGrid.addColumn(Delegation::getDescription).setCaption("Description");
        delegationGrid.addColumn(Delegation::getDateTimeStart).setCaption("Date start").setWidth(130);
        delegationGrid.addColumn(Delegation::getDateTimeStop).setCaption("Date stop").setWidth(130);
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

        delegationGrid.setWidth(1050f, Unit.PIXELS);
        delegationGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        List<Delegation> delegationList = delegationService.getAllByUserId(loggedUser.getId());
        ListDataProvider<Delegation> provider = DataProvider.ofCollection(delegationList);
        provider.setSortOrder(Delegation::getDateTimeStart,
                SortDirection.ASCENDING);
        delegationGrid.setDataProvider(provider);

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        Button addButton = new Button("Add");
        addButton.setWidth("100");
        addButton.addClickListener(clickEvent -> {
                }
        );

        Button editButton = new Button("Edit");
        editButton.setWidth("100");
        editButton.addClickListener(clickEvent -> {
                }
        );

        Button deleteButton = new Button("Delete");
        deleteButton.setWidth("100");
        deleteButton.addClickListener(clickEvent -> {
                    if (delegationGrid.getSelectedItems().size() == 1) {
                        Delegation delegation = delegationGrid.getSelectedItems().iterator().next();
                        delegation = delegationService.getDelegation(delegation.getId());
                        //loggedUser = userService.getUser(loggedUser.getId());

                        int indexDelegation = delegationList.indexOf(delegation);

                        delegation.removeUser(loggedUser);
                        delegationService.updateDelegation(delegation);
                        delegationService.deleteEmptyDelegation(delegation);

                        delegationList.remove(indexDelegation);
                        provider.refreshAll();

                        Notification.show("Delegation has been successfully deleted.",
                                "",
                                Notification.Type.HUMANIZED_MESSAGE);
                    } else {
                        Notification.show("Select delegation to delete!",
                                "",
                                Notification.Type.ERROR_MESSAGE);
                    }

                }
        );

        horizontalLayout.setMargin(true);
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponents(addButton, editButton, deleteButton);


        addComponents(delegationGrid, horizontalLayout);
    }
}
