package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphSectorsTest {
    @Test
    void sectorsInAreaWorksWithExtremeValues() {

        ByteBuffer buffer = ByteBuffer.allocate(8000000);
        for (int i = 0; i < 200000; i++) {
            buffer.putInt(i).putShort((short)1);
        }
        GraphSectors graph = new GraphSectors(buffer);
        List<GraphSectors.Sector> sectors = graph.sectorsInArea(new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N),2000);
        int actual=sectors.size();
        int expected = 2;
        assertEquals(expected, actual);

    }
}
