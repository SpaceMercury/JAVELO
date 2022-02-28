package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebMercatorTest {

    @Test
    public void xWorksOnValues(){

    }

    @Test
    public void yWorksOnValues(){

    }

    @Test
    public void lonWorksOnValues(){

        var actual1 = WebMercator.lon(0);
        var expected1 = -Math.PI;
        assertEquals(expected1, actual1, 1e-5);

        var actual2 = WebMercator.lon(1);
        var expected2 = Math.PI;
        assertEquals(expected2, actual2, 1e-5);

        var actual3 = WebMercator.lon(0.5);
        var expected3 = 0.;
        assertEquals(expected3, actual3, 1e-5);


    }

    @Test
    public void latWorksOnValues(){

        var actual1 = WebMercator.lat(0);
        var expected1 = 1.484422;
        assertEquals(expected1, actual1, 1e-5);

        var actual2 = WebMercator.lat(1);
        var expected2 = -1.484422;
        assertEquals(expected2, actual2, 1e-5);

        var actual3 = WebMercator.lat(0.3);
        var expected3 = 1.01624;
        assertEquals(expected3, actual3, 1e-5);

    }

}