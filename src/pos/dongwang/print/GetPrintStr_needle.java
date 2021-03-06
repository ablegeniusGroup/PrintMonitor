package pos.dongwang.print;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class GetPrintStr_needle {
	private static int[]	len;
	private static int		usetype ;//1打印，0显示
	private static int		DBClength	= 16;//半角占的点阵數
	private static int		SBClength	= 24;//全角占的点阵數
	
	//处理打印數据
	public static String TranslateTableStyle(String[] style, List<String> list_1, int type, Map<String,String> mapmessage){
		usetype = type;
		String printall = "";
		for(int i = 1 ; i < style.length ; i++){
			if(style[i].startsWith("!"))//不翻译的内容
				continue;
			if(style[i].startsWith("~"))//设定列宽（后台使用的是针式打印机，这里实际上是每一列的点阵數）
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
					if(i>=orderStr.length)
						break;
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
		for(int i =0;i<num/DBClength ;i++){
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
		for(int i =0;i<num/SBClength ;i++){
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
	 * 2013-09-25 薛修改了算法
	 * 处理列模式，仅适用于针式打印机
	 * @return
	 */
	private static String dealColStyle(String colstr, Map<String,String> mapmessage){
		if(colstr.startsWith("$")){
			colstr=Delp_col(colstr,mapmessage);
		}
		String space	= "                                          ";//40个空格
		String details[]	=  colstr.split("\\|");
		String prnstr	= "";
		String str		= "";
		int		strwid	= 0;
		String message;
		for(int j=0;j<len.length-1;j++){
			if(j>=details.length)
				break;
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
			//获取对应列要打印的字符串的点阵數
			strwid=getStringWidth(details[j]);
			dis=len[j+1] - strwid;
			if(j>0 && dis<0){
				while(dis < 0 && details[j-1].endsWith(" ")){//如果字符串 的点阵數大于列的点阵數
					//如果有并且列的点阵數仍小于字符串的点阵數，去掉前一列的最后一个空格
					details[j-1] = details[j-1].substring(0,details[j-1].length()-1);
					dis += DBClength;
				}
				//如果差值仍然小于0，且当前列不是最后一列,截取字符串，直到差值小于0
				if(j != len.length -2){
					String strX = null;
					while(dis < 0 && details[j].length()-1 > 0){
						strX = details[j].substring(details[j].length()-2);
						details[j] = details[j].substring(0,details[j].length()-2);
						dis += getStringWidth(strX);
					}
				}
			}
			else if(dis > 0){//如果列的点阵數大于字符串的点阵數
				if(right){//右对齐，左补空格
					details[j]=space.substring(0,dis/DBClength)+details[j];
				}
				else{//左对齐，右补空格
					details[j]=details[j]+space.substring(0,dis/DBClength);
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
	
	/**
	 * 获取字符串所占的点阵數
	 * @param str 待验证的字符串
	 * @return 占用的点阵數
	 */
	public static int getStringWidth(String str) {
		String string = null;
		int length = 0;
		int bytLength = 0;
		int	Stringwith = 0;
		for(int i =0 ;i<=str.length()-1;i++){
			string = str.substring(i,i+1);
			length = string.length();
			bytLength = string.getBytes().length;        
			//该字符是半角
			if(bytLength == length) {
				Stringwith+= DBClength;  
			}
			//该字符是全角
			else{
				Stringwith += SBClength;
			}        
		}
		return Stringwith;
	}

	public static String getSystemDateTime(String style){
		SimpleDateFormat format1 = new SimpleDateFormat(
				style, Locale.CHINA);
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.setTimeInMillis(System.currentTimeMillis());
		return format1.format(cal.getTime());
	}
}
