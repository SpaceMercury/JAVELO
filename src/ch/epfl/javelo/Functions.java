package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

public final class Functions {

    private Functions(){

    }


    public static DoubleUnaryOperator constant(double y){

        return new Constant(y);
    }


    public static DoubleUnaryOperator sampled(float[] samples, double xMax){

        return new Sampled(samples, xMax);
    }


    /**
     * Nested class Constant
     */

    private static final class Constant implements DoubleUnaryOperator{

        public Constant(double y){
            applyAsDouble(y);
        }

        @Override
        public double applyAsDouble(double operand) {
            return operand;
        }

    }

    /**
     * Nested class Sampled
     */

    private static final class Sampled implements DoubleUnaryOperator{


        public Sampled(float[] samples, double x){

        }

        @Override
        public double applyAsDouble(double operand) {
            return 0;
        }
    }

}



