package cs2212.westernmaps.maps;

import com.formdev.flatlaf.ui.FlatBorder;
import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import cs2212.westernmaps.core.POI;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.net.URI;
import java.util.List;
import javax.swing.JPanel;

public final class MapViewerPanel extends JPanel {
    private final SVGUniverse universe = SVGCache.getSVGUniverse();
    private final AffineTransform transform = new AffineTransform();

    private URI currentMapUri;
    private List<POI> displayedPois;

    public MapViewerPanel(URI initialMapUri, List<POI> displayedPois) {
        currentMapUri = initialMapUri;
        this.displayedPois = displayedPois;

        var mouseAdapter = new MouseAdapter() {
            private int lastMouseX;
            private int lastMouseY;
            private boolean dragging = false;

            @Override
            public void mousePressed(MouseEvent e) {
                // Only pan the map with left click (button 1) or middle click
                // (button 2).
                if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON2) {
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                    dragging = true;
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
                requestFocusInWindow();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragging = false;
                setCursor(null);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    int deltaX = e.getX() - lastMouseX;
                    lastMouseX = e.getX();
                    int deltaY = e.getY() - lastMouseY;
                    lastMouseY = e.getY();

                    transform.translate(deltaX, deltaY);
                    repaint();
                }
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);

        setBorder(new FlatBorder());
        setFocusable(true);
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    public URI getCurrentMapUri() {
        return currentMapUri;
    }

    public void setCurrentMapUri(URI uri) {
        this.currentMapUri = uri;
    }

    public List<POI> getDisplayedPois() {
        return displayedPois;
    }

    public void setDisplayedPois(List<POI> pois) {
        displayedPois = pois;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        var gfx = (Graphics2D) g.create();
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        var oldTransform = gfx.getTransform();
        gfx.transform(transform);

        // The SVG universe keeps a cached version of all loaded documents, so
        // we can just use that instead of keeping a copy ourselves.
        var diagram = universe.getDiagram(currentMapUri);

        // Render the map SVG.
        try {
            diagram.render(this, gfx);
        } catch (SVGException ex) {
            throw new RuntimeException(ex);
        }

        // Render icons for each displayed POI.
        gfx.setTransform(oldTransform);
        for (var poi : displayedPois) {
            var icon = poi.layer().getIcon();

            // POI icons are rendered at the same size regardless of the map's
            // scale, so we need to transform their locations manually.
            var location = new Point(poi.x(), poi.y());
            transform.transform(location, location);
            // Offset the location so that the icon is centered on the POI.
            location.translate(-icon.getIconWidth() / 2, -icon.getIconHeight() / 2);

            icon.paintIcon(this, gfx, location.x, location.y);
        }
    }
}
