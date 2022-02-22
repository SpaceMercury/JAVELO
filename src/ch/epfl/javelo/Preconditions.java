package ch.epfl.javelo;
/**@author Vincent Ventura (302810)
 **/
public final class Preconditions {
    /** Private constructor making this
     * method a non-instanciable method.
     */
    private Preconditions() {}

    /**
     * @param shouldBeTrue boolean that checks a necessary condition.
     *  If the condition is false, the method throws an IllegalArgumentException.
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
