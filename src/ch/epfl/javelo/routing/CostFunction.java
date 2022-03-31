package ch.epfl.javelo.routing;
/**
 * @author ventura
 * @author fuentes
 */
public interface CostFunction {
    public abstract double costFactor(int nodeId, int nodeEdge);
}
