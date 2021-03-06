package pos.dongwang.model;

import javafx.beans.property.*;
import pos.dongwang.util.AppUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wenjing on 2017/10/17.
 */
public class PosOrder implements Serializable {

    private StringProperty bill_no; //主单号

    private StringProperty sub_no; //副单号

    private StringProperty type; //账单类型

    private StringProperty table_no; //桌号

    private StringProperty goodsno; //商品编号

    private LongProperty order_idx; //商品排序

    private StringProperty name1; //商品名稱

    private StringProperty print_info; //印機地址

    private StringProperty rt_op_date; //入单日期

    private StringProperty rt_op_time; //入单时间

    private StringProperty s_code; //操作员

    private IntegerProperty seal_count; //數量

    private DoubleProperty amt; //金额

    private StringProperty print_state; //打印狀態

    private StringProperty single_prt;//TRUE: 單飛，FALSE:總飛

    private StringProperty t_kic_msg;//廚房訊息

    private StringProperty pos_id; //客戶端ID

    private StringProperty print_name; //打印機編號

    private StringProperty barcode; //分店編號

    private StringProperty staff; //下單員工

    private StringProperty zone; //台層區域

    private StringProperty r_desc; //取消原因

    private StringProperty print_msg; //綜合信息

    private StringProperty region_id; //业务线

    private IntegerProperty person_num; //人數

    private StringProperty att_name; //人數

    private StringProperty order_type; //單據類型

    private StringProperty in_date;

    private Long hang_date;

    public PosOrder(String bill_no, String sub_no, String type, String table_no, String goodsno, Long order_idx, String name1, String print_info, String rt_op_date, String rt_op_time, String s_code, Integer seal_count, Double amt, String print_state, String single_prt, String t_kic_msg, String pos_id, String print_name, String barcode, String staff, String zone, String r_desc , String print_msg, String region_id,Integer  person_num, String att_name, String order_type,String in_date) {
        this.bill_no = new SimpleStringProperty(bill_no);
        this.sub_no = new SimpleStringProperty(sub_no);
        this.type = new SimpleStringProperty(type);
        this.table_no = new SimpleStringProperty(table_no);
        this.goodsno = new SimpleStringProperty(goodsno);
        this.order_idx = new SimpleLongProperty(order_idx);
        this.name1 = new SimpleStringProperty(name1);
        this.print_info = new SimpleStringProperty(print_info);
        this.rt_op_date = new SimpleStringProperty(rt_op_date);
        this.rt_op_time = new SimpleStringProperty(rt_op_time);
        this.s_code = new SimpleStringProperty(s_code);
        this.seal_count = new SimpleIntegerProperty(seal_count);
        this.amt = new SimpleDoubleProperty(amt);
        this.print_state = new SimpleStringProperty(print_state);
        this.single_prt = new SimpleStringProperty(single_prt);
        this.t_kic_msg = new SimpleStringProperty(t_kic_msg);
        this.pos_id = new SimpleStringProperty(pos_id);
        this.print_name = new SimpleStringProperty(print_name);
        this.barcode = new SimpleStringProperty(barcode);
        this.staff = new SimpleStringProperty(staff);
        this.zone = new SimpleStringProperty(zone);
        this.r_desc = new SimpleStringProperty(r_desc);
        this.print_msg = new SimpleStringProperty(print_msg);
        this.region_id = new SimpleStringProperty(region_id);
        this.person_num = new SimpleIntegerProperty(person_num);
        this.att_name = new SimpleStringProperty(att_name);
        this.order_type = new SimpleStringProperty(order_type);
        this.in_date = new SimpleStringProperty(in_date);

    }



    public PosOrder(String bill_no, String table_no, String print_name, String barcode, String staff, String zone, String name1, Integer seal_count, String pos_id) {
        this.bill_no = new SimpleStringProperty(bill_no);
        this.table_no = new SimpleStringProperty(table_no);
        this.print_name = new SimpleStringProperty(print_name);
        this.barcode = new SimpleStringProperty(barcode);
        this.staff = new SimpleStringProperty(staff);
        this.zone = new SimpleStringProperty(zone);
        this.seal_count = new SimpleIntegerProperty(seal_count);
        this.name1 = new SimpleStringProperty(name1);
        this.pos_id = new SimpleStringProperty(pos_id);

    }

    public String getBill_no() {
        return AppUtils.isBlank(bill_no.get()) ? "" : bill_no.get();
    }

    public StringProperty bill_noProperty() {
        return bill_no;
    }

    public void setBill_no(String bill_no) {
        this.bill_no.set(bill_no);
    }

    public String getSub_no() {
        return sub_no.get();
    }

    public StringProperty sub_noProperty() {
        return sub_no;
    }

    public void setSub_no(String sub_no) {
        this.sub_no.set(sub_no);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getTable_no() {
        return AppUtils.isBlank(table_no.get())? "" : table_no.get();
    }

    public StringProperty table_noProperty() {
        return table_no;
    }

    public void setTable_no(String table_no) {
        this.table_no.set(table_no);
    }

    public String getGoodsno() {
        return goodsno.get();
    }

    public StringProperty goodsnoProperty() {
        return goodsno;
    }

    public void setGoodsno(String goodsno) {
        this.goodsno.set(goodsno);
    }

    public long getOrder_idx() {
        return order_idx.get();
    }

    public LongProperty order_idxProperty() {
        return order_idx;
    }

    public void setOrder_idx(long order_idx) {
        this.order_idx.set(order_idx);
    }

    public String getName1() {
        return AppUtils.isBlank(name1.get())? "" : name1.get();
    }

    public StringProperty name1Property() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1.set(name1);
    }

