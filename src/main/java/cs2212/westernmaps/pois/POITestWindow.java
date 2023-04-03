package cs2212.westernmaps.pois;

import cs2212.westernmaps.core.*;
import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class POITestWindow extends JFrame {
    public POITestWindow() {
        super();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Floor testFloor = new Floor("2", "Floor 2", Path.of("test.svg"));
        POI testPOI = new POI("Electrical 1", "This is a description.", 50, 100, false, testFloor, Layer.UTILITIES);
        POI testCustomPOI = new POI(
                "Grad Club Patio", "This is an editable description.", 123, 456, false, testFloor, Layer.CUSTOM);
        List<POI> poiList = new ArrayList<>();
        poiList.add(testPOI);
        poiList.add(testCustomPOI);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.LINE_AXIS));

        try {
            Database database = Database.openDirectory(Path.of("data"));
            DatabaseState currentState = database.getCurrentState();
            List<POI> pois = new ArrayList<>(currentState.pois());
            pois.add(testPOI);
            pois.add(testCustomPOI);
            database.getHistory().pushState(new DatabaseState(currentState.accounts(), currentState.buildings(), pois));

            content.add(new POISummary(testPOI, database, true));
            content.add(new POISummary(testCustomPOI, database, true));
        } catch (IOException e) {
            System.out.println(e);
        }

        setContentPane(content);

        setPreferredSize(new Dimension(600, 400));
        pack();
    }
}
