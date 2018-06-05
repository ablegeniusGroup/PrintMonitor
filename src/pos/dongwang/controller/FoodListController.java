package pos.dongwang.controller;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.SqlSession;
import pos.dongwang.Main;
import pos.dongwang.dao.TGoodsDao;
import pos.dongwang.dto.TGoodsDto;
import pos.dongwang.mapper.BranchMapper;
import pos.dongwang.mapper.TGoodsDtoMapper;
import pos.dongwang.model.TGoods;
import pos.dongwang.model.TopButton;
import pos.dongwang.mybatis.MyBatisSqlSessionFactory;
import pos.dongwang.util.SqlSessionUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


public class FoodListController extends TableRow<TGoods> {

    @FXML
    private TableView<TGoods> TgoodsTab;

    @FXML
    private TableColumn<TGoods, String> itemCodeCol;

    @FXML
    private TableColumn<TGoods, String> itemNameCol;


    @FXML
    private FlowPane flowPane;

    @FXML
    private FlowPane topButtonScrollPane;

    @FXML
    private VBox foodButtonScrollPane;

    @FXML
    private VBox topButtonsVbox;

    @FXML
    private Button closeButton;

    @FXML
    private HBox pageButtonHbox;

    @FXML
    private Button previousPageButton;

    @FXML
    private Button nextPageButton;

    @FXML
    private VBox  keyPageButtonVbox;

    @FXML
    private Button keyPreviousPageButton;

    @FXML
    private Button keyNextPageButton;




    private Main main;

    private ObservableList<TGoods> tGoodsData = FXCollections.observableArrayList();

    private ObservableList<TGoods> outOfStockTgoodsData = FXCollections.observableArrayList();

    private ObservableList<TopButton> topButtonDatas = FXCollections.observableArrayList();

    private ObservableList<TopButton> selectedTopButtonDatas = FXCollections.observableArrayList();

    private ObservableList<Button> foodButtons = FXCollections.observableArrayList();

    private VBox topButtonVbox = new VBox();

    List<TGoodsDto>  tGoodsDtos = FXCollections.observableArrayList();


    List<TGoodsDto> selectedTGoodsDtos = FXCollections.observableArrayList();

    private IntegerProperty page =  new SimpleIntegerProperty(1);

    private IntegerProperty pageSize = new SimpleIntegerProperty(12);

    private HBox foodListBox;


    private IntegerProperty keyPage =  new SimpleIntegerProperty(1);

    private IntegerProperty keyPageSize = new SimpleIntegerProperty(4);






    /**
     * 初始化方法
     */
    @FXML
    private void initialize() {
        itemCodeCol.setCellValueFactory(cellData -> cellData.getValue().goodsNoProperty());
        itemNameCol.setCellValueFactory(cellData -> cellData.getValue().goodName1Property());
    }

