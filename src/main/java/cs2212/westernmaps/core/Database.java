package cs2212.westernmaps.core;

import java.util.ArrayList;
import java.util.List;

/**
 * A container for the application's data with undo history.
 *
 * <p>Although this class is called {@code Database}, it does not represent an
 * actual database system or a connection to one; it simply holds data.</p>
 */
public final class Database {
    private final List<DatabaseState> history = new ArrayList<>();
    private int currentStateIndex;

    /**
     * Creates a new database with one state in the history.
     *
     * @param initialState The initial state of the database.
     */
    public Database(DatabaseState initialState) {
        history.add(initialState);
        currentStateIndex = 0;
    }

    /**
     * Gets the current state of this database.
     *
     * @return The current state of this database.
     */
    public DatabaseState getCurrentState() {
        return history.get(currentStateIndex);
    }

    /**
     * Modifies the current state of this database.
     *
     * <p>The previous state will be added to the undo history. If this method
     * is called in the past (that is, when redo is possible), then any states
     * in the future will be overwritten.</p>
     *
     * @param state The state to make current.
     */
    public void pushState(DatabaseState state) {
        history.subList(currentStateIndex + 1, history.size()).clear();
        history.add(state);
        currentStateIndex++;
    }

    /**
     * Clears the undo history of this database.
     *
     * <p>After this function is called, undo and redo will not be possible. The
     * current state is left intact.</p>
     */
    public void clearHistory() {
        var currentState = getCurrentState();
        history.clear();
        history.add(currentState);
        currentStateIndex = 0;
    }

    /**
     * Moves backwards in the undo history, changing the current state.
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
     * Moves forwards in the undo history, changing the current state.
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
