package cs2212.westernmaps.maps;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatButtonBorder;
import cs2212.westernmaps.MiscIcons;
import cs2212.westernmaps.core.Layer;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import javax.swing.*;

/**
 * A panel that allows the user to toggle the visibility of individual
 * {@linkplain Layer POI layers} on the map.
 *
 * @author Connor Cummings
 */
public final class LayerVisibilityPanel extends JPanel {
    private final EnumMap<Layer, JCheckBox> layerCheckboxes = new EnumMap<>(Layer.class);

    private final List<BiConsumer<Layer, Boolean>> layerToggleListeners = new ArrayList<>();

    /**
     * Creates a new layer visibility panel.
     *
     * @param initialVisibleLayers The layers whose checkboxes will be checked
     *                             when the panel is created.
     */
    public LayerVisibilityPanel(EnumSet<Layer> initialVisibleLayers) {
        var cardLayout = new ResizableCardLayout();
        setLayout(cardLayout);

        var innerPanel = new JPanel();
        innerPanel.setLayout(new GridBagLayout());
        innerPanel.setBorder(BorderFactory.createCompoundBorder(
                new FlatButtonBorder(), BorderFactory.createEmptyBorder(4, 4, 3, 4)));
        // Block mouse events so that the map doesn't get panned when the layer
        // visibility panel is clicked on.
        innerPanel.addMouseListener(new MouseAdapter() {});

        var constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_START;

        for (var layer : Layer.values()) {
            var checkbox = new JCheckBox();
            checkbox.setBorder(BorderFactory.createEmptyBorder(7, 6, 6, 7));
            checkbox.setSelected(initialVisibleLayers.contains(layer));
            checkbox.addItemListener(e -> {
                var visible = e.getStateChange() == ItemEvent.SELECTED;
                layerToggleListeners.forEach(listener -> listener.accept(layer, visible));
            });
            constraints.gridx = 0;
            constraints.weightx = 0.0;
            innerPanel.add(checkbox, constraints);
            layerCheckboxes.put(layer, checkbox);

            var label = new JLabel(layer.getDisplayName(), layer.getIcon(), SwingConstants.LEADING);
            constraints.gridx = 1;
            constraints.weightx = 1.0;
            innerPanel.add(label, constraints);

            constraints.gridy++;
        }

        var collapseButton = new JButton(MiscIcons.CLOSE_ICON);
        collapseButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, "borderless");
        collapseButton.addActionListener(e -> cardLayout.show(this, "Collapsed"));

        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        constraints.anchor = GridBagConstraints.FIRST_LINE_END;
        innerPanel.add(collapseButton, constraints);

        var expandButton = new JButton("Show/Hide Layers");
        expandButton.putClientProperty(
                FlatClientProperties.STYLE, Map.of("background", UIManager.getColor("Panel.background")));
        expandButton.addActionListener(e -> cardLayout.show(this, "Expanded"));

        add(expandButton, "Collapsed");
        add(innerPanel, "Expanded");
    }

    /**
     * Changes the state of the checkbox for the given {@linkplain Layer layer}.
     *
     * @param layer   The layer to change the state of.
     * @param checked Whether the layer should be checked.
     */
    public void setLayerChecked(Layer layer, boolean checked) {
        var checkbox = Objects.requireNonNull(layerCheckboxes.get(layer), "All layers must have a checkbox");
        checkbox.setSelected(checked);
    }

    /**
     * Adds an event listener that will be called when a layer's visibility is
     * toggled on or off.
     *
     * @param listener A function that receives the layer that changed state and
     *                 the visibility state it changed to.
     */
    public void addLayerToggleListener(BiConsumer<Layer, Boolean> listener) {
        this.layerToggleListeners.add(listener);
    }

    // https://stackoverflow.com/a/23881790
    private static final class ResizableCardLayout extends CardLayout {
        @Override
        public Dimension preferredLayoutSize(Container parent) {
            Component current = findCurrentComponent(parent);
            if (current != null) {
                Insets insets = parent.getInsets();
                Dimension pref = current.getPreferredSize();
                pref.width += insets.left + insets.right;
                pref.height += insets.top + insets.bottom;
                return pref;
            }
            return super.preferredLayoutSize(parent);
        }

        private static @Nullable Component findCurrentComponent(Container parent) {
            for (Component comp : parent.getComponents()) {
                if (comp.isVisible()) {
                    return comp;
                }
            }
            return null;
        }
    }
}
