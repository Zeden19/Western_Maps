package cs2212.westernmaps.core;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FloorTest {

    // todo add test once this method has been implemented, 2 tests one for valid one for invalid
    @Test
    void loadMap() {}

    @Test
    void shortName() {
        Floor floor = new Floor("T", "test", Path.of("test"));
        assertEquals("T", floor.shortName());
    }

    @Test
    void longName() {
        Floor floor = new Floor("T", "test", Path.of("test"));
        assertEquals("test", floor.longName());
    }

    @Test
    void mapPath() {
        Path path = Path.of("test");
        Floor floor = new Floor("T", "test", path);
        assertEquals(path, floor.mapPath());
    }
}
