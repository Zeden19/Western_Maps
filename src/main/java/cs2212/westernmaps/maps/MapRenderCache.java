package cs2212.westernmaps.maps;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.annotation.Nullable;
import javax.swing.JComponent;

/**
 * This class is for the Maps rendering and cache
 * @author Connor Cummings
 */
public final class MapRenderCache {
    private static final double MAX_CACHED_SCALE = 5.0;
    private static final double MIN_CACHED_SCALE = 0.0;
    private static final long RENDER_DELAY_MS = 200;

    private final ExecutorService executor = Executors.newCachedThreadPool(runnable -> {
        var thread = new Thread(runnable);
        // Make sure the thread will stop when all other threads exit.
        thread.setDaemon(true);
        thread.setName("Map Render Cache Thread");
        return thread;
    });

    // The diagram of the map being rendered, along with the scale of zoom
    private SVGDiagram diagram;

    private double scale;
    private double deviceScale = 1.0;

    // The cached image, and the future for the cached image.
    private @Nullable Future<BufferedImage> cachedImageFuture;

    /**
     * Creates the map render cache
     * @param diagram is the diagram of the map that is being rendered
     * @param scale is the scale of the map being used
     */
    public MapRenderCache(SVGDiagram diagram, double scale) {
        this.diagram = diagram;
        this.scale = scale;
    }

    /**
     * gets the diagram of the svg file
     * @return the diagram that is being used
     */
    public SVGDiagram getDiagram() {
        return diagram;
    }

    /**
     * sets the diagram of the svg file
     * @param diagram is the new diagram that will be used in place of the previous one
     */
    public void setDiagram(SVGDiagram diagram) {
        this.diagram = diagram;
        invalidateCache();
    }

    /**
     * sets the scale
     * @param scale is the new scale that would be set for previous one
     */
    public void setScale(double scale) {
        if (this.scale != scale) {
            invalidateCache();
        }
        this.scale = scale;
    }

    /**
     * renders the image
     * @param gfx the gfx that is used for map
     * @param x the positioning of the map in x-axis
     * @param y the position of map in y-axis
     * @param component a component that is used for the map
     */
    public void render(Graphics2D gfx, int x, int y, @Nullable JComponent component) {
        deviceScale = gfx.getDeviceConfiguration().getDefaultTransform().getScaleX();
        var cachedImage = getCachedImageIfReady();
        var gfx2 = (Graphics2D) gfx.create();
        if (cachedImage != null) {
            gfx2.translate(x, y);
            gfx2.scale(1.0 / deviceScale, 1.0 / deviceScale);
            gfx2.drawImage(cachedImage, 0, 0, component);
        } else {
            renderSvgToGraphicsDestructive(diagram, gfx2, x, y, scale, component);
        }
    }

    // Returns the cached image if it is ready, or null if it is not ready.
    private @Nullable BufferedImage getCachedImageIfReady() {
        if (cachedImageFuture == null) {
            if (scale <= MAX_CACHED_SCALE && scale >= MIN_CACHED_SCALE) {
                cachedImageFuture = executor.submit(() -> {
                    Thread.sleep(RENDER_DELAY_MS);
                    return renderSvgToImage(diagram, scale, deviceScale);
                });
            }
            return null;
        }
        try {
            return cachedImageFuture.isDone() ? cachedImageFuture.get() : null;
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Invalidates the cached image.
    private void invalidateCache() {
        if (cachedImageFuture != null) {
            cachedImageFuture.cancel(true);
        }
        cachedImageFuture = null;
        System.gc();
    }

    // Renders the SVG diagram to an image.
    private static BufferedImage renderSvgToImage(SVGDiagram diagram, double scale, double deviceScale) {
        var width = (int) Math.ceil(diagram.getWidth() * scale * deviceScale);
        var height = (int) Math.ceil(diagram.getHeight() * scale * deviceScale);

        var image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        var gfx = (Graphics2D) image.createGraphics();
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gfx.scale(deviceScale, deviceScale);

        gfx.setPaint(Color.WHITE);
        gfx.fillRect(0, 0, width, height);
        renderSvgToGraphicsDestructive(diagram, gfx, 0, 0, scale, null);

        return image;
    }

    // The Graphics2D passed to this method should not be used afterward.
    private static void renderSvgToGraphicsDestructive(
            SVGDiagram diagram, Graphics2D gfx, int x, int y, double scale, @Nullable JComponent component) {
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gfx.translate(x, y);
        gfx.scale(scale, scale);

        var mapPath = diagram.getRoot().getChild("map");
        var lineThickness = Math.min(1.0 / scale, 1.0);

        // Render the map SVG to an image and cache the result.
        try {
            if (mapPath != null) {
                mapPath.setAttribute("stroke-width", 2, Double.toString(lineThickness));
            }
            diagram.render(component, gfx);
        } catch (SVGException ex) {
            throw new RuntimeException(ex);
        }
    }
}
