package cs2212.westernmaps;

import cs2212.westernmaps.core.Building;
import cs2212.westernmaps.core.Floor;
import cs2212.westernmaps.login.CreateAccountPanel;
import cs2212.westernmaps.login.LoginPanel;
import cs2212.westernmaps.maps.MapPanel;
import cs2212.westernmaps.select.BuildingSelect;
import java.awt.*;
import java.nio.file.*;
import java.util.ArrayList;
import javax.swing.*;

public final class MainWindow extends JFrame {

    private LoginPanel loginPanel; // the log in panel, the starting location of app
    private CreateAccountPanel createAccountPanel; // the create account panel
    private BuildingSelect buildingSelect; // the building select panel
    private MapPanel mapPanel; // the map viewer panel
    private JPanel cardPanel; // the panel that holds all the above panels

    private final CardLayout cardLayout;

    public MainWindow() {
        super("Sign in");

        // delete this once database is implemented
        Floor floor1 = new Floor("g1", "Ground", Path.of("resources"));
        ArrayList<Floor> floors = new ArrayList<>();
        floors.add(floor1);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // creating all the panels
        loginPanel = new LoginPanel();
        createAccountPanel = new CreateAccountPanel();
        buildingSelect = new BuildingSelect();
        mapPanel = new MapPanel(new Building("TEST", floors));

        // adding all panels to the card panel
        cardPanel.add(loginPanel, "login");
        cardPanel.add(createAccountPanel, "create");
        cardPanel.add(buildingSelect, "building");
        cardPanel.add(mapPanel, "map");

        // navigation between panels
        loginPanel.getCreateAccountLink().addActionListener(e -> changeToCreateAccount());
        loginPanel.getSignInButton().addActionListener(e -> changeToBuildingSelect());
        createAccountPanel.getCreateAccountButton().addActionListener(e -> changeToLoginFromCreate());
        createAccountPanel.getBackButton().addActionListener(e -> changeToLogin());
        buildingSelect.getBackButton().addActionListener(e -> changeToLogin());
        buildingSelect.getSelectButton().addActionListener(e -> changeToMap());
        mapPanel.getBackButton().addActionListener(e -> changeToBuildingSelect());

        // setting up the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(cardPanel);
        setPreferredSize(new Dimension(1280, 720));
        pack();
    }

    // Changing to account
    public void changeToCreateAccount() {
        setTitle("Create Account");
        cardLayout.show(cardPanel, "create");
    }

    // Changing to log in
    public void changeToLogin() {
        setTitle("Sign in");
        cardLayout.show(cardPanel, "login");
    }

    // changing to log-in from the create account screen
    public void changeToLoginFromCreate() {
        if (!createAccountPanel.checkValidCreate()) return;
        setTitle("Sign in");
        cardLayout.show(cardPanel, "login");
    }

    // changing to the building select screen
    public void changeToBuildingSelect() {
        if (!loginPanel.checkValidLogin()) return;

        if (loginPanel.getIsDeveloper()) {
            setTitle("Developer Mode: Select Building");
        } else {
            setTitle("Select Building");
        }

        // changing the layout
        cardLayout.show(cardPanel, "building");
    }

    // changing to the map
    public void changeToMap() {
        if (!buildingSelect.checkValidSelection()) return;

        if (loginPanel.getIsDeveloper()) {
            setTitle("Developer Mode: Map");
        } else {
            setTitle("Map");
        }

        cardLayout.show(cardPanel, "map");
    }
}
