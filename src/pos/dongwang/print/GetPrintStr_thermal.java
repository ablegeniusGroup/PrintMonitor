package pos.dongwang.print;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class GetPrintStr_thermal {
	private static int[]			len;
	private static int				usetype ;//1打印，0显示
	private final static Font DRIVERS_PRINT = new Font("宋体", 0, 10);
	//处理打印數据
	public static String TranslateTableStyle(String[] style, List<String> list_1, int type, Map<String,String> mapmessage){
		usetype = type;
		String printall = "";
		for(int i = 1 ; i < style.length ; i++){
			if(style[i].startsWith("!"))//不翻译的内容
				continue;
			if(style[i].startsWith("~"))//设定列宽
				setlenarrery(style[i]);
			else if(style[i].startsWith("$"))//内容中有参數，需要去Map
				printall+=Delp(style[i],mapmessage);
			else if(style[i].startsWith("#"))//传递的是命令
				printall+=DelOrder(style[i]);
			else if(style[i].startsWith("%")){//打印的是链表
				String str = style[i].substring(1);
				if(str.endsWith("ORDERLIST")){
					printall+=setform(list_1,mapmessage);
				}
			}
			else if(style[i].startsWith("@"))//需要调用函數
				printall+=Delm(style[i]);
			else if(style[i].startsWith("*"))//需要按照列模式处理
				printall+=dealColStyle(style[i].substring(1),mapmessage);
			else
				printall+=(style[i]+"\n");//直接送端口
		}
		return printall;
	}
	
	/**
	 * 设置列宽
	 * @param cmdarrary
	 */
	
	private static void setlenarrery(String cmdarrary){
		String arrary[] =cmdarrary.substring(1).split(",");
		len = new int[arrary.length];
		for(int i= 0;i<arrary.length;i++){
			len[i]= Integer.parseInt(arrary[i]);
		}
	}
	
	
	/**
	 * 表样中该行的开始是$，需要到相应的MAP中或则數据
	 * @param cmdline 待处理命令
	 * @return 加工后的數据
	 */
	private static String Delp(String cmdline, Map<String,String> mapmessage)
	{
		String ret="";
		String message;
		String str= cmdline.substring(1);
		String cmds[] = str.split("\\|");
		for(int i = 0 ; i < cmds.length ; i++)
		{
			if(cmds[i].startsWith("@")){
				ret+=Delm(cmds[i]);
			}
			else{
				message =getMessage(cmds[i],mapmessage);
				if(message==null){
					if(cmds[i].equals("MESSAGE")){
						continue;
					}
					else{
						ret += cmds[i];
					}
				}
				else{
					ret += message;
				}
			} 
		}
		if(!("").equals(ret) && !ret.endsWith("\n")){
			ret += "\n";
		}
		return ret;
	}
	
	/**
	 * 以#开头，处理命令
	 * @return 翻译后的结果
	 */
	private static String DelOrder(String order){
		String str = "";
		if(order.indexOf("|")>0){//从數据库中读出的需要添加分行符
			String orderStr[] = order.substring(1).split("\\|");
			int count = Integer.parseInt(orderStr[1]);
			if(orderStr[0].equals("R")){//从右边开始加
				for(int i =0;i<len.length;i++){
					if(i>=(len.length-count<0?(count-len.length):(len.length-count))){//添加--
						if(orderStr[2].equals("COMPART")){
							str+=GetDelimiter(len[i],"-");
						}
						else if(orderStr[2].equals("DOUB_COMPART")){
							str+=GetDelimiter(len[i],"=");
						}
					}
					else{//添加空格
						str+=GetSpaec(len[i]);
					}
				}
			}
			else if(orderStr[0].equals("L")){//从左边开始加
				for(int i =0;i<len.length;i++){
					if(i<count){//添加--
						if(orderStr[2].equals("COMPART")){
							str+=GetDelimiter(len[i],"-");
						}
						else if(orderStr[2].equals("DOUB_COMPART")){
							str+=GetDelimiter(len[i],"=");
						}
					}
				}
			}
			return str+"\n";
		}
		else {
			return order+"\n";
		}
	}
	
	/**
	 *添加分隔符 ------
	 * @param num
	 * @return
	 */
	public static String GetDelimiter(int num, String apps){
		StringBuilder str = new StringBuilder();
		for(int i =0;i<num/5 ;i++){
			str.append(apps);
		}
		return str.toString();
	}
	
	/**
	 * 添加空格
	 * @return
	 */
	private static String GetSpaec(int num){
		StringBuilder str = new StringBuilder();
		for(int i =0;i<num/5 ;i++){
			str.append(" ");
		}
		return str.toString();
	}
	
	private static String Delp_col(String cmdline, Map<String,String> mapmessage)
	{
		String ret="";
		String str= cmdline.substring(1);
		String cmds[] = str.split("\\|");
		String message ;
		for(int i = 0 ; i < cmds.length ; i++)
		{
			if(cmds[i].startsWith("@")){
				ret+=Delm(cmds[i]);
			}
			else {
				message = getMessage(cmds[i],mapmessage);
				if(message==null){
					ret += cmds[i]+"|";
				}
				else{
					ret += message+"|";
				}
			}
		}
		return ret.substring(0,ret.length()-1)+"\n";
	}
	/**
	 * 需要调用函數，这里暂时只有调用获得系统时间的函數
	 * @param cmdline 待处理命令
	 * @return 返回的调用函數的返回值
	 */
	private static String Delm(String cmdline)
	{
		String ret="";
		String cmds[] = cmdline.substring(1).split("\\|");
		for(int i = 0 ; i < cmds.length ; i++)
		{
			if(cmds[i].equals("DATETIME"))
				ret += getSystemDateTime("yyyy-MM-dd HH:mm:ss");
			else if(cmds[i].equals("POSDATE"))
				ret += getSystemDateTime("MM/dd HH:mm");
			else if(cmds[i].equals("POSTIME"))
				ret += getSystemDateTime("HH:mm");
			else if(cmds[i].equals("SYSTEMDATE"))
				ret += getSystemDateTime("yyyy-MM-dd");
			else 
				ret += cmds[i];
		}
		return ret+"\n";
	}
	
	/**
	 * 
	 * 处理打印链表
	 * 
	 */
	private static String setform(List<String> list, Map<String,String> mapmessage){
		String retstr ="";
		String str ="";
		if(list!=null){
			for(int i = 0 ; i <list.size() ; i++){
				str = list.get(i);
				if(str.startsWith("#")){//发送的命令
					if(usetype==1 && str.equals("#SPLIT_SCREEN")){
						retstr+="\n";
					}
					else{
						retstr += DelOrder(list.get(i));
					}
				}
				else{
					str = dealColStyle(str,mapmessage);
					retstr	= retstr+str;
				}
			}
		}
		return retstr;
	}
	
	/**
	 * 
	 * 处理列模式
	 * @return
	 */
	private static String dealColStyle(String colstr, Map<String,String> mapmessage){
		if(colstr.startsWith("$")){
			colstr=Delp_col(colstr,mapmessage);
		}
		FontMetrics fm 			= Toolkit.getDefaultToolkit().getFontMetrics(DRIVERS_PRINT);
		int				spacelen	= fm.stringWidth(" ");//一个空格的宽度
		String space		= "                                         ";//40个空格
		String details[]	=  colstr.split("\\|");
		String prnstr		= "";
		String str			= "";
		int				strwid		= 0;
		String message;
		for(int j=0;j<len.length-1;j++){
			int dis;
			boolean right=false;//是否右对齐
			if(details[j].startsWith("R")){//以R开头的就是需要右对齐的
				str=details[j].substring(1);
				message = getMessage(str,mapmessage);
				if(message==null)//map不出来就直接加上这个字符串
					details[j]= str;
				else {
					details[j]=message;//map出来就加上新的字符串
				}
				right=true;//需要右对齐
			}
			else{
				message = getMessage(details[j],mapmessage);
				if(message!=null){//map出来就直接加上这个字符串
					details[j]= message;
				}
			}
			strwid=fm.stringWidth(details[j]);//对应列要打印的字符串的长度
			if(strwid>len[j+1] && right){//如果字符超过列宽
				dis=strwid-len[j+1];
				int x=0;//记录加了多少个空格
				for(int n=prnstr.length();n>0;n--){
					if(x>=dis/spacelen)//如果空格加够了，就跳出循环
						break;
					x++;
					if(prnstr.substring(n-1).equals(" ")){
						prnstr=prnstr.substring(0,n-1);//去掉临近的空格，直到出现非空格字符
					}
					else
						break;
				}
			}
			else{
				dis=len[j+1]-strwid;
				if(right){
					if(dis>0)
						details[j]=space.substring(0,dis/spacelen)+details[j];//右对齐，左补空格
				}
				else{
					if(dis<0){
						details[j]=details[j].substring(0,(int)((1-(float)(-dis)/(float)strwid)*details[j].length()));
						strwid=fm.stringWidth(details[j]);
						if(len[j+1]-strwid>0)
							details[j]=details[j]+space.substring(0,(len[j+1]-strwid)/spacelen);//补空格
					}
					else
						details[j]=details[j]+space.substring(0,dis/spacelen);//补空格
				}
			}
			prnstr+=details[j];
		}
		return prnstr+"\n";
	}
	
	/** 
	 *用于map字符串
	 * @param key
	 * @return
	 */
	private static String getMessage(String key, Map<String,String> mapmessage){
		String str =mapmessage.get(key);
		return str;
	}
	public static String getSystemDateTime(String style){
		SimpleDateFormat format1 = new SimpleDateFormat(
				style, Locale.CHINA);
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.setTimeInMillis(System.currentTimeMillis());
		return format1.format(cal.getTime());
	}
}
