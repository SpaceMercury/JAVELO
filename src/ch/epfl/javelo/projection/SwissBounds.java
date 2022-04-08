package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * @author fuentes
 */

public final class SwissBounds {


    /**
     * Non-instantiable class therefore we use a private Constructor
     */
    private SwissBounds(){
    }


    // Constants for the limits of the SwissBounds
    final static public double MIN_E = 2485000;
    final static public double MAX_E = 2834000;
    final static public double MIN_N = 1075000;
    final static public double MAX_N = 1296000;
    final static public double WIDTH = MAX_E - MIN_E;
    final static public double HEIGHT = MAX_N - MIN_N;


    /**
     *
     * @param e E coordinate in the Swiss Coordinate system
     * @param n N Coordinate in the Swiss Coordinate system
     * @return True if and only if the parameters are in bounds of Switzerland's borders
     */
    public static boolean containsEN(double e, double n) {
        double containE = Math2.clamp(MIN_E,e,MAX_E);
        double containN = Math2.clamp(MIN_N, n, MAX_N);

        return containE == e && containN == n;

    }


}
