package cs2212.westernmaps.login;

import cs2212.westernmaps.maps.MapWindow;
import cs2212.westernmaps.select.BuildingSelect;
import java.awt.*;
import java.util.Arrays;
import javax.swing.*;

public final class LoginWindow extends JFrame {

    private LoginPanel loginPanel; // the log in panel, the starting location of app
    private CreateAccountPanel createAccountPanel; // the create account panel
    private BuildingSelect buildingSelect; // the building select panel
    private MapWindow mapViewerPanel; // the map viewer panel
    private JPanel cardPanel; // the panel that holds all the above panels

    public LoginWindow() {
        super("Sign in");

        // creating all the panels
        loginPanel = new LoginPanel();
        createAccountPanel = new CreateAccountPanel();
        buildingSelect = new BuildingSelect();
        cardPanel = new JPanel(new CardLayout());
        mapViewerPanel = new MapWindow();

        // adding all panels to the card panel
        cardPanel.add(loginPanel, "login");
        cardPanel.add(createAccountPanel, "create");
        cardPanel.add(buildingSelect, "building");
        cardPanel.add(mapViewerPanel, "map");

        // navigation between panels
        loginPanel.getCreateAccountLink().addActionListener(e -> changeToCreateAccount());
        loginPanel.getSignInButton().addActionListener(e -> changeToBuildingSelect());
        createAccountPanel.getBackButton().addActionListener(e -> changeToLoginFromBuilding());
        createAccountPanel.getCreateAccountButton().addActionListener(e -> changeToLoginFromBuilding());
        createAccountPanel.getBackButton().addActionListener(e -> changeToLogin());
        buildingSelect.getBackButton().addActionListener(e -> changeToLogin());
        buildingSelect.getSelectButton().addActionListener(e -> changeToMap());
        mapViewerPanel.getBackButton().addActionListener(e -> changeToBuildingSelect());

        // setting up the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(cardPanel);
        setPreferredSize(new Dimension(1280, 720));
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
        switch (buildingSelect.getList().getSelectedIndex()) {
            case 0 ->
            // Go to Middlesex College
            System.out.println("Middlesex College selected.");
            case 1 ->
            // Go to Talbot College
            System.out.println("Talbot College selected.");
            case 2 ->
            // Go to Recreation Centre
            System.out.println("Recreation Centre selected.");
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
