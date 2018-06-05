package pos.dongwang.print;

/**
 * Created by wenjing on 2017/10/22.
 */

/**
 *	打印机指令集
 * @AUTHOR_NAME 薛
 *
 */
public class InstructionSet {

    //通用指令集
    private static	final	char[]	INITIALIZE			= {27,64};		//初始化打印机
    private	static	final	char[]	CUTTER				= {27,105};		//切纸
    private static	final	char[]	OPENBOX				= {27,'p',0,60,240};//打开钱箱
    private static	final	char[]	GOHEAD5				= {27,100,5};	//进纸5行

    public static char[] getGOHEAD1() {
        return GOHEAD1;
    }

    private static	final	char[]	GOHEAD1				= {27,100,1};	//进纸1行
    private static	final	char[]	NEW_LINE			= {13,10};		//换行
    //热敏印机指令集 （适用于BIXOLON SRP-350II）
    private static	final	char[]	UPSIDE_DOWN 		= {27,123,1};	//反转180度打印
    private static	final	char[]	LEFT				= {27,97,0};	//靠左
    private static	final	char[]	CENTER				= {27,97,1};	//居中
    private static	final	char[]	RIGHT				= {27,97,2};	//靠右
    private static	final	char[]	BIG1				= {29,33,17};	//放大1倍(1D,21,11;最后的11代表的意思是纵横放大1倍)
    private static	final	char[]	BIG2				= {29,33,34};	//放大2倍(1D,21,22;最后的22代表的意思是纵横放大2倍)
    private static	final	char[]	BIG3				= {29,33,51};	//放大3倍(1D,21,33;最后的33代表的意思是纵横放大3倍)
    private static	final	char[]	BIG4				= {29,33,68};	//放大4倍(1D,21,44;最后的44代表的意思是纵横放大4倍)
    private static	final	char[]	HEIGHT_BIG1			= {29,33,1};	//高度放大1倍(1D,21,01;最后的01代表的意思是纵向放大1倍)
    private static	final	char[]	BIG_END				= {29,33,0};	//终止放大(1D,21,0)
    private static	final	char[]	BOLD				= {27,69,1};	//加重
    private static	final	char[]	BOLDOFF				= {27,69,0};	//取消加重
    private static	final	char[]	UNDERLINE			= {27,45,1};	//下划线
    private static	final	char[]	UNDERLINEOFF		= {27,45,0};	//取消下划线
    //针式印机指令集 （适用于BIXOLON SRP-375II）
    private static	final	char[]	PRINT_LOGO			= {28,112,1,0};		//读取位图1
    private static	final	char[]	PRINT_LOGO1			= {28,112,2,0};		//读取位图2
    private static	final	char[]	PRINT_LOGO2			= {28,112,3,0};		//读取位图3
    private static	final	char[]	PRINT_LOGO3			= {28,112,4,0};		//读取位图4
    private static	final	char[]	PRINT_LOGO4			= {28,112,5,0};		//读取位图5
    private static	final	char[]	FONT_COLOR_BLACK	= {27,114,0};	//黑色
    private static	final	char[]	FONT_COLOR_RED		= {27,114,1};	//红色
    private static	final	char[]	FONT_SIZE_LARGER_1	= {28,33,8};	//放大全角
    private static	final	char[]	FONT_SIZE_LARGER_2	= {27,33,16};	//放大半角
    private static	final	char[]	FONT_SIZE_LARGEST	= {28,87,1};	//最大
    private static	final	char[]	FONT_SIZE_NORMAL	= {28,87,0};	//正常大小
    private static	final	char[]	FONT_SIZE_LARGEST_1	= {28,33,12};	//最大,放大全角 （28,33,12）纵横都放大（28,33,8）倍高（23,33,4）倍宽
    private static	final	char[]	FONT_SIZE_LARGEST_2	= {27,33,48};	//最大,放大半角（27,33,48）纵横都放大（27,33,16）倍高（23,37,32）倍宽
    private static	final	char[]	FONT_SIZE_NORMAL_1	= {28,33,0};	//全角正常大小
    private static	final	char[]	FONT_SIZE_NORMAL_2	= {27,33,0};	//半角正常大小
    private static	final	char[]	FONT_BOLD			= {27,33,8};	//打印粗体命令
    private static	final	char[]	CHINESE_MODEL		= {28,38};		//进入中文模式
    private static	final	char[]	BIG4_END			= {29,33,0};

