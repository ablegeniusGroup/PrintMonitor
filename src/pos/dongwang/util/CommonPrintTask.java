package pos.dongwang.util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.ibatis.session.SqlSession;
import pos.dongwang.Main;
import pos.dongwang.dao.BranchDao;
import pos.dongwang.dao.OrderHangListDao;
import pos.dongwang.dao.PosOrderDao;
import pos.dongwang.dao.RtLogDao;
import pos.dongwang.dto.PosOrderDto;
import pos.dongwang.dto.RulerDto;
import pos.dongwang.enums.LogTypeEnum;
import pos.dongwang.enums.OrderType;
import pos.dongwang.enums.PrintStateEnum;
import pos.dongwang.mapper.OrderHangListMapper;
import pos.dongwang.model.PosOrder;
import pos.dongwang.mybatis.MyBatisSqlSessionFactory;
import pos.dongwang.print.GetPrintOrReportStr;
import pos.dongwang.print.PrintRxTxVirtaul;
import pos.dongwang.properties.ReadProperties;

import javax.swing.text.TabableView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lodi on 2017/11/22.
 */
public class CommonPrintTask {

    private static CommonPrintTask  instance;
    public static boolean isInit = false;
    public static boolean isInitHang = false;
    public static  boolean isPing = true;
    private static ExecutorService executor = Executors.newFixedThreadPool(3);
    private CommonPrintTask  (){}

    public static CommonPrintTask  getInstance() {
        if (instance == null) {
            instance = new CommonPrintTask ();
        }
        return instance;
    }




