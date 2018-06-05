package pos.dongwang.util;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pos.dongwang.Main;

import java.io.IOException;

/**
 * Created by lodi on 2018/4/11.
 */
public class ShowViewUtil {

    public void showWarningView(String title,  String content, Stage primaryStage,Rectangle2D  primaryScreenBounds) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("controller/view/WarningView.fxml"));
        FlowPane flowPane  =  loader.load();
        double flowPaneWidth = primaryScreenBounds.getWidth()/3;
        double flowPaneHeight = primaryScreenBounds.getHeight()/3;
        flowPane.setPrefHeight(flowPaneHeight);
        flowPane.setPrefWidth(flowPaneWidth);
        Label titleLabel = (Label) flowPane.getChildren().get(0);
        titleLabel.setPrefHeight(flowPaneHeight/6-4);
        titleLabel.setPrefWidth(flowPaneWidth-2);
        titleLabel.setText(title);
        Label contentLabel = (Label) flowPane.getChildren().get(1);
        contentLabel.setPrefHeight(flowPaneHeight/6*3);
        contentLabel.setPrefWidth(flowPaneWidth-2);
        contentLabel.setText(content);
        FlowPane buttomFlowPane = (FlowPane) flowPane.getChildren().get(2);
        buttomFlowPane.setPrefHeight(flowPaneHeight/6*2-6);
        buttomFlowPane.setPrefWidth(flowPaneWidth-2);
        Button confirmButton = (Button) buttomFlowPane.getChildren().get(0);
        confirmButton.setPrefHeight(flowPaneHeight/6*2/3*2);
        confirmButton.setPrefWidth(flowPaneWidth/2);
        Stage confirmStage = new Stage();
        confirmButton.setOnAction(event -> {
            confirmStage.close();
        });
        confirmStage.setScene(new Scene(flowPane));
        confirmStage.initOwner(primaryStage);
        confirmStage.initModality(Modality.APPLICATION_MODAL);
        confirmStage.initStyle(StageStyle.TRANSPARENT);
        confirmStage.show();
    }
}
