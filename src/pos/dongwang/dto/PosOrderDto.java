package pos.dongwang.dto;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wenjing on 2017/10/20.
 */
public class PosOrderDto {

    private String bill_no; //主单号

    private String sub_no; //副单号

    private String type; //账单类型

    private String table_no; //桌号

    private String goodsno; //商品编号

    private Long order_idx; //商品排序

    private String name1; //商品名稱

    private String print_info; //印機地址

    private Date rt_op_date; //入单日期

    private Date rt_op_time; //入单时间

    private String s_code; //操作员

    private Integer seal_count; //數量

    private BigDecimal amt; //金额

    private String print_state; //打印狀態

    private String single_prt;//TRUE: 單飛，FALSE:總飛

    private String t_kic_msg; //廚房訊息

    private String pos_id; //客戶端ID

    private String print_name; //打印機編號

    private String barcode; //分店編號

    private String staff; //下單員工

    private String zone; //台層區域


    private String r_desc; //取消原因

    private String print_msg; //綜合信息

    private String region_id; //业务线

    private Integer person_num; //人數

    private String att_name; //人數

    private String order_type; //單據

    private String in_date;

    public PosOrderDto() {
    }

    public String getBill_no() {
        return bill_no;
    }

    public void setBill_no(String bill_no) {
        this.bill_no = bill_no;
    }

    public String getSub_no() {
        return sub_no;
    }

    public void setSub_no(String sub_no) {
        this.sub_no = sub_no;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTable_no() {
        return table_no;
    }

    public void setTable_no(String table_no) {
        this.table_no = table_no;
    }

    public String getGoodsno() {
        return goodsno;
    }

    public void setGoodsno(String goodsno) {
        this.goodsno = goodsno;
    }

    public Long getOrder_idx() {
        return order_idx;
    }

    public void setOrder_idx(Long order_idx) {
        this.order_idx = order_idx;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getPrint_info() {
        return print_info;
    }

    public void setPrint_info(String print_info) {
        this.print_info = print_info;
    }

    public Date getRt_op_date() {
        return rt_op_date;
    }

    public void setRt_op_date(Date rt_op_date) {
        this.rt_op_date = rt_op_date;
    }

    public Date getRt_op_time() {
        return rt_op_time;
    }

    public void setRt_op_time(Date rt_op_time) {
        this.rt_op_time = rt_op_time;
    }

    public String getS_code() {
        return s_code;
    }

    public void setS_code(String s_code) {
        this.s_code = s_code;
    }

    public Integer getSeal_count() {
        return seal_count;
    }

    public void setSeal_count(Integer seal_count) {
        this.seal_count = seal_count;
    }

    public BigDecimal getAmt() {
        return amt;
    }

    public void setAmt(BigDecimal amt) {
        this.amt = amt;
    }

    public String getPrint_state() {
        return print_state;
    }

    public void setPrint_state(String print_state) {
        this.print_state = print_state;
    }

    public String getSingle_prt() {
        return single_prt;
    }

    public void setSingle_prt(String single_prt) {
        this.single_prt = single_prt;
    }

    public String getT_kic_msg() {
        return t_kic_msg;
    }

    public void setT_kic_msg(String t_kic_msg) {
        this.t_kic_msg = t_kic_msg;
    }


    public String getPos_id() {
        return pos_id;
    }

    public void setPos_id(String pos_id) {
        this.pos_id = pos_id;
    }


    public String getPrint_name() {
        return print_name;
    }

    public void setPrint_name(String print_name) {
        this.print_name = print_name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getR_desc() {
        return r_desc;
    }

    public void setR_desc(String r_desc) {
        this.r_desc = r_desc;
    }

    public String getPrint_msg() {
        return print_msg;
    }

    public void setPrint_msg(String print_msg) {
        this.print_msg = print_msg;
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public Integer getPerson_num() {
        return person_num;
    }

    public void setPerson_num(Integer person_num) {
        this.person_num = person_num;
    }

    public String getAtt_name() {
        return att_name;
    }

    public void setAtt_name(String att_name) {
        this.att_name = att_name;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getIn_date() {
        return in_date;
    }

    public void setIn_date(String in_date) {
        this.in_date = in_date;
    }
}