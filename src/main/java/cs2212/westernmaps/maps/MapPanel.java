package cs2212.westernmaps.maps;

import static javax.swing.SwingUtilities.convertPoint;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.formdev.flatlaf.ui.FlatBorder;
import cs2212.westernmaps.core.*;
import cs2212.westernmaps.pois.POISummaryPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.swing.*;

public final class MapPanel extends JPanel {
    private final Database database;

    private Building building;
    private final Account loggedInAccount;

    private Floor currentFloor;

    private final MapViewerPanel mapViewer;
    private final POISummaryPanel poiSummaryPanel;
    private final List<Runnable> backListeners = new ArrayList<>();
    private final List<Consumer<Building>> changeTitleListeners = new ArrayList<>();
    private final JList<POI> poiList = new JList<>();
    private final JList<POI> favoritesList = new JList<>();

    private JList<POI> searchResults = new JList<>();
    private FloorSwitcher floorSwitcher;

    private final Container glassPane;
    private JPanel floatingControls = new JPanel();

    private final EnumSet<Layer> visibleLayers = EnumSet.allOf(Layer.class);

    private JLabel databaseSaved = new JLabel();
    private JLabel saveFailed = new JLabel();

    private int numPendingCalls =
            0; // the number of pending calls, used to make sure the database saved message is displayed for the most
    // recent save
    private final LayerVisibilityPanel layerVisibilityPanel = new LayerVisibilityPanel(EnumSet.allOf(Layer.class));
    // used to display the layer toggles


    // the main map panel
    public MapPanel(Database database, Building building, Account loggedInAccount, Container glassPane) {
        this.database = database;
        this.building = building;
        this.loggedInAccount = loggedInAccount;
        this.glassPane = glassPane;

        glassPane.setLayout(new GridBagLayout());

        // This determines what MainWindow will use as its title.
        setName(building.name());
        glassPane.setLayout(null);

        // This determines what MainWindow will use as its title.
        setName(building.name());
        setLayout(new BorderLayout());

        // for everything at the top of the map panel, like search, back, and create poi.
        var toolbar = createToolbar(building, database);

        currentFloor = building.floors().get(0);
        floorSwitcher = new FloorSwitcher(building.floors());
        floorSwitcher.addFloorSwitchListener(this::changeToFloor);
        var initialMapUri = database.resolveFloorMapUri(currentFloor);

        // for the summary panel
        poiSummaryPanel = new POISummaryPanel(loggedInAccount);

        poiSummaryPanel.setVisible(false);

        poiSummaryPanel.addPoiChangeListener((oldPoi, newPoi) -> {
            var state = database.getCurrentState().modifyPOIs(pois -> pois.stream()
                    .map(poi -> poi.equals(oldPoi) ? newPoi : poi)
                    .toList());
            database.getHistory().pushState(state);
            try {
                database.save();
                showLabel(databaseSaved);
            } catch (IOException ex) {
                showLabel(saveFailed);
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
                showLabel(databaseSaved);
            } catch (IOException ex) {
                showLabel(saveFailed);
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
                showLabel(databaseSaved);
            } catch (IOException ex) {
                showLabel(saveFailed);
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

        var floatingControls = createFloatingControls();

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

    // toolbar for the top, the search create poi buttons, and database saved text
    private JPanel createToolbar(Building building, Database database) {
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

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
            var results = getSearchResults(searchBar.getText().split(" "), database, building);

            if (results.isEmpty()) {
            } else {
                searchResults.setListData(results.toArray(POI[]::new));
            }
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
                showLabel(databaseSaved);
            } catch (IOException ex) {
                showLabel(saveFailed);
            }
            refreshPois();
        });

        // wrapping it all in a toolbar
        var toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.LINE_AXIS));
        toolbar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Label if no search results are found
        JLabel noResultsFound = new JLabel("No results found");
        noResultsFound.setHorizontalAlignment(SwingConstants.CENTER);
        noResultsFound.setBorder(new FlatBorder());
        cardPanel.setBackground(UIManager.getColor("List.background"));

        // the search
        searchResults.setCellRenderer(new POICellRenderer(poi -> poi.floor().longName()));
        var poiListScroller = new JScrollPane(searchResults);

        cardPanel.add(noResultsFound, "no results");
        cardPanel.add(poiListScroller, "results");
        searchBar.addActionListener(e -> {
            var bounds = searchBar.getBounds();
            var position = convertPoint(toolbar, bounds.x, bounds.y, glassPane);
            bounds.x = position.x;
            bounds.y = position.y + bounds.height;
            bounds.height = 200;
            cardPanel.setBounds(bounds);
            glassPane.setVisible(true);

            var results = getSearchResults(searchBar.getText().split(" "), database, building);
            if (results.isEmpty()) {
                // making a "blank" POI as search results can only contain a list of POIS
                searchResults.setListData(new POI[] {});
                cardLayout.show(cardPanel, "no results");
            } else {
                searchResults.setListData(results.toArray(POI[]::new));
                cardLayout.show(cardPanel, "results");
            }
        });
        glassPane.add(cardPanel);

