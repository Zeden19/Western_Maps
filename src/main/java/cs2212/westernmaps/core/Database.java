package cs2212.westernmaps.core;

import java.nio.file.Path;
import java.util.List;

public record Database(List<Account> accounts, List<Building> buildings, List<POI> pois) {
    public Database {
        accounts = List.copyOf(accounts);
        buildings = List.copyOf(buildings);
        pois = List.copyOf(pois);
    }

    public static Database loadFromDirectory(Path path) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void saveToDirectory(Path path) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
