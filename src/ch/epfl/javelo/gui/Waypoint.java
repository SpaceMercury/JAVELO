package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

public record Waypoint(PointCh waypoint, int nodeId) {}