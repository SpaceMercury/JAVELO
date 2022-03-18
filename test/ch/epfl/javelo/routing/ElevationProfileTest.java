package ch.epfl.javelo.routing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileTest {


    @Test
    public void elevationAt(){

        float[] floatvalues = new float[]{1,2,3,4,5,6,7};

        var elevations = new ElevationProfile(6, floatvalues);

        assertEquals(1.5, elevations.elevationAt(1.5), 10e6);
    }


    @Test
    public void elevationMin(){

        float[] floatvalues = new float[]{-1,2,3.f,78.56f,5,78.54f,70,-45,-5000.6f};

        var elevation = new ElevationProfile(68, floatvalues );
        assertEquals(-5000.6f ,elevation.minElevation());

    }

    @Test
    public void elevationMax(){
        float[] floatvalues = new float[]{-1,2,3.f,78.56f,5,78.54f,70,-45,-5000.6f};

        var elevation = new ElevationProfile(68, floatvalues );
        assertEquals(78.56f ,elevation.maxElevation());

    }

    @Test
    public void lengthcheck(){
        float[] floatvalues = new float[]{-1,2,3.f,78.56f,5,78.54f,70,-45,-5000.6f};

        var elevation = new ElevationProfile(68, floatvalues );
        assertEquals(68 ,elevation.length());


    }

    @Test
    public void totalAscentCheck(){
        float[] floatvalues = new float[]{-5,20,-10};
        float[] floatvalues2 = new float[]{-5,20,30};

        var elevation = new ElevationProfile(68, floatvalues);
        var elevation2 = new ElevationProfile(68, floatvalues2);

        assertEquals(25 ,elevation.totalAscent());
        assertEquals(35, elevation2.totalAscent());
    }

    @Test
    public void totalDescentCheck(){
        float[] floatvalues = new float[]{-5,20,-10, 40,30};
        float[] floatvalues2 = new float[]{-5,20,30};

        var elevation = new ElevationProfile(68, floatvalues);
        var elevation2 = new ElevationProfile(68, floatvalues2);

        assertEquals(40 ,elevation.totalDescent());
        assertEquals(0, elevation2.totalDescent());
    }




}