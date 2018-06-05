package pos.dongwang.util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import pos.dongwang.model.PosOrder;
import pos.dongwang.print.GetPrintOrReportStr;
import pos.dongwang.print.PrintRxTxVirtaul;
import pos.dongwang.properties.ReadProperties;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lodi on 2017/11/22.
 */
public class CommonPingTask {

    private static CommonPingTask instance;
    public static boolean isPing  = false;
    public static Color[] colors = new Color[]{Color.BLUE,Color.ORANGE,Color.GREEN,Color.PINK,Color.YELLOW,Color.WHEAT};
    public static  Integer i = 0;

    private CommonPingTask(){}

    public static CommonPingTask getInstance() {
        if (instance == null) {
            instance = new CommonPingTask();
        }
        return instance;
    }

    public static synchronized void execute(Main main) {
       if(CommonPrintTask.isPing){
           if(i<=colors.length-1){
               main.getCircle().setFill(colors[i]);
           }
           if(i ==colors.length-1 ){
               i = 0;
           }
           else{
               i++;
           }
       }
       else{
           main.getCircle().setFill(Color.RED);
       }

    };


}