    public String getPrint_info() {
        return print_info.get();
    }

    public StringProperty print_infoProperty() {
        return print_info;
    }

    public void setPrint_info(String print_info) {
        this.print_info.set(print_info);
    }

    public String getRt_op_date() {
        return rt_op_date.get();
    }

    public StringProperty rt_op_dateProperty() {
        return rt_op_date;
    }

    public void setRt_op_date(String rt_op_date) {
        this.rt_op_date.set(rt_op_date);
    }

    public String getRt_op_time() {
        return rt_op_time.get();
    }

    public StringProperty rt_op_timeProperty() {
        return rt_op_time;
    }

    public void setRt_op_time(String rt_op_time) {
        this.rt_op_time.set(rt_op_time);
    }

    public String getS_code() {
        return s_code.get();
    }

    public StringProperty s_codeProperty() {
        return s_code;
    }

    public void setS_code(String s_code) {
        this.s_code.set(s_code);
    }

    public int getSeal_count() {
        return AppUtils.isBlank(seal_count.get())? 0 : seal_count.get();
    }

    public IntegerProperty seal_countProperty() {
        return seal_count;
    }

    public void setSeal_count(int seal_count) {
        this.seal_count.set(seal_count);
    }

    public double getAmt() {
        return amt.get();
    }

    public DoubleProperty amtProperty() {
        return amt;
    }

    public void setAmt(double amt) {
        this.amt.set(amt);
    }


    public String getPrint_state() {
        return print_state.get();
    }

    public StringProperty print_stateProperty() {
        return print_state;
    }

    public void setPrint_state(String print_state) {
        this.print_state.set(print_state);
    }

    public String getSingle_prt() {
        return single_prt.get();
    }

    public StringProperty single_prtProperty() {
        return single_prt;
    }

    public void setSingle_prt(String single_prt) {
        this.single_prt.set(single_prt);
    }

    public String getT_kic_msg() {

        return AppUtils.isBlank(t_kic_msg)? "" : t_kic_msg.get();
    }

    public StringProperty t_kic_msgProperty() {
        return t_kic_msg;
    }

    public void setT_kic_msg(String t_kic_msg) {
        this.t_kic_msg.set(t_kic_msg);
    }

    public String getPos_id() {
        return AppUtils.isBlank(pos_id.get())? "" : pos_id.get();
    }

    public StringProperty pos_idProperty() {
        return pos_id;
    }

    public void setPos_id(String pos_id) {
        this.pos_id.set(pos_id);
    }

    public String getPrint_name() {
        return AppUtils.isBlank(print_name.get())? "" : print_name.get();
    }

    public StringProperty print_nameProperty() {
        return print_name;
    }

    public void setPrint_name(String print_name) {
        this.print_name.set(print_name);
    }

    public String getBarcode() {
        return AppUtils.isBlank(barcode.get())? "" : barcode.get();
    }

    public StringProperty barcodeProperty() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode.set(barcode);
    }

    public String getStaff() {
        return AppUtils.isBlank(staff.get())? "" : staff.get();
    }

    public StringProperty staffProperty() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff.set(staff);
    }

    public String getZone() {
        return AppUtils.isBlank(zone.get()) ? "" : zone.get();
    }

    public StringProperty zoneProperty() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone.set(zone);
    }

    public String getR_desc() {
        return r_desc.get();
    }

    public StringProperty r_descProperty() {
        return r_desc;
    }

    public void setR_desc(String r_desc) {
        this.r_desc.set(r_desc);
    }

    public String getPrint_msg() {
        return print_msg.get();
    }

    public StringProperty print_msgProperty() {
        return print_msg;
    }

    public void setPrint_msg(String print_msg) {
        this.print_msg.set(print_msg);
    }

    public String getRegion_id() {
        return region_id.get();
    }

    public StringProperty region_idProperty() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id.set(region_id);
    }

    public int getPerson_num() {
        return person_num.get();
    }

    public IntegerProperty person_numProperty() {
        return person_num;
    }

    public void setPerson_num(int person_num) {
        this.person_num.set(person_num);
    }

    public String getAtt_name() {
        return att_name.get();
    }

    public StringProperty att_nameProperty() {
        return att_name;
    }

    public void setAtt_name(String att_name) {
        this.att_name.set(att_name);
    }

    public String getOrder_type() {
        return order_type.get();
    }

    public StringProperty order_typeProperty() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type.set(order_type);
    }

    public String getIn_date() {
        return in_date.get();
    }

    public StringProperty in_dateProperty() {
        return in_date;
    }

    public void setIn_date(String in_date) {
        this.in_date.set(in_date);
    }

    public Long getHang_date() {
        return hang_date;
    }

    public void setHang_date(Long hang_date) {
        this.hang_date = hang_date;
    }
}