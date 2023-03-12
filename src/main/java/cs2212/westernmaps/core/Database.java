package cs2212.westernmaps.core;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Database {
    private final List<Account> accounts;
    private final List<Building> buildings;
    private final List<POI> pois;

    public Database(List<Account> accounts, List<Building> buildings, List<POI> pois) {
        this.accounts = accounts;
        this.buildings = buildings;
        this.pois = pois;
    }

    public Database() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public static Database loadFromDirectory(Path path) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void saveToDirectory(Path path) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public List<POI> getPOIs() {
        return pois;
    }
}
