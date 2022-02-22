package ch.epfl.javelo.projections;

/**
 * @author fuentes
 */

public final class SwissBounds {


    private SwissBounds(){

    }

    double MIN_E = 2485000;
    double MAX_E = 2834000;
    double MIN_N = 1075000;
    double MAX_N = 1296000;
    double WIDTH = MAX_E - MIN_E;
    double HEIGHT = MAX_N - MIN_N;

    boolean containsEN(double e, double n) {


    }


}
