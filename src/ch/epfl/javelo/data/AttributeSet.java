package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

/**
 * @author fuentes
 */

public record AttributeSet(long bits) {


    public AttributeSet{
        long newBits = bits >>> Attribute.COUNT;
        boolean isBit0 = newBits == 0;
        Preconditions.checkArgument(isBit0);
    }

    public static AttributeSet of(Attribute... attributes){

        long orBit = 0L;
        for ( Attribute attribute : attributes) {
            long temp = 1L << attribute.ordinal();
            orBit = orBit | temp;
        }
        return new AttributeSet(orBit);
    }

    public boolean contains(Attribute attribute){

        long isolatedBit = this.bits >> attribute.ordinal();
        return (isolatedBit & 1) == 1;

    }

    public boolean intersects(AttributeSet that){
        return (this.bits & that.bits) != 0;
    }

    @Override
    public String toString(){

    }


}
