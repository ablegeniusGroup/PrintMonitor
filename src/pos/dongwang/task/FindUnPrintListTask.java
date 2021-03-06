package pos.dongwang.task;

import com.sun.deploy.util.StringUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.apache.ibatis.session.SqlSession;
import pos.dongwang.Main;
import pos.dongwang.dao.PosOrderDao;
import pos.dongwang.dto.PosOrderDto;
import pos.dongwang.dto.RulerDto;
import pos.dongwang.enums.OrderType;
import pos.dongwang.enums.PrintStateEnum;
import pos.dongwang.model.PosOrder;
import pos.dongwang.mybatis.MyBatisSqlSessionFactory;
import pos.dongwang.print.GetPrintOrReportStr;
import pos.dongwang.print.PrintRxTx;
import pos.dongwang.print.PrintRxTxVirtaul;
import pos.dongwang.properties.ReadProperties;
import pos.dongwang.util.AppUtils;
import pos.dongwang.util.CommonPrintTask;
import pos.dongwang.util.DateUtil;
import pos.dongwang.util.JdbcUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wenjing on 2017/10/19.
 * 啟動線程
 */
public class FindUnPrintListTask extends TimerTask {

    private Main main;
    private String receivePrinter;
    private Integer showCount;
    private String timeInterval;


    public FindUnPrintListTask(Main main) {
        this.main = main;
        receivePrinter = getPrintList(ReadProperties.readStringByKey("receivePrinter"));
        showCount = ReadProperties.readIntegerByKey("showCount");
        timeInterval = ReadProperties.readStringByKey("timeInterval");


    }

    public void insertPosOrdersToVBox(){
        RulerDto rulerDto = this.main.getRulerDto();
        ObservableList<ObservableList<PosOrder>> selectedOrderBoxs = this.main.getSelectedOrderBoxs();
        if(selectedOrderBoxs.size()<4 && main.getPosOrderData().size()>0){
            if(!rulerDto.getFlag1()){
                ObservableList<PosOrder> orders = main.getOrdersByType(OrderType.TAILORDER.getValue());
                if(orders != null && orders.size() > 0){
                    rulerDto.setFlag1(true);
                    this.main.getPosOrderData().remove(orders);
                    selectedOrderBoxs.add(orders);
                }
                else{
                    rulerDto.setFlag1(true);
                    insertPosOrdersToVBox();
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
                     insertPosOrdersToVBox();
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
                     insertPosOrdersToVBox();
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
                    insertPosOrdersToVBox();
                }
            }
        }

    }

    public void insertPosOrdersToCorrespondingBox() {
        ObservableList<ObservableList<PosOrder>> selectedFirstOrderBoxs = this.main.getSelectedFirstOrderBoxs();
        ObservableList<ObservableList<PosOrder>> selectedAddOrderBoxs = this.main.getSelectedAddOrderBoxs();
        ObservableList<ObservableList<PosOrder>> selectedLastOrderBoxs = this.main.getSelectedLastOrderBoxsOrderBoxs();
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




    @Override
    public  void run() {
      CommonPrintTask.getInstance().execute(main,receivePrinter,showCount,timeInterval);
    }
    public  static String getPrintList(String orgString) {
        String[] orgStringList = orgString.split(",");
        String printList = "";
        for (String prt : orgStringList) {
            printList += "'" + prt + "',";
        }

        return printList.substring(0, printList.length() - 1);


    }


}
