package cs2212.mapmaker.help;

import cs2212.mapmaker.Main;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.annotation.Nullable;
import javax.swing.*;

public class HelpWindow extends JFrame {
    private static @Nullable HelpWindow openedWindow = null;

    public HelpWindow() {
        super(Main.APPLICATION_NAME + " Help");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            // Load the page index and get the root page.
            // TODO: Show a loading indicator instead of blocking the UI thread
            //       if this becomes too slow.
            var rootPage = HelpPage.getRootPage();

            var rootPageTitle = new JLabel(rootPage.getName());
            rootPageTitle.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
            rootPageTitle.putClientProperty("FlatLaf.styleClass", "h2");

            var tree = new JTree(rootPage);
            tree.setRootVisible(false);
            tree.setShowsRootHandles(true);

            // Fully expand the tree. This works because expanding a row adds
            // more rows to the tree, causing the loop to go on for longer.
            // https://stackoverflow.com/a/15211697
            for (var row = 0; row < tree.getRowCount(); row++) {
                tree.expandRow(row);
            }

            var treePanel = new JPanel();
            treePanel.setLayout(new BorderLayout());
            treePanel.setBackground(tree.getBackground());
            treePanel.add(rootPageTitle, BorderLayout.PAGE_START);
            treePanel.add(tree, BorderLayout.CENTER);

            var viewer = new JEditorPane();
            viewer.setEditable(false);
            viewer.setPage(rootPage.getContentURL());

            var treeScrollPane = new JScrollPane(treePanel);
            treeScrollPane.setBorder(null);
            treeScrollPane.setMinimumSize(new Dimension(50, 0));
            treeScrollPane.setPreferredSize(new Dimension(200, 0));

            var viewerScrollPane = new JScrollPane(viewer);
            treeScrollPane.setMinimumSize(new Dimension(100, 0));
            viewerScrollPane.setBorder(null);

            var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, viewerScrollPane);
            splitPane.setResizeWeight(0.0);
            // Make sure the tree panel is given enough space. The split pane is
            // supposed to respond to the widget's preferred size, but it
            // doesn't for some reason.
            splitPane.setDividerLocation(200);

            var contentPane = new JPanel();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(splitPane, BorderLayout.CENTER);

            setContentPane(contentPane);
        } catch (IOException ex) {
            // TODO: Show an error message to the user saying that the help
            //       could not be loaded.
            throw new RuntimeException(ex);
        }

        setPreferredSize(new Dimension(800, 600));
        pack();
    }

    public static class ShowAction extends AbstractAction {
        public ShowAction() {
            super("Help");
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_H);
            putValue(Action.SHORT_DESCRIPTION, "Show the help browser.");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (openedWindow == null) {
                // This is the first time the help window has been opened.
                openedWindow = new HelpWindow();
                openedWindow.setVisible(true);
            } else if (!openedWindow.isVisible()) {
                // The help window has been opened and then closed before.
                // pack() must be called here because the window is disposed.
                openedWindow.pack();
                openedWindow.setVisible(true);
            } else {
                // The help window is already open.
                openedWindow.requestFocus();
            }
        }
    }
}
