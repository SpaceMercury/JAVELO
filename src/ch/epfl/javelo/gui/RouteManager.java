package ch.epfl.javelo.gui;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author fuentes
 */
public final class RouteManager {
    private final Pane pane;
    private final RouteBean beanItinerary;
    private ReadOnlyObjectProperty<MapViewParameters> mvParameters;
    private final Consumer<String> errorConsumer;
    private  Polyline itinerary;
    private final Circle disc;


    public RouteManager(RouteBean beanItinerary, ReadOnlyObjectProperty<MapViewParameters> mvParameters, Consumer<String> errorConsumer) {

        this.beanItinerary = beanItinerary;
        this.mvParameters = mvParameters;
        this.errorConsumer = errorConsumer;
        this.itinerary= new Polyline();
        this.disc = new Circle(5);
        itinerary.setId("route");
        disc.setId("highlight");
        itinerary.setVisible(true);
        disc.setVisible(true);
        pane = new Pane();
        pane.getChildren().add(itinerary);
        pane.getChildren().add(disc);

        eventClickManager();
      /*  reconstruct();

        beanItinerary.waypoints().addListener((ListChangeListener<? super Waypoint>) e -> {
            // rend le panneau invisible quand l'itinéraire entier n'existe pas
            pane.setVisible(beanItinerary.route() != null);
            reconstruct();
            placeCircle();
        });


       */
    }

  /*  private void placeCircle() {
        Route route = bean.route();

        if (route != null){
            PointCh circlePointCh = route.pointAt(bean.highlightedPosition());
            PointWebMercator circlePointWM = PointWebMercator.ofPointCh(circlePointCh);
            MapViewParameters mapViewParameters = parameters.get();

            disc.setCenterX(mapViewParameters.viewX(circlePointWM));
            disc.setCenterY(mapViewParameters.viewY(circlePointWM));
        }
    }

    private void reconstruct(){
        System.out.println("reconstruct");

        bean.routeProperty().addListener(event -> {
            itinerary.getPoints().clear();
            itinerary.setLayoutX(-parameters.get().coordX());
            itinerary.setLayoutY(-parameters.get().coordY());

            if(bean.routeProperty().get()!= null){
                List<Double> points = new ArrayList<>();
                System.out.println("route differente de null");

                for (PointCh point :  bean.routeProperty().get().points()) {
                    PointWebMercator pointWM = PointWebMercator.ofPointCh(point);
                    points.add(pointWM.xAtZoomLevel(parameters.get().zoomLevel()));
                    points.add(pointWM.yAtZoomLevel(parameters.get().zoomLevel()));
                }
                itinerary.getPoints().addAll(points);


            }});


    }
    private void moveRoute(){
        System.out.println("move route");

        bean.routeProperty().addListener(event -> {
            System.out.println("move route");

            if(bean.routeProperty().get() != null){
                itinerary.setLayoutX(-parameters.get().coordX());
                itinerary.setLayoutY(-parameters.get().coordY());
                disc.setCenterX(bean.routeProperty().get().pointAt(bean.highlightedPosition()).e());
                disc.setCenterY(bean.routeProperty().get().pointAt(bean.highlightedPosition()).n());
                System.out.println("route differente de null");

            }});

    }



   */

    private void eventClickManager(){
        disc.setOnMouseClicked(click -> {
            //Transformed coordinates
            Point2D mCoord = disc.localToParent(click.getX(), click.getY());

            PointCh cursorPoint = (mvParameters.getValue().pointAt(mCoord.getX(), mCoord.getY())).toPointCh();
            Waypoint newWaypoint = new Waypoint(cursorPoint,beanItinerary.getRoute().nodeClosestTo(beanItinerary.getHighlightedPosition()));

                if(beanItinerary.getWaypoints().contains(newWaypoint)){
                    errorConsumer.accept("Un point de passage est déjà présent à cet endroit!");
                }
                else{
                    beanItinerary.getWaypoints().add(beanItinerary.getRoute().indexOfSegmentAt(beanItinerary.getHighlightedPosition()) , newWaypoint );
                }
        });



/*
        //la route change
        bean.routeProperty().addListener(e -> {
            System.out.println("routeProperty listener");

            if ( e == null){
                System.out.println("e null en listener RP");
                System.out.println("invisible");
                itinerary.setVisible(false);
                disc.setVisible(false);


            }else {
                System.out.println("e pas null en listener RP");
                System.out.println("Visible");
                disc.setVisible(true);
                itinerary.setVisible(true);

                moveRoute();
            }

        });
        // la highlightedPosition change
        bean.highlightedPositionProperty().addListener((pos, oldV, newV) ->{
            System.out.println("listener highlightedPosition");

            if(Double.isNaN(newV.doubleValue())){
                System.out.println("newV is null in highlightedPosition Listener");
                System.out.println("invisible");
                disc.setVisible(false);
            }

        });

        //la object property (le mapviewparameter) change
        parameters.addListener(e -> {
            //disc.setVisible(false);
            System.out.println("objectProperty Listener");

            // System.out.println("reconstruct");

        });

        parameters.addListener((parameters, oldV, newV) ->{
            if(oldV.zoomLevel() != newV.zoomLevel()){
                //reconstruire route
                reconstruct();
            }else {
                // deplacer route
                moveRoute();
            }
            //placeCircle();
        });

        //bean.waypoints().addListener();
        bean.waypoints().addListener((ListChangeListener<? super Waypoint>) c -> reconstruct());


        //  itinerary.addEventHandler()


     /*   //itineraire change
        objectProperty.addListener((e)-> itinerary.setVisible(false));
        //niveau de zoom change
        objectProperty.addListener(e -> itinerary.setVisible(false));*/
    }


    public Pane pane(){
        return pane;
    }
}

