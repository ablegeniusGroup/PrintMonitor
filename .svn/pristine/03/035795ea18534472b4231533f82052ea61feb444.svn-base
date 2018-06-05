package pos.dongwang.properties;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by wenjing on 2017/10/23.
 */
public class ReadProperties {

    /**
     * 讀取
     *
     * @return
     * @throws IOException
     */
    public static ObservableList<SettingModel> read() {
        ObservableList<SettingModel> settings = FXCollections.observableArrayList();
        try {
            String currpath = System.getProperty("user.dir");
            File settingFile = new File(currpath + File.separator + "setting.properties");
            if (!settingFile.exists()) {
                settingFile.createNewFile();
            } else {

                FileInputStream input1 = new FileInputStream(currpath + File.separator + "setting.properties");//读取代码

                SafeProperties safeProp1 = new SafeProperties();
                safeProp1.load(input1);
                Iterator<String> it = safeProp1.stringPropertyNames().iterator();

                while (it.hasNext()) {
                    String key = it.next();
                    // System.out.println(key + ":" + safeProp1.getProperty(key));
                    String[] values = safeProp1.getProperty(key).split("@comment@");
                    settings.add(new SettingModel(key, values[0], values[1]));

                }

                //  settings.forEach(settingModel -> System.out.println(settingModel.getKey() + " ;value: " + settingModel.getValue() + " ;desc: " + settingModel.getDesc()));


                input1.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return settings;
    }

    /**
     * 根據KEY值查詢
     *
     * @param key
     * @return
     */
    public static String readStringByKey(String key) {
        String value = "";
        try {
            String currpath = System.getProperty("user.dir");
            File settingFile = new File(currpath + File.separator + "setting.properties");
            if (!settingFile.exists()) {
                settingFile.createNewFile();
            } else {

                FileInputStream input1 = new FileInputStream(currpath + File.separator + "setting.properties");//读取代码

                SafeProperties safeProp1 = new SafeProperties();
                safeProp1.load(input1);

                String[] values = safeProp1.getProperty(key).split("@comment@");
                value = values[0];
                input1.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static Integer readIntegerByKey(String key) {
        Integer value = 0;
        try {
            String currpath = System.getProperty("user.dir");
            File settingFile = new File(currpath + File.separator + "setting.properties");
            if (!settingFile.exists()) {
                settingFile.createNewFile();
            } else {

                FileInputStream input1 = new FileInputStream(currpath + File.separator + "setting.properties");//读取代码

                SafeProperties safeProp1 = new SafeProperties();
                safeProp1.load(input1);

                String[] values = safeProp1.getProperty(key).split("@comment@");
                value =  Integer.parseInt(values[0]);
                input1.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 增改
     *
     * @param settingModel
     * @throws IOException
     */
    public static void setProperties(SettingModel settingModel) {
        try {
            String currpath = System.getProperty("user.dir");
            FileInputStream input = new FileInputStream(currpath + File.separator + "setting.properties");//項目根目錄下的配置文件
            SafeProperties safeProp = new SafeProperties();
            safeProp.load(input);
            input.close();
            safeProp.setProperty(settingModel.getKey(), settingModel.getValue() + "@comment@" + settingModel.getDesc());

            FileOutputStream output = new FileOutputStream(currpath + File.separator + "setting.properties");
            safeProp.store(output, null);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 刪除
     *
     * @param settingModel
     * @throws IOException
     */
    public static void reomveProperties(SettingModel settingModel) {
        try {
            String currpath = System.getProperty("user.dir");
            FileInputStream input = new FileInputStream(currpath + File.separator + "setting.properties");//項目根目錄下的配置文件
            SafeProperties safeProp = new SafeProperties();
            safeProp.load(input);
            input.close();
            safeProp.remove(settingModel.getKey());

            FileOutputStream output = new FileOutputStream(currpath + File.separator + "setting.properties");
            safeProp.store(output, null);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {

        ReadProperties.setProperties(new SettingModel("dbpasswd", "Taoheung2014", "數據庫密碼"));
        ReadProperties.read();


    }
}
