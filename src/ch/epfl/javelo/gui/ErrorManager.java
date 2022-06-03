package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author fuentes
 */
public final class ErrorManager {

    private VBox pane;
    private FadeTransition fadeTransition;
    private PauseTransition pauseTransition;
    private FadeTransition opacity;
    private SequentialTransition allTransitions;
    private Text msg;

    /**
     * Public ErrorManager constructor
     */
    public ErrorManager(){

        pane = new VBox();

            pauseTransition = new PauseTransition(Duration.seconds(2));
            msg = new Text();
            pane = new VBox(msg);
            pane.getStylesheets().add("error.css");
            pane.setMouseTransparent(true);
            fadeTransition = new FadeTransition(Duration.seconds(0.2), pane);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(0.8);
            opacity = new FadeTransition(Duration.seconds(0.5), pane);
            opacity.setFromValue(0.8);
            opacity.setToValue(0);
            allTransitions = new SequentialTransition(fadeTransition, pauseTransition, opacity);

    }

    /**
     * Public getter for the pane
     * @return the Pane
     */
    public Pane pane(){
        return pane;
    }

    /**
     * Public function that assigns the errorMessage to the Vbox and makes an errorSound
     * @param errorMessage a short String explaining error
     */
    public void displayError(String errorMessage){
        //Arreter toutes transitions anterieurres
        allTransitions.stop();

        //Mettre le text dans la Vbox
        msg.setText(errorMessage);

        allTransitions.play();
        //Production du son
        java.awt.Toolkit.getDefaultToolkit().beep();

    }
}
