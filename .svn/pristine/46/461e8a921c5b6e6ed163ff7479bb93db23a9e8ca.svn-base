package pos.dongwang.properties;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by wenjing on 2017/10/24.
 */
public class SettingModel {
    private StringProperty key;
    private StringProperty value;
    private StringProperty desc;

    public SettingModel(String key, String value, String desc) {
        this.key = new SimpleStringProperty(key);
        this.value = new SimpleStringProperty(value);
        this.desc = new SimpleStringProperty(desc);
    }

    public String getKey() {
        return key.get();
    }

    public StringProperty keyProperty() {
        return key;
    }

    public void setKey(String key) {
        this.key.set(key);
    }

    public String getValue() {
        return value.get();
    }

    public StringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public String getDesc() {
        return desc.get();
    }

    public StringProperty descProperty() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc.set(desc);
    }
}
