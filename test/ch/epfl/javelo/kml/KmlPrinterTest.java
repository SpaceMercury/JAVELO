package ch.epfl.javelo.kml;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public final class KmlPrinterTest {
    public static void main(String[] args) throws IOException {
        Graph g = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route r = rc.bestRouteBetween(2046055, 2694240);
        long t0 = System.nanoTime();
        KmlPrinter.write("javelo.kml", r);
        System.out.printf("Itinéraire calculé en %d ms\n", (System.nanoTime() - t0) / 1_000_000);
    }
}