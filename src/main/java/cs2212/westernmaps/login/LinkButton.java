package cs2212.westernmaps.login;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.UIManager;

/**
 * A Java Swing button with custom styles to make it look like a link.
 */
public final class LinkButton extends JButton {
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

    private void setUnderlined() {
        var font = getFont();
        var attributes = new HashMap<TextAttribute, Object>(font.getAttributes());
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        setFont(font.deriveFont(attributes));
    }
}
