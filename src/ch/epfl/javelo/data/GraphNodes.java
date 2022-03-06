package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;

import java.nio.IntBuffer;

/**
 * @author fuentes
 */
public record GraphNodes(IntBuffer buffer){


    //constants that wlil be used in the functions
    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;


    /**
     * Function that counts how many nodes are in the buffer
     * @return number of nodes contained in the buffer
     */
    public int count(){
        return (buffer.capacity() / NODE_INTS);
    }

    /**
     * Function that provides us with the E coordinate of a desired node
     * @param nodeID used to identify which node will be used, starts at 0
     * @return returns the E coordinate as a double
     */
    public double nodeE(int nodeID) {
        return buffer.get((nodeID*NODE_INTS)+OFFSET_E);
    }

    /**
     * Function that provides us with the N coordinate of a desired node
     * @param nodeID used to identify which node will be used, starts at 0
     * @return
     */
    public double nodeN(int nodeID) {
        return buffer.get((nodeID*NODE_INTS)+OFFSET_N);
    }

    /**
     * Function that provides us with the number of edges going out of the desired node
     * @param nodeID used to identify which node will be used, starts at 0
     * @return
     */
    public int outDegree(int nodeID) {
        return  Bits.extractSigned(buffer.get((nodeID*NODE_INTS)+OFFSET_OUT_EDGES), 28, 4);
    }

    /**
     *
     * @param nodeID
     * @param edgeIndex
     * @return
     */
    public int edgeId(int nodeID, int edgeIndex) {

        //TODO don't exactly know how to grab the other edegeID will ask tmrw

        return Bits.extractUnsigned(outDegree(nodeID),0,28);

    }

}
