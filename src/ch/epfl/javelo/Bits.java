package ch.epfl.javelo;

/**
 * @author fuentes
 * @author vince
 */

public final class Bits {

    private Bits(){

    }


    static int extractSigned(int value, int start, int length){

        int moveLeft = value << 32-(start+length);
        int moveRight = moveLeft >> 32-length;

        return moveRight;

    }

    static int extractUnsigned(int value, int start, int length){

        int moveLeft = value << 32-(start+length);
        int moveRight = moveLeft >>> 32-length;

        return moveRight;

    }



}
