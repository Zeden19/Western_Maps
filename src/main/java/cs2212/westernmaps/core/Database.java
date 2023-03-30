package cs2212.westernmaps.core;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

// TODO: Add Javadoc.

public final class Database implements Closeable {
    private final Path directory;
    private final FileChannel channel;
    private final UndoHistory history;

    private Database(Path directory, FileChannel channel, UndoHistory history) {
        this.directory = directory;
        this.channel = channel;
        this.history = history;
    }

    public static Database openDirectory(Path directory) throws IOException {
        var jsonFile = directory.resolve("database.json");
        var channel = FileChannel.open(jsonFile, StandardOpenOption.READ, StandardOpenOption.WRITE);

        var stream = Channels.newInputStream(channel);
        var state = DatabaseState.loadFromStream(stream);
        var history = new UndoHistory(state);

        return new Database(directory, channel, history);
    }

    public UndoHistory getHistory() {
        return history;
    }

    public DatabaseState getCurrentState() {
        return getHistory().getCurrentState();
    }

    public void reload() throws IOException {
        channel.position(0);
        var stream = Channels.newInputStream(channel);
        var state = DatabaseState.loadFromStream(stream);
        history.replaceHistoryWithState(state);
    }

    public void save() throws IOException {
        channel.truncate(0);
        var stream = Channels.newOutputStream(channel);
        history.getCurrentState().saveToStream(stream);
        // Make sure the changes are fully written to disk. The "false" here
        // tells the operating system that we don't care about file metadata.
        channel.force(false);
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
