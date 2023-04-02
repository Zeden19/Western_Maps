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

    private SVGDiagram diagram;
    private double scale;

    private @Nullable Future<BufferedImage> cachedImageFuture;

    public MapRenderCache(SVGDiagram diagram, double scale) {
        this.diagram = diagram;
        this.scale = scale;
    }

    public SVGDiagram getDiagram() {
        return diagram;
    }

    public void setDiagram(SVGDiagram diagram) {
        this.diagram = diagram;
        invalidateCache();
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        if (this.scale != scale) {
            invalidateCache();
        }
        this.scale = scale;
    }

    public void render(Graphics2D gfx, int x, int y, @Nullable JComponent component) {
        var cachedImage = getCachedImageIfReady();
        if (cachedImage != null) {
            gfx.drawImage(cachedImage, x, y, component);
        } else {
            var gfx2 = (Graphics2D) gfx.create();
            renderSvgToGraphicsDestructive(diagram, gfx2, x, y, scale, component);
        }
    }

    private @Nullable BufferedImage getCachedImageIfReady() {
        if (cachedImageFuture == null) {
            if (scale <= MAX_CACHED_SCALE && scale >= MIN_CACHED_SCALE) {
                cachedImageFuture = executor.submit(() -> {
                    Thread.sleep(RENDER_DELAY_MS);
                    return renderSvgToImage(diagram, scale);
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

    private void invalidateCache() {
        if (cachedImageFuture != null) {
            cachedImageFuture.cancel(true);
        }
        cachedImageFuture = null;
        System.gc();
    }

    private static BufferedImage renderSvgToImage(SVGDiagram diagram, double scale) {
        var width = (int) Math.ceil(diagram.getWidth() * scale);
        var height = (int) Math.ceil(diagram.getHeight() * scale);

        var image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        var gfx = (Graphics2D) image.createGraphics();
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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
