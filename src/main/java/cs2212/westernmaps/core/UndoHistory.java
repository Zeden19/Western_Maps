package cs2212.westernmaps.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the application's data and allows undoing and redoing changes.
 */
public final class UndoHistory {
    private final List<DatabaseState> history = new ArrayList<>();
    private int currentStateIndex;

    /**
     * Creates a new undo history with one state.
     *
     * @param initialState The initial database state.
     */
    public UndoHistory(DatabaseState initialState) {
        history.add(initialState);
        currentStateIndex = 0;
    }

    /**
     * Gets the current database state for this undo history.
     *
     * @return The current state.
     */
    public DatabaseState getCurrentState() {
        return history.get(currentStateIndex);
    }

    /**
     * Adds a new database state to this undo history and makes it current.
     *
     * <p>If this method is called in the past (that is, when redo is possible),
     * then any states in the future will be overwritten.</p>
     *
     * @param state The state to make current.
     */
    public void pushState(DatabaseState state) {
        history.subList(currentStateIndex + 1, history.size()).clear();
        history.add(state);
        currentStateIndex++;
    }

    /**
     * Replaces this entire undo history with the provided state.
     *
     * <p>After this function is called, undo and redo will not be possible. The
     * current state is left intact.</p>
     *
     * @param state The state to replace the history with.
     */
    public void replaceHistoryWithState(DatabaseState state) {
        history.clear();
        history.add(state);
        currentStateIndex = 0;
    }

    /**
     * Moves backwards in this undo history, changing the current state.
     *
     * <p>If the current state is at the beginning of the undo history, then
     * this function will return {@code false} and the current state will not be
     * changed.</p>
     *
     * @return Whether the undo was successful.
     */
    public boolean undo() {
        if (currentStateIndex <= 0) {
            return false;
        }
        currentStateIndex--;
        return true;
    }

    /**
     * Moves forwards in this undo history, changing the current state.
     *
     * <p>If the current state is at the end of the undo history, then this
     * function will return {@code false} and the current state will not be
     * changed.</p>
     *
     * @return Whether the redo was successful.
     */
    public boolean redo() {
        if (currentStateIndex >= history.size() - 1) {
            return false;
        }
        currentStateIndex++;
        return true;
    }
}
