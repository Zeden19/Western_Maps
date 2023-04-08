package cs2212.westernmaps.core;

import static org.junit.jupiter.api.Assertions.*;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.junit.jupiter.api.Test;

class LayerTest {
    @Test
    void getIcon() {
        Layer layer = Layer.ACCESSIBILITY;
        FlatSVGIcon icon =
                new FlatSVGIcon("cs2212/westernmaps/poi-icons/accessibility.svg", Layer.class.getClassLoader());
        assertEquals(icon.getName(), ((FlatSVGIcon) layer.getIcon()).getName());
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

    @Test
    void getDisplayName() {
        Layer layer = Layer.EATERIES;
        assertEquals("Eateries", layer.getDisplayName());

        layer = Layer.CUSTOM;
        assertEquals("Custom", layer.getDisplayName());

        layer = Layer.UTILITIES;
        assertEquals("Utilities", layer.getDisplayName());

        layer = Layer.WASHROOMS;
        assertEquals("Washrooms", layer.getDisplayName());

        layer = Layer.ACCESSIBILITY;
        assertEquals("Accessibility", layer.getDisplayName());

        layer = Layer.CLASSROOMS;
        assertEquals("Classrooms", layer.getDisplayName());

        layer = Layer.FITNESS;
        assertEquals("Fitness", layer.getDisplayName());

        layer = Layer.LOUNGES;
        assertEquals("Lounges", layer.getDisplayName());

        layer = Layer.MISCELLANEOUS;
        assertEquals("Miscellaneous", layer.getDisplayName());
    }

    @Test
    public void testToString() {
        Layer layer = Layer.EATERIES;
        assertEquals("Eateries", layer.getDisplayName());

        layer = Layer.CUSTOM;
        assertEquals("Custom", layer.getDisplayName());

        layer = Layer.UTILITIES;
        assertEquals("Utilities", layer.getDisplayName());

        layer = Layer.WASHROOMS;
        assertEquals("Washrooms", layer.getDisplayName());

        layer = Layer.ACCESSIBILITY;
        assertEquals("Accessibility", layer.getDisplayName());

        layer = Layer.CLASSROOMS;
        assertEquals("Classrooms", layer.getDisplayName());

        layer = Layer.FITNESS;
        assertEquals("Fitness", layer.getDisplayName());

        layer = Layer.LOUNGES;
        assertEquals("Lounges", layer.getDisplayName());

        layer = Layer.MISCELLANEOUS;
        assertEquals("Miscellaneous", layer.getDisplayName());
    }
}
