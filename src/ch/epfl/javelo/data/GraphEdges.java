package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {



    // THE FOLLOWING 4 functions will use the edgesBuffer input
    /**
     *
     * @param edgeID the ID of a specific edge of a node
     * @return true if the edge is going in the opposite way
     */
    public boolean isInverted(int edgeID) {

        int firstValue = edgesBuffer.get(edgeID*4);
        int firstBit = Bits.extractUnsigned(firstValue, 31,1);

        return firstBit !=0;

    }

    /**
     * Function that allows us to find the targetNodeID by checking a connected edge
     * @param edgeID the edge connecting the node with the destination node
     * @return the identity of the destination node
     */
    public int targetNodeId(int edgeID) {

         return Bits.extractUnsigned(edgesBuffer.get(edgeID*4), 0, 31);
    }

    /**
     * Function to measure the length of a desired edge
     * @param edgeID the ID of the edge coming out of the node
     * @return the length of the edge of edgeID in meters
     */
    public double length(int edgeID) {
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
