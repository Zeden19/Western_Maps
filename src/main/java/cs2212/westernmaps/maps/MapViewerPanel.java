package cs2212.westernmaps.maps;

import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.net.URI;
import javax.swing.JPanel;

public final class MapViewerPanel extends JPanel {
    private final SVGUniverse universe = SVGCache.getSVGUniverse();
    private final AffineTransform transform = new AffineTransform();

    private URI currentMapUri;

    public MapViewerPanel(URI initialMapUri) {
        currentMapUri = initialMapUri;
    }

    public URI getCurrentMapUri() {
        return currentMapUri;
    }

    public void setCurrentMapUri(URI uri) {
        this.currentMapUri = uri;
    }

    @Override
    protected void paintComponent(Graphics g) {
        var gfx = (Graphics2D) g.create();
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gfx.transform(transform);

        // The SVG universe keeps a cached version of all loaded documents, so
        // we can just use that instead of keeping a copy ourselves.
        var diagram = universe.getDiagram(currentMapUri);

        try {
            diagram.render(this, gfx);
        } catch (SVGException ex) {
            throw new RuntimeException(ex);
        }
    }
}
