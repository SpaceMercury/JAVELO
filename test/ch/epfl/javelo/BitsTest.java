package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitsTest {

    @Test
    public void extractSignedTest() {

        var actual1 = Bits.extractSigned(0b11001010111111101011101010111110, 8, 4);
        assertEquals(-6, actual1);

        var actual2 = Bits.extractSigned(0b11001010111111101011101010111110, 1, 5);
        assertEquals(-1, actual2);

    }

    @Test
    public void extractUnsignedTest() {

        var actual1 = Bits.extractUnsigned(0b11001010111111101011101010111110, 8, 4);
        assertEquals(10, actual1);

        var actual2 = Bits.extractUnsigned(0b11001010111111101011101010111110, 1, 5);
        assertEquals(31, actual2);

        var actual3 = Bits.extractUnsigned(0b00000000000000000000000000001010, 0, 4);
        assertEquals(10, actual3);

        var actual4 = Bits.extractUnsigned(0b11001010111111101011101010111110, 31, 1);
        System.out.println(actual4);


    }


}