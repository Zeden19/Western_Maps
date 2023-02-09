package cs2212.mapmaker.help;

import cs2212.mapmaker.Main;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.*;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class HelpWindow extends JFrame {
    private static @Nullable HelpWindow openedWindow = null;

    private final JTree tree;
    private final JEditorPane viewer;

    public HelpWindow(HelpPage rootPage) {
        super(Main.APPLICATION_NAME + " Help");

        var rootPageTitle = new JLabel(rootPage.getName());
        rootPageTitle.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        rootPageTitle.putClientProperty("FlatLaf.styleClass", "h2");

        tree = new JTree(rootPage);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

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
        splitPane.setDividerLocation(200);

        var contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(splitPane, BorderLayout.CENTER);

        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
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
        var path = new TreePath(nodes);
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

    public static class ShowAction extends AbstractAction {
        private static @Nullable HelpPage rootPage = null;

        public ShowAction() {
            super("Help");
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_H);
            putValue(Action.SHORT_DESCRIPTION, "Show the help browser.");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            // Load the help index and cache the result.
            if (rootPage == null) {
                rootPage = HelpPage.loadPageIndex();
            }

            if (openedWindow == null || !openedWindow.isVisible()) {
                // The help window is not already open. A new window is always
                // created here so that IntelliJ hot-reloading will take effect.
                openedWindow = new HelpWindow(Objects.requireNonNull(rootPage));
                openedWindow.setVisible(true);
            } else {
                // The help window is already open.
                openedWindow.requestFocus();
            }
        }
    }
}
