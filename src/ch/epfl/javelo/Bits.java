package ch.epfl.javelo;

/**
 * @author fuentes
 * @author vince
 */

public final class Bits {

    private Bits(){

    }


    static int extractSigned(int value, int start, int length){

        Preconditions.checkArgument((start>=0) && (length > 0) && (start+length<=32));

        int moveLeft = value << 32-(start+length);
        int moveRight = moveLeft >> 32-length;

        return moveRight;

    }

    static int extractUnsigned(int value, int start, int length){

        Preconditions.checkArgument((start>=0) && (length > 0) && (start+length<=32));
        int moveLeft = value << 32-(start+length);
        int moveRight = moveLeft >>> 32-length;

        return moveRight;

    }



}