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
}
