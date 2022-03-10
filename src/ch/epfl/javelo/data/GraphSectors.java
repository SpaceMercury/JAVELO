package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.projection.SwissBounds.*;
import static java.lang.Short.toUnsignedInt;

public record GraphSectors(ByteBuffer buffer) {
    private static final int OFFSET_ID = Integer.BYTES;
    private static final int OFFSET_NUMBER = Integer.BYTES + Short.BYTES;
    private static final double SECTOR_NUMBER = 128.0;


    public record Sector(int startNodeId, int endNodeId) {}

    public List<Sector> sectorsInArea(PointCh center, double distance) {

        double negativeE = center.e() - distance - MIN_E;
        double positiveE = center.e() + distance - MIN_E;
        double negativeN = center.n() - distance - MIN_N;
        double positiveN = center.n() + distance - MIN_N;


        int xNeg = (int)( negativeE / ( WIDTH / SECTOR_NUMBER ) );
        int xPos = (int)( positiveE / ( WIDTH / SECTOR_NUMBER ) );
        int yNeg = (int)( negativeN / ( HEIGHT / SECTOR_NUMBER ) );
        int yPos = (int)( positiveN / ( HEIGHT / SECTOR_NUMBER ) );
        ArrayList<Sector> retour = null; /** Pas sur de ca*/

        for(int i = xNeg; i <= xPos; i++){
            for(int u = yNeg; u <= yPos; u++){

                int indexSector = u * 128 + i;
                int startNodeId = buffer.getInt(indexSector * OFFSET_ID);
                int endNodeId = startNodeId + toUnsignedInt(buffer.getShort( OFFSET_NUMBER + OFFSET_ID * indexSector));
                Sector s = new Sector(startNodeId, endNodeId);
                retour.add(s);

            }
        }
        return retour;
    }
}
