package cs2212.westernmaps.pois;

import com.formdev.flatlaf.FlatClientProperties;
import cs2212.westernmaps.core.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class POISummary extends JPanel {
    private POI poi;
    /**
     * Summary window of a POI that displays its metadata.
     *
     * @param poiToSummarize POI to summarize
     * @param database Database
     * @param developer If the logged-in user is a developer
     */
    public POISummary(POI poiToSummarize, Database database, boolean developer) {

        final int MAX_COLUMNS = 10;

        poi = poiToSummarize;

        JPanel summaryBox = new JPanel();
        summaryBox.setLayout(new BoxLayout(summaryBox, BoxLayout.PAGE_AXIS));
        summaryBox.add(Box.createRigidArea(new Dimension(0, 10)));

        // Title
        JTextArea title = new JTextArea(poi.name(), 1, MAX_COLUMNS);
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
        title.setEditable(developer);
        title.setLineWrap(true);
        title.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                this.changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                this.changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                DatabaseState currentState = database.getCurrentState();
                List<POI> pois = new ArrayList<>(currentState.pois());
                int poiIndex = pois.indexOf(poi);
                Document doc = e.getDocument();
                String newTitle;
                try {
                    newTitle = doc.getText(0, doc.getLength());

                    poi = new POI(
                            newTitle, poi.description(), poi.x(), poi.y(), poi.favorite(), poi.floor(), poi.layer());

                    pois.set(poiIndex, poi);
                    database.getHistory()
                            .pushState(new DatabaseState(currentState.accounts(), currentState.buildings(), pois));
                } catch (BadLocationException ex) {
                    System.out.println("Error reading Document: " + e);
                }
            }
        });

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
                    DatabaseState currentState = database.getCurrentState();
                    List<POI> pois = new ArrayList<>(currentState.pois());
                    int poiIndex = pois.indexOf(poi);
                    Layer newLayer = (Layer) layerCombo.getSelectedItem();
                    poi = new POI(
                            poi.name(), poi.description(), poi.x(), poi.y(), poi.favorite(), poi.floor(), newLayer);
                    pois.set(poiIndex, poi);
                    database.getHistory()
                            .pushState(new DatabaseState(currentState.accounts(), currentState.buildings(), pois));
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
        favoriteCheck.setSelected(poi.favorite());
        // TODO: Add icon here
        favoriteCheck.addItemListener(e -> {
            DatabaseState currentState = database.getCurrentState();
            List<POI> pois = new ArrayList<>(currentState.pois());
            int poiIndex = pois.indexOf(poi);
            poi = new POI(poi.name(), poi.description(), poi.x(), poi.y(), !poi.favorite(), poi.floor(), poi.layer());
            pois.set(poiIndex, poi);
            database.getHistory().pushState(new DatabaseState(currentState.accounts(), currentState.buildings(), pois));
        });
        addToBox(summaryBox, favoriteCheck);

        // Position
        JLabel pos = new JLabel("Location: " + poi.x() + ", " + poi.y());
        // TODO: Add icon here
        addToBox(summaryBox, pos);

        final boolean isCustom = poi.layer() == Layer.CUSTOM;

        // Description
        JTextArea desc = new JTextArea(poi.description(), 3, MAX_COLUMNS);
        desc.setEditable(isCustom || developer);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                this.changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                this.changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                DatabaseState currentState = database.getCurrentState();
                List<POI> pois = new ArrayList<>(currentState.pois());
                int poiIndex = pois.indexOf(poi);
                Document doc = e.getDocument();
                try {
                    String newDesc = doc.getText(0, doc.getLength());

                    poi = new POI(poi.name(), newDesc, poi.x(), poi.y(), poi.favorite(), poi.floor(), poi.layer());
                    pois.set(poiIndex, poi);
                    database.getHistory()
                            .pushState(new DatabaseState(currentState.accounts(), currentState.buildings(), pois));
                } catch (BadLocationException ex) {
                    System.out.println("Error reading Document: " + e);
                }
            }
        });
        addToBox(summaryBox, desc);

        summaryBox.add(Box.createRigidArea(new Dimension(0, 10)));

        // Delete POI button
        if (isCustom || developer) {
            JButton deleteButton = new JButton("Delete POI");
            deleteButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int confirmDelete = JOptionPane.showConfirmDialog(
                            (Component) e.getSource(),
                            "Are you sure you want to delete " + poi.name() + "?",
                            "Delete POI",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmDelete == JOptionPane.YES_OPTION) {
                        DatabaseState currentState = database.getCurrentState();
                        List<POI> pois = new ArrayList<>(currentState.pois());
                        pois.remove(poi);
                        database.getHistory()
                                .pushState(new DatabaseState(currentState.accounts(), currentState.buildings(), pois));
                    }
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
