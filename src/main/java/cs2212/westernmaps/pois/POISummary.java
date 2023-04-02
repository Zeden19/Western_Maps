package cs2212.westernmaps.pois;

import com.formdev.flatlaf.FlatClientProperties;
import cs2212.westernmaps.core.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;

public class POISummary extends JPanel {
    public POISummary(POI poi) {

        JPanel summaryBox = new JPanel();
        summaryBox.setLayout(new BoxLayout(summaryBox, BoxLayout.PAGE_AXIS));
        summaryBox.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel title = new JLabel(poi.name());
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
        addToBox(summaryBox, title);

        JLabel layer = new JLabel(poi.layer().name());
        layer.setIcon(poi.layer().getIcon());
        addToBox(summaryBox, layer);

        JCheckBox favouriteCheck = new JCheckBox("Favourite?");
        // TODO: Add icon here
        favouriteCheck.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                // Toggle POI favourite
            }
        });
        addToBox(summaryBox, favouriteCheck);

        JLabel pos = new JLabel(poi.x() + ", " + poi.y());
        // TODO: Add icon here
        addToBox(summaryBox, pos);

        final boolean isCustom = poi.layer() == Layer.CUSTOM;

        JTextArea desc = new JTextArea(poi.description());
        desc.setEditable(isCustom);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        addToBox(summaryBox, desc);

        summaryBox.add(Box.createRigidArea(new Dimension(0, 10)));

        if (isCustom) {
            JButton deleteButton = new JButton("Delete POI");
            deleteButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Call a delete POI function
                }
            });
            addToBox(summaryBox, deleteButton);
        }

        JPanel contentBox = new JPanel();
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.LINE_AXIS));
        contentBox.add(Box.createRigidArea(new Dimension(10, 0)));
        contentBox.add(summaryBox);
        contentBox.add(Box.createRigidArea(new Dimension(10, 0)));
        contentBox.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")));

        add(contentBox);
    }

    private void addToBox(JPanel box, JComponent component) {
        box.add(component);
        component.setAlignmentX(LEFT_ALIGNMENT);
        box.add(Box.createRigidArea(new Dimension(0, 5)));
    }
}