    public static synchronized void execute(Main main, String receivePrinter, Integer showCount, String timeInterval) {
      // testPing(main);
       SqlSession sqlSession = SqlSessionUtil.getSqlSession();
       Integer size = PosOrderDao.otheridxs.size();
        Date date = new Date();
        try {
            //自動刪除超時的掛起記錄
            OrderHangListDao.autoDeleteOrderHangListByRefNum(sqlSession,date.getTime());
            Map<String, List<PosOrderDto>> posmap = new HashMap<>();
            Map<String, List<PosOrderDto>> posHangMap = new HashMap<>();
            String[] printers = null;
            if (receivePrinter != null && !"".equals(receivePrinter.trim())) {
                printers = receivePrinter.split(",");
            }
            if (!isInit) {
                posmap = PosOrderDao.getInstance().getPosOrderListInit(Arrays.asList(printers), timeInterval,date);
                System.out.println("--------------------------------------");
                isInit = true;

            } else {
                posmap = PosOrderDao.getInstance().getPosOrderList(Arrays.asList(printers), timeInterval,date);
                System.out.println("**********************************************");
            }
            if(!isInitHang){
                 posHangMap = PosOrderDao.getInstance().getHangPosOrderList(Arrays.asList(printers), timeInterval,date);
                if(AppUtils.isNotBlank(posHangMap)){
                    main.setHangBoxTotal("掛起("+posHangMap.size()+")");
                    main.setOrderHangCount(posHangMap.size());
                    Iterator<Map.Entry<String, List<PosOrderDto>>> it = posHangMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, List<PosOrderDto>> entry = it.next();
                        System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
                        List<PosOrderDto> posmaplist = entry.getValue();
                        ObservableList<PosOrder> posOrderData = FXCollections.observableArrayList();
                        for (PosOrderDto posDetail : posmaplist) {
                            // System.out.println("臺號" + k + "組別:" + finalI + " 單號：" + posDetail.getBill_no() + " 食品：" + posDetail.getName1());
                            posOrderData.add(new PosOrder(posDetail.getBill_no(), posDetail.getSub_no(), posDetail.getType(), posDetail.getTable_no(), posDetail.getGoodsno(),
                                    posDetail.getOrder_idx(), posDetail.getName1(), posDetail.getPrint_info(), DateUtil.getFormatDay(posDetail.getRt_op_date()), DateUtil.getFormatTime(posDetail.getRt_op_time()), posDetail.getS_code(), posDetail.getSeal_count(), posDetail.getAmt().doubleValue(), PrintStateEnum.HANG.getValue(), posDetail.getSingle_prt(), posDetail.getT_kic_msg(), posDetail.getPos_id(), posDetail.getPrint_name(), posDetail.getBarcode(), posDetail.getStaff(), posDetail.getZone(), posDetail.getR_desc(), posDetail.getPrint_msg(), posDetail.getRegion_id(), posDetail.getPerson_num(), posDetail.getAtt_name(), posDetail.getOrder_type(), posDetail.getIn_date()));
                        }
                        main.getHangOrderBoxs().add(posOrderData);
                    }
                }
                isInitHang = true;
            }

            //System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxx: "+ posmap.size());
            if(posmap != null){
                Iterator<Map.Entry<String, List<PosOrderDto>>> it = posmap.entrySet().iterator();
                while (it.hasNext()) {
                    List<PosOrderDto> posmaplist = null;
                    ObservableList<PosOrder> posOrderData = FXCollections.observableArrayList();
                    try {
                        Map.Entry<String, List<PosOrderDto>> entry = it.next();
                        System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
                        posmaplist = entry.getValue();
                        for (PosOrderDto posDetail : posmaplist) {
                            // System.out.println("臺號" + k + "組別:" + finalI + " 單號：" + posDetail.getBill_no() + " 食品：" + posDetail.getName1());
                            posOrderData.add(new PosOrder(posDetail.getBill_no(), posDetail.getSub_no(), posDetail.getType(), posDetail.getTable_no(), posDetail.getGoodsno(),
                                    posDetail.getOrder_idx(), posDetail.getName1(), posDetail.getPrint_info(), DateUtil.getFormatDay(posDetail.getRt_op_date()), DateUtil.getFormatTime(posDetail.getRt_op_time()), posDetail.getS_code(), posDetail.getSeal_count(), posDetail.getAmt().doubleValue(), PrintStateEnum.PREPRINT.getValue(), posDetail.getSingle_prt(), posDetail.getT_kic_msg(), posDetail.getPos_id(), posDetail.getPrint_name(), posDetail.getBarcode(), posDetail.getStaff(), posDetail.getZone(), posDetail.getR_desc(), posDetail.getPrint_msg(), posDetail.getRegion_id(), posDetail.getPerson_num(), posDetail.getAtt_name(), posDetail.getOrder_type(),posDetail.getIn_date()));
                            PrintStateEnum oldPrintState = PrintStateEnum.getPrintStateEnumByValue(posDetail.getPrint_state());
                            if(PosOrderDao.otheridxs.containsKey(posDetail.getOrder_idx())){
                                List<PosOrderDto> posOrderDtoList = PosOrderDao.otheridxs.get(posDetail.getOrder_idx());
                                posOrderDtoList.forEach(posOrderDto -> {
                                    PrintStateEnum otherOldPrintState = PrintStateEnum.getPrintStateEnumByValue(posOrderDto.getPrint_state());
                                    PosOrderDao.updataPrintState(sqlSession,PrintStateEnum.PREPRINT, otherOldPrintState, posOrderDto.getBill_no(), posOrderDto.getOrder_idx(), posOrderDto.getType());
                                });
                            }
                            synchronized (PosOrderDao.class) {
                                PosOrderDao.updataPrintState(sqlSession,PrintStateEnum.PREPRINT, oldPrintState, posDetail.getBill_no(), posDetail.getOrder_idx(), posDetail.getType());
                            }
                        }
                        if(PrintStateEnum.HANG.getValue().equals(posmaplist.get(0).getPrint_state())){
                            for(int i = 0;i< main.getHangOrderBoxs().size();i++ ){
                                if(main.getHangOrderBoxs().get(i).get(0).getBill_no().equals(posmaplist.get(0).getBill_no())){
                                    main.getHangOrderBoxs().remove(main.getHangOrderBoxs().get(i));
                                    main.setOrderHangCount(main.getOrderHangCount()-1);
                                    int finalI = i;
                                    if(PrintStateEnum.HANG.getName().equals(main.getSelectedOrderType())){
                                        if(main.getFlowPane().getChildren() != null && main.getFlowPane().getChildren().size() > 0){
                                            Platform.runLater(() -> main.getFlowPane().getChildren().remove(finalI));
                                        }
                                    }
                                    main.setHangBoxTotal("掛起"+"("+ main.getOrderHangCount()+")");
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        sqlSession.rollback();
                        break;

                    }
                    if(AppUtils.isNotBlank(posmaplist) && main.getAutoPrint().equals("OFF")){
                        //ObservableList<ObservableList<PosOrder>> posOrderMap = main.getOrderBoxs();
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

                    String o = main.getAutoPrint();
                    if (main.getAutoPrint().equals("ON")) {
                        System.out.println("print_______________________");
                        List<String> list = new LinkedList<String>();
                        posOrderData.forEach(posOrder -> {
                            list.add(posOrder.getName1() + "|R" + posOrder.getSeal_count() + "|R" + posOrder.getPrint_msg());
//                        if(AppUtils.isNotBlank(posOrder.getPrint_msg())){
//                            list.add("-"+posOrder.getPrint_msg().replace("\n",",")+ "|R");
//                        }

                        });
                        String print = null;
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
                        printmessage.put("PRN_NAM", posOrderData.get(0).getPrint_name());
                        printmessage.put("TABLE_NAM", posOrderData.get(0).getTable_no());
                        printmessage.put("BARCODE", posOrderData.get(0).getBarcode());
                        printmessage.put("STATION", posOrderData.get(0).getPos_id());
                        printmessage.put("ZONE", posOrderData.get(0).getZone());
                        printmessage.put("REF_NUM", posOrderData.get(0).getBill_no());
                        printmessage.put("ORDER_TYPE",posOrderData.get(0).getOrder_type());
                        printmessage.put("STAFF", posOrderData.get(0).getStaff());
                        printmessage.put("PERSON", String.valueOf(posOrderData.get(0).getPerson_num()));
                        printmessage.put("ORDER_TYPE", posOrderData.get(0).getOrder_type());
                        printmessage.put("POSDATE", posOrderData.get(0).getRt_op_time());
                        print = GetPrintOrReportStr.TranslateTableStyle(tableStyle, list, null, 1, printmessage);
                        try {
                            String printMsg = PrintRxTxVirtaul.getInstance().PrinterCheck();
                            if (AppUtils.isNotBlank(printMsg)) {
                                Platform.runLater(() -> {
                                    try {
                                        main.showWarningView("錯誤", "打印機異常", main.getPrimaryStage());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                try {
                                    PrintRxTxVirtaul.getInstance().localPrint(print);
                                } catch (IOException e) {
                                    Platform.runLater(() -> {
                                        try {
                                            main.showWarningView("錯誤", "打印機異常", main.getPrimaryStage());
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    });
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Platform.runLater(() -> {
                                try {
                                    main.showWarningView("錯誤", "打印機異常", main.getPrimaryStage());
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            });
                            return;
                        }

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
                        Date nowDate = new Date();
                        posOrderData.forEach(posDetail -> {
                            synchronized (PosOrderDao.class) {
                                PrintStateEnum oldPrintState = PrintStateEnum.getPrintStateEnumByValue(posDetail.getPrint_state());
                                if(PosOrderDao.otheridxs.containsKey(posDetail.getOrder_idx())){
                                    List<PosOrderDto> posOrderDtoList = PosOrderDao.otheridxs.get(posDetail.getOrder_idx());
                                    posOrderDtoList.forEach(posOrderDto -> {
                                        PosOrderDao.updatePrintStateWithoutOldPrintState(sqlSession,PrintStateEnum.PRINTED, posOrderDto.getBill_no(), posOrderDto.getOrder_idx(), posOrderDto.getType());
                                        RtLogDao.insertLog(posOrderDto.getBarcode(),simpleDateFormat.format(nowDate),simpleTimeFormat.format(nowDate),"9999999", LogTypeEnum.KVSPRINT.getValue(),"N",posOrderDto.getOrder_idx().toString(),posOrderDto.getBill_no(),posOrderDto.getTable_no(),posOrderDto.getGoodsno());
                                    });
                                    PosOrderDao.otheridxs.remove(posDetail.getOrder_idx());

                                }
                                RtLogDao.insertLog(posDetail.getBarcode(),simpleDateFormat.format(nowDate),simpleTimeFormat.format(nowDate),"9999999", LogTypeEnum.KVSPRINT.getValue(),"N",String.valueOf(posDetail.getOrder_idx()),posDetail.getBill_no(),posDetail.getTable_no(),posDetail.getGoodsno());
                                PosOrderDao.updataPrintState(sqlSession,PrintStateEnum.PRINTED, oldPrintState, posDetail.getBill_no(), posDetail.getOrder_idx(), posDetail.getType());
                            }
                        });
                        main.getPosOrderData().remove(posOrderData);
                        Boolean flag = false;
                        for(ObservableList<PosOrder> posOrderObservableList :  main.getPosOrderData() ){
                            if(AppUtils.isNotBlank(posOrderObservableList)){
                                if(posOrderObservableList.get(0).getBill_no().equals(posOrderData.get(0).getBill_no())){
                                    flag = true;
                                }
                            }
                        }
                        for(ObservableList<PosOrder> selectedPosOrderObservableList :main.getSelectedOrderBoxs()){
                            if(AppUtils.isNotBlank(selectedPosOrderObservableList )){
                                if(selectedPosOrderObservableList .get(0).getBill_no().equals(posOrderData.get(0).getBill_no())){
                                    flag = true;
                                }
                            }
                        }
                        if(!flag){
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
                        break;
                    }
                    else {
                        System.out.println("data_______________________");
                        switch (posOrderData.get(0).getOrder_type()){
                            case "首單":
                               /* boolean firstFlag = false;
                                boolean firstSelected = false;
                                if(main.getSelectedFirstOrderBoxs() != null && main.getSelectedFirstOrderBoxs().size() > 0){
                                    for(ObservableList<PosOrder> posOrders : main.getSelectedFirstOrderBoxs()){
                                        if(posOrders.get(0).getBill_no().equals(posOrderData.get(0).getBill_no())){
                                            posOrders.addAll(posOrderData);
                                            if("首單".equals(main.getSelectedOrderType())|| "全部".equals(main.getSelectedOrderType())){
                                                if(main.getFlowPane().getChildren()!= null && main.getFlowPane().getChildren().size()>0){
                                                    for(Node node : main.getFlowPane().getChildren()){
                                                        VBox vBox = (VBox) node;
                                                        TableView tableView = (TableView) vBox.getChildren().get(2);
                                                        PosOrder posOrder = (PosOrder) tableView.getItems().get(0);
                                                        if(posOrder.getBill_no().equals(posOrderData.get(0).getBill_no())){
                                                            tableView.getItems().addAll(posOrderData);
                                                            break;
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }

                                }
                                if(main.getFirstPosOrderData()!= null && main.getFirstPosOrderData().size() >0){
                                     for(ObservableList<PosOrder> posOrders : main.getFirstPosOrderData()){
                                         if(posOrders.get(0).getBill_no().equals(posOrderData.get(0).getBill_no())){
                                             posOrders.addAll(posOrderData);
                                             firstFlag = true;
                                             break;
                                         }
                                     }
                                }
                                if(!firstFlag){
                                    main.getFirstPosOrderData().add(posOrderData);
                                }*/
                                main.getFirstPosOrderData().add(posOrderData);
                                break;
                            case "加單":
                               /* boolean addFlag = false;
                                if(main.getSelectedAddOrderBoxs() != null && main.getSelectedAddOrderBoxs().size() > 0){
                                    for(ObservableList<PosOrder> posOrders : main.getSelectedAddOrderBoxs()){
                                        if(posOrders.get(0).getBill_no().equals(posOrderData.get(0).getBill_no())){
                                            posOrders.addAll(posOrderData);
                                            if("加單".equals(main.getSelectedOrderType())|| "全部".equals(main.getSelectedOrderType())){
                                                if(main.getFlowPane().getChildren()!= null && main.getFlowPane().getChildren().size()>0){
                                                    for(Node node : main.getFlowPane().getChildren()){
                                                        VBox vBox = (VBox) node;
                                                        TableView tableView = (TableView) vBox.getChildren().get(2);
                                                        PosOrder posOrder = (PosOrder) tableView.getItems().get(0);
                                                        if(posOrder.getBill_no().equals(posOrderData.get(0).getBill_no())){
                                                            tableView.getItems().addAll(posOrderData);
                                                            break;
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }

                                }
                                if(main.getAddPosOrderData()!= null && main.getAddPosOrderData().size() >0){
                                    for(ObservableList<PosOrder> posOrders : main.getAddPosOrderData()){
                                        if(posOrders.get(0).getBill_no().equals(posOrderData.get(0).getBill_no())){
                                            posOrders.addAll(posOrderData);
                                            addFlag = true;
                                            break;
                                        }
                                    }
                                }
                                if(!addFlag){
                                    main.getAddPosOrderData().add(posOrderData);
                                }*/
                                main.getAddPosOrderData().add(posOrderData);
                                break;
                            case "尾單":
                               /*// main.getLastPosOrderData().add(posOrderData);
                                boolean lastFlag = false;
                                if(main.getSelectedLastOrderBoxsOrderBoxs() != null && main.getSelectedLastOrderBoxsOrderBoxs().size() > 0){
                                    for(ObservableList<PosOrder> posOrders : main.getSelectedLastOrderBoxsOrderBoxs()){
                                        if(posOrders.get(0).getBill_no().equals(posOrderData.get(0).getBill_no())){
                                            posOrders.addAll(posOrderData);
                                            if("尾單".equals(main.getSelectedOrderType())|| "全部".equals(main.getSelectedOrderType())){
                                                if(main.getFlowPane().getChildren()!= null && main.getFlowPane().getChildren().size()>0){
                                                    for(Node node : main.getFlowPane().getChildren()){
                                                        VBox vBox = (VBox) node;
                                                        TableView tableView = (TableView) vBox.getChildren().get(2);
                                                        PosOrder posOrder = (PosOrder) tableView.getItems().get(0);
                                                        if(posOrder.getBill_no().equals(posOrderData.get(0).getBill_no())){
                                                            tableView.getItems().addAll(posOrderData);
                                                            break;
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }

                                }
                                if(main.getLastPosOrderData()!= null && main.getLastPosOrderData().size() >0){
                                    for(ObservableList<PosOrder> posOrders : main.getLastPosOrderData()){
                                        if(posOrders.get(0).getBill_no().equals(posOrderData.get(0).getBill_no())){
                                            posOrders.addAll(posOrderData);
                                            addFlag = true;
                                            break;
                                        }
                                    }
                                }
                                if(!lastFlag){

                                }*/
                                main.getLastPosOrderData().add(posOrderData);
                                break;
                            default:
                                break;
                        }
                       /* boolean flag = false;
                        for(ObservableList<PosOrder> posOrderObservableList : main.getPosOrderData()){
                            if(posOrderObservableList.get(0).getBill_no().equals(posOrderData.get(0).getBill_no())){
                              posOrderObservableList.addAll(posOrderData);
                              flag = true;
                              break;
                            }
                        }*/
                       /* if(!flag){*/
                            main.getPosOrderData().add(posOrderData);
                        insertPosOrdersToVBox(main);
                        insertPosOrdersToCorrespondingBox(main);
                    }
                }
                if(main.getPosOrderData().size()>0 && main.getSelectedOrderBoxs().size()<4){
                    insertPosOrdersToVBox(main);
                }
                insertPosOrdersToCorrespondingBox(main);
            }
        } catch (Exception e) {
            e.printStackTrace();
            isPing = false;
            sqlSession.rollback();

        }

    };


    public  static void insertPosOrdersToVBox(Main main){
        RulerDto rulerDto = main.getRulerDto();
        ObservableList<ObservableList<PosOrder>> selectedOrderBoxs = main.getSelectedOrderBoxs();
        if(selectedOrderBoxs.size()<4 && main.getPosOrderData().size()>0){
            if(!rulerDto.getFlag1()){
                ObservableList<PosOrder> orders = main.getOrdersByType(OrderType.TAILORDER.getValue());
                if(orders != null && orders.size() > 0){
                    rulerDto.setFlag1(true);
                    main.getPosOrderData().remove(orders);
                    selectedOrderBoxs.add(orders);
                }
                else{
                    rulerDto.setFlag1(true);
                    insertPosOrdersToVBox(main);
                }
            }
            else if(!rulerDto.getFlag2()){
                ObservableList<PosOrder> orders = main.getOrdersByType(OrderType.TAILORDER.getValue());
                if(orders != null && orders.size() > 0){
                    rulerDto.setFlag2(true);
                    main.getPosOrderData().remove(orders);
                    selectedOrderBoxs.add(orders);
                }
                else{
                    rulerDto.setFlag2(true);
                    insertPosOrdersToVBox(main);
                }
            }
            else if(!rulerDto.getFlag3()){
                ObservableList<PosOrder> orders = main.getOrdersByType(OrderType.FIRSTORDER.getValue());
                if(orders != null && orders.size() > 0){
                    rulerDto.setFlag3(true);
                    main.getPosOrderData().remove(orders);
                    selectedOrderBoxs.add(orders);
                }
                else{
                    rulerDto.setFlag3(true);
                    insertPosOrdersToVBox(main);
                }
            }
            else if(!rulerDto.getFlag4()){
                ObservableList<PosOrder> orders = main.getOrdersByType(OrderType.ADDORDER.getValue());
                main.reSetRulerDto();
                if(orders != null && orders.size() > 0){
                    main.getPosOrderData().remove(orders);
                    selectedOrderBoxs.add(orders);
                }
                else{
                    insertPosOrdersToVBox(main);
                }
            }
        }

    }

    public static void insertPosOrdersToCorrespondingBox(Main main) {
        ObservableList<ObservableList<PosOrder>> selectedFirstOrderBoxs = main.getSelectedFirstOrderBoxs();
        ObservableList<ObservableList<PosOrder>> selectedAddOrderBoxs = main.getSelectedAddOrderBoxs();
        ObservableList<ObservableList<PosOrder>> selectedLastOrderBoxs = main.getSelectedLastOrderBoxsOrderBoxs();
        if(selectedFirstOrderBoxs.size()<4 && main.getFirstPosOrderData().size()>0){
            ObservableList<PosOrder> firstOrders =   main.getFirstOrdersByType(OrderType.FIRSTORDER.getValue());
            if(firstOrders != null && firstOrders.size() > 0){
                selectedFirstOrderBoxs.add(firstOrders);
                main.getFirstPosOrderData().remove(firstOrders);
            }
        }
        if(selectedAddOrderBoxs.size()<4 && main.getAddPosOrderData().size()>0){
            ObservableList<PosOrder> addOrders =   main.getAddOrdersByType(OrderType.ADDORDER.getValue());
            if(addOrders != null && addOrders.size() > 0){
                selectedAddOrderBoxs.add(addOrders);
                main.getAddPosOrderData().remove(addOrders);
            }
        }
        if(selectedLastOrderBoxs.size()<4 && main.getLastPosOrderData().size()>0){
            ObservableList<PosOrder> lastOrders =   main.getLastOrdersByType(OrderType.TAILORDER.getValue());
            if( lastOrders != null &&  lastOrders.size() > 0){
                selectedLastOrderBoxs.add( lastOrders);
                main.getLastPosOrderData().remove(lastOrders);
            }
        }
    }





}

