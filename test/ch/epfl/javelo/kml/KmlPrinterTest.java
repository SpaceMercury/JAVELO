package ch.epfl.javelo.kml;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public final class KmlPrinterTest {
    public static void main(String[] args) throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route r = rc.bestRouteBetween(159049, 117669);
        KmlPrinter.write("javelo.kml", r);
    }
}