package ch.epfl.javelo;
/**@author vince
 **/
public final class Preconditions {
    /** Private constructor making this
     * a non-instantiable class.
     */
    private Preconditions() {}

    /**
     * If the condition is false, the method throws an IllegalArgumentException.
     * @param shouldBeTrue boolean that checks a necessary condition.
     * @throws IllegalArgumentException if condition is not realised.
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
