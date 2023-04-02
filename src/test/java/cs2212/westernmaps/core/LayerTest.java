package cs2212.westernmaps.core;

import static org.junit.jupiter.api.Assertions.*;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import org.junit.jupiter.api.Test;

class LayerTest {
    @Test
    // fixme make proper comparison between icons
    void getIcon() {
        Layer layer = Layer.ACCESSIBILITY;
        Icon icon = new FlatSVGIcon("cs2212/westernmaps/poi-icons/accessibility.svg", Layer.class.getClassLoader());
        assertEquals(icon.getIconHeight(), layer.getIcon().getIconHeight());
    }

    @Test
    void values() {
        Layer[] layers = {
            Layer.ACCESSIBILITY,
            Layer.CLASSROOMS,
            Layer.CUSTOM,
            Layer.EATERIES,
            Layer.FITNESS,
            Layer.LOUNGES,
            Layer.MISCELLANEOUS,
            Layer.UTILITIES,
            Layer.WASHROOMS
        };
        assertArrayEquals(layers, Layer.values());
    }

    @Test
    void valueOf() {
        Layer layer = Layer.CUSTOM;
        assertEquals(Layer.CUSTOM, layer);
        assertEquals(layer, Layer.valueOf("CUSTOM"));
    }
}
