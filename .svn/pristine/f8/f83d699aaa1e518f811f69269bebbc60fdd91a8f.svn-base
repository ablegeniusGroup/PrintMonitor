package pos.dongwang.model;

import javafx.beans.property.*;

import java.io.Serializable;

/**
 * Created by lodi on 2017/11/9.
 */
public class TopButton implements Serializable {

    private LongProperty ID;

    private StringProperty NAME1;

    private StringProperty NAME2;

    public LongProperty idProperty() {
        return ID;
    }


    public long getID() {
        return ID.get();
    }

    public LongProperty IDProperty() {
        return ID;
    }

    public void setID(long ID) {
        this.ID.set(ID);
    }

    public String getNAME1() {
        return NAME1.get();
    }

    public StringProperty NAME1Property() {
        return NAME1;
    }

    public void setNAME1(String NAME1) {
        this.NAME1.set(NAME1);
    }

    public String getNAME2() {
        return NAME2.get();
    }

    public StringProperty NAME2Property() {
        return NAME2;
    }

    public void setNAME2(String NAME2) {
        this.NAME2.set(NAME2);
    }


    public TopButton() {
    }

    public TopButton(Long  ID, String NAME1, String NAME2) {
        this.ID = new SimpleLongProperty(ID);
        this.NAME1 = new SimpleStringProperty(NAME1);
        this.NAME2 =  new SimpleStringProperty(NAME2);
    }


}