    /**
     * @return 进纸5行
     */
    public static char[] getGoHead5() {
        return GOHEAD5;
    }

    /**
     * @return 切纸命令
     */
    public static char[] getCutter() {
        return CUTTER;
    }
    /**
     * @return 居中命令
     */
    public static char[] getCenter() {
        return CENTER;
    }
    /**
     * @return 靠左命令
     */
    public static char[] getLeft() {
        return LEFT;
    }
    /**
     * @return 靠右命令
     */
    public static char[] getRight() {
        return RIGHT;
    }
    /**
     * @return 换行命令
     */
    public static char[] getNewLine() {
        return NEW_LINE;
    }
    /**
     * @return 下载打印位图
     */
    public static char[] getPrintLogo() {
        return PRINT_LOGO;
    }
    /**
     * @return 打印红色命令
     */
    public static char[] getFontColorRed() {
        return FONT_COLOR_RED;
    }
    /**
     * @return 打印黑色命令
     */
    public static char[] getFontColorBlack() {
        return FONT_COLOR_BLACK;
    }
    /**
     * @return 全角放大命令
     */
    public static char[] getFontSizeLarger1() {
        return FONT_SIZE_LARGER_1;
    }
    /**
     * @return 半角放大命令
     */
    public static char[] getFontSizeLarger2() {
        return FONT_SIZE_LARGER_2;
    }
    /**
     * @return 最大命令
     */
    public static char[] getFontSizeLargest() {
        return FONT_SIZE_LARGEST;
    }

    /**
     * @return 字体恢复正常大小
     */
    public static char[] getFontSizeNormal() {
        return FONT_SIZE_NORMAL;
    }
    /**
     * @return 全角字体放至最大
     */
    public static char[] getFontSizeLargest1() {
        return FONT_SIZE_LARGEST_1;
    }
    /**
     * @return 半角字体放至最大
     */
    public static char[] getFontSizeLargest2() {
        return FONT_SIZE_LARGEST_2;
    }
    /**
     * @return 全角字体恢复正常大小
     */
    public static char[] getFontSizeNormal1() {
        return FONT_SIZE_NORMAL_1;
    }
    /**
     * @return 半角字体恢复正常大小
     */
    public static char[] getFontSizeNormal2() {
        return FONT_SIZE_NORMAL_2;
    }
    /**
     * @return 打开钱箱命令
     */
    public static char[] getOpenbox() {
        return OPENBOX;
    }
    /**
     * @return 打印粗体命令
     */
    public static char[] getFontBold() {
        return FONT_BOLD;
    }
    /**
     * @return 中文模式命令
     */
    public static char[] getChineseModel() {
        return CHINESE_MODEL;
    }
    /**
     * @return 初始化打印机
     */
    public static char[] getInitialize() {
        return INITIALIZE;
    }

    /**
     * @return 反转打印命令
     */
    public static char[] getUpsideDown() {
        return UPSIDE_DOWN;
    }

    /**
     * @return 放大1倍
     */
    public static char[] getBig1() {
        return BIG1;
    }

    /**
     * @return 放大2倍
     */
    public static char[] getBig2()
    {
        return BIG2;
    }

    /**
     * @return 放大3倍
     */
    public static char[] getBig3() {
        return BIG3;
    }

    /**
     * @return 放大4倍
     */
    public static char[] getBig4() {
        return BIG4;
    }

    /**
     * @return 终止放大
     */
    public static char[] getBigEnd()
    {
        return BIG_END;
    }

    /**
     * @return 纵向放大一倍
     */
    public static char[] getHeightBig1() {
        return HEIGHT_BIG1;
    }

    /**
     * 加重
     * @return
     */
    public static char[] getBold() {
        return BOLD;
    }

    /**
     * 关闭加重
     * @return
     */
    public static char[] getBoldoff() {
        return BOLDOFF;
    }

    /**
     * 下划线
     * @return
     */
    public static char[] getUnderline() {
        return UNDERLINE;
    }

    /**
     * 关闭下划线
     * @return
     */
    public static char[] getUnderlineoff() {
        return UNDERLINEOFF;
    }

    public static char[] getPrintLogo1() {
        return PRINT_LOGO1;
    }

    public static char[] getPrintLogo2() {
        return PRINT_LOGO2;
    }

    public static char[] getPrintLogo3() {
        return PRINT_LOGO3;
    }

    public static char[] getPrintLogo4() {
        return PRINT_LOGO4;
    }

    public static char[] getBig4End()
    {
        return BIG4_END;
    }

}
