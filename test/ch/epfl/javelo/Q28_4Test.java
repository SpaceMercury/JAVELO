package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Q28_4Test {

   /** @Test
    void DoubleReturnsCorrect() {
        assertEquals();
    }**/

    @Test
    void IntegerReturnsCorrect() {
        assertEquals(1, Q28_4.ofInt(25));
    }

    @Test
    void DoubleReturnsCorrect() { assertEquals(1., Q28_4.asDouble(25));}
}
