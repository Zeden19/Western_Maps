package cs2212.westernmaps;

import cs2212.westernmaps.core.Account;
import cs2212.westernmaps.core.Database;
import cs2212.westernmaps.core.DatabaseState;
import cs2212.westernmaps.help.AboutAction;
import cs2212.westernmaps.help.HelpWindow;
import cs2212.westernmaps.login.CreateAccountPanel;
import cs2212.westernmaps.login.LoginPanel;
import cs2212.westernmaps.maps.MapPanel;
import cs2212.westernmaps.select.BuildingSelectPanel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.swing.*;

/**
 * This class creates the main window for the application.
 * Through this class, we switch between the different panels, such as building select, create account login, and map.
 * @author Arjun Sharma
 * @author Connor Cummings
 */
public final class MainWindow extends JFrame {

    // All the panels, used to switch between them.
    private final LoginPanel loginPanel;
    private final CreateAccountPanel createAccountPanel;
    private final BuildingSelectPanel buildingSelectPanel;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    // The current logged in account, to activate/restrict developer features
    private @Nullable Account loggedInAccount = null;

    /**
     * Creates the Main window
     * @param database the database being used for the application
     */
    public MainWindow(Database database) {
        super("Sign in");

        // Create a card layout to allow switching between panels.
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // creating all the panels
        loginPanel = new LoginPanel(database);
        createAccountPanel = new CreateAccountPanel(database);
        buildingSelectPanel = new BuildingSelectPanel(database.getCurrentState().buildings());

        // log in to create account
        loginPanel.addCreateAccountClickListener(() -> changeTo(createAccountPanel));

        // change to building select panel
        loginPanel.addLoginListener(account -> {
            loggedInAccount = account;
            changeTo(buildingSelectPanel);
        });

        // create account to log in, once account has been made
        createAccountPanel.addAccountCreateListener(account -> {
            List<Account> accounts = new ArrayList<>(database.getCurrentState().accounts());
            accounts.add(account);
            database.getHistory()
                    .pushState(new DatabaseState(
                            accounts,
                            database.getCurrentState().buildings(),
                            database.getCurrentState().pois()));
            try {
                database.save();
            } catch (IOException ex) {
                System.out.println("Error: Couldn't save new account to the database.");
            }
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
                throw new IllegalStateException("No account is logged in.");
            }

            // glass pane used for the search bar being overlay on the map screen
            setGlassPane(new JComponent() {});

            Container glassPane = (Container) getGlassPane();
            var mapPanel = new MapPanel(database, building, loggedInAccount, glassPane);
            mapPanel.addBackListener(() -> changeTo(buildingSelectPanel));
            mapPanel.addBackListener(() -> {
                glassPane.setVisible(false);
                changeTo(buildingSelectPanel);
            });

            String developerModeSuffix = loggedInAccount.developer() ? " (Developer Mode)" : "";

            mapPanel.addBuildingChangeListeners(
                    newBuilding -> setTitle(Main.APPLICATION_NAME + ": " + newBuilding.name() + developerModeSuffix));

            changeTo(mapPanel);
        });

        // Start at the login screen.
        changeTo(loginPanel);
        setJMenuBar(createMenuBar());

        // setting up the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(cardPanel);
        setPreferredSize(new Dimension(1280, 720));
        pack();
    }

    // Allows switching between panels, including name formatting for the title of the page
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

    private JMenuBar createMenuBar() {
        JMenu helpMenu = new JMenu("Help");
        AboutAction aboutAction = new AboutAction(this);
        helpMenu.add(new HelpWindow.ShowAction());
        helpMenu.add(aboutAction);

        // Set about action for standard macOS about menu bar
        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.APP_ABOUT)) {
            desktop.setAboutHandler(e -> aboutAction.showAbout());
        }

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(helpMenu);

        return menuBar;
    }
}
