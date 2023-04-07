package cs2212.westernmaps.help;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * An index of all help pages.
 *
 * @author Connor Cummings
 */
public final class HelpPageIndex {
    private static final String PAGES_JSON_RESOURCE = HelpPage.HELP_ROOT + "/pages.json";
    private final HelpPage rootPage;
    private final Map<String, HelpPage> reverseIndex = new HashMap<>();

    /**
     * Creates a new help page index.
     *
     * @param rootPage The root of all help pages.
     */
    public HelpPageIndex(HelpPage rootPage) {
        this.rootPage = rootPage;
        buildReverseIndex(rootPage);
    }

    private void buildReverseIndex(HelpPage page) {
        reverseIndex.put(page.getContentURL().toString(), page);
        page.getSubpages().forEach(this::buildReverseIndex);
    }

    /**
     * Loads the page index from the {@code pages.json} resource.
     *
     * @return The root page of the page index.
     * @throws InvalidHelpException If the page index could not be loaded.
     */
    public static HelpPageIndex loadFromResources() {
        try {
            var objectMapper = new ObjectMapper();
            var rootPage =
                    objectMapper.readValue(HelpPage.class.getResourceAsStream(PAGES_JSON_RESOURCE), HelpPage.class);
            return new HelpPageIndex(rootPage);
        } catch (IOException ex) {
            throw new InvalidHelpException(ex);
        }
    }

    /**
     * Gets the root page of the help system.
     *
     * <p>This is the page that will be shown first when the user opens the help
     * window.</p>
     *
     * @return The root page of the help pages.
     */
    public HelpPage getRootPage() {
        return rootPage;
    }

    /**
     * Gets the help page specified by a content URL.
     *
     * @param contentURL The URL that is associated with the page.
     * @return           The help page with the given content URL.
     */
    public @Nullable HelpPage getPageByContentURL(URL contentURL) {
        return reverseIndex.get(contentURL.toString());
    }
}
