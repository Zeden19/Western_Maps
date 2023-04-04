package cs2212.westernmaps.pois;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatButtonBorder;
import cs2212.westernmaps.core.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class POISummaryPanel extends JPanel {
    private static final int MAX_COLUMNS = 10;
    private POI poi;
    private final List<BiConsumer<POI, POI>> poiChangeListeners = new ArrayList<>();
    private final List<Consumer<POI>> poiDeleteListeners = new ArrayList<>();
    /**
     * Summary window of a POI that displays its metadata.
     *
     * @param poiToSummarize POI to summarize
     * @param database Database
     * @param developer If the logged-in user is a developer
     */
    public POISummaryPanel(POI poiToSummarize, Database database, boolean developer) {

        poi = poiToSummarize;

        JPanel summaryBox = new JPanel();
        summaryBox.setLayout(new BoxLayout(summaryBox, BoxLayout.PAGE_AXIS));
        summaryBox.add(Box.createRigidArea(new Dimension(0, 10)));

        // Title
        JTextField title = new JTextField(poi.name());
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
        title.setEditable(developer);

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
                Document doc = e.getDocument();
                String newTitle;
                try {
                    newTitle = doc.getText(0, doc.getLength());

                    POI newPoi = new POI(
                            newTitle, poi.description(), poi.x(), poi.y(), poi.favorite(), poi.floor(), poi.layer());

                    poiChangeListeners.forEach(listener -> listener.accept(poi, newPoi));

                    poi = newPoi;
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
                    Layer newLayer = (Layer) layerCombo.getSelectedItem();

                    POI newPoi = new POI(
                            poi.name(), poi.description(), poi.x(), poi.y(), poi.favorite(), poi.floor(), newLayer);

                    poiChangeListeners.forEach(listener -> listener.accept(poi, newPoi));

                    poi = newPoi;
                    layerIcon.setIcon(poi.layer().getIcon());
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
            POI newPoi =
                    new POI(poi.name(), poi.description(), poi.x(), poi.y(), !poi.favorite(), poi.floor(), poi.layer());
            poiChangeListeners.forEach(listener -> listener.accept(poi, newPoi));

            poi = newPoi;
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
                Document doc = e.getDocument();
                try {
                    String newDesc = doc.getText(0, doc.getLength());

                    POI newPoi =
                            new POI(poi.name(), newDesc, poi.x(), poi.y(), poi.favorite(), poi.floor(), poi.layer());
                    poiChangeListeners.forEach(listener -> listener.accept(poi, newPoi));

                    poi = newPoi;
                } catch (BadLocationException ex) {
                    System.out.println("Error reading Document: " + e);
                }
            }
        });
        JScrollPane descScroll = new JScrollPane(desc);
        addToBox(summaryBox, descScroll);

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
                        poiDeleteListeners.forEach(listener -> listener.accept(poi));
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
        contentBox.setBorder(new FlatButtonBorder());

        add(contentBox);
    }

    /**
     * Registers an event listener that is called when a field of the POI summary is edited.
     * @param listener Event listener, taking two arguments: the old POI and the modified POI.
     */
    public void addPoiChangeListener(BiConsumer<POI, POI> listener) {
        poiChangeListeners.add(listener);
    }

    /**
     * Registers an event listener that is called when a POI is deleted.
     * @param listener Event listener, taking the POI to be deleted as an argument.
     */
    public void addPoiDeleteListener(Consumer<POI> listener) {
        poiDeleteListeners.add(listener);
    }

    private static void addToBox(JPanel box, JComponent component) {
        box.add(component);
        component.setAlignmentX(LEFT_ALIGNMENT);
        box.add(Box.createRigidArea(new Dimension(0, 5)));
    }
}
