package pos.dongwang.controller;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.ibatis.session.SqlSession;
import pos.dongwang.Main;
import pos.dongwang.dao.*;
import pos.dongwang.dto.IsBilledDto;
import pos.dongwang.dto.PosOrderDto;
import pos.dongwang.dto.TopButtonDto;
import pos.dongwang.enums.LogTypeEnum;
import pos.dongwang.enums.OrderType;
import pos.dongwang.enums.PrintStateEnum;
import pos.dongwang.httpUtil.HttpUtil;
import pos.dongwang.mapper.OrderHangListMapper;
import pos.dongwang.model.PosOrder;
import pos.dongwang.model.TGoods;
import pos.dongwang.model.TopButton;
import pos.dongwang.mybatis.MyBatisSqlSessionFactory;
import pos.dongwang.print.GetPrintOrReportStr;
import pos.dongwang.print.PrintRxTx;
import pos.dongwang.print.PrintRxTxVirtaul;
import pos.dongwang.properties.ReadProperties;
import pos.dongwang.util.AppUtils;
import pos.dongwang.util.CommonPrintTask;
import pos.dongwang.util.SendUtils;
import pos.dongwang.util.SqlSessionUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class OrderListController{

    @FXML
    private TableView<PosOrder> posOrderTab;

    @FXML
    private TableColumn<PosOrder, String> itemDescCol;

    @FXML
    private TableColumn<PosOrder, String> itemTimeCol;

    @FXML
    private TableColumn<PosOrder, String> itemQtyCol;

    @FXML
    private Button printBtn;

    @FXML
    private Button  hangBtn;

    @FXML
    private Label orderCountTab;

    @FXML
    private Label tableNumLab;


    @FXML
    private Label orderTypeLab;


    private Main main;

    private Integer showCount;

    private ObservableList<PosOrder> posOrderData = FXCollections.observableArrayList();


    private VBox posOrderBox;

    private Stage primaryStage;


    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public OrderListController() {
    }

    public TableView<PosOrder> getPosOrderTab() {
        return posOrderTab;
    }

    public void setPosOrderTab(TableView<PosOrder> posOrderTab) {
        this.posOrderTab = posOrderTab;
    }

    public TableColumn<PosOrder, String> getItemDescCol() {
        return itemDescCol;
    }

    public void setItemDescCol(TableColumn<PosOrder, String> itemDescCol) {
        this.itemDescCol = itemDescCol;
    }

    public TableColumn<PosOrder, String> getItemTimeCol() {
        return itemTimeCol;
    }

    public void setItemTimeCol(TableColumn<PosOrder, String> itemTimeCol) {
        this.itemTimeCol = itemTimeCol;
    }

    public TableColumn<PosOrder, String> getItemQtyCol() {
        return itemQtyCol;
    }

    public void setItemQtyCol(TableColumn<PosOrder, String> itemQtyCol) {
        this.itemQtyCol = itemQtyCol;
    }

    /**
     * 初始化方法
     */
    @FXML
    private void initialize() {

        itemDescCol.setCellValueFactory(cellData -> cellData.getValue().name1Property());
        itemTimeCol.setCellValueFactory(cellData -> cellData.getValue().print_msgProperty());
        itemQtyCol.setCellValueFactory(cellData -> cellData.getValue().seal_countProperty().asString());

    }

    public void setMain(Main main, ObservableList<PosOrder> posOrderData, VBox posOrderBox,Stage primaryStage) {
        Long orderCount = PosOrderDao.getOrderCountsByBillNo(posOrderData.get(0).getBill_no());
        this.main = main;
        this.posOrderData = posOrderData;
        this.posOrderBox = posOrderBox;
        this.primaryStage = primaryStage;
        tableNumLab.textProperty().setValue("檯号:" + posOrderData.get(0).table_noProperty().getValue() + " ,时间:" + posOrderData.get(0).rt_op_timeProperty().getValue()+" ,次數:"+orderCount +" , 菜品記錄數:"+ posOrderData.size());
        tableNumLab.setWrapText(true);
        orderTypeLab.setText(posOrderData.get(0).getOrder_type());
        //orderCountTab.setText("次數:"+ orderCount);
        orderCountTab.setText("人數:"+posOrderData.get(0).person_numProperty().getValue());
        posOrderTab.setItems(this.posOrderData);
        this.showCount = main.getShowcount();

      /*  posOrderTab.setRowFactory(new Callback<TableView<TGoods>, TableRow<TGoods>>() {
            @Override
            public TableRow<TGoods> call(TableView<TGoods> param) {
                return new OrderListController(param);
            }
        });*/





    }


   /* public OrderListController(TableView<TGoods> param) {
        super();
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    if (event.getButton().equals(MouseButton.PRIMARY)
                            && event.getClickCount() == 1
                            ) {
                     ;
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
*/
    @FXML
    public  void removeOrderData() {
        try {
            printBtn.setDisable(true);
            Map<Long,List<PosOrderDto>> posOrderDtos = new HashMap<>();
            IsBilledDto isBilledDto = PosOrderDao.isBilled(posOrderData.get(0).getBill_no(), posOrderData.get(0).getType(),posOrderData.get(0).getSub_no());
            if(AppUtils.isNotBlank(isBilledDto) && AppUtils.isNotBlank(isBilledDto.getLeaveDate() )){
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Main.class.getResource("controller/view/ConfirmPrintView.fxml"));
                Rectangle2D primaryScreenBounds = main.getPrimaryScreenBounds();
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
                        ObservableList<PosOrder> needPrintDatas = FXCollections.observableArrayList();
                        if(this.posOrderData.size() >showCount) {
                            needPrintDatas.addAll(posOrderData.subList(0,showCount));
                        }
                        else {
                            needPrintDatas.addAll(posOrderData);
                        }
                        if(main.getSelectedOrderType().equals(OrderType.HANG.getValue())){
                            Integer index =  main.getHangOrderBoxs().indexOf(posOrderData);
                            if(index>=0){
                                if(posOrderData.get(0).getBill_no().equals(main.getHangOrderBoxs().get(index).get(0).getBill_no())){
                                    /*if(posOrderData.size()>showCount){
                                        main.getHangOrderBoxs().get(index).remove(0,showCount);
                                    }*/
                                    if(posOrderData.size()<showCount){
                                        SqlSession sqlSession = SqlSessionUtil.getSqlSession();
                                        OrderHangListDao.deleteOrderHangListByRefNum(sqlSession,posOrderData.get(posOrderData.size()-1).getBill_no(),posOrderData.get(posOrderData.size()-1).getHang_date());
                                       // main.getHangOrderBoxs().remove(posOrderData);
                                    /*    main.getFlowPane().getChildren().remove(0);*/
                                    }

                                }
                                main.setHangBoxTotal("掛起"+"(" +  main.getOrderHangCount() + ")");
                            }
                        }
                        if(needPrintDatas != null && needPrintDatas.size() > 0) {
                            List<String> list = new LinkedList<String>();
                            needPrintDatas.forEach(posOrder -> {

    //                String printIitem  = posOrder.getName1() + (AppUtils.isNotBlank(posOrder.getPrint_msg())? "("+ posOrder.getPrint_msg()+" )":"");

                                int k = posOrder.getName1().length()%8;
                                int m = posOrder.getName1().length()/8;
                                if(m > 0){
                                    for(int i = 0;i<m;i++){
                                        if(i == 0){
                                            list.add(posOrder.getName1().substring(0,8) + "|R" + posOrder.getSeal_count() + "|R" + posOrder.getPrint_msg());
                                        }
                                        else if(i != m ){
                                            list.add(posOrder.getName1().substring(i*8,i*8+8) + "|R" + "" + "|R" + "");
                                        }
                                        else{
                                            list.add(posOrder.getName1().substring(i*8,m*8) + "|R" + "" + "|R" + "");
                                        }
                                    }
                                    if(k>0){
                                        list.add(posOrder.getName1().substring(m*8,posOrder.getName1().length()) + "|R" + "" + "|R" + "");
                                    }

                                }
                                else{
                                    list.add(posOrder.getName1() + "|R" + posOrder.getSeal_count() + "|R" + posOrder.getPrint_msg());
                                }
                                if(AppUtils.isNotBlank(posOrder.getPrint_msg())){
                                    list.add("訊息:" + posOrder.getPrint_msg() + "|R" + "" + "|R" + "");
                                }
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
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {
                                    main.showWarningView("錯誤", "打印機異常",primaryStage);
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                printBtn.setDisable(false);
                                return;
                            }
                            if (AppUtils.isNotBlank(printMsg)) {
                                try {
                                    main.showWarningView("錯誤", "打印機異常",primaryStage);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                printBtn.setDisable(false);
                                return;
                            }
                            else {
                                try {
                                    PrintRxTxVirtaul.getInstance().localPrint(print);
                                } catch (Exception e) {
                                    try {
                                        main.showWarningView("錯誤", "打印機異常",primaryStage);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                    printBtn.setDisable(false);
                                    return;
                                }
                                if (this.posOrderData.size() > showCount) {
                                    main.removePosOrderData(false, this.posOrderData, posOrderBox,posOrderTab);
                                    tableNumLab.textProperty().setValue("檯号:" + posOrderData.get(0).table_noProperty().getValue() + " ,时间:" + posOrderData.get(0).rt_op_timeProperty().getValue() + " ,人數:" + posOrderData.get(0).person_numProperty().getValue() + " , 菜品數量:" + posOrderData.size());
                                } else {
                                    main.removePosOrderData(true, this.posOrderData, posOrderBox,posOrderTab);
                                }
                                if(posOrderDtos != null && posOrderDtos.size()>0){
                                    Iterator<Long> idxs =  posOrderDtos.keySet().iterator();
                                    while(idxs.hasNext()){
                                        PosOrderDao.otheridxs.remove(idxs.next());
                                    }
                                }
                            }
                        }
                        stage.close();
                    });
                    Button cancleButton = (Button) buttomFlowPane.getChildren().get(1);
                    cancleButton.setPrefWidth(primaryScreenBounds.getWidth()/3/4);
                    cancleButton.setPrefHeight(primaryScreenBounds.getHeight()/3/6);
                    cancleButton.setOnAction(event -> {
                        SqlSession sqlSession = SqlSessionUtil.getSqlSession();
                        OrderHangListDao.deleteOrderHangListByRefNum(sqlSession,posOrderData.get(posOrderData.size()-1).getBill_no(),posOrderData.get(posOrderData.size()-1).getHang_date());
                        main.removeLeavePosOrderData(true,this.posOrderData, posOrderBox,posOrderTab);
                       /* if(main.getFlowPane().getChildren() != null && main.getFlowPane().getChildren().size()>0){
                            main.getFlowPane().getChildren().remove(0);
                            main.setOrderHangCount(main.getOrderHangCount()-1);
                        }*/
                        main.setHangBoxTotal("掛起"+"(" +  main.getOrderHangCount() + ")");
                        stage.close();
                        printBtn.setDisable(false);
                        return;
                    });
                    stage.setScene(new Scene(vBox));
                    stage.initOwner(primaryStage);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.TRANSPARENT);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();

                }
               /* main.crateAlert("提示", "此臺已結賬", "此臺已結賬，不再打印！",primaryStage);
                main.removePosOrderData(true,this.posOrderData, posOrderBox,posOrderTab);
                return;*/
            }
            else{
              /*  if(main.getSelectedOrderType().equals(OrderType.HANG.getValue())){
                    Integer index =  main.getHangOrderBoxs().indexOf(posOrderData);
                    if(index>=0){
                        SqlSession sqlSession = SqlSessionUtil.getSqlSession();
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
                            Date nowDate = new Date();
                            for (PosOrder posOrder : posOrderData) {
                                PosOrderDao.updatePrintStateWithoutOldPrintState(sqlSession, PrintStateEnum.PRINTED, posOrder.getBill_no(), posOrder.getOrder_idx(), posOrder.getType());
                                posOrder.setPrint_state(PrintStateEnum.PRINTED.getValue());
                                RtLogDao.insertLog(posOrder.getBarcode(), simpleDateFormat.format(nowDate), simpleTimeFormat.format(nowDate), "9999999", LogTypeEnum.KVSPRINT.getValue(), "N", String.valueOf(posOrder.getOrder_idx()), posOrder.getBill_no(), posOrder.getTable_no(), posOrder.getGoodsno());
                            }
                            OrderHangListDao.deleteOrderHangListByRefNum(sqlSession,posOrderData.get(0).getBill_no());
                        } catch (Exception e) {
                            e.printStackTrace();
                            sqlSession.rollback();
                            main.showWarningView("錯誤", "程序出錯",primaryStage);
                            printBtn.setDisable(false);
                            return;
                        }
                            main.executor.execute((new Runnable() {
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
                        for(int i = 0;i<main.getHangOrderBoxs().size();i++){
                            if(posOrderData.get(0).getBill_no().equals(main.getHangOrderBoxs().get(i).get(0).getBill_no())){
                                main.getFlowPane().getChildren().remove(0);
                            }
                            if(main.getSelectedHangOrderBoxs() != null && main.getSelectedHangOrderBoxs().size() > 0){
                                if(posOrderData.get(0).getBill_no().equals(main.getSelectedHangOrderBoxs().get(0).get(0).getBill_no())){
                                    main.getSelectedHangOrderBoxs().remove(0);
                                }
                            }
                        }
                        main.getHangOrderBoxs().remove(posOrderData);
                        main.setOrderHangCount(main.getOrderHangCount()-1);
                        main.setHangBoxTotal("掛起"+"(" +  main.getOrderHangCount() + ")");
                    }
                }*/
             /*   String tabNum = PosOrderDao.getTableNumByOrderIdx(posOrderData.get(0).getOrder_idx());
                if(!posOrderData.get(0).getTable_no().equals(tabNum)){
                    main.crateAlert("提示", "此臺已轉檯", "此臺已轉檯，不能列印！",primaryStage);
                    main.removePosOrderData(true, this.posOrderData, posOrderBox,posOrderTab);
                    return;
                }*/
                ObservableList<PosOrder> needPrintDatas = FXCollections.observableArrayList();
                if(this.posOrderData.size() >showCount) {
                    needPrintDatas.addAll(posOrderData.subList(0,showCount));
                }
                else{
                    needPrintDatas.addAll(posOrderData);
                }
                if(needPrintDatas != null && needPrintDatas.size() > 0) {
                    List<String> list = new LinkedList<String>();
                    needPrintDatas.forEach(posOrder -> {

    //                String printIitem  = posOrder.getName1() + (AppUtils.isNotBlank(posOrder.getPrint_msg())? "("+ posOrder.getPrint_msg()+" )":"");

                        int k = posOrder.getName1().length()%8;
                        int m = posOrder.getName1().length()/8;
                        if(m > 0){
                            for(int i = 0;i<m;i++){
                                if(i == 0){
                                    list.add(posOrder.getName1().substring(0,8) + "|R" + posOrder.getSeal_count() + "|R" + posOrder.getPrint_msg());
                                }
                                else if(i != m ){
                                    list.add(posOrder.getName1().substring(i*8,i*8+8) + "|R" + "" + "|R" + "");
                                }
                                else{
                                    list.add(posOrder.getName1().substring(i*8,m*8) + "|R" + "" + "|R" + "");
                                }
                            }
                            if(k>0){
                                list.add(posOrder.getName1().substring(m*8,posOrder.getName1().length()) + "|R" + "" + "|R" + "");
                            }

                        }
                        else{
                            list.add(posOrder.getName1() + "|R" + posOrder.getSeal_count() + "|R" + posOrder.getPrint_msg());
                        }
                        if(AppUtils.isNotBlank(posOrder.getPrint_msg())){
                            list.add("訊息:" + posOrder.getPrint_msg() + "|R" + "" + "|R" + "");
                        }
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
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        main.showWarningView("錯誤", "打印機異常",primaryStage);
                        printBtn.setDisable(false);
                        return;
                    }
                    if (AppUtils.isNotBlank(printMsg)) {
                        //main.crateAlert("錯誤", "打印機異常", printMsg,primaryStage);
                        main.showWarningView("錯誤提示","程序出錯",primaryStage);
                        printBtn.setDisable(false);
                        return;
                    }
                    else {
                        try {
                            PrintRxTxVirtaul.getInstance().localPrint(print);
                        }
                        catch (IOException s) {
                            s.printStackTrace();
                            //main.crateAlert("錯誤", "打印機異常", printMsg,primaryStage);
                            main.showWarningView("錯誤提示","程序出錯",primaryStage);
                            printBtn.setDisable(false);
                            return;
                        }
                        catch (Exception e) {
                           // main.crateAlert("錯誤", "打印機異常", e.getMessage(),primaryStage);
                            main.showWarningView("錯誤提示","程序出錯",primaryStage);
                            printBtn.setDisable(false);
                            return;
                        }
                        if(main.getSelectedOrderType().equals(OrderType.HANG.getValue())){
                           /* Integer index =  main.getHangOrderBoxs().indexOf(posOrderData);
                            if(index>=0){*/
                                SqlSession sqlSession = SqlSessionUtil.getSqlSession();
                               if(posOrderData.size() <= showCount ){
                                   OrderHangListDao.deleteOrderHangListByRefNum(sqlSession,posOrderData.get(posOrderData.size()-1).getBill_no(),posOrderData.get(posOrderData.size()-1).getHang_date());
                               }

                        }
                  /*      String refNum = posOrderData.get(0).getBill_no();
                        String subNo = posOrderData.get(0).getSub_no();
                        String inDate = posOrderData.get(0).getIn_date();*/
                        if (this.posOrderData.size() > showCount) {
                            main.removePosOrderData(false,this.posOrderData, posOrderBox,posOrderTab);
                            tableNumLab.textProperty().setValue("檯号:" + posOrderData.get(0).table_noProperty().getValue() + " ,时间:" + posOrderData.get(0).rt_op_timeProperty().getValue() + " ,人數:" + posOrderData.get(0).person_numProperty().getValue() + " , 菜品數量:" + posOrderData.size());
                        } else {
                            main.removePosOrderData(true, this.posOrderData, posOrderBox,posOrderTab);
                        }
                        if(posOrderDtos != null && posOrderDtos.size()>0){
                            Iterator<Long> idxs =  posOrderDtos.keySet().iterator();
                            while(idxs.hasNext()){
                                PosOrderDao.otheridxs.remove(idxs.next());
                            }
                        }
                       /* main.executor.execute((new Runnable() {
                            public void run() {
                                String outline = BranchDao.getOutlineByOutlet(ReadProperties.readStringByKey("outlet"));
                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("refNum", refNum));
                                params.add(new BasicNameValuePair("subRef", subNo));
                                params.add(new BasicNameValuePair("outline", outline));
                                params.add(new BasicNameValuePair("outlet", ReadProperties.readStringByKey("outlet")));
                                params.add(new BasicNameValuePair("opType", "FREE"));
                                params.add(new BasicNameValuePair("inDate", inDate));
                                SendUtils.sendRequest(ReadProperties.readStringByKey("messageUrl") + "/message/cancelOrFree", params);
                            }
                        }));*/
                        /*if(main.getSelectedOrderType().equals(OrderType.HANG.getValue())){
                            Integer index =  main.getHangOrderBoxs().indexOf(posOrderData);
                            if(index>=0){
                                SqlSession sqlSession = SqlSessionUtil.getSqlSession();
                                try {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
                                    Date nowDate = new Date();
                                    if(posOrderData.size()>showCount){
                                         List<PosOrder> needPrintPosorders = posOrderData.subList(0,showCount);
                                        for (PosOrder posOrder : needPrintPosorders) {
                                            PosOrderDao.updatePrintStateWithoutOldPrintState(sqlSession, PrintStateEnum.PRINTED, posOrder.getBill_no(), posOrder.getOrder_idx(), posOrder.getType());
                                            posOrder.setPrint_state(PrintStateEnum.PRINTED.getValue());
                                            RtLogDao.insertLog(posOrder.getBarcode(), simpleDateFormat.format(nowDate), simpleTimeFormat.format(nowDate), "9999999", LogTypeEnum.KVSPRINT.getValue(), "N", String.valueOf(posOrder.getOrder_idx()), posOrder.getBill_no(), posOrder.getTable_no(), posOrder.getGoodsno());
                                        }
                                    }
                                    else{
                                        for (PosOrder posOrder : posOrderData) {
                                            PosOrderDao.updatePrintStateWithoutOldPrintState(sqlSession, PrintStateEnum.PRINTED, posOrder.getBill_no(), posOrder.getOrder_idx(), posOrder.getType());
                                            posOrder.setPrint_state(PrintStateEnum.PRINTED.getValue());
                                            RtLogDao.insertLog(posOrder.getBarcode(), simpleDateFormat.format(nowDate), simpleTimeFormat.format(nowDate), "9999999", LogTypeEnum.KVSPRINT.getValue(), "N", String.valueOf(posOrder.getOrder_idx()), posOrder.getBill_no(), posOrder.getTable_no(), posOrder.getGoodsno());
                                        }
                                        OrderHangListDao.deleteOrderHangListByRefNum(sqlSession,posOrderData.get(0).getBill_no());
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    sqlSession.rollback();
                                    main.showWarningView("錯誤", "程序出錯",primaryStage);
                                    printBtn.setDisable(false);
                                    return;
                                }
                                main.executor.execute((new Runnable() {
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
                                for(int i = 0;i<main.getHangOrderBoxs().size();i++){
                                    if(posOrderData.get(0).getBill_no().equals(main.getHangOrderBoxs().get(i).get(0).getBill_no())){
                                       *//* if(posOrderData.size()>showCount){

                                        }
                                        main.getFlowPane().getChildren().remove(0);*//*
                                        if(posOrderData.size()>showCount) {
                                            // ObservableList<PosOrder> posOrderList = (ObservableList<PosOrder>) main.getFlowPane().getChildren().get(0);
                                            int dex = main.getHangOrderBoxs().indexOf(posOrderData);
                                            if(dex >= 0){
                                                ObservableList<PosOrder> hangPosOrderList =   main.getHangOrderBoxs().get(dex);
                                                hangPosOrderList.remove(0,showCount);
                                            }

                                        }
                                        else{
                                            main.getHangOrderBoxs().remove(posOrderData);
                                            main.setOrderHangCount(main.getOrderHangCount()-1);
                                        }
                                        main.setHangBoxTotal("掛起"+"(" +  main.getOrderHangCount() + ")");
                                    }
                                   *//* if(main.getSelectedHangOrderBoxs() != null && main.getSelectedHangOrderBoxs().size() > 0){
                                        if(posOrderData.get(0).getBill_no().equals(main.getSelectedHangOrderBoxs().get(0).get(0).getBill_no())){
                                            main.getSelectedHangOrderBoxs().remove(0);
                                        }
                                    }*//*
                                }
                              *//*  main.getHangOrderBoxs().remove(posOrderData);
                                main.setOrderHangCount(main.getOrderHangCount()-1);
                                main.setHangBoxTotal("掛起"+"(" +  main.getOrderHangCount() + ")");*//*
                            }
                        }*/
                    }
                }
            }

            printBtn.setDisable(false);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                main.showWarningView("錯誤", "程序出錯",primaryStage);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            printBtn.setDisable(false);
        }
    }

    @FXML
    public void saleOutFun() throws Exception {
        PosOrder selectPosOrder = posOrderTab.getSelectionModel().getSelectedItem();
        System.out.println(selectPosOrder.getName1());
        if (PosOrderDao.pauseItem(selectPosOrder)) {
            Platform.runLater(() -> {
                try {
                    main.showWarningView("暫停", "暫停成功", primaryStage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            String url = ReadProperties.readStringByKey("messageUrl") + "/message/stopOrStartSellingItem";
            if(AppUtils.isNotBlank(url)) {
                main.executor.execute((new Runnable() {
                    public void run() {
                        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                        nvps.add(new BasicNameValuePair("outline", selectPosOrder.getRegion_id()));
                        nvps.add(new BasicNameValuePair("itemCode", selectPosOrder.getGoodsno()));
                        nvps.add(new BasicNameValuePair("outlet", selectPosOrder.getBarcode()));
                        nvps.add(new BasicNameValuePair("staff", selectPosOrder.getStaff()));
                        nvps.add(new BasicNameValuePair("optionType", "STOP"));
                        sendRequest(url, nvps);
                    }
                }));
            }
        }
    }


    private void sendRequest(String url, List<NameValuePair> nvps) {
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
            protected Void call() throws Exception {
                HttpUtil.post(url, nvps);
                updateMessage("Finish");
                return null;
            }

        };

        new Thread(progressTask).start();
    }


    //掛起
    @FXML
    public void hangOrderData() throws IOException {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
        Map<Long,List<PosOrderDto>> posOrderDtos = new HashMap<>();
        IsBilledDto isBilledDto = PosOrderDao.isBilled(posOrderData.get(0).getBill_no(), posOrderData.get(0).getType(),posOrderData.get(0).getSub_no());
        if(AppUtils.isNotBlank(isBilledDto) && AppUtils.isNotBlank(isBilledDto.getLeaveDate())){
            main.showWarningView("提示", "此臺已離座，不能掛起！",primaryStage);
            return;
        }
        String checkBill = PosOrderDao.checkBilled(posOrderData.get(0).getBill_no(), posOrderData.get(0).getType(),posOrderData.get(0).getSub_no());
        if(AppUtils.isNotBlank(checkBill)){
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("controller/view/ConfirmPrintView.fxml"));
             primaryScreenBounds = main.getPrimaryScreenBounds();
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
                contentLabel.setText("此臺已結賬 掛起失敗,是否要列印");
                FlowPane buttomFlowPane = (FlowPane) vBox.getChildren().get(2);
                buttomFlowPane.setPrefWidth(primaryScreenBounds.getWidth()/3);
                buttomFlowPane.setPrefHeight(primaryScreenBounds.getHeight()/3/3);
                buttomFlowPane.setHgap(primaryScreenBounds.getWidth()/3/10);
                Button confirmButton = (Button) buttomFlowPane.getChildren().get(0);
                confirmButton.setPrefWidth(primaryScreenBounds.getWidth()/3/4);
                confirmButton.setPrefHeight(primaryScreenBounds.getHeight()/3/6);
                confirmButton.setOnAction(event -> {
                    ObservableList<PosOrder> needPrintDatas = FXCollections.observableArrayList();
                    if(this.posOrderData.size() >showCount) {
                        needPrintDatas.addAll(posOrderData.subList(0,showCount));
                    }
                    else{
                        needPrintDatas.addAll(posOrderData);
                    }
                    if(needPrintDatas != null && needPrintDatas.size() > 0) {
                        List<String> list = new LinkedList<String>();
                        needPrintDatas.forEach(posOrder -> {

                            //                String printIitem  = posOrder.getName1() + (AppUtils.isNotBlank(posOrder.getPrint_msg())? "("+ posOrder.getPrint_msg()+" )":"");

                            int k = posOrder.getName1().length()%8;
                            int m = posOrder.getName1().length()/8;
                            if(m > 0){
                                for(int i = 0;i<m;i++){
                                    if(i == 0){
                                        list.add(posOrder.getName1().substring(0,8) + "|R" + posOrder.getSeal_count() + "|R" + posOrder.getPrint_msg());
                                    }
                                    else if(i != m ){
                                        list.add(posOrder.getName1().substring(i*8,i*8+8) + "|R" + "" + "|R" + "");
                                    }
                                    else{
                                        list.add(posOrder.getName1().substring(i*8,m*8) + "|R" + "" + "|R" + "");
                                    }
                                }
                                if(k>0){
                                    list.add(posOrder.getName1().substring(m*8,posOrder.getName1().length()) + "|R" + "" + "|R" + "");
                                }

                            }
                            else{
                                list.add(posOrder.getName1() + "|R" + posOrder.getSeal_count() + "|R" + posOrder.getPrint_msg());
                            }
                            if(AppUtils.isNotBlank(posOrder.getPrint_msg())){
                                list.add("訊息:" + posOrder.getPrint_msg() + "|R" + "" + "|R" + "");
                            }
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
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                main.showWarningView("錯誤", "打印機異常",primaryStage);

                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            stage.close();
                            printBtn.setDisable(false);
                            return;
                        }
                        if (AppUtils.isNotBlank(printMsg)) {
                            try {
                                main.showWarningView("錯誤", "打印機異常",primaryStage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            stage.close();
                            printBtn.setDisable(false);
                            return;
                        }
                        else {
                            try {
                                PrintRxTxVirtaul.getInstance().localPrint(print);
                            } catch (Exception e) {
                                try {
                                    main.showWarningView("錯誤", "打印機異常",primaryStage);
                                    stage.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                printBtn.setDisable(false);
                                return;
                            }
                            if (this.posOrderData.size() > showCount) {
                                main.removePosOrderData(false, this.posOrderData, posOrderBox,posOrderTab);
                                tableNumLab.textProperty().setValue("檯号:" + posOrderData.get(0).table_noProperty().getValue() + " ,时间:" + posOrderData.get(0).rt_op_timeProperty().getValue() + " ,人數:" + posOrderData.get(0).person_numProperty().getValue() + " , 菜品數量:" + posOrderData.size());
                            } else {
                                main.removePosOrderData(true, this.posOrderData, posOrderBox,posOrderTab);
                            }
                            if(posOrderDtos != null && posOrderDtos.size()>0){
                                Iterator<Long> idxs =  posOrderDtos.keySet().iterator();
                                while(idxs.hasNext()){
                                    PosOrderDao.otheridxs.remove(idxs.next());
                                }
                            }
                        }
                    }
                    stage.close();
                });
                Button cancleButton = (Button) buttomFlowPane.getChildren().get(1);
                cancleButton.setPrefWidth(primaryScreenBounds.getWidth()/3/4);
                cancleButton.setPrefHeight(primaryScreenBounds.getHeight()/3/6);
                cancleButton.setOnAction(event -> {
                    main.removeLeavePosOrderData(true,this.posOrderData, posOrderBox,posOrderTab);
                    stage.close();
                    printBtn.setDisable(false);
                    return;
                });
                stage.setScene(new Scene(vBox));
                stage.initOwner(primaryStage);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();

            }
               /* main.crateAlert("提示", "此臺已結賬", "此臺已結賬，不再打印！",primaryStage);
                main.removePosOrderData(true,this.posOrderData, posOrderBox,posOrderTab);
                return;*/
        }

       /* String tabNum = PosOrderDao.getTableNumByOrderIdx(posOrderData.get(0).getOrder_idx());
        if(!posOrderData.get(0).getTable_no().equals(tabNum)){
            main.crateAlert("提示", "此臺已轉檯", "此臺已轉檯，不能掛起！",primaryStage);
            int isExist = main.getSelectedOrderBoxs().indexOf(posOrderData);
            if(isExist >=0) {
                main.getSelectedOrderBoxs().remove(isExist);
                if(main.getSelectedOrderType().equals(OrderType.ALLORDER.getValue())){
                    main.getFlowPane().getChildren().remove(isExist);
                }
            }
            if(posOrderData.get(0).getOrder_type().equals(OrderType.FIRSTORDER.getValue())){
                int index =  main.getSelectedFirstOrderBoxs().indexOf(posOrderData);
                if(index >= 0){
                    main.getSelectedFirstOrderBoxs().remove(index);
                    if(main.getSelectedOrderType().equals(OrderType.FIRSTORDER.getValue())){
                        main.getFlowPane().getChildren().remove(index);
                    }
                    index = main.getSelectedFirstOrderBoxs().indexOf(posOrderData);
                    if(index >= 0){
                        main.getSelectedOrderBoxs().remove(index);
                    }
                    else{
                        index = main.getOrderBoxs().indexOf(posOrderData);
                        if(index >= 0){
                            main.getOrderBoxs().remove(index);
                        }
                    }

                }
            }
            else if(posOrderData.get(0).getOrder_type().equals(OrderType.ADDORDER.getValue())){
                int index =  main.getSelectedAddOrderBoxs().indexOf(posOrderData);
                if(index >= 0){
                    main.getSelectedAddOrderBoxs().remove(index);
                    if(main.getSelectedOrderType().equals(OrderType.ADDORDER.getValue())){
                        main.getFlowPane().getChildren().remove(index);
                    }
                    index = main.getSelectedFirstOrderBoxs().indexOf(posOrderData);
                    if(index >= 0){
                        main.getSelectedOrderBoxs().remove(index);
                    }
                    else{
                        index = main.getOrderBoxs().indexOf(posOrderData);
                        if(index >= 0){
                            main.getOrderBoxs().remove(index);
                        }
                    }
                }
            }
            else if(posOrderData.get(0).getOrder_type().equals(OrderType.TAILORDER.getValue())){
                int index =  main.getSelectedLastOrderBoxsOrderBoxs().indexOf(posOrderData);
                if(index >= 0){
                    main.getSelectedLastOrderBoxsOrderBoxs().remove(index);
                    if(main.getSelectedOrderType().equals(OrderType.TAILORDER.getValue())){
                        main.getFlowPane().getChildren().remove(index);
                    }
                    index = main.getSelectedFirstOrderBoxs().indexOf(posOrderData);
                    if(index >= 0){
                        main.getSelectedOrderBoxs().remove(index);
                    }
                    else{
                        index = main.getOrderBoxs().indexOf(posOrderData);
                        if(index >= 0){
                            main.getOrderBoxs().remove(index);
                        }
                    }
                }
            }
            return;
        }*/
       else {
            FXMLLoader loader = null;
            VBox vbox = null;
            Stage dialogStage = null;
            FoodListController controller = null;
            Scene scene = null;
            try {
                loader = new FXMLLoader();
                loader.setLocation(Main.class.getResource("controller/view/ChooseHangTimeView.fxml"));
                vbox = loader.load();
                vbox.setPrefHeight(primaryScreenBounds.getHeight() / 3);
                vbox.setPrefWidth(primaryScreenBounds.getWidth() / 2);
                vbox.setStyle("-fx-background-color: darkgoldenrod; -fx-border-width: 1px; ");
                Label label = (Label) vbox.getChildren().get(0);
                label.setTextAlignment(TextAlignment.CENTER);
                label.setAlignment(Pos.CENTER);
                label.setPrefHeight(primaryScreenBounds.getHeight() / 3 / 3);
                label.setPrefWidth(primaryScreenBounds.getWidth() / 2);
                String hangTime = ReadProperties.readStringByKey("hangTime");
                String[] hangTimes = null;
                if (AppUtils.isNotBlank(hangTime)) {
                    hangTimes = hangTime.split(",");
                }
                FlowPane flowPane = (FlowPane) vbox.getChildren().get(1);
                flowPane.setPrefWidth(primaryScreenBounds.getWidth() / 2);
                flowPane.setPrefHeight(primaryScreenBounds.getHeight() / 3 / 3);
                flowPane.setHgap(10);
                flowPane.setAlignment(Pos.CENTER);
                dialogStage = new Stage();
                Stage finalDialogStage = dialogStage;
                if (hangTimes != null) {
                    for (int i = 0; i < hangTimes.length; i++) {
                        Button button = new Button();
                        button.setText(hangTimes[i] + "分鐘");
                        String hangDate = hangTimes[i];
                        button.setStyle("-fx-font-size: 30px; -fx-font-weight: bolder");
                        button.setPrefWidth(primaryScreenBounds.getWidth() / 2 / 3 - 10);
                        button.setPrefHeight(primaryScreenBounds.getHeight() / 3 / 3);
                        button.setOnAction(event -> {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                            Date date = new Date();
                            Date releaseDate = new Date(date.getTime() + (Integer.parseInt(hangDate) * 60) * 1000);
                            String refNum = posOrderData.get(0).getBill_no();
                            SqlSession sqlSession = SqlSessionUtil.getSqlSession();
                            try {
                                for (PosOrder posOrder : posOrderData) {
                                    PosOrderDao.updatePrintStateWithoutOldPrintState(sqlSession, PrintStateEnum.HANG, posOrder.getBill_no(), posOrder.getOrder_idx(), posOrder.getType());
                                    posOrder.setPrint_state(PrintStateEnum.HANG.getValue());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                sqlSession.rollback();
                                try {
                                    main.showWarningView("提示", "程序出錯", primaryStage);
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                return;
                            }
                            main.executor.execute((new Runnable() {
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
                            posOrderData.get(posOrderData.size()-1).setHang_date(releaseDate.getTime());
                            OrderHangListDao.addOrderHangList(refNum, date, releaseDate,releaseDate.getTime());
                            int isExist = main.getSelectedOrderBoxs().indexOf(posOrderData);
                            if (isExist >= 0) {
                                main.getSelectedOrderBoxs().remove(isExist);
                                if (main.getSelectedOrderType().equals(OrderType.ALLORDER.getValue())) {
                                    main.getFlowPane().getChildren().remove(isExist);
                                }
                            }
                            if (posOrderData.get(0).getOrder_type().equals(OrderType.FIRSTORDER.getValue())) {
                                int index = main.getSelectedFirstOrderBoxs().indexOf(posOrderData);
                                if (index >= 0) {
                                    main.getSelectedFirstOrderBoxs().remove(index);
                                    if (main.getSelectedOrderType().equals(OrderType.FIRSTORDER.getValue())) {
                                        main.getFlowPane().getChildren().remove(index);
                                    }
                                    index = main.getSelectedFirstOrderBoxs().indexOf(posOrderData);
                                    if (index >= 0) {
                                        main.getSelectedOrderBoxs().remove(index);
                                    } else {
                                        index = main.getOrderBoxs().indexOf(posOrderData);
                                        if (index >= 0) {
                                            main.getOrderBoxs().remove(index);
                                        }
                                    }

                                }
                            } else if (posOrderData.get(0).getOrder_type().equals(OrderType.ADDORDER.getValue())) {
                                int index = main.getSelectedAddOrderBoxs().indexOf(posOrderData);
                                if (index >= 0) {
                                    main.getSelectedAddOrderBoxs().remove(index);
                                    if (main.getSelectedOrderType().equals(OrderType.ADDORDER.getValue())) {
                                        main.getFlowPane().getChildren().remove(index);
                                    }
                                    index = main.getSelectedFirstOrderBoxs().indexOf(posOrderData);
                                    if (index >= 0) {
                                        main.getSelectedOrderBoxs().remove(index);
                                    } else {
                                        index = main.getOrderBoxs().indexOf(posOrderData);
                                        if (index >= 0) {
                                            main.getOrderBoxs().remove(index);
                                        }
                                    }
                                }
                            } else if (posOrderData.get(0).getOrder_type().equals(OrderType.TAILORDER.getValue())) {
                                int index = main.getSelectedLastOrderBoxsOrderBoxs().indexOf(posOrderData);
                                if (index >= 0) {
                                    main.getSelectedLastOrderBoxsOrderBoxs().remove(index);
                                    if (main.getSelectedOrderType().equals(OrderType.TAILORDER.getValue())) {
                                        main.getFlowPane().getChildren().remove(index);
                                    }
                                    index = main.getSelectedFirstOrderBoxs().indexOf(posOrderData);
                                    if (index >= 0) {
                                        main.getSelectedOrderBoxs().remove(index);
                                    } else {
                                        index = main.getOrderBoxs().indexOf(posOrderData);
                                        if (index >= 0) {
                                            main.getOrderBoxs().remove(index);
                                        }
                                    }
                                }
                            }
                            main.getHangOrderBoxs().add(posOrderData);
                            main.setOrderHangCount(main.getOrderHangCount() + 1);
                            main.setHangBoxTotal("掛起(" + main.getOrderHangCount() + ")");
                            CommonPrintTask.insertPosOrdersToVBox(main);
                            CommonPrintTask.insertPosOrdersToCorrespondingBox(main);
                            finalDialogStage.close();
                        });
                        flowPane.getChildren().add(button);
                    }
                }
                HBox hbox = (HBox) vbox.getChildren().get(2);
                hbox.setPrefWidth(primaryScreenBounds.getWidth() / 2);
                hbox.setPrefHeight(primaryScreenBounds.getHeight() / 3 / 3);
                hbox.setAlignment(Pos.CENTER);
                Button button = new Button();
                button.setText("取消");
                button.setPrefHeight(primaryScreenBounds.getHeight() / 3 / 3);
                button.setPrefWidth(primaryScreenBounds.getWidth() / 2 / 3);

                button.setOnAction(event -> {
                    finalDialogStage.close();
                });
                hbox.getChildren().add(button);
                dialogStage.setResizable(false);
                dialogStage.setMinWidth(800);
                dialogStage.setMaxHeight(700);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.initStyle(StageStyle.TRANSPARENT);
                dialogStage.initOwner(primaryStage);
                scene = new Scene(vbox);
                dialogStage.setScene(scene);
                dialogStage.showAndWait();

                /*vBox.setPrefWidth(100);
                vBox.setPrefHeight(200);*/


            } catch (IOException e1) {
                e1.printStackTrace();
                main.showWarningView("提示", "程序出錯", primaryStage);
            }

        }
    }


}





