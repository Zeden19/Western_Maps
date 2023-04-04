package cs2212.westernmaps.core.maps;

import java.awt.event.*;
import javax.swing.*;

/**
 * Add an Action to a JList that can be invoked either by using
 * the keyboard or a mouse.
 *
 * <p>By default, the Enter key will be used to invoke the Action from the
 * keyboard, although you can specify and KeyStroke you wish.</p>
 *
 * <p>A double click with the mouse will invoke the same Action.</p>
 *
 * @see <a href="https://tips4java.wordpress.com/2008/10/14/list-action/">Java Tips Weblog</a>
 */
public final class ListAction {
    private static final KeyStroke ENTER = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

    private ListAction() {}

    public static void setListAction(JList<?> list, Action action) {
        setListAction(list, action, ENTER);
    }

    public static void setListAction(JList<?> list, Action action, KeyStroke keyStroke) {
        // Add the KeyStroke to the InputMap.
        list.getInputMap().put(keyStroke, keyStroke);
        // Add the Action to the ActionMap.
        list.getActionMap().put(keyStroke, action);
        // Handle mouse double click.
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Action action = list.getActionMap().get(keyStroke);

                    if (action != null) {
                        ActionEvent event = new ActionEvent(list, ActionEvent.ACTION_PERFORMED, "");
                        action.actionPerformed(event);
                    }
                }
            }
        });
    }
}
