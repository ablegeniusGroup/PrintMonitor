package pos.dongwang;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.ibatis.session.SqlSession;
import pos.dongwang.controller.FoodListController;
import pos.dongwang.controller.HangTableController;
import pos.dongwang.dao.*;
import pos.dongwang.dto.*;
import pos.dongwang.enums.LogTypeEnum;
import pos.dongwang.enums.OrderType;
import pos.dongwang.enums.PrintStateEnum;
import pos.dongwang.mapper.TGoodsDtoMapper;
import pos.dongwang.model.TGoods;
import pos.dongwang.model.TopButton;
import pos.dongwang.mybatis.MyBatisSqlSessionFactory;
import pos.dongwang.print.GetPrintOrReportStr;
import pos.dongwang.print.PrintRxTxVirtaul;
import pos.dongwang.print.RXTXInit;
import pos.dongwang.properties.ReadProperties;
import pos.dongwang.properties.SettingModel;
import pos.dongwang.task.FindUnPrintListTask;
import pos.dongwang.controller.OrderListController;
import pos.dongwang.model.PosOrder;
import pos.dongwang.task.PingTask;
import pos.dongwang.util.*;

import javax.print.attribute.standard.PrinterState;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {


    private FlowPane flowPane = new FlowPane();

    private Timer timerPrintData = new Timer();

    private Stage primaryStage;

    private Integer showCount = ReadProperties.readIntegerByKey("showCount");

    Rectangle2D primaryScreenBounds;

    //全部
    private ObservableList<ObservableList<PosOrder>> orderBoxs = FXCollections.observableArrayList();

    //首單
    private ObservableList<ObservableList<PosOrder>> firstOrderBoxs = FXCollections.observableArrayList();

    //加單
    private ObservableList<ObservableList<PosOrder>> addOrderBoxs = FXCollections.observableArrayList();

    //尾單
    private ObservableList<ObservableList<PosOrder>> lastOrderBoxs = FXCollections.observableArrayList();

    //選中的首單
    private ObservableList<ObservableList<PosOrder>> selectedFirstOrderBoxs = FXCollections.observableArrayList();

    //選中的加單
    private ObservableList<ObservableList<PosOrder>> selectedAddOrderBoxs = FXCollections.observableArrayList();

    //選中的尾單
    private ObservableList<ObservableList<PosOrder>> selectedLastOrderBoxs = FXCollections.observableArrayList();

    //選中的所有單
    private ObservableList<ObservableList<PosOrder>> selectedOrderBoxs = FXCollections.observableArrayList();

    //選中的掛起的單
   // private ObservableList<ObservableList<PosOrder>> selectedHangOrderBoxs = FXCollections.observableArrayList();

    //掛起的單
    private ObservableList<ObservableList<PosOrder>> hangOrderBoxs = FXCollections.observableArrayList();


    private ObservableList<TopButton> topButtons;

    private StringProperty boxTotal = new SimpleStringProperty();

    private StringProperty allBoxTotal = new SimpleStringProperty();

    private StringProperty firstBoxTotal = new SimpleStringProperty();

    private StringProperty addBoxTotal = new SimpleStringProperty();

    private StringProperty lastBoxTotal = new SimpleStringProperty();

    private StringProperty hangBoxTotal = new SimpleStringProperty();

    private StringProperty  selectedOrderType = new SimpleStringProperty();

    private Integer showcount;

    private StringProperty autoPrintProp = new SimpleStringProperty();

    private StringProperty isAutoPrint = new SimpleStringProperty();

    private  ObservableList<Button> buttonList = FXCollections.observableArrayList();

    private Stage foodStage;

  /*  private StringProperty orderType = new SimpleStringProperty(OrderType.ALLORDER.getValue());*/

    private IntegerProperty orderHangCount = new SimpleIntegerProperty();

    //出單規則：尾尾首加
    private RulerDto rulerDto = new RulerDto();

    private Circle circle = new Circle();

    public static ExecutorService executor = Executors.newFixedThreadPool(3);

    //记录选中的订单号
    private String hangRefNum;

    static {
        RXTXInit.loadLib();
    }


    public Main() {
        autoPrintProp.set("自動列印:" + ReadProperties.readStringByKey("autoPrint"));
        boxTotal.set(orderBoxs.size() + selectedOrderBoxs.size() + "個任務需要列印，請盡快處理");

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("已經停止------------------------");
        System.exit(0);
    }



    @Override
    public void start(Stage primaryStage) throws Exception {
        showcount =  Integer.parseInt(ReadProperties.readStringByKey("showCount"));
        BorderPane root = new BorderPane();
        this.primaryStage = primaryStage;
        root.getStylesheets().add(Main.class.getResource("controller/view/DarkTheme.css").toExternalForm());
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
         this.setPrimaryScreenBounds(primaryScreenBounds);
        isAutoPrint =  new SimpleStringProperty(ReadProperties.readStringByKey("autoPrint")) ;
        autoPrintProp.set("自動列印:" + ReadProperties.readStringByKey("autoPrint"));
        selectedOrderType.set(OrderType.ALLORDER.getValue());
        selectedOrderBoxs.addListener(new ListChangeListener<ObservableList<PosOrder>>() {
            @Override
            public void onChanged(Change<? extends ObservableList<PosOrder>> c) {
               if(selectedOrderType.getValue().equals(OrderType.ALLORDER.getValue())){
                   if (c.next()) {
                       if (c.getAddedSize() > 0) {

                           //監聽列表新增，如果有新增則更新模型
                           setupVbox(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight(), c.getAddedSubList().get(0),primaryStage);

                       } else if (c.getRemovedSize() > 0) {
                           System.out.println("刪除");
                       }
                   }
                   Platform.runLater(() ->
                   allBoxTotal.set("全部("+ (selectedOrderBoxs.size() + orderBoxs.size())+")"));
                   Platform.runLater(() ->
                   firstBoxTotal.set("首單("+ (selectedFirstOrderBoxs.size() +firstOrderBoxs.size())+")"));
                   Platform.runLater(() -> addBoxTotal.set("加單("+ (selectedAddOrderBoxs.size() +addOrderBoxs.size())+")"));
                   Platform.runLater(() ->lastBoxTotal.set("尾單("+(selectedLastOrderBoxs.size() +lastOrderBoxs.size())+")"));
               }
            }
        });
        selectedFirstOrderBoxs.addListener(new ListChangeListener<ObservableList<PosOrder>>() {
            @Override
            public void onChanged(Change<? extends ObservableList<PosOrder>> c) {
                if(selectedOrderType.getValue().equals(OrderType.FIRSTORDER.getValue())){
                    if (c.next()) {
                        if (c.getAddedSize() > 0) {
                            //監聽列表新增，如果有新增則更新模型
                            setupVbox(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight(), c.getAddedSubList().get(0), primaryStage);

                        } else if (c.getRemovedSize() > 0) {
                            System.out.println("刪除");
                        }
                       // Platform.runLater(() ->boxTotal.set(selectedFirstOrderBoxs.size() + firstOrderBoxs.size() + "個任務需要列印，請盡快處理"));
                        Platform.runLater(() ->
                                allBoxTotal.set("全部("+ (selectedOrderBoxs.size() + orderBoxs.size())+")"));
                        Platform.runLater(() ->
                                firstBoxTotal.set("首單("+ (selectedFirstOrderBoxs.size() +firstOrderBoxs.size())+")"));
                        Platform.runLater(() -> addBoxTotal.set("加單("+ (selectedAddOrderBoxs.size() +addOrderBoxs.size())+")"));
                        Platform.runLater(() ->lastBoxTotal.set("尾單("+(selectedLastOrderBoxs.size() +lastOrderBoxs.size())+")"));
                    }
                }
            }
        });
        selectedAddOrderBoxs.addListener(new ListChangeListener<ObservableList<PosOrder>>() {
            @Override
            public void onChanged(Change<? extends ObservableList<PosOrder>> c) {
                if(selectedOrderType.getValue().equals(OrderType.ADDORDER.getValue())){
                    if (c.next()) {
                        if (c.getAddedSize() > 0) {
                            //監聽列表新增，如果有新增則更新模型
                            setupVbox(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight(), c.getAddedSubList().get(0),primaryStage);

                        } else if (c.getRemovedSize() > 0) {
                            System.out.println("刪除");
                        }
                       // Platform.runLater(() ->boxTotal.set(selectedAddOrderBoxs.size() + addOrderBoxs.size() + "個任務需要列印，請盡快處理"));
                        Platform.runLater(() ->
                                allBoxTotal.set("全部("+ (selectedOrderBoxs.size() + orderBoxs.size())+")"));
                        Platform.runLater(() ->
                                firstBoxTotal.set("首單("+ (selectedFirstOrderBoxs.size() +firstOrderBoxs.size())+")"));
                        Platform.runLater(() -> addBoxTotal.set("加單("+ (selectedAddOrderBoxs.size() +addOrderBoxs.size())+")"));
                        Platform.runLater(() ->lastBoxTotal.set("尾單("+(selectedLastOrderBoxs.size() +lastOrderBoxs.size())+")"));
                    }
                }
            }
        });
        selectedLastOrderBoxs.addListener(new ListChangeListener<ObservableList<PosOrder>>() {
            @Override
            public void onChanged(Change<? extends ObservableList<PosOrder>> c) {
                if(selectedOrderType.getValue().equals(OrderType.TAILORDER.getValue())){
                    if (c.next()) {
                        if (c.getAddedSize() > 0) {
                            //監聽列表新增，如果有新增則更新模型
                            setupVbox(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight(), c.getAddedSubList().get(0),primaryStage);
                        } else if (c.getRemovedSize() > 0) {
                            System.out.println("刪除");
                        }
                       // Platform.runLater(() ->boxTotal.set(selectedLastOrderBoxs.size() + lastOrderBoxs.size() + "個任務需要列印，請盡快處理"));
                        Platform.runLater(() ->
                                allBoxTotal.set("全部("+ (selectedOrderBoxs.size() + orderBoxs.size())+")"));
                        Platform.runLater(() ->
                                firstBoxTotal.set("首單("+ (selectedFirstOrderBoxs.size() +firstOrderBoxs.size())+")"));
                        Platform.runLater(() -> addBoxTotal.set("加單("+ (selectedAddOrderBoxs.size() +addOrderBoxs.size())+")"));
                        Platform.runLater(() ->lastBoxTotal.set("尾單("+(selectedLastOrderBoxs.size() +lastOrderBoxs.size())+")"));
                    }
                }
            }
        });
        hangOrderBoxs.addListener(new ListChangeListener<ObservableList<PosOrder>>() {
            @Override
            public void onChanged(Change<? extends ObservableList<PosOrder>> c) {
                if(selectedOrderType.getValue().equals(OrderType.HANG.getValue())){
                    if (c.next()) {
                        if (c.getAddedSize() > 0) {
                            //監聽列表新增，如果有新增則更新模型
                            setupHangVbox(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight(), c.getAddedSubList().get(0),primaryStage);
                        } else if (c.getRemovedSize() > 0) {
                            System.out.println("刪除");
                                        System.out.println("刪除");
                            //ObservableList<PosOrder> posOrderObservableList = c.getList();
                           // Platform.runLater(() ->  getFlowPane().getChildren().remove(0));
                            }
                        }
                        // Platform.runLater(() ->boxTotal.set(selectedAddOrderBoxs.size() + addOrderBoxs.size() + "個任務需要列印，請盡快處理"));
                        Platform.runLater(() ->
                                allBoxTotal.set("全部("+ (selectedOrderBoxs.size() + orderBoxs.size())+")"));
                        Platform.runLater(() ->
                                firstBoxTotal.set("首單("+ (selectedFirstOrderBoxs.size() +firstOrderBoxs.size())+")"));
                        Platform.runLater(() -> addBoxTotal.set("加單("+ (selectedAddOrderBoxs.size() +addOrderBoxs.size())+")"));
                        Platform.runLater(() ->lastBoxTotal.set("尾單("+(selectedLastOrderBoxs.size() +lastOrderBoxs.size())+")"));
                    }
            }
        });
        orderBoxs.addListener(new ListChangeListener<ObservableList<PosOrder>>() {
            @Override
            public void onChanged(Change<? extends ObservableList<PosOrder>> c) {
                if (c.next()) {
                    if (c.getAddedSize() > 0) {
                        System.out.println("添加");
                            //ObservableList<ObservableList<PosOrder>> posOrderMap = main.getOrderBoxs();
                        Platform.runLater(() ->
                                allBoxTotal.set("全部("+ (selectedOrderBoxs.size() + orderBoxs.size())+")"));
                        Platform.runLater(() ->
                                firstBoxTotal.set("首單("+ (selectedFirstOrderBoxs.size() +firstOrderBoxs.size())+")"));
                        Platform.runLater(() -> addBoxTotal.set("加單("+ (selectedAddOrderBoxs.size() +addOrderBoxs.size())+")"));
                        Platform.runLater(() ->lastBoxTotal.set("尾單("+(selectedLastOrderBoxs.size() +lastOrderBoxs.size())+")"));
                    } else if (c.getRemovedSize() > 0) {
                        System.out.println("刪除");
                    }
                }
            }
        });
        addOrderBoxs.addListener(new ListChangeListener<ObservableList<PosOrder>>() {
            @Override
            public void onChanged(Change<? extends ObservableList<PosOrder>> c) {
                if (c.next()) {
                    if (c.getAddedSize() > 0) {
                        System.out.println("添加");
                        Platform.runLater(() ->
                                allBoxTotal.set("全部("+ (selectedOrderBoxs.size() + orderBoxs.size())+")"));
                        Platform.runLater(() ->
                                firstBoxTotal.set("首單("+ (selectedFirstOrderBoxs.size() +firstOrderBoxs.size())+")"));
                        Platform.runLater(() -> addBoxTotal.set("加單("+ (selectedAddOrderBoxs.size() +addOrderBoxs.size())+")"));
                        Platform.runLater(() ->lastBoxTotal.set("尾單("+(selectedLastOrderBoxs.size() +lastOrderBoxs.size())+")"));
                    } else if (c.getRemovedSize() > 0) {
                        System.out.println("刪除");
                    }
                }
            }
        });
        firstOrderBoxs.addListener(new ListChangeListener<ObservableList<PosOrder>>() {
            @Override
            public void onChanged(Change<? extends ObservableList<PosOrder>> c) {
                if (c.next()) {
                    if (c.getAddedSize() > 0) {
                        System.out.println("添加");
                        Platform.runLater(() ->
                                allBoxTotal.set("全部("+ (selectedOrderBoxs.size() + orderBoxs.size())+")"));
                        Platform.runLater(() ->
                                firstBoxTotal.set("首單("+ (selectedFirstOrderBoxs.size() +firstOrderBoxs.size())+")"));
                        Platform.runLater(() -> addBoxTotal.set("加單("+ (selectedAddOrderBoxs.size() +addOrderBoxs.size())+")"));
                        Platform.runLater(() ->lastBoxTotal.set("尾單("+(selectedLastOrderBoxs.size() +lastOrderBoxs.size())+")"));
                    } else if (c.getRemovedSize() > 0) {
                        System.out.println("刪除");
                    }
                }
            }
        });
        lastOrderBoxs.addListener(new ListChangeListener<ObservableList<PosOrder>>() {
            @Override
            public void onChanged(Change<? extends ObservableList<PosOrder>> c) {
                if (c.next()) {
                    if (c.getAddedSize() > 0) {
                        System.out.println("添加");
                        Platform.runLater(() ->
                                allBoxTotal.set("全部("+ (selectedOrderBoxs.size() + orderBoxs.size())+")"));
                        Platform.runLater(() ->
                                firstBoxTotal.set("首單("+ (selectedFirstOrderBoxs.size() +firstOrderBoxs.size())+")"));
                        Platform.runLater(() -> addBoxTotal.set("加單("+ (selectedAddOrderBoxs.size() +addOrderBoxs.size())+")"));
                        Platform.runLater(() ->lastBoxTotal.set("尾單("+(selectedLastOrderBoxs.size() +lastOrderBoxs.size())+")"));
                    } else if (c.getRemovedSize() > 0) {
                        System.out.println("刪除");
                    }
                }
            }
        });



        topComponent(root,primaryScreenBounds,primaryStage);
        bottomComponent(root, primaryScreenBounds,primaryStage);
        centerComponnet(root, primaryScreenBounds,primaryStage);

        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());

        // primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root));
        primaryStage.setFullScreen(true);
        primaryStage.show();

        getPrintDataTask();


    }

    public void centerComponnet(BorderPane root, Rectangle2D primaryScreenBounds,Stage primaryStage) {

        //設置中間下單信息
        ScrollPane pane = new ScrollPane();
        pane.setPrefSize(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight()/10*8);
        pane.setHvalue(primaryScreenBounds.getHeight()/10*8 );
        flowPane.setPrefWidth(primaryScreenBounds.getWidth());
        flowPane.setPrefHeight(primaryScreenBounds.getHeight()/10*8 );
        flowPane.setHgap(3);
        flowPane.setVgap(2);
        flowPane.setPadding(new Insets(0, 0, 0, 3));
        flowPane.setStyle("-fx-background-color: #1d1d1d");
        pane.setContent(flowPane);
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
      //  pane.setDisable(true);
        root.setCenter(pane);
        selectedOrderBoxs.forEach(posOrders -> {
            setupVbox(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight()/10*8, posOrders, primaryStage);
        });



    }


    public void setupVbox(Double parentsWidth, Double parentsHeight, ObservableList<PosOrder> posOrders,Stage primaryStage) {
        try {

            Double paneWidth = parentsWidth / 3 - 5;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("controller/view/OrderListView.fxml"));
            final VBox posOrderBox = loader.load();
            posOrderBox.setPrefSize(paneWidth, parentsHeight);
            OrderListController orderListController = loader.getController();
            orderListController.setMain(this, posOrders, posOrderBox, primaryStage);
            posOrderBox.setStyle("-fx-border-width: 10px; -fx-border-color:#c5c6c6");
            if(AppUtils.isNotBlank(posOrders) && posOrders.size() >showcount){
                posOrderBox.setStyle("-fx-border-width: 10px;-fx-border-color:red");
            }
            HBox hBox = (HBox) posOrderBox.getChildren().get(0);
            hBox.setPrefHeight(parentsHeight/10*1);
            hBox.setPrefWidth(paneWidth);
            Label orderTypeLabel = (Label) hBox.getChildren().get(0);
            orderTypeLabel.setPrefWidth(paneWidth/3);
            orderTypeLabel.setPrefHeight(parentsHeight/10*1);
            Label tableNumLabel = (Label) hBox.getChildren().get(1);
            tableNumLabel.setPrefWidth(paneWidth/3*2);
            tableNumLabel.setPrefHeight(parentsHeight/10*1);
            HBox buttonBox = (HBox) posOrderBox.getChildren().get(1);
            buttonBox.setPrefHeight(parentsHeight/10*1);
            buttonBox.setPrefWidth(paneWidth);
            Label orderCountLabel = (Label) buttonBox.getChildren().get(0);
            orderCountLabel.setPrefWidth(paneWidth/2);
            orderCountLabel.setPrefHeight(parentsHeight/10*1);
            FlowPane buttonFlowPane = (FlowPane) buttonBox.getChildren().get(1);
            buttonFlowPane.setPrefHeight(parentsHeight/10*1);
            buttonFlowPane.setPrefWidth(paneWidth/2);
            buttonFlowPane.setHgap(10);
            Button printButton = (Button)  buttonFlowPane.getChildren().get(1);
            printButton.setPrefWidth(paneWidth/4-10);
            printButton.setPrefHeight(parentsHeight/10*1);
            orderListController.getPosOrderTab().setPrefWidth(paneWidth);
            orderListController.getItemDescCol().setPrefWidth(paneWidth/6*3);
            orderListController.getItemQtyCol().setPrefWidth(paneWidth/6);
            orderListController.getItemTimeCol().setPrefWidth(paneWidth/6*2);
           // orderListController.getItemDescCol().setStyle(" -fx-font-size: 18pt;-fx-font-family:Segoe UI Light;-fx-text-fill: darkkhaki; -fx-alignment: center-left; -fx-opacity: 1;-fx-font-weight: bolder;-fx-pref-width:" + paneWidth/5*2);
           // orderListController.getItemQtyCol().setStyle(" -fx-font-size: 23pt;-fx-font-family:Segoe UI Light;-fx-text-fill: darkkhaki; -fx-alignment: center-center; -fx-opacity: 1;-fx-font-weight: bolder;-fx-pref-width:" + paneWidth/5*1);
           //orderListController.getItemTimeCol().setStyle(" -fx-font-size: 18pt;-fx-font-family:Segoe UI Light;-fx-text-fill: darkkhaki; -fx-alignment: center-center; -fx-opacity: 1;-fx-font-weight: bolder;-fx-pref-width:" + paneWidth/5*2);
            if(PrintStateEnum.HANG.getValue().equals(posOrders.get(0).getPrint_state())){
                 buttonFlowPane.getChildren().remove(0);
            }
            else{
                Button hangButton = (Button) buttonFlowPane.getChildren().get(0);
                hangButton.setPrefWidth(paneWidth/4-10);
                hangButton.setPrefHeight(parentsHeight/10*1);
            }
            if(!selectedOrderType.getValue().equals(OrderType.HANG.getValue())){
                Platform.runLater(() ->  flowPane.getChildren().add(posOrderBox));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupHangVbox(Double parentsWidth, Double parentsHeight, ObservableList<PosOrder> posOrders,Stage primaryStage) {
        try {

            Double paneWidth = parentsWidth / 3 - 5;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("controller/view/OrderListView.fxml"));
            final VBox posOrderBox = loader.load();
            posOrderBox.setPrefSize(paneWidth, parentsHeight);
            OrderListController orderListController = loader.getController();
            orderListController.setMain(this, posOrders, posOrderBox, primaryStage);
            posOrderBox.setStyle("-fx-border-width: 10px; -fx-border-color: #c5c6c6");
            if(AppUtils.isNotBlank(posOrders) && posOrders.size() >showcount){
                posOrderBox.setStyle("-fx-border-width: 10px;-fx-border-color:red");
            }
            HBox hBox = (HBox) posOrderBox.getChildren().get(0);
            hBox.setPrefHeight(parentsHeight/10*1);
            hBox.setPrefWidth(paneWidth);
            Label orderTypeLabel = (Label) hBox.getChildren().get(0);
            orderTypeLabel.setPrefWidth(paneWidth/3);
            orderTypeLabel.setPrefHeight(parentsHeight/10*1);
            Label tableNumLabel = (Label) hBox.getChildren().get(1);
            tableNumLabel.setPrefWidth(paneWidth/3*2);
            tableNumLabel.setPrefHeight(parentsHeight/10*1);
            HBox buttonBox = (HBox) posOrderBox.getChildren().get(1);
            buttonBox.setPrefHeight(parentsHeight/10*1);
            buttonBox.setPrefWidth(paneWidth);
            Label orderCountLabel = (Label) buttonBox.getChildren().get(0);
            orderCountLabel.setPrefWidth(paneWidth/2);
            orderCountLabel.setPrefHeight(parentsHeight/10*1);
            FlowPane buttonFlowPane = (FlowPane) buttonBox.getChildren().get(1);
            buttonFlowPane.setPrefHeight(parentsHeight/10*1);
            buttonFlowPane.setPrefWidth(paneWidth/2);
            buttonFlowPane.setHgap(10);
            Button printButton = (Button)  buttonFlowPane.getChildren().get(1);
            printButton.setPrefWidth(paneWidth/4-10);
            printButton.setPrefHeight(parentsHeight/10*1);
            orderListController.getPosOrderTab().setPrefWidth(paneWidth);
            orderListController.getItemDescCol().setPrefWidth(paneWidth/6*3);
            orderListController.getItemQtyCol().setPrefWidth(paneWidth/6);
            orderListController.getItemTimeCol().setPrefWidth(paneWidth/6*2);
            if(PrintStateEnum.HANG.getValue().equals(posOrders.get(0).getPrint_state())){
                buttonFlowPane.getChildren().remove(0);
            }
            else{
                Button hangButton = (Button) buttonFlowPane.getChildren().get(0);
                hangButton.setPrefWidth(paneWidth/4-10);
                hangButton.setPrefHeight(parentsHeight/10*1);
            }
                Platform.runLater(() ->  flowPane.getChildren().add(posOrderBox));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void topComponent(BorderPane root,Rectangle2D primaryScreenBounds,Stage primaryStage) {
        //設置頂部信息欄
        HBox topPane = new HBox();
        topPane.setPrefWidth(primaryScreenBounds.getWidth());
        topPane.setPrefHeight(primaryScreenBounds.getHeight()/10);
        topPane.setStyle("-fx-background-color: #1d1d1d;-fx-border-width: 0 0 2 0; -fx-border-color:transparent transparent derive(-fx-base, 80%) transparent;");
        topPane.setAlignment(Pos.CENTER);
        Button allButton = new Button();
        allBoxTotal.set("全部("+ (selectedFirstOrderBoxs.size() +firstOrderBoxs.size())+")");
        allButton.setStyle("-fx-font-size: 25pt; -fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill: #fcc101; -fx-alignment:center;-fx-border-width: 8 8 8 8; -fx-border-color:#fcc101 ");
        allButton.setPrefWidth(topPane.getPrefWidth()/6+100);
        allButton.setPrefHeight(topPane.getPrefHeight()-100);
        allButton.setId("allButton");
        allButton.textProperty().bind(allBoxTotal);
        allButton.setOnAction(event->{
            selectedOrderType.set(OrderType.ALLORDER.getValue());
            //boxTotal.set(selectedOrderBoxs.size() +orderBoxs.size() + "個任務需要列印，請盡快處理");
            allBoxTotal.set("全部("+ (selectedOrderBoxs.size() + orderBoxs.size())+")");
            flowPane.getChildren().remove(0,flowPane.getChildren().size());
            allButton.setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill:  #fcc101; -fx-alignment:center;-fx-border-width: 8 8 8 8;-fx-border-color: #fcc101 ");
            ObservableList<Node> nodes = topPane.getChildren();
            for(int i = 0;i<nodes.size();i++){
                if(i!=0){
                    nodes.get(i).setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color:#1d1d1d;-fx-text-fill: white; -fx-alignment:center;-fx-border-width: 8 8 8 8;-fx-border-color:#c5c6c6");
                }
            }
            selectedOrderBoxs.forEach(posOrders -> {
                setupVbox(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight(), posOrders,primaryStage);
            });
        });
        topPane.getChildren().add(allButton);
        Button firstButton = new Button();
        firstBoxTotal.set("首單("+ (selectedFirstOrderBoxs.size() +firstOrderBoxs.size())+")");
        firstButton.setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill: white; -fx-alignment: center;-fx-border-width: 8 8 8 8;");
        firstButton.setPrefWidth(topPane.getPrefWidth()/6+100);
        firstButton.setPrefHeight(topPane.getPrefHeight()-100);
        firstButton.textProperty().bind(firstBoxTotal);
        firstButton.setOnAction(event->{
            selectedOrderType.set(OrderType.FIRSTORDER.getValue());
           // boxTotal.set(selectedFirstOrderBoxs.size() +firstOrderBoxs.size() + "個任務需要列印，請盡快處理");
            firstBoxTotal.set("首單("+ (selectedFirstOrderBoxs.size() +firstOrderBoxs.size())+")");
            flowPane.getChildren().remove(0,flowPane.getChildren().size());
            firstButton.setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill: #fcc101; -fx-alignment: center;-fx-border-width: 8 8 8 8;-fx-border-color: #fcc101");
            ObservableList<Node> nodes = topPane.getChildren();
            for(int i = 0;i<nodes.size();i++){
                if(i!=1){
                    nodes.get(i).setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill:white; -fx-alignment:center;-fx-border-width: 8 8 8 8;-fx-border-color: #c5c6c6");
                }
            }
            selectedFirstOrderBoxs.forEach(posOrders -> {
                setupVbox(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight(), posOrders, primaryStage);
            });
        });
        topPane.getChildren().add(firstButton);
        Button addButton = new Button();
        addBoxTotal.set("加單("+ (selectedAddOrderBoxs.size() +addOrderBoxs.size())+")");
        addButton.setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill: white; -fx-alignment: center;-fx-border-width: 8 8 8 8;");
        addButton.setPrefWidth(topPane.getPrefWidth()/6+100);
        addButton.setPrefHeight(topPane.getPrefHeight()-100);
        addButton.textProperty().bind(addBoxTotal);
        addButton.setOnAction(event->{
            selectedOrderType.set(OrderType.ADDORDER.getValue());
           // boxTotal.set(selectedAddOrderBoxs.size() +addOrderBoxs.size() + "個任務需要列印，請盡快處理");
            addBoxTotal.set("加單("+ (selectedAddOrderBoxs.size() +addOrderBoxs.size())+")");
            flowPane.getChildren().remove(0,flowPane.getChildren().size());
            addButton.setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill: #fcc101; -fx-alignment:center;-fx-border-width: 8 8 8 8;-fx-border-color: #fcc101 ");
            ObservableList<Node> nodes = topPane.getChildren();
            for(int i = 0;i<nodes.size();i++){
                if(i!=2){
                    nodes.get(i).setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill: white; -fx-alignment:center;-fx-border-width: 8 8 8 8;-fx-border-color:#c5c6c6");
                }
            }
            selectedAddOrderBoxs.forEach(posOrders -> {
                setupVbox(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight(), posOrders,primaryStage);
            });
        });
        topPane.getChildren().add(addButton);
        Button lastButton = new Button();
        lastBoxTotal.set("尾單("+(selectedLastOrderBoxs.size() +lastOrderBoxs.size())+")");
        lastButton.setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill: white; -fx-alignment: center;-fx-border-width: 8 8 8 8;");
        lastButton.setPrefWidth(topPane.getPrefWidth()/6+100);
        lastButton.setPrefHeight(topPane.getPrefHeight()-100);
        lastButton.textProperty().bind(lastBoxTotal);
        lastButton.setOnAction(event->{
            selectedOrderType.set(OrderType.TAILORDER.getValue());
           // boxTotal.set(selectedLastOrderBoxs.size() +lastOrderBoxs.size() + "個任務需要列印，請盡快處理");
            lastBoxTotal.set("尾單("+(selectedLastOrderBoxs.size() +lastOrderBoxs.size())+")");
            selectedOrderType.setValue(OrderType.TAILORDER.getValue());
            flowPane.getChildren().remove(0,flowPane.getChildren().size());
            lastButton.setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill:  #fcc101; -fx-alignment:center;-fx-border-width: 8 8 8 8;-fx-border-color: #fcc101");
            ObservableList<Node> nodes = topPane.getChildren();
            for(int i = 0;i<nodes.size();i++){
                if(i!=3){
                    nodes.get(i).setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill: white; -fx-alignment:center;-fx-border-width: 8 8 8 8;-fx-border-color: #c5c6c6");
                }
            }
            selectedLastOrderBoxs.forEach(posOrders -> {
                setupVbox(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight(), posOrders,primaryStage);
            });
        });
        topPane.getChildren().add(lastButton);
        Button hangButton = new Button();
        hangBoxTotal.set("掛起("+(hangOrderBoxs.size())+")");
        hangButton.setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill: white; -fx-alignment: center;-fx-border-width: 8 8 8 8;");
        hangButton.setPrefWidth(topPane.getPrefWidth()/6+100);
        hangButton.setPrefHeight(topPane.getPrefHeight()-100);
        hangButton.textProperty().bind(hangBoxTotal);
        hangButton.setOnAction(event->{
             setSelectedOrderType(OrderType.HANG.getValue());
             FXMLLoader loader = new FXMLLoader();
             loader.setLocation(Main.class.getResource("controller/view/HangTableView.fxml"));
            try {

                VBox hangTableVox = loader.load();
                hangTableVox.setPrefWidth(primaryScreenBounds.getWidth()/4*3);
                hangTableVox.setPrefHeight(primaryScreenBounds.getHeight()/4*3);
                FlowPane titleFlowPane = (FlowPane) hangTableVox.getChildren().get(0);
                titleFlowPane.setPrefHeight(primaryScreenBounds.getHeight()/4*3/10);
                titleFlowPane.setPrefWidth(primaryScreenBounds.getWidth()/4*3);
                // primaryStage.initStyle(StageStyle.TRANSPARENT);
                Stage stage = new Stage();
                stage.setScene(new Scene(hangTableVox));
                stage.initOwner(primaryStage);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.TRANSPARENT);
                HangTableController hangTableController = loader.getController();
                hangTableController.setStage(stage);
                hangTableController.getTablesFlowPane().setPrefHeight(primaryScreenBounds.getHeight()/4*3/10*8);
                hangTableController.getTablesFlowPane().setPrefWidth(primaryScreenBounds.getWidth()/4*3);
                hangTableController.getTablesFlowPane().setPadding(new Insets(primaryScreenBounds.getWidth()/4*3/47,0,0,primaryScreenBounds.getWidth()/4*3/25));
                hangTableController.getTablesFlowPane().setHgap(primaryScreenBounds.getWidth()/4*3/60);
                hangTableController.getTablesFlowPane().setVgap(primaryScreenBounds.getHeight()/4*3/50);
                hangTableController.addTables(0,this);

                hangTableController.getButtomFlowPane().setPrefWidth(primaryScreenBounds.getWidth()/4*3);
                hangTableController.getButtomFlowPane().setPrefHeight(primaryScreenBounds.getHeight()/4*3/10);
                //hangTableController.getButtomFlowPane().setPadding(new Insets(0,0,0,1));
                hangTableController.getButtomFlowPane().setHgap(3);

                //關閉按鈕
                hangTableController.getCloseButton().setPrefWidth(primaryScreenBounds.getWidth()/4*3/3-5);
                hangTableController.getCloseButton().setPrefHeight(primaryScreenBounds.getHeight()/4*3/10);
                hangTableController.getCloseButton().setOnAction(event1 -> {
                      flowPane.getChildren().remove(0, flowPane.getChildren().size());
                      stage.close();
                });
                //上一頁按鈕
                hangTableController.getUpPageButton().setPrefWidth(primaryScreenBounds.getWidth()/4*3/3-5);
                hangTableController.getUpPageButton().setPrefHeight(primaryScreenBounds.getHeight()/4*3/10);
                //下一頁按鈕
                hangTableController.getDownPageButton().setPrefWidth(primaryScreenBounds.getWidth()/4*3/3-5);
                hangTableController.getDownPageButton().setPrefHeight(primaryScreenBounds.getHeight()/4*3/10);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            hangButton.setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill:  #fcc101; -fx-alignment:center;-fx-border-width: 8 8 8 8;-fx-border-color: #fcc101 ");
            ObservableList<Node> nodes = topPane.getChildren();
            for(int i = 0;i<nodes.size();i++){
                if(i!=4){
                    nodes.get(i).setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color:#1d1d1d;-fx-text-fill: white; -fx-alignment:center;-fx-border-width: 8 8 8 8;-fx-border-color:#c5c6c6");
                }
            }

          /*  selectedOrderType.set(OrderType.HANG.getValue());
            flowPane.getChildren().remove(0,flowPane.getChildren().size());
            hangButton.setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill:  #fcc101; -fx-alignment:center;-fx-border-width: 8 8 8 8;-fx-border-color: #fcc101");
            ObservableList<Node> nodes = topPane.getChildren();
            for(int i = 0;i<nodes.size();i++){
                if(i!=4){
                    nodes.get(i).setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill: white; -fx-alignment:center;-fx-border-width: 8 8 8 8;-fx-border-color: #c5c6c6");
                }
            }
            hangOrderBoxs.forEach(posOrders -> {
                setupHangVbox(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight(), posOrders,primaryStage);
            });*/
        });
        topPane.getChildren().add(hangButton);
        circle.setRadius(20.0f);
        circle.setId("state");
        circle.setFill(Color.BLACK);
        topPane.getChildren().add(circle);
        root.setTop(topPane);
    }

    public void bottomComponent(BorderPane root, Rectangle2D primaryScreenBounds,Stage primaryStage) {
        //設置底部功能按鈕欄
        Double paneWidth = primaryScreenBounds.getWidth()/4 ;
        HBox bottomPane = new HBox();
        bottomPane.setAlignment(Pos.CENTER);
        bottomPane.setPrefSize(paneWidth,primaryScreenBounds.getHeight()/10);
        //列印按鈕
        Button printButton = new Button("列印");
        printButton.setPrefSize(paneWidth, primaryScreenBounds.getHeight()/10);
        printButton.setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill: white; -fx-alignment: center;-fx-border-width: 8 8 8 8;-fx-border-color: #c5c6c6");
        printButton.setOnAction(e -> {
            printButton.setDisable(true);
            if(flowPane.getChildren() != null && flowPane.getChildren().size() > 0 ){
                VBox posOrderBox = (VBox) flowPane.getChildren().get(0);
                 ObservableList<PosOrder> posOrderData = FXCollections.observableArrayList();
                TableView<PosOrder> posOrderTab = (TableView<PosOrder>) posOrderBox.getChildren().get(2);
                HBox hBox  = (HBox) posOrderBox.getChildren().get(0);
                Label tableNumLab = (Label) hBox.getChildren().get(1);
                if(OrderType.ALLORDER.getValue().equals(selectedOrderType.getValue())){
                    posOrderData = selectedOrderBoxs.get(0);
                }
                else if(OrderType.FIRSTORDER.getValue().equals(selectedOrderType.getValue())){
                    posOrderData = selectedFirstOrderBoxs.get(0);
                }
                else if(OrderType.ADDORDER.getValue().equals(selectedOrderType.getValue())){
                    posOrderData = selectedAddOrderBoxs.get(0);
                }
                else if(OrderType.TAILORDER.getValue().equals(selectedOrderType.getValue())){
                    posOrderData = selectedLastOrderBoxs.get(0);
                }
                else if(OrderType.HANG.getValue().equals(selectedOrderType.getValue())){
                    if(hangOrderBoxs != null && hangOrderBoxs.size()>0 && AppUtils.isNotBlank(hangRefNum)){
                        for(ObservableList<PosOrder> posOrders : hangOrderBoxs) {
                            if (hangRefNum.equals(posOrders.get(0).getBill_no())) {
                                posOrderData = posOrders;
                                break;
                            }
                        }
                    }
                }
                Map<Long,List<PosOrderDto>> posOrderDtos = new HashMap<>();
                IsBilledDto isBilledDto = PosOrderDao.isBilled(posOrderData.get(0).getBill_no(), posOrderData.get(0).getType(),posOrderData.get(0).getSub_no());
                if(AppUtils.isNotBlank(isBilledDto) && AppUtils.isNotBlank(isBilledDto.getLeaveDate() )) {
                    Stage stage = new Stage();
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(Main.class.getResource("controller/view/ConfirmPrintView.fxml"));
                    try {
                        VBox vBox = loader.load();
                        vBox.setPrefWidth(primaryScreenBounds.getWidth() / 3);
                        vBox.setPrefHeight(primaryScreenBounds.getHeight() / 3);
                        AnchorPane titleAnchorPane = (AnchorPane) vBox.getChildren().get(0);
                        titleAnchorPane.setPrefWidth(primaryScreenBounds.getWidth() / 3);
                        titleAnchorPane.setPrefHeight(primaryScreenBounds.getHeight() / 3 / 3);
                        Label titleLabel = (Label) titleAnchorPane.getChildren().get(0);
                        titleLabel.setPrefHeight(primaryScreenBounds.getHeight() / 3 / 3);
                        titleLabel.setPrefWidth(primaryScreenBounds.getWidth() / 3);
                        AnchorPane contentAnchorPane = (AnchorPane) vBox.getChildren().get(1);
                        contentAnchorPane.setPrefWidth(primaryScreenBounds.getWidth() / 3);
                        contentAnchorPane.setPrefHeight(primaryScreenBounds.getHeight() / 3 / 3);
                        Label contentLabel = (Label) contentAnchorPane.getChildren().get(0);
                        contentLabel.setPrefWidth(primaryScreenBounds.getWidth() / 3);
                        contentLabel.setPrefHeight(primaryScreenBounds.getHeight() / 3 / 3);
                        FlowPane buttomFlowPane = (FlowPane) vBox.getChildren().get(2);
                        buttomFlowPane.setPrefWidth(primaryScreenBounds.getWidth() / 3);
                        buttomFlowPane.setPrefHeight(primaryScreenBounds.getHeight() / 3 / 3);
                        buttomFlowPane.setHgap(primaryScreenBounds.getWidth() / 3 / 10);
                        Button confirmButton = (Button) buttomFlowPane.getChildren().get(0);
                        confirmButton.setPrefWidth(primaryScreenBounds.getWidth() / 3 / 4);
                        ObservableList<PosOrder> finalPosOrderData1 = posOrderData;
                        confirmButton.setOnAction(event -> {
                            ObservableList<PosOrder> needPrintDatas = FXCollections.observableArrayList();
                            if (finalPosOrderData1.size() > showCount) {
                                needPrintDatas.addAll(finalPosOrderData1.subList(0, showCount));
                            } else {
                                needPrintDatas.addAll(finalPosOrderData1);
                            }
                            if (needPrintDatas != null && needPrintDatas.size() > 0) {
                                List<String> list = new LinkedList<String>();
                                needPrintDatas.forEach(posOrder -> {

//                String printIitem  = posOrder.getName1() + (AppUtils.isNotBlank(posOrder.getPrint_msg())? "("+ posOrder.getPrint_msg()+" )":"");

                                    int k = posOrder.getName1().length() % 8;
                                    int m = posOrder.getName1().length() / 8;
                                    if (m > 0) {
                                        for (int i = 0; i < m; i++) {
                                            if (i == 0) {
                                                list.add(posOrder.getName1().substring(0, 8) + "|R" + posOrder.getSeal_count() + "|R" + posOrder.getPrint_msg());
                                            } else if (i != m) {
                                                list.add(posOrder.getName1().substring(i * 8, i * 8 + 8) + "|R" + "" + "|R" + "");
                                            } else {
                                                list.add(posOrder.getName1().substring(i * 8, m * 8) + "|R" + "" + "|R" + "");
                                            }
                                        }
                                        if (k > 0) {
                                            list.add(posOrder.getName1().substring(m * 8, posOrder.getName1().length()) + "|R" + "" + "|R" + "");
                                        }

                                    } else {
                                        list.add(posOrder.getName1() + "|R" + posOrder.getSeal_count() + "|R" + posOrder.getPrint_msg());
                                    }
                                    if (AppUtils.isNotBlank(posOrder.getPrint_msg())) {
                                        list.add("訊息:" + posOrder.getPrint_msg() + "|R" + "" + "|R" + "");
                                    }
//                list.add(posOrder.getName1() + "|R" + posOrder.getSeal_count() );
//                if(AppUtils.isNotBlank(posOrder.getPrint_msg())){
//                    list.add("-"+posOrder.getPrint_msg().replace("\n",",")+ "|R");
//                }


                                    if (PosOrderDao.otheridxs.containsKey(posOrder.getOrder_idx())) {
                                        posOrderDtos.put(posOrder.getOrder_idx(), PosOrderDao.otheridxs.get(posOrder.getOrder_idx()));
                                    }
                                });

                                String print = null;
                                String printMsg = null;
                                try {
                                    String[] tableStyle = {
                                            "~0,160,35",
                                            "#BIG4",
                                            "#CENTER",
                                            "#GOHEAD1",
                                            "$TABLE_NAM| |ORDER_TYPE",
                                            "#GOHEAD1",
                                            "#SMALL",
                                            "#LEFT",
                                            "$賬單編號：|REF_NUM",
                                            "$BARCODE|/|STATION|-|STAFF| |@POSDATE",
                                            "#SMALL",
                                            "#R|2|COMPART",
                                            "#GOHEAD1",
                                            "#BIG",
                                            "*菜單名稱|R數量",
                                            "#SMALL",
                                            "#R|2|COMPART",
                                            "#BIG",
                                            "%ORDERLIST",
                                            "#SMALL",
                                            "#R|2|COMPART",
                                            "#GOHEAD5",
                                            "#CUTPAPER"
                                    };
                                    HashMap<String, String> printmessage = new HashMap<String, String>();
                                    printmessage.put("PRN_NAM", needPrintDatas.get(0).getPrint_name());
                                    printmessage.put("TABLE_NAM", needPrintDatas.get(0).getTable_no());
                                    printmessage.put("BARCODE", needPrintDatas.get(0).getBarcode());
                                    printmessage.put("STATION", needPrintDatas.get(0).getPos_id());
                                    printmessage.put("ZONE", needPrintDatas.get(0).getZone());
                                    printmessage.put("REF_NUM", needPrintDatas.get(0).getBill_no());
                                    printmessage.put("ORDER_TYPE", needPrintDatas.get(0).getOrder_type());
                                    printmessage.put("STAFF", needPrintDatas.get(0).getStaff());
                                    printmessage.put("PERSON", String.valueOf(needPrintDatas.get(0).getPerson_num()));
                                    printmessage.put("ORDER_TYPE", needPrintDatas.get(0).getOrder_type());
                                    printmessage.put("POSDATE", needPrintDatas.get(0).getRt_op_time());
                                    print = GetPrintOrReportStr.TranslateTableStyle(tableStyle, list, null, 1, printmessage);
                                    printMsg = PrintRxTxVirtaul.getInstance().PrinterCheck();
                                } catch (Exception p) {
                                    p.printStackTrace();
                                    try {
                                        showWarningView("錯誤", "打印機異常",primaryStage);
                                        printButton.setDisable(false);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                        printButton.setDisable(false);
                                    }
                                    return;
                                }
                                if (AppUtils.isNotBlank(printMsg)) {
                                    try {
                                        showWarningView("錯誤", "打印機異常",primaryStage);
                                        printButton.setDisable(false);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                        printButton.setDisable(false);
                                    }
                                    return;
                                } else {
                                    try {
                                        PrintRxTxVirtaul.getInstance().localPrint(print);
                                    } catch (Exception q) {
                                        try {
                                            showWarningView("錯誤", "打印機異常", primaryStage);
                                            printButton.setDisable(false);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                            printButton.setDisable(false);
                                        }
                                        return;
                                    }
                                    if (getSelectedOrderType().equals(OrderType.HANG.getValue())) {
                                        Integer index = getHangOrderBoxs().indexOf(finalPosOrderData1);
                                /* if (index >= 0) {
                                    for (int i = 0; i < getHangOrderBoxs().size(); i++) {
                                        if (finalPosOrderData1.get(0).getBill_no().equals(getHangOrderBoxs().get(i).get(0).getBill_no())) {
                                            getFlowPane().getChildren().remove(0);
                                        }
                                       *//* if (getSelectedHangOrderBoxs() != null && getSelectedHangOrderBoxs().size() > 0) {
                                            if (finalPosOrderData1.get(0).getBill_no().equals(getSelectedHangOrderBoxs().get(0).get(0).getBill_no())) {
                                                getSelectedHangOrderBoxs().remove(0);
                                            }
                                        }*//*
                                    }
                                    getHangOrderBoxs().remove(finalPosOrderData1);
                                    setOrderHangCount(getOrderHangCount() - 1);
                                    setHangBoxTotal("掛起" + "(" + getOrderHangCount() + ")");
                                }*/
                                        if(index>=0){
                                            // ObservableList<PosOrder> posOrders =  getHangOrderBoxs().get(index);
                                            if(finalPosOrderData1.get(0).getBill_no().equals(getHangOrderBoxs().get(index).get(0).getBill_no())){
                                               /* if(finalPosOrderData1.size()>showCount){
                                                    getHangOrderBoxs().get(index).remove(0,showCount);
                                                }*/
                                                if(finalPosOrderData1.size()<=showcount){
                                                    SqlSession sqlSession = SqlSessionUtil.getSqlSession();
                                                    OrderHangListDao.deleteOrderHangListByRefNum(sqlSession,finalPosOrderData1.get(finalPosOrderData1.size()-1).getBill_no(),finalPosOrderData1.get(finalPosOrderData1.size()-1).getHang_date());
                                    /*    main.getFlowPane().getChildren().remove(0);*/
                                                }

                                            }
                                        }
                                    }
                                    if (finalPosOrderData1.size() > showCount) {
                                        removePosOrderData(false, finalPosOrderData1, posOrderBox, posOrderTab);
                                        tableNumLab.textProperty().setValue("檯号:" + finalPosOrderData1.get(0).table_noProperty().getValue() + " ,时间:" + finalPosOrderData1.get(0).rt_op_timeProperty().getValue() + " ,人數:" + finalPosOrderData1.get(0).person_numProperty().getValue() + " , 菜品數量:" + finalPosOrderData1.size());
                                    } else {
                                        removePosOrderData(true, finalPosOrderData1, posOrderBox, posOrderTab);
                                    }
                                    if (posOrderDtos != null && posOrderDtos.size() > 0) {
                                        Iterator<Long> idxs = posOrderDtos.keySet().iterator();
                                        while (idxs.hasNext()) {
                                            PosOrderDao.otheridxs.remove(idxs.next());
                                        }
                                    }
                                }
                            }
                            printButton.setDisable(false);
                            stage.close();
                        });
                        Button cancleButton = (Button) buttomFlowPane.getChildren().get(1);
                        cancleButton.setPrefWidth(primaryScreenBounds.getWidth() / 3 / 4);
                        ObservableList<PosOrder> finalPosOrderData = posOrderData;
                        cancleButton.setOnAction(event -> {
                            this.removeLeavePosOrderData(true, finalPosOrderData, posOrderBox, posOrderTab);
                            printButton.setDisable(false);
                            stage.close();
                            return;
                        });
                        stage.setScene(new Scene(vBox));
                        stage.initOwner(primaryStage);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.TRANSPARENT);
                        stage.show();
                    } catch (Exception r) {
                        r.printStackTrace();
                        printButton.setDisable(false);
                    }
                }
                else{
                    /*if(getSelectedOrderType().equals(OrderType.HANG.getValue())){
                        Integer index =  getHangOrderBoxs().indexOf(posOrderData);
                        if(index>=0){
                            SqlSession sqlSession = SqlSessionUtil.getSqlSession();
                            try {
                                for(PosOrder posOrder : posOrderData){
                                    PosOrderDao.updatePrintStateWithoutOldPrintState(sqlSession,PrintStateEnum.PRINTED,posOrder.getBill_no(),posOrder.getOrder_idx(),posOrder.getType());
                                    posOrder.setPrint_state(PrintStateEnum.PRINTED.getValue());
                                }
                                OrderHangListDao.deleteOrderHangListByRefNum(sqlSession,posOrderData.get(0).getBill_no());
                                ObservableList<PosOrder> finalPosOrderData2 = posOrderData;
                                executor.execute((new Runnable() {
                                    public void run() {
                                        String outline = BranchDao.getOutlineByOutlet(ReadProperties.readStringByKey("outlet"));
                                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                                        params.add(new BasicNameValuePair("refNum", finalPosOrderData2.get(0).getBill_no()));
                                        params.add(new BasicNameValuePair("subRef", finalPosOrderData2.get(0).getSub_no()));
                                        params.add(new BasicNameValuePair("outline", outline));
                                        params.add(new BasicNameValuePair("outlet", ReadProperties.readStringByKey("outlet")));
                                        params.add(new BasicNameValuePair("opType", "FREE"));
                                        params.add(new BasicNameValuePair("inDate", finalPosOrderData2.get(0).getIn_date()));
                                        SendUtils.sendRequest(ReadProperties.readStringByKey("messageUrl") + "/message/cancelOrFree", params);
                                    }
                                }));
                            } catch (Exception b) {
                                b.printStackTrace();
                                sqlSession.rollback();
                                printButton.setDisable(false);
                                return;
                            }
                            for(int i = 0;i<getHangOrderBoxs().size();i++){
                                if(posOrderData.get(0).getBill_no().equals(getHangOrderBoxs().get(i).get(0).getBill_no())){
                                    getFlowPane().getChildren().remove(0);
                                }
                            }
                            getHangOrderBoxs().remove(posOrderData);
                            selectedHangOrderBoxs.remove(0);
                            setOrderHangCount(getOrderHangCount()-1);
                            setHangBoxTotal("掛起"+"(" +  getOrderHangCount() + ")");
                        }
                    }*/
                   /* String tabNum = PosOrderDao.getTableNumByOrderIdx(posOrderData.get(0).getOrder_idx());
                    if(!posOrderData.get(0).getTable_no().equals(tabNum)){
                        crateAlert("提示", "此臺已轉檯", "此臺已轉檯，不能掛起！",primaryStage);
                        removePosOrderData(true, posOrderData, posOrderBox,posOrderTab);
                        return;
                    }*/
                    ObservableList<PosOrder> needPrintDatas = FXCollections.observableArrayList();
                    if(posOrderData.size() >showCount) {
                        needPrintDatas.addAll(posOrderData.subList(0,showCount));
                    }
                    else{
                        needPrintDatas.addAll(posOrderData);
                    }
                    if(needPrintDatas != null && needPrintDatas.size() > 0) {
                        List<String> list = new LinkedList<String>();
                        needPrintDatas.forEach(posOrder -> {

//                String printIitem  = posOrder.getName1() + (AppUtils.isNotBlank(posOrder.getPrint_msg())? "("+ posOrder.getPrint_msg()+" )":"");

                            list.add(posOrder.getName1() + "|R" + posOrder.getSeal_count() );
//                list.add(posOrder.getName1() + "|R" + posOrder.getSeal_count() );
//                if(AppUtils.isNotBlank(posOrder.getPrint_msg())){
//                    list.add("-"+posOrder.getPrint_msg().replace("\n",",")+ "|R");
//                }
                            if(PosOrderDao.otheridxs.containsKey(posOrder.getOrder_idx())){
                                posOrderDtos.put(posOrder.getOrder_idx(),PosOrderDao.otheridxs.get(posOrder.getOrder_idx()));
                            }
                        });

                        String print = null;
                        String printMsg = null;
                        try {
                            String[] tableStyle  = {
                                    "~0,160,35",
                                    "#BIG4",
                                    "#CENTER",
                                    "#GOHEAD1",
                                    "$TABLE_NAM| |ORDER_TYPE",
                                    "#GOHEAD1",
                                    "#SMALL",
                                    "#LEFT",
                                    "$賬單編號：|REF_NUM",
                                    "$BARCODE|/|STATION|-|STAFF| |@POSDATE",
                                    "#SMALL",
                                    "#R|2|COMPART",
                                    "#GOHEAD1",
                                    "#BIG",
                                    "*菜單名稱|R數量",
                                    "#SMALL",
                                    "#R|2|COMPART",
                                    "#BIG",
                                    "%ORDERLIST",
                                    "#SMALL",
                                    "#R|2|COMPART",
                                    "#GOHEAD5",
                                    "#CUTPAPER"
                            };
                            HashMap<String, String> printmessage = new HashMap<String, String>();
                            printmessage.put("PRN_NAM", needPrintDatas.get(0).getPrint_name());
                            printmessage.put("TABLE_NAM", needPrintDatas.get(0).getTable_no());
                            printmessage.put("BARCODE", needPrintDatas.get(0).getBarcode());
                            printmessage.put("STATION", needPrintDatas.get(0).getPos_id());
                            printmessage.put("ZONE", needPrintDatas.get(0).getZone());
                            printmessage.put("REF_NUM", needPrintDatas.get(0).getBill_no());
                            printmessage.put("ORDER_TYPE",needPrintDatas.get(0).getOrder_type());
                            printmessage.put("STAFF", needPrintDatas.get(0).getStaff());
                            printmessage.put("PERSON", String.valueOf(needPrintDatas.get(0).getPerson_num()));
                            printmessage.put("ORDER_TYPE", needPrintDatas.get(0).getOrder_type());
                            printmessage.put("POSDATE", needPrintDatas.get(0).getRt_op_time());
                            print = GetPrintOrReportStr.TranslateTableStyle(tableStyle, list, null, 1, printmessage);
                            printMsg = PrintRxTxVirtaul.getInstance().PrinterCheck();
                        } catch (Exception s) {
                            s.printStackTrace();
                            try {
                                showWarningView("錯誤", "打印機異常",primaryStage);
                                printButton.setDisable(false);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                                printButton.setDisable(false);
                            }
                            return;
                        }
                        if (AppUtils.isNotBlank(printMsg)) {
                            try {
                                showWarningView("錯誤", "打印機異常",primaryStage);
                                printButton.setDisable(false);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                                printButton.setDisable(false);
                            }
                            return;
                        }
                        else {
                            try {
                                PrintRxTxVirtaul.getInstance().localPrint(print);
                            } catch (Exception t) {
                                try {
                                    showWarningView("錯誤", "打印機異常",primaryStage);
                                    printButton.setDisable(false);
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                    printButton.setDisable(false);
                                }
                                return;
                            }
                            if(getSelectedOrderType().equals(OrderType.HANG.getValue())){
                           /* Integer index =  main.getHangOrderBoxs().indexOf(posOrderData);
                            if(index>=0){*/
                                SqlSession sqlSession = SqlSessionUtil.getSqlSession();
                                if(posOrderData.size() <= showCount ){
                                    OrderHangListDao.deleteOrderHangListByRefNum(sqlSession,posOrderData.get(posOrderData.size()-1).getBill_no(),posOrderData.get(posOrderData.size()-1).getHang_date());
                                }

                            }
                            if (posOrderData.size() > showCount) {
                                removePosOrderData(false, posOrderData, posOrderBox,posOrderTab);
                                tableNumLab.textProperty().setValue("檯号:" + posOrderData.get(0).table_noProperty().getValue() + " ,时间:" + posOrderData.get(0).rt_op_timeProperty().getValue() + " ,人數:" + posOrderData.get(0).person_numProperty().getValue() + " , 菜品數量:" + posOrderData.size());
                            } else {
                                removePosOrderData(true, posOrderData, posOrderBox,posOrderTab);
                            }
                            if(posOrderDtos != null && posOrderDtos.size()>0){
                                Iterator<Long> idxs =  posOrderDtos.keySet().iterator();
                                while(idxs.hasNext()){
                                    PosOrderDao.otheridxs.remove(idxs.next());
                                }
                            }
                           /* if(getSelectedOrderType().equals(OrderType.HANG.getValue())){
                                Integer index =  getHangOrderBoxs().indexOf(posOrderData);
                                if(index>=0){
                                    SqlSession sqlSession = SqlSessionUtil.getSqlSession();
                                    try {
                                        for(PosOrder posOrder : posOrderData){
                                            PosOrderDao.updatePrintStateWithoutOldPrintState(sqlSession,PrintStateEnum.PRINTED,posOrder.getBill_no(),posOrder.getOrder_idx(),posOrder.getType());
                                            posOrder.setPrint_state(PrintStateEnum.PRINTED.getValue());
                                        }
                                        OrderHangListDao.deleteOrderHangListByRefNum(sqlSession,posOrderData.get(0).getBill_no());

                                    } catch (Exception b) {
                                        b.printStackTrace();
                                        sqlSession.rollback();
                                        try {
                                            showWarningView("錯誤", "打印機異常",primaryStage);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                        printButton.setDisable(false);
                                        return;
                                    }
                                    ObservableList<PosOrder> finalPosOrderData2 = posOrderData;
                                        executor.execute((new Runnable() {
                                            public void run() {
                                                String outline = BranchDao.getOutlineByOutlet(ReadProperties.readStringByKey("outlet"));
                                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                                params.add(new BasicNameValuePair("refNum", finalPosOrderData2.get(0).getBill_no()));
                                                params.add(new BasicNameValuePair("subRef", finalPosOrderData2.get(0).getSub_no()));
                                                params.add(new BasicNameValuePair("outline", outline));
                                                params.add(new BasicNameValuePair("outlet", ReadProperties.readStringByKey("outlet")));
                                                params.add(new BasicNameValuePair("opType", "FREE"));
                                                params.add(new BasicNameValuePair("inDate", finalPosOrderData2.get(0).getIn_date()));
                                                SendUtils.sendRequest(ReadProperties.readStringByKey("messageUrl") + "/message/cancelOrFree", params);
                                            }
                                        }));
                                    for(int i = 0;i<getHangOrderBoxs().size();i++){
                                        if(posOrderData.get(0).getBill_no().equals(getHangOrderBoxs().get(i).get(0).getBill_no())){
                                            getFlowPane().getChildren().remove(0);
                                        }
                                    }
                                    getHangOrderBoxs().remove(posOrderData);
                                    setOrderHangCount(getOrderHangCount()-1);
                                    setHangBoxTotal("掛起"+"(" +  getOrderHangCount() + ")");
                                }
                            }*/
                        }
                    }


                }

            }
            printButton.setDisable(false);
        });
        bottomPane.getChildren().add(printButton);
        Button autoPrint = new Button();
        autoPrint.textProperty().bind(autoPrintProp);
        autoPrint.setOnAction(a -> {
            String isAuto = autoPrintProp.getValue().indexOf("OFF") != -1 ? "ON" : "OFF";
            autoPrintProp.set("自動列印:" + isAuto);
            ReadProperties.setProperties(new SettingModel("autoPrint", isAuto, "自動列印"));
            isAutoPrint =  new SimpleStringProperty(isAuto);
        });
        autoPrint.setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill: white; -fx-alignment: center;-fx-border-width: 8 8 8 8;-fx-border-color: #c5c6c6");
        autoPrint.setPrefSize(paneWidth, primaryScreenBounds.getHeight()/10);
        bottomPane.getChildren().add(autoPrint);

        Button print = new Button("沽清");
        print.setPrefSize(paneWidth, primaryScreenBounds.getHeight()/10);
        print.setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill: white; -fx-alignment: center;-fx-border-width: 8 8 8 8;-fx-border-color: #c5c6c6");
        print.setOnAction(o -> {
            FXMLLoader loader = null;
            HBox hbox = null;
            Stage dialogStage = null;
            FoodListController controller = null;
            Scene scene = null;
            try {
                loader = new FXMLLoader();
                loader.setLocation(Main.class.getResource("controller/view/FoodListView.fxml"));
                hbox =  loader.load();
                controller = loader.getController();
                List<TopButtonDto> topButtonDtos =  TopButtonDao.getTopButtons();
                topButtons = FXCollections.observableArrayList();
                for(TopButtonDto topButtonDto : topButtonDtos){
                     topButtons.add(new TopButton(topButtonDto.getID(),topButtonDto.getNAME1(),topButtonDto.getNAME2()));
                }
                hbox.setPrefHeight(primaryScreenBounds.getHeight()*0.8);
                hbox.setPrefWidth(primaryScreenBounds.getWidth()*0.8);
                hbox.setStyle("-fx-background-color: #1d1d1d; -fx-border-width: 1px; -fx-border-color: wheat");
                /*vBox.setPrefWidth(100);
                vBox.setPrefHeight(200);*/
                dialogStage = new Stage();
                dialogStage.setResizable(false);
                dialogStage.setMinWidth(800);
                dialogStage.setMaxHeight(700);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.initStyle(StageStyle.TRANSPARENT);
                dialogStage.initOwner(primaryStage);
                scene = new Scene(hbox);
                dialogStage.setScene(scene);
                controller.setMain(this,this.topButtons,hbox,dialogStage,scene);
                dialogStage.showAndWait();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        bottomPane.getChildren().add(print);
        Button closeButton = new Button("關閉");
        closeButton.setPrefSize(paneWidth, primaryScreenBounds.getHeight()/10);
        closeButton.setStyle("-fx-font-size: 25pt;-fx-font-weight:bolder;-fx-background-color: #1d1d1d;-fx-text-fill: white; -fx-alignment: center;-fx-border-width: 8 8 8 8;-fx-border-color: #c5c6c6");
        closeButton.setOnAction(q -> {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("controller/view/ConfirmCloseView.fxml"));
            try {
                VBox vBox =  loader.load();
                vBox.setPrefWidth(primaryScreenBounds.getWidth()/3);
                vBox.setPrefHeight(primaryScreenBounds.getHeight()/3);
                AnchorPane titleAnchorPane = (AnchorPane) vBox.getChildren().get(0);
                titleAnchorPane.setPrefWidth(primaryScreenBounds.getWidth()/3);
                titleAnchorPane.setPrefHeight(primaryScreenBounds.getHeight()/3/3);
                Label titleLabel = (Label) titleAnchorPane.getChildren().get(0);
                titleLabel.setPrefHeight(primaryScreenBounds.getHeight()/3/3);
                titleLabel.setPrefWidth(primaryScreenBounds.getWidth()/3);
                AnchorPane contentAnchorPane = (AnchorPane) vBox.getChildren().get(1);
                contentAnchorPane.setPrefWidth(primaryScreenBounds.getWidth()/3);
                contentAnchorPane.setPrefHeight(primaryScreenBounds.getHeight()/3/3);
                Label contentLabel = (Label) contentAnchorPane.getChildren().get(0);
                contentLabel.setPrefWidth(primaryScreenBounds.getWidth()/3);
                contentLabel.setPrefHeight(primaryScreenBounds.getHeight()/3/3);
                FlowPane buttomFlowPane = (FlowPane) vBox.getChildren().get(2);
                buttomFlowPane.setPrefWidth(primaryScreenBounds.getWidth()/3);
                buttomFlowPane.setPrefHeight(primaryScreenBounds.getHeight()/3/3);
                buttomFlowPane.setHgap(primaryScreenBounds.getWidth()/3/10);
                Button confirmButton = (Button) buttomFlowPane.getChildren().get(0);
                confirmButton.setPrefWidth(primaryScreenBounds.getWidth()/3/4);
                confirmButton.setPrefHeight(primaryScreenBounds.getHeight()/3/6);
                confirmButton.setOnAction(event -> {
                    primaryStage.close();
                });
                Button cancleButton = (Button) buttomFlowPane.getChildren().get(1);
                cancleButton.setPrefWidth(primaryScreenBounds.getWidth()/3/4);
                cancleButton.setPrefHeight(primaryScreenBounds.getHeight()/3/6);
                cancleButton.setOnAction(event -> {
                    stage.close();
                });
                stage.setScene(new Scene(vBox));
                stage.initOwner(primaryStage);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.show();
            } catch (Exception i) {
                i.printStackTrace();
            }
           /* primaryStage.close();*/
        });
        bottomPane.getChildren().add(closeButton);
        root.setBottom(bottomPane);
    }



    public  void removePosOrderData(boolean flag,ObservableList<PosOrder> posOrderData, VBox posOrderBox,TableView<PosOrder> posOrderTab) {
        SqlSession sqlSession = SqlSessionUtil.getSqlSession();
        try {
            //orderBoxs.remove(posOrderData);
            //如果true整个posOrderBox去掉

            if(flag){
                if(selectedOrderType.get().equals(OrderType.HANG.getValue())){
                                if(flowPane.getChildren()!= null && flowPane.getChildren().size()>0){
                                    for(int i = 0;i<flowPane.getChildren().size();i++){
                                        VBox vBox = (VBox) flowPane.getChildren().get(i);
                                        TableView tableView = (TableView) vBox.getChildren().get(2);
                                        ObservableList<PosOrder> posOrder = (ObservableList<PosOrder>) tableView.getItems();
                                        if(posOrderData == posOrder) {
                                          flowPane.getChildren().remove(i);
                                        }
                                    }
                                }
                        hangOrderBoxs.remove(posOrderData);
                        orderHangCount.set(orderHangCount.getValue()-1);
                        setHangBoxTotal("掛起"+"(" +  getOrderHangCount() + ")");
                       /* if(getOrderType().equals(OrderType.TAILORDER.getValue())) {
                            boxTotal.set(selectedLastOrderBoxs.size() + lastOrderBoxs.size() + "個任務需要列印，請盡快處理");
                        }*/
                }
                else{
                    int i = selectedOrderBoxs.indexOf(posOrderData);
                    int index = orderBoxs.indexOf(posOrderData);

                    if(i>=0){
                        selectedOrderBoxs.remove(i);
                    }
                    if(index >=0){
                        orderBoxs.remove(index);
                    }
                    if(i>=0 || index>=0){
                        flowPane.getChildren().remove(posOrderBox);
                    }
                /*if(getOrderType().equals(OrderType.ALLORDER.getValue())){
                    boxTotal.set(selectedOrderBoxs.size() + orderBoxs.size() + "個任務需要列印，請盡快處理");
                }*/

                    if(posOrderData.get(0).getOrder_type().equals(OrderType.FIRSTORDER.getValue())){
                        int j = selectedFirstOrderBoxs.indexOf(posOrderData);
                        if(j>=0){
                            selectedFirstOrderBoxs.remove(j);
                        /*if(getOrderType().equals(OrderType.FIRSTORDER.getValue())){
                            boxTotal.set(selectedFirstOrderBoxs.size() + firstOrderBoxs.size() + "個任務需要列印，請盡快處理");
                        }*/
                        }
                    }
                    if(posOrderData.get(0).getOrder_type().equals(OrderType.ADDORDER.getValue())){
                        int j = selectedAddOrderBoxs.indexOf(posOrderData);
                        if(j>=0){
                            selectedAddOrderBoxs.remove(j);
                       /* if(getOrderType().equals(OrderType.ADDORDER.getValue())) {
                            boxTotal.set(selectedAddOrderBoxs.size() + addOrderBoxs.size() + "個任務需要列印，請盡快處理");
                        }*/
                        }
                    }
                    if(posOrderData.get(0).getOrder_type().equals(OrderType.TAILORDER.getValue())){
                        int j = selectedLastOrderBoxs.indexOf(posOrderData);
                        if(j>=0){
                            selectedLastOrderBoxs.remove(j);
                       /* if(getOrderType().equals(OrderType.TAILORDER.getValue())) {
                            boxTotal.set(selectedLastOrderBoxs.size() + lastOrderBoxs.size() + "個任務需要列印，請盡快處理");
                        }*/
                        }
                    }
                    CommonPrintTask.insertPosOrdersToVBox(this);
                    CommonPrintTask.insertPosOrdersToCorrespondingBox(this);
                }
                allBoxTotal.set("全部("+ (selectedOrderBoxs.size() + orderBoxs.size())+")");
                firstBoxTotal.set("首單("+ (selectedFirstOrderBoxs.size() +firstOrderBoxs.size())+")");
                addBoxTotal.set("加單("+ (selectedAddOrderBoxs.size() +addOrderBoxs.size())+")");
                lastBoxTotal.set("尾單("+(selectedLastOrderBoxs.size() +lastOrderBoxs.size())+")");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
                Date nowDate = new Date();
                for(PosOrder posOrder :posOrderData ){
                    PrintStateEnum oldPrintState = PrintStateEnum.getPrintStateEnumByValue(posOrder.getPrint_state());
                    if(PosOrderDao.otheridxs.containsKey(posOrder.getOrder_idx())){
                        List<PosOrderDto> posOrderDtoList = PosOrderDao.otheridxs.get(posOrder.getOrder_idx());
                          for(PosOrderDto posOrderDto : posOrderDtoList){
                            PosOrderDao.updatePrintStateWithoutOldPrintState(sqlSession,PrintStateEnum.PRINTED,posOrderDto.getBill_no(), posOrderDto.getOrder_idx(), posOrderDto.getType());
                              RtLogDao.insertLog(posOrderDto.getBarcode(),simpleDateFormat.format(nowDate),simpleTimeFormat.format(nowDate),"9999999", LogTypeEnum.KVSPRINT.getValue(),"N",posOrderDto.getOrder_idx().toString(),posOrderDto.getBill_no(),posOrderDto.getTable_no(),posOrderDto.getGoodsno());
                          }
                    }
                    RtLogDao.insertLog(posOrder.getBarcode(),simpleDateFormat.format(nowDate),simpleTimeFormat.format(nowDate),"9999999", LogTypeEnum.KVSPRINT.getValue(),"N",String.valueOf(posOrder.getOrder_idx()),posOrder.getBill_no(),posOrder.getTable_no(),posOrder.getGoodsno());
                    PosOrderDao.updataPrintState(sqlSession,PrintStateEnum.PRINTED, oldPrintState, posOrder.getBill_no(),posOrder.getOrder_idx(), posOrder.getType());
                }
                executor.execute((new Runnable() {
                    public void run() {
                        String outline = BranchDao.getOutlineByOutlet(ReadProperties.readStringByKey("outlet"));
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("refNum", posOrderData.get(0).getBill_no()));
                        params.add(new BasicNameValuePair("subRef", posOrderData.get(0).getSub_no()));
                        params.add(new BasicNameValuePair("outline", outline));
                        params.add(new BasicNameValuePair("outlet", ReadProperties.readStringByKey("outlet")));
                        params.add(new BasicNameValuePair("opType", "FREE"));
                        params.add(new BasicNameValuePair("inDate", posOrderData.get(0).getIn_date()));
                        SendUtils.sendRequest(ReadProperties.readStringByKey("messageUrl") + "/message/cancelOrFree", params);
                    }
                }));
            }
            else{
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
                Date nowDate = new Date();
                for(int i = 0;i< posOrderData.size();i++){
                    if(i<showcount){
                        PrintStateEnum oldPrintState = PrintStateEnum.getPrintStateEnumByValue(posOrderData.get(i).getPrint_state());
                        if(PosOrderDao.otheridxs.containsKey(posOrderData.get(i).getOrder_idx())){
                            List<PosOrderDto> posOrderDtoList = PosOrderDao.otheridxs.get(posOrderData.get(i).getOrder_idx());
                            posOrderDtoList.forEach(posOrderDto -> {
                                PosOrderDao.updatePrintStateWithoutOldPrintState(sqlSession,PrintStateEnum.PRINTED,  posOrderDto.getBill_no(), posOrderDto.getOrder_idx(), posOrderDto.getType());
                                RtLogDao.insertLog(posOrderDto.getBarcode(),simpleDateFormat.format(nowDate),simpleTimeFormat.format(nowDate),"9999999", LogTypeEnum.KVSPRINT.getValue(),"N",posOrderDto.getOrder_idx().toString(),posOrderDto.getBill_no(),posOrderDto.getTable_no(),posOrderDto.getGoodsno());
                            });
                        }
                        RtLogDao.insertLog(posOrderData.get(i).getBarcode(),simpleDateFormat.format(nowDate),simpleTimeFormat.format(nowDate),"9999999", LogTypeEnum.KVSPRINT.getValue(),"N",String.valueOf(posOrderData.get(i).getOrder_idx()),posOrderData.get(i).getBill_no(),posOrderData.get(i).getTable_no(),posOrderData.get(i).getGoodsno());
                        PosOrderDao.updataPrintState(sqlSession,PrintStateEnum.PRINTED, oldPrintState, posOrderData.get(i).getBill_no(),posOrderData.get(i).getOrder_idx(), posOrderData.get(i).getType());
                    }
                }
                executor.execute((new Runnable() {
                    public void run() {
                        String outline = BranchDao.getOutlineByOutlet(ReadProperties.readStringByKey("outlet"));
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("refNum", posOrderData.get(0).getBill_no()));
                        params.add(new BasicNameValuePair("subRef", posOrderData.get(0).getSub_no()));
                        params.add(new BasicNameValuePair("outline", outline));
                        params.add(new BasicNameValuePair("outlet", ReadProperties.readStringByKey("outlet")));
                        params.add(new BasicNameValuePair("opType", "FREE"));
                        params.add(new BasicNameValuePair("inDate", posOrderData.get(0).getIn_date()));
                        SendUtils.sendRequest(ReadProperties.readStringByKey("messageUrl") + "/message/cancelOrFree", params);
                    }
                }));
                if(selectedOrderType.getValue().equals(OrderType.HANG.getValue())){
                    //int j = hangOrderBoxs.indexOf(posOrderData);
                   // if(j>=0){
                        //selectedLastOrderBoxs.remove(j,showcount);
                       // ObservableList<PosOrder> orders = hangOrderBoxs.get(j);
                        posOrderData.remove(0,showcount);
                        if(AppUtils.isNotBlank(posOrderData) && posOrderData.size()> showcount){
                            posOrderBox.setStyle("-fx-border-color: red;-fx-border-width: 10px ");
                        }
                        else if(AppUtils.isNotBlank(posOrderData) &&  posOrderData.size() <= showcount){
                            posOrderBox.setStyle("-fx-border-width: 10px;-fx-border-color: #c5c6c6  ");
                        }
                        posOrderTab.setItems(posOrderData);
                }
                else{
                    int i = selectedOrderBoxs.indexOf(posOrderData);
                    if(i>=0){
                        posOrderData.remove(0,showcount);
                        ObservableList<PosOrder> orders = selectedOrderBoxs.get(i);
                        orders = posOrderData;
                        if(AppUtils.isNotBlank(posOrderData) && posOrderData.size()> showcount){
                            posOrderBox.setStyle("-fx-border-color: red;-fx-border-width: 10px ");
                        }
                        else if(AppUtils.isNotBlank(posOrderData) &&  posOrderData.size() <= showcount){
                            posOrderBox.setStyle("-fx-border-width: 10px; -fx-border-color: #c5c6c6 ");
                        }
                        posOrderTab.setItems(orders);
                    }
                    if(posOrderData.get(0).getOrder_type().equals(OrderType.FIRSTORDER.getValue())){
                        int j = selectedFirstOrderBoxs.indexOf(posOrderData);
                        if(j>=0){
                            ObservableList<PosOrder> orders = selectedFirstOrderBoxs.get(j);
                            if(i>=0){
                                orders = posOrderData;
                            }
                            else{
                              orders.remove(0,showcount);
                            }
                            if(AppUtils.isNotBlank(posOrderData) && posOrderData.size()> showcount){
                                posOrderBox.setStyle("-fx-border-color: red;-fx-border-width: 10px ");
                            }
                            else if(AppUtils.isNotBlank(posOrderData) &&  posOrderData.size() <= showcount){
                                posOrderBox.setStyle("-fx-border-width: 10px;-fx-border-color: #c5c6c6  ");
                            }
                            posOrderTab.setItems(orders);
                        }
                    }
                    if(posOrderData.get(0).getOrder_type().equals(OrderType.ADDORDER.getValue())){
                        int j = selectedAddOrderBoxs.indexOf(posOrderData);
                        if(j>=0){
                            //selectedAddOrderBoxs.remove(j,showcount);
                            ObservableList<PosOrder> orders = selectedAddOrderBoxs.get(j);
                            if(i>=0){
                                orders = posOrderData;
                            }
                            else{
                                orders.remove(0,showcount);
                            }
                            if(AppUtils.isNotBlank(posOrderData) && posOrderData.size()> showcount){
                                posOrderBox.setStyle("-fx-border-color: red;-fx-border-width: 10px ");
                            }
                            else if(AppUtils.isNotBlank(posOrderData) &&  posOrderData.size() <= showcount){
                                posOrderBox.setStyle("-fx-border-width: 10px;-fx-border-color: #c5c6c6 ");
                            }
                            posOrderTab.setItems(orders);
                        }
                    }
                    if(posOrderData.get(0).getOrder_type().equals(OrderType.TAILORDER.getValue())){
                        int j = selectedLastOrderBoxs.indexOf(posOrderData);
                        if(j>=0){
                            //selectedLastOrderBoxs.remove(j,showcount);
                            ObservableList<PosOrder> orders = selectedLastOrderBoxs.get(j);
                            if(i>=0){
                                orders = posOrderData;
                            }
                            else{
                                orders.remove(0,showcount);
                            }
                            if(AppUtils.isNotBlank(posOrderData) && posOrderData.size()> showcount){
                                posOrderBox.setStyle("-fx-border-color: red;-fx-border-width: 10px ");
                            }
                            else if(AppUtils.isNotBlank(posOrderData) &&  posOrderData.size() <= showcount){
                                posOrderBox.setStyle("-fx-border-width: 10px;-fx-border-color: #c5c6c6  ");
                            }
                            posOrderTab.setItems(orders);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        }
    }

    public  void removeLeavePosOrderData(boolean flag,ObservableList<PosOrder> posOrderData, VBox posOrderBox,TableView<PosOrder> posOrderTab) {
        SqlSession sqlSession = SqlSessionUtil.getSqlSession();
        try {
            //orderBoxs.remove(posOrderData);
            //如果true整个posOrderBox去掉

            if(flag){
                int i = selectedOrderBoxs.indexOf(posOrderData);
                int index = orderBoxs.indexOf(posOrderData);

                if(i>=0){
                    selectedOrderBoxs.remove(i);
                }
                if(index >=0){
                    orderBoxs.remove(index);
                }
                if(i>=0 || index>=0){
                    flowPane.getChildren().remove(posOrderBox);
                }
                /*if(getOrderType().equals(OrderType.ALLORDER.getValue())){
                    boxTotal.set(selectedOrderBoxs.size() + orderBoxs.size() + "個任務需要列印，請盡快處理");
                }*/

                if(posOrderData.get(0).getOrder_type().equals(OrderType.FIRSTORDER.getValue())){
                    int j = selectedFirstOrderBoxs.indexOf(posOrderData);
                    if(j>=0){
                        selectedFirstOrderBoxs.remove(j);
                        /*if(getOrderType().equals(OrderType.FIRSTORDER.getValue())){
                            boxTotal.set(selectedFirstOrderBoxs.size() + firstOrderBoxs.size() + "個任務需要列印，請盡快處理");
                        }*/
                    }
                }
                if(posOrderData.get(0).getOrder_type().equals(OrderType.ADDORDER.getValue())){
                    int j = selectedAddOrderBoxs.indexOf(posOrderData);
                    if(j>=0){
                        selectedAddOrderBoxs.remove(j);
                       /* if(getOrderType().equals(OrderType.ADDORDER.getValue())) {
                            boxTotal.set(selectedAddOrderBoxs.size() + addOrderBoxs.size() + "個任務需要列印，請盡快處理");
                        }*/
                    }
                }
                if(posOrderData.get(0).getOrder_type().equals(OrderType.TAILORDER.getValue())){
                    int j = selectedLastOrderBoxs.indexOf(posOrderData);
                    if(j>=0){
                        selectedLastOrderBoxs.remove(j);
                       /* if(getOrderType().equals(OrderType.TAILORDER.getValue())) {
                            boxTotal.set(selectedLastOrderBoxs.size() + lastOrderBoxs.size() + "個任務需要列印，請盡快處理");
                        }*/
                    }
                }
                allBoxTotal.set("全部("+ (selectedOrderBoxs.size() + orderBoxs.size())+")");
                firstBoxTotal.set("首單("+ (selectedFirstOrderBoxs.size() +firstOrderBoxs.size())+")");
                addBoxTotal.set("加單("+ (selectedAddOrderBoxs.size() +addOrderBoxs.size())+")");
                lastBoxTotal.set("尾單("+(selectedLastOrderBoxs.size() +lastOrderBoxs.size())+")");

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
                Date nowDate = new Date();
                for(PosOrder posOrder :posOrderData ){
                    PrintStateEnum oldPrintState = PrintStateEnum.getPrintStateEnumByValue(posOrder.getPrint_state());
                    if(PosOrderDao.otheridxs.containsKey(posOrder.getOrder_idx())){
                        List<PosOrderDto> posOrderDtoList = PosOrderDao.otheridxs.get(posOrder.getOrder_idx());
                        for(PosOrderDto posOrderDto : posOrderDtoList){
                            PosOrderDao.updatePrintStateWithoutOldPrintState(sqlSession,PrintStateEnum.LEAVE,posOrderDto.getBill_no(), posOrderDto.getOrder_idx(), posOrderDto.getType());
                            //RtLogDao.insertLog(posOrderDto.getBarcode(),simpleDateFormat.format(nowDate),simpleTimeFormat.format(nowDate),"9999999", LogTypeEnum.KVSPRINT.getValue(),"N",posOrderDto.getOrder_idx().toString(),posOrderDto.getBill_no(),posOrderDto.getTable_no(),posOrderDto.getGoodsno());
                        }
                    }
                   // RtLogDao.insertLog(posOrder.getBarcode(),simpleDateFormat.format(nowDate),simpleTimeFormat.format(nowDate),"9999999", LogTypeEnum.KVSPRINT.getValue(),"N",String.valueOf(posOrder.getOrder_idx()),posOrder.getBill_no(),posOrder.getTable_no(),posOrder.getGoodsno());
                    PosOrderDao.updataPrintState(sqlSession,PrintStateEnum.LEAVE, oldPrintState, posOrder.getBill_no(),posOrder.getOrder_idx(), posOrder.getType());
                }
                    if(index >= 0 || i>=0){
                        executor.execute((new Runnable() {
                            public void run() {
                                   /* //取出队列中元素
                                    queue.take().run();*/
                                try {
                                    String outline = BranchDao.getOutlineByOutlet(ReadProperties.readStringByKey("outlet"));
                                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                                    params.add(new BasicNameValuePair("refNum", posOrderData.get(0).getBill_no()));
                                    params.add(new BasicNameValuePair("subRef",posOrderData.get(0).getSub_no()));
                                    params.add(new BasicNameValuePair("outline",outline));
                                    params.add(new BasicNameValuePair("outlet",ReadProperties.readStringByKey("outlet")));
                                    params.add(new BasicNameValuePair("opType","FREE"));
                                    params.add(new BasicNameValuePair("inDate",posOrderData.get(0).getIn_date()));
                                    SendUtils.sendRequest(ReadProperties.readStringByKey("messageUrl")+ "/message/cancelOrFree", params);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return;
                                }
                            }
                        }));
                    }
                CommonPrintTask.insertPosOrdersToVBox(this);
                CommonPrintTask.insertPosOrdersToCorrespondingBox(this);
            }
            else{
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
                Date nowDate = new Date();
                for(int i = 0;i< posOrderData.size();i++){
                    if(i<=showcount){
                        PrintStateEnum oldPrintState = PrintStateEnum.getPrintStateEnumByValue(posOrderData.get(i).getPrint_state());
                        if(PosOrderDao.otheridxs.containsKey(posOrderData.get(i).getOrder_idx())){
                            List<PosOrderDto> posOrderDtoList = PosOrderDao.otheridxs.get(posOrderData.get(i).getOrder_idx());
                            posOrderDtoList.forEach(posOrderDto -> {
                                PosOrderDao.updatePrintStateWithoutOldPrintState(sqlSession,PrintStateEnum.PRINTED,  posOrderDto.getBill_no(), posOrderDto.getOrder_idx(), posOrderDto.getType());
                                RtLogDao.insertLog(posOrderDto.getBarcode(),simpleDateFormat.format(nowDate),simpleTimeFormat.format(nowDate),"9999999", LogTypeEnum.KVSPRINT.getValue(),"N",posOrderDto.getOrder_idx().toString(),posOrderDto.getBill_no(),posOrderDto.getTable_no(),posOrderDto.getGoodsno());
                            });
                        }
                        RtLogDao.insertLog(posOrderData.get(i).getBarcode(),simpleDateFormat.format(nowDate),simpleTimeFormat.format(nowDate),"9999999", LogTypeEnum.KVSPRINT.getValue(),"N",String.valueOf(posOrderData.get(i).getOrder_idx()),posOrderData.get(i).getBill_no(),posOrderData.get(i).getTable_no(),posOrderData.get(i).getGoodsno());
                        PosOrderDao.updataPrintState(sqlSession,PrintStateEnum.PRINTED, oldPrintState, posOrderData.get(i).getBill_no(),posOrderData.get(i).getOrder_idx(), posOrderData.get(i).getType());
                    }
                }
                executor.execute((new Runnable() {
                public void run() {
                    String outline = BranchDao.getOutlineByOutlet(ReadProperties.readStringByKey("outlet"));
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("refNum", posOrderData.get(0).getBill_no()));
                    params.add(new BasicNameValuePair("subRef", posOrderData.get(0).getSub_no()));
                    params.add(new BasicNameValuePair("outline", outline));
                    params.add(new BasicNameValuePair("outlet", ReadProperties.readStringByKey("outlet")));
                    params.add(new BasicNameValuePair("opType", "FREE"));
                    params.add(new BasicNameValuePair("inDate", posOrderData.get(0).getIn_date()));
                    SendUtils.sendRequest(ReadProperties.readStringByKey("messageUrl") + "/message/cancelOrFree", params);
                }
                }));
                int i = selectedOrderBoxs.indexOf(posOrderData);
                if(i>=0){
                    posOrderData.remove(0,showcount);
                    ObservableList<PosOrder> orders = selectedOrderBoxs.get(i);
                    orders = posOrderData;
                    if(AppUtils.isNotBlank(posOrderData) && posOrderData.size()> showcount){
                        posOrderBox.setStyle("-fx-border-color: red;-fx-border-width: 10px ");
                    }
                    else if(AppUtils.isNotBlank(posOrderData) &&  posOrderData.size() <= showcount){
                        posOrderBox.setStyle("-fx-border-width: 10px; -fx-border-color: #c5c6c6 ");
                    }
                    posOrderTab.setItems(orders);
                }

                if(posOrderData.get(0).getOrder_type().equals(OrderType.FIRSTORDER.getValue())){
                    int j = firstOrderBoxs.indexOf(posOrderData);
                    if(j>=0){
                        // selectedFirstOrderBoxs.remove(0,showcount);
                        ObservableList<PosOrder> orders = selectedFirstOrderBoxs.get(j);
                        orders = posOrderData;
                        if(AppUtils.isNotBlank(posOrderData) && posOrderData.size()> showcount){
                            posOrderBox.setStyle("-fx-border-color: red;-fx-border-width: 10px ");
                        }
                        else if(AppUtils.isNotBlank(posOrderData) &&  posOrderData.size() <= showcount){
                            posOrderBox.setStyle("-fx-border-width: 10px;-fx-border-color: #c5c6c6  ");
                        }
                        posOrderTab.setItems(orders);
                    }
                }
                if(posOrderData.get(0).getOrder_type().equals(OrderType.ADDORDER.getValue())){
                    int j = selectedAddOrderBoxs.indexOf(posOrderData);
                    if(j>=0){
                        //selectedAddOrderBoxs.remove(j,showcount);
                        ObservableList<PosOrder> orders = selectedAddOrderBoxs.get(j);
                        orders = posOrderData;
                        if(AppUtils.isNotBlank(posOrderData) && posOrderData.size()> showcount){
                            posOrderBox.setStyle("-fx-border-color: red;-fx-border-width: 10px ");
                        }
                        else if(AppUtils.isNotBlank(posOrderData) &&  posOrderData.size() <= showcount){
                            posOrderBox.setStyle("-fx-border-width: 10px;-fx-border-color: #c5c6c6 ");
                        }
                        posOrderTab.setItems(orders);
                    }
                }
                if(posOrderData.get(0).getOrder_type().equals(OrderType.TAILORDER.getValue())){
                    int j = selectedLastOrderBoxs.indexOf(posOrderData);
                    if(j>=0){
                        //selectedLastOrderBoxs.remove(j,showcount);
                        ObservableList<PosOrder> orders = selectedLastOrderBoxs.get(j);
                        orders = posOrderData;
                        if(AppUtils.isNotBlank(posOrderData) && posOrderData.size()> showcount){
                            posOrderBox.setStyle("-fx-border-color: red;-fx-border-width: 10px ");
                        }
                        else if(AppUtils.isNotBlank(posOrderData) &&  posOrderData.size() <= showcount){
                            posOrderBox.setStyle("-fx-border-width: 10px;-fx-border-color: #c5c6c6  ");
                        }
                        posOrderTab.setItems(orders);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        }
    }









    private void getPrintDataTask() {
        Integer time = ReadProperties.readIntegerByKey("taskTime");
        Integer pingTime = ReadProperties.readIntegerByKey("pingTime");
        Date now = new Date();
        Date afterDate = new Date(now .getTime() + 3000);
        timerPrintData.schedule(new FindUnPrintListTask(this), time , time );
        timerPrintData.schedule(new PingTask(this), pingTime , pingTime );
    }

    /*public void crateAlert(String title, String header, String content, Stage primaryStage){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }*/

    public void showWarningView(String title,  String content, Stage primaryStage) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("controller/view/WarningView.fxml"));
        Rectangle2D primaryScreenBounds = getPrimaryScreenBounds();
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



    public static void main(String[] args) {
        launch(args);
    }

    public ObservableList<ObservableList<PosOrder>> getPosOrderData() {
        return orderBoxs;
    }

    public ObservableList<ObservableList<PosOrder>> getFirstPosOrderData() {
        return firstOrderBoxs;
    }

    public ObservableList<ObservableList<PosOrder>> getAddPosOrderData() {
        return addOrderBoxs;
    }

    public ObservableList<ObservableList<PosOrder>> getLastPosOrderData() {
        return lastOrderBoxs;
    }

    public RulerDto getRulerDto(){
        return this.rulerDto;
    }

    public ObservableList<ObservableList<PosOrder>> getSelectedOrderBoxs(){
        return selectedOrderBoxs;
    }

    public ObservableList<PosOrder> getOrdersByType(String  type){
          for(ObservableList<PosOrder> p :orderBoxs){
             if(type.equals(p.get(0).getOrder_type())){
                 return p;
             }
          }
        return null;
    }
    public ObservableList<PosOrder> getFirstOrdersByType(String  type){
        for(ObservableList<PosOrder> p :firstOrderBoxs){
            if(type.equals(p.get(0).getOrder_type())){
                return p;
            }
        }
        return null;
    }
    public ObservableList<PosOrder> getAddOrdersByType(String  type){
        for(ObservableList<PosOrder> p :addOrderBoxs){
            if(type.equals(p.get(0).getOrder_type())){
                return p;
            }
        }
        return null;
    }
    public ObservableList<PosOrder> getLastOrdersByType(String  type){
        for(ObservableList<PosOrder> p :lastOrderBoxs){
            if(type.equals(p.get(0).getOrder_type())){
                return p;
            }
        }
        return null;
    }





    public void reSetRulerDto(){
       this.rulerDto =  new RulerDto();
    }

    public Integer getShowcount(){
        return showcount;
    }

    public StringProperty getBoxTotal(){
        return boxTotal;
    }

    public String getAutoPrint(){
          return isAutoPrint.getValue();
    }


    public ObservableList<ObservableList<PosOrder>> getSelectedFirstOrderBoxs(){
        return  selectedFirstOrderBoxs;
    }

    public ObservableList<ObservableList<PosOrder>> getSelectedAddOrderBoxs(){
        return  selectedAddOrderBoxs;
    }
    public ObservableList<ObservableList<PosOrder>> getSelectedLastOrderBoxsOrderBoxs(){
        return  selectedLastOrderBoxs;
    }


    public Circle getCircle() {
        return circle;
    }

    public ObservableList<ObservableList<PosOrder>> getOrderBoxs() {
        return orderBoxs;
    }

    public void setOrderBoxs(ObservableList<ObservableList<PosOrder>> orderBoxs) {
        this.orderBoxs = orderBoxs;
    }


    public FlowPane getFlowPane() {
        return flowPane;
    }

    public void setFlowPane(FlowPane flowPane) {
        this.flowPane = flowPane;
    }


    public String getSelectedOrderType() {
        return selectedOrderType.getValue();
    }

    public StringProperty selectedOrderTypeProperty() {
        return selectedOrderType;
    }

    public void setSelectedOrderType(String selectedOrderType) {
        this.selectedOrderType.set(selectedOrderType);
    }

    public ObservableList<ObservableList<PosOrder>> getHangOrderBoxs() {
        return hangOrderBoxs;
    }

    public void setHangOrderBoxs(ObservableList<ObservableList<PosOrder>> hangOrderBoxs) {
        this.hangOrderBoxs = hangOrderBoxs;
    }

    public String getHangBoxTotal() {
        return hangBoxTotal.get();
    }

    public StringProperty hangBoxTotalProperty() {
        return hangBoxTotal;
    }

    public void setHangBoxTotal(String hangBoxTotal) {
        Platform.runLater(() ->  this.hangBoxTotal.set(hangBoxTotal));
    }

    public int getOrderHangCount() {
        return orderHangCount.get();
    }

    public IntegerProperty orderHangCountProperty() {
        return orderHangCount;
    }

    public void setOrderHangCount(int orderHangCount) {
        this.orderHangCount.set(orderHangCount);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    public Rectangle2D getPrimaryScreenBounds() {
        return primaryScreenBounds;
    }

    public void setPrimaryScreenBounds(Rectangle2D primaryScreenBounds) {
        this.primaryScreenBounds = primaryScreenBounds;
    }

    public String getHangRefNum() {
        return hangRefNum;
    }

    public void setHangRefNum(String hangRefNum) {
        this.hangRefNum = hangRefNum;
    }
}
