package cs2212.westernmaps.core;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

// TODO: Add Javadoc.

public final class Database implements Closeable {
    private final Path baseDirectory;
    private final FileChannel channel;
    private final UndoHistory history;

    private Database(Path baseDirectory, FileChannel channel, UndoHistory history) {
        this.baseDirectory = baseDirectory;
        this.channel = channel;
        this.history = history;
    }

    public static Database open(Path path) throws IOException {
        var absolutePath = path.toAbsolutePath();
        var baseDirectory = absolutePath.getParent();
        if (baseDirectory == null) {
            throw new RuntimeException("Unable to determine data directory from database path");
        }

        var channel = FileChannel.open(absolutePath, StandardOpenOption.READ, StandardOpenOption.WRITE);

        var stream = Channels.newInputStream(channel);
        var state = DatabaseState.loadFromStream(stream);
        var history = new UndoHistory(state);

        return new Database(baseDirectory, channel, history);
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
