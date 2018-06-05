package pos.dongwang.print;

/**
 * Created by wenjing on 2017/10/22.
 */
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;


import gnu.io.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pos.dongwang.model.PosOrder;
import pos.dongwang.properties.ReadProperties;

///#CENTER
public class PrintRxTx {
    private static PrintRxTx instance = new PrintRxTx();
    InputStream			inputStream	= null; //讀串口
    SerialPort			serialPort	= null;
    OutputStreamWriter	writer		= null;	//寫串口
    CommPortIdentifier	portId		= null; //串口ID
    OutputStream		output		= null;
    private String[]	tablestyle	= null;	//打印用到的表樣
    private String		prtPort		= null;	//打印機對應的端口
    private String 		printall	= null;	//待打印的數據
    private List<String> orderlist	= null;	//待打印的鏈表
    private List<String> paylist	= null;	//待打印的鏈表
    public  int	boudrate = 9600; //波特率
    private HashMap<String,String> mapmessage = new HashMap<String,String>();
    private String ENCODE_FORMAT;

    public void setPaylist(List<String> paylist) {
        this.paylist = paylist;
    }
    public void setOrderlist(List<String> orderlist) {
        this.orderlist = orderlist;
    }
    public void setMapmessage(HashMap<String,String> map){
        mapmessage = map;
    }

    public HashMap<String, String> getMapmessage() {
        return mapmessage;
    }



    public PrintRxTx() {
        boudrate =   ReadProperties.readIntegerByKey("boudRate");
        prtPort =  ReadProperties.readStringByKey("printPort");
        this.ENCODE_FORMAT = ReadProperties.readStringByKey("encode");
    }

    //改成單例
    public static PrintRxTx getInstance() {
        return instance;
    }

    /**
     * 設定打印用的表樣
     * @param style
     */
    public void setTableStyle(String[] style) {
        tablestyle = style;
    }



    /**
     * 打印數據
     * @return
     * @return
     */
    public boolean printAction(){
        if(prtPort == null || tablestyle == null )
            return false;
        //整理打印數據
        printall = GetPrintOrReportStr.TranslateTableStyle(tablestyle, orderlist, paylist,1,mapmessage);
        System.out.println(printall);
        //執行打印
        try {
            return localPrint(printall);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 打印前台收銀小票
     * @return 打印結果s
     * @throws IOException
     */
    public boolean localPrint(String printstr) throws IOException {
        try {
            initPort(prtPort);
        } catch (Exception e) {
            e.printStackTrace();
            //印機初始化失敗
            System.out.println("印機端口初始化失敗");
            System.out.println(prtPort);

            return false;
        }
        //把打印數據寫入串口
        String[] tablestyle=printstr.split("\n");
        for(int i = 0 ; i < tablestyle.length ; i++){
            System.out.println(tablestyle[i]);
            //發送命令
            if(tablestyle[i].startsWith("#")){

                Delc(tablestyle[i]);
            }
            else if(tablestyle[i].startsWith("*")){//组合命令

                DelcNew(tablestyle[i]);
            }
            //發送數據
            else{

                //writer.write(tablestyle[i]+"\n");
                //writer.flush();
                this.output.write((tablestyle[i]+"\n").getBytes(ENCODE_FORMAT));
            }
        }
        //關閉端口
        serialPort.close();
        return true;
    }

    /**
     * 初始化對應的端口
     * @param CFG_PORT 指定的端口
     * @return
     */
    private void initPort(String CFG_PORT) throws Exception{




        //獲得串口ID
        portId=CommPortIdentifier.getPortIdentifier(CFG_PORT);

        //打開串口
        serialPort=(SerialPort) portId.open("TestMain", 2000);
        //設置串口初始化參數，依次是波特率，數據位，停止位和 校驗
        serialPort.setSerialPortParams(boudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        inputStream = serialPort.getInputStream();
        output = serialPort.getOutputStream(); //讀取串口輸出
        //解決中文亂碼主要是用OutputStreamWriter包在OutputStream的底層流，設置GBK/BIG5（通用繁体机器）編碼讀取，
        writer = new OutputStreamWriter(output,ENCODE_FORMAT);
        //中文简体
        //writer = new OutputStreamWriter(output,"GBK");




    }



    private void DelcNew(String cmd)throws IOException{
        cmd = cmd.substring(1);
        String[] str = cmd.split("\\|");
        for(int i= 0;i<str.length;i++){
            if(str[i].startsWith("#")){
                Delc(str[i]);

            }
            else{
                //writer.write(str[i]);
                this.output.write(str[i].getBytes(ENCODE_FORMAT));
                //writer.flush();
            }
        }
    }



    private byte[] getBytes (char[] chars) {
        Charset cs = Charset.forName ("UTF-8");
        CharBuffer cb = CharBuffer.allocate (chars.length);
        cb.put (chars);
        cb.flip ();
        ByteBuffer bb = cs.encode (cb);

        return bb.array();

    }

    /**
     * 處理打印命令
     * @param cmdline 打印命令
     * @throws IOException
     */
    private void Delc(String cmdline) throws IOException
    {
        //通用指令集
        writer.flush();	//刷新串口缓存f
        if(cmdline.substring(1).equals("CUTPAPER")){	//切纸
            writer.write(InstructionSet.getCutter());
        }
        else if(cmdline.substring(1).equals("OPENBOX")){//开钱箱
            writer.write(InstructionSet.getOpenbox());
        }
        else if(cmdline.substring(1).equals("GOHEAD5")){//进纸5行
            writer.write(InstructionSet.getGoHead5());
        }else if(cmdline.substring(1).equals("GOHEAD1")){//进纸5行
            writer.write(InstructionSet.getGOHEAD1());
        }
        else if(cmdline.substring(1).equals("INITIALIZE")){//初始化印机
            writer.write(InstructionSet.getInitialize());
        }
        //热敏印机指令集
        else if(cmdline.substring(1).equals("PRINT_LOGO")){	//打印LOGO
            writer.write(InstructionSet.getPrintLogo());
        }
        else if(cmdline.substring(1).equals("CENTER")){	//居中打印
            writer.write(InstructionSet.getCenter());
        }
        else if(cmdline.substring(1).equals("LEFT")){	//靠左打印
            writer.write(InstructionSet.getLeft());
        }
        else if(cmdline.substring(1).equals("BIG1")){	//放大1倍
            writer.write(InstructionSet.getBig1());
        }
        else if(cmdline.substring(1).equals("BIG2")){	//放大2倍
            writer.write(InstructionSet.getBig2());
        }
        else if(cmdline.substring(1).equals("BIG3")){	//放大3倍
            writer.write(InstructionSet.getBig3());
        }
        else if(cmdline.substring(1).equals("BIG4")){	//放大4倍
            writer.write(InstructionSet.getBig4());
        }
        else if(cmdline.substring(1).equals("BOLD")){	//加粗
            writer.write(InstructionSet.getBold());
        }
        else if(cmdline.substring(1).equals("BOLDOFF")){	//取消加粗
            writer.write(InstructionSet.getBoldoff());
        }
        else if(cmdline.substring(1).equals("HEIGHT_BIG1")){//纵向放大一倍
            writer.write(InstructionSet.getHeightBig1());
        }
        else if(cmdline.substring(1).equals("UPSIDE_DOWN")){	//反转180
            writer.write(InstructionSet.getUpsideDown());
        }
        //针式印机指令集
        else if(cmdline.substring(1).equals("RED")){	//打印红色
            writer.write(InstructionSet.getFontColorRed());
        }
        else if(cmdline.substring(1).equals("BLACK")){	//打印黑色
            writer.write(InstructionSet.getFontColorBlack());
        }
        else if(cmdline.substring(1).equals("ENLARGE")){//放大字体
            writer.write(InstructionSet.getFontSizeLarger1());
            writer.flush();//刷新串口缓存
            writer.write(InstructionSet.getFontSizeLarger2());
        }
        else if(cmdline.substring(1).equals("NORMAL")){	//缩小字体
            writer.write(InstructionSet.getFontSizeNormal1());
            writer.flush();//刷新串口缓存
            writer.write(InstructionSet.getFontSizeNormal2());
        }
        else if(cmdline.substring(1).equals("PRINT_LOGO")){//打印LOGO
            writer.write(InstructionSet.getPrintLogo());
            writer.flush();//刷新串口缓存
            writer.write(" \n");//换行
        }
        else if(cmdline.substring(1).equals("BIG")){	//放至最大
            writer.write(InstructionSet.getFontSizeLargest1());
            writer.flush();//刷新串口缓存
            writer.write(InstructionSet.getFontSizeLargest2());
        }
        else if(cmdline.substring(1).equals("SMALL")){	//放小
            writer.write(InstructionSet.getFontSizeNormal1());
            writer.flush();//刷新串口缓存
            writer.write(InstructionSet.getFontSizeNormal2());
        }
        else if(cmdline.substring(1).equals("BAR_CODE")){//打印条码
            String str = mapmessage.get("BILL_NO");
            if(str != null){
                byte[] center ={0x1b,0x61,0x01};
                output.write(center);
                byte[] head = {0x1D,0x6B,0x04};
                byte[] tail = {0x00,0x0D,0x0A};
                byte[] data =str.getBytes();
                byte[] all=new byte[head.length+tail.length+data.length];
                int i=0;
                for(byte bytes : head){
                    all[i++] = bytes;
                }
                for(byte bytes : data){
                    all[i++] = bytes;
                }
                for(byte bytes : tail){
                    all[i++] = bytes;
                }
                output.write(all);
                writer.flush();
                writer.write("单号："+str+"\n");
            }
        }

        writer.flush();//刷新串口缓存
    }

    private String checkPrinter(){
        //發送校驗信息
        int count=0;
        byte[] readBuffer = new byte[1];
        char[] check ={16,4,1};//或者是{29.114,49}
        try {
            writer.write(check);//寫串口
            writer.flush();//刷新串口緩存
            while (inputStream.available() <= 0 && count<10){
                count++;
                Thread.sleep(300);
            }
            if(count>=10){
                serialPort.close();
                return "收不到校驗返回值";
            }
            while (inputStream.available() > 0) {//如果串口有信息返回
                inputStream.read(readBuffer);//讀取串口
            }
            if(readBuffer[0] == 22){//打印機正常
//				System.out.println("印機正常");
                return null;
            }
            else if(readBuffer[0] == 30){//打印機沒紙
                return "打印機缺紙";
            }
        } catch (IOException e1) {//錯誤編號5
            serialPort.close();
            return "操作端口異常";
        } catch (InterruptedException e) {
            serialPort.close();
            return "操作端口異常";
        }
        return "打印機出現未知異常";
    }

    public String PrinterCheck(){
        String errrMsg = null;
        try {
            initPort(prtPort);
        } catch (Exception e) {
            e.printStackTrace();
            errrMsg = "初始化端口"+prtPort+"失敗";
        }//初始化端口
        errrMsg = checkPrinter();//校驗打印機狀態
        serialPort.close();//關閉打印機端口
        return errrMsg;
    }

    private  String toStringHex1(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 8));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "ASCII");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static void main ( String[] args ) throws NoSuchFieldException, IllegalAccessException, IOException {

       // RXTXInit.loadLib();

        ObservableList<PosOrder> posOrderData = FXCollections.observableArrayList();
            posOrderData.add(new PosOrder("0573", "12", "廚房", "888", "溫靖", "A", "排骨炒飯", 1, "11"));
            posOrderData.add(new PosOrder("0573", "12", "廚房", "888", "溫靖", "A", "排骨炒飯排骨炒飯炒飯", 1, "11"));
            posOrderData.add(new PosOrder("0573", "12", "廚房", "888", "溫靖", "A", "排骨炒飯排骨炒飯", 1, "11"));
            posOrderData.add(new PosOrder("0573", "12", "廚房", "888", "溫靖", "A", "排骨炒飯排骨炒飯", 1, "11"));
            posOrderData.add(new PosOrder("0573", "12", "廚房", "888", "溫靖", "A", "排骨炒飯", 1, "11"));
            posOrderData.add(new PosOrder("0573", "12", "廚房", "888", "溫靖", "A", "排骨炒飯", 1, "11"));


        List<String> list = new LinkedList<String>();
        posOrderData.forEach(posOrder -> {
            list.add(posOrder.getName1()+"|R"+posOrder.getSeal_count()+"|R");
        });
        String[] tableStyle  = {
                "~0,160,35",
                "#BIG",
                "#CENTER",
                "$PRN_NAM",
                "#GOHEAD1",
                "$檯號：|TABLE_NAM| 區域：|ZONE",
                "#GOHEAD1",
                "#SMALL",
                "#LEFT",
                "$账单编号：|REF_NUM",
                "$BARCODE|/|STATION|-|STAFF| |@POSDATE",
                "#SMALL",
                "#R|2|COMPART",
                "#GOHEAD1",
                "#BIG",
                "*菜品名称|R數量",
                "#SMALL",
                "#R|2|COMPART",
                "#BIG",
                "%ORDERLIST",
                "#SMALL",
                "#R|2|COMPART",
                "#GOHEAD5",
                "#CUTPAPER"
        };
        HashMap<String,String> printmessage =  new HashMap<String,String>();
        printmessage.put("PRN_NAM", posOrderData.get(0).getPrint_name());
        printmessage.put("TABLE_NAM", posOrderData.get(0).getTable_no());
        printmessage.put("BARCODE", posOrderData.get(0).getBarcode());
        printmessage.put("STATION", posOrderData.get(0).getPos_id());
        printmessage.put("ZONE", posOrderData.get(0).getZone());
        printmessage.put("REF_NUM", posOrderData.get(0).getBill_no());
        printmessage.put("STAFF", posOrderData.get(0).getStaff());

        String print = GetPrintOrReportStr.TranslateTableStyle(tableStyle, list , null, 1, printmessage);

        PrintRxTx.getInstance().localPrint(print);

    }
}