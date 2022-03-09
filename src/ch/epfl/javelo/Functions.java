package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * @author fuentes
 */
public final class Functions {

    private Functions(){

    }


    public static DoubleUnaryOperator constant(double y){

        return new Constant(y);
    }


    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        Preconditions.checkArgument(! (samples.length < 2 || xMax <= 0));
        return new Sampled(samples, xMax);
    }


    /**
     * Nested class Constant
     */

    private static final record Constant (double y) implements DoubleUnaryOperator{

        @Override
        public double applyAsDouble(double operand) {
            return y;
        }

    }

    /**
     * Nested class Sampled
     */

    private static final record Sampled (float[] samples, double xMax) implements DoubleUnaryOperator{


        /**
         *
         * @param x position x of where we want to interpolate the function
         * @return returns the interpolated between the point before and after x
         */

        @Override
        public double applyAsDouble(double x) {

            double pos1 = samples[0];
            double finalpos = samples[samples.length-1];

            if (x < 0 ){
                return pos1;
            }
            if (x> xMax){
                return finalpos;
            }
            else {

                double difference = xMax / (samples.length-1 );
                double position = (x / difference);
                double xValue = position - (int) position;

                if((int)position < samples.length -1) {
                    return Math2.interpolate(samples[(int) position], samples[(int) position + 1], xValue);
                }
                else {
                    return Math2.interpolate(samples[(int) position], samples[(int) position], xValue);
                }



            }
        }
    }

}



