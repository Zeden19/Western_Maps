package cs2212.westernmaps.login;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.UIManager;

/**
 * A Java Swing button with custom styles to make it look like a link. Used in {@link LoginPanel} and
 * {@link CreateAccountPanel}.
 *
 * @author Connor Cummings
 */
public final class LinkButton extends JButton {
    /**
     * This constructor creates the link Button for the application
     * @param text the text that is being added to the link button
     */
    public LinkButton(String text) {
        super(text);

        // Remove the button's regular border and padding.
        setBorder(null);
        setMargin(new Insets(0, 0, 0, 0));
        // Remove the button's regular background.
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));

        // Style the button's text to look like a link.
        // TODO: Make it possible to distinguish hovered and focused states.
        setForeground(UIManager.getColor("Component.linkColor"));
        setUnderlined();

        // Show the "pointing" mouse cursor when hovering over the button.
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    // setting the underline for the text in the panel to add a nicer look to panel
    private void setUnderlined() {
        var font = getFont();
        var attributes = new HashMap<TextAttribute, Object>(font.getAttributes());
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        setFont(font.deriveFont(attributes));
    }
}
