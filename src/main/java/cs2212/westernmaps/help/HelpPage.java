package cs2212.westernmaps.help;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.annotation.Nullable;
import javax.swing.tree.TreeNode;

public class HelpPage implements TreeNode {
    public static final String HELP_ROOT = "/cs2212/westernmaps/help";

    private final String name;
    private final List<HelpPage> subpages;

    private @Nullable HelpPage parent = null;

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

    public String getName() {
        return name;
    }

    public List<HelpPage> getSubpages() {
        return subpages;
    }

    public @Nullable HelpPage getParentPage() {
        return parent;
    }

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

    private String contentPathWithoutExtension() {
        var parent = getParentPage();
        var parentDirectory = parent != null ? parent.contentPathWithoutExtension() : HELP_ROOT;
        return parentDirectory + "/" + getName();
    }

    @Override
    public @Nullable TreeNode getChildAt(int i) {
        return getSubpages().get(i);
    }

    @Override
    public int getChildCount() {
        return getSubpages().size();
    }

    @Override
    public @Nullable TreeNode getParent() {
        return getParentPage();
    }

    @Override
    public int getIndex(TreeNode treeNode) {
        if (treeNode instanceof HelpPage page) {
            return getSubpages().indexOf(page);
        } else {
            return -1;
        }
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return getChildCount() == 0;
    }

    @Override
    public Enumeration<? extends TreeNode> children() {
        return Collections.enumeration(getSubpages());
    }

    // This determines the text that is shown in the tree view.
    @Override
    public String toString() {
        return getName();
    }
}
