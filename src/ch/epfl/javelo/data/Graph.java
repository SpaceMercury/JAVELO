package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * @author fuentes
 * @author vince
 */

public final class Graph {


    private GraphNodes nodes;
    private GraphSectors sectors;
    private GraphEdges edges;
    private List<AttributeSet> attributeSets;

    /**
     * Constructor of the graph class
     * @param nodes
     * @param sectors
     * @param edges
     * @param attributeSets
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){

        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = attributeSets;
    }


    /**
     *
     * @param basePath
     * @return a new Graph with the attributes which have been loaded from the bin files
     * @throws IOException
     */
    static Graph loadFrom(Path basePath) throws IOException {
        // Nodes
        Path nodePath = basePath.resolve("nodes.bin");
        try(FileChannel channel = FileChannel.open(nodePath)){
            nodes = new GraphNodes(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asIntBuffer());
        }
        catch(IOException){

        }

        // Sectors
        Path sectorPath = basePath.resolve("sector.bin");
        try(FileChannel channel = FileChannel.open(sectorPath)){
            sectors = new GraphSectors(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));
        }
        catch(IOException){

        }

        // Edges
        Path edgePath = basePath.resolve("edges.bin");
        try(FileChannel channel = FileChannel.open(edgePath)){

        }
        catch(IOException){

        }

        return new Graph(nodes, sectors, edges, attributeSets);

    }

    /**
     *
     * @return
     */
    public int nodeCount(){
        return nodes.count();
    }

    /**
     *
     * @param nodeId the ID of the node
     * @return
     */
    public PointCh nodePoint(int nodeId){
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     *
     * @param nodeId Index of the node
     * @return
     */
    public int nodeOutDegree(int nodeId){
        return nodes.outDegree(nodeId);
    }

    /**
     *
     * @param nodeId Index of the node
     * @param edgeIndex
     * @return
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex){
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * Method that finds the closest Node to a point by comparing the square of the distances.
     * @param point
     * @param searchDistance
     * @return
     */
    public int nodeClosestTo(PointCh point, double searchDistance){
        ArrayList<GraphSectors.Sector> inArea = sectors.sectorsInArea(point, searchDistance);
        int closestNodeId = -1;
        double minDistance = Math.pow(searchDistance,2);
        for(GraphSectors.Sector sect : inArea){
            for(int i = sect.startNodeId(); i < sect.endNodeId(); i++){
                PointCh contender = new PointCh(nodes.nodeE(i), nodes.nodeN(i));
                if(point.squaredDistanceTo(contender) < minDistance){
                    closestNodeId = i;
                    minDistance = point.squaredDistanceTo(contender);
                }
            }
        }
        return closestNodeId;
    }

    /**
     *
     * @param edgeId Index of the edge
     * @return the ID of the target node
     */
    public int edgeTargetNodeId(int edgeId){
        return edges.targetNodeId(edgeId);
    }

    /**
     *
     * @param edgeId Index of the edge
     * @return
     */
    public boolean edgeIsInverted(int edgeId){
        return edges.isInverted(edgeId);
    }

    /**
     *
     * @param edgeId Index of the edge
     * @return
     */
    public AttributeSet edgeAttributes(int edgeId){
        return AttributeSet.of()
    }

    /**
     *
     * @param edgeId Index of the edge
     * @return
     */
    public double edgeLength(int edgeId){
        edges.length(edgeId);
    }

    /**
     *
     * @param edgeId Index of the edge
     * @return
     */
    public double edgeElevationGain(int edgeId){
        return edges.elevationGain(edgeId);
    }

    /**
     *
     * @param edgeId Index of the edge
     * @return the profile of an edge as a function
     */
    public DoubleUnaryOperator edgeProfile(int edgeId){
        if(edges.hasProfile(edgeId)){
            return Functions.constant(edgeId);
        }
        else{
            return Functions.constant(Double.NaN);
        }
    }

}

