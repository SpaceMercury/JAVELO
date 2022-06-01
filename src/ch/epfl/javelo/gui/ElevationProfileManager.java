package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;


import java.util.List;

/**
 * @author fuentes
 */
public final class ElevationProfileManager {

    private final BorderPane borderPane;
    private final VBox vBox;
    private final Pane pane;
    private final Polygon polygon;
    private final Path path;
    private final Line line;
    private final Text text;
    private final Group group;
    private final ObjectProperty<Affine> screenToWorld;
    private final ObjectProperty<Affine> worldToScreen;
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private final ReadOnlyDoubleProperty readDouble;
    private final ObjectProperty<Rectangle2D> borderRectangle;
    private final DoubleProperty mouse;

    //Constants
    private final static Insets INSETS = new Insets(10, 10, 20, 40);
    private final static int METER_MULTI = 1000;
    private final static int POS_MIN_DISTANCE = 50;
    private final static int ELE_MIN_DISTANCE = 25;
    private final static int[] POS_STEPS =
            {1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000};
    private final static int[] ELE_STEPS =
            {5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000};


    /**
     * Public ElevationProfileManager Constructor
     *
     * @param readObject the ObjectProperty of the ElevationProfile
     * @param readDouble the ObjectProperty of the mouse movement
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> readObject, ReadOnlyDoubleProperty readDouble) {

        elevationProfileProperty = readObject;
        this.readDouble = readDouble;

        path = new Path();
        path.setId("grid");
        group = new Group();
        polygon = new Polygon();
        polygon.setId("profile");
        line = new Line();
        text = new Text();
        pane = new Pane(path, group, polygon, line);
        vBox = new VBox(text);
        vBox.setId("profile_data");
        borderPane = new BorderPane(pane, null, null, vBox, null);
        borderPane.getStylesheets().add("elevation_profile.css");
        borderPane.setCenter(pane);
        mouse = new SimpleDoubleProperty();
        screenToWorld = new SimpleObjectProperty<>(new Affine());
        worldToScreen = new SimpleObjectProperty<>(new Affine());

        //binding the rectangle to the pane
        borderRectangle = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
        borderRectangle.bind(Bindings.createObjectBinding(this::createRectangle,
                pane.widthProperty(), pane.heightProperty()));

        //call all the methods
        setScreenToWorldTransform();
        eventHandler();
        createPolygon();
        showText();
        createGrid();

        //Listeners for redrawing the grid, the bottom text,
        //And recalculating the conversions
        elevationProfileProperty.addListener((p, oV, nV) -> {
            setScreenToWorldTransform();
            eventHandler();
            createPolygon();
            showText();
            createGrid();
        });
        borderRectangle.addListener((p, oV, nV) -> {
            setScreenToWorldTransform();
            eventHandler();
            createPolygon();
            showText();
            createGrid();
        });
        this.readDouble.addListener((p, oV, nV) -> movingLine());
    }

    /**
     * Function that generates the polygon of the profile
     */
    private void createPolygon() {

        if (elevationProfileProperty.get() != null) {
            polygon.getPoints().clear();
            //Minimum and maximum x coordinates of our rectangle
            double minX = borderRectangle.get().getMinX();
            double maxX = borderRectangle.get().getMaxX();

            //Adding the first edge point
            polygon.getPoints().add(borderRectangle.get().getMinX());
            polygon.getPoints().add(borderRectangle.get().getMaxY());

            //Iterate on the rectangle from left to right, adding the points to our polygon list
            for (double x = minX; x < maxX; x++) {
                //getting the real world point for our x coordinate
                Point2D worldPoint = screenToWorld.get().transform(x, 0);
                //converting it again to our rectangle coordinate to find the correct y position
                Point2D rectanglePoint = worldToScreen.get().transform(0, elevationProfileProperty.get().elevationAt(worldPoint.getX()));
                polygon.getPoints().add(x);
                polygon.getPoints().add(rectanglePoint.getY());
            }

            //Adding the last edge point
            polygon.getPoints().add(borderRectangle.get().getMaxX());
            polygon.getPoints().add(borderRectangle.get().getMaxY());

        }

    }

    /**
     * Function that creates the moving line inside the rectangle
     */
    private void movingLine() {
        if (!readDouble.getValue().isNaN()) {

            //Make the line visible
            line.setVisible(true);

            //Transform the coordinate to the rectangle coordinates
            double xCoord = worldToScreen.get().transform(readDouble.getValue(), 0).getX();
            line.setStartY(borderRectangle.get().getMaxY());
            line.setStartX(xCoord);
            line.setEndY(borderRectangle.get().getMinY());
            line.setEndX(xCoord);
        } else {
            //Hide the line
            line.setVisible(false);
        }
    }


