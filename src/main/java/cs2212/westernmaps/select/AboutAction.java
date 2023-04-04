package cs2212.westernmaps.select;

import cs2212.westernmaps.Main;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class AboutAction extends AbstractAction {
    private static final String VERSION = "v1.0";
    private static final String RELEASE_DATE = "April 9th, 2023";
    private static final String[] AUTHORS = {
        "Arjun Sharma", "Connor Cummings", "Christopher Brent Hosang", "Randeep Singh Bhalla", "Royal Gok Lun Cheung"
    };
    private static final String[] USERNAMES = {"ashar562", "ccummi26", "chosang3", "rbhall3", "rcheun52"};
    private final Component parent;

    public AboutAction(Component parent) {
        super("About " + Main.APPLICATION_NAME);
        this.parent = parent;
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JOptionPane.showMessageDialog(
                parent, aboutMessage(), "About " + Main.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE);
    }

    private static String aboutMessage() {
        String message =
                "Western Maps is a geographic information application for browsing maps of Western's buildings.\n\n"
                        + VERSION
                        + "\n" + "Released on "
                        + RELEASE_DATE + "\n" + "Created by: ";

        for (int i = 0; i < AUTHORS.length; i++) {
            message += AUTHORS[i] + " | " + USERNAMES[i] + "@uwo.ca\n";
        }

        return message;
    }
}
