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
 * this class is for the Help page that is implemented in the application. It implements the TreeNode class in java
 */
public class HelpPage implements TreeNode {
    // declaring the classes private and public variables
    public static final String HELP_ROOT = "/cs2212/westernmaps/help";

    private final String name;
    private final List<HelpPage> subpages;

    private @Nullable HelpPage parent = null;

    /**
     * @param name the name of the help page
     * @param subpages the list of subpages that are in the help page
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
     * @return the name of the help page
     */
    public String getName() {
        return name;
    }

    /**
     * @return the list of subpages that are in the help page
     */
    public List<HelpPage> getSubpages() {
        return subpages;
    }

    /**
     * @return the parent page to the subpage in the current help page that the user is in
     */
    public @Nullable HelpPage getParentPage() {
        return parent;
    }

    /**
     *
     * @return the content URL in the help page. It's used to find out where in the help page the user is and keep track
     * of where in the help pages they are.
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
     * @param i  which subpage to look at
     * @return the subpage at the current index that is pushed as the parameter
     */
    @Override
    public @Nullable TreeNode getChildAt(int i) {
        return getSubpages().get(i);
    }

    /**
     *
     * @return the amount of subpages that are in the help page all together
     */
    @Override
    public int getChildCount() {
        return getSubpages().size();
    }

    /**
     *
     * @return the parent page of the current page in the help page
     */
    @Override
    public @Nullable TreeNode getParent() {
        return getParentPage();
    }

    /**
     *
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
     *
     * @return true if it allows children
     */
    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    /**
     *
     * @return false if the node is a leaf
     */
    @Override
    public boolean isLeaf() {
        return getChildCount() == 0;
    }

    @Override
    public Enumeration<? extends TreeNode> children() {
        return Collections.enumeration(getSubpages());
    }

    /**
     *
     * @return the text that is shown in tree view
     */
    // This determines the text that is shown in the tree view.
    @Override
    public String toString() {
        return getName();
    }
}
