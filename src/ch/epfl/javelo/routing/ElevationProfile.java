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

        Preconditions.checkArgument(length > 0 && elevationSamples.length >=2);
        this.length = length;
        this.elevationSamples = elevationSamples;

        // Usage of DoubleSummaryStatistics to simplify task of finding min and max elevation
        for (float elevationSample : elevationSamples) {
            stats.accept(elevationSample);
        }


    }

    /**
     * Getter for the length parameter in the class
     * @return the length of the profile in meters
     */
    public double length(){
        return length;
    }

    /**
     * Minimum altitude in the elevationSamples float list
     * @return minimum altitude of the profile in meters
     */
    public double minElevation() {
        return stats.getMin();
    }

    /**
     * Maximum elevation in the elevationSamples float list
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
        for (int i = 0; i < elevationSamples.length-1; i++) {

            if(elevationSamples[i+1] - elevationSamples[i] > 0){
                tempAscent = tempAscent + elevationSamples[i+1] - elevationSamples[i];
            }
        }
        return tempAscent;
    }

    /**
     * Returns the absolute value of the total descent of a profile
     * @return negative incline of the profile in meters
     */
    public double totalDescent(){

        double tempDescent = 0;
        for (int i = 0; i < elevationSamples.length-1; i++) {

            if(elevationSamples[i+1] < elevationSamples[i]){
                tempDescent = tempDescent + elevationSamples[i+1] - elevationSamples[i];
            }
        }
        return Math.abs(tempDescent);

    }

    /**
     * Uses sampled in Functions to interpolate the altitude at a given position (double)
     * @param position the desired position as a double
     * @return The elevation at the given position
     */
    public double elevationAt(double position){
            return Functions.sampled(elevationSamples, length).applyAsDouble(position);
    }




}