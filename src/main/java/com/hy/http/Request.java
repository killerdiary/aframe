package com.hy.http;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author HeYan
 * @title
 * @time 2015/11/18 18:27
 */
public abstract class Request {

    public interface Method {
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
    }

    private static final String[] METHODS = {"GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "TRACE"};

    private int method;

    public interface ContentType {
        String json = "";
    }

    private String path;

    public void start() {
        try {
            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true, 默认情况下是false;
            con.setDoOutput(method > 0);
            // Post 请求不能使用缓存
            con.setUseCaches(false);
            // 设定传送的内容类型是可序列化的java对象
            // (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
            con.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            // 设定请求的方法为"POST"，默认是GET
            con.setRequestMethod(METHODS[method]);
            // 连接，上面对urlConn的所有配置必须要在connect之前完成，
            con.connect();
            if (method > 0) {
                // 此处getOutputStream会隐含的进行connect (即：如同调用上面的connect()方法，
                // 所以在开发中不调用上述的connect()也可以)。
                OutputStream out = con.getOutputStream();
                // 现在通过输出流对象构建对象输出流对象，以实现输出可序列化的对象。
                ObjectOutputStream oos = new ObjectOutputStream(out);
                // 向对象输出流写出数据，这些数据将存到内存缓冲区中
                oos.writeObject(new String("id=1"));
                // 刷新对象输出流，将任何字节都写入潜在的流中（些处为ObjectOutputStream）
                oos.flush();
                // 关闭流对象。此时，不能再向对象输出流写入任何数据，先前写入的数据存在于内存缓冲区中,
                // 再调用下边的getInputStream()函数时才把准备好的http请求正式发送到服务器
                oos.close();
            }
            // 调用HttpURLConnection连接对象的getInputStream()函数,
            // 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
            InputStream in = con.getInputStream(); // <===注意，实际发送请求的代码段就在这里
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[4096];
            for (int n; (n = in.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
            Log.e("TEST", out.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();//URL地址异常
        } catch (IOException e) {
            e.printStackTrace(); //URL连接异常
        }
    }
    private String params;

    public void setParams(String params) {
        this.params = params;
    }

    private String getParamString() {

        return null;
    }
}
