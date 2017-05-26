package com.hy.http;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.UiThread;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hy.frame.R;
import com.hy.frame.bean.DownFile;
import com.hy.frame.bean.ResultInfo;
import com.hy.frame.util.HyUtil;
import com.hy.frame.util.MyLog;
import com.hy.frame.view.LoadingDialog;
import com.hy.http.file.Binary;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 网络请求，不能直接使用<br/>
 * 只能继承
 *
 * @author HeYan
 * @time 2014年8月9日 下午3:43:44
 */
public abstract class MyHttpClient {
    private Context context;
    private List<IMyHttpListener> listeners;
    protected boolean showDialog;// 显示加载对话框
    protected LoadingDialog loadingDialog;
    //    private String signatures;// 密钥KEY
    private String host;// 域名
    //private final static int TIME_OUT = 60 * 1000;
    //private String contentType, userAgent, accept;
    private Map<String, String> headerParams;
    //    private int qid;//队列ID
    protected boolean isDestroy;
    //    private RequestQueue requestQueue;
//    private CacheMode cacheMode;
//    private Request request;
    private OkHttpClient client;
    private Handler handler;
    public static final int REQUEST_TYPE_JSON = 0;
    public static final int REQUEST_TYPE_JSONARRAY = 1;
    public static final int REQUEST_TYPE_STRING = 2;
    public static final int REQUEST_TYPE_FILE = 3;
    public Set<Integer> queues;
    private String cerName = ""; //https签名证书name

    public MyHttpClient(Context context, IMyHttpListener listener, String host) {
        this(context, listener, host, REQUEST_TYPE_JSON);
    }

    public MyHttpClient(Context context, IMyHttpListener listener, String host, int requestType) {
        super();
        if (context == null || host == null) {
            MyLog.INSTANCE.i("MyHttpClient init error!");
            return;
        }
        this.context = context;
        this.host = host;
        this.handler = new Handler(Looper.getMainLooper());
        setListener(listener);
    }

    /**
     * @param context  上下文
     * @param listener 舰艇
     * @param host     域名
     * @param verify   开启验证
     */
    public MyHttpClient(Context context, IMyHttpListener listener, String host, boolean verify) {
        this(context, listener, host);
        if (verify) {
            PackageInfo pi = HyUtil.Companion.getAppVersion(context);
            if (pi != null) {
                if (pi.signatures != null && pi.signatures.length > 0)
                    addHeader("signatures", pi.signatures[0].toCharsString());
            }
        }
    }

    public void setListener(IMyHttpListener listener) {
        if (this.listeners != null)
            this.listeners.clear();
        addListener(listener);
    }

    /**
     * 慎用
     *
     * @param listener
     */
    @Deprecated
    public void addListener(IMyHttpListener listener) {
        if (this.listeners == null)
            this.listeners = new ArrayList<>();
        this.listeners.add(listener);
    }

    private void addQueue(int requestCode) {
        if (this.queues == null)
            this.queues = new HashSet<>();
        this.queues.add(requestCode);
    }

    private boolean hasQueue(int requestCode) {
        if (queues != null)
            return queues.contains(requestCode);
        return false;
    }

    private boolean removeQueue(int requestCode) {
        if (queues != null)
            return queues.remove(requestCode);
        return false;
    }

//    @Deprecated
//    public void setRequest(Request request) {
//        this.request = request;
//    }

    public Context getContext() {
        return context;
    }

//    /**
//     * 无效方法
//     *
//     * @param contentType
//     */
//    @Deprecated
//    public void setContentType(String contentType) {
//        addHeader(Headers.HEAD_KEY_CONTENT_TYPE, contentType);
//    }
//
//    /**
//     * 无效方法
//     *
//     * @param accept
//     */
//    @Deprecated
//    public void setAccept(String accept) {
//        addHeader(Headers.HEAD_KEY_ACCEPT, accept);
//    }

    /**
     * 无效方法
     *
     * @param userAgent
     */
    @Deprecated
    public void setUserAgent(String userAgent) {
        addHeader(Headers.HEAD_KEY_USER_AGENT, userAgent);
    }

