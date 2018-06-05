package pos.dongwang.enums;

/**
 * Created by wenjing on 2017/10/20.
 */
public enum PrintStateEnum {
    UNPRINT("未打印", "UNPRINT"), PREPRINT("等待打印", "PREPRINT"), PRINTED("已經打印", "PRINTED"), CANCEL("取消", "CANCEL"),
    CHANGE("換菜", "CHANGE"), FREE("免費", "FREE"), MSG("廚房訊息" , "MSG"), NULL("null",null),HANG("掛起","HANG"),LEAVE("離座","LEAVE");


    private String name;

    private String value;

    PrintStateEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static PrintStateEnum getPrintStateEnumByValue(String value){
        if(value == null){
            return NULL;
        }
        for(PrintStateEnum stateEnum : PrintStateEnum.values()){
            if(stateEnum.getValue() != null){
                if((stateEnum.getValue().equals(value))){
                    return stateEnum;
                }
            }

        }
        return null;
    }
}
