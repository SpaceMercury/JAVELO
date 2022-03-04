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
        var actual1 = Functions.sampled(array1,4).applyAsDouble(2.5);
        assertEquals(Math2.interpolate(7,8,0.5), actual1);


        float[] array3 = {-10, 10, 0, 5, 0, 1};
        var actual4 = Functions.sampled(array3,10).applyAsDouble(-5);
        assertEquals(-10, actual4);
        var actual5 = Functions.sampled(array3,10).applyAsDouble(20);
        assertEquals(1, actual5);
        var actual6 = Functions.sampled(array3,10).applyAsDouble(1);
        assertEquals(0, actual6);
        var actual8 = Functions.sampled(array3,10).applyAsDouble(1.5);
        assertEquals(5, actual8);
        var actual7 = Functions.sampled(array3,10).applyAsDouble(3.25);
        assertEquals(3.75, actual7);
        var actual9 = Functions.sampled(array3,10).applyAsDouble(3.5);
        assertEquals(2.5, actual9);



    }

}