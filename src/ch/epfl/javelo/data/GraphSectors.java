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
        double sectorWidth = WIDTH / SECTOR_NUMBER;
        double sectorHeight = HEIGHT / SECTOR_NUMBER;

        int xNeg = (int) (negativeE / sectorWidth);
        int xPos = (int) (positiveE / sectorWidth);
        int yNeg = (int) (negativeN / sectorHeight);
        int yPos = (int) (positiveN / sectorHeight);
        ArrayList<Sector> retour = null; /** Pas sur de ca*/

        /**
         * Defining this as a constant allows us to easier change the number of sectors we use
         * should it be necessary
         */
        int sectorNumber = 128;


        for(int i = xNeg; i <= xPos; i++){
            for(int u = yNeg; u <= yPos; u++){

                int indexSector = u * sectorNumber + i;
                int startNodeId = buffer.getInt(indexSector * OFFSET_ID);
                int endNodeId = startNodeId + toUnsignedInt(buffer.getShort( OFFSET_NUMBER + OFFSET_ID * indexSector));
                Sector s = new Sector(startNodeId, endNodeId);
                retour.add(s);

            }
        }
        return retour;
    }
}
