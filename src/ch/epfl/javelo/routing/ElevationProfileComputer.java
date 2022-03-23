package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

import static java.lang.Float.NaN;

/**
 * @author ventura
 * @author fuentes
 */

public final class ElevationProfileComputer {
    private ElevationProfileComputer() {}

    /**
     * Takes a road, and returns a filled ElevationProfile of it
     * @param route Road of which the elevationProfile shall be analysed
     * @param maxStepLength distance between each point on the road.
     * @return ElevationProfile of the road
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength){
        Preconditions.checkArgument(maxStepLength > 0);

        int sampleNumber = (int) Math.ceil(route.length()/maxStepLength) + 1;
        float[] road = new float[sampleNumber];
        for(int i = 0; i < sampleNumber; i++){
            road[i] = (float) route.elevationAt(maxStepLength*i);
        }

        int firstValid = 0;
        while(road[firstValid] == NaN && firstValid < sampleNumber - 1){
            firstValid++;
        }
        if(firstValid < sampleNumber - 1) {
            Arrays.fill(road, 0, firstValid, road[firstValid]);
        } else{
            Arrays.fill(road, 0, firstValid, 0);
        }
        int lastValid = sampleNumber - 1;
        while(road[lastValid] == NaN){
            lastValid--;
        }
        Arrays.fill(road, lastValid + 1, sampleNumber, road[lastValid]);

        /**
         * Given that we fill the start and the end of the table before this step, in every
         * possible case, any holes will be contained after the first valid number,
         * and before the last valid number, so we can save possibly quite some time by running
         * the following checks only on the interval [firstValid, lastValid], as we know before and after
         * are no holes.
         */
        for(int i = firstValid; i <= lastValid; i++){
            int holeStart = firstValid;
            int holeEnd = firstValid;
            if(road[i] == NaN){
                holeStart = i - 1;
                holeEnd = i;
                while(road[holeEnd] == NaN){
                    holeEnd++;
                }
                double respectiveDistance = (holeEnd - holeStart)/(holeEnd - holeStart - 1)*maxStepLength;
                for (int j = 1; j < holeEnd - holeStart - 1; j++) {
                    road[j + holeStart] = (float) Math2.interpolate(road[holeStart - 1], road[holeEnd], respectiveDistance * j);
                }
            }
        }
        return new ElevationProfile(route.length(), road);
    }
}
