package cs2212.westernmaps.pois;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatButtonBorder;
import cs2212.westernmaps.MiscIcons;
import cs2212.westernmaps.core.Layer;
import cs2212.westernmaps.core.POI;
import java.awt.*;
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
    private static final int MAX_COLUMNS = 20;

    private @Nullable POI poi;
    private final boolean developer;

    private final JTextField titleField;
    private final JLabel layerIcon;
    private final JComboBox<Layer> layerComboBox;
    private final JLabel layerLabel;
    private final JCheckBox favoriteCheckbox;
    private final JLabel locationLabel;
    private final JTextArea descriptionField;

    private final JButton deleteButton;

    private final List<BiConsumer<POI, POI>> poiChangeListeners = new ArrayList<>();
    private final List<Consumer<POI>> poiDeleteListeners = new ArrayList<>();

    /**
     * Summary window of a POI that displays its metadata.
     *
     * @param developer If the logged-in user is a developer
     */
    public POISummaryPanel(boolean developer) {
        this.developer = developer;

        JPanel summaryBox = new JPanel();
        summaryBox.setLayout(new BoxLayout(summaryBox, BoxLayout.PAGE_AXIS));
        summaryBox.add(Box.createRigidArea(new Dimension(0, 10)));

        // Title
        titleField = new JTextField();
        titleField.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
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
                if (poi == null) {
                    return;
                }

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

        //        addToBox(summaryBox, titleField);

        // Layer
        JPanel layerBox = new JPanel();
        layerBox.setLayout(new BoxLayout(layerBox, BoxLayout.LINE_AXIS));

        layerIcon = new JLabel();
        layerBox.add(layerIcon);

        layerBox.add(Box.createHorizontalStrut(6));

        layerComboBox = new JComboBox<>(Layer.values());
        layerComboBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (poi == null) {
                    return;
                }

                Layer newLayer = (Layer) layerComboBox.getSelectedItem();

                POI newPoi =
                        new POI(poi.name(), poi.description(), poi.x(), poi.y(), poi.favorite(), poi.floor(), newLayer);

                poiChangeListeners.forEach(listener -> listener.accept(poi, newPoi));

                poi = newPoi;
                layerIcon.setIcon(poi.layer().getIcon());
            }
        });
        layerBox.add(layerComboBox);

        layerLabel = new JLabel();
        layerBox.add(layerLabel);

        addToBox(summaryBox, layerBox);

        // Favorite
        favoriteCheckbox = new JCheckBox("Favourite");
        // TODO: Add icon here
        favoriteCheckbox.addItemListener(e -> {
            if (poi == null) {
                return;
            }

            POI newPoi =
                    new POI(poi.name(), poi.description(), poi.x(), poi.y(), !poi.favorite(), poi.floor(), poi.layer());
            poiChangeListeners.forEach(listener -> listener.accept(poi, newPoi));

            poi = newPoi;
        });
        addToBox(summaryBox, favoriteCheckbox);

        // Position
        locationLabel = new JLabel();

        addToBox(summaryBox, locationLabel);

        // Description
        descriptionField = new JTextArea("", 5, MAX_COLUMNS);
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
                if (poi == null) {
                    return;
                }

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
        deleteButton = new JButton("Delete POI");
        deleteButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (poi == null) {
                    return;
                }

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

        setBorder(BorderFactory.createCompoundBorder(
                new FlatButtonBorder(), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        // Set preferred and maximum sizes.
        setMaximumSize(new Dimension(getPreferredSize().width, Short.MAX_VALUE));

        var closeButton = new JButton(MiscIcons.CLOSE_ICON);
        closeButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, "borderless");
        closeButton.addActionListener(e -> setVisible(false));

        setLayout(new GridBagLayout());
        var constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, 0, 8);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        add(titleField, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(0, 0, 0, 0);
        add(closeButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        add(summaryBox, constraints);
    }

    /**
     * Sets the POI that is currently being viewed/edited.
     *
     * @param poi The new POI to view/edit.
     */
    public void setCurrentPoi(POI poi) {
        this.poi = poi;
        refreshFields();
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

    private void refreshFields() {
        if (poi == null) {
            return;
        }

        titleField.setText(poi.name());
        layerIcon.setIcon(poi.layer().getIcon());
        layerComboBox.setSelectedItem(poi.layer());
        layerLabel.setText(poi.layer().toString());
        favoriteCheckbox.setSelected(poi.favorite());
        locationLabel.setText("Location: " + poi.x() + ", " + poi.y());
        descriptionField.setText(poi.description());

        boolean editEverything = developer || poi.layer() == Layer.CUSTOM;
        titleField.setEditable(editEverything);
        layerComboBox.setVisible(editEverything);
        layerLabel.setVisible(!editEverything);
        descriptionField.setEditable(editEverything);
        deleteButton.setVisible(editEverything);
    }

    private static void addToBox(JPanel box, JComponent component) {
        box.add(component);
        component.setAlignmentX(LEFT_ALIGNMENT);
        box.add(Box.createRigidArea(new Dimension(0, 5)));
    }
}
