package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public final class TileManager {

    LinkedHashMap cache = new LinkedHashMap();

    public TileManager(Path access, String serverName)throws IOException{
        URL u = new URL(
                "https://tile.openstreetmap.org/19/271725/185422.png");
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");
        InputStream i = c.getInputStream();
        i.close();
        OutputStream os = new FileOutputStream("src/a/b/");
        os.close();
    }

    public Image imageForTileAt (TileId id) throws FileNotFoundException {



        return new Image(new FileInputStream("src/a/b/c.text"));
    }


    public record TileId(int zoom, int X, int Y){

        /**
         *
         * @param zoom the level of zoom
         * @param X x index of the tile
         * @param Y y index of the tile
         * @return true if the ID of the Tile is valid, it is within bounds
         */
        public static boolean isValid( int zoom, int X, int Y){
            Preconditions.checkArgument(zoom >= 0 );
            return true;
        }

    }
}
