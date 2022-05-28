package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/**
 * @author fuentes
 */
public final class TileManager {

    private final LinkedHashMap<TileId, Image> cache = new LinkedHashMap(100, 0.75f, true);
    private final Path access;
    private final String serverName;

    /**
     * Public constructor of TileManager
     *
     * @param access     the Path where the tiles will be stored
     * @param serverName serverName where we will obtain the tiles from
     * @throws IOException
     */
    public TileManager(Path access, String serverName) throws IOException {
        this.serverName = serverName;
        this.access = access;
    }

    /**
     * Method that returns the Image of a specific TileId
     *
     * @param id Id of the tile
     * @return the Image of that tile
     * @throws IOException
     */
    public Image imageForTileAt(TileId id) throws IOException {

        Path xPath = access.resolve(Integer.valueOf(id.zoom).toString()).resolve(Integer.valueOf(id.X).toString());
        Path finalPath = xPath.resolve(id.Y + ".png");

        //Check the local cache if the image exists
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        //Check if the image exists in the path
        if (Files.exists(finalPath)) {
            InputStream path = new FileInputStream(finalPath.toFile());
            return new Image(path);
        }
        //If the image of the tile wasn't in the previous: download it and store it
        storeImage(id, xPath, finalPath);
        return cache.get(id);
    }

    /**
     * Private method to store the image
     *
     * @param id        id of the tile
     * @param xPath     Path for x Coordinate in the folder of the image
     * @param finalPath Final path in the folder of the image
     * @throws IOException
     */
    private void storeImage(TileId id, Path xPath, Path finalPath) throws IOException {

        //Obtain image from the server
        URL u = new URL(
                "https://" + serverName + "/" + id.zoom + "/" + id.X + "/" + id.Y + ".png");
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");
        //Save image to the correct location in the Path
        try (InputStream i = c.getInputStream()) {
            OutputStream os = new FileOutputStream(Files.createDirectories(xPath).resolve(id.Y + ".png").toFile());
            i.transferTo(os);
        }
        //Obtain image from the Path and put it into the cache for faster access
        try (InputStream i = new FileInputStream(finalPath.toFile())) {
            Image streamImage = new Image(i);
            cache.put(id, streamImage);
        }
    }

    /**
     * Nested record TileId
     */
    public record TileId(int zoom, int X, int Y) {

        /**
         * @param zoom the level of zoom
         * @param X    x index of the tile
         * @param Y    y index of the tile
         * @return true if the ID of the Tile is valid, it is within bounds
         */
        public static boolean isValid(int zoom, int X, int Y) {
            Preconditions.checkArgument(zoom >= 0);
            return true;
        }

    }
}
