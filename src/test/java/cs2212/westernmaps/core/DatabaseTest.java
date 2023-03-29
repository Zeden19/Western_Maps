package cs2212.westernmaps.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class DatabaseTest {
    private static final byte[] EMPTY_PASSWORD_HASH = new byte[0];

    @Test
    public void testInitialState() {
        var database = new Database(DatabaseState.empty());
        Assertions.assertEquals(DatabaseState.empty(), database.getCurrentState());
    }

    @Test
    public void testPushState() {
        var state0 = DatabaseState.empty();
        var database = new Database(state0);
        Assertions.assertEquals(state0, database.getCurrentState());

        var asdfAccount = new Account("asdf", EMPTY_PASSWORD_HASH, false);
        var state1 = database.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, asdfAccount));
        database.pushState(state1);
        Assertions.assertEquals(state1, database.getCurrentState());

        var hjklAccount = new Account("hjkl", EMPTY_PASSWORD_HASH, true);
        var state2 = database.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, hjklAccount));
        database.pushState(state2);
        Assertions.assertEquals(state2, database.getCurrentState());

        // pushState(...) adds states to the history, so undo is possible.
        Assertions.assertTrue(database.undo());
        Assertions.assertEquals(state1, database.getCurrentState());
        Assertions.assertTrue(database.undo());
        Assertions.assertEquals(state0, database.getCurrentState());

        // We're at the start, so undo is no longer possible.
        Assertions.assertFalse(database.undo());
        Assertions.assertEquals(state0, database.getCurrentState());
    }

    @Test
    public void testPushStateInThePast() {
        var state0 = DatabaseState.empty();
        var database = new Database(state0);
        Assertions.assertEquals(state0, database.getCurrentState());

        // Start by populating the history with some states.
        var asdfAccount = new Account("asdf", EMPTY_PASSWORD_HASH, false);
        var state1 = database.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, asdfAccount));
        database.pushState(state1);
        Assertions.assertEquals(state1, database.getCurrentState());

        var hjklAccount = new Account("hjkl", EMPTY_PASSWORD_HASH, true);
        var state2 = database.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, hjklAccount));
        database.pushState(state2);
        Assertions.assertEquals(state2, database.getCurrentState());

        // Then, undo once...
        Assertions.assertTrue(database.undo());
        Assertions.assertEquals(state1, database.getCurrentState());

        // ...and push a state while we're in the past.
        var qwerAccount = new Account("qwer", EMPTY_PASSWORD_HASH, true);
        var state3 = database.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, qwerAccount));
        database.pushState(state3);
        Assertions.assertEquals(state3, database.getCurrentState());

        // Now, state2 should be completely gone from the history, since we
        // overwrote it.
        Assertions.assertTrue(database.undo());
        Assertions.assertEquals(state1, database.getCurrentState());
        Assertions.assertTrue(database.undo());
        Assertions.assertEquals(state0, database.getCurrentState());

        // We're at the start, so undo is no longer possible.
        Assertions.assertFalse(database.undo());
        Assertions.assertEquals(state0, database.getCurrentState());
    }

    @Test
    public void testUndoAndRedo() {
        var state0 = DatabaseState.empty();
        var database = new Database(state0);
        Assertions.assertEquals(state0, database.getCurrentState());

        // Start by populating the history with some states.
        var asdfAccount = new Account("asdf", EMPTY_PASSWORD_HASH, false);
        var state1 = database.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, asdfAccount));
        database.pushState(state1);
        Assertions.assertEquals(state1, database.getCurrentState());

        var hjklAccount = new Account("hjkl", EMPTY_PASSWORD_HASH, true);
        var state2 = database.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, hjklAccount));
        database.pushState(state2);
        Assertions.assertEquals(state2, database.getCurrentState());

        // We haven't undone anything yet, so redo is not possible.
        Assertions.assertFalse(database.redo());
        Assertions.assertEquals(state2, database.getCurrentState());

        // Undo all the way to the start.
        Assertions.assertTrue(database.undo());
        Assertions.assertEquals(state1, database.getCurrentState());
        Assertions.assertTrue(database.undo());
        Assertions.assertEquals(state0, database.getCurrentState());

        // We're at the start, so undo is no longer possible.
        Assertions.assertFalse(database.undo());
        Assertions.assertEquals(state0, database.getCurrentState());

        // Redo everything again.
        Assertions.assertTrue(database.redo());
        Assertions.assertEquals(state1, database.getCurrentState());
        Assertions.assertTrue(database.redo());
        Assertions.assertEquals(state2, database.getCurrentState());

        // We're at the end again, so redo is no longer possible.
        Assertions.assertFalse(database.redo());
        Assertions.assertEquals(state2, database.getCurrentState());
    }

    @Test
    public void testClearHistory() {
        var state0 = DatabaseState.empty();
        var database = new Database(state0);
        Assertions.assertEquals(state0, database.getCurrentState());

        // Start by populating the history with some states.
        var asdfAccount = new Account("asdf", EMPTY_PASSWORD_HASH, false);
        var state1 = database.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, asdfAccount));
        database.pushState(state1);
        Assertions.assertEquals(state1, database.getCurrentState());

        var hjklAccount = new Account("hjkl", EMPTY_PASSWORD_HASH, true);
        var state2 = database.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, hjklAccount));
        database.pushState(state2);
        Assertions.assertEquals(state2, database.getCurrentState());

        // Clear the history and make sure the current state is the same.
        database.clearHistory();
        Assertions.assertEquals(state2, database.getCurrentState());

        // Now, we shouldn't be able to undo since the history has been cleared.
        Assertions.assertFalse(database.undo());
        Assertions.assertEquals(state2, database.getCurrentState());
    }
}
