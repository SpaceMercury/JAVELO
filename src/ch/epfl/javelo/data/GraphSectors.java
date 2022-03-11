package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

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

        double xNeg = Math.floor(Math2.clamp(MIN_E, (negativeE / sectorWidth),MAX_E));
        double xPos = Math.floor(Math2.clamp(MIN_E, (positiveE / sectorWidth), MAX_E));
        double yNeg = Math.floor(Math2.clamp(MIN_N, (negativeN / sectorHeight), MAX_N));
        double yPos = Math.floor(Math2.clamp(MIN_N, (positiveN / sectorHeight), MAX_N));
        ArrayList<Sector> containedList = new ArrayList<>();

        /**
         * Defining this as a constant allows us to easier change the number of sectors we use
         * should it be necessary
         */
        int sectorNumber = 128;



        for(int i = (int) xNeg; i <= (int) xPos; i++){
            for(int j = (int) yNeg; j <= (int) yPos; j++){

                int indexSector = j * sectorNumber + i;
                Sector s = new Sector(buffer.getInt(indexSector * OFFSET_ID), buffer.getInt(indexSector * OFFSET_ID) + toUnsignedInt(buffer.getShort( OFFSET_NUMBER + OFFSET_ID * indexSector)));
                containedList.add(s);

            }
        }
        return containedList;
    }
}