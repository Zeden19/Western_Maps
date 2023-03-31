package cs2212.westernmaps;

import cs2212.westernmaps.core.Account;
import cs2212.westernmaps.core.Building;
import cs2212.westernmaps.core.Database;
import cs2212.westernmaps.core.Floor;
import cs2212.westernmaps.login.CreateAccountPanel;
import cs2212.westernmaps.login.LoginPanel;
import cs2212.westernmaps.maps.MapPanel;
import cs2212.westernmaps.select.BuildingSelectPanel;
import java.awt.*;
import java.nio.file.*;
import java.util.List;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

public final class MainWindow extends JFrame {
    private final LoginPanel loginPanel;
    private final CreateAccountPanel createAccountPanel;
    private final BuildingSelectPanel buildingSelectPanel;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    private @Nullable Account loggedInAccount = null;
    Database database;

    public MainWindow(Database database) {
        super("Sign in");
        this.database = database;

        // delete this once database is implemented
        var floors = List.of(new Floor("g1", "Ground", Path.of("resources")));
        var buildings = List.of(
                new Building("Middlesex College", floors),
                new Building("Talbot College", floors),
                new Building("Western Student Recreation Centre", floors));

        // Create a card layout to allow switching between panels.
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // creating all the panels
        loginPanel = new LoginPanel();
        createAccountPanel = new CreateAccountPanel();
        buildingSelectPanel = new BuildingSelectPanel(buildings);

        // navigation between panels
        loginPanel.addCreateAccountClickListener(() -> changeTo(createAccountPanel));
        loginPanel.addLoginListener(account -> {
            loggedInAccount = account;
            changeTo(buildingSelectPanel);
        });

        createAccountPanel.addAccountCreateListener(account -> {
            // TODO: Add the created account to the database.
            changeTo(loginPanel);
        });

        createAccountPanel.addBackListener(() -> {
            changeTo(loginPanel);
            loggedInAccount = null;
        });

        buildingSelectPanel.addLogOutListener(() -> {
            loggedInAccount = null;
            changeTo(loginPanel);
        });
        buildingSelectPanel.addBuildingSelectListener(building -> {
            var mapPanel = new MapPanel(building);
            mapPanel.addBackListener(() -> changeTo(buildingSelectPanel));
            changeTo(mapPanel);
        });

        // Start at the login screen.
        changeTo(loginPanel);

        // setting up the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(cardPanel);
        setPreferredSize(new Dimension(1280, 720));
        pack();
    }

    private void changeTo(JPanel panel) {
        var titleBuilder = new StringBuilder();
        titleBuilder.append(Main.APPLICATION_NAME);
        titleBuilder.append(": ");
        titleBuilder.append(panel.getName());
        if (loggedInAccount != null && loggedInAccount.developer()) {
            titleBuilder.append(" (Developer Mode)");
        }

        setTitle(titleBuilder.toString());
        cardPanel.add(panel, "Current");
        cardLayout.show(cardPanel, "Current");

    }
}
