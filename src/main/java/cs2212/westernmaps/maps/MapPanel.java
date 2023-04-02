package cs2212.westernmaps.maps;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import cs2212.westernmaps.core.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public final class MapPanel extends JPanel {
    private final Database database;
    private final MapViewerPanel mapViewer;

    private final List<Runnable> backListeners = new ArrayList<>();

    public MapPanel(Database database, Building building, Account loggedInAccount) {
        this.database = database;

        // temporary code, just printing out if user is a developer
        System.out.println(loggedInAccount.developer());

        // This determines what MainWindow will use as its title.
        setName(building.name());
        setLayout(new BorderLayout());

        var toolbar = createToolbar();

        var floors = building.floors();
        var initialFloor = floors.get(0);
        var initialMapUri = database.resolveFloorMapUri(initialFloor);

        mapViewer = new MapViewerPanel(initialMapUri, List.of());
        mapViewer.addPoiClickListener(poi -> System.out.println(poi.name() + " clicked!"));
        // This adds the POIs to the map.
        changeToFloor(initialFloor);

        var floatingControls = createFloatingControls(building);

        var layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));
        layeredPane.add(mapViewer, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(floatingControls, JLayeredPane.PALETTE_LAYER);
        // Make sure the mouse cursor can still change.
        mapViewer.setCursorComponent(layeredPane);

        var leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(toolbar, BorderLayout.PAGE_START);
        leftPanel.add(layeredPane, BorderLayout.CENTER);

        // todo make the panel get the actual floor, instead of just the first one
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

    private String[] getPOIsToAdd(Floor floor, Database database) {
        List<POI> pois = database.getCurrentState().pois();
        List<POI> poisOnFloor =
                pois.stream().filter(a -> a.floor().equals(floor)).toList();
        List<String> poiNames = poisOnFloor.stream().map(POI::name).toList();

        return (poiNames.toArray(String[]::new));
    }

    private JPanel createSidebar(Database database, Building building) {
        var poiListHeader = new JLabel("POIs on this map");
        poiListHeader.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");

        // todo make the panel get the actual floor, instead of just the first one
        var poiList = new JList<>(getPOIsToAdd(building.floors().get(0), database));

        var poiListScroller = new JScrollPane(poiList);
        poiListScroller.setAlignmentX(0.0f);
        poiListScroller.getViewport().setPreferredSize(new Dimension(0, 400));

        var favoritesListHeader = new JLabel("Favourite POIs");
        favoritesListHeader.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");

        var favoritesList = new JList<POI>();

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
        var floorSwitcher = new FloorSwitcher(building.floors());
        floorSwitcher.addFloorSwitchListener(this::changeToFloor);

        var floatingControls = new JPanel();
        floatingControls.setOpaque(false);
        floatingControls.setLayout(new GridBagLayout());

        var constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LAST_LINE_START;
        constraints.insets = new Insets(16, 16, 16, 16);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        floatingControls.add(floorSwitcher, constraints);

        return floatingControls;
    }

    // Does not update the selection of the floor switcher.
    private void changeToFloor(Floor floor) {
        mapViewer.setCurrentMapUri(database.resolveFloorMapUri(floor));

        var pois = database.getCurrentState().pois().stream()
                .filter(poi -> poi.floor().equals(floor))
                .toList();
        mapViewer.setDisplayedPois(pois);
    }

    public void addBackListener(Runnable listener) {
        backListeners.add(listener);
    }
}
