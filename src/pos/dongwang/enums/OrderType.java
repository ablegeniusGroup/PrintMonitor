package pos.dongwang.enums;

/**
 * Created by lodi on 2017/11/23.
 */
public enum OrderType {
    ALLORDER("全部"), FIRSTORDER("首單"), ADDORDER("加單"), TAILORDER("尾單"),HANG("掛起");

    private String value;

    OrderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }





}
