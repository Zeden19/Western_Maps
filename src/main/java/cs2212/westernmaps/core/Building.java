package cs2212.westernmaps.core;

import java.util.List;

public record Building(String name, List<Floor> floors) {
    public Building {
        floors = List.copyOf(floors);
    }
}
