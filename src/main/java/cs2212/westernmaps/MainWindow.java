package cs2212.westernmaps;

import cs2212.westernmaps.core.Account;
import cs2212.westernmaps.core.Database;
import cs2212.westernmaps.login.CreateAccountPanel;
import cs2212.westernmaps.login.LoginPanel;
import cs2212.westernmaps.maps.MapPanel;
import cs2212.westernmaps.select.BuildingSelectPanel;
import java.awt.*;
import javax.annotation.Nullable;
import javax.swing.*;

public final class MainWindow extends JFrame {
    private final LoginPanel loginPanel;
    private final CreateAccountPanel createAccountPanel;
    private final BuildingSelectPanel buildingSelectPanel;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;
    private @Nullable Account loggedInAccount = null;

    public MainWindow(Database database) {
        super("Sign in");

        // Create a card layout to allow switching between panels.
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // creating all the panels
        loginPanel = new LoginPanel(database);
        createAccountPanel = new CreateAccountPanel();
        buildingSelectPanel = new BuildingSelectPanel(database.getCurrentState().buildings());

        // navigation between panels

        // log in to create account
        loginPanel.addCreateAccountClickListener(() -> changeTo(createAccountPanel));

        // change to building select panel
        loginPanel.addLoginListener(account -> {
            loggedInAccount = account;
            changeTo(buildingSelectPanel);
        });

        // create account to log in, once account has been made
        createAccountPanel.addAccountCreateListener(account -> {
            // TODO: Add the created account to the database.
            changeTo(loginPanel);
        });

        // create account to log in, if user decides not to create account
        createAccountPanel.addBackListener(() -> {
            loggedInAccount = null;
            changeTo(loginPanel);
        });

        // building select to log in screen (log out)
        buildingSelectPanel.addLogOutListener(() -> {
            loggedInAccount = null;
            changeTo(loginPanel);
        });

        // building select to map
        buildingSelectPanel.addBuildingSelectListener(building -> {
            if (loggedInAccount == null) {
                return;
            }
            var mapPanel = new MapPanel(building, loggedInAccount);
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
