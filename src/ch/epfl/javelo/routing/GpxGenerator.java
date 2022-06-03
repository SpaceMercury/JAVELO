package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;

/**
 * @author ventura
 */
public class GpxGenerator {

    /**
     * Non instantiable class
     */
    private GpxGenerator(){
    }

    public static Document createGPX(Route route, ElevationProfile profile) {
        double lon, lat, length = 0;
        ArrayList<PointCh> routePoints = new ArrayList<>(route.points());
        ArrayList<Edge> routeEdges = new ArrayList<>(route.edges());

        Document doc = newDocument(); //class that was given

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        for (int i = 0; i < routePoints.size(); i++) {
            Element rtept = doc.createElement("rtept");
            rte.appendChild(rtept);
            lat = Math.toDegrees(routePoints.get(i).lat());
            lon = Math.toDegrees(routePoints.get(i).lon());
            rtept.setAttribute("lat", String.format(Locale.ROOT, "%.5f", lat));
            rtept.setAttribute("lon", String.format(Locale.ROOT, "%.5f", lon));

            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);
            ele.setTextContent(String.format(Locale.ROOT, "%.2", profile.elevationAt(length)));
            length += routeEdges.get(i).length();
        }

        return doc;
    }

    public static void writeGPX(String fileName, Route route, ElevationProfile profile) throws IOException {
        Document doc = createGPX(route, profile);
        Writer w = Files.newBufferedWriter(Path.of(fileName));

        try {
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(w));
        } catch (TransformerException e) {
            throw new Error(e);
        }
    }

    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }
}
