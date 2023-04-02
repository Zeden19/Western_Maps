package cs2212.westernmaps.pois;

import cs2212.westernmaps.core.*;
import java.awt.Dimension;
import java.nio.file.*;
import javax.swing.*;

public class POITestWindow extends JFrame {
    public POITestWindow() {
        super();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        POI testPOI = new POI(
                "Electrical 1",
                "This is an uneditable description.",
                50,
                100,
                false,
                new Floor("2", "Floor 2", Path.of("test.svg")),
                Layer.UTILITIES);
        POI testCustomPOI = new POI(
                "Grad Club Patio",
                "This is an editable description.",
                123,
                456,
                false,
                new Floor("2", "Floor 2", Path.of("test.svg")),
                Layer.CUSTOM);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.LINE_AXIS));
        content.add(new POISummary(testPOI));
        content.add(new POISummary(testCustomPOI));

        setContentPane(content);

        setPreferredSize(new Dimension(600, 400));
        pack();
    }
}
