package ch.epfl.javelo;

/**
 * @author vince
 */
public final class Math2 {
    /**
     * Private constructor making this a non-instanciable class
     */
    private Math2() {}

    /**
     * Uses checkArgument(boolean shouldBeTrue) from Preconditions
     * class in order to make sure x and y are positive integers, and returns
     * integer (floor) part of dixision of x by y
     * @param x positive or null integer
     * @param y positive integer
     * @return integer part of division of x by y
     * @throws IllegalArgumentException if x < 0 or y <= 0
     */
    public static int ceilDiv(int x, int y) {
        Preconditions.checkArgument((x >= 0) && (y > 0));
        return ((x + y - 1)/y);
    }

    /**
     * Method that does linear interpolation between y0 and y1
     * and returns value of height on that line with a distance x
     * to the 0-coordinate
     * @param y0 y-coordinate at x=0
     * @param y1 y-coordinate at x=1
     * @param x x-coordinate at the point where y-coordinate is searched for
     * @return height of the line at x, similar to f(x) with f(x) = a*x + b, where b = y0, and a = y1-y0
     */
    public static double interpolate(double y0, double y1, double x) {
        return Math.fma((y1-y0), x, y0);
    }

    /**
     * Limits the int value v to the int interval [min, max]
     * @param min lower bound of the interval
     * @param v value that will be restricted to the interval
     * @param max upper bound of the interval
     * @return the limited value, either v, or min or max if it is smaller or bigger than that value respectively
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min <= max);
        if(v >= min) {
            return Math.min(v, max);
        }
        return min;
    }

    /**
     * Limits the double value v to the double interval [min, max]
     * @param min lower bound of the interval
     * @param v value to be restricted
     * @param max upper bound of the interval
     * @return the limited value, either v, or min or max if it is smaller or bigger than that value respectively
     */
    public static double clamp(double min, double v, double max) {
        //TODO: Ask assistant whether or not straight comparison between doubles is accurate enough in this scenario!
        Preconditions.checkArgument(min <= max);
        if(v >= min) {
            return Math.min(v, max);
        }
        return min;
    }

    /**
     * Gives the inverse hyperbolic sinus value of x
     * @param x Number of which we want to know the asinh of
     * @return asinh of x
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + Math.pow(x, 2)));
    }

    /**
     * Returns dot product of two vectors U and V
     * @param uX x-coordinate of U
     * @param uY y-coordinate of U
     * @param vX x-coordinate of V
     * @param vY y-coordinate of V
     * @return dot product U*V = uX*vX + uY*vY
     */
    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return (uX*vX + uY*vY);
    }

    /**
     * Gives the square of the norm of a vector U
     * @param uX x-coordinate of U
     * @param uY y-coordinate of U
     * @return squared norm of U
     */
    public static double squaredNorm(double uX, double uY) {
        return (Math.pow(uX, 2) + Math.pow(uY, 2));
    }

    /**
     * Gives the norm of a vector U
     * @param uX x-coordinate of U
     * @param uY y-coordinate of U
     * @return norm of U
     */
    public static double norm(double uX, double uY) {
        return Math.sqrt(squaredNorm(uX, uY));
    }

    /**
     * Gives the length of the projection of the vector AP onto vector AB, with vPX the x coordinate of
     * the vector AP, vPY the y coordinate of the vector AP, and vBX the x coordinate of
     * the vector AB, vBY the y coordinate of the vector AB
     * @param aX x-coordinate of point A
     * @param aY y-coordinate of point A
     * @param bX x-coordinate of point B
     * @param bY y-coordinate of point B
     * @param pX x-coordinate of point P
     * @param pY y-coordinate of point P
     * @return length of the projection
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {
        double vPX = pX - aX;
        double vPY = pY - aY;
        double vBX = bX - aX;
        double vBY = bY - aY;
        return dotProduct(vPX, vPY, vBX,vBY)/norm(vBX, vBY);
    }

}
