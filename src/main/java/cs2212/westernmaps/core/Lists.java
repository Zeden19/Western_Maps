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

    /**
     * Removes the element at the specified index from an unmodifiable list.
     *
     * @param list  The list to remove an element from.
     * @param index The index of the element to remove.
     * @return      A new unmodifiable list that is a copy of {@code list}
     *              with the element at {@code index} removed.
     * @param <E>   The type of the elements in the list.
     */
    public static <E> List<E> removeIndex(List<? extends E> list, int index) {
        var stream1 = list.stream().limit(index);
        var stream2 = list.stream().skip(index + 1);
        return Stream.concat(stream1, stream2).toList();
    }

    /**
     * Replaces the element at the specified index in an unmodifiable list.
     *
     * @param list    The list to remove an element from.
     * @param index   The index of the element to remove.
     * @param element The element to replace the element at {@code index} with.
     * @return        A new unmodifiable list that is a copy of {@code list}
     *                with the element at {@code index} replaced.
     * @param <E>     The type of the elements in the list.
     */
    public static <E> List<E> replaceIndex(List<? extends E> list, int index, E element) {
        var stream1 = list.stream().limit(index);
        var stream2 = Stream.of(element);
        var stream3 = list.stream().skip(index + 1);
        return Stream.concat(Stream.concat(stream1, stream2), stream3).toList();
    }
}
