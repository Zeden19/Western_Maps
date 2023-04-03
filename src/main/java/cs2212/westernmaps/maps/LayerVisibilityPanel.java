package cs2212.westernmaps.maps;

import com.formdev.flatlaf.ui.FlatButtonBorder;
import cs2212.westernmaps.core.Layer;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiConsumer;
import javax.swing.*;

/**
 * A panel that allows the user to toggle the visibility of individual
 * {@linkplain Layer POI layers} on the map.
 */
public final class LayerVisibilityPanel extends JPanel {
    private final List<BiConsumer<Layer, Boolean>> layerToggleListeners = new ArrayList<>();

    /**
     * Creates a new layer visibility panel.
     *
     * @param initialVisibleLayers The layers whose checkboxes will be checked
     *                             when the panel is created.
     */
    public LayerVisibilityPanel(EnumSet<Layer> initialVisibleLayers) {
        setLayout(new GridBagLayout());
        // FlatButtonBorder has rounded corners; FlatBorder doesn't.
        setBorder(BorderFactory.createCompoundBorder(
                new FlatButtonBorder(), BorderFactory.createEmptyBorder(4, 4, 3, 12)));

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
            add(checkbox, constraints);

            var label = new JLabel(layer.name(), layer.getIcon(), SwingConstants.LEADING);
            constraints.gridx = 1;
            constraints.weightx = 1.0;
            add(label, constraints);

            constraints.gridy++;
        }
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
}
