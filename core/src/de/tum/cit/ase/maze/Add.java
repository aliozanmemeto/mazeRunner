package de.tum.cit.ase.maze;

/**
 * The Add interface enables the implementing class to add
 * Coordinate type objects to the relevant in-class List.
 *
 * @param <T> The type of Coordinate to be added.
 */
public interface Add<T extends Number> {
    /**
     * Creates and adds a Coordinate type object to the relevant in-class list.
     *
     * @param x The x-coordinate, sets the x-coordinate of the newly created Coordinate type object.
     * @param y The y-coordinate, sets the y-coordinate of the newly created Coordinate type object.
     */
    void add(T x, T y);
}
