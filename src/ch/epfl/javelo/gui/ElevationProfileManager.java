package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Affine;


public final class ElevationProfileManager {

    Pane borderPane;
    VBox Vbox;
    Pane pane;
    Affine screenToWorld;
    Affine worldToScreen;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> readObject, ReadOnlyDoubleProperty readDouble){
        borderPane = new Pane();
        new Insets(10, 10, 20, 40);
    }

    public ReadOnlyObjectProperty<MouseEvent> mousePositionOnProfileProperty(){
        return null;
    }

    public Pane pane(){
        return borderPane;
    }
}
