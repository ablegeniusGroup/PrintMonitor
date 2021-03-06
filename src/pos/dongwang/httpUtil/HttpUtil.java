package pos.dongwang.httpUtil;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import pos.dongwang.util.AppUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by wenjing on 2017/10/27.
 */
public class HttpUtil {

    public static String post(String url, List<NameValuePair> params) throws Exception {
        String body = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();


            // Post请求
            HttpPost httppost = new HttpPost(url);

            // 设置参數

            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        // 发送请求
            CloseableHttpResponse httpresponse = httpClient.execute(httppost);
        try {
            // 获取返回數据
            HttpEntity entity = httpresponse.getEntity();
            body = EntityUtils.toString(entity);
            if (entity != null) {
                EntityUtils.consume(entity);
            }
        } finally {
            httpresponse.close();
        }

        return body;
    }
}