        // closing the search results when the user clicks off the search EXCEPT when the user clicks on the list
        searchBar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                var focused = evt.getOppositeComponent();
                if (focused != null && !(focused.equals(searchResults))) {
                    glassPane.setVisible(false);
                }
            }
        });

        // closing the search results when the user clicks from the list and clicks off
        searchResults.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                var focused = evt.getOppositeComponent();
                if (focused != null && !(focused.equals(searchBar))) glassPane.setVisible(false);
            }
        });

        // changes saved label
        databaseSaved = new JLabel("Changes saved.");
        databaseSaved.putClientProperty(FlatClientProperties.STYLE_CLASS, "regular");
        databaseSaved.setForeground(UIManager.getColor("Actions.Green"));
        databaseSaved.setVisible(false);

        // failed to save changes label
        saveFailed = new JLabel("Failed to save changes.");
        saveFailed.putClientProperty(FlatClientProperties.STYLE_CLASS, "regular");
        saveFailed.setForeground(UIManager.getColor("Actions.Red"));
        saveFailed.setVisible(false);

        // the search results
        ListAction.setListAction(searchResults, new JumpToPoiAction());

        toolbar.add(backButton);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(searchBar);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(databaseSaved);
        toolbar.add(saveFailed);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(createPOIButton);

        return toolbar;
    }

    // Sidebar for the favourite pois and pois on the map
    private JPanel createSidebar(Database database, Building building) {
        var poiListHeader = new JLabel("POIs on this Floor");
        poiListHeader.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");

        var poiListScroller = new JScrollPane(poiList);
        poiListScroller.setAlignmentX(0.0f);
        poiListScroller.getViewport().setPreferredSize(new Dimension(0, 400));

        // if a poi on list got selected then jump to that poi
        ListAction.setListAction(poiList, new JumpToPoiAction());
        // if a poi onm favourite list got selected then jump to that poi
        ListAction.setListAction(favoritesList, new JumpToPoiAction());

        var favoritesListHeader = new JLabel("Favourite POIs");
        favoritesListHeader.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");

        // favourite POIS on the list
        favoritesList.setCellRenderer(new POICellRenderer(poi -> {
            // Check if the POI is on any floor in the current building. If so,
            // don't show any subtitle.
            if (building.floors().contains(poi.floor())) {
                return null;
            }

            var poiBuilding = database.getCurrentState().buildings().stream()
                    .filter(b -> b.floors().contains(poi.floor()))
                    .findFirst();
            return poiBuilding.map(Building::name).orElse(null);
        }));
        updateFavouritePOIs(database);

        var favoritesListScroller = new JScrollPane(favoritesList);
        favoritesListScroller.setAlignmentX(0.0f);
        favoritesListScroller.getViewport().setPreferredSize(new Dimension(0, 200));

        var sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.PAGE_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 8));

        sidebar.add(Box.createVerticalStrut(40)); // The height of the toolbar.
        sidebar.add(poiListHeader);
        sidebar.add(poiListScroller);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(favoritesListHeader);
        sidebar.add(favoritesListScroller);

        return sidebar;
    }

    // creating the layer visibility panel and the floor switcher
    private JPanel createFloatingControls() {
        layerVisibilityPanel.addLayerToggleListener(this::setLayerVisible);

        floatingControls = new JPanel();
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

    // getting the search results
    private List<POI> getSearchResults(String[] query, Database database, Building building) {
        List<POI> pois = database.getCurrentState().pois();

        // Going through the list of words
        for (int i = 0; i < query.length; i++) {
            int finalI = i;

            // Basically looking through all the words and removing POIs in the list that don't match
            pois = pois.stream()
                    .filter(poi ->
                            building.floors().contains(poi.floor()) && (poiMatches(query[finalI], poi, building)))
                    .sorted()
                    .toList();
        }
        return pois;
    }

    // Getting if the POI in the search matches
    private boolean poiMatches(String word, POI poi, Building building) {
        String wordLowerCase = word.toLowerCase();
        return poi.name().toLowerCase().contains(wordLowerCase)
                || poi.description().toLowerCase().contains(wordLowerCase)
                || poi.layer().name().toLowerCase().contains(wordLowerCase)
                || poi.floor().longName().toLowerCase().contains(wordLowerCase)
                || building.name().toLowerCase().contains(wordLowerCase);
    }


    // updating the favourites POI list
    private void updateFavouritePOIs(Database database) {
        POI[] poisToAdd = database.getCurrentState().pois().stream()
                .filter(poi -> poi.isFavoriteOfAccount(loggedInAccount) && isPoiVisible(poi))
                .sorted()
                .toArray(POI[]::new);
        favoritesList.setListData(poisToAdd);

        // favourite POIS on the list
        favoritesList.setCellRenderer(new POICellRenderer(poi -> {
            // Check if the POI is on any floor in the current building. If so,
            // don't show any subtitle.
            if (building.floors().contains(poi.floor())) {
                return null;
            }
            var poiBuilding = database.getCurrentState().buildings().stream()
                    .filter(b -> b.floors().contains(poi.floor()))
                    .findFirst();
            return poiBuilding.map(Building::name).orElse(null);
        }));
    }

    // Updating the map to a new floor
    private void changeToFloor(Floor floor) {
        currentFloor = floor;
        mapViewer.setCurrentMapUri(database.resolveFloorMapUri(floor));
        floorSwitcher.setSelectedFloor(floor);
        poiSummaryPanel.setVisible(false);
        refreshPois();
    }

    // when the user selects a POI from favourite, search or POI list, this function will jump to the poi and
    // open the summary panel
    private void jumpToPoi(POI poi) {
        // if the poi selected is not in the same building then change building
        if (!building.floors().contains(poi.floor())) {

            // we have to remove the current floor switcher to replace it with another one
            floatingControls.remove(floorSwitcher);

            database.getCurrentState().buildings().stream()
                    .filter(b -> b.floors().contains(poi.floor()))
                    .findFirst()
                    .ifPresent(buildingToSwitch -> building = buildingToSwitch);

            // now we make a new floor switcher so that the user can move to different floors between the new building
            floorSwitcher = new FloorSwitcher(building.floors());
            floorSwitcher.addFloorSwitchListener(this::changeToFloor);

            // setting position of the floor switcher
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.insets = new Insets(16, 16, 16, 16);
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            constraints.gridy = 1;
            constraints.gridx = 0;
            constraints.anchor = GridBagConstraints.LAST_LINE_START;

            changeTitleListeners.forEach(listener -> listener.accept(building));

            // adding floor switcher
            floatingControls.add(floorSwitcher, constraints);
            updateFavouritePOIs(database);
        }

        // Switch to the floor containing the selected POI if POI is on a different floor
        if (!poi.floor().equals(currentFloor)) {
            changeToFloor(poi.floor());
        }

        // Scroll map to put the POI in the center.
        mapViewer.scrollPoiToCenter(poi);

        // forcing layer to be visible
        layerVisibilityPanel.setLayerChecked(poi.layer(), true);

        // opening the summary panel
        poiSummaryPanel.setCurrentPoi(poi);
        poiSummaryPanel.setVisible(true);
    }


    private void refreshPois() {
        var pois = database.getCurrentState().pois().stream()
                .filter(poi -> poi.floor().equals(currentFloor) && isPoiVisible(poi))
                .filter(poi -> poi.floor().equals(currentFloor))
                .sorted()
                .toList();
        mapViewer.setDisplayedPois(pois);
        poiList.setListData(pois.toArray(POI[]::new));
        updateFavouritePOIs(database);
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

    public void addChangeTitleListener(Consumer<Building> listener) {
        changeTitleListeners.add(listener);
    }

    // Showing the database saved label
    private void showLabel(JLabel label) {
        numPendingCalls++;
        label.setVisible(true);
        Timer timer = new Timer(2000, e -> {
            numPendingCalls--;
            if (numPendingCalls == 0) {
                label.setVisible(false);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Used to render small grey text beside search and favourites list
    private static class POICellRenderer extends DefaultListCellRenderer {
        private final SubtitleProvider subtitleProvider;

        public POICellRenderer(SubtitleProvider subtitleProvider) {
            this.subtitleProvider = subtitleProvider;
        }

        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            POI poi = (POI) value;

            var textBuilder = new StringBuilder();
            textBuilder.append("<html>");
            textBuilder.append(poi.name());

            var subtitle = subtitleProvider.getSubtitle(poi);

            if (subtitle != null) {
                textBuilder.append(" <font size=\"-2\" color=\"");

                Color color = UIManager.getColor("TextField.placeholderForeground");
                String hexColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                textBuilder.append(hexColor);
                textBuilder.append("\">");

                textBuilder.append(subtitle);
                textBuilder.append("</font>");
            }
            textBuilder.append("</html>");

            setText(textBuilder.toString());
            return this;
        }

        public interface SubtitleProvider {
            @Nullable String getSubtitle(POI poi);
        }
    }

    private class JumpToPoiAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            var list = (JList<?>) e.getSource();
            var poi = (POI) list.getSelectedValue();
            if (poi != null) {
                jumpToPoi(poi);
            }
        }
    }
}
