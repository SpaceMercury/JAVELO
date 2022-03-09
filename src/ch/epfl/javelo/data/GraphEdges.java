package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
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
    private static final int OFFSET_LENGTH = OFFSET_1 + 1;
    private static final int OFFSET_GRADIENT = OFFSET_LENGTH + 1;
    private static final int OFFSET_IDENTITY = OFFSET_GRADIENT+1;
    private static final int TOTAl_EDGE = OFFSET_IDENTITY+1;



    // THE FOLLOWING 4 functions will use the edgesBuffer input

    /**
     * Function that indicates if the edge is going the opposite way: towards the starting node
     * @param edgeID the ID of a specific edge of a node
     * @return true if the edge is going in the opposite way
     */
    public boolean isInverted(int edgeID) {

        int firstValue = edgesBuffer.get(edgeID*TOTAl_EDGE + OFFSET_1);
        int firstBit = Bits.extractUnsigned(firstValue, 31,1);

        return firstBit !=0;

    }

    /**
     * Function that allows us to find the targetNodeID by checking a connected edge
     * @param edgeID the edge connecting the node with the destination node
     * @return the identity of the destination node
     */
    public int targetNodeId(int edgeID) {

         return Bits.extractUnsigned(edgesBuffer.get(edgeID*TOTAl_EDGE + OFFSET_1), 0, 31);
    }

    /**
     * Function to measure the length of a desired edge in Q12.4
     * @param edgeID the ID of the edge coming out of the node
     * @return the length of the edge of edgeID in meters
     */
    public double length(int edgeID) {
        // Note the use of Short.toUnsignedInt to convert the short to an int for the use of asDouble
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.get(edgeID*TOTAl_EDGE + OFFSET_LENGTH)));
    }


    /**
     *
     * @param edgeID
     * @return
     */
    public double elevationGain(int edgeID) {
    }


//
    /**
     *
     * @param edgeID
     * @return
     */
    public boolean hasProfile(int edgeID) {
    }


    /**
     *
     * @param edgeID
     * @return
     */
    public float[] profileSamples(int edgeID) {
    }


    /**
     *
     * @param edgeID
     * @return
     */
    public int attributesIndex(int edgeID) {
    }



}
