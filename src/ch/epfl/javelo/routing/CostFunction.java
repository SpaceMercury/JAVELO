package ch.epfl.javelo.routing;
/**
 * @author ventura
 * @author fuentes
 */
@SuppressWarnings("ALL")
public interface CostFunction {
    public abstract double costFactor(int nodeId, int nodeEdge);
}
