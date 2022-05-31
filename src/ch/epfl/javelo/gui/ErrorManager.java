package ch.epfl.javelo.gui;

import javafx.scene.layout.Pane;

public final class ErrorManager {


    private Pane pane;

    public ErrorManager(){

        pane = new Pane();

    }

    public Pane pane(){
        return pane;
    }

    public String displayError(String errorMessage){

        java.awt.Toolkit.getDefaultToolkit().beep();

        return "55";
    }
}
