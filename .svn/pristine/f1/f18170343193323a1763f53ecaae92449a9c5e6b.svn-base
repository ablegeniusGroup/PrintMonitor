package pos.dongwang.task;

import javafx.scene.paint.Color;
import pos.dongwang.Main;
import pos.dongwang.properties.ReadProperties;
import pos.dongwang.util.CommonPingTask;
import pos.dongwang.util.CommonPrintTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TimerTask;

/**
 * Created by lodi on 2017/12/13.
 */
public class PingTask  extends TimerTask {


    private Main main;

    Color[] colors = new Color[]{Color.BLUE,Color.ALICEBLUE,Color.AQUA,Color.AZURE,Color.BEIGE,Color.DARKKHAKI,Color.DEEPPINK};



    public PingTask(Main main) {
        this.main = main;
    }
     /*  public  void  testPing(){
       Runtime runtime =Runtime.getRuntime(); // 获取当前程序的运行进对象
        Process process = null; //声明处理类对象
        String line = null; //返回行信息
        InputStream is = null; //输入流
        InputStreamReader isr = null;// 字节流
        BufferedReader br = null;
        String ip = ReadProperties.readStringByKey("serverAdd");
        boolean res = false;// 结果
        try {
            process =runtime.exec("ping " + ip); // PING
            is =process.getInputStream(); // 实例化输入流
            isr = new InputStreamReader(is);// 把输入流转换成字节流
            br = new BufferedReader(isr);// 从字节中读取文本
            while ((line= br.readLine()) != null) {
                if(line.contains("TTL")) {
                    res= true;
                    break;
                }
            }
            is.close();
            isr.close();
            br.close();
            if (res){
               this.main.getCircle().setFill(Color.GREEN);
            } else{
                this.main.getCircle().setFill(Color.RED);
            }
        } catch (IOException e) {
            System.out.println(e);
            runtime.exit(1);
        }






    }*/


    @Override
    public void run() {
        CommonPingTask.getInstance().execute(main);
    }
}
