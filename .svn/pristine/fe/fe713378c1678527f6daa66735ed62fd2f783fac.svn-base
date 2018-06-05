package pos.dongwang.util;

import javafx.concurrent.Task;
import org.apache.http.NameValuePair;
import pos.dongwang.httpUtil.HttpUtil;

import java.util.List;

/**
 * Created by lodi on 2017/12/28.
 */
public class SendUtils {
     /**
     * 異步請求
     * @param url
     * @param nvps
     */
     public static void sendRequest(String url, List<NameValuePair> nvps) {
        Task<Void> progressTask = new Task<Void>() {
            @Override
            protected void succeeded() {

                super.succeeded();

                updateMessage("Succeeded");

            }
            @Override
            protected void cancelled() {

                super.cancelled();

                updateMessage("Cancelled");

            }
            @Override
            protected void failed() {

                super.failed();

                updateMessage("Failed");

            }
            @Override
            protected Void call() {
                try {
                    HttpUtil.post(url, nvps);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateMessage("Finish");
                return null;
            }

        };

        new Thread(progressTask).start();
    }
}
