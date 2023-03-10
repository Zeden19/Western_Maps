package cs2212.westernmaps.help;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class HelpPageIndex {
    private static final String PAGES_JSON_RESOURCE = HelpPage.HELP_ROOT + "/pages.json";

    private final HelpPage rootPage;
    private final Map<String, HelpPage> reverseIndex = new HashMap<>();

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

    public HelpPage getRootPage() {
        return rootPage;
    }

    public @Nullable HelpPage getPageByContentURL(URL contentURL) {
        return reverseIndex.get(contentURL.toString());
    }
}
