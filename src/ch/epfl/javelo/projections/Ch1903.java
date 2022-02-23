package ch.epfl.javelo.projections;
import java.lang.Math;

import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * @author fuentes
 */

public final class Ch1903 {

    private Ch1903(){
    }

    /**
     * method that returns the E coordinate for the Swiss Grid based on longitude and latitude
     * @param lon the longitude of the coordinate
     * @param lat the latitude of the coordinate
     * @return double E coordinate
     */

    public static double e(double lon, double lat){

        double lambda1 = Math.pow(10,-4) * ((3600*Math.toDegrees(lon)) - 26782.5);
        double phi1 = Math.pow(10,-4) * (3600*Math.toDegrees(lat) - 169028.66);

        double E = 2600072.37 + (211455.93*lambda1) - (10938.51*lambda1*phi1) - (0.36*lambda1*Math.pow(phi1,2)) - (44.54* Math.pow(lambda1, 3)) ;
        return E;
    }

    /**
     * method that returns the N coordinate for the Swiss Grid baded on longitude and latitude
     * @param lon longitude of the coordinate
     * @param lat latitude of the coordinate
     * @return double N coordinate
     */

    public static double n(double lon, double lat){

        double lambda1 = Math.pow(10,-4) * (3600*Math.toDegrees(lon) - 26782.5);
        double phi1 = Math.pow(10,-4) * (3600*Math.toDegrees(lat) - 169028.66);

        double N = 1200147.07 + (308807.95*phi1) + (3745.25*Math.pow(lambda1,2)) + (76.63*Math.pow(phi1,2)) - (194.56*Math.pow(lambda1, 2)*phi1) + (119.79*Math.pow(phi1, 3)) ;
        return N;
    }

    /**
     * method that gives the longitude of a location with two coordinates (E,N) of the Swiss Grid
     * @param e displacement on the horizontal axis with Bern as the origin
     * @param n displacement on the vertical axis with Bern as the origin
     * @return double longitude of the coordinate
     */

    public static double lon (double e, double n){

        double x = Math.pow(10,-6) * (e-2600000);
        double y = Math.pow(10,-6) * (n - 1200000);
        double lambda0 = (2.6779094) + (4.728982*x)+ (0.791484*x*y) + (0.1306*x*Math.pow(y,2) - (0.0436*Math.pow(x,3)));

        double lon = (lambda0) * ((double)100/36);
        return toRadians(lon);
    }

    /**
     * method that gives the latitude of a location with two coordinates (E,N) of the Swiss Grid
     * @param e displacement on the horizontal axis with Bern as the origin
     * @param n displacement on the vertical axis with Bern as the origin
     * @return double latitude of the coordinate
     */

    public static double lat (double e, double n){

        double x = Math.pow(10,-6) * (e-2600000);
        double y = Math.pow(10,-6) * (n - 1200000);
        double phi0 = (16.9023892) + (3.238272*y) - (0.270978*Math.pow(x,2)) - (0.002528*Math.pow(y,2)) -(0.0447*Math.pow(x,2)*y) - (0.0140*Math.pow(y,3));

        double lat = (phi0) * ((double)100/36);
        return toRadians(lat);
    }
}
