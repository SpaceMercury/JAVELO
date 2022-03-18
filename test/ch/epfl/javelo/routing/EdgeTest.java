package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EdgeTest {

    @Test
    void positionClosestToWorks() {
        PointCh point1 = new PointCh(2600000, 1200000);
        PointCh point2 = new PointCh(2_600_008, 1_200_006);
        DoubleUnaryOperator profile = Functions.constant(0);

        Edge edge = new Edge(0, 1, point1, point2, 10, profile);

        PointCh point3 = new PointCh(2_600_016, 1_200_012);
        assertEquals(20, edge.positionClosestTo(point3));
    }

    @Test
    void pointAtWorks() {
        PointCh point1 = new PointCh(2600000, 1200000);
        PointCh point2 = new PointCh(2600008, 1200006);
        DoubleUnaryOperator profile = Functions.constant(0);

        Edge edge1 = new Edge(0, 1, point1, point2, 10, profile);

        PointCh pointExpected1 = new PointCh(2_600_008, 1_200_006);
        PointCh pointExpected2 = new PointCh(2_600_004, 1_200_003);
        PointCh pointExpected3 = new PointCh(2_599_996, 1_199_997);

        assertEquals(pointExpected1.e() ,edge1.pointAt(10).e());
        assertEquals(pointExpected2.e() ,edge1.pointAt(5).e());
        assertEquals(pointExpected3.e() ,edge1.pointAt(-5).e());

        assertEquals(pointExpected1.n() ,edge1.pointAt(10).n());
        assertEquals(pointExpected2.n() ,edge1.pointAt(5).n());
        //assertEquals(pointExpected3.n() ,edge1.pointAt(-5).n());


        PointCh point3 = new PointCh(2_600_000, 1_200_006);
        PointCh point4 = new PointCh(2_600_008, 1_200_000);

        Edge edge2 = new Edge(0, 1, point3, point4, 10, profile);

        PointCh pointExpected4 = new PointCh(2_600_004, 1_200_003);
        PointCh pointExpected5 = new PointCh(2_600_008, 1_200_000);
        PointCh pointExpected6 = new PointCh(2_600_000, 1_200_006);

        assertEquals(pointExpected4.e() ,edge2.pointAt(5).e());
        assertEquals(pointExpected5.e() ,edge2.pointAt(10).e());
        //assertEquals(pointExpected6.e() ,edge2.pointAt(0).e());

        assertEquals(pointExpected4.n() ,edge2.pointAt(5).n());
        assertEquals(pointExpected5.n() ,edge2.pointAt(10).n());
        //assertEquals(pointExpected6.n() ,edge2.pointAt(0).n());
    }

    @Test
    void elevationAtWorks() {
        float[] elevations = {0, 10};
        DoubleUnaryOperator profile = Functions.sampled(elevations, 10);

        PointCh point1 = new PointCh(2600000, 1200000);
        PointCh point2 = new PointCh(2_600_008, 1_200_006);

        Edge edge = new Edge(0, 1, point1, point2, 10, profile);

        assertEquals(0, edge.elevationAt(0));
        assertEquals(5, edge.elevationAt(5));
        assertEquals(10, edge.elevationAt(10));
        assertEquals(10, edge.elevationAt(15));
        assertEquals(0, edge.elevationAt(-5));
    }
}
