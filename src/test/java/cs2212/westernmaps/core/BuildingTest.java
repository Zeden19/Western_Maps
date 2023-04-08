package cs2212.westernmaps.core;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class BuildingTest {
    @Test
    void testToString() {
        List<Floor> floors = List.of(new Floor("testName", "test", Path.of("resources")));
        Building building = new Building("name", floors);
        assertEquals("name", building.toString());
    }

    @Test
    void name() {
        List<Floor> floors = List.of(new Floor("testName", "test", Path.of("resources")));

        Building building = new Building("name", floors);
        assertEquals("name", building.name());
    }

    @Test
    void floors() {
        Floor floor1 = new Floor("testName", "test", Path.of("resources"));
        Floor floor2 = new Floor("testName2", "test2", Path.of("resources"));
        List<Floor> floors = List.of(floor1, floor2);

        Building building = new Building("name", floors);
        assertEquals(floors, building.floors());
    }
}
