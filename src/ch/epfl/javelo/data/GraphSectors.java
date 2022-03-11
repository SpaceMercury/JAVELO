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

    /**
     * Method returns all sectors that are within the square of length 2*distance of the Point that is chosen
     * @param center The point in respect to which we look at the map
     * @param distance Half of the length of the square, or max linear distance between point and sector
     * @return A list of sectors that are contained within the square
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {

        double negativeE = center.e() - distance - MIN_E;
        double positiveE = center.e() + distance - MIN_E;
        double negativeN = center.n() - distance - MIN_N;
        double positiveN = center.n() + distance - MIN_N;
        double sectorWidth = WIDTH / SECTOR_NUMBER;
        double sectorHeight = HEIGHT / SECTOR_NUMBER;
        double xNeg = Math.floor(Math2.clamp(0.0, (negativeE / sectorWidth),127.0));
        double xPos = Math.floor(Math2.clamp(0.0, (positiveE / sectorWidth), 127.0));
        double yNeg = Math.floor(Math2.clamp(0.0, (negativeN / sectorHeight), 127.0));
        double yPos = Math.floor(Math2.clamp(0.0, (positiveN / sectorHeight), 127.0));
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