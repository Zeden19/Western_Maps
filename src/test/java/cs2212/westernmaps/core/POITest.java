package cs2212.westernmaps.core;

import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class POITest {
    @Test
    public void testFavorites() {
        var account1 = new Account("account1", "ESIzRA==", false);
        var account2 = new Account("account2", "VWZ3iA==", false);

        var floor = new Floor("1", "First Floor", Path.of("maps/Example Building/First Floor.svg"));

        var poi = new POI("Test POI", "", 0, 0, Set.of(), floor, Layer.MISCELLANEOUS, null);
        Assertions.assertFalse(poi.isFavoriteOfAccount(account1));
        Assertions.assertFalse(poi.isFavoriteOfAccount(account2));

        // Test some cases twice to make sure the function is idempotent.
        for (int iteration = 0; iteration < 2; iteration++) {
            poi = poi.withFavoriteOfAccount(account1, true);
            Assertions.assertTrue(poi.isFavoriteOfAccount(account1));
            Assertions.assertFalse(poi.isFavoriteOfAccount(account2));
        }

        // Test some other cases once to make sure the function works when only
        // called once.
        poi = poi.withFavoriteOfAccount(account2, true);
        Assertions.assertTrue(poi.isFavoriteOfAccount(account1));
        Assertions.assertTrue(poi.isFavoriteOfAccount(account2));

        poi = poi.withFavoriteOfAccount(account1, false);
        Assertions.assertFalse(poi.isFavoriteOfAccount(account1));
        Assertions.assertTrue(poi.isFavoriteOfAccount(account2));

        for (int iteration = 0; iteration < 2; iteration++) {
            poi = poi.withFavoriteOfAccount(account2, false);
            Assertions.assertFalse(poi.isFavoriteOfAccount(account1));
            Assertions.assertFalse(poi.isFavoriteOfAccount(account2));
        }
    }

    @Test
    public void testName() {
        var floor1 = new Floor("1", "First Floor", Path.of("maps/Example Building/First Floor.svg"));
        var poi1 = new POI("Test POI", "", 0, 0, Set.of(), floor1, Layer.MISCELLANEOUS, null);
        String result = "Test POI";
        Assertions.assertEquals(poi1.name(), result);
        Assertions.assertNotEquals(poi1.name(), "test");
    }

    @Test
    public void testDescription() {
        var floor1 = new Floor("1", "First Floor", Path.of("maps/Example Building/First Floor.svg"));
        var poi1 = new POI("Test POI", "test description", 0, 0, Set.of(), floor1, Layer.MISCELLANEOUS, null);
        String result = "test description";
        Assertions.assertEquals(poi1.description(), result);
        Assertions.assertNotEquals(poi1.description(), "test");
    }

    @Test
    public void testX() {
        var floor1 = new Floor("1", "First Floor", Path.of("maps/Example Building/First Floor.svg"));
        var poi1 = new POI("Test POI", "test description", 0, 0, Set.of(), floor1, Layer.MISCELLANEOUS, null);
        int x = 0;
        Assertions.assertEquals(poi1.x(), x);
        Assertions.assertNotEquals(poi1.x(), 1);
    }

    @Test
    public void testY() {
        var floor1 = new Floor("1", "First Floor", Path.of("maps/Example Building/First Floor.svg"));
        var poi1 = new POI("Test POI", "test description", 0, 0, Set.of(), floor1, Layer.MISCELLANEOUS, null);
        int y = 0;
        Assertions.assertEquals(poi1.y(), y);
        Assertions.assertNotEquals(poi1.y(), 1);
    }

    @Test
    public void testFloor() {
        var floor1 = new Floor("1", "First Floor", Path.of("maps/Example Building/First Floor.svg"));
        var poi1 = new POI("Test POI", "test description", 0, 0, Set.of(), floor1, Layer.MISCELLANEOUS, null);
        Assertions.assertEquals(poi1.floor(), floor1);
        Assertions.assertNotEquals(
                poi1.floor(), new Floor("2", "Second Floor", Path.of("maps/Example Building/Second Floor.svg")));
    }

    @Test
    public void testLayer() {
        var floor1 = new Floor("1", "First Floor", Path.of("maps/Example Building/First Floor.svg"));
        var poi1 = new POI("Test POI", "test description", 0, 0, Set.of(), floor1, Layer.MISCELLANEOUS, null);
        Assertions.assertEquals(poi1.layer(), Layer.MISCELLANEOUS);
        Assertions.assertNotEquals(poi1.layer(), Layer.CUSTOM);
    }
}
