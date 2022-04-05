package ch.epfl.javelo;

/**
 * @author fuentes
 * @author vince
 */

public final class Bits {

    /**
     * Private constructor of Bits, class cannot be instantiated
     */
    private Bits(){
    }


    /**
     * Extrats the signed value of a 32 bit value at a given position
     * @param value value of the number of which we want to extract
     * @param start position where we start, start from the right
     * @param length until when do we want to extract
     * @return the extracted value between start and start+length
     */
    public static int extractSigned(int value, int start, int length){

        Preconditions.checkArgument((start>=0) && (length > 0) && (start+length <=32));
        int moveLeft = value << 32-(start+length);
        int moveRight = moveLeft >> 32-length;
        return moveRight;

    }

    /**
     * Extracts the Unsigned value of a 32 bit value at a given position
     * @param value value of the number of which we want to extract
     * @param start position where we start, start from the right
     * @param length until when do we want to extract
     * @return the extracted value between start and start+length
     */
    public static int extractUnsigned(int value, int start, int length){

        Preconditions.checkArgument((start>=0) && (length > 0) && (start+length <= 32) && length < 32);
        int moveLeft = value << 32-(start+length);
        int moveRight = moveLeft >>> 32-length;
        return moveRight;

    }



}