package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.data.Attribute.HIGHWAY_SERVICE;
import static org.junit.jupiter.api.Assertions.*;

class GraphTest {


    public GraphEdges graphEdges(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);


//        IntBuffer profileIds = IntBuffer.wrap(new int[]{
//                // Type : 3. Index du premier échantillon : 1.
//                (3 << 30) | 1
//        });

        IntBuffer profileIds = IntBuffer.wrap(new int[]{(3 << 30) | 1});


        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000,
                (short) 0xFEFF, (short) 0xFFFE,
                (short) 0xF000, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000,
                (short) 0xFEFF, (short) 0xFFFE,
                (short) 0xF000,

        });

        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);

        return new GraphEdges(edgesBuffer,profileIds,elevations);
    }

    public GraphNodes graphNodes(){
        IntBuffer b = IntBuffer.wrap(new int[]{2485000 << 4, 1075000 << 4, 9});

        return new GraphNodes(b);
    }

    public GraphNodes graphNodes1(){
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        return new GraphNodes(b1);
    }

    public GraphSectors graphSectors(){

        ByteBuffer buffer = ByteBuffer.allocate(6 * 16684);

        for (int i = 0; i <= 16683; i += 1) {
            buffer.putInt(i * 6, i);
        }

        for (int j = 0; j <= 16683; j += 1) {
            buffer.putShort(j * 6 + 4, (short) 5);
        }

        return new GraphSectors(buffer);
    }

    public List<AttributeSet> ListOfAttributeSet(){
        AttributeSet set1 = AttributeSet.of(Attribute.HIGHWAY_UNCLASSIFIED, Attribute.HIGHWAY_TRACK, Attribute.HIGHWAY_CYCLEWAY, Attribute.LCN_YES);
        AttributeSet set2 = AttributeSet.of(HIGHWAY_SERVICE,Attribute.HIGHWAY_UNCLASSIFIED,Attribute.ONEWAY_BICYCLE_NO);
        List<AttributeSet> listOfAttributeSet = new ArrayList<AttributeSet>();
        listOfAttributeSet.add(set1);
        listOfAttributeSet.add(set2);

        return listOfAttributeSet;
    }

    Graph graph1 = new Graph(graphNodes(),graphSectors(),graphEdges(),ListOfAttributeSet());
    Graph graph2 = new Graph(graphNodes1(),graphSectors(),graphEdges(),ListOfAttributeSet());
    @Test
    void loadFrom() {
    }

    @Test
    void nodeCount() {
        assertEquals(1,graph1.nodeCount());
        assertEquals(1,graph2.nodeCount());
    }

    @Test
    void nodePoint() {
        assertEquals(new PointCh(2_485_000,1075000),graph1.nodePoint(0));
        assertEquals(new PointCh(2_600_000,1_200_000),graph2.nodePoint(0));
    }

    @Test
    void nodeOutDegree() {
        assertEquals(2,graph2.nodeOutDegree(0));
    }

    @Test
    void nodeOutEdgeId() {
        assertEquals(0x1234, graph2.nodeOutEdgeId(0,0));
        assertEquals(0x1235, graph2.nodeOutEdgeId(0,1));    }

    /*@Test
    void nodeClosestTo() {
        assertEquals(new PointCh(2_485_000,1075000),graph1.nodeClosestTo(new PointCh(2_485_047,1075000),50));
    }*/

    @Test
    void edgeIsInverted() {
        assertTrue(graph1.edgeIsInverted(0));    }

    @Test
    void edgeTargetNodeId() {
        assertEquals(12,graph1.edgeTargetNodeId(0));
    }


    @Test
    void edgeAttributes() {

    }

    @Test
    void edgeLength() {
        assertEquals(16.6875,graph1.edgeLength(0));
    }

    @Test
    void edgeElevationGain() {
        assertEquals(16.0,graph1.edgeElevationGain(0));
    }

    @Test
    void edgeProfile() {
        double max = 10;
        DoubleUnaryOperator f = Functions.sampled(new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f}, 10);

        for(int i=0; i<10;++i){
            assertEquals(f.applyAsDouble(i),graph1.edgeProfile(0).applyAsDouble(i));
        }
    }

    @Test
    void nodeClosestToTest(){
        Graph graph = generateLausanneGraph();

        int nodeId1 = 0; // 1684019323
        int nodeId2 = 123_567; // 3761311896
        int nodeId3 = graph.nodeCount() -1; // 5475839472

        PointCh node1 = new PointCh(Ch1903.e(Math.toRadians(6.7761194), Math.toRadians(46.6455770)), Ch1903.n(Math.toRadians(6.7761194), Math.toRadians(46.6455770)));
        PointCh node2 = new PointCh(Ch1903.e(Math.toRadians(6.6291292), Math.toRadians(46.5235985)), Ch1903.n(Math.toRadians(6.6291292), Math.toRadians(46.5235985)));
        PointCh node3 = new PointCh(Ch1903.e(Math.toRadians(6.4789731), Math.toRadians(46.6422279)), Ch1903.n(Math.toRadians(6.4789731), Math.toRadians(46.6422279)));

        PointCh pointNearNode1 = new PointCh(node1.e() - 20, node1.n());

        assertEquals(nodeId1, graph.nodeClosestTo(node1, 100));
        assertEquals(nodeId2, graph.nodeClosestTo(node2, 100));
        assertEquals(nodeId3, graph.nodeClosestTo(node3, 100));
        assertEquals(nodeId1, graph.nodeClosestTo(pointNearNode1, 20));
        assertEquals(-1, graph.nodeClosestTo(pointNearNode1, 19));
    }

    private Graph generateLausanneGraph(){
        Path basePath = Path.of("lausanne");
        Graph graph = null;
        try{ graph = Graph.loadFrom(basePath);}
        catch (IOException e){
            e.printStackTrace();
            fail();}
        return graph;
    }
}
