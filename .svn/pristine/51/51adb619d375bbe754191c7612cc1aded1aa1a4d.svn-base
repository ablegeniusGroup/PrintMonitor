package pos.dongwang.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

/**
 * Created by lodi on 2017/11/10.
 */
public class TGoods implements Serializable {

 private StringProperty goodsNo;

 private StringProperty goodName1;

    public StringProperty goodsNoProperty() {
        return goodsNo;
    }

    public String getGoodsNo() {
        return goodsNo.get();
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo.set(goodsNo);
    }

    public StringProperty goodName1Property() {
        return goodName1;
    }

    public String getGoodName1() {
        return goodName1.get();
    }

    public void setGoodName1(String goodName1) {
        this.goodName1.set(goodName1);
    }

    public TGoods(String goodsNo, String goodName1) {
        this.goodsNo =  new SimpleStringProperty(goodsNo);
        this.goodName1 = new SimpleStringProperty(goodName1);
    }
}
