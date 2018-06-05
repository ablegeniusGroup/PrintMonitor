package pos.dongwang.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.ibatis.session.SqlSession;
import pos.dongwang.Main;
import pos.dongwang.dao.BranchDao;
import pos.dongwang.dao.TGoodsDao;
import pos.dongwang.dto.BranchDto;
import pos.dongwang.dto.TGoodsDto;
import pos.dongwang.httpUtil.HttpUtil;
import pos.dongwang.mapper.BranchMapper;
import pos.dongwang.mapper.TGoodsDtoMapper;
import pos.dongwang.model.TGoods;
import pos.dongwang.mybatis.MyBatisSqlSessionFactory;
import pos.dongwang.properties.ReadProperties;
import pos.dongwang.util.AppUtils;
import pos.dongwang.util.SendUtils;
import pos.dongwang.util.SqlSessionUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lodi on 2017/11/17.
 */
public class ConfirmController {


    @FXML
    private ToolBar toolBar;

    @FXML
    private AnchorPane titleAnchorPane;

    @FXML
    private AnchorPane contentAnchorPane;

    @FXML
    private Label contentLabel;

    private ObservableList<TGoods> outOfStockTgoodsData = FXCollections.observableArrayList();


    public void confirmAction(Button button,Boolean isPause,TableView<TGoods> TgoodsTab,String foodName,Stage stage,VBox vbox){
        try {
            titleAnchorPane.setStyle("-fx-pref-height:  "+ vbox.getPrefHeight()*0.2);
            titleAnchorPane.setTranslateX(vbox.getPrefWidth()*0.4);
            contentAnchorPane.setStyle("-fx-pref-height:  "+ + vbox.getPrefHeight()*0.3);
            contentAnchorPane.setTranslateX(vbox.getPrefWidth()*0.1);
            if(isPause != null){
                if(isPause){
                    contentLabel.setText("確定要沽清該菜品嗎");
                }
                else if(!isPause){
                    contentLabel.setText("確定要啓用該菜品嗎");
                }
                Button confirmButton = new Button();
                confirmButton.setText("確定");
                confirmButton.setTranslateX(vbox.getPrefWidth()*0.1);
                confirmButton.setStyle("-fx-background-color: bisque;-fx-pref-width: "+vbox.getPrefWidth()*0.3 );
                confirmButton.setOnAction(event-> {
                            SqlSession sqlSession = SqlSessionUtil.getSqlSession();
                            String url = ReadProperties.readStringByKey("messageUrl") + "/message/stopOrStartSellingItem";
                            String outlet = ReadProperties.readStringByKey("outlet");
                            String outline = sqlSession.getMapper(BranchMapper.class).getLineByBarcode(outlet);
                            if (AppUtils.isNotBlank(outline)) {
                                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                                nvps.add(new BasicNameValuePair("outline", outline));
                                nvps.add(new BasicNameValuePair("itemCode", button.getId()));
                                nvps.add(new BasicNameValuePair("outlet", outlet));
                                if (isPause) {
                                    // boolean isSuccess = TGoodsDao.setOutOfStockTGoodsDto(button.getId());
                                    try {
                                        sqlSession.getMapper(TGoodsDtoMapper.class).insertOutOfStockTGoodsDto(button.getId());
                                        sqlSession.getMapper(TGoodsDtoMapper.class).updateTGoodsPause(button.getId(), "TRUE");
                                        sqlSession.commit();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        sqlSession.rollback();
                                        throw e;
                                    }
                                    if(!button.getText().contains("(暫停)")){
                                        button.setText(button.getText() + "\n(暫停)");
                                    }
                                    List<TGoodsDto> tGoodsDtos1 = sqlSession.getMapper(TGoodsDtoMapper.class).getOutOfStockTGoodsDto();
                                    outOfStockTgoodsData = FXCollections.observableArrayList();
                                    for (TGoodsDto tGoodDto : tGoodsDtos1) {
                                        outOfStockTgoodsData.add(new TGoods(tGoodDto.getGoodsNo(), tGoodDto.getGoodName1()));
                                    }
                                    TgoodsTab.setItems(this.outOfStockTgoodsData);

                                    nvps.add(new BasicNameValuePair("optionType", "STOP"));
                                }
                                else {
                                    button.setText(foodName);
                                    try {
                                        sqlSession.getMapper(TGoodsDtoMapper.class).updateTGoodsPause(button.getId(), "FALSE");
                                        sqlSession.getMapper(TGoodsDtoMapper.class).updateSUSPENDIPause(button.getId(), "FALSE");
                                        sqlSession.commit();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        sqlSession.rollback();
                                        throw e;
                                    }
                                    // boolean isStart = TGoodsDao.startGoodsDto(button.getId());
                                    List<TGoodsDto> tGoodsDtos1 = sqlSession.getMapper(TGoodsDtoMapper.class).getOutOfStockTGoodsDto();
                                    outOfStockTgoodsData = FXCollections.observableArrayList();
                                    for (TGoodsDto tGoodDto : tGoodsDtos1) {
                                        outOfStockTgoodsData.add(new TGoods(tGoodDto.getGoodsNo(), tGoodDto.getGoodName1()));
                                    }
                                    TgoodsTab.setItems(this.outOfStockTgoodsData);
                                    nvps.add(new BasicNameValuePair("optionType", "START"));
                                }
                                SendUtils.sendRequest(url, nvps);
                                stage.close();
                            }
                });
                toolBar.getItems().add(confirmButton);
                Button cancelButton = new Button();
                cancelButton.setText("取消");
                cancelButton.setStyle("-fx-background-color: bisque;-fx-pref-width:" +  vbox.getPrefWidth()*0.3);
                cancelButton.setTranslateX(vbox.getPrefWidth()*0.2);
                cancelButton.setOnAction(event->{
                    stage.close();
                });
                toolBar.getItems().add(cancelButton);
                stage.showAndWait();
            }
            else{
                contentLabel.setText("程序出錯");
                Button confirmButton = new Button();
                confirmButton.setText("確定");
                confirmButton.setTranslateX(vbox.getPrefWidth()*0.4);
                confirmButton.setStyle("-fx-background-color: bisque;-fx-pref-width: "+vbox.getPrefWidth()*0.3 );
                confirmButton.setOnAction(event-> {
                   stage.close();
                });
                stage.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            contentLabel.setText("程序出錯");
            Button confirmButton = new Button();
            confirmButton.setText("確定");
            confirmButton.setTranslateX(vbox.getPrefWidth()*0.4);
            confirmButton.setStyle("-fx-background-color: bisque;-fx-pref-width: "+vbox.getPrefWidth()*0.3 );
            confirmButton.setOnAction(event-> {
                stage.close();
            });
            stage.showAndWait();
        }

    }

    public void confirmAction(Boolean isPause,TableView<TGoods> TgoodsTab, TGoods tGoods,Stage stage,VBox vbox,FlowPane flowPaneNode){
        try {
            titleAnchorPane.setStyle("-fx-pref-height:  "+ vbox.getPrefHeight()*0.2);
            titleAnchorPane.setTranslateX(vbox.getPrefWidth()*0.4);
            contentAnchorPane.setStyle("-fx-pref-height:  "+ + vbox.getPrefHeight()*0.3);
           // contentAnchorPane.setTranslateX(vbox.getPrefWidth()*0.1);
            if(isPause != null){
                if(isPause){
                    contentLabel.setText("確定要沽清該菜品嗎");
                }
                else if(!isPause){
                    contentLabel.setText("確定要啓用該菜品嗎");
                }
                Button confirmButton = new Button();
                confirmButton.setText("確定");
                confirmButton.setTranslateX(vbox.getPrefWidth()*0.1);
                confirmButton.setStyle("-fx-background-color: bisque;-fx-pref-width: "+vbox.getPrefWidth()*0.3 );
                confirmButton.setOnAction(event-> {
                    SqlSession sqlSession =  SqlSessionUtil.getSqlSession();
                    String url = ReadProperties.readStringByKey("messageUrl") + "/message/stopOrStartSellingItem";
                    String outlet = ReadProperties.readStringByKey("outlet");
                    String outline = sqlSession.getMapper(BranchMapper.class).getLineByBarcode(outlet);
                    if (AppUtils.isNotBlank(outline)) {
                        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                        nvps.add(new BasicNameValuePair("outline", outline));
                        nvps.add(new BasicNameValuePair("itemCode",tGoods.getGoodsNo()));
                        nvps.add(new BasicNameValuePair("outlet", outlet));
                        if (!isPause) {
                            // boolean isSuccess = TGoodsDao.setOutOfStockTGoodsDto(button.getId());
                            try {
                                sqlSession.getMapper(TGoodsDtoMapper.class).updateTGoodsPause(tGoods.getGoodsNo(), "FALSE");
                                sqlSession.getMapper(TGoodsDtoMapper.class).updateSUSPENDIPause(tGoods.getGoodsNo(), "FALSE");
                                sqlSession.commit();
                                ObservableList<Node> buttons = flowPaneNode.getChildren();
                                for(Node node : buttons){
                                    Button button = (Button) node;
                                    if(button.getId().equals(tGoods.getGoodsNo())){
                                        button.setText(tGoods.getGoodName1());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                sqlSession.rollback();
                                throw e;
                            }
                            TgoodsTab.getItems().removeAll(tGoods);
                           /* button.setText(button.getText() + "\n(暫停)");
                            List<TGoodsDto> tGoodsDtos1 = sqlSession.getMapper(TGoodsDtoMapper.class).getOutOfStockTGoodsDto();
                            outOfStockTgoodsData = FXCollections.observableArrayList();
                            for (TGoodsDto tGoodDto : tGoodsDtos1) {
                                outOfStockTgoodsData.add(new TGoods(tGoodDto.getGoodsNo(), tGoodDto.getGoodName1()));
                            }
                            TgoodsTab.setItems(this.outOfStockTgoodsData);*/

                            nvps.add(new BasicNameValuePair("optionType", "START"));
                        }
                        SendUtils.sendRequest(url, nvps);
                        stage.close();
                    }
                });
                toolBar.getItems().add(confirmButton);
                Button cancelButton = new Button();
                cancelButton.setText("取消");
                cancelButton.setStyle("-fx-background-color: bisque;-fx-pref-width:" +  vbox.getPrefWidth()*0.3);
                cancelButton.setTranslateX(vbox.getPrefWidth()*0.2);
                cancelButton.setOnAction(event->{
                    stage.close();
                });
                toolBar.getItems().add(cancelButton);
                stage.showAndWait();
            }
            else{
                contentLabel.setText("程序出錯");
                Button confirmButton = new Button();
                confirmButton.setText("確定");
                confirmButton.setTranslateX(vbox.getPrefWidth()*0.4);
                confirmButton.setStyle("-fx-background-color: bisque;-fx-pref-width: "+vbox.getPrefWidth()*0.3 );
                confirmButton.setOnAction(event-> {
                    stage.close();
                });
                stage.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            contentLabel.setText("程序出錯");
            Button confirmButton = new Button();
            confirmButton.setText("確定");
            confirmButton.setTranslateX(vbox.getPrefWidth()*0.4);
            confirmButton.setStyle("-fx-background-color: bisque;-fx-pref-width: "+vbox.getPrefWidth()*0.3 );
            confirmButton.setOnAction(event-> {
                stage.close();
            });
            stage.showAndWait();
        }

    }







   /* private void sendRequest(String url, List<NameValuePair> nvps) {
        Task<Void> progressTask = new Task<Void>() {
            @Override
            protected void succeeded() {

                super.succeeded();

                updateMessage("Succeeded");

            }
            @Override
            protected void cancelled() {

                super.cancelled();

                updateMessage("Cancelled");

            }
            @Override
            protected void failed() {

                super.failed();

                updateMessage("Failed");

            }
            @Override
            protected Void call() {
                try {
                    HttpUtil.post(url, nvps);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateMessage("Finish");
                return null;
            }

        };

        new Thread(progressTask).start();
    }*/

}
