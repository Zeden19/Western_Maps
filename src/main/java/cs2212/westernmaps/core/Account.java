package cs2212.westernmaps.core;

public final class Account {
    private String username;
    private byte[] passwordHash;
    private boolean developer;

    public Account(String username, byte[] passwordHash, boolean developer) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.developer = developer;
    }

    public String getUsername() {
        return username;
    }

    public boolean isDeveloper() {
        return developer;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(char[] password) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setDeveloper(boolean developer) {
        this.developer = developer;
    }

    public boolean isPasswordCorrect(char[] password) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
