package ch.epfl.javelo;
/**@author Vincent Ventura (302810)
 **/
public final class Preconditions {
    /** Private constructor making this
     * a non-instanciable class.
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
