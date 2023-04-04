package cs2212.westernmaps.maps;

import com.formdev.flatlaf.ui.FlatBorder;
import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGUniverse;
import cs2212.westernmaps.core.Layer;
import cs2212.westernmaps.core.POI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public final class MapViewerPanel extends JPanel {
    private static final int POI_CLICK_TARGET_SIZE = 16;
    private static final int POI_HOVER_CIRCLE_RADIUS = 18;
    private static final Color POI_HOVER_CIRCLE_COLOR = new Color(0x00, 0x00, 0x00, 0x1F);

    private static final Color POI_TOOLTIP_BACKGROUND_COLOR = new Color(0x00, 0x00, 0x00, 0xBF);
    private static final Color POI_TOOLTIP_FOREGROUND_COLOR = Color.WHITE;

    private static final double ZOOM_OUT_LIMIT = 0.1;
    private static final double ZOOM_IN_LIMIT = 10.0;

    private final SVGUniverse universe = SVGCache.getSVGUniverse();
    private final AffineTransform transform = new AffineTransform();

    private final MapRenderCache renderCache;

    private final List<Consumer<POI>> poiClickListeners = new ArrayList<>();
    private final List<BiConsumer<POI, Point>> poiMoveListeners = new ArrayList<>();
    private Predicate<POI> poiMoveCondition = poi -> true;
    private Predicate<POI> poiVisibleCondition = poi -> true;

    private Component cursorComponent;
    private URI currentMapUri;
    private List<POI> displayedPois;

    private @Nullable POI hoveredPoi = null;

    private @Nullable POI draggedPoi = null;
    private final Point draggedPoiLocation = new Point();

    public MapViewerPanel(URI initialMapUri, List<POI> displayedPois) {
        cursorComponent = this;
        currentMapUri = initialMapUri;

        this.displayedPois = displayedPois;
        this.renderCache = new MapRenderCache(universe.getDiagram(initialMapUri), 1.0);

        var mouseAdapter = new MouseAdapter() {
            private final Point lastMousePosition = new Point();
            private DragState dragState = DragState.NONE;

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && hoveredPoi != null) {
                    dragState = DragState.HOLDING_POI;
                } else if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON2) {
                    lastMousePosition.setLocation(e.getX(), e.getY());
                    dragState = DragState.PANNING_MAP;
                    cursorComponent.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
                requestFocusInWindow();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                switch (dragState) {
                    case NONE -> {
                        return;
                    }
                    case HOLDING_POI -> {
                        var poi = Objects.requireNonNull(hoveredPoi);
                        poiClickListeners.forEach(listener -> listener.accept(poi));
                    }
                    case DRAGGING_POI -> {
                        var poi = Objects.requireNonNull(draggedPoi);
                        poiMoveListeners.forEach(listener -> listener.accept(poi, draggedPoiLocation));
                        draggedPoi = null;
                        repaint();
                    }
                }
                dragState = DragState.NONE;
                cursorComponent.setCursor(null);
                refreshHoveredPoi(e.getX(), e.getY());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                refreshHoveredPoi(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                switch (dragState) {
                    case PANNING_MAP -> {
                        var deltaX = e.getX() - lastMousePosition.x;
                        var deltaY = e.getY() - lastMousePosition.y;
                        lastMousePosition.setLocation(e.getX(), e.getY());
                        var scaleFactor = 1.0 / transform.getScaleX();
                        transform.translate(deltaX * scaleFactor, deltaY * scaleFactor);
                        repaint();
                    }
                    case HOLDING_POI -> {
                        var poi = Objects.requireNonNull(hoveredPoi);
                        var distanceToPoi = chebyshevDistanceToPoi(poi, e.getX(), e.getY());
                        if (distanceToPoi > POI_CLICK_TARGET_SIZE) {
                            if (!poiMoveCondition.test(poi)) {
                                // The user is not allowed to move this POI.
                                dragState = DragState.NONE;
                                cursorComponent.setCursor(null);
                                return;
                            }

                            dragState = DragState.DRAGGING_POI;
                            draggedPoi = poi;
                            hoveredPoi = null;

                            var location = new Point(e.getX(), e.getY());
                            try {
                                transform.inverseTransform(location, draggedPoiLocation);
                            } catch (NoninvertibleTransformException ex) {
                                throw new RuntimeException(ex);
                            }
                            repaint();
                        }
                    }
                    case DRAGGING_POI -> {
                        var location = new Point(e.getX(), e.getY());
                        try {
                            transform.inverseTransform(location, draggedPoiLocation);
                        } catch (NoninvertibleTransformException ex) {
                            throw new RuntimeException(ex);
                        }
                        repaint();
                    }
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

                renderCache.setScale(transform.getScaleX());
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

    /**
     * Converts a position from component-space to map-space.
     *
     * <p><i>Map-space</i> is the coordinate space used for POIs on the map and
     * <i>component-space</i> is the coordinate space used by this Swing
     * component (for example, the position in mouse events).</p>
     *
     * @param componentPos The position to convert.
     * @param mapPos       The point to set to the converted position.
     * @return             A copy of {@code mapPos} for convenience.
     */
    public Point componentToMapPosition(Point componentPos, Point mapPos) {
        try {
            transform.inverseTransform(componentPos, mapPos);
        } catch (NoninvertibleTransformException ex) {
            throw new RuntimeException(ex);
        }
        return componentPos;
    }

    /**
     * Scrolls this map such that the given POI is in the center of the view.
     *
     * <p>If the map is zoomed out further than the default, then the map will
     * be zoomed in to the default zoom level.</p>
     *
     * @param poi The POI to center the view on.
     */
    public void scrollPoiToCenter(POI poi) {
        var bounds = getBounds();

        // Get the map-space position at the current center of the map.
        var location = new Point(bounds.width / 2, bounds.height / 2);
        componentToMapPosition(location, location);

        // Since the transform only permits relative movement, we have to
        // calculate the difference from the current position.
        var deltaX = location.x - poi.x();
        var deltaY = location.y - poi.y();
        transform.translate(deltaX, deltaY);
        repaint();
    }

    /**
     * Sets the component that receives cursor changes from this panel.
     *
     * <p>This is needed because, when using a {@link JLayeredPane}, only the
     * topmost component can set the cursor the normal way.</p>
     *
     * @param cursorComponent A component that will have its cursor changed.
     */
    public void setCursorComponent(@Nullable Component cursorComponent) {
        this.cursorComponent = cursorComponent == null ? this : cursorComponent;
    }

    public void setCurrentMapUri(URI uri) {
        renderCache.setDiagram(universe.getDiagram(uri));
        this.currentMapUri = uri;
        repaint();
    }

    public void setDisplayedPois(List<POI> pois) {
        displayedPois = pois;
        // TODO: Refresh the hovered POI here.
        repaint();
    }

    public void addPoiClickListener(Consumer<POI> listener) {
        poiClickListeners.add(listener);
    }

    /**
     * Registers an event listener that is called when a POI is moved.
     *
     * <p>For the POI to be successfully moved, at least one POI move listener
     * should call {@link #setDisplayedPois} with an updated list containing
     * the moved POI. If this is not done, the POI will snap back to its
     * previous location.</p>
     *
     * @param listener A function taking two arguments: the POI that moved and
     *                 the location it moved to.
     */
    public void addPoiMoveListener(BiConsumer<POI, Point> listener) {
        poiMoveListeners.add(listener);
    }

    /**
     * Sets the predicate that determines what POIs can be moved the user.
     *
     * @param condition A function taking a {@link POI} and returning whether
     *                  that POI should be able to be moved by the user.
     */
    public void setPoiMoveCondition(@Nullable Predicate<POI> condition) {
        poiMoveCondition = Objects.requireNonNullElse(condition, poi -> true);
    }

    /**
     * Sets the predicate that determines what POIs are visible on the map.
     *
     * <p>This is used by {@link MapPanel} to hide specific layers and custom
     * POIs belonging to other users.</p>
     *
     * @param condition A function taking a {@link POI} and returning whether
     *                  that POI should be visible on the map.
     */
    public void setPoiVisibleCondition(@Nullable Predicate<POI> condition) {
        poiVisibleCondition = Objects.requireNonNullElse(condition, poi -> true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        var gfx = (Graphics2D) g.create();
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Render the cached map image at the correct position.
        var mapPosition = new Point(0, 0);
        transform.transform(mapPosition, mapPosition);
        renderCache.render(gfx, mapPosition.x, mapPosition.y, this);

        // Render icons for each displayed POI. POIs are rendered in reverse
        // order so that the first POI in the list gets displayed on top.
        for (int index = displayedPois.size() - 1; index >= 0; index--) {
            var poi = displayedPois.get(index);

            // If the POI is being hovered or dragged, skip it since it will be
            // rendered on top of everything else later. Comparison by reference
            // is intentional.
            if (poi == hoveredPoi || poi == draggedPoi) {
                continue;
            }
            // If the POI's layer is not visible, skip it.
            if (!poiVisibleCondition.test(poi)) {
                continue;
            }

            renderPoiIcon(gfx, poi, false);
        }

        // Render the hovered and dragged POIs.
        if (hoveredPoi != null) {
            renderPoiIcon(gfx, hoveredPoi, true);
            renderPoiTooltip(gfx, hoveredPoi);
        }
        if (draggedPoi != null) {
            renderPoiIcon(gfx, draggedPoi.layer(), draggedPoiLocation.x, draggedPoiLocation.y, true);
            renderPoiTooltip(gfx, draggedPoi.name(), draggedPoiLocation.x, draggedPoiLocation.y);
        }
    }

    private void renderPoiIcon(Graphics2D gfx, POI poi, boolean hoverCircle) {
        renderPoiIcon(gfx, poi.layer(), poi.x(), poi.y(), hoverCircle);
    }

    private void renderPoiIcon(Graphics2D gfx, Layer layer, int x, int y, boolean hoverCircle) {
        // POI icons are rendered at the same size regardless of the map's
        // scale, so we need to transform their locations manually.
        var location = new Point(x, y);
        transform.transform(location, location);

        if (hoverCircle) {
            var radius = POI_HOVER_CIRCLE_RADIUS;
            gfx.setPaint(POI_HOVER_CIRCLE_COLOR);
            gfx.fillOval(location.x - radius, location.y - radius, radius * 2, radius * 2);
        }

        // Offset the location so that the icon is centered on the POI.
        var icon = layer.getIcon();
        location.translate(-icon.getIconWidth() / 2, -icon.getIconHeight() / 2);
        // Draw the POI icon.
        icon.paintIcon(this, gfx, location.x, location.y);
    }

    private void renderPoiTooltip(Graphics2D gfx, POI poi) {
        renderPoiTooltip(gfx, poi.name(), poi.x(), poi.y());
    }

    private void renderPoiTooltip(Graphics2D gfx, String text, int poiX, int poiY) {
        // POI icons are rendered at the same size regardless of the map's
        // scale, so we need to transform their locations manually.
        var location = new Point(poiX, poiY);
        transform.transform(location, location);

        int textWidth = gfx.getFontMetrics().stringWidth(text);

        var x = location.x - textWidth / 2 - 8;
        var y = location.y + 22;

        gfx.setPaint(POI_TOOLTIP_BACKGROUND_COLOR);
        gfx.fillRoundRect(x, y, textWidth + 16, 20, 6, 6);
        gfx.setPaint(POI_TOOLTIP_FOREGROUND_COLOR);
        gfx.drawString(text, x + 8, y + 2 + 13);
    }

    private void refreshHoveredPoi(int mouseX, int mouseY) {
        var hoveredPoi = getHoveredPoiByChebyshevDistance(mouseX, mouseY);
        if (hoveredPoi != this.hoveredPoi) {
            repaint();
            this.hoveredPoi = hoveredPoi;
        }

        if (hoveredPoi != null) {
            cursorComponent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            cursorComponent.setCursor(null);
        }
    }

    private @Nullable POI getHoveredPoiByChebyshevDistance(int mouseX, int mouseY) {
        POI hoveredPoi = null;
        int hoveredPoiDistance = Integer.MAX_VALUE;
        for (var poi : displayedPois) {
            // If the is not visible, then it can't be hovered.
            if (!poiVisibleCondition.test(poi)) {
                continue;
            }

            // POI icons are rendered at the same size regardless of the map's
            // scale, so we need to transform their locations manually.
            var location = new Point(poi.x(), poi.y());
            transform.transform(location, location);

            var distance = chebyshevDistanceToPoi(poi, mouseX, mouseY);
            if (distance <= POI_CLICK_TARGET_SIZE && distance < hoveredPoiDistance) {
                hoveredPoi = poi;
                hoveredPoiDistance = distance;
            }
        }
        return hoveredPoi;
    }

    private int chebyshevDistanceToPoi(POI poi, int mouseX, int mouseY) {
        // POI icons are rendered at the same size regardless of the map's
        // scale, so we need to transform their locations manually.
        var location = new Point(poi.x(), poi.y());
        transform.transform(location, location);

        return chebyshevDistance(location.x, location.y, mouseX, mouseY);
    }

    // https://en.wikipedia.org/wiki/Chebyshev_distance
    private static int chebyshevDistance(int x1, int y1, int x2, int y2) {
        return Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
    }

    private enum DragState {
        NONE,
        PANNING_MAP,
        HOLDING_POI,
        DRAGGING_POI,
    }
}
