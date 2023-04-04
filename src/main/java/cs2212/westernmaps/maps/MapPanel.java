package cs2212.westernmaps.maps;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import cs2212.westernmaps.core.*;
import cs2212.westernmaps.pois.POISummaryPanel;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.swing.*;

public final class MapPanel extends JPanel {
    private final Database database;
    private final Building building;
    private Floor currentFloor;

    private final MapViewerPanel mapViewer;

    private final List<Runnable> backListeners = new ArrayList<>();
    private final JList<POI> poiList = new JList<>();
    private final JList<POI> favoritesList = new JList<>();

    public MapPanel(Database database, Building building, Account loggedInAccount) {
        this.database = database;
        this.building = building;

        // This determines what MainWindow will use as its title.
        setName(building.name());
        setLayout(new BorderLayout());

        var toolbar = createToolbar();

        currentFloor = building.floors().get(0);
        var initialMapUri = database.resolveFloorMapUri(currentFloor);

        mapViewer = new MapViewerPanel(initialMapUri, List.of());
        mapViewer.addPoiMoveListener((movedPoi, location) -> {
            var newState = database.getCurrentState().modifyPOIs(pois -> pois.stream()
                    .map(poi -> poi == movedPoi ? poi.withLocation(location.x, location.y) : poi)
                    .toList());
            database.getHistory().pushState(newState);
            // Save changes to disk.
            try {
                database.save();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            refreshPois();
        });
        mapViewer.setPoiMoveCondition(poi -> loggedInAccount.developer() || poi.layer() == Layer.CUSTOM);
        refreshPois();

        var floatingControls = createFloatingControls(building);

        var layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));
        layeredPane.add(mapViewer, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(floatingControls, JLayeredPane.PALETTE_LAYER);

        mapViewer.addPoiClickListener(poi -> {
            var poiSummary = new POISummaryPanel(poi, database, true);
            poiSummary.addPoiChangeListener((currentPoi, newPoi) -> {
                var pois = database.getCurrentState().pois();
                List<POI> newPois =
                        Lists.replaceIndex(pois, pois.indexOf(currentPoi), newPoi);
                database.getHistory().pushState(database.getCurrentState().modifyPOIs(currentPois -> newPois));
            });
            poiSummary.addPoiDeleteListener(poiToDelete -> {
                var pois = database.getCurrentState().pois();
                List<POI> newPois = Lists.removeIndex(pois, pois.indexOf(poiToDelete));
                database.getHistory().pushState(database.getCurrentState().modifyPOIs(currentPois -> newPois));

                // Delete this panel
            });

            poiSummary.setBounds(800, 100, 200, 250);
            layeredPane.add(poiSummary, 1, 0);
        });

        // Make sure the mouse cursor can still change.
        mapViewer.setCursorComponent(layeredPane);

        var leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(toolbar, BorderLayout.PAGE_START);
        leftPanel.add(layeredPane, BorderLayout.CENTER);

        // the right panel, where the favourites and pois on the floor go
        var rightPanel = createSidebar(database, building);

        var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.75);
        splitPane.setOneTouchExpandable(true);
        splitPane.putClientProperty(FlatClientProperties.SPLIT_PANE_EXPANDABLE_SIDE, "left");
        splitPane.setDividerLocation(splitPane.getResizeWeight());

        add(splitPane);
    }

    private JPanel createToolbar() {
        var backButton = new JButton("Back");
        backButton.addActionListener(e -> backListeners.forEach(Runnable::run));

        var searchBar = new JTextField(30);
        searchBar.setMaximumSize(new Dimension(384, Short.MAX_VALUE));
        searchBar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search");
        searchBar.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSearchIcon());

        var createPOIButton = new JButton("Create POI");

        var toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.LINE_AXIS));
        toolbar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        toolbar.add(backButton);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(searchBar);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(createPOIButton);

        return toolbar;
    }

    // updating the favourites POI list
    private void updateFavouritePOIs(Database database) {
        POI[] poisToAdd =
                database.getCurrentState().pois().stream().filter(POI::favorite).toArray(POI[]::new);
        favoritesList.setListData(poisToAdd);
    }

    private JPanel createSidebar(Database database, Building building) {
        var poiListHeader = new JLabel("POIs on this Floor");
        poiListHeader.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");

        var poiListScroller = new JScrollPane(poiList);
        poiListScroller.setAlignmentX(0.0f);
        poiListScroller.getViewport().setPreferredSize(new Dimension(0, 400));

        var favoritesListHeader = new JLabel("Favourite POIs");
        favoritesListHeader.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");

        // favourite POIS on the list
        favoritesList.setCellRenderer(new POIFavouriteCellRenderer());
        updateFavouritePOIs(database);

        var favoritesListScroller = new JScrollPane(favoritesList);
        favoritesListScroller.setAlignmentX(0.0f);
        favoritesListScroller.getViewport().setPreferredSize(new Dimension(0, 200));

        var sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.PAGE_AXIS));

        sidebar.add(Box.createVerticalStrut(40)); // The height of the toolbar.
        sidebar.add(poiListHeader);
        sidebar.add(poiListScroller);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(favoritesListHeader);
        sidebar.add(favoritesListScroller);

        return sidebar;
    }

    private JPanel createFloatingControls(Building building) {
        var layerVisibilityPanel = new LayerVisibilityPanel(EnumSet.allOf(Layer.class));
        layerVisibilityPanel.addLayerToggleListener(mapViewer::setLayerVisible);

        var floorSwitcher = new FloorSwitcher(building.floors());
        floorSwitcher.addFloorSwitchListener(this::changeToFloor);

        var floatingControls = new JPanel();
        floatingControls.setOpaque(false);
        floatingControls.setLayout(new GridBagLayout());

        var constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.insets = new Insets(16, 16, 16, 16);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        floatingControls.add(layerVisibilityPanel, constraints);

        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LAST_LINE_START;
        floatingControls.add(floorSwitcher, constraints);

        return floatingControls;
    }

    // Does not update the selection of the floor switcher.
    private void changeToFloor(Floor floor) {
        currentFloor = floor;
        mapViewer.setCurrentMapUri(database.resolveFloorMapUri(floor));
        refreshPois();
    }

    private void refreshPois() {
        var pois = database.getCurrentState().pois().stream()
                .filter(poi -> poi.floor().equals(currentFloor))
                .toList();
        mapViewer.setDisplayedPois(pois);
        poiList.setListData(pois.toArray(POI[]::new));
    }

    public void addBackListener(Runnable listener) {
        backListeners.add(listener);
    }

    private class POIFavouriteCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            POI poi = (POI) value;

            var poiBuilding = database.getCurrentState().buildings().stream()
                    .filter(b -> b.floors().contains(poi.floor()))
                    .findFirst();

            var textBuilder = new StringBuilder();
            textBuilder.append("<html>");
            textBuilder.append(poi.name());

            if (poiBuilding.isPresent() && poiBuilding.get() != building) {
                textBuilder.append(" <font size=\"-2\" color=\"");

                Color color = UIManager.getColor("TextField.placeholderForeground");
                String hexColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                textBuilder.append(hexColor);
                textBuilder.append("\">");

                textBuilder.append(poiBuilding.get().name());
                textBuilder.append("</font>");
            }
            textBuilder.append("</html>");

            setText(textBuilder.toString());
            return this;
        }
    }
}
