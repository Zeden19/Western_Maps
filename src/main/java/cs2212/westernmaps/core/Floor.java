package cs2212.westernmaps.core;

import com.kitfox.svg.SVGDiagram;
import java.nio.file.Path;

public record Floor(String shortName, String longName, Path mapPath) {
    public SVGDiagram loadMap() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
