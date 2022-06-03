package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.function.Consumer;

/**
 * @author ventura
 */

public final class RouteManager {
    //Constants used in this class
    private final static String ERROR = "Un point de passage est déjà présent à cet endroit !";
    private final static String DISC = "highlight";
    private final static String LINE = "route";
    private final static double RADIUS = 5;
    //Attributes of the class
    private final RouteBean bean;
    private final ReadOnlyObjectProperty<MapViewParameters> parameters;
    private final Consumer<String> errorConsumer;
    private final Pane pane;
    private final Polyline line;
    private final Circle disc;

    /**
     * Public constructor for the RouteManager class
     * @param bean The RouteBean
     * @param parameters The MapViewParameters
     * @param errorConsumer The ErrorConsumer
     */
    public RouteManager(RouteBean bean, ReadOnlyObjectProperty<MapViewParameters> parameters, Consumer<String> errorConsumer) {
        this.bean = bean;
        this.parameters = parameters;
        this.errorConsumer = errorConsumer;
        this.line = new Polyline();
        this.disc = new Circle(RADIUS);
        line.setId(LINE);
        disc.setId(DISC);
        this.pane = new Pane(line, disc);
        this.pane.setPickOnBounds(false);
        bean.getWaypoints().addListener((ListChangeListener<? super Waypoint>)
                c -> draw());
        parameters.addListener((o, oV, nV) -> move(oV, nV));
        disc.setOnMouseClicked(e -> {
            PointCh pointCh = parameters.getValue().pointAt(e.getX(), e.getY()).toPointCh();
            double position = bean.getRoute().pointClosestTo(pointCh).position();
            int nodeId = bean.getRoute().nodeClosestTo(position);
            Waypoint waypoint = new Waypoint(bean.getRoute().pointAt(position), nodeId);
            if(bean.getWaypoints().contains(waypoint)) {
                errorConsumer.accept(ERROR);
            } else {
                bean.getWaypoints().add(bean.indexOfNonEmptySegmentAt(position) + 1, waypoint);
            }
        });
        bean.getHighlightedPositionProperty().addListener((o, oV, nV) -> draw());
        draw();
    }

    /**
     * Getter for the pane
     * @return pane
     */
    public Pane pane() {
        return this.pane;
    }

    /**
     * Method that draws the line and circle
     */
    private void draw() {
        line.getPoints().clear();
        setLine(line, 0, 0);
        setDisc(disc, 0, 0);
        if(bean.getRoute() == null) {
            line.setVisible(false);
            disc.setVisible(false);
            return;
        } else {
            line.setVisible(true);
        }
        if(!Double.isNaN(bean.getHighlightedPosition())) {
            disc.setVisible(true);
            PointCh pointCh = bean.getRoute().pointAt(bean.getHighlightedPosition());
            PointWebMercator discCoords = PointWebMercator.ofPointCh(pointCh);
            setDisc(disc, parameters.getValue().viewX(discCoords), parameters.getValue().viewY(discCoords));
        }
        for(PointCh pointCh : bean.getRoute().points()) {
            PointWebMercator pointWM = PointWebMercator.ofPointCh(pointCh);
            line.getPoints().add(parameters.getValue().viewX(pointWM));
            line.getPoints().add(parameters.getValue().viewY(pointWM));
        }
    }

    /**
     * Method that moves the line and disc when the map has been moved
     * @param old old mapview
     * @param newer new mapview
     */
    private void move(MapViewParameters old, MapViewParameters newer) {
        if(old != newer) {
            draw();
        } else {
            PointWebMercator lineCoords = old.pointAt(line.getLayoutX(), line.getLayoutY());
            PointWebMercator discCoords = old.pointAt(disc.getLayoutX(), disc.getLayoutY());
            setLine(line, newer.viewX(lineCoords), newer.viewY(lineCoords));
            setDisc(disc, newer.viewX(discCoords), newer.viewY(discCoords));
        }
    }

    /**
     * Method that sets new layout coords for a polyline
     * @param line The polyline
     * @param x new x coord
     * @param y new y coord
     */
    private void setLine(Polyline line, double x, double y) {
        line.setLayoutX(x);
        line.setLayoutY(y);
    }

    /**
     * Method that sets new center coords for a circle
     * @param disc The circle
     * @param x new x coord
     * @param y new y coord
     */
    private void setDisc(Circle disc, double x, double y) {
        disc.setCenterX(x);
        disc.setCenterY(y);
    }
}