package ch.epfl.javelo;

/**
 * @author vince
 */
public final class Math2 {
    /**
     * Private constructor making this a
     * non-instanciable class.
     */
    private Math2() {}

    /**
     * Uses checkArgument(boolean shouldBeTrue) from Preconditions
     * class in order to make sure x and y are positive integers, and returns
     * integer (floor) part of dixision of x by y.
     * @param x positive or null integer
     * @param y positive integer
     * @throws IllegalArgumentException if x < 0 or y <= 0
     * @return integer part of division of x by y
     */
    public static int ceilDiv(int x, int y) {
        Preconditions.checkArgument((x >= 0) && (y > 0));
        return ((x + y - 1)/y);
    }

    /**
     * Method that does linear interpolation between y0 and y1
     * and returns value of height on that line with a distance x
     * to the 0-coordinate.
     * @param y0 y-coordinate at x=0
     * @param y1 y-coordinate at x=1
     * @param x x-coordinate at the point where y-coordinate is searched for
     * @return height of the line at x, similar to f(x) with f(x) = a*x + b, where b = y0, and a = y1-y0
     */
    public static double interpolate(double y0, double y1, double x) {
        return Math.fma((y1-y0), x, y0);
    }



}
