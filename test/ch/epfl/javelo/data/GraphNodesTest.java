package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.*;

class GraphNodesTest {


    @Test
    public void checkCount(){

        IntBuffer buffer1 = IntBuffer.wrap(new int[]{1, 2, 3});
        GraphNodes graph1 = new GraphNodes(buffer1);

        IntBuffer buffer2 = IntBuffer.wrap(new int[]{1, 2, 3, 6, 4, 5});
        GraphNodes graph2 = new GraphNodes(buffer2);

        IntBuffer buffer3 = IntBuffer.wrap(new int[]{100, 2500, 3800, 6, 23, 56, 10000, 30000, 56});
        GraphNodes graph3 = new GraphNodes(buffer3);

        assertEquals(1, graph1.count());
        assertEquals(2, graph2.count());
        assertEquals(3, graph3.count());

    }

    @Test
    public void checkNodeE(){

        IntBuffer buffer1 = IntBuffer.wrap(new int[]{1, 2, 3});
        GraphNodes graph11 = new GraphNodes(buffer1);

        IntBuffer buffer2 = IntBuffer.wrap(new int[]{1, 2, 3, 6, 4, 5});
        GraphNodes graph22 = new GraphNodes(buffer2);

        IntBuffer buffer3 = IntBuffer.wrap(new int[]{100, 2500, 3800, 6, 23, 56, 10000, 30000, 56});
        GraphNodes graph33 = new GraphNodes(buffer3);

        assertEquals(1, graph11.nodeE(0));
        assertEquals(6, graph22.nodeE(1));
        assertEquals(6, graph22.nodeE(1));
        assertEquals(10000, graph33.nodeE(2));



    }

    @Test
    public void checkNodeN(){

        IntBuffer buffer1 = IntBuffer.wrap(new int[]{1, 2, 3});
        GraphNodes graph1 = new GraphNodes(buffer1);

        IntBuffer buffer2 = IntBuffer.wrap(new int[]{1, 2, 3, 6, 4, 5});
        GraphNodes graph2 = new GraphNodes(buffer2);

        IntBuffer buffer3 = IntBuffer.wrap(new int[]{100, 2500, 3800, 6, 23, 56, 10000, 30000, 56});
        GraphNodes graph3 = new GraphNodes(buffer3);

        assertEquals(2, graph1.nodeN(0));
        assertEquals(4, graph2.nodeN(1));
        assertEquals(23, graph3.nodeN(1));
        assertEquals(30000, graph3.nodeN(2));


    }

    @Test
    public void checkOutAll(){

        IntBuffer b = IntBuffer.wrap(new int[]{2_600_000 << 4, 1_200_000 << 4, 0x2_000_1234});
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }



}