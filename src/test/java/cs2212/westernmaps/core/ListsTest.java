package cs2212.westernmaps.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListsTest {

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    @Test
    void append() {
        List<Integer> list = List.of(1, 2, 3);
        List<Integer> result = Lists.append(list, 4);

        assertEquals(List.of(1, 2, 3), list);
        assertEquals(List.of(1, 2, 3, 4), result);
    }

    @Test
    void removeIndex() {
        List<Integer> list = List.of(1, 2, 3);
        List<Integer> result = Lists.removeIndex(list, 1);

        assertEquals(List.of(1, 2, 3), list);
        assertEquals(List.of(1, 3), result);
    }

    @Test
    void replaceIndex() {
        List<Integer> list = List.of(1, 2, 3);
        List<Integer> result = Lists.replaceIndex(list, 1, 4);

        assertEquals(List.of(1, 2, 3), list);
        assertEquals(List.of(1, 4, 3), result);
    }
}
