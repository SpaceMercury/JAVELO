package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * @author fuentes
 */
public record Waypoint(PointCh point, int nodeId) {}