package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.List;

public record GraphSectors(ByteBuffer buffer) {
    record Sector(int startNodeId, int endNodeId) {}

    public List<Sector> sectorsInArea(PointCh center, double distance) {

    }
}
