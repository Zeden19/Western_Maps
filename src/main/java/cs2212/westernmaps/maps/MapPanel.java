package cs2212.westernmaps.maps;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import cs2212.westernmaps.core.*;
import cs2212.westernmaps.pois.POISummaryPanel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;

public final class MapPanel extends JPanel {
    private final Database database;
    private final Building building;
    private final Account loggedInAccount;
    private Floor currentFloor;

    private final MapViewerPanel mapViewer;
    private final POISummaryPanel poiSummaryPanel;

    private final List<Runnable> backListeners = new ArrayList<>();
    private final JList<POI> poiList = new JList<>();
    private final JList<POI> favoritesList = new JList<>();
    private JList<String> searchResults = new JList<>();

    private final EnumSet<Layer> visibleLayers = EnumSet.allOf(Layer.class);

    public MapPanel(Database database, Building building, Account loggedInAccount, Container glassPane) {

        this.database = database;
        this.building = building;
        this.loggedInAccount = loggedInAccount;

        glassPane.setLayout(new GridBagLayout());

        // This determines what MainWindow will use as its title.
        setName(building.name());
        glassPane.setLayout(null);

        // This determines what MainWindow will use as its title.
        setName(building.name());
        setLayout(new BorderLayout());

        // for everything at the top of the map panel, like search, back, and create poi.
        var toolbar = createToolbar(glassPane, building, database);

        currentFloor = building.floors().get(0);
        var initialMapUri = database.resolveFloorMapUri(currentFloor);

        poiSummaryPanel = new POISummaryPanel(loggedInAccount);
        poiSummaryPanel.setVisible(false);

        poiSummaryPanel.addPoiChangeListener((oldPoi, newPoi) -> {
            var state = database.getCurrentState().modifyPOIs(pois -> pois.stream()
                    .map(poi -> poi.equals(oldPoi) ? newPoi : poi)
                    .toList());
            database.getHistory().pushState(state);
            try {
                database.save();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            refreshPois();
        });

        poiSummaryPanel.addPoiDeleteListener(oldPoi -> {
            var state = database.getCurrentState()
                    .modifyPOIs(pois ->
                            pois.stream().filter(poi -> !poi.equals(oldPoi)).toList());
            database.getHistory().pushState(state);
            try {
                database.save();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            poiSummaryPanel.setVisible(false);
            refreshPois();
        });

        mapViewer = new MapViewerPanel(initialMapUri, List.of());
        mapViewer.addPoiClickListener(poi -> {
            poiSummaryPanel.setCurrentPoi(poi);
            poiSummaryPanel.setVisible(true);
        });

        mapViewer.addPoiMoveListener((oldPoi, location) -> {
            var newPoi = oldPoi.withLocation(location.x, location.y);
            var newState = database.getCurrentState().modifyPOIs(pois -> pois.stream()
                    .map(poi -> poi == oldPoi ? newPoi : poi)
                    .toList());
            database.getHistory().pushState(newState);

            // Save changes to disk.
            try {
                database.save();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            // If the summary panel is open, make sure it's up to date.
            if (poiSummaryPanel.isVisible()) {
                poiSummaryPanel.setCurrentPoi(newPoi);
            }
            refreshPois();
        });
        mapViewer.setPoiMoveCondition(poi -> loggedInAccount.developer() || poi.layer() == Layer.CUSTOM);
        mapViewer.setPoiVisibleCondition(poi -> isLayerVisible(poi.layer()) && isPoiVisible(poi));
        refreshPois();

        var floatingControls = createFloatingControls(building);

        var layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));
        layeredPane.add(mapViewer, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(floatingControls, JLayeredPane.PALETTE_LAYER);

        // Make sure the mouse cursor can still change.
        mapViewer.setCursorComponent(layeredPane);

        // everything to the left of the map panel
        var leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(toolbar, BorderLayout.PAGE_START);
        leftPanel.add(layeredPane, BorderLayout.CENTER);

        // the right panel, where the favourites and pois on the floor go
        var rightPanel = createSidebar(database, building);

        // wrapping everything in a split pane
        var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.75);
        splitPane.setOneTouchExpandable(true);
        splitPane.putClientProperty(FlatClientProperties.SPLIT_PANE_EXPANDABLE_SIDE, "left");
        splitPane.setDividerLocation(splitPane.getResizeWeight());

        add(splitPane);
    }

    private JPanel createToolbar(Container glassPane, Building building, Database database) {

        // back button
        var backButton = new JButton("Back");
        backButton.addActionListener(e -> backListeners.forEach(Runnable::run));

        // search bar
        var searchBar = new JTextField(30);
        searchBar.setMaximumSize(new Dimension(384, Short.MAX_VALUE));
        searchBar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search");
        searchBar.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSearchIcon());

        searchResults = new JList<>();
        searchResults.setBounds(88, 30, 374, 200);
        searchResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResults.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        searchResults.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")));

        searchBar.addActionListener(e -> {
            var query = searchBar.getText();
            glassPane.add(searchResults);
            glassPane.setVisible(true);
        });

        // removing list when focus is lost
        searchResults.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                glassPane.setVisible(false);
            }
        });

        // create POI button
        var createPOIButton = new JButton("Create POI");
        createPOIButton.addActionListener(e -> {
            // Determine the location in the center of the map view right now,
            // and use that as the location of the new POI.
            var mapViewerBounds = mapViewer.getBounds();
            var location = new Point(mapViewerBounds.width / 2, mapViewerBounds.height / 2);
            mapViewer.componentToMapPosition(location, location);

            var poi = new POI(
                    "New POI", "", location.x, location.y, Set.of(), currentFloor, Layer.CUSTOM, loggedInAccount);
            poiSummaryPanel.setCurrentPoi(poi);
            poiSummaryPanel.setVisible(true);

            var state = database.getCurrentState().modifyPOIs(pois -> Lists.append(pois, poi));
            database.getHistory().pushState(state);
            try {
                database.save();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            refreshPois();
        });

        // wrapping it all in a toolbar
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
        POI[] poisToAdd = database.getCurrentState().pois().stream()
                .filter(poi -> poi.isFavoriteOfAccount(loggedInAccount) && isPoiVisible(poi))
                .toArray(POI[]::new);
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
        layerVisibilityPanel.addLayerToggleListener(this::setLayerVisible);

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

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.FIRST_LINE_END;
        floatingControls.add(poiSummaryPanel, constraints);

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
                .filter(poi -> poi.floor().equals(currentFloor) && isPoiVisible(poi))
                .toList();
        mapViewer.setDisplayedPois(pois);
        poiList.setListData(pois.toArray(POI[]::new));
    }

    private boolean isPoiVisible(POI poi) {
        return poi.onlyVisibleTo() == null || poi.onlyVisibleTo().equals(loggedInAccount);
    }

    /**
     * Determines if the given {@linkplain Layer layer} is currently visible.
     *
     * @return Whether the given layer is visible.
     */
    private boolean isLayerVisible(Layer layer) {
        return visibleLayers.contains(layer);
    }

    /**
     * Changes the visibility of the given {@linkplain Layer layer}.
     *
     * @param layer   The layer to change the visibility of.
     * @param visible Whether the layer should be visible.
     */
    private void setLayerVisible(Layer layer, boolean visible) {
        if (visible) {
            visibleLayers.add(layer);
        } else {
            visibleLayers.remove(layer);
        }
        repaint();
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
