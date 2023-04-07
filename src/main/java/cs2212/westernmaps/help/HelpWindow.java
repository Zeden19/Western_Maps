package cs2212.westernmaps.help;

import cs2212.westernmaps.Main;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * The help window of the application used to display help pages.
 *
 * @author Connor Cummings
 */
public class HelpWindow extends JFrame {
    private static @Nullable HelpWindow openedWindow = null;

    private final JTree tree;
    private final JEditorPane viewer;
    // Creating the help page for the actual application

    /**
     * Creates the help window of the application.
     *
     * @param index An index of the help pages to display.
     */
    public HelpWindow(HelpPageIndex index) {
        super(Main.APPLICATION_NAME + " Help");

        var rootPage = index.getRootPage();

        var rootPageTitle = new JLabel(rootPage.getName());
        rootPageTitle.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        rootPageTitle.putClientProperty("FlatLaf.styleClass", "h2");
        rootPageTitle.setFocusable(true);
        rootPageTitle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rootPageTitle.addMouseListener(new MouseAdapter() {
            /**
             * This method helps for when a mouse click occurs
             * @param e the event to be processed
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                tree.clearSelection();
                showPage(rootPage);
            }
        });

        tree = new JTree(rootPage);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // https://stackoverflow.com/a/23095104
                var treePath = tree.getPathForLocation(e.getX(), e.getY());
                var cursorType = treePath != null ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR;
                tree.setCursor(Cursor.getPredefinedCursor(cursorType));
            }
        });

        viewer = new JEditorPane();
        viewer.setEditable(false);

        // Start by showing the root page.
        showPage(rootPage);
        // When a page is selected from the tree, show it.
        tree.addTreeSelectionListener(e -> {
            var page = (HelpPage) tree.getLastSelectedPathComponent();
            if (page == null) {
                page = rootPage;
            }
            showPage(page);
        });
        // When a link is clicked in the HTML viewer, follow it.
        viewer.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                var url = e.getURL();
                var page = index.getPageByContentURL(url);
                if (page != null) {
                    selectPageInTree(page);
                    showPage(page);
                }
            }
        });

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
        splitPane.setDividerLocation(250);

        var contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(splitPane, BorderLayout.CENTER);

        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(1200, 800));
        pack();
    }

    private void selectPageInTree(@Nullable HelpPage page) {
        // Determine the TreePath for the page by walking through its parents.
        // http://www.java2s.com/Code/Java/Swing-JFC/GettreepathfromTreeNode.htm
        TreeNode node = page;
        Deque<TreeNode> nodes = new ArrayDeque<>();
        while (node != null) {
            nodes.addFirst(node);
            node = node.getParent();
        }
        var path = new TreePath(nodes.toArray());
        // Then, select that path.
        tree.setSelectionPath(path);
    }

    private void showPage(HelpPage page) {
        try {
            viewer.setPage(page.getContentURL());
        } catch (IOException ex) {
            throw new InvalidHelpException(ex);
        }
    }

    /**
     * An action that opens the help window, creating one if it doesn't exist.
     *
     * @author Connor Cummings
     */
    public static class ShowAction extends AbstractAction {
        private static @Nullable HelpPageIndex index = null;

        /**
         * Creates a new help action.
         */
        public ShowAction() {
            super("Help");
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_H);
            putValue(Action.SHORT_DESCRIPTION, "Show the help browser.");
        }

        /**
         * Shows the help window to the user.
         *
         * <p>This method will either make a new help window or focuses an
         * existing one.</p>
         *
         * @param actionEvent The event that triggered the window to open.
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            // Load the help index and cache the result.
            if (index == null) {
                index = HelpPageIndex.loadFromResources();
            }

            if (openedWindow == null || !openedWindow.isVisible()) {
                // The help window is not already open. A new window is always
                // created here so that IntelliJ hot-reloading will take effect.
                openedWindow = new HelpWindow(Objects.requireNonNull(index));
                openedWindow.setVisible(true);
            } else {
                // The help window is already open.
                openedWindow.requestFocus();
            }
        }
    }
}
