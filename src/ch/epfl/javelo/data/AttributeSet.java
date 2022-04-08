package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;

/**
 * @author fuentes
 * @author vince
 */

public record AttributeSet(long bits) {


    /**
     * Attribute set Constructor
     * @param bits represents the contents of the set using one bit per possible value
     */
    public AttributeSet{
        long newBits = bits >>> Attribute.COUNT;
        boolean isBit0 = newBits == 0;
        Preconditions.checkArgument(isBit0);
    }

    /**
     * Construction function that allows us to build an AttributeSet with certain attributes
     * @param attributes Ellipsis of attributes
     * @return returns a set of Attributes containing only the attributes given as a parameter
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
     * Function that allows us to check if an Attribute contains another
     * @param attribute an attribute argument
     * @return True if the intersection between this and the other attribute isn't null
     */
    public boolean contains(Attribute attribute){

        long isolatedBit = this.bits >> attribute.ordinal();
        return (isolatedBit & 1) == 1;

    }

    /**
     * Function that allows us to check if an Attribute intersects another
     * @param that other attribute argument
     * @return True if the intersection between this and the other attribute isn't null
     */
    public boolean intersects(AttributeSet that){
        return (this.bits & that.bits) != 0;
    }

    /**
     *
     * @return all of the attributes in a string list
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
