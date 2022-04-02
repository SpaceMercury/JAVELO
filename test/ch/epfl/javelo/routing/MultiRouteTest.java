package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class MultiRouteTest {

    private static final double delta = 1e-4;

    private static final int ORIGIN_N = 1_200_000;
    private static final int ORIGIN_E = 2_600_000;
    private static final double EDGE_LENGTH_E = 40;
    private static final double EDGE_LENGTH_N = 30;
    private static final int NUM_OF_NODES = 19;
    private static final int NUM_OF_EDGES = NUM_OF_NODES - 1;
    private static final int NUM_OF_SAMPLES = NUM_OF_EDGES * 4;

    private MultiRoute trivialMultiRoute;
    private List<Route> trivialRouteSegments = new ArrayList<>();
    private List<Edge> trivialRouteEdges = new ArrayList<>();
    private List<PointCh> trivialRoutePoints = new ArrayList<>();

    private MultiRoute knownMultiRoute;
    private List<Route> knownRouteSegments = new ArrayList<>();
    private List<Edge> knownRouteEdges = new ArrayList<>();
    private List<PointCh> knownRoutePoints = new ArrayList<>();

    private MultiRoute randomMultiRoute;
    private List<Route> randomRouteSegments;
    private List<Edge> randomRouteEdges;
    private List<Route> empty = new ArrayList<>();


    private Edge edge0;

    public MultiRouteTest(){

        PointCh point0 = new PointCh(ORIGIN_E, ORIGIN_N);
        PointCh point1 = new PointCh(ORIGIN_E + EDGE_LENGTH_E, ORIGIN_N);
        trivialRoutePoints.add(point0);
        trivialRoutePoints.add(point1);


        for (int i = 0; i < NUM_OF_NODES; i++) {
            if(i > NUM_OF_NODES/2){
                knownRoutePoints.add(new PointCh(ORIGIN_E + i*EDGE_LENGTH_E, ORIGIN_N - (i - NUM_OF_NODES/2)*EDGE_LENGTH_N));
            }else{
                knownRoutePoints.add(new PointCh(ORIGIN_E + i*EDGE_LENGTH_E, ORIGIN_N));
            }
        }

        // creating elevation profile for the multiRoute, forms something like _____
        //                                                                          \
        //                                                                           \
        //                                                                            \
        float[][] elevationSamplesList = new float[NUM_OF_EDGES][4];
        for (int j1 = 0; j1 < NUM_OF_EDGES; j1++) {
            if(j1 > NUM_OF_EDGES / 2 - 1){
                elevationSamplesList[j1][0] = (NUM_OF_EDGES - j1) * 30;
                elevationSamplesList[j1][1] = (NUM_OF_EDGES - j1) * 30 - 10;
                elevationSamplesList[j1][2] = (NUM_OF_EDGES - j1) * 30 - 20;
                elevationSamplesList[j1][3] = (NUM_OF_EDGES - j1) * 30 - 30;
            }else{
                for (int j2 = 0; j2 < 4; j2++) {
                    elevationSamplesList[j1][j2] = 270;
                }
            }
        }




        edge0 = new Edge(0, 1, knownRoutePoints.get(0), knownRoutePoints.get(1), 40, Functions.constant(0));

        trivialRouteEdges.add(edge0);
        SingleRoute trivialRoute = new SingleRoute(trivialRouteEdges);

        trivialRouteSegments.add(trivialRoute);

        trivialMultiRoute = new MultiRoute(trivialRouteSegments);




        for (int k = 0; k < NUM_OF_EDGES; k++) {
            if(k > (NUM_OF_EDGES / 2) - 1) {
                knownRouteEdges.add(new Edge(k, k + 1, knownRoutePoints.get(k), knownRoutePoints.get(k + 1), 50, Functions.sampled(elevationSamplesList[k], 50)));
            }else{
                knownRouteEdges.add(new Edge(k, k + 1, knownRoutePoints.get(k), knownRoutePoints.get(k + 1), 40, Functions.sampled(elevationSamplesList[k], 40)));
            }
        }
        List<Route> knownSingleRoutes = new ArrayList<>();
        knownSingleRoutes.add(new SingleRoute(knownRouteEdges.subList(0, 3)));
        knownSingleRoutes.add(new SingleRoute(knownRouteEdges.subList(3, 6)));
        knownSingleRoutes.add(new SingleRoute(knownRouteEdges.subList(6, 9)));
        knownSingleRoutes.add(new SingleRoute(knownRouteEdges.subList(9, 12)));
        knownSingleRoutes.add(new SingleRoute(knownRouteEdges.subList(12, 15)));
        knownSingleRoutes.add(new SingleRoute(knownRouteEdges.subList(15, 18)));


        MultiRoute knownSubMulitRoute1 = new MultiRoute(knownSingleRoutes.subList(0, 3));
        MultiRoute knownSubMulitRoute2 = new MultiRoute(knownSingleRoutes.subList(3, 6));
        List<Route> multiRouteList = new ArrayList<>();
        multiRouteList.add(knownSubMulitRoute1);
        multiRouteList.add(knownSubMulitRoute2);

        knownMultiRoute = new MultiRoute(multiRouteList);
    }

    @Test
    void constructorThrowsOnNullList(){
        assertThrows(IllegalArgumentException.class, () -> new MultiRoute(empty));
    }

    @Test
    void indeOfSegmentAtWorks(){
        assertEquals(0, trivialMultiRoute.indexOfSegmentAt(40));
        assertEquals(3, knownMultiRoute.indexOfSegmentAt(400));
    }

    @Test
    void lengthWorks(){
        assertEquals(40, trivialMultiRoute.length());
        assertEquals(810, knownMultiRoute.length());
    }

    @Test
    void edgesWorks(){
        assertEquals(trivialRouteEdges, trivialMultiRoute.edges());
        assertEquals(knownRouteEdges, knownMultiRoute.edges());
    }

    @Test
    void pointsWorks(){
        assertEquals(trivialRoutePoints, trivialMultiRoute.points());
        assertEquals(knownRoutePoints, knownMultiRoute.points());
    }

    @Test
    void pointAtWorks(){
        PointCh expectedPoint = new PointCh(ORIGIN_E + 500, ORIGIN_N - 105);

        //assertEquals(edge0.pointAt(30), trivialMultiRoute.pointAt(30));
        assertEquals(expectedPoint, knownMultiRoute.pointAt(535));
    }

    @Test
    void elevationAt(){
        assertEquals(0, trivialMultiRoute.elevationAt(30));
        assertEquals(270, knownMultiRoute.elevationAt(200));
        assertEquals(165, knownMultiRoute.elevationAt(535));
    }

    @Test
    void nodeClosestToWorks(){
        assertEquals(0, trivialMultiRoute.nodeClosestTo(5));
        assertEquals(1, trivialMultiRoute.nodeClosestTo(20));
        assertEquals(1, trivialMultiRoute.nodeClosestTo(40));

        assertEquals(0, knownMultiRoute.nodeClosestTo(0));
        assertEquals(12, knownMultiRoute.nodeClosestTo(520));
    }

    @Test
    void pointClosestToWorks(){
        PointCh target = new PointCh(ORIGIN_E + 578.75, ORIGIN_N);
        PointCh expectedPoint = new PointCh(ORIGIN_E + 500, ORIGIN_N - 105);
        RoutePoint knownExpected = new RoutePoint(expectedPoint, 535,131.25);
        RoutePoint trivialExpected = new RoutePoint(new PointCh(ORIGIN_E + 5, ORIGIN_N), 5, 5);

        assertEquals(trivialExpected, trivialMultiRoute.pointClosestTo(new PointCh(ORIGIN_E + 5, ORIGIN_N + 5)));
        assertEquals(knownExpected, knownMultiRoute.pointClosestTo(target));
    }

}
