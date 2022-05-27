package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;

public final class TileManager {

    LinkedHashMap<TileId, Image> cache = new LinkedHashMap(100, 0.75f, true);
    Path access;
    String serverName;

    public TileManager(Path access, String serverName)throws IOException{
        this.serverName = serverName;
        this.access = access;
    }

    public Image imageForTileAt (TileId id) throws IOException {

        if(cache.containsKey(id)){
            return cache.get(id);
        }
        Path xPath = access.resolve(Integer.valueOf(id.zoom).toString()).resolve(Integer.valueOf(id.X).toString());
        Path finalPath = xPath.resolve(id.Y + ".png");

        if(Files.exists(finalPath)){
            InputStream path = new FileInputStream(finalPath.toFile());
            return new Image(path);
        }
        storeImage(id, xPath, finalPath);
        return cache.get(id);
    }

    private void storeImage(TileId id, Path xPath, Path finalPath) throws IOException {
        String srcLink = "src/" + id.zoom + "/" + id.X + "/" + id.Y + ".png";

        URL u = new URL(
                "https://"+serverName+"/"+ id.zoom+"/"+ id.X+"/"+ id.Y+".png");
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");
        try(InputStream i = c.getInputStream()){
            OutputStream os = new FileOutputStream(Files.createDirectories(xPath).resolve(id.Y + ".png").toFile());
            i.transferTo(os);
        }
        try(InputStream i = new FileInputStream(finalPath.toFile())){
            Image streamImage = new Image(i);
            cache.put(id, streamImage);
        }
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
