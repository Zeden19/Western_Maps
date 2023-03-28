package cs2212.westernmaps.maps;

import com.formdev.flatlaf.ui.FlatBorder;
import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.net.URI;
import javax.swing.JPanel;

public final class MapViewerPanel extends JPanel {
    private final SVGUniverse universe = SVGCache.getSVGUniverse();
    private final AffineTransform transform = new AffineTransform();

    private URI currentMapUri;

    public MapViewerPanel(URI initialMapUri) {
        currentMapUri = initialMapUri;

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

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