    public void setCerName(String cerName) {
        this.cerName = cerName;
    }

    /**
     * 添加头信息 User-Agent|Content-Type|Accept 无效
     *
     * @param key
     * @param value
     */
    public void addHeader(String key, String value) {
        if (headerParams == null)
            headerParams = new HashMap<>();
        headerParams.put(key, value);
    }

    //    /**
//     * 设置队列ID
//     *
//     * @param qid 队列ID
//     */
//    @Deprecated
//    public void setQid(int qid) {
//        this.qid = qid;
//    }
//
//    @Deprecated
//    public void setCacheMode(CacheMode cacheMode) {
//        this.cacheMode = cacheMode;
//    }
//    private SSLContext sslContext;


    /**
     * 忽略所有https证书
     */
    private void overlockCard() {
//        final TrustManager[] trustAllCerts = new TrustManager[]{
//                new X509TrustManager() {
//                    @Override
//                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                    }
//
//                    @Override
//                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                    }
//
//                    @Override
//                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                        X509Certificate[] x509Certificates = new X509Certificate[0];
//                        return x509Certificates;
//                    }
//                }};
//        try {
////            sslContext = SSLContext.getInstance("SSL");
////            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//        } catch (Exception e) {
//            MyLog.i("ssl出现异常");
//        }
    }

    /**
     * @see #post(int, String, AjaxParams, Class, boolean)
     */
    protected String getPath(int resId) {
        String path = getString(resId);
        if (path.startsWith("http"))
            return path;
        StringBuilder sb = new StringBuilder();
        sb.append(host);
        if (!host.endsWith("/") && !path.startsWith("/")) {
            if (!path.startsWith(":"))
                sb.append("/");
            sb.append(path);
        } else if (host.endsWith("/") && path.startsWith("/")) {
            sb.append(path.substring(1));
        } else {
            sb.append(path);
        }
        return sb.toString();
    }

    public void get(int resId) {
        this.get(resId, null, null, false);
    }

    public <T> void get(int resId, Class<T> cls) {
        this.get(resId, null, cls, false);
    }

    public <T> void get(int resId, AjaxParams params, Class<T> cls) {
        this.get(resId, this.getPath(resId), params, cls, false);
    }

    public <T> void get(int resId, Class<T> cls, boolean list) {
        this.get(resId, this.getPath(resId), null, cls, list);
    }

    public <T> void get(int resId, AjaxParams params, Class<T> cls, boolean list) {
        this.get(resId, this.getPath(resId), params, cls, list);
    }

    public <T> void get(int requestCode, String url, AjaxParams params, Class<T> cls, boolean list) {
        request(true, requestCode, url, params, cls, list);
    }

    /**
     * @see #post(int, String, AjaxParams, Class, boolean)
     */
    public void post(int resId) {
        post(resId, null, null, false);
    }

    /**
     * @see #post(int, String, AjaxParams, Class, boolean)
     */
    public <T> void post(int resId, Class<T> cls) {
        post(resId, null, cls, false);
    }

    /**
     * @see #post(int, String, AjaxParams, Class, boolean)
     */
    public <T> void post(int resId, AjaxParams params, Class<T> cls) {
        post(resId, getPath(resId), params, cls, false);
    }

    /**
     * @see #post(int, String, AjaxParams, Class, boolean)
     */
    public <T> void post(int resId, Class<T> cls, boolean list) {
        post(resId, getPath(resId), null, cls, list);
    }

    /**
     * @see #post(int, String, AjaxParams, Class, boolean)
     */
    public <T> void post(int resId, AjaxParams params, Class<T> cls, boolean list) {
        post(resId, getPath(resId), params, cls, list);
    }


    /**
     * 请求方法
     *
     * @param requestCode 请求码
     * @param url         请求地址
     * @param params      请求参数
     * @param cls         类<泛型> (例如：Version.class) 如果为空则直接返回json字符串
     * @param list        结果是否是List
     */
    public <T> void post(int requestCode, String url, AjaxParams params, Class<T> cls, boolean list) {
        request(false, requestCode, url, params, cls, list);
    }


