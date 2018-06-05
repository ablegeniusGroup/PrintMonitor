package pos.dongwang.dto;

import java.util.Date;

/**
 * Created by lodi on 2018/1/5.
 */
public class OrderHangDto {

    private String billNo;

    private String tableNum;


    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getTableNum() {
        return tableNum;
    }

    public void setTableNum(String tableNum) {
        this.tableNum = tableNum;
    }

}
