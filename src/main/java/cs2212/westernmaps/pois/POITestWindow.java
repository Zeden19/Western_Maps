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
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed pharetra aliquam odio et lobortis.",
                50,
                100,
                false,
                new Floor("2", "Floor 2", Path.of("test.svg")),
                Layer.UTILITIES);
        setContentPane(new POISummary(testPOI));

        setPreferredSize(new Dimension(300, 400));
        pack();
    }
}