    public void setMain(Main main,ObservableList<TopButton> topButtonData,HBox hbox,Stage stage,Scene hboxScene) {
        this.main = main;
        topButtonDatas = topButtonData;
        pageButtonHbox.setVisible(false);
        List<TGoodsDto> tGoodsDtoList = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession().getMapper(TGoodsDtoMapper.class).getOutOfStockTGoodsDto();
        for(TGoodsDto tGoodsDto : tGoodsDtoList){
            outOfStockTgoodsData.add(new TGoods(tGoodsDto.getGoodsNo(),tGoodsDto.getGoodName1()));
        }
        TgoodsTab.setItems(this.outOfStockTgoodsData);
        flowPane.setStyle("-fx-background-color: #1d1d1d");
        foodButtonScrollPane.setStyle("-fx-background-color: #1d1d1d");
        //顯示菜品部分設置寬度
        foodButtonScrollPane.setPrefHeight(hbox.getPrefHeight());
        foodButtonScrollPane.setPrefWidth(hbox.getPrefWidth()*0.65);
        //顯示整個按鍵部分設置寬度
        this.topButtonsVbox.setPrefWidth( hbox.getPrefWidth()*0.15);
        this.topButtonsVbox.setPrefHeight(hbox.getPrefHeight());
        //顯示按鍵部分設置寬度
        topButtonScrollPane.setPrefHeight(hbox.getPrefHeight()*0.7);
        topButtonScrollPane.setPrefWidth(this.topButtonsVbox.getPrefWidth());
        keyPageButtonVbox.setPrefWidth(hbox.getPrefWidth()*0.65);
        keyPageButtonVbox.setPrefHeight(hbox.getPrefHeight()*0.2);
        keyPreviousPageButton.setPrefWidth(hbox.getPrefWidth()*0.65);
        keyPreviousPageButton.setPrefHeight(hbox.getPrefHeight()*0.1);
        keyNextPageButton.setPrefWidth(hbox.getPrefWidth()*0.65);
        keyNextPageButton.setPrefHeight(hbox.getPrefHeight()*0.1);
        //topButtonAnchorPane.getContent().r(this.topButtonVbox);
        //this.topButtonVbox = topButtonbox;
        if(topButtonData != null && topButtonData.size()>=4){
            selectedTopButtonDatas.addAll(topButtonData.subList(0,4));
        }
        /*for(TopButton topButton :selectedTopButtonDatas){
            Button button = new Button();
            double m = topButtonsVbox.getPrefHeight();
            button.setPrefSize(topButtonsVbox.getPrefWidth(), hbox.getPrefHeight()*0.7/4);
            button.setText(topButton.getNAME1());
            button.setId(Long.toString(topButton.getID()));
            button.setMnemonicParsing(false);
            button.setOnAction(oa->{
                page.setValue(1);
                flowPane.getChildren().removeAll(flowPane.getChildren());
                foodButtons.removeAll(foodButtons);
                flowPane.setPrefWidth(hbox.getPrefWidth()*0.65);
                flowPane.setPrefHeight((hbox.getPrefHeight()/5)*4);
                foodButtons = FXCollections.observableArrayList();
                SqlSession session = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                TGoodsDtoMapper tGoodsDtoMapper = session.getMapper(TGoodsDtoMapper.class);
                tGoodsDtos = tGoodsDtoMapper.getTGoodsDto(button.getId());
                session.close();
                selectedTGoodsDtos.removeAll(selectedTGoodsDtos);
                if(tGoodsDtos != null && tGoodsDtos.size()>0){
                    if(tGoodsDtos.size()>=pageSize.getValue()){
                       selectedTGoodsDtos.addAll(tGoodsDtos.subList(0,pageSize.getValue()));
                    }
                    else{
                        selectedTGoodsDtos.addAll(tGoodsDtos);
                    }
                }

                for(TGoodsDto  tGoodsDto: selectedTGoodsDtos  ){
                    Button foodButton = new Button();
                    foodButton.setId(tGoodsDto.getGoodsNo());
                    SqlSession foodSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                    //根据菜品编号判断是否沽清
                    List<TGoodsDto> tGoodsDtosList =  foodSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(tGoodsDto.getGoodsNo());
                    foodSession.close();
                    if(tGoodsDtosList != null && tGoodsDtosList.size() > 0){
                        foodButton.setText(tGoodsDto.getGoodName1()+"\n(暫停)");
                    }
                    else{
                        foodButton.setText(tGoodsDto.getGoodName1());
                    }
                    foodButton.setMnemonicParsing(false);
                    foodButton.setStyle("-fx-pref-height:" + flowPane.getPrefHeight()/4 +  ";-fx-font-size:30; -fx-font-weight: bolder; -fx-pref-width:"+ flowPane.getPrefWidth()*0.313);
                    foodButton.setOnAction(event->{
                        SqlSession foodButtonSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                        List<TGoodsDto> tGoodsDtosList1 = foodButtonSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(foodButton.getId());
                        foodButtonSession.close();
                        try {
                         if(tGoodsDtosList1 == null || tGoodsDtosList1.size()==0){
                            FXMLLoader loader = new FXMLLoader();
                            loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                VBox vbox =  loader.load();
                                ConfirmController controller = loader.getController();
                                Scene vboxScene = new Scene(vbox);
                                Stage vboxStage = new Stage();
                                vbox.setPrefHeight(hbox.getHeight()*0.5);
                                vbox.setPrefWidth(hbox.getWidth()*0.5);
                                vboxStage.setScene(vboxScene);
                                vboxStage.initOwner(stage);
                                vboxStage.initModality(Modality.APPLICATION_MODAL);
                                vboxStage.initStyle(StageStyle.TRANSPARENT);
                                controller.confirmAction(foodButton,true,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                            }
                         else{
                            FXMLLoader loader = new FXMLLoader();
                            loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                            VBox vbox = null;
                                vbox = loader.load();
                            ConfirmController controller = loader.getController();
                            Scene vboxScene = new Scene(vbox);
                            Stage vboxStage = new Stage();
                            vbox.setPrefHeight(hbox.getHeight()*0.5);
                            vbox.setPrefWidth(hbox.getWidth()*0.5);
                            vboxStage.setScene(vboxScene);
                            vboxStage.initOwner(stage);
                            vboxStage.initModality(Modality.APPLICATION_MODAL);
                            vboxStage.initStyle(StageStyle.TRANSPARENT);
                            controller.confirmAction(foodButton,false,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                        }
                        } catch (IOException e) {
                            e.printStackTrace();
                            FXMLLoader loader = new FXMLLoader();
                            loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                            VBox vbox = null;
                            try {
                                vbox = loader.load();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            ConfirmController controller = loader.getController();
                            Scene vboxScene = new Scene(vbox);
                            Stage vboxStage = new Stage();
                            vbox.setPrefHeight(hbox.getHeight()*0.5);
                            vbox.setPrefWidth(hbox.getWidth()*0.5);
                            vboxStage.setScene(vboxScene);
                            vboxStage.initOwner(stage);
                            vboxStage.initModality(Modality.APPLICATION_MODAL);
                            vboxStage.initStyle(StageStyle.TRANSPARENT);
                            controller.confirmAction(foodButton,null,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                        }
                    });
                    foodButtons.add(foodButton);
                    flowPane.getChildren().add(foodButton);
                }
                previousPageButton.setOnAction(o-> {
                    if(page.getValue()>1){
                        foodButtons.remove(0,foodButtons.size());
                        flowPane.getChildren().remove(0,flowPane.getChildren().size());
                        page.set(page.getValue()-1);
                        int i = (page.getValue()-1)*pageSize.getValue();
                        selectedTGoodsDtos.removeAll(selectedTGoodsDtos);
                        selectedTGoodsDtos.addAll(tGoodsDtos.subList(i,(page.getValue()-1)*pageSize.getValue()+pageSize.getValue()));
                        for(TGoodsDto  tGoodsDto: selectedTGoodsDtos  ){
                            Button foodButton = new Button();
                            foodButton.setId(tGoodsDto.getGoodsNo());
                            SqlSession foodSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                            //根据菜品编号判断是否沽清
                            List<TGoodsDto> tGoodsDtosList =  foodSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(tGoodsDto.getGoodsNo());
                            foodSession.close();
                            if(tGoodsDtosList != null && tGoodsDtosList.size() > 0){
                                foodButton.setText(tGoodsDto.getGoodName1()+"\n(暫停)");
                            }
                            else{
                                foodButton.setText(tGoodsDto.getGoodName1());
                            }
                            foodButton.setMnemonicParsing(false);
                            foodButton.setStyle("-fx-pref-height:" + flowPane.getPrefHeight()/4 +  ";-fx-font-size:30; -fx-font-weight: bolder; -fx-pref-width:"+ flowPane.getPrefWidth()*0.313);
                            foodButton.setOnAction(event->{
                                SqlSession foodButtonSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                                List<TGoodsDto> tGoodsDtosList1 = foodButtonSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(foodButton.getId());
                                foodButtonSession.close();
                                try {
                                    if(tGoodsDtosList1 == null || tGoodsDtosList1.size()==0){
                                        FXMLLoader loader = new FXMLLoader();
                                        loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                        VBox vbox =  loader.load();
                                        ConfirmController controller = loader.getController();
                                        Scene vboxScene = new Scene(vbox);
                                        Stage vboxStage = new Stage();
                                        vbox.setPrefHeight(hbox.getHeight()*0.5);
                                        vbox.setPrefWidth(hbox.getWidth()*0.5);
                                        vboxStage.setScene(vboxScene);
                                        vboxStage.initOwner(stage);
                                        vboxStage.initModality(Modality.APPLICATION_MODAL);
                                        vboxStage.initStyle(StageStyle.TRANSPARENT);
                                        controller.confirmAction(foodButton,true,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                    }
                                    else{
                                        FXMLLoader loader = new FXMLLoader();
                                        loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                        VBox vbox = null;
                                        vbox = loader.load();
                                        ConfirmController controller = loader.getController();
                                        Scene vboxScene = new Scene(vbox);
                                        Stage vboxStage = new Stage();
                                        vbox.setPrefHeight(hbox.getHeight()*0.5);
                                        vbox.setPrefWidth(hbox.getWidth()*0.5);
                                        vboxStage.setScene(vboxScene);
                                        vboxStage.initOwner(stage);
                                        vboxStage.initModality(Modality.APPLICATION_MODAL);
                                        vboxStage.initStyle(StageStyle.TRANSPARENT);
                                        controller.confirmAction(foodButton,false,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    FXMLLoader loader = new FXMLLoader();
                                    loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                    VBox vbox = null;
                                    try {
                                        vbox = loader.load();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                    ConfirmController controller = loader.getController();
                                    Scene vboxScene = new Scene(vbox);
                                    Stage vboxStage = new Stage();
                                    vbox.setPrefHeight(hbox.getHeight()*0.5);
                                    vbox.setPrefWidth(hbox.getWidth()*0.5);
                                    vboxStage.setScene(vboxScene);
                                    vboxStage.initOwner(stage);
                                    vboxStage.initModality(Modality.APPLICATION_MODAL);
                                    vboxStage.initStyle(StageStyle.TRANSPARENT);
                                    controller.confirmAction(foodButton,null,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                }
                            });
                            foodButtons.add(foodButton);
                            flowPane.setPrefWidth(hbox.getPrefWidth()*0.65);
                            flowPane.setPrefHeight((hbox.getPrefHeight()/5)*4);
                            flowPane.getChildren().add(foodButton);
                        }
                    }
                });
                nextPageButton.setOnAction(o-> {
                    if(tGoodsDtos.size()>(page.getValue()*pageSize.getValue())){
                        foodButtons.remove(0,foodButtons.size());
                        flowPane.getChildren().remove(0,flowPane.getChildren().size());
                        if(tGoodsDtos.size()-(page.getValue()*pageSize.getValue()) >= pageSize.getValue()){
                            page.set(page.getValue()+1);
                            selectedTGoodsDtos.removeAll(selectedTGoodsDtos);
                            System.out.println("ooooooooooooooooooo=" + page.getValue());
                            selectedTGoodsDtos.addAll(tGoodsDtos.subList((page.getValue()-1)*pageSize.getValue(),(page.getValue()-1)*pageSize.getValue()+pageSize.getValue()));

                        }
                        else{
                            page.set(page.getValue()+1);
                            selectedTGoodsDtos.removeAll(selectedTGoodsDtos);
                            selectedTGoodsDtos.addAll(tGoodsDtos.subList((page.getValue()-1)*pageSize.getValue(),tGoodsDtos.size()));
                        }
                        for(TGoodsDto  tGoodsDto: selectedTGoodsDtos  ){
                            Button foodButton = new Button();
                            foodButton.setId(tGoodsDto.getGoodsNo());
                            SqlSession foodSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                            //根据菜品编号判断是否沽清
                            List<TGoodsDto> tGoodsDtosList =  foodSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(tGoodsDto.getGoodsNo());
                            foodSession.close();
                            if(tGoodsDtosList != null && tGoodsDtosList.size() > 0){
                                foodButton.setText(tGoodsDto.getGoodName1()+"\n(暫停)");
                            }
                            else{
                                foodButton.setText(tGoodsDto.getGoodName1());
                            }
                            foodButton.setMnemonicParsing(false);
                            foodButton.setStyle("-fx-pref-height:" + flowPane.getPrefHeight()/4 +  ";-fx-font-size:30; -fx-font-weight: bolder; -fx-pref-width:"+ flowPane.getPrefWidth()*0.313);
                            foodButton.setOnAction(event->{
                                SqlSession foodButtonSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                                List<TGoodsDto> tGoodsDtosList1 = foodButtonSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(foodButton.getId());
                                foodButtonSession.close();
                                try {
                                    if(tGoodsDtosList1 == null || tGoodsDtosList1.size()==0){
                                        FXMLLoader loader = new FXMLLoader();
                                        loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                        VBox vbox =  loader.load();
                                        ConfirmController controller = loader.getController();
                                        Scene vboxScene = new Scene(vbox);
                                        Stage vboxStage = new Stage();
                                        vbox.setPrefHeight(hbox.getHeight()*0.5);
                                        vbox.setPrefWidth(hbox.getWidth()*0.5);
                                        vboxStage.setScene(vboxScene);
                                        vboxStage.initOwner(stage);
                                        vboxStage.initModality(Modality.APPLICATION_MODAL);
                                        vboxStage.initStyle(StageStyle.TRANSPARENT);
                                        controller.confirmAction(foodButton,true,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                    }
                                    else{
                                        FXMLLoader loader = new FXMLLoader();
                                        loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                        VBox vbox = null;
                                        vbox = loader.load();
                                        ConfirmController controller = loader.getController();
                                        Scene vboxScene = new Scene(vbox);
                                        Stage vboxStage = new Stage();
                                        vbox.setPrefHeight(hbox.getHeight()*0.5);
                                        vbox.setPrefWidth(hbox.getWidth()*0.5);
                                        vboxStage.setScene(vboxScene);
                                        vboxStage.initOwner(stage);
                                        vboxStage.initModality(Modality.APPLICATION_MODAL);
                                        vboxStage.initStyle(StageStyle.TRANSPARENT);
                                        controller.confirmAction(foodButton,false,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    FXMLLoader loader = new FXMLLoader();
                                    loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                    VBox vbox = null;
                                    try {
                                        vbox = loader.load();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                    ConfirmController controller = loader.getController();
                                    Scene vboxScene = new Scene(vbox);
                                    Stage vboxStage = new Stage();
                                    vbox.setPrefHeight(hbox.getHeight()*0.5);
                                    vbox.setPrefWidth(hbox.getWidth()*0.5);
                                    vboxStage.setScene(vboxScene);
                                    vboxStage.initOwner(stage);
                                    vboxStage.initModality(Modality.APPLICATION_MODAL);
                                    vboxStage.initStyle(StageStyle.TRANSPARENT);
                                    controller.confirmAction(foodButton,null,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                }
                            });
                            foodButtons.add(foodButton);
                            flowPane.setPrefWidth(hbox.getPrefWidth()*0.65);
                            flowPane.setPrefHeight((hbox.getPrefHeight()/5)*4);
                            flowPane.getChildren().add(foodButton);
                        }
                    }
                });
                nextPageButton.setPrefHeight(hbox.getPrefHeight()/5);
                nextPageButton.setPrefWidth(hbox.getPrefWidth()*0.65/2);
                previousPageButton.setPrefHeight(hbox.getPrefHeight()/5);
                previousPageButton.setPrefWidth(hbox.getPrefWidth()*0.65/2);
                pageButtonHbox.setVisible(true);
            });
            this.topButtonVbox.getChildren().add(button);
        }*/
        addTopButton(hbox, stage);
        keyNextPageButton.setOnAction(oa-> {
             if(topButtonDatas.size()>0 && topButtonDatas.size()> keyPage.getValue()*keyPageSize.getValue()){
                 topButtonScrollPane.setPrefHeight(hbox.getPrefHeight()*0.7);
                 topButtonScrollPane.setPrefWidth(this.topButtonsVbox.getPrefWidth());
                 topButtonScrollPane.getChildren().remove(0,topButtonScrollPane.getChildren().size());
                  selectedTopButtonDatas.removeAll(selectedTopButtonDatas);
                 this.topButtonVbox.getChildren().remove(0,  this.topButtonVbox.getChildren().size());
                 if((topButtonDatas.size()-keyPage.getValue()*keyPageSize.getValue())>=keyPageSize.getValue()){
                     keyPage.setValue(keyPage.getValue()+1);
                     selectedTopButtonDatas.addAll(topButtonDatas.subList((keyPage.getValue()-1)*keyPageSize.getValue(),(keyPage.getValue()-1)*keyPageSize.getValue()+keyPageSize.getValue()));
                 }
                 else{
                     keyPage.setValue(keyPage.getValue()+1);
                     selectedTopButtonDatas.addAll(topButtonDatas.subList((keyPage.getValue()-1)*keyPageSize.getValue(),topButtonDatas.size()));
                 }
                /* for(TopButton topButton :selectedTopButtonDatas){
                     Button button = new Button();
                     double m = topButtonsVbox.getPrefHeight();
                     button.setPrefSize(topButtonsVbox.getPrefWidth(), hbox.getPrefHeight()*0.7/4);
                     button.setText(topButton.getNAME1());
                     button.setId(Long.toString(topButton.getID()));
                     button.setMnemonicParsing(false);
                     button.setOnAction(o->{
                         page.setValue(1);
                         flowPane.getChildren().removeAll(flowPane.getChildren());
                         foodButtons.removeAll(foodButtons);
                         flowPane.setPrefWidth(hbox.getPrefWidth()*0.65);
                         flowPane.setPrefHeight((hbox.getPrefHeight()/5)*4);
                         foodButtons = FXCollections.observableArrayList();
                         SqlSession session = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                         TGoodsDtoMapper tGoodsDtoMapper = session.getMapper(TGoodsDtoMapper.class);
                         tGoodsDtos = tGoodsDtoMapper.getTGoodsDto(button.getId());
                         session.close();
                         selectedTGoodsDtos.removeAll(selectedTGoodsDtos);
                         if(tGoodsDtos != null && tGoodsDtos.size()>0){
                             if(tGoodsDtos.size()>=pageSize.getValue()){
                                 selectedTGoodsDtos.addAll(tGoodsDtos.subList(0,pageSize.getValue()));
                             }
                             else{
                                 selectedTGoodsDtos.addAll(tGoodsDtos);
                             }
                         }

                         for(TGoodsDto  tGoodsDto: selectedTGoodsDtos  ){
                             Button foodButton = new Button();
                             foodButton.setId(tGoodsDto.getGoodsNo());
                             SqlSession foodSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                             //根据菜品编号判断是否沽清
                             List<TGoodsDto> tGoodsDtosList =  foodSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(tGoodsDto.getGoodsNo());
                             foodSession.close();
                             if(tGoodsDtosList != null && tGoodsDtosList.size() > 0){
                                 foodButton.setText(tGoodsDto.getGoodName1()+"\n(暫停)");
                             }
                             else{
                                 foodButton.setText(tGoodsDto.getGoodName1());
                             }
                             foodButton.setMnemonicParsing(false);
                             foodButton.setStyle("-fx-pref-height:" + flowPane.getPrefHeight()/4 +  ";-fx-font-size:30; -fx-font-weight: bolder; -fx-pref-width:"+ flowPane.getPrefWidth()*0.313);
                             foodButton.setOnAction(event->{
                                 SqlSession foodButtonSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                                 List<TGoodsDto> tGoodsDtosList1 = foodButtonSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(foodButton.getId());
                                 foodButtonSession.close();
                                 try {
                                     if(tGoodsDtosList1 == null || tGoodsDtosList1.size()==0){
                                         FXMLLoader loader = new FXMLLoader();
                                         loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                         VBox vbox =  loader.load();
                                         ConfirmController controller = loader.getController();
                                         Scene vboxScene = new Scene(vbox);
                                         Stage vboxStage = new Stage();
                                         vbox.setPrefHeight(hbox.getHeight()*0.5);
                                         vbox.setPrefWidth(hbox.getWidth()*0.5);
                                         vboxStage.setScene(vboxScene);
                                         vboxStage.initOwner(stage);
                                         vboxStage.initModality(Modality.APPLICATION_MODAL);
                                         vboxStage.initStyle(StageStyle.TRANSPARENT);
                                         controller.confirmAction(foodButton,true,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                     }
                                     else{
                                         FXMLLoader loader = new FXMLLoader();
                                         loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                         VBox vbox = null;
                                         vbox = loader.load();
                                         ConfirmController controller = loader.getController();
                                         Scene vboxScene = new Scene(vbox);
                                         Stage vboxStage = new Stage();
                                         vbox.setPrefHeight(hbox.getHeight()*0.5);
                                         vbox.setPrefWidth(hbox.getWidth()*0.5);
                                         vboxStage.setScene(vboxScene);
                                         vboxStage.initOwner(stage);
                                         vboxStage.initModality(Modality.APPLICATION_MODAL);
                                         vboxStage.initStyle(StageStyle.TRANSPARENT);
                                         controller.confirmAction(foodButton,false,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                     }
                                 } catch (IOException e) {
                                     e.printStackTrace();
                                     FXMLLoader loader = new FXMLLoader();
                                     loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                     VBox vbox = null;
                                     try {
                                         vbox = loader.load();
                                     } catch (IOException e1) {
                                         e1.printStackTrace();
                                     }
                                     ConfirmController controller = loader.getController();
                                     Scene vboxScene = new Scene(vbox);
                                     Stage vboxStage = new Stage();
                                     vbox.setPrefHeight(hbox.getHeight()*0.5);
                                     vbox.setPrefWidth(hbox.getWidth()*0.5);
                                     vboxStage.setScene(vboxScene);
                                     vboxStage.initOwner(stage);
                                     vboxStage.initModality(Modality.APPLICATION_MODAL);
                                     vboxStage.initStyle(StageStyle.TRANSPARENT);
                                     controller.confirmAction(foodButton,null,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                 }
                             });
                             foodButtons.add(foodButton);
                             flowPane.getChildren().add(foodButton);
                         }
                         previousPageButton.setOnAction(op-> {
                             if(page.getValue()>1){
                                 foodButtons.remove(0,foodButtons.size());
                                 flowPane.getChildren().remove(0,flowPane.getChildren().size());
                                 page.set(page.getValue()-1);
                                 int i = (page.getValue()-1)*pageSize.getValue();
                                 selectedTGoodsDtos.removeAll(selectedTGoodsDtos);
                                 selectedTGoodsDtos.addAll(tGoodsDtos.subList(i,(page.getValue()-1)*pageSize.getValue()+pageSize.getValue()));
                                 for(TGoodsDto  tGoodsDto: selectedTGoodsDtos  ){
                                     Button foodButton = new Button();
                                     foodButton.setId(tGoodsDto.getGoodsNo());
                                     SqlSession foodSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                                     //根据菜品编号判断是否沽清
                                     List<TGoodsDto> tGoodsDtosList =  foodSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(tGoodsDto.getGoodsNo());
                                     foodSession.close();
                                     if(tGoodsDtosList != null && tGoodsDtosList.size() > 0){
                                         foodButton.setText(tGoodsDto.getGoodName1()+"\n(暫停)");
                                     }
                                     else{
                                         foodButton.setText(tGoodsDto.getGoodName1());
                                     }
                                     foodButton.setMnemonicParsing(false);
                                     foodButton.setStyle("-fx-pref-height:" + flowPane.getPrefHeight()/4 +  ";-fx-font-size:30; -fx-font-weight: bolder; -fx-pref-width:"+ flowPane.getPrefWidth()*0.313);
                                     foodButton.setOnAction(event->{
                                         SqlSession foodButtonSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                                         List<TGoodsDto> tGoodsDtosList1 = foodButtonSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(foodButton.getId());
                                         foodButtonSession.close();
                                         try {
                                             if(tGoodsDtosList1 == null || tGoodsDtosList1.size()==0){
                                                 FXMLLoader loader = new FXMLLoader();
                                                 loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                                 VBox vbox =  loader.load();
                                                 ConfirmController controller = loader.getController();
                                                 Scene vboxScene = new Scene(vbox);
                                                 Stage vboxStage = new Stage();
                                                 vbox.setPrefHeight(hbox.getHeight()*0.5);
                                                 vbox.setPrefWidth(hbox.getWidth()*0.5);
                                                 vboxStage.setScene(vboxScene);
                                                 vboxStage.initOwner(stage);
                                                 vboxStage.initModality(Modality.APPLICATION_MODAL);
                                                 vboxStage.initStyle(StageStyle.TRANSPARENT);
                                                 controller.confirmAction(foodButton,true,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                             }
                                             else{
                                                 FXMLLoader loader = new FXMLLoader();
                                                 loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                                 VBox vbox = null;
                                                 vbox = loader.load();
                                                 ConfirmController controller = loader.getController();
                                                 Scene vboxScene = new Scene(vbox);
                                                 Stage vboxStage = new Stage();
                                                 vbox.setPrefHeight(hbox.getHeight()*0.5);
                                                 vbox.setPrefWidth(hbox.getWidth()*0.5);
                                                 vboxStage.setScene(vboxScene);
                                                 vboxStage.initOwner(stage);
                                                 vboxStage.initModality(Modality.APPLICATION_MODAL);
                                                 vboxStage.initStyle(StageStyle.TRANSPARENT);
                                                 controller.confirmAction(foodButton,false,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                             }
                                         } catch (IOException e) {
                                             e.printStackTrace();
                                             FXMLLoader loader = new FXMLLoader();
                                             loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                             VBox vbox = null;
                                             try {
                                                 vbox = loader.load();
                                             } catch (IOException e1) {
                                                 e1.printStackTrace();
                                             }
                                             ConfirmController controller = loader.getController();
                                             Scene vboxScene = new Scene(vbox);
                                             Stage vboxStage = new Stage();
                                             vbox.setPrefHeight(hbox.getHeight()*0.5);
                                             vbox.setPrefWidth(hbox.getWidth()*0.5);
                                             vboxStage.setScene(vboxScene);
                                             vboxStage.initOwner(stage);
                                             vboxStage.initModality(Modality.APPLICATION_MODAL);
                                             vboxStage.initStyle(StageStyle.TRANSPARENT);
                                             controller.confirmAction(foodButton,null,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                         }
                                     });
                                     foodButtons.add(foodButton);
                                     flowPane.setPrefWidth(hbox.getPrefWidth()*0.65);
                                     flowPane.setPrefHeight((hbox.getPrefHeight()/5)*4);
                                     flowPane.getChildren().add(foodButton);
                                 }
                             }
                         });
                         nextPageButton.setOnAction(od-> {
                             if(tGoodsDtos.size()>(page.getValue()*pageSize.getValue())){
                                 foodButtons.remove(0,foodButtons.size());
                                 flowPane.getChildren().remove(0,flowPane.getChildren().size());
                                 if(tGoodsDtos.size()-(page.getValue()*pageSize.getValue()) >= pageSize.getValue()){
                                     page.set(page.getValue()+1);
                                     selectedTGoodsDtos.removeAll(selectedTGoodsDtos);
                                     System.out.println("ooooooooooooooooooo=" + page.getValue());
                                     selectedTGoodsDtos.addAll(tGoodsDtos.subList((page.getValue()-1)*pageSize.getValue(),(page.getValue()-1)*pageSize.getValue()+pageSize.getValue()));

                                 }
                                 else{
                                     page.set(page.getValue()+1);
                                     selectedTGoodsDtos.removeAll(selectedTGoodsDtos);
                                     selectedTGoodsDtos.addAll(tGoodsDtos.subList((page.getValue()-1)*pageSize.getValue(),tGoodsDtos.size()));
                                 }
                                 for(TGoodsDto  tGoodsDto: selectedTGoodsDtos  ){
                                     Button foodButton = new Button();
                                     foodButton.setId(tGoodsDto.getGoodsNo());
                                     SqlSession foodSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                                     //根据菜品编号判断是否沽清
                                     List<TGoodsDto> tGoodsDtosList =  foodSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(tGoodsDto.getGoodsNo());
                                     foodSession.close();
                                     if(tGoodsDtosList != null && tGoodsDtosList.size() > 0){
                                         foodButton.setText(tGoodsDto.getGoodName1()+"\n(暫停)");
                                     }
                                     else{
                                         foodButton.setText(tGoodsDto.getGoodName1());
                                     }
                                     foodButton.setMnemonicParsing(false);
                                     foodButton.setStyle("-fx-pref-height:" + flowPane.getPrefHeight()/4 +  ";-fx-font-size:30; -fx-font-weight: bolder; -fx-pref-width:"+ flowPane.getPrefWidth()*0.313);
                                     foodButton.setOnAction(event->{
                                         SqlSession foodButtonSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                                         List<TGoodsDto> tGoodsDtosList1 = foodButtonSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(foodButton.getId());
                                         foodButtonSession.close();
                                         try {
                                             if(tGoodsDtosList1 == null || tGoodsDtosList1.size()==0){
                                                 FXMLLoader loader = new FXMLLoader();
                                                 loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                                 VBox vbox =  loader.load();
                                                 ConfirmController controller = loader.getController();
                                                 Scene vboxScene = new Scene(vbox);
                                                 Stage vboxStage = new Stage();
                                                 vbox.setPrefHeight(hbox.getHeight()*0.5);
                                                 vbox.setPrefWidth(hbox.getWidth()*0.5);
                                                 vboxStage.setScene(vboxScene);
                                                 vboxStage.initOwner(stage);
                                                 vboxStage.initModality(Modality.APPLICATION_MODAL);
                                                 vboxStage.initStyle(StageStyle.TRANSPARENT);
                                                 controller.confirmAction(foodButton,true,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                             }
                                             else{
                                                 FXMLLoader loader = new FXMLLoader();
                                                 loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                                 VBox vbox = null;
                                                 vbox = loader.load();
                                                 ConfirmController controller = loader.getController();
                                                 Scene vboxScene = new Scene(vbox);
                                                 Stage vboxStage = new Stage();
                                                 vbox.setPrefHeight(hbox.getHeight()*0.5);
                                                 vbox.setPrefWidth(hbox.getWidth()*0.5);
                                                 vboxStage.setScene(vboxScene);
                                                 vboxStage.initOwner(stage);
                                                 vboxStage.initModality(Modality.APPLICATION_MODAL);
                                                 vboxStage.initStyle(StageStyle.TRANSPARENT);
                                                 controller.confirmAction(foodButton,false,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                             }
                                         } catch (IOException e) {
                                             e.printStackTrace();
                                             FXMLLoader loader = new FXMLLoader();
                                             loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                             VBox vbox = null;
                                             try {
                                                 vbox = loader.load();
                                             } catch (IOException e1) {
                                                 e1.printStackTrace();
                                             }
                                             ConfirmController controller = loader.getController();
                                             Scene vboxScene = new Scene(vbox);
                                             Stage vboxStage = new Stage();
                                             vbox.setPrefHeight(hbox.getHeight()*0.5);
                                             vbox.setPrefWidth(hbox.getWidth()*0.5);
                                             vboxStage.setScene(vboxScene);
                                             vboxStage.initOwner(stage);
                                             vboxStage.initModality(Modality.APPLICATION_MODAL);
                                             vboxStage.initStyle(StageStyle.TRANSPARENT);
                                             controller.confirmAction(foodButton,null,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                         }
                                     });
                                     foodButtons.add(foodButton);
                                     flowPane.setPrefWidth(hbox.getPrefWidth()*0.65);
                                     flowPane.setPrefHeight((hbox.getPrefHeight()/5)*4);
                                     flowPane.getChildren().add(foodButton);
                                 }
                             }
                         });
                         nextPageButton.setPrefHeight(hbox.getPrefHeight()/5);
                         nextPageButton.setPrefWidth(hbox.getPrefWidth()*0.65/2);
                         previousPageButton.setPrefHeight(hbox.getPrefHeight()/5);
                         previousPageButton.setPrefWidth(hbox.getPrefWidth()*0.65/2);
                         pageButtonHbox.setVisible(true);
                     });
                     this.topButtonVbox.getChildren().add(button);
                 }*/
                 addTopButton(hbox, stage);
                 topButtonScrollPane.getChildren().add((this.topButtonVbox));
             }

        });
        keyPreviousPageButton.setOnAction(oa-> {
            if(topButtonDatas.size()>0 && keyPage.getValue()>1 ){
                topButtonScrollPane.setPrefHeight(hbox.getPrefHeight()*0.7);
                topButtonScrollPane.setPrefWidth(this.topButtonsVbox.getPrefWidth());
                topButtonScrollPane.getChildren().removeAll(topButtonScrollPane.getChildren());
                selectedTopButtonDatas.removeAll(selectedTopButtonDatas);
                this.topButtonVbox.getChildren().remove(0,this.topButtonVbox.getChildren().size());
                keyPage.setValue(keyPage.getValue()-1);
                selectedTopButtonDatas.addAll(topButtonDatas.subList((keyPage.getValue()-1)*keyPageSize.getValue(),(keyPage.getValue()-1)*keyPageSize.getValue()+keyPageSize.getValue()));
                /*for(TopButton topButton :selectedTopButtonDatas){
                    Button button = new Button();
                    double m = topButtonsVbox.getPrefHeight();
                    button.setPrefSize(topButtonsVbox.getPrefWidth(), hbox.getPrefHeight()*0.7/4);
                    button.setText(topButton.getNAME1());
                    button.setId(Long.toString(topButton.getID()));
                    button.setMnemonicParsing(false);
                    button.setOnAction(o->{
                        page.setValue(1);
                        flowPane.getChildren().removeAll(flowPane.getChildren());
                        foodButtons.removeAll(foodButtons);
                        flowPane.setPrefWidth(hbox.getPrefWidth()*0.65);
                        flowPane.setPrefHeight((hbox.getPrefHeight()/5)*4);
                        foodButtons = FXCollections.observableArrayList();
                        SqlSession session = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                        TGoodsDtoMapper tGoodsDtoMapper = session.getMapper(TGoodsDtoMapper.class);
                        tGoodsDtos = tGoodsDtoMapper.getTGoodsDto(button.getId());
                        session.close();
                        selectedTGoodsDtos.removeAll(selectedTGoodsDtos);
                        if(tGoodsDtos != null && tGoodsDtos.size()>0){
                            if(tGoodsDtos.size()>=pageSize.getValue()){
                                selectedTGoodsDtos.addAll(tGoodsDtos.subList(0,pageSize.getValue()));
                            }
                            else{
                                selectedTGoodsDtos.addAll(tGoodsDtos);
                            }
                        }

                        for(TGoodsDto  tGoodsDto: selectedTGoodsDtos  ){
                            Button foodButton = new Button();
                            foodButton.setId(tGoodsDto.getGoodsNo());
                            SqlSession foodSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                            //根据菜品编号判断是否沽清
                            List<TGoodsDto> tGoodsDtosList =  foodSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(tGoodsDto.getGoodsNo());
                            foodSession.close();
                            if(tGoodsDtosList != null && tGoodsDtosList.size() > 0){
                                foodButton.setText(tGoodsDto.getGoodName1()+"\n(暫停)");
                            }
                            else{
                                foodButton.setText(tGoodsDto.getGoodName1());
                            }
                            foodButton.setMnemonicParsing(false);
                            foodButton.setStyle("-fx-pref-height:" + flowPane.getPrefHeight()/4 +  ";-fx-font-size:30; -fx-font-weight: bolder; -fx-pref-width:"+ flowPane.getPrefWidth()*0.313);
                            foodButton.setOnAction(event->{
                                SqlSession foodButtonSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                                List<TGoodsDto> tGoodsDtosList1 = foodButtonSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(foodButton.getId());
                                foodButtonSession.close();
                                try {
                                    if(tGoodsDtosList1 == null || tGoodsDtosList1.size()==0){
                                        FXMLLoader loader = new FXMLLoader();
                                        loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                        VBox vbox =  loader.load();
                                        ConfirmController controller = loader.getController();
                                        Scene vboxScene = new Scene(vbox);
                                        Stage vboxStage = new Stage();
                                        vbox.setPrefHeight(hbox.getHeight()*0.5);
                                        vbox.setPrefWidth(hbox.getWidth()*0.5);
                                        vboxStage.setScene(vboxScene);
                                        vboxStage.initOwner(stage);
                                        vboxStage.initModality(Modality.APPLICATION_MODAL);
                                        vboxStage.initStyle(StageStyle.TRANSPARENT);
                                        controller.confirmAction(foodButton,true,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                    }
                                    else{
                                        FXMLLoader loader = new FXMLLoader();
                                        loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                        VBox vbox = null;
                                        vbox = loader.load();
                                        ConfirmController controller = loader.getController();
                                        Scene vboxScene = new Scene(vbox);
                                        Stage vboxStage = new Stage();
                                        vbox.setPrefHeight(hbox.getHeight()*0.5);
                                        vbox.setPrefWidth(hbox.getWidth()*0.5);
                                        vboxStage.setScene(vboxScene);
                                        vboxStage.initOwner(stage);
                                        vboxStage.initModality(Modality.APPLICATION_MODAL);
                                        vboxStage.initStyle(StageStyle.TRANSPARENT);
                                        controller.confirmAction(foodButton,false,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    FXMLLoader loader = new FXMLLoader();
                                    loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                    VBox vbox = null;
                                    try {
                                        vbox = loader.load();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                    ConfirmController controller = loader.getController();
                                    Scene vboxScene = new Scene(vbox);
                                    Stage vboxStage = new Stage();
                                    vbox.setPrefHeight(hbox.getHeight()*0.5);
                                    vbox.setPrefWidth(hbox.getWidth()*0.5);
                                    vboxStage.setScene(vboxScene);
                                    vboxStage.initOwner(stage);
                                    vboxStage.initModality(Modality.APPLICATION_MODAL);
                                    vboxStage.initStyle(StageStyle.TRANSPARENT);
                                    controller.confirmAction(foodButton,null,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                }
                            });
                            foodButtons.add(foodButton);
                            flowPane.getChildren().add(foodButton);
                        }
                        previousPageButton.setOnAction(op-> {
                            if(page.getValue()>1){
                                foodButtons.remove(0,foodButtons.size());
                                flowPane.getChildren().remove(0,flowPane.getChildren().size());
                                page.set(page.getValue()-1);
                                int i = (page.getValue()-1)*pageSize.getValue();
                                selectedTGoodsDtos.removeAll(selectedTGoodsDtos);
                                selectedTGoodsDtos.addAll(tGoodsDtos.subList(i,(page.getValue()-1)*pageSize.getValue()+pageSize.getValue()));
                                for(TGoodsDto  tGoodsDto: selectedTGoodsDtos  ){
                                    Button foodButton = new Button();
                                    foodButton.setId(tGoodsDto.getGoodsNo());
                                    SqlSession foodSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                                    //根据菜品编号判断是否沽清
                                    List<TGoodsDto> tGoodsDtosList =  foodSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(tGoodsDto.getGoodsNo());
                                    foodSession.close();
                                    if(tGoodsDtosList != null && tGoodsDtosList.size() > 0){
                                        foodButton.setText(tGoodsDto.getGoodName1()+"\n(暫停)");
                                    }
                                    else{
                                        foodButton.setText(tGoodsDto.getGoodName1());
                                    }
                                    foodButton.setMnemonicParsing(false);
                                    foodButton.setStyle("-fx-pref-height:" + flowPane.getPrefHeight()/4 +  ";-fx-font-size:30; -fx-font-weight: bolder; -fx-pref-width:"+ flowPane.getPrefWidth()*0.313);
                                    foodButton.setOnAction(event->{
                                        SqlSession foodButtonSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                                        List<TGoodsDto> tGoodsDtosList1 = foodButtonSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(foodButton.getId());
                                        foodButtonSession.close();
                                        try {
                                            if(tGoodsDtosList1 == null || tGoodsDtosList1.size()==0){
                                                FXMLLoader loader = new FXMLLoader();
                                                loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                                VBox vbox =  loader.load();
                                                ConfirmController controller = loader.getController();
                                                Scene vboxScene = new Scene(vbox);
                                                Stage vboxStage = new Stage();
                                                vbox.setPrefHeight(hbox.getHeight()*0.5);
                                                vbox.setPrefWidth(hbox.getWidth()*0.5);
                                                vboxStage.setScene(vboxScene);
                                                vboxStage.initOwner(stage);
                                                vboxStage.initModality(Modality.APPLICATION_MODAL);
                                                vboxStage.initStyle(StageStyle.TRANSPARENT);
                                                controller.confirmAction(foodButton,true,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                            }
                                            else{
                                                FXMLLoader loader = new FXMLLoader();
                                                loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                                VBox vbox = null;
                                                vbox = loader.load();
                                                ConfirmController controller = loader.getController();
                                                Scene vboxScene = new Scene(vbox);
                                                Stage vboxStage = new Stage();
                                                vbox.setPrefHeight(hbox.getHeight()*0.5);
                                                vbox.setPrefWidth(hbox.getWidth()*0.5);
                                                vboxStage.setScene(vboxScene);
                                                vboxStage.initOwner(stage);
                                                vboxStage.initModality(Modality.APPLICATION_MODAL);
                                                vboxStage.initStyle(StageStyle.TRANSPARENT);
                                                controller.confirmAction(foodButton,false,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            FXMLLoader loader = new FXMLLoader();
                                            loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                            VBox vbox = null;
                                            try {
                                                vbox = loader.load();
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
                                            ConfirmController controller = loader.getController();
                                            Scene vboxScene = new Scene(vbox);
                                            Stage vboxStage = new Stage();
                                            vbox.setPrefHeight(hbox.getHeight()*0.5);
                                            vbox.setPrefWidth(hbox.getWidth()*0.5);
                                            vboxStage.setScene(vboxScene);
                                            vboxStage.initOwner(stage);
                                            vboxStage.initModality(Modality.APPLICATION_MODAL);
                                            vboxStage.initStyle(StageStyle.TRANSPARENT);
                                            controller.confirmAction(foodButton,null,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                        }
                                    });
                                    foodButtons.add(foodButton);
                                    flowPane.setPrefWidth(hbox.getPrefWidth()*0.65);
                                    flowPane.setPrefHeight((hbox.getPrefHeight()/5)*4);
                                    flowPane.getChildren().add(foodButton);
                                }
                            }
                        });
                        nextPageButton.setOnAction(od-> {
                            if(tGoodsDtos.size()>(page.getValue()*pageSize.getValue())){
                                foodButtons.remove(0,foodButtons.size());
                                flowPane.getChildren().remove(0,flowPane.getChildren().size());
                                if(tGoodsDtos.size()-(page.getValue()*pageSize.getValue()) >= pageSize.getValue()){
                                    page.set(page.getValue()+1);
                                    selectedTGoodsDtos.removeAll(selectedTGoodsDtos);
                                    System.out.println("ooooooooooooooooooo=" + page.getValue());
                                    selectedTGoodsDtos.addAll(tGoodsDtos.subList((page.getValue()-1)*pageSize.getValue(),(page.getValue()-1)*pageSize.getValue()+pageSize.getValue()));

                                }
                                else{
                                    page.set(page.getValue()+1);
                                    selectedTGoodsDtos.removeAll(selectedTGoodsDtos);
                                    selectedTGoodsDtos.addAll(tGoodsDtos.subList((page.getValue()-1)*pageSize.getValue(),tGoodsDtos.size()));
                                }
                                for(TGoodsDto  tGoodsDto: selectedTGoodsDtos  ){
                                    Button foodButton = new Button();
                                    foodButton.setId(tGoodsDto.getGoodsNo());
                                    SqlSession foodSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                                    //根据菜品编号判断是否沽清
                                    List<TGoodsDto> tGoodsDtosList =  foodSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(tGoodsDto.getGoodsNo());
                                    foodSession.close();
                                    if(tGoodsDtosList != null && tGoodsDtosList.size() > 0){
                                        foodButton.setText(tGoodsDto.getGoodName1()+"\n(暫停)");
                                    }
                                    else{
                                        foodButton.setText(tGoodsDto.getGoodName1());
                                    }
                                    foodButton.setMnemonicParsing(false);
                                    foodButton.setStyle("-fx-pref-height:" + flowPane.getPrefHeight()/4 +  ";-fx-font-size:30; -fx-font-weight: bolder; -fx-pref-width:"+ flowPane.getPrefWidth()*0.313);
                                    foodButton.setOnAction(event->{
                                        SqlSession foodButtonSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
                                        List<TGoodsDto> tGoodsDtosList1 = foodButtonSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(foodButton.getId());
                                        foodButtonSession.close();
                                        try {
                                            if(tGoodsDtosList1 == null || tGoodsDtosList1.size()==0){
                                                FXMLLoader loader = new FXMLLoader();
                                                loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                                VBox vbox =  loader.load();
                                                ConfirmController controller = loader.getController();
                                                Scene vboxScene = new Scene(vbox);
                                                Stage vboxStage = new Stage();
                                                vbox.setPrefHeight(hbox.getHeight()*0.5);
                                                vbox.setPrefWidth(hbox.getWidth()*0.5);
                                                vboxStage.setScene(vboxScene);
                                                vboxStage.initOwner(stage);
                                                vboxStage.initModality(Modality.APPLICATION_MODAL);
                                                vboxStage.initStyle(StageStyle.TRANSPARENT);
                                                controller.confirmAction(foodButton,true,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                            }
                                            else{
                                                FXMLLoader loader = new FXMLLoader();
                                                loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                                VBox vbox = null;
                                                vbox = loader.load();
                                                ConfirmController controller = loader.getController();
                                                Scene vboxScene = new Scene(vbox);
                                                Stage vboxStage = new Stage();
                                                vbox.setPrefHeight(hbox.getHeight()*0.5);
                                                vbox.setPrefWidth(hbox.getWidth()*0.5);
                                                vboxStage.setScene(vboxScene);
                                                vboxStage.initOwner(stage);
                                                vboxStage.initModality(Modality.APPLICATION_MODAL);
                                                vboxStage.initStyle(StageStyle.TRANSPARENT);
                                                controller.confirmAction(foodButton,false,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            FXMLLoader loader = new FXMLLoader();
                                            loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                            VBox vbox = null;
                                            try {
                                                vbox = loader.load();
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
                                            ConfirmController controller = loader.getController();
                                            Scene vboxScene = new Scene(vbox);
                                            Stage vboxStage = new Stage();
                                            vbox.setPrefHeight(hbox.getHeight()*0.5);
                                            vbox.setPrefWidth(hbox.getWidth()*0.5);
                                            vboxStage.setScene(vboxScene);
                                            vboxStage.initOwner(stage);
                                            vboxStage.initModality(Modality.APPLICATION_MODAL);
                                            vboxStage.initStyle(StageStyle.TRANSPARENT);
                                            controller.confirmAction(foodButton,null,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                        }
                                    });
                                    foodButtons.add(foodButton);
                                    flowPane.setPrefWidth(hbox.getPrefWidth()*0.65);
                                    flowPane.setPrefHeight((hbox.getPrefHeight()/5)*4);
                                    flowPane.getChildren().add(foodButton);
                                }
                            }
                        });
                        nextPageButton.setPrefHeight(hbox.getPrefHeight()/5);
                        nextPageButton.setPrefWidth(hbox.getPrefWidth()*0.65/2);
                        previousPageButton.setPrefHeight(hbox.getPrefHeight()/5);
                        previousPageButton.setPrefWidth(hbox.getPrefWidth()*0.65/2);
                        pageButtonHbox.setVisible(true);
                    });
                    this.topButtonVbox.getChildren().add(button);
                }*/
                 addTopButton(hbox,stage);
                topButtonScrollPane.getChildren().add((this.topButtonVbox));
            }
        });

        topButtonScrollPane.getChildren().add((this.topButtonVbox));
        this.topButtonVbox.getStylesheets().add(Main.class.getResource("controller/view/Category.css").toExternalForm());
        this.topButtonVbox.getStyleClass().add("background");
        this.topButtonVbox.setPrefHeight(topButtonScrollPane.getPrefHeight());


        closeButton.setStyle("-fx-pref-height: "+ hbox.getPrefHeight()*0.1+ ";-fx-pref-width: "+ topButtonsVbox.getPrefWidth());
        closeButton.setOnAction(event-> {
            stage.close();
        });

        TgoodsTab.setPrefHeight(hbox.getPrefHeight());
        TgoodsTab.setStyle("-fx-font-size: 30; -fx-font-weight: bolder");
        TgoodsTab.setPrefWidth(hbox.getPrefWidth()*0.27);


        itemCodeCol.setPrefWidth(TgoodsTab.getPrefWidth()*0.3);
        itemNameCol.setPrefWidth(TgoodsTab.getPrefWidth()*0.6);

        TgoodsTab.setRowFactory(new Callback<TableView<TGoods>, TableRow<TGoods>>() {
            @Override
            public TableRow<TGoods> call(TableView<TGoods> param) {
                return new FoodListController(flowPane);
            }
        });

    }

