package cs2212.westernmaps.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class ListsTest {

    @Test
    void testAppend() {
        List<Integer> list = List.of(1, 2, 3);
        List<Integer> appended = Lists.append(list, 4);
        assertEquals(4, appended.size());
        assertEquals(1, appended.get(0));
        assertEquals(2, appended.get(1));
        assertEquals(3, appended.get(2));
        assertEquals(4, appended.get(3));
    }
}
