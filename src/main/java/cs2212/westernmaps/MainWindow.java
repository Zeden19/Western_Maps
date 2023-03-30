package cs2212.westernmaps;

import cs2212.westernmaps.core.Building;
import cs2212.westernmaps.core.Floor;
import cs2212.westernmaps.login.CreateAccountPanel;
import cs2212.westernmaps.login.LoginPanel;
import cs2212.westernmaps.maps.MapWindow;
import cs2212.westernmaps.select.BuildingSelect;
import java.awt.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;

public final class MainWindow extends JFrame {

    private LoginPanel loginPanel; // the log in panel, the starting location of app
    private CreateAccountPanel createAccountPanel; // the create account panel
    private BuildingSelect buildingSelect; // the building select panel
    private MapWindow mapPanel; // the map viewer panel
    private JPanel cardPanel; // the panel that holds all the above panels

    public MainWindow() {
        super("Sign in");

        // delete this once database is implemented
        Floor floor1 = new Floor("g1", "Ground", Paths.get("resources"));
        ArrayList<Floor> floors = new ArrayList<>();
        floors.add(floor1);

        // creating all the panels
        loginPanel = new LoginPanel();
        createAccountPanel = new CreateAccountPanel();
        buildingSelect = new BuildingSelect();
        cardPanel = new JPanel(new CardLayout());
        mapPanel = new MapWindow(new Building("TEST", floors));

        // adding all panels to the card panel
        cardPanel.add(loginPanel, "login");
        cardPanel.add(createAccountPanel, "create");
        cardPanel.add(buildingSelect, "building");
        cardPanel.add(mapPanel, "map");

        // navigation between panels
        loginPanel.getCreateAccountLink().addActionListener(e -> changeToCreateAccount());
        loginPanel.getSignInButton().addActionListener(e -> changeToBuildingSelect());
        createAccountPanel.getBackButton().addActionListener(e -> changeToLoginFromBuilding());
        createAccountPanel.getCreateAccountButton().addActionListener(e -> changeToLoginFromBuilding());
        createAccountPanel.getBackButton().addActionListener(e -> changeToLogin());
        buildingSelect.getBackButton().addActionListener(e -> changeToLogin());
        buildingSelect.getSelectButton().addActionListener(e -> changeToMap());
        mapPanel.getBackButton().addActionListener(e -> changeToBuildingSelect());

        // setting up the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(cardPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        pack();
    }

    public void changeToCreateAccount() {
        setTitle("Create Account");
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "create");
    }

    public void changeToLogin() {
        setTitle("Sign in");
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "login");
    }

    public void changeToBuildingSelect() {
        setTitle("Select Building");
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "building");
    }

    public void changeToMap() {

        // getting the building
        Floor floor1 = new Floor("g1", "Ground", Paths.get("resources"));
        ArrayList<Floor> floors = new ArrayList<>();
        floors.add(floor1);

        switch (buildingSelect.getList().getSelectedIndex()) {
            case 0 ->
            // Go to Middlesex College
            mapPanel = new MapWindow(new Building("Middlesex College", floors));
            case 1 ->
            // Go to Talbot College
            mapPanel = new MapWindow(new Building("Talbot College", floors));
            case 2 ->
            // Go to Recreation Centre
            mapPanel = new MapWindow(new Building("Rec centre", floors));
        }

        setTitle("Map");
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "map");
    }

    public void changeToLoginFromBuilding() {
        createAccountPanel.getPasswordMatchError().setVisible(false);
        createAccountPanel.getPasswordInvalidError().setVisible(false);

        // checking if the password was valid, if not displaying error
        if (!Arrays.equals(
                createAccountPanel.getPassword().getPassword(),
                createAccountPanel.getConfirmPassword().getPassword()))
            createAccountPanel.getPasswordMatchError().setVisible(true); // passwords not matching
        else if (!createAccountPanel.isPasswordValid(
                createAccountPanel.getPassword().getPassword())) // password not valid
        createAccountPanel.getPasswordInvalidError().setVisible(true);

        // todo add more checks

        // if all checks passed, change to login
        else {
            setTitle("Sign in");
            ((CardLayout) cardPanel.getLayout()).show(cardPanel, "login");
        }
    }
}
