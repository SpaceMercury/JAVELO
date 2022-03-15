package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;

/**
 * @author fuentes
 */
public final class ElevationProfile {

    private float[] elevationSamples;
    private double length;
    DoubleSummaryStatistics stats = new DoubleSummaryStatistics();


    /**
     * Public constructor of ElevationProfile
     *
     * @param length
     * @param elevationSamples
     */
    public ElevationProfile(double length, float[] elevationSamples) {

        Preconditions.checkArgument(length > 0 && elevationSamples.length >2);
        this.length = length;
        this.elevationSamples = elevationSamples;

        // Usage of DoubleSummaryStatistics to simplify task of finding min and max elevation
        for (float elevationSample : elevationSamples) {
            stats.accept(elevationSample);
        }


    }

    /**
     *
     * @return the length of the profile in meters
     */
    public double length(){
        return length;
    }

    /**
     *
     * @return minimum altitude of the profile in meters
     */
    public double minElevation() {
        return stats.getMin();
    }

    /**
     *
     * @return maximum altitude of the profile in meters
     */
    public double maxElevation(){
        return stats.getMax();
    }

    /**
     *
     * @return positive incline of the profile in meters
     */
    public double totalAscent(){
        double tempAscent = 0;
        for (int i = 0; i < elevationSamples.length; i++) {

            if(elevationSamples[i+1] - elevationSamples[i] > 0){
                tempAscent = tempAscent + elevationSamples[i+1] - elevationSamples[i];
            }
        }
        return tempAscent;
    }

    /**
     *
     * @return negative incline of the profile in meters
     */
    public double totalDescent(){

        double tempDescent = 0;
        for (int i = 0; i < elevationSamples.length; i++) {

            if(elevationSamples[i+1] - elevationSamples[i] < 0){
                tempDescent = tempDescent + elevationSamples[i+1] - elevationSamples[i];
            }
        }
        return tempDescent;

    }

    /**
     * Uses sampled in Functions to interpolate the altitude at a given position (double)
     * @param position the desired position as a double
     * @return The elevation at the given position
     */
    public double elevationAt(double position){

        if (position < 0) {
            return elevationSamples[0];
        }
        else {
            return Functions.sampled(elevationSamples, maxElevation()).applyAsDouble(position);
        }

    }




}