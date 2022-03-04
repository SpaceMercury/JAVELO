package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsTest {

    @Test
    public void constantCheck(){


        var actual1 = Functions.constant(5).applyAsDouble(9);
        assertEquals(5, actual1);

        var actual2 = Functions.constant(4.4589).applyAsDouble(45.8);
        assertEquals(4.4589, actual2);


    }

    @Test
    public void sampledCheck(){

        float[] array1 = {5,6,7,8,9};
        var actual1 = Functions.sampled(array1,5).applyAsDouble(2.5);
        assertEquals(Math2.interpolate(7,8,0.5), actual1);

        float[] array2 = {1,2.3f,3.2f,4.65f,5};
        var actual2 = Functions.sampled(array1,5).applyAsDouble(2.74);
        assertEquals(Math2.interpolate(3.2, 4.65, 0.74), actual2, 10e5);

        var actual3 = Functions.sampled(array1,2).applyAsDouble(1.6);
        assertEquals(Math2.interpolate(4.65, 5, 0.1), actual2, 10e5);

    }

}