package cs2212.westernmaps.core;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A point of interest (POI) on a map.
 *
 * @param name        The display name of this POI.
 * @param description A long-form description of this POI.
 * @param x           The X-coordinate of this POI on the map of its floor.
 * @param y           The Y-coordinate of this POI on the map of its floor.
 * @param favoriteOf  The accounts that have marked this POI as a favorite.
 * @param floor       The floor this POI is on.
 * @param layer       The layer this POI is assigned to.
 */
public record POI(String name, String description, int x, int y, Set<Account> favoriteOf, Floor floor, Layer layer) {
    public POI {
        favoriteOf = Set.copyOf(favoriteOf);
    }

    /**
     * Alias of {@link #name()} used by Java Swing components.
     *
     * @return The display name of this POI.
     */
    @Override
    public String toString() {
        return name();
    }

    /**
     * Creates a copy of this POI with its location updated.
     *
     * @param x The x-coordinate of the new location.
     * @param y The y-coordinate of the new location.
     * @return  A copy of this POI with the new location.
     */
    public POI withLocation(int x, int y) {
        return new POI(name(), description(), x, y, favoriteOf(), floor(), layer());
    }

    /**
     * Checks if this POI is a favorite of the given account.
     *
     * @param account The account to check.
     * @return        Whether this POI is a favorite of the given account.
     */
    public boolean isFavoriteOfAccount(Account account) {
        return favoriteOf().contains(account);
    }

    /**
     * Creates a copy of this POI where the given account has or has not marked
     * the POI as a favorite.
     *
     * @param account  The account to update the favorite state of.
     * @param favorite Whether the returned POI is a favorite of the account.
     * @return         A copy of this POI with the set of accounts that have
     *                 marked this POI as a favorite updated.
     */
    public POI withFavoriteOfAccount(Account account, boolean favorite) {
        Set<Account> newSet;
        if (favorite) {
            // Construct a new set containing the accounts that have currently
            // marked this POI as a favorite, plus the given account.
            newSet = Stream.concat(favoriteOf().stream(), Stream.of(account)).collect(Collectors.toUnmodifiableSet());
        } else {
            // Construct a new set containing the accounts that have currently
            // marked this POI as a favorite, minus the given account.
            newSet = favoriteOf().stream().filter(a -> !a.equals(account)).collect(Collectors.toUnmodifiableSet());
        }
        return new POI(name(), description(), x(), y(), newSet, floor(), layer());
    }
}
