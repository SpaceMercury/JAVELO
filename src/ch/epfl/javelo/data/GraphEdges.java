package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * @author fuentes
 * @author vince
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {


    //Constants used in the classes

    private static final int OFFSET_1 = 0;
    private static final int OFFSET_LENGTH = OFFSET_1 + 4;
    private static final int OFFSET_GRADIENT = OFFSET_LENGTH + 2;
    private static final int OFFSET_IDENTITY = OFFSET_GRADIENT+2;
    private static final int TOTAl_EDGE = OFFSET_IDENTITY+2;



    // THE FOLLOWING 4 functions will use the edgesBuffer input

    /**
     * Function that indicates if the edge is going the opposite way: towards the starting node
     * @param edgeID the ID of a specific edge of a node
     * @return true if the edge is going in the opposite way
     */
    public boolean isInverted(int edgeID) {

        int firstValue = edgesBuffer.getShort(edgeID*TOTAl_EDGE + OFFSET_1);
        int firstBit = Bits.extractUnsigned(firstValue, 30,1);

        return firstBit !=0;

    }

    /**
     * Function that allows us to find the targetNodeID by checking a connected edge
     * @param edgeID the edge connecting the node with the destination node
     * @return the identity of the destination node
     */
    public int targetNodeId(int edgeID) {

        int targetNode = edgesBuffer.getInt((edgeID*TOTAl_EDGE) + OFFSET_1);

        if (isInverted(edgeID)) {
            return ~targetNode;
        }
        else{
            return targetNode;
        }

    }

    /**
     * Function to measure the length of a desired edge in Q12.4
     * @param edgeID the ID of the edge coming out of the node
     * @return the length of the edge of edgeID in meters
     */
    public double length(int edgeID) {
        // Note the use of Short.toUnsignedInt to convert the short to an int for the use of asDouble
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(edgeID*TOTAl_EDGE + OFFSET_LENGTH)));
    }


    /**
     *
     * @param edgeID
     * @return
     */
    public double elevationGain(int edgeID) {
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(edgeID*TOTAl_EDGE + OFFSET_GRADIENT)));
    }



    /**
     * An edge can have 4 types of different profiles, this
     * @param edgeID the ID of the edge coming out of the node
     * @return true if the edge has a profile
     */
    public boolean hasProfile(int edgeID) {

        int profileValue = Bits.extractUnsigned(profileIds.get(edgeID), 29, 2);
        return profileValue != 0;
    }


    /**
     *
     * @param edgeID
     * @return
     */
    public float[] profileSamples(int edgeID) {

        int profile = profileIds.get(edgeID) >>> 30;
        int firstIndex = Bits.extractUnsigned(profileIds.get(edgeID), 0, 29);
        int profiles = 1 + Math2.ceilDiv((Short.toUnsignedInt(edgesBuffer.getShort(edgeID*TOTAl_EDGE + OFFSET_LENGTH))), (Q28_4.ofInt(2)));

        int j = 1;
        elevations.get(firstIndex);
        float samples[] = new float[profiles];
        samples[0] = Q28_4.asFloat(elevations.get(firstIndex));

        switch(profile){
            case 0:
               return new float[0];

            case 1:
                for (int i = 1; i < profiles; i++) {
                    samples[i] = Q28_4.asFloat(elevations.get(firstIndex + i));
                }
                break;

            case 2:
                for (int i = 1; i < profiles-1  ; ++i) {
                    samples [i] = Q28_4.asFloat(Bits.extractUnsigned(elevations.get(j), 7,8 ));
                    samples [i+1] = Q28_4.asFloat(Bits.extractUnsigned(elevations.get(j), 0,8));
                    ++j;
                    ++i;

                }
                break;

            case 3:
                for (int i = 1; i < profiles  ; ++i) {
                    for (int k = 0; k < profiles; k++) {
                        samples[i] = Q28_4.asFloat(Bits.extractSigned(Short.toUnsignedInt(elevations.get(j)), 12, 4));
                        samples[i + 1] = Q28_4.asFloat(Bits.extractSigned(Short.toUnsignedInt(elevations.get(j)), 8, 4));
                        samples[i + 2] = Q28_4.asFloat(Bits.extractSigned(Short.toUnsignedInt(elevations.get(j)), 4, 4));
                        samples[i + 3] = Q28_4.asFloat(Bits.extractSigned(Short.toUnsignedInt(elevations.get(j)), 0, 4));
                    }
                }

                break;
        }

        //inverting the array if the edge is inverted

        if (isInverted(edgeID)) {
            float[] sampleInverted = new float[profiles];
            for (int i = 0; i < profiles - 1; i++) {
                sampleInverted[i] = samples[profiles - 1 - i];
            }
            return sampleInverted;
        }
        else{
            return samples;
        }
    }


    /**
     *
     * @param edgeID
     * @return
     */
    public int attributesIndex(int edgeID) {
        return Short.toUnsignedInt(edgesBuffer.getShort(edgeID*TOTAl_EDGE + OFFSET_IDENTITY));
    }

}
