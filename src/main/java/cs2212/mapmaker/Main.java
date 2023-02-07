package cs2212.mapmaker;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import javax.swing.SwingUtilities;

public final class Main {
    public static final String APPLICATION_NAME = "Mapmaker";

    private Main() {}

    public static void main(String... args) {
        // TODO: Load this from a user preferences file.
        boolean darkTheme = false;

        // Set up the FlatLaf theme. This must be done before creating the UI.
        setPropertiesForMacOs(darkTheme);
        if (darkTheme) FlatDarkLaf.setup();
        else FlatLightLaf.setup();

        // Enable the FlatLaf widget inspector if requested via a Java system
        // property.
        if (Boolean.getBoolean("cs2212.mapmaker.enableInspector")) {
            FlatInspector.install("ctrl shift alt X");
            FlatUIDefaultsInspector.install("ctrl shift alt Y");
        }

        // Create and show the main window.
        SwingUtilities.invokeLater(() -> {
            var window = new MainWindow();
            window.setVisible(true);
        });
    }

    /**
     * Set some Java system properties that visually integrate the application
     * with macOS.
     *
     * <p>In order for the properties to be correctly recognized by macOS, this
     * method must be called on the main thread before any Java Swing components
     * are created.</p>
     *
     * @param darkTheme Whether to use a dark theme for window title bars.
     */
    private static void setPropertiesForMacOs(boolean darkTheme) {
        // Use the macOS menu bar instead of creating one inside the window.
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        // Set the application name that appears in the macOS menu bar.
        System.setProperty("apple.awt.application.name", APPLICATION_NAME);

        // Tell macOS which theme to use for the window title bar. This cannot
        // be changed after the UI has been created.
        var appearance = darkTheme ? "NSAppearanceNameDarkAqua" : "NSAppearanceNameAqua";
        System.setProperty("apple.awt.application.appearance", appearance);
    }
}
