package cs2212.westernmaps.maps;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import cs2212.westernmaps.core.Building;
import cs2212.westernmaps.core.Floor;
import cs2212.westernmaps.core.Layer;
import cs2212.westernmaps.core.POI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public final class MapPanel extends JPanel {
    private final List<Runnable> backListeners = new ArrayList<>();

    public MapPanel(Building building) {
        // This determines what MainWindow will use as its title.
        setName(building.name());

        // temporary code, just printing out name of selected building
        System.out.println(building.name());

        setLayout(new BorderLayout());

        var toolbar = createToolbar();

        // Temporary code; remove before merging.
        var uri = Path.of("MiddleSex-2.svg").toUri();
        var floor = new Floor("T", "Test Floor", Path.of("asdf"));
        var pois = List.of(
                new POI("Test POI", "POI added for testing.", 500, 300, false, floor, Layer.UTILITIES),
                new POI("Test POI 2", "POI added for testing.", 600, 300, false, floor, Layer.CLASSROOMS),
                new POI("Test POI 3", "POI added for testing.", 600, 400, false, floor, Layer.ACCESSIBILITY));

        var mapViewer = new MapViewerPanel(uri, pois);

        var leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(toolbar, BorderLayout.PAGE_START);
        leftPanel.add(mapViewer, BorderLayout.CENTER);

        var rightPanel = createSidebar();

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

    private JPanel createSidebar() {
        var poiListHeader = new JLabel("POIs on this map");
        poiListHeader.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");

        var poiList = new JList<POI>();

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

    public void addBackListener(Runnable listener) {
        backListeners.add(listener);
    }
}
