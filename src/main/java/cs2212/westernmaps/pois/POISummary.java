package cs2212.westernmaps.pois;

import com.formdev.flatlaf.FlatClientProperties;
import cs2212.westernmaps.core.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;

public class POISummary extends JPanel {
    /**
     * Summary window of a POI that displays its metadata.
     *
     * @param poi POI to summarize
     */
    public POISummary(POI poi) {

        final int MAX_COLUMNS = 10;

        // Temporary variable, will eventually check if logged-in user is a developer
        final boolean developer = true;

        JPanel summaryBox = new JPanel();
        summaryBox.setLayout(new BoxLayout(summaryBox, BoxLayout.PAGE_AXIS));
        summaryBox.add(Box.createRigidArea(new Dimension(0, 10)));

        // Title
        JTextArea title = new JTextArea(poi.name(), 1, MAX_COLUMNS);
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
        title.setEditable(developer);
        addToBox(summaryBox, title);

        // Layer
        JPanel layerBox = new JPanel();
        layerBox.setLayout(new BoxLayout(layerBox, BoxLayout.LINE_AXIS));

        JLabel layerIcon = new JLabel(poi.layer().getIcon());
        layerBox.add(layerIcon);

        if (developer) {
            JComboBox<Layer> layerCombo = new JComboBox<>(Layer.values());
            layerCombo.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO: Call a "setLayer" function here
                }
            });
            layerCombo.setSelectedItem(poi.layer());

            layerBox.add(layerCombo);
        } else {
            JLabel layerLabel = new JLabel(poi.layer().name());
            layerBox.add(layerLabel);
        }

        addToBox(summaryBox, layerBox);

        // Favorite
        JCheckBox favoriteCheck = new JCheckBox("Favourite");
        // TODO: Add icon here
        favoriteCheck.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                // TODO: Call a "toggleFavorite" function here
            }
        });
        addToBox(summaryBox, favoriteCheck);

        // Position
        JLabel pos = new JLabel("Location: " + poi.x() + ", " + poi.y());
        // TODO: Add icon here
        addToBox(summaryBox, pos);

        final boolean isCustom = poi.layer() == Layer.CUSTOM;

        // Description
        JTextArea desc = new JTextArea(poi.description(), 3, MAX_COLUMNS);
        desc.setEditable(isCustom);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        addToBox(summaryBox, desc);

        summaryBox.add(Box.createRigidArea(new Dimension(0, 10)));

        // Delete POI button
        if (isCustom) {
            JButton deleteButton = new JButton("Delete POI");
            deleteButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // TODO: Call a "deletePOI" function here
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
