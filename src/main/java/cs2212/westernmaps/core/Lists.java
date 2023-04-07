package cs2212.westernmaps.core;

import java.util.List;
import java.util.stream.Stream;

/**
 * Contains utility methods for working with unmodifiable lists.
 */
public final class Lists {
    // Prevents instances of this class from being created.
    private Lists() {}

    /**
     * Appends an element to an unmodifiable list.
     *
     * @param list    The list to append to.
     * @param element The element to append.
     * @return        A new unmodifiable list that is a copy of {@code list}
     *                with {@code element} appended.
     * @param <E>     The type of the elements in the list.
     */
    public static <E> List<E> append(List<? extends E> list, E element) {
        var stream1 = list.stream();
        var stream2 = Stream.of(element);
        return Stream.concat(stream1, stream2).toList();
    }
}
