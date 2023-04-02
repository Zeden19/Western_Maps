package cs2212.westernmaps.maps;

import com.formdev.flatlaf.ui.FlatBorder;
import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import cs2212.westernmaps.core.POI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.swing.JPanel;

public final class MapViewerPanel extends JPanel {
    private static final int POI_CLICK_TARGET_SIZE = 16;
    private static final int POI_HOVER_CIRCLE_RADIUS = 18;
    private static final Color POI_HOVER_CIRCLE_COLOR = new Color(0x00, 0x00, 0x00, 0x1F);

    private static final double ZOOM_OUT_LIMIT = 0.1;
    private static final double ZOOM_IN_LIMIT = 10.0;

    private final SVGUniverse universe = SVGCache.getSVGUniverse();
    private final AffineTransform transform = new AffineTransform();

    private @Nullable BufferedImage cachedMapImage = null;

    private final List<Consumer<POI>> poiClickListeners = new ArrayList<>();

    private URI currentMapUri;
    private List<POI> displayedPois;

    private @Nullable POI hoveredPoi = null;

    public MapViewerPanel(URI initialMapUri, List<POI> displayedPois) {
        currentMapUri = initialMapUri;
        this.displayedPois = displayedPois;

        var mouseAdapter = new MouseAdapter() {
            private final Point lastMousePosition = new Point();
            private boolean dragging = false;

            @Override
            public void mousePressed(MouseEvent e) {
                // Only pan the map with left click (button 1) or middle click
                // (button 2).
                if (hoveredPoi != null) {
                    poiClickListeners.forEach(listener -> listener.accept(hoveredPoi));
                } else if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON2) {
                    lastMousePosition.setLocation(e.getX(), e.getY());
                    dragging = true;
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
                requestFocusInWindow();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragging) {
                    dragging = false;
                    setCursor(null);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                var hoveredPoi = getHoveredPoiByChebyshevDistance(e.getX(), e.getY());
                if (hoveredPoi != MapViewerPanel.this.hoveredPoi) {
                    repaint();
                    MapViewerPanel.this.hoveredPoi = hoveredPoi;
                }

                if (hoveredPoi != null) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(null);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    var deltaX = e.getX() - lastMousePosition.x;
                    var deltaY = e.getY() - lastMousePosition.y;
                    lastMousePosition.setLocation(e.getX(), e.getY());

                    var scaleFactor = 1.0 / transform.getScaleX();

                    transform.translate(deltaX * scaleFactor, deltaY * scaleFactor);
                    repaint();
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                var scaleFactor = Math.pow(1.5, -e.getPreciseWheelRotation());

                // Correct the scale factor to enforce the zoom limit.
                if (transform.getScaleX() * scaleFactor < ZOOM_OUT_LIMIT) {
                    scaleFactor = ZOOM_OUT_LIMIT / transform.getScaleX();
                }
                if (transform.getScaleX() * scaleFactor > ZOOM_IN_LIMIT) {
                    scaleFactor = ZOOM_IN_LIMIT / transform.getScaleX();
                }

                var mouseLocation = new Point(e.getX(), e.getY());
                try {
                    transform.inverseTransform(mouseLocation, mouseLocation);
                } catch (NoninvertibleTransformException ex) {
                    throw new RuntimeException(ex);
                }
                transform.translate(mouseLocation.x, mouseLocation.y);
                transform.scale(scaleFactor, scaleFactor);
                transform.translate(-mouseLocation.x, -mouseLocation.y);

                cachedMapImage = null;
                repaint();
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);

        setBackground(Color.WHITE);
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

        cachedMapImage = null;
        repaint();
    }

    public List<POI> getDisplayedPois() {
        return displayedPois;
    }

    public void setDisplayedPois(List<POI> pois) {
        displayedPois = pois;
    }

    public void addPoiClickListener(Consumer<POI> listener) {
        poiClickListeners.add(listener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        var gfx = (Graphics2D) g.create();
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // The SVG universe keeps a cached version of all loaded documents, so
        // we can just use that instead of keeping a copy ourselves.
        var diagram = universe.getDiagram(currentMapUri);

        // Render the map SVG to an image and cache the result.
        if (cachedMapImage == null) {
            cachedMapImage = renderMapSvg(diagram, transform.getScaleX());
        }
        // Render the cached map image at the correct position.
        var mapPosition = new Point(0, 0);
        transform.transform(mapPosition, mapPosition);
        gfx.drawImage(cachedMapImage, mapPosition.x, mapPosition.y, this);

        // Render icons for each displayed POI.
        for (var poi : displayedPois) {
            // If the POI is hovered, skip it since it will be rendered on top
            // of everything else later. Comparison by reference is intentional.
            if (poi == hoveredPoi) {
                continue;
            }

            // POI icons are rendered at the same size regardless of the map's
            // scale, so we need to transform their locations manually.
            var location = new Point(poi.x(), poi.y());
            transform.transform(location, location);

            // Offset the location so that the icon is centered on the POI.
            var icon = poi.layer().getIcon();
            location.translate(-icon.getIconWidth() / 2, -icon.getIconHeight() / 2);
            // Draw the POI icon.
            icon.paintIcon(this, gfx, location.x, location.y);
        }

        // Render the hovered POI.
        if (hoveredPoi != null) {
            var poi = hoveredPoi;

            // POI icons are rendered at the same size regardless of the map's
            // scale, so we need to transform their locations manually.
            var location = new Point(poi.x(), poi.y());
            transform.transform(location, location);

            var radius = POI_HOVER_CIRCLE_RADIUS;
            gfx.setPaint(POI_HOVER_CIRCLE_COLOR);
            gfx.fillOval(location.x - radius, location.y - radius, radius * 2, radius * 2);

            // Offset the location so that the icon is centered on the POI.
            var icon = poi.layer().getIcon();
            location.translate(-icon.getIconWidth() / 2, -icon.getIconHeight() / 2);
            // Draw the POI icon.
            icon.paintIcon(this, gfx, location.x, location.y);
        }
    }

    private static BufferedImage renderMapSvg(SVGDiagram diagram, double scale) {
        var width = (int) Math.ceil(diagram.getWidth() * scale);
        var height = (int) Math.ceil(diagram.getHeight() * scale);

        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var gfx = (Graphics2D) image.createGraphics();
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gfx.scale(scale, scale);

        var mapPath = diagram.getRoot().getChild("map");
        var lineThickness = Math.min(1.0 / scale, 1.0);

        // Render the map SVG to an image and cache the result.
        try {
            if (mapPath != null) {
                mapPath.setAttribute("stroke-width", 2, Double.toString(lineThickness));
            }
            diagram.render(null, gfx);
        } catch (SVGException ex) {
            throw new RuntimeException(ex);
        }

        return image;
    }

    private @Nullable POI getHoveredPoiByChebyshevDistance(int mouseX, int mouseY) {
        POI hoveredPoi = null;
        int hoveredPoiDistance = Integer.MAX_VALUE;
        for (var poi : displayedPois) {
            // POI icons are rendered at the same size regardless of the map's
            // scale, so we need to transform their locations manually.
            var location = new Point(poi.x(), poi.y());
            transform.transform(location, location);

            var distance = chebyshevDistance(location.x, location.y, mouseX, mouseY);
            if (distance <= POI_CLICK_TARGET_SIZE && distance < hoveredPoiDistance) {
                hoveredPoi = poi;
                hoveredPoiDistance = distance;
            }
        }
        return hoveredPoi;
    }

    // https://en.wikipedia.org/wiki/Chebyshev_distance
    private static int chebyshevDistance(int x1, int y1, int x2, int y2) {
        return Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
    }
}
