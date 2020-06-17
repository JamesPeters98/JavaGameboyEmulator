package com.jamesdpeters.monitoring;

import com.jamesdpeters.gpu.Sprite;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.imgscalr.Scalr;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class SpriteWindow extends Application implements Runnable {

    private static Image nintendoLogo;
    public static final ObservableList<Sprite> data = FXCollections.observableArrayList();
    public static ListView<Sprite> listView;

    public static void main(String[] args) {
        launch(args);
    }

    public void open(){
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sprite Viewer");

        data.addAll(Sprite.getSprites());
        nintendoLogo = new Image("nintendo-logo-square.png");

        listView = new ListView<>(data);
        listView.setPrefSize(300,800);
        listView.setCellFactory(param -> new ListCell<Sprite>(){
            private ImageView imageView = new ImageView();
            private BufferedImage image = new BufferedImage(8,8, BufferedImage.TYPE_INT_RGB);

            @Override
            protected void updateItem(Sprite item, boolean empty) {
                super.updateItem(item, empty);

                imageView.setPreserveRatio(true);
                imageView.setFitWidth(40);
                imageView.setSmooth(true);

                if(empty){
                    setText("Empty");
                } else {
                    if(item != null) {
                        image.setRGB(0, 0, 8, 8, item.getTile().getRGBArray(item.getPalette()), 0, 8);
                        BufferedImage imageScaled = Scalr.resize(image,Scalr.Method.SPEED, 40, 40);
                        imageView.setImage(SwingFXUtils.toFXImage(imageScaled, null));

                        setText("X: "+item.getXPosition()+" Y: "+item.getYPosition()+" \n"+
                                "Index: "+item.getIndex());
                    } else {
                        imageView.setImage(nintendoLogo);
                    }
                    setGraphic(imageView);
                }
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(listView);
        primaryStage.setScene(new Scene(root, 300, 800));
        primaryStage.show();
    }

    @Override
    public void run() {
        launch();
    }
}
