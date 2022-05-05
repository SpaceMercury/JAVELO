package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;

import java.awt.*;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;

public final class TileManager {


    public TileManager(Path access, String serverName){



    }

    public Image imageAt (TileId id){

        URL u = new URL(
                "https://tile.openstreetmap.org/19/271725/185422.png");
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");
        InputStream i = c.getInputStream();


        return new Image;
    }


    public record TileId(){

        /**
         *
         * @param zoom the level of zoom
         * @param X x index of the tile
         * @param Y y index of the tile
         * @return true if the ID of the Tile is valid, it is within bounds
         */
        public static boolean isValid( int zoom, int X, int Y){
            Preconditions.checkArgument(zoom >= 0 && );
            return true;
        }

    }
}
