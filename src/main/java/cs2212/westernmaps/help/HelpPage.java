package cs2212.westernmaps.help;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.annotation.Nullable;
import javax.swing.tree.TreeNode;

/**
 * This class is for the Help page that is implemented in the application.
 *
 * <p>It implements the {@link TreeNode} interface so it can be used with a
 * {@link javax.swing.JTree} component.</p>
 *
 * @author Connor Cummings
 */
public class HelpPage implements TreeNode {
    // declaring the classes private and public variables
    public static final String HELP_ROOT = "/cs2212/westernmaps/help";

    private final String name;
    private final List<HelpPage> subpages;

    private @Nullable HelpPage parent = null;

    /**
     * Creates a new help page.
     *
     * @param name     The name of the new help page.
     * @param subpages The list of subpages of the new help page.
     */
    @JsonCreator
    public HelpPage(@JsonProperty("name") String name, @JsonProperty("subpages") @Nullable List<HelpPage> subpages) {
        if (subpages == null) {
            subpages = List.of();
        }

        this.name = name;
        this.subpages = subpages;

        for (var subpage : subpages) {
            subpage.parent = this;
        }
    }

    /**
     * Gets the name of this help page.
     *
     * @return The name of this help page.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of subpages of this page.
     *
     * @return A list of help pages.
     */
    public List<HelpPage> getSubpages() {
        return subpages;
    }

    /**
     * Gets the parent page of this help page.
     *
     * @return A help page that is the parent of this help page.
     */
    public @Nullable HelpPage getParentPage() {
        return parent;
    }

    /**
     * Determines the content URL of this help page.
     *
     * <p>This is used to load the content of the help page later, and will
     * point to a Java resource.</p>
     *
     * @return The URL of the content of this help page.
     */
    public URL getContentURL() {
        var prefix = contentPathWithoutExtension();

        var fileOnlyURL = HelpPage.class.getResource(prefix + ".html");
        var directoryIndexURL = HelpPage.class.getResource(prefix + "/index.html");

        if (fileOnlyURL != null && directoryIndexURL != null) {
            throw new InvalidHelpException("Page '" + getName() + "' exists as both a directory and a file");
        } else if (fileOnlyURL != null) {
            return fileOnlyURL;
        } else if (directoryIndexURL != null) {
            return directoryIndexURL;
        } else {
            throw new InvalidHelpException("Page '" + getName() + "' does not exist");
        }
    }

    // return the path without the extension of the page
    private String contentPathWithoutExtension() {
        var parent = getParentPage();
        var parentDirectory = parent != null ? parent.contentPathWithoutExtension() : HELP_ROOT;
        return parentDirectory + "/" + getName();
    }

    /**
     * Getter to get the subpages of a page
     * @param i  which subpage to look at
     * @return the subpage at the current index that is pushed as the parameter
     */
    @Override
    public @Nullable TreeNode getChildAt(int i) {
        return getSubpages().get(i);
    }

    /**
     *Getter to get the amount of subpages
     * @return the number of subpages
     */
    @Override
    public int getChildCount() {
        return getSubpages().size();
    }

    /**
     * Getter for the parent page of a page
     * @return the parent page of the current page in the help page
     */
    @Override
    public @Nullable TreeNode getParent() {
        return getParentPage();
    }

    /**
     * getter for the index a page is at
     * @param treeNode node to be looked for to get the index
     * @return the index of the page that is being looked for. If it isn't found than return -1
     */
    @Override
    public int getIndex(TreeNode treeNode) {
        if (treeNode instanceof HelpPage page) {
            return getSubpages().indexOf(page);
        } else {
            return -1;
        }
    }

    /**
     * getter to enable if a page can have subpages
     * @return true if it allows children
     */
    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    /**
     * Makes a page have no more children
     * @return the child count being set to 0 as it is the last page available
     */
    @Override
    public boolean isLeaf() {
        return getChildCount() == 0;
    }

    /**
     *This helps get the final pages of the help page
     * @return the last pages of the help page
     */
    @Override
    public Enumeration<? extends TreeNode> children() {
        return Collections.enumeration(getSubpages());
    }

    /**
     * This determines the text that is shown in the tree view.
     * @return the text that is shown in tree view
     */
    @Override
    public String toString() {
        return getName();
    }
}