    public FoodListController() {
    }

    public FoodListController(FlowPane flowPaneNode) {
        super();
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                if (event.getButton().equals(MouseButton.PRIMARY)
                        && event.getClickCount() == 1
                      ) {
                    System.out.println(flowPaneNode);
                    TableView<TGoods> tableView = FoodListController.super.getTableView();
                    TGoods tGoods = tableView.getSelectionModel().getSelectedItem();
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                    VBox vbox = null;
                    vbox = loader.load();
                    ConfirmController controller = loader.getController();
                    Scene vboxScene = new Scene(vbox);
                    Stage vboxStage = new Stage();
                    vbox.setPrefHeight(FoodListController.super.getScene().getHeight() * 0.5);
                    vbox.setPrefWidth(FoodListController.super.getScene().getWidth() * 0.5);
                    vbox.setAlignment(Pos.CENTER);
                    vboxStage.setScene(vboxScene);
                    vboxStage.initOwner(FoodListController.super.getScene().getWindow());
                    vboxStage.initModality(Modality.APPLICATION_MODAL);
                    vboxStage.initStyle(StageStyle.TRANSPARENT);
                    AnchorPane anchorPane = (AnchorPane) vbox.getChildren().get(1);
                    anchorPane.setPrefWidth(FoodListController.super.getScene().getWidth() * 0.5);
                    Label label = (Label) anchorPane.getChildren().get(0);
                    label.setPrefWidth(FoodListController.super.getScene().getWidth() * 0.5);
                    label.setTextAlignment(TextAlignment.CENTER);
                    System.out.print(FoodListController.super.getChildren().get(0));
                    controller.confirmAction(false, tableView,tGoods ,vboxStage, vbox,flowPaneNode);
                }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void addTopButton(HBox hbox,Stage stage){
        for(TopButton topButton :selectedTopButtonDatas){
            Button button = new Button();
            double m = topButtonsVbox.getPrefHeight();
            button.setPrefSize(topButtonsVbox.getPrefWidth(), hbox.getPrefHeight()*0.7/4);
            button.setText(topButton.getNAME1());
            button.setId(Long.toString(topButton.getID()));
            button.setMnemonicParsing(false);
            button.setOnAction(o->{
                page.setValue(1);
                flowPane.getChildren().removeAll(flowPane.getChildren());
                foodButtons.removeAll(foodButtons);
                flowPane.setPrefWidth(hbox.getPrefWidth()*0.65);
                flowPane.setPrefHeight((hbox.getPrefHeight()/5)*4);
                foodButtons = FXCollections.observableArrayList();
                SqlSession session = SqlSessionUtil.getSqlSession();
                TGoodsDtoMapper tGoodsDtoMapper = session.getMapper(TGoodsDtoMapper.class);
                tGoodsDtos = tGoodsDtoMapper.getTGoodsDto(button.getId());
                selectedTGoodsDtos.removeAll(selectedTGoodsDtos);
                if(tGoodsDtos != null && tGoodsDtos.size()>0){
                    if(tGoodsDtos.size()>=pageSize.getValue()){
                        selectedTGoodsDtos.addAll(tGoodsDtos.subList(0,pageSize.getValue()));
                    }
                    else{
                        selectedTGoodsDtos.addAll(tGoodsDtos);
                    }
                }

                for(TGoodsDto  tGoodsDto: selectedTGoodsDtos  ){
                    Button foodButton = new Button();
                    foodButton.setId(tGoodsDto.getGoodsNo());
                    SqlSession foodSession = SqlSessionUtil.getSqlSession();
                    //根据菜品编号判断是否沽清
                    List<TGoodsDto> tGoodsDtosList =  foodSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(tGoodsDto.getGoodsNo());
                    if(tGoodsDtosList != null && tGoodsDtosList.size() > 0){
                        foodButton.setText(tGoodsDto.getGoodName1()+"\n(暫停)");
                    }
                    else{
                        foodButton.setText(tGoodsDto.getGoodName1());
                    }
                    foodButton.setMnemonicParsing(false);
                    foodButton.setStyle("-fx-pref-height:" + flowPane.getPrefHeight()/4 +  ";-fx-font-size:30; -fx-font-weight: bolder; -fx-pref-width:"+ flowPane.getPrefWidth()*0.313);
                    foodButton.setOnAction(event->{
                        SqlSession foodButtonSession = SqlSessionUtil.getSqlSession();
                        List<TGoodsDto> tGoodsDtosList1 = foodButtonSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(foodButton.getId());
                        try {
                            if(tGoodsDtosList1 == null || tGoodsDtosList1.size()==0){
                                FXMLLoader loader = new FXMLLoader();
                                loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                VBox vbox =  loader.load();
                                ConfirmController controller = loader.getController();
                                Scene vboxScene = new Scene(vbox);
                                Stage vboxStage = new Stage();
                                vbox.setPrefHeight(hbox.getHeight()*0.5);
                                vbox.setPrefWidth(hbox.getWidth()*0.5);
                                vboxStage.setScene(vboxScene);
                                vboxStage.initOwner(stage);
                                vboxStage.initModality(Modality.APPLICATION_MODAL);
                                vboxStage.initStyle(StageStyle.TRANSPARENT);
                                controller.confirmAction(foodButton,true,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                            }
                            else{
                                FXMLLoader loader = new FXMLLoader();
                                loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                VBox vbox = null;
                                vbox = loader.load();
                                ConfirmController controller = loader.getController();
                                Scene vboxScene = new Scene(vbox);
                                Stage vboxStage = new Stage();
                                vbox.setPrefHeight(hbox.getHeight()*0.5);
                                vbox.setPrefWidth(hbox.getWidth()*0.5);
                                vboxStage.setScene(vboxScene);
                                vboxStage.initOwner(stage);
                                vboxStage.initModality(Modality.APPLICATION_MODAL);
                                vboxStage.initStyle(StageStyle.TRANSPARENT);
                                controller.confirmAction(foodButton,false,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            FXMLLoader loader = new FXMLLoader();
                            loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                            VBox vbox = null;
                            try {
                                vbox = loader.load();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            ConfirmController controller = loader.getController();
                            Scene vboxScene = new Scene(vbox);
                            Stage vboxStage = new Stage();
                            vbox.setPrefHeight(hbox.getHeight()*0.5);
                            vbox.setPrefWidth(hbox.getWidth()*0.5);
                            vboxStage.setScene(vboxScene);
                            vboxStage.initOwner(stage);
                            vboxStage.initModality(Modality.APPLICATION_MODAL);
                            vboxStage.initStyle(StageStyle.TRANSPARENT);
                            controller.confirmAction(foodButton,null,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                        }
                    });
                    foodButtons.add(foodButton);
                    flowPane.getChildren().add(foodButton);
                }
                previousPageButton.setOnAction(op-> {
                    if(page.getValue()>1){
                        foodButtons.remove(0,foodButtons.size());
                        flowPane.getChildren().remove(0,flowPane.getChildren().size());
                        page.set(page.getValue()-1);
                        int i = (page.getValue()-1)*pageSize.getValue();
                        selectedTGoodsDtos.removeAll(selectedTGoodsDtos);
                        selectedTGoodsDtos.addAll(tGoodsDtos.subList(i,(page.getValue()-1)*pageSize.getValue()+pageSize.getValue()));
                        for(TGoodsDto  tGoodsDto: selectedTGoodsDtos  ){
                            Button foodButton = new Button();
                            foodButton.setId(tGoodsDto.getGoodsNo());
                            SqlSession foodSession = SqlSessionUtil.getSqlSession();
                            //根据菜品编号判断是否沽清
                            List<TGoodsDto> tGoodsDtosList =  foodSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(tGoodsDto.getGoodsNo());
                            if(tGoodsDtosList != null && tGoodsDtosList.size() > 0){
                                foodButton.setText(tGoodsDto.getGoodName1()+"\n(暫停)");
                            }
                            else{
                                foodButton.setText(tGoodsDto.getGoodName1());
                            }
                            foodButton.setMnemonicParsing(false);
                            foodButton.setStyle("-fx-pref-height:" + flowPane.getPrefHeight()/4 +  ";-fx-font-size:30; -fx-font-weight: bolder; -fx-pref-width:"+ flowPane.getPrefWidth()*0.313);
                            foodButton.setOnAction(event->{
                                SqlSession foodButtonSession = SqlSessionUtil.getSqlSession();
                                List<TGoodsDto> tGoodsDtosList1 = foodButtonSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(foodButton.getId());
                                try {
                                    if(tGoodsDtosList1 == null || tGoodsDtosList1.size()==0){
                                        FXMLLoader loader = new FXMLLoader();
                                        loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                        VBox vbox =  loader.load();
                                        ConfirmController controller = loader.getController();
                                        Scene vboxScene = new Scene(vbox);
                                        Stage vboxStage = new Stage();
                                        vbox.setPrefHeight(hbox.getHeight()*0.5);
                                        vbox.setPrefWidth(hbox.getWidth()*0.5);
                                        vboxStage.setScene(vboxScene);
                                        vboxStage.initOwner(stage);
                                        vboxStage.initModality(Modality.APPLICATION_MODAL);
                                        vboxStage.initStyle(StageStyle.TRANSPARENT);
                                        controller.confirmAction(foodButton,true,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                    }
                                    else{
                                        FXMLLoader loader = new FXMLLoader();
                                        loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                        VBox vbox = null;
                                        vbox = loader.load();
                                        ConfirmController controller = loader.getController();
                                        Scene vboxScene = new Scene(vbox);
                                        Stage vboxStage = new Stage();
                                        vbox.setPrefHeight(hbox.getHeight()*0.5);
                                        vbox.setPrefWidth(hbox.getWidth()*0.5);
                                        vboxStage.setScene(vboxScene);
                                        vboxStage.initOwner(stage);
                                        vboxStage.initModality(Modality.APPLICATION_MODAL);
                                        vboxStage.initStyle(StageStyle.TRANSPARENT);
                                        controller.confirmAction(foodButton,false,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    FXMLLoader loader = new FXMLLoader();
                                    loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                    VBox vbox = null;
                                    try {
                                        vbox = loader.load();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                    ConfirmController controller = loader.getController();
                                    Scene vboxScene = new Scene(vbox);
                                    Stage vboxStage = new Stage();
                                    vbox.setPrefHeight(hbox.getHeight()*0.5);
                                    vbox.setPrefWidth(hbox.getWidth()*0.5);
                                    vboxStage.setScene(vboxScene);
                                    vboxStage.initOwner(stage);
                                    vboxStage.initModality(Modality.APPLICATION_MODAL);
                                    vboxStage.initStyle(StageStyle.TRANSPARENT);
                                    controller.confirmAction(foodButton,null,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                }
                            });
                            foodButtons.add(foodButton);
                            flowPane.setPrefWidth(hbox.getPrefWidth()*0.65);
                            flowPane.setPrefHeight((hbox.getPrefHeight()/5)*4);
                            flowPane.getChildren().add(foodButton);
                        }
                    }
                });
                nextPageButton.setOnAction(od-> {
                    if(tGoodsDtos.size()>(page.getValue()*pageSize.getValue())){
                        foodButtons.remove(0,foodButtons.size());
                        flowPane.getChildren().remove(0,flowPane.getChildren().size());
                        if(tGoodsDtos.size()-(page.getValue()*pageSize.getValue()) >= pageSize.getValue()){
                            page.set(page.getValue()+1);
                            selectedTGoodsDtos.removeAll(selectedTGoodsDtos);
                            System.out.println("ooooooooooooooooooo=" + page.getValue());
                            selectedTGoodsDtos.addAll(tGoodsDtos.subList((page.getValue()-1)*pageSize.getValue(),(page.getValue()-1)*pageSize.getValue()+pageSize.getValue()));

                        }
                        else{
                            page.set(page.getValue()+1);
                            selectedTGoodsDtos.removeAll(selectedTGoodsDtos);
                            selectedTGoodsDtos.addAll(tGoodsDtos.subList((page.getValue()-1)*pageSize.getValue(),tGoodsDtos.size()));
                        }
                        for(TGoodsDto  tGoodsDto: selectedTGoodsDtos  ){
                            Button foodButton = new Button();
                            foodButton.setId(tGoodsDto.getGoodsNo());
                            SqlSession foodSession = SqlSessionUtil.getSqlSession();
                            //根据菜品编号判断是否沽清
                            List<TGoodsDto> tGoodsDtosList =  foodSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(tGoodsDto.getGoodsNo());
                            if(tGoodsDtosList != null && tGoodsDtosList.size() > 0){
                                foodButton.setText(tGoodsDto.getGoodName1()+"\n(暫停)");
                            }
                            else{
                                foodButton.setText(tGoodsDto.getGoodName1());
                            }
                            foodButton.setMnemonicParsing(false);
                            foodButton.setStyle("-fx-pref-height:" + flowPane.getPrefHeight()/4 +  ";-fx-font-size:30; -fx-font-weight: bolder; -fx-pref-width:"+ flowPane.getPrefWidth()*0.313);
                            foodButton.setOnAction(event->{
                                SqlSession foodButtonSession = SqlSessionUtil.getSqlSession();
                                List<TGoodsDto> tGoodsDtosList1 = foodButtonSession.getMapper(TGoodsDtoMapper.class).existOutOfStockTGoodsDto(foodButton.getId());
                                try {
                                    if(tGoodsDtosList1 == null || tGoodsDtosList1.size()==0){
                                        FXMLLoader loader = new FXMLLoader();
                                        loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                        VBox vbox =  loader.load();
                                        ConfirmController controller = loader.getController();
                                        Scene vboxScene = new Scene(vbox);
                                        Stage vboxStage = new Stage();
                                        vbox.setPrefHeight(hbox.getHeight()*0.5);
                                        vbox.setPrefWidth(hbox.getWidth()*0.5);
                                        vboxStage.setScene(vboxScene);
                                        vboxStage.initOwner(stage);
                                        vboxStage.initModality(Modality.APPLICATION_MODAL);
                                        vboxStage.initStyle(StageStyle.TRANSPARENT);
                                        controller.confirmAction(foodButton,true,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                    }
                                    else{
                                        FXMLLoader loader = new FXMLLoader();
                                        loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                        VBox vbox = null;
                                        vbox = loader.load();
                                        ConfirmController controller = loader.getController();
                                        Scene vboxScene = new Scene(vbox);
                                        Stage vboxStage = new Stage();
                                        vbox.setPrefHeight(hbox.getHeight()*0.5);
                                        vbox.setPrefWidth(hbox.getWidth()*0.5);
                                        vboxStage.setScene(vboxScene);
                                        vboxStage.initOwner(stage);
                                        vboxStage.initModality(Modality.APPLICATION_MODAL);
                                        vboxStage.initStyle(StageStyle.TRANSPARENT);
                                        controller.confirmAction(foodButton,false,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    FXMLLoader loader = new FXMLLoader();
                                    loader.setLocation(Main.class.getResource("controller/view/ConfirmView.fxml"));
                                    VBox vbox = null;
                                    try {
                                        vbox = loader.load();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                    ConfirmController controller = loader.getController();
                                    Scene vboxScene = new Scene(vbox);
                                    Stage vboxStage = new Stage();
                                    vbox.setPrefHeight(hbox.getHeight()*0.5);
                                    vbox.setPrefWidth(hbox.getWidth()*0.5);
                                    vboxStage.setScene(vboxScene);
                                    vboxStage.initOwner(stage);
                                    vboxStage.initModality(Modality.APPLICATION_MODAL);
                                    vboxStage.initStyle(StageStyle.TRANSPARENT);
                                    controller.confirmAction(foodButton,null,TgoodsTab,tGoodsDto.getGoodName1(),vboxStage,vbox);
                                }
                            });
                            foodButtons.add(foodButton);
                            flowPane.setPrefWidth(hbox.getPrefWidth()*0.65);
                            flowPane.setPrefHeight((hbox.getPrefHeight()/5)*4);
                            flowPane.getChildren().add(foodButton);
                        }
                    }
                });
                nextPageButton.setPrefHeight(hbox.getPrefHeight()/5);
                nextPageButton.setPrefWidth(hbox.getPrefWidth()*0.65/2);
                previousPageButton.setPrefHeight(hbox.getPrefHeight()/5);
                previousPageButton.setPrefWidth(hbox.getPrefWidth()*0.65/2);
                pageButtonHbox.setVisible(true);
            });
            this.topButtonVbox.getChildren().add(button);
        }

    }






}






