package cs2212.westernmaps.core;

import java.awt.Point;

public final class POI {
    private String name;
    private String description;
    private Point location;
    private boolean favorite;
    private Floor floor;
    private Layer layer;

    public POI(String name, String description, Point location, boolean favorite, Floor floor, Layer layer) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.favorite = favorite;
        this.floor = floor;
        this.layer = layer;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Point getLocation() {
        return location;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public Floor getFloor() {
        return floor;
    }

    public Layer getLayer() {
        return layer;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }
}