    /**
     * Function that creates the grid in the rectangle
     */
    private void createGrid() {
        //Clear if there was a previous grid
        group.getChildren().clear();
        path.getElements().clear();

        //Get the value of elevationProfile
        ElevationProfile profile = elevationProfileProperty.get();
        Rectangle2D rectangle2D = borderRectangle.get();
        double elevation = profile.maxElevation() - profile.minElevation();
        int pos = 0;
        int ele = 0;


        for (int posStep : POS_STEPS) {
            pos = posStep;
            if (pos * rectangle2D.getWidth() / profile.length() >= POS_MIN_DISTANCE) {
                break;
            }
        }

        for (int eleStep : ELE_STEPS) {
            ele = eleStep;
            if ((ele * rectangle2D.getHeight() / elevation >= ELE_MIN_DISTANCE)) {
                break;
            }
        }


        // Iterate on all the grid lines and add them to the pathElements List

        for (int i = 0; i < profile.length() / pos; i++) {

            double transformedX = rectangle2D.getMinX() + worldToScreen.get().deltaTransform(pos * i, 0).getX();

            Text posText = new Text(Integer.toString(pos * i / METER_MULTI));

            path.getElements().add(new MoveTo(transformedX, borderRectangle.get().getMaxY()));
            path.getElements().add(new LineTo(transformedX, borderRectangle.get().getMinY()));

            posText.getStyleClass().add("grid_label");
            posText.getStyleClass().add("horizontal");
            posText.setFont(Font.font("Avenir", 10));
            posText.setTextOrigin(VPos.TOP);
            posText.setLayoutX(transformedX - (posText.prefWidth(0) / 2));
            posText.setLayoutY(rectangle2D.getMaxY());

            group.getChildren().add(posText);
        }

        for (int i = 0; i < elevation / ele; i++) {
            double transformedY = rectangle2D.getMaxY() + worldToScreen.get().deltaTransform(0, ele * i).getY();

            path.getElements().add(new MoveTo(rectangle2D.getMinX(), transformedY));
            path.getElements().add(new LineTo(rectangle2D.getMaxX(), transformedY));

            Text altText = new Text(Integer.toString((int) (ele * i + profile.minElevation())));
            altText.getStyleClass().add("grid_label");
            altText.getStyleClass().add("vertical");
            altText.setFont(Font.font("Avenir", 10));
            altText.setTextOrigin(VPos.CENTER);
            altText.setLayoutX(rectangle2D.getMinX() - (altText.prefWidth(0) + 2));
            altText.setLayoutY(transformedY);

            group.getChildren().add(altText);

        }
    }

    /**
     * Function that sets the bottom part of the text, showing statistics for our Route
     */
    private void showText() {

        text.setText(String.format("Longueur : %.1f km" +
                        "     Montée : %.0f m" +
                        "     Descente : %.0f m" +
                        "     Altitude : de %.0f m à %.0f m",
                elevationProfileProperty.get().length() / METER_MULTI,
                elevationProfileProperty.get().totalAscent(),
                elevationProfileProperty.get().totalDescent(),
                elevationProfileProperty.get().minElevation(),
                elevationProfileProperty.get().maxElevation()
        ));
        text.setFont(Font.font("Avenir", 10));
    }


    /**
     * Function that sets the functions worldToScreen and screenToWorld
     * Which convert the coordinates of the world to the screen
     * And the coordinates of the screen to the world respectively
     */
    private void setScreenToWorldTransform() {
        if (elevationProfileProperty.get() != null) {

            Affine temp = new Affine();
            ElevationProfile property = elevationProfileProperty.get();
            //Translate to the top left of our borderRectangle
            temp.prependTranslation(-borderRectangle.get().getMinX(), -borderRectangle.get().getMaxY());
            //Scale to the size of the borderRectangle
            temp.prependScale(property.length() / borderRectangle.get().getWidth(),
                    ((property.minElevation() - property.maxElevation()) / borderRectangle.get().getHeight()));
            //Move the profile up
            temp.prependTranslation(0, property.minElevation());

            screenToWorld.set(temp);
            try {
                worldToScreen.set(temp.createInverse());
            } catch (NonInvertibleTransformException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * Event handler for when the mouse is out of bounds
     */
    private void eventHandler() {
        pane.setOnMouseMoved(move -> {
            if (borderRectangle.get().contains(move.getX(), move.getY())) {
                mouse.set(Math.round(screenToWorld.get().transform(move.getX(), 0).getX()));
            } else {
                mouse.set(Double.NaN);
            }
        });
        pane.setOnMouseExited(out -> {
            mouse.set(Double.NaN);
        });
    }


    /**
     * Getter for the mouse position
     *
     * @return the DoubleProperty of the mouse (Read Only)
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mouse;
    }

    /**
     * Getter for the pane
     *
     * @return the borderPane where all of our graphic context is situated
     */
    public Pane pane() {
        return borderPane;
    }

    /**
     * Creates the rectangle in which our profile and grid will be displayed
     *
     * @return a Rectangle2D with the correct dimensions
     */
    private Rectangle2D createRectangle() {
        return new Rectangle2D(INSETS.getLeft(), INSETS.getTop(),
                Math.max(0,
                        pane.getWidth() - INSETS.getRight() - INSETS.getLeft()),
                Math.max(0, pane.getHeight() - INSETS.getTop()
                        - INSETS.getBottom()));
    }

}
