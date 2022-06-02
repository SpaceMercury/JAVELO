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
    private static final double RADIUS = 5;
    private static final String DISK = "highlight";
    private static final String LINE = "route";
    private static final String ERROR = "Un point de passage est déjà présent à cet endroit !";
    private final Pane pane;
    private final RouteBean beanItinerary;
    private final ReadOnlyObjectProperty<MapViewParameters> mvParameters;
    private final Consumer<String> errorConsumer;
    private final Polyline itinerary;
    private final Circle disk;
    private final double diskPos;



    public RouteManager(RouteBean beanItinerary, ReadOnlyObjectProperty<MapViewParameters> mvParameters, Consumer<String> errorConsumer) {

        this.beanItinerary = beanItinerary;
        this.mvParameters = mvParameters;
        this.errorConsumer = errorConsumer;
        this.itinerary= new Polyline();
        this.disk = new Circle(RADIUS);
        itinerary.setId(LINE);
        disk.setId(DISK);
        itinerary.setVisible(true);
        disk.setVisible(true);
        diskPos = beanItinerary.getHighlightedPosition();
        this.pane = new Pane(itinerary, disk);
        this.pane.setPickOnBounds(false);
        this.mvParameters.addListener((object, oV, nV) -> repositionLine(oV, nV));
        this.beanItinerary.getRouteProperty().addListener((object, oV, nV) -> reconstructLine(oV, nV));
        eventClickManager();
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
                errorConsumer.accept(ERROR);
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
            drawLine();
        }
        else{
            lineOffsets(oldV, newV);
        }
    }

    private void drawLine(){

    }

    /**
     * Function that calculates the offsets and applies them
     * @param oldV old Value of the MapViewParameters
     * @param newV New Value of the MapViewParameters
     */
    private void lineOffsets(MapViewParameters oldV, MapViewParameters newV){
        //Apply the offsets to the polyline and disk
        itinerary.setLayoutX(itinerary.getLayoutX() + oldV.x() - newV.x());
        itinerary.setLayoutY(itinerary.getLayoutY() + oldV.y() - newV.y());
        disk.setLayoutX(disk.getLayoutX() + oldV.x() - newV.x());
        disk.setLayoutY(disk.getLayoutY() + oldV.y() - newV.y());
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

