package ch.epfl.javelo;

/**
 * @author vince
 * @author fuentes
 */

public final class Q28_4 {

    private Q28_4(){

    }

    /**
     * This function takes the bit representation of a value in Q28.4 and gives back the double representation
     * @param q28_4 Bit representation that will be turned into double
     * @return Double corresponding to the bit representation of the bit.
     */

    double asDouble(int q28_4){
        double value = 0.0;
        String bit = String.valueOf(q28_4);
        for (int j = 31; j >=0; j--) {
            value += Math.scalb(bit.charAt(j), 27-j);
        }
        return value;
    }

    /**
     * Applies the asDouble function and returns the Integer rounding of the value
     * @param i Bit representaion in Q28.4 that needs to be turned into an integer representation
     * @return Integer representation of i
     */
    int ofInt(int i){
        return (int) asDouble(i);
    }


    /**
     * This function takes the bit representation of a value in Q28.4 and gives back the float representation
     * @param q28_4 Bit representation that will be turned into float
     * @return Float corresponding to the bit representation of the bit
     */
    double asFloat(int q28_4){
        float value = 0;
        String bit = String.valueOf(q28_4);
        for (int j = 31; j >=0; j--) {
            value += Math.scalb(bit.charAt(j), 27-j);
        }
        return value;
    }

}
