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
        if(Files.exists(access)){
            InputStream path = new FileInputStream(access.toFile());
            return new Image(path);
        }


        storeImage(id);

            Files.createDirectories(access);


        return cache.get(id);
    }

    private void storeImage(TileId id) throws IOException {
        String srcLink = "src/" + id.zoom + "/" + id.X + "/" + id.Y + ".png";

        URL u = new URL(
                "https://"+serverName+"/"+ id.zoom+"/"+ id.X+"/"+ id.Y+".png");
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");
        try(InputStream i = c.getInputStream()){
            OutputStream os = new FileOutputStream(String.valueOf(access));
            i.transferTo(os);
            Image streamImage = new Image(i);
            cache.put(id, streamImage);
            os.close();
        }


    }

    private boolean isCacheMax(){
        return (cache.size() == 100);
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
