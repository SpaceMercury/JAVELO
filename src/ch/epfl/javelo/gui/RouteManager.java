package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.Route;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;
import ch.epfl.javelo.projection.PointCh;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

/**
 * @author fuentes
 */
public final class RouteManager {
    private final Pane pane;
    private final RouteBean beanItinerary;
    private ReadOnlyObjectProperty<MapViewParameters> mvParameters;
    private final Consumer<String> errorConsumer;
    private  Polyline itinerary;
    private final Circle disk;
    private final double diskPos;
    private double xOffset;
    private double yOffset;



    public RouteManager(RouteBean beanItinerary, ReadOnlyObjectProperty<MapViewParameters> mvParameters, Consumer<String> errorConsumer) {

        this.beanItinerary = beanItinerary;
        this.mvParameters = mvParameters;
        this.errorConsumer = errorConsumer;
        this.itinerary= new Polyline();
        this.disk = new Circle(5);
        itinerary.setId("route");
        disk.setId("highlight");
        itinerary.setVisible(true);
        disk.setVisible(true);
        diskPos = beanItinerary.getHighlightedPosition();
        pane = new Pane();
        pane.getChildren().add(itinerary);
        pane.getChildren().add(disk);
        pane.setPickOnBounds(false);

        eventClickManager();
        this.mvParameters.addListener((object, oV, nV) -> repositionLine(oV, nV));
        this.beanItinerary.getRouteProperty().addListener((object, oV, nV) -> reconstructLine(oV, nV));

    }


    /**
     * Event manager for when the user clicks to add a waypoint
     */
    private void eventClickManager() {
        disk.setOnMouseClicked(click -> {
            //Transformed coordinates
            Point2D mCoord = disk.localToParent(click.getX(), click.getY());

            PointCh cursorPoint = (mvParameters.getValue().pointAt(mCoord.getX(), mCoord.getY())).toPointCh();
            Waypoint newWaypoint = new Waypoint(cursorPoint, beanItinerary.getRoute().nodeClosestTo(beanItinerary.getHighlightedPosition()));

            if (beanItinerary.getWaypoints().contains(newWaypoint)) {
                errorConsumer.accept("Un point de passage est déjà présent à cet endroit!");
            } else {
                beanItinerary.getWaypoints().add(beanItinerary.getRoute().indexOfSegmentAt(beanItinerary.getHighlightedPosition())+1, newWaypoint);
            }
        });
    }


    /**
     * Function that offsets the line when the map is dragged
     * @param oldV old Value
     * @param newV new Value
     */
    private void repositionLine(MapViewParameters oldV, MapViewParameters newV){

        if(oldV.zoom() != newV.zoom()){
            drawline();
        }
        else{
            lineOffsets(oldV, newV);
        }
    }

    private void drawline(){

    }

    /**
     * Function that calculates the offsets and applies them
     * @param oldV old Value of the MapViewParameters
     * @param newV New Value of the MapViewParameters
     */
    private void lineOffsets(MapViewParameters oldV, MapViewParameters newV){
        xOffset = oldV.x() - newV.x();
        yOffset = oldV.y() - newV.y();

        //Apply the offsets to the polyline and disk
        itinerary.setLayoutX(itinerary.getLayoutX() + xOffset);
        itinerary.setLayoutY(itinerary.getLayoutY() + yOffset);
        disk.setLayoutX(disk.getLayoutX() + xOffset);
        disk.setLayoutY(disk.getLayoutY() + yOffset);

        //Set offsets to 0 again, as the new position will become the new old position
        xOffset = 0;
        yOffset = 0;
    }

    /**
     * Function that reconstructs the polyLine when the user zooms
     * @param oldValue
     * @param newValue
     */
    private void reconstructLine(Route oldValue, Route newValue){

        if(newValue == null){
            itinerary.setVisible(false);
            disk.setVisible(false);
        }
        else{
            if(!Double.isNaN(diskPos)){
                disk.setVisible(true);
            }
            itinerary.setLayoutX(itinerary.getLayoutX());
            itinerary.setLayoutY(itinerary.getLayoutY());
            disk.setLayoutX(disk.getLayoutX());
            disk.setLayoutY(disk.getLayoutY());
            itinerary.setVisible(true);
        }
    }



    public Pane pane(){
        return pane;
    }

}

