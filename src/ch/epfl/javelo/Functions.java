package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

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


        @Override
        public double applyAsDouble(double x) {

            double difference = samples.length / xMax ;
            double position = (x / difference);
            double xValue = position - (int)position*difference;

            return Math2.interpolate(samples[(int)position] ,samples[(int)position+1] ,xValue);


        }
    }

}



