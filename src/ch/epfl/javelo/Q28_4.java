package ch.epfl.javelo;

/**
 * @author vince
 * @author fuentes
 */

public final class Q28_4 {

    /**
     * As this is a non-instantiable class, the constructor is private
     */
    private Q28_4(){}

    /**
     * This function takes the bit representation of a value in Q28.4 and gives back the double representation
     * @param q28_4 Bit representation that will be turned into double
     * @return Double corresponding to the bit representation of the bit.
     */

    public static double asDouble(int q28_4){
        return Math.scalb((double) q28_4, -4);
    }

    /**
     * Applies the asDouble function and returns the Integer rounding of the value
     * @param i Bit representaion in Q28.4 that needs to be turned into an integer representation
     * @return Integer representation of i
     */
    public static int ofInt(int i) {
        return i >> 4;
    }


    /**
     * This function takes the bit representation of a value in Q28.4 and gives back the float representation
     * @param q28_4 Bit representation that will be turned into float
     * @return Float corresponding to the bit representation of the bit
     */
    public static float asFloat(int q28_4){
        return Math.scalb(q28_4, -4);
    }

}
