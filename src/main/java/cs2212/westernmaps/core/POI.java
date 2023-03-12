package cs2212.westernmaps.core;

public record POI(String name, String description, int x, int y, boolean favorite, Floor floor, Layer layer) {}
