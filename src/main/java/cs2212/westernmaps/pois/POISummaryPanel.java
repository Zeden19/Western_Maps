package cs2212.westernmaps.pois;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatButtonBorder;
import cs2212.westernmaps.core.Layer;
import cs2212.westernmaps.core.POI;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class POISummaryPanel extends JPanel {
    private static final int MAX_COLUMNS = 10;

    private POI poi;

    private final JTextField titleField;
    private final @Nullable JComboBox<Layer> layerComboBox;
    private final @Nullable JLabel layerLabel;
    private final JCheckBox favoriteCheckbox;
    private final JLabel locationLabel;
    private final JTextArea descriptionField;

    private final List<BiConsumer<POI, POI>> poiChangeListeners = new ArrayList<>();
    private final List<Consumer<POI>> poiDeleteListeners = new ArrayList<>();

    /**
     * Summary window of a POI that displays its metadata.
     *
     * @param poiToSummarize POI to summarize
     * @param developer If the logged-in user is a developer
     */
    public POISummaryPanel(POI poiToSummarize, boolean developer) {

        poi = poiToSummarize;

        JPanel summaryBox = new JPanel();
        summaryBox.setLayout(new BoxLayout(summaryBox, BoxLayout.PAGE_AXIS));
        summaryBox.add(Box.createRigidArea(new Dimension(0, 10)));

        // Title
        titleField = new JTextField(poi.name());
        titleField.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
        titleField.setEditable(developer);

        titleField.getDocument().addDocumentListener(new DocumentListener() {
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

        addToBox(summaryBox, titleField);

        // Layer
        JPanel layerBox = new JPanel();
        layerBox.setLayout(new BoxLayout(layerBox, BoxLayout.LINE_AXIS));

        JLabel layerIcon = new JLabel(poi.layer().getIcon());
        layerBox.add(layerIcon);

        if (developer) {
            layerComboBox = new JComboBox<>(Layer.values());
            layerComboBox.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Layer newLayer = (Layer) layerComboBox.getSelectedItem();

                    POI newPoi = new POI(
                            poi.name(), poi.description(), poi.x(), poi.y(), poi.favorite(), poi.floor(), newLayer);

                    poiChangeListeners.forEach(listener -> listener.accept(poi, newPoi));

                    poi = newPoi;
                    layerIcon.setIcon(poi.layer().getIcon());
                }
            });
            layerComboBox.setSelectedItem(poi.layer());

            layerLabel = null;
            layerBox.add(layerComboBox);
        } else {
            layerComboBox = null;
            layerLabel = new JLabel(poi.layer().name());
            layerBox.add(layerLabel);
        }

        addToBox(summaryBox, layerBox);

        // Favorite
        favoriteCheckbox = new JCheckBox("Favourite");
        favoriteCheckbox.setSelected(poi.favorite());
        // TODO: Add icon here
        favoriteCheckbox.addItemListener(e -> {
            POI newPoi =
                    new POI(poi.name(), poi.description(), poi.x(), poi.y(), !poi.favorite(), poi.floor(), poi.layer());
            poiChangeListeners.forEach(listener -> listener.accept(poi, newPoi));

            poi = newPoi;
        });
        addToBox(summaryBox, favoriteCheckbox);

        // Position
        locationLabel = new JLabel("Location: " + poi.x() + ", " + poi.y());
        // TODO: Add icon here
        addToBox(summaryBox, locationLabel);

        final boolean isCustom = poi.layer() == Layer.CUSTOM;

        // Description
        descriptionField = new JTextArea(poi.description(), 3, MAX_COLUMNS);
        descriptionField.setEditable(isCustom || developer);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        descriptionField.getDocument().addDocumentListener(new DocumentListener() {
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
        JScrollPane descScroll = new JScrollPane(descriptionField);
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

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(summaryBox);
        add(Box.createRigidArea(new Dimension(10, 0)));
        setBorder(new FlatButtonBorder());
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
