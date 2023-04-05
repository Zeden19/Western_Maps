package cs2212.westernmaps.help;

import cs2212.westernmaps.Main;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;

/**
 * About action for use with the "About" message dialog.
 */
public class AboutAction extends AbstractAction {
    private static final String VERSION = "v1.0";
    private static final String RELEASE_DATE = "April 9th, 2023";
    private static final String[] AUTHORS = {
        "Arjun Sharma", "Connor Cummings", "Christopher Brent Hosang", "Randeep Singh Bhalla", "Royal Gok Lun Cheung"
    };
    private static final String[] USERNAMES = {"ashar562", "ccummi26", "chosang3", "rbhall3", "rcheun52"};
    private final Component parent;

    /**
     * Construct a new AboutAction.
     * @param parent Component creating this action.
     */
    public AboutAction(Component parent) {
        super("About " + Main.APPLICATION_NAME);
        this.parent = parent;
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        showAbout();
    }

    /**
     * Show the About message dialog.
     */
    public void showAbout() {
        JOptionPane.showMessageDialog(
                parent, aboutMessage(), "About " + Main.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
    }

    private static String aboutMessage() {
        StringBuilder message = new StringBuilder();

        message.append(
                        "Western Maps is a geographic information application for browsing maps of Western's buildings.\n\n")
                .append(VERSION)
                .append("\n")
                .append("Released on ")
                .append(RELEASE_DATE)
                .append("\n" + "Created by: ");

        for (int i = 0; i < AUTHORS.length; i++) {
            message.append(AUTHORS[i]).append(" | ").append(USERNAMES[i]).append("@uwo.ca\n");
        }

        return message.toString();
    }
}