    /**
     * 请求方法
     *
     * @param isGet       是否是GET请求
     * @param requestCode 请求码
     * @param url         请求地址
     * @param params      请求参数
     * @param cls         类<泛型> (例如：Version.class) 如果为空则直接返回json字符串
     * @param list        结果是否是List
     */
    public <T> void request(boolean isGet, int requestCode, String url, AjaxParams params, final Class<T> cls, final boolean list) {
        MyLog.INSTANCE.i("request", url);
        MyLog.INSTANCE.i("params", params == null ? null : params.toString());
        ResultInfo result = new ResultInfo();
        result.setRequestCode(requestCode);
        result.setRequestType(REQUEST_TYPE_JSON);
        if (isGet && params != null && params.getUrlParams() != null && params.getUrlParams().size() > 0) {
            url = url + "?" + params.getUrlParamsString();
        }
        result.setQid(params == null ? 0 : params.getQid());
        RequestMethod method = isGet ? RequestMethod.GET : RequestMethod.POST;
        request(method, url, buildBody(isGet, params), result, cls, list);
    }

    private RequestBody buildBody(boolean isGet, AjaxParams params) {
        if (isGet || params == null) {
            return null;
        }
        if (params.getFileParams() != null && params.getFileParams().size() > 0) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            if (params.getUrlParams() != null && params.getUrlParams().size() > 0)
                for (Map.Entry<String, String> map : params.getUrlParams().entrySet()) {
                    builder.addFormDataPart(map.getKey(), map.getValue());
                }
            for (Map.Entry<String, Binary> map : params.getFileParams().entrySet()) {
                RequestBody body = RequestBody.create(MediaType.parse(map.getValue().getMimeType()), map.getValue().getFile());
                //return body;
                builder.addPart(body);
                builder.addFormDataPart(map.getKey(), map.getValue().getFileName(), body);
            }
            return builder.build();
        } else if (params.getUrlParams() != null) {
            FormBody.Builder builder = new FormBody.Builder();
            if (params.getUrlParams() != null)
                for (Map.Entry<String, String> map : params.getUrlParams().entrySet()) {
                    builder.add(map.getKey(), map.getValue());
                }
            return builder.build();
        }
        return null;
    }

    @MainThread
    public <T> void request(RequestMethod method, String url, RequestBody body, ResultInfo result, final Class<T> cls, final boolean list) {
        if (isDestroy) return;
        if (hasQueue(result.getRequestCode())) {
            MyLog.INSTANCE.e("request", "what=" + result.getRequestCode() + ",msg=" + getString(R.string.API_FLAG_REPEAT));
            //result.setMsg(getString(R.string.API_FLAG_REPEAT));
            //onRequestError(result);
            return;
        }
        addQueue(result.getRequestCode());
        if (client == null) {
            OkHttpClient.Builder obuilder = new OkHttpClient.Builder();
            if (!TextUtils.isEmpty(cerName)) {
                //选择证书
                try {
                    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    keyStore.load(null);
                    InputStream certificate = getContext().getAssets().open(cerName);
                    keyStore.setCertificateEntry(Integer.toString(0), certificateFactory.generateCertificate(certificate));
                    try {
                        if (certificate != null)
                            certificate.close();
                    } catch (IOException e) {
                    }
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    TrustManagerFactory trustManagerFactory =
                            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    trustManagerFactory.init(keyStore);
                    sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
                    obuilder.sslSocketFactory(sslContext.getSocketFactory());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            obuilder.writeTimeout(10 * 60, TimeUnit.SECONDS);
            obuilder.connectTimeout(30, TimeUnit.SECONDS);
            obuilder.readTimeout(30, TimeUnit.SECONDS);
            client = obuilder.build();
        }
        Request.Builder builder = new Request.Builder().url(url);
        builder.method(method.toString(), body);
        builder.tag(result);
        if (headerParams != null) {
            for (Map.Entry<String, String> map : headerParams.entrySet()) {
                builder.addHeader(map.getKey(), map.getValue());
            }
        }
        switch (result.getRequestType()) {
            case REQUEST_TYPE_JSON:
            case REQUEST_TYPE_JSONARRAY:
                builder.addHeader(Headers.HEAD_KEY_ACCEPT, Headers.HEAD_ACCEPT_JSON);
                break;
            case REQUEST_TYPE_STRING:
                builder.addHeader(Headers.HEAD_KEY_ACCEPT, Headers.HEAD_ACCEPT_STRING);
                break;
            case REQUEST_TYPE_FILE:
                builder.addHeader(Headers.HEAD_KEY_ACCEPT, Headers.HEAD_ACCEPT_FILE);
                break;
        }
//        if (cacheMode != null)
//            request.setCacheMode(cacheMode);
//        else
//            request.setCacheMode(CacheMode.DEFAULT);
        showLoading();
        client.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ResultInfo result = (ResultInfo) call.request().tag();
                String msg = e != null ? e.getMessage() : null;
                MyLog.INSTANCE.i("onFailed", "what=" + result.getRequestCode() + ",msg=" + msg);
                int code = R.string.API_FLAG_CON_EXCEPTION;
                if (msg != null) {
                    String thr = msg.toLowerCase(Locale.CHINA);
                    if (thr.contains("broken pipe"))
                        code = R.string.API_FLAG_CON_BROKEN;
                    else if (thr.contains("timed out"))
                        code = R.string.API_FLAG_CON_TIMEOUT;
                    else if (thr.contains("unknownhostexception"))
                        code = R.string.API_FLAG_CON_UNKNOWNHOSTEXCEPTION;
                }
                result.setMsg(getString(code));
                onRequestError(result);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (isDestroy) return;
                //hideLoading();
                ResultInfo result = (ResultInfo) call.request().tag();
                if (response.isSuccessful()) {
                    String token = response.header("token");
                    if (!TextUtils.isEmpty(token)) {
                        result.putValue("token", token);
                    }
                    if (result.getRequestType() == REQUEST_TYPE_FILE) {
                        doSuccessFile(result, response.body());
                        return;
                    }
                    String data = String.valueOf(response.body().string());
                    MyLog.INSTANCE.i("onSucceed", "what=" + result.getRequestCode() + ",data=" + data);
                    if (data.length() > 0) {
                        try {
                            data = data.replaceAll(":\\[\\]", ":null");
                            data = data.replaceAll(":\"\"", ":null");
                            switch (result.getRequestType()) {
                                case REQUEST_TYPE_JSON:
                                    doSuccess(result, new JsonParser().parse(data).getAsJsonObject(), cls, list);
                                    return;
                                case REQUEST_TYPE_JSONARRAY:
                                    doSuccess(result, new JsonParser().parse(data).getAsJsonArray(), cls, list);
                                    return;
                                case REQUEST_TYPE_STRING:
                                    doSuccess(result, data, cls, list);
                                    return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    MyLog.INSTANCE.i("onFail", "what=" + result.getRequestCode() + ",data=" + response.message());
                }
                result.setMsg(getString(R.string.API_FLAG_NO_RESPONSE));
                onRequestError(result);
            }
        });

    }

    //private DownloadQueue downloadQueue;
    public void download(int requestCode, String url, String fileFolder, String fileName, boolean isRange, boolean isDeleteOld) {
        download(requestCode, url, fileFolder, fileName, isRange, isDeleteOld, 0);
    }

    public void download(int requestCode, String url, String fileFolder, String fileName, boolean isRange, boolean isDeleteOld, long qid) {
        MyLog.INSTANCE.i("download", url);
        ResultInfo result = new ResultInfo();
        result.setRequestCode(requestCode);
        result.setRequestType(REQUEST_TYPE_FILE);
        result.setQid(qid);
        RequestMethod method = RequestMethod.GET;

        DownFile downFile = new DownFile();
        downFile.setSaveDir(fileFolder);
        downFile.setRange(isRange);
        downFile.setUrl(url);
        downFile.setFileName(fileName);
        downFile.setDeleteOld(isDeleteOld);
        result.setObj(downFile);
        request(method, url, null, result, null, false);
    }

    protected void doSuccessFile(ResultInfo result, ResponseBody body) {
        //MyLog.i("doSuccessFile" + result.getRequestCode());
        if (body != null) {
            DownFile downFile = result.getObj();
            long total = body.contentLength();
            MyLog.INSTANCE.i("onProgress(File)", "what=" + result.getRequestCode() + ",total=" + total);
            downFile.setState(DownFile.Companion.getSTATUS_START());
            downFile.setAllCount(total);
            onRequestSuccess(result);
            InputStream is = null;
            FileOutputStream fos = null;
            byte[] buf = new byte[2048];
            int len = 0;
            try {
                is = body.byteStream();
                File cacheFile = new File(downFile.getSaveDir(), downFile.getFileName() + ".cache");
                fos = new FileOutputStream(cacheFile);
                long sum = 0;
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                    sum += len;
                    int progress = (int) (sum * 1.0f / total * 100);
                    MyLog.INSTANCE.i("onProgress(File)", "what=" + result.getRequestCode() + ",progress=" + progress + ",fileCount=" + sum + ",total=" + total);
                    downFile.setState(DownFile.Companion.getSTATUS_PROGRESS());
                    downFile.setProgress(progress);
                    downFile.setFileCount(sum);
                    result.setObj(downFile);
                    onRequestSuccess(result);
                }
                fos.flush();
                // else if (total < 0) {
                //    downFile.setState(DownFile.STATUS_SUCCESS);
                //    File file = new File(downFile.getSaveDir(), downFile.getFileName());
                //    cacheFile.renameTo(file);
                //    MyLog.i("onSucceed", "文件下载成功");
                //}
                if (total > 0 && sum == total) {
                    downFile.setState(DownFile.Companion.getSTATUS_SUCCESS());
                    File file = new File(downFile.getSaveDir(), downFile.getFileName());
                    cacheFile.renameTo(file);
                    MyLog.INSTANCE.i("onSucceed", "文件下载成功");
                } else {
                    downFile.setState(DownFile.Companion.getSTATUS_ERROR());
                    MyLog.INSTANCE.i("onSucceed", "文件下载中断");
                }
            } catch (Exception e) {
                MyLog.INSTANCE.i("onSucceed", "文件下载失败");
                downFile.setState(DownFile.Companion.getSTATUS_ERROR());
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            result.setObj(downFile);
        }
        onRequestSuccess(result);

    }

    /**
     * String 格式数据(如果要使用请重构)
     *
     * @param result
     * @param json
     * @param cls
     * @param list
     * @param <T>
     */
    @Deprecated
    protected <T> void doSuccess(ResultInfo result, String json, Class<T> cls, boolean list) {
        result.setObj(json);
        onRequestSuccess(result);
    }

    /**
     * JSONArray 格式数据
     *
     * @param result
     * @param obj
     * @param cls
     * @param list
     * @param <T>
     */
    @Deprecated
    protected <T> void doSuccess(ResultInfo result, JsonArray obj, Class<T> cls, boolean list) {
        result.setObj(obj);
        onRequestSuccess(result);
    }

    /**
     * JSONObject 格式数据 (默认)
     *
     * @param result
     * @param obj
     * @param cls
     * @param list
     * @param <T>
     */
    protected <T> void doSuccess(ResultInfo result, JsonObject obj, Class<T> cls, boolean list) {
//        try {
//            int flag = 0;
//            if (obj.has("state")) {
//                flag = obj.getInt("state");
//                result.setErrorCode(flag);
//            }
//            if (obj.has("msg")) {
//                String msg = obj.getString("msg");
//                result.setMsg(msg);
//            }
//            String data = null;
//            if (obj.has("result")) {
//                data = obj.getString("result");
//                data = data.replaceAll("\\\\", "").replaceAll("\"\\[", "\\[").replaceAll("\\]\"", "\\]");
//                data = data.replaceAll("\"\\[", "\\[").replaceAll("\\]\"", "\\]");
//            }
//            // 成功
//            if (flag == 1) {
//                doSuccessData(result, data, cls, list);
//            } else {
//                onRequestError(result);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            result.setErrorCode(ResultInfo.CODE_ERROR_DECODE);
//            result.setMsg(getString(R.string.API_FLAG_ANALYSIS_ERROR));
//            onRequestError(result);
//        }
    }




    /**
     * not main thread
     */
    @Deprecated
    protected void onStart() {
    }

    /**
     * not main thread
     */
    @Deprecated
    protected void onRequestSuccess(ResultInfo result) {
        //MyLog.i("onRequestSuccess" + result.getRequestCode());
        if (isDestroy) return;
        result.setErrorCode(0);
        initHandler();
        if (handler != null) {
            runnable.setSuccess(true);
            runnable.setResult(result);
            handler.post(runnable);
        }
    }

    /**
     * not main thread
     */
    @Deprecated
    protected void onRequestError(final ResultInfo result) {
        if (isDestroy) return;
        initHandler();
        if (handler != null) {
            runnable.setSuccess(false);
            runnable.setResult(result);
            handler.post(runnable);
        }
    }

    private MyRunnable runnable;

    private void initHandler() {
        if (runnable == null) {
            runnable = new MyRunnable() {
                @Override
                public void run() {
                    handler.removeCallbacks(runnable);
                    ResultInfo result = getResult();
                    //MyLog.i("onRequest run" + result.getRequestCode());
                    if (!hasQueue(result.getRequestCode())) return;
                    if (result.getRequestType() == REQUEST_TYPE_FILE) {
                        DownFile downFile = result.getObj();
                        if (downFile.getState() == DownFile.Companion.getSTATUS_SUCCESS() || downFile.getState() == DownFile.Companion.getSTATUS_ERROR()) {
                            removeQueue(result.getRequestCode());
                            hideLoading();
                        }
                    } else {
                        removeQueue(result.getRequestCode());
                        hideLoading();
                    }
                    if (listeners != null) {
                        for (IMyHttpListener listener : listeners) {
                            //MyLog.i("onRequest listener" + result.getRequestCode());
                            if (isSuccess())
                                listener.onRequestSuccess(result);
                            else
                                listener.onRequestError(result);
                        }
                    }
                }
            };
        }
    }


    /**
     * 显示加载对话框
     *
     * @param showDialog 是否显示加载对话框
     */
    @UiThread
    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    @UiThread
    public void setLoadingDialog(LoadingDialog loadingDialog) {
        this.loadingDialog = loadingDialog;
    }

    /**
     * 显示加载框
     */
    protected void showLoading() {
        if (isDestroy) return;
        if (showDialog) {
            if (loadingDialog == null) {
                setLoadingDialog(new LoadingDialog(context));
            }
            loadingDialog.show();
        }
    }

    /**
     * 隐藏加载框
     */
    protected void hideLoading() {
        if (isDestroy) return;
        if (loadingDialog != null)
            loadingDialog.dismiss();
    }

    @UiThread
    public void updateLoadingMsg(String msg) {
        if (isDestroy) return;
        if (msg == null) {
            showDialog = false;
            return;
        }
        showDialog = true;
        if (loadingDialog == null) {
            setLoadingDialog(new LoadingDialog(context, msg));
        } else {
            loadingDialog.updateMsg(msg);
        }
    }

    /**
     * 销毁，释放
     */
    @MainThread
    public void onDestroy() {
        //在这里销毁所有当前请求
        MyLog.INSTANCE.i(getClass(), "onDestroy");
        isDestroy = true;
        listeners = null;
        loadingDialog = null;
        if (client != null && client.dispatcher() != null)
            client.dispatcher().cancelAll();
    }


    protected String getString(int resId) {
        return context == null ? null : context.getString(resId);
    }

    private class MyRunnable implements Runnable {
        private ResultInfo result;
        private boolean success;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public ResultInfo getResult() {
            return result;
        }

        public void setResult(ResultInfo result) {
            this.result = result;
        }

        @Override
        public void run() {

        }
    }
}