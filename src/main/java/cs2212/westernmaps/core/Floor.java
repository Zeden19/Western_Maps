package cs2212.westernmaps.core;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.IntSequenceGenerator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kitfox.svg.SVGDiagram;
import java.io.IOException;
import java.nio.file.Path;

/**
 * A floor of a building that the application has map data for.
 *
 * @param shortName The name of this floor, abbreviated to one or two characters
 *                  (for example {@code "1"} or {@code "G"}).
 * @param longName  The full display name of this floor (for example
 *                  {@code "First Floor"} or {@code "Ground Floor"}).
 * @param mapPath   The path to the SVG map data for this floor.
 */
@JsonIdentityInfo(generator = IntSequenceGenerator.class)
public record Floor(
        String shortName, String longName, @JsonSerialize(using = RelativePathSerializer.class) Path mapPath) {
    /**
     * Loads the map of this floor as an {@link SVGDiagram}.
     *
     * @return An {@code SVGDiagram} of this floor's map.
     */
    public SVGDiagram loadMap() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static class RelativePathSerializer extends JsonSerializer<Path> {
        @Override
        public void serialize(Path path, JsonGenerator generator, SerializerProvider provider) throws IOException {
            generator.writeString(path.toString());
        }
    }
}
