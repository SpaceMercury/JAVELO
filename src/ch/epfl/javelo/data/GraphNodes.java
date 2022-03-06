package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;

import java.nio.IntBuffer;

/**
 * @author fuentes
 */
public record GraphNodes(IntBuffer buffer){


    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;
    
    
    
    public int count(){
        return (buffer.capacity() / NODE_INTS);
    }


    public double nodeE(int nodeID) {
        return buffer.get((nodeID*NODE_INTS)+OFFSET_E);
    }


    public double nodeN(int nodeID) {
        return buffer.get((nodeID*NODE_INTS)+OFFSET_N);
    }


    public int outDegree(int nodeID) {
        return buffer.get((nodeID*NODE_INTS)+OFFSET_OUT_EDGES);
    }


    public int edgeId(int nodeID, int edgeIndex) {

        //TODO don't exactly know how to grab the other edegeID will ask tmrw

        return Bits.extractUnsigned(outDegree(nodeID),0,28);

    }

}
