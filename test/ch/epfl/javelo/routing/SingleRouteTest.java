package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SingleRouteTest {





    @Test
    public void IndexOfSegmentTest(){
        List<Edge> edgeList2= new ArrayList<Edge>();
        edgeList2.add(new Edge(0,  1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 5800, SwissBounds.MIN_N), 5800, Functions.constant(1)));
        edgeList2.add(new Edge(1,  2, new PointCh(SwissBounds.MIN_E + 5800, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 2300, SwissBounds.MIN_N), 2300, Functions.constant(1)));
        edgeList2.add(new Edge(2,  3, new PointCh(SwissBounds.MIN_E + 2300, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 1100, SwissBounds.MIN_N), 1100, Functions.constant(1)));
        edgeList2.add(new Edge(3,  4, new PointCh(SwissBounds.MIN_E + 1100, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 2200, SwissBounds.MIN_N), 2200, Functions.constant(1)));
        edgeList2.add(new Edge(4, 5, new PointCh(SwissBounds.MIN_E + 2200, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 1700, SwissBounds.MIN_N), 1700, Functions.constant(1)));

        SingleRoute singleRoute2 = new SingleRoute(edgeList2);

        assertEquals(1, singleRoute2.indexOfSegmentAt(6000));
        assertEquals(4, singleRoute2.indexOfSegmentAt(13000));


    }
    @Test
    public void lengthTest(){

        List<Edge> edgeList= new ArrayList<Edge>();
        for (int i = 0; i < 5; i++) {
            edgeList.add(new Edge(i, i + 1, new PointCh(SwissBounds.MIN_E + 120*i, SwissBounds.MIN_N),
                    new PointCh(SwissBounds.MIN_E + 120*(i+1), SwissBounds.MIN_N), 120, Functions.constant(1)));
        }
        SingleRoute singleRoute = new SingleRoute(edgeList);


        assertEquals(600, singleRoute.length());

        List<Edge> edgeList2= new ArrayList<Edge>();
        edgeList2.add(new Edge(0,  1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 5800, SwissBounds.MIN_N), 5800, Functions.constant(1)));
        edgeList2.add(new Edge(1,  2, new PointCh(SwissBounds.MIN_E + 5800, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 2300, SwissBounds.MIN_N), 2300, Functions.constant(1)));
        edgeList2.add(new Edge(2,  3, new PointCh(SwissBounds.MIN_E + 2300, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 1100, SwissBounds.MIN_N), 1100, Functions.constant(1)));
        edgeList2.add(new Edge(3,  4, new PointCh(SwissBounds.MIN_E + 1100, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 2200, SwissBounds.MIN_N), 2200, Functions.constant(1)));
        edgeList2.add(new Edge(4, 5, new PointCh(SwissBounds.MIN_E + 2200, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 1700, SwissBounds.MIN_N), 1700, Functions.constant(1)));

        SingleRoute singleRoute2 = new SingleRoute(edgeList2);

        assertEquals(13100, singleRoute2.length());




    }
    @Test
    public void pointAtTest(){
        List<Edge> edgeList= new ArrayList<Edge>();
        edgeList.add(new Edge(0,  1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 5800, SwissBounds.MIN_N), 5800, Functions.constant(1)));
        edgeList.add(new Edge(1,  2, new PointCh(SwissBounds.MIN_E + 5800, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 2300, SwissBounds.MIN_N), 2300, Functions.constant(1)));
        edgeList.add(new Edge(2,  3, new PointCh(SwissBounds.MIN_E + 2300, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 1100, SwissBounds.MIN_N), 1100, Functions.constant(1)));
        edgeList.add(new Edge(3,  4, new PointCh(SwissBounds.MIN_E + 1100, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 2200, SwissBounds.MIN_N), 2200, Functions.constant(1)));
        edgeList.add(new Edge(4, 5, new PointCh(SwissBounds.MIN_E + 2200, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 1700, SwissBounds.MIN_N), 1700, Functions.constant(1)));

        SingleRoute singleRoute = new SingleRoute(edgeList);

        assertEquals(new PointCh(SwissBounds.MIN_E + 10000, SwissBounds.MIN_N), singleRoute.pointAt(10000));

    }
    @Test
    public void nodeClosestTest(){
        List<Edge> edgeList= new ArrayList<Edge>();
        for(int i = 0; i < 5; i++) {
            edgeList.add(new Edge(i, i + 1, new PointCh(SwissBounds.MIN_E + 120*i, SwissBounds.MIN_N),
                    new PointCh(SwissBounds.MIN_E + 120*(i+1), SwissBounds.MIN_N), 120, Functions.constant(1)));
        }
        SingleRoute singleRoute = new SingleRoute(edgeList);
        assertEquals( 3, singleRoute.nodeClosestTo(400));
        assertEquals( 4, singleRoute.nodeClosestTo(450));

    }
    @Test
    public void elevationAtTest(){
        List<Edge> edgeList= new ArrayList<Edge>();
        for(int i = 0; i < 5; i++) {
            edgeList.add(new Edge(i, i + 1, new PointCh(SwissBounds.MIN_E + 120*i, SwissBounds.MIN_N),
                    new PointCh(SwissBounds.MIN_E + 120*(i+1), SwissBounds.MIN_N), 120, Functions.constant(1)));
        }
        SingleRoute singleRoute = new SingleRoute(edgeList);
    }


}