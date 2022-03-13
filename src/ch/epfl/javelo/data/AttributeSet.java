package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;

/**
 * @author fuentes
 * @author vince
 */

public record AttributeSet(long bits) {


    /**
     *
     * @param bits
     */
    public AttributeSet{
        long newBits = bits >>> Attribute.COUNT;
        boolean isBit0 = newBits == 0;
        Preconditions.checkArgument(isBit0);
    }

    /**
     *
     * @param attributes Ellipsis of attributes
     * @return
     */
    public static AttributeSet of(Attribute... attributes){

        long orBit = 0L;
        for ( Attribute attribute : attributes) {
            long temp = 1L << attribute.ordinal();
            orBit = orBit | temp;
        }
        return new AttributeSet(orBit);
    }

    /**
     *
     * @param attribute
     * @return
     */
    public boolean contains(Attribute attribute){

        long isolatedBit = this.bits >> attribute.ordinal();
        return (isolatedBit & 1) == 1;

    }

    /**
     *
     * @param that
     * @return
     */
    public boolean intersects(AttributeSet that){
        return (this.bits & that.bits) != 0;
    }

    /**
     * @TODO vince can you comment this?
     * @return
     */
    @Override
    public String toString(){
        StringJoiner joiner = new StringJoiner(",", "{", "}");
        for(Attribute att : Attribute.ALL) {
            if(contains(att)) {
                joiner.add(att.toString());
            }
        }
        return joiner.toString();
    }


}
