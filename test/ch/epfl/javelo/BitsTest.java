package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitsTest {

    @Test
    public void extractSignedTest() {

        var actual1 = Bits.extractSigned(0b11001010111111101011101010111110, 8, 4);
        assertEquals(-2, actual1);

        var actual2 = Bits.extractSigned(0b11001010111111101011101010111110, 1, 8);
        assertEquals(95, actual2);

    }

    @Test
    public void extractUnsignedTest() {

        var actual2 = Bits.extractUnsigned(0b11001010111111101011101010111110, 8, 4);
        System.out.println(actual2);

    }


}