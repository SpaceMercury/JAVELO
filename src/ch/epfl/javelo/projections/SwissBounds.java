package ch.epfl.javelo.projections;

import ch.epfl.javelo.Math2;

/**
 * @author fuentes
 */

public final class SwissBounds {


    private SwissBounds(){

    }

    final static double MIN_E = 2485000;
    final static double MAX_E = 2834000;
    final static double MIN_N = 1075000;
    final static double MAX_N = 1296000;
    final static double WIDTH = MAX_E - MIN_E;
    final static double HEIGHT = MAX_N - MIN_N;


    /**
     *
     * @param e
     * @param n
     * @return True if and only if the parameters are in bounds of Switzerland's borders
     */
    public static boolean containsEN(double e, double n) {
        double containE = Math2.clamp(MIN_E,e,MAX_E);
        double containN = Math2.clamp(MIN_N, n, MAX_N);

        return containE == e && containN == n;

    }


}
