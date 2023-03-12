package cs2212.westernmaps.core;

// Note: Although the content of a byte[] is mutable, please don't mutate the
//       password hash (it might break things in the future).
public record Account(String username, byte[] passwordHash, boolean developer) {
    public Account withPassword(char[] password) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean isPasswordCorrect(char[] password) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
