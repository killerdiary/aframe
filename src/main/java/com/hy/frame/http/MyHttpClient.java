package com.hy.frame.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hy.frame.R;
import com.hy.frame.util.HyUtil;
import com.hy.frame.util.MyLog;
import com.hy.frame.view.LoadingDialog;

/**
 * 网络请求，不能直接使用<br/>
 * 只能继承
 * 
 * @author HeYan
 * @time 2014年8月9日 下午3:43:44
 */
public abstract class MyHttpClient {
    private Context context;
    protected Gson gson;
    private IMyHttpListener listener;
    private boolean showDialog;// 显示加载对话框
    private LoadingDialog loadingDialog;
    private String signatures;// 密钥KEY
    private String host;// 域名
    private final static int TIME_OUT = 60 * 1000;
    private String contentType, userAgent, accept;
    private Map<String, String> maps;

    public MyHttpClient(Context context, IMyHttpListener listener, String host) {
        this(context, listener, host, null, null, null);
    }

    /**
     * 初始化
     * 
     * @param context
     * @param listener
     * @param host
     * @param contentType
     * @param userAgent
     * @param accept
     */
    public MyHttpClient(Context context, IMyHttpListener listener, String host, String contentType, String userAgent, String accept) {
        super();
        if (context == null || listener == null || host == null) {
            MyLog.e("MyHttpClient init error!");
            return;
        }
        this.context = context;
        this.listener = listener;
        this.host = host;
        this.contentType = contentType;
        this.userAgent = userAgent;
        this.accept = accept;
        // this.http = new FinalHttp();
        // this.http.configTimeout(TIME_OUT);
        this.gson = new Gson();
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void addHeader(String key, String value) {
        if (maps == null)
            maps = new HashMap<String, String>();
        maps.put(key, value);
    }

    /**
     * 
     * @param context
     * @param listener
     * @param host
     * @param verify
     *            开启验证
     * @throws Exception
     */
    public MyHttpClient(Context context, IMyHttpListener listener, String host, boolean verify) {
        this(context, listener, host);
        if (verify) {
            PackageInfo pi = HyUtil.getAppVersion(context);
            if (pi != null) {
                if (pi.signatures != null && pi.signatures.length > 0)
                    this.signatures = pi.signatures[0].toCharsString();
            }
        }
    }

    /**
     * 显示加载对话框
     * 
     * @param showDialog
     */
    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
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
     * @see #post(int, String, AjaxParams, Class, boolean)
     */
    protected String getPath(int resId) {
        String path = context.getString(resId);
        if (path.startsWith("http"))
            return path;
        StringBuilder sb = new StringBuilder();
        sb.append(host);
        if (!host.endsWith("/") && !path.startsWith("/")) {
            sb.append("/");
        }
        sb.append(path);
        return sb.toString();
    }

    public <T> void post(int requestCode, String url, AjaxParams params, final Class<T> cls, final boolean list) {
        request(false, requestCode, url, params, cls, list);
    }

    /**
     * 请求方法
     * 
     * @param requestCode
     *            请求码
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @param cls
     *            类<泛型> (例如：Version.class) 如果为空则直接返回json字符串
     * @param list
     *            结果是否是List
     */
    public <T> void request(boolean isGet, int requestCode, String url, AjaxParams params, final Class<T> cls, final boolean list) {
        if (!HyUtil.isNetworkConnected(context)) {
            onRequestError(requestCode, R.string.API_FLAG_NET_FAIL, getString(R.string.API_FLAG_NET_FAIL));
            return;
        }
        showLoading();
        if (signatures != null) {
            if (params == null)
                params = new AjaxParams();
            params.put("signatures", signatures);
        }
        MyLog.d(url);
        if (params != null)
            MyLog.d(params.toString());
        FinalHttp fh = new FinalHttp();
        fh.configTimeout(TIME_OUT);
        if (HyUtil.isNoEmpty(userAgent))
            fh.configUserAgent(userAgent);
        if (HyUtil.isNoEmpty(accept))
            fh.addHeader("Accept", accept);
        if (maps != null) {
            for (Entry<String, String> map : maps.entrySet()) {
                fh.addHeader(map.getKey(), map.getValue());
            }
        }
        MyAjaxCallBack callback = new MyAjaxCallBack(requestCode) {

            @Override
            public void onSuccess(String json) {
                hideLoading();
                MyLog.d("onSuccess | " + json);
                if (HyUtil.isNoEmpty(json)) {
                    json = json.trim();
                    if (cls == null)
                        onRequestSuccess(getRequestCode(), json, null);
                    else if (!TextUtils.equals(json.substring(0, 1), "{") || !TextUtils.equals(json.substring(json.length() - 1), "}"))
                        onRequestError(getRequestCode(), R.string.API_FLAG_DATA_ERROR, context.getString(R.string.API_FLAG_DATA_ERROR));
                    else {
                        if (json.indexOf("[]") > -1)
                            json = json.replaceAll("\\[\\]", "null");
                        if (json.indexOf("\"\"") > -1)
                            json = json.replaceAll("\"\"", "null");
                        doSuccess(json, getRequestCode(), cls, list);
                    }
                } else
                    onRequestError(getRequestCode(), R.string.API_FLAG_NO_RESPONSE, context.getString(R.string.API_FLAG_NO_RESPONSE));
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                hideLoading();
                MyLog.e("onFailure | " + t.toString() + " | " + errorNo + " | " + strMsg);
                int code = R.string.API_FLAG_CON_EXCEPTION;
                if (errorNo == 401) {
                    onRequestError(getRequestCode(), errorNo, "用户令牌过期");
                } else if (strMsg != null) {
                    if (strMsg.indexOf("Broken pipe") > -1)
                        code = R.string.API_FLAG_CON_BROKEN;
                    else if (strMsg.indexOf("timed out") > -1)
                        code = R.string.API_FLAG_CON_TIMEOUT;
                    onRequestError(getRequestCode(), code, context.getString(code));
                } else {
                    String thr = t.toString().toLowerCase(Locale.CHINA);
                    if (thr.indexOf("brokenpipe") > -1)
                        code = R.string.API_FLAG_CON_BROKEN;
                    else if (thr.indexOf("timedout") > -1)
                        code = R.string.API_FLAG_CON_TIMEOUT;
                    onRequestError(getRequestCode(), code, context.getString(code));
                }
                hideLoading();
            }
        };
        if (isGet)
            fh.get(url, params, contentType, callback);
        else
            fh.post(url, params == null ? null : params.getEntity(), contentType, callback);
    }

    protected <T> void doSuccess(String json, int requestCode, Class<T> cls, boolean list) {
        JSONObject obj;
        try {
            obj = new JSONObject(json);
            int flag = 0;
            if (obj.has("state")) {
                flag = obj.getInt("state");
            }
            String msg = null;
            if (obj.has("msg")) {
                msg = obj.getString("msg");
            }
            String data = null;
            if (obj.has("result")) {
                data = obj.getString("result");
            }
            // 成功
            if (flag == 1) {
                if (!HyUtil.isEmpty(data)) {
                    if (cls == null || cls.getSimpleName().equals(String.class.getSimpleName())) {
                        onRequestSuccess(requestCode, data, msg);
                    } else if (list) {
                        // List<T> rlt = gson.fromJson(data, new
                        // TypeToken<T>() {
                        // }.getType());
                        List<T> beans = null;
                        if (data.indexOf("[]") != 0) {
                            JSONArray rlt = new JSONArray(data);
                            if (rlt != null && rlt.length() > 0) {
                                int size = rlt.length();
                                beans = new ArrayList<T>();
                                for (int i = 0; i < size; i++) {
                                    // String str = gson.toJson(rlt.get(i));
                                    String str = rlt.getString(i);
                                    T t = gson.fromJson(str, cls);
                                    beans.add(t);
                                }
                            }
                        }
                        onRequestSuccess(requestCode, beans, msg);
                    } else {
                        T t = gson.fromJson(data, cls);
                        onRequestSuccess(requestCode, t, msg);
                    }
                } else
                    onRequestSuccess(requestCode, null, msg);
                return;
            } else if (msg != null) {
                onRequestError(requestCode, flag, msg);
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onRequestError(requestCode, R.string.API_FLAG_ANALYSIS_ERROR, context.getString(R.string.API_FLAG_ANALYSIS_ERROR));
    }

    protected void onRequestError(int requestCode, int errorCode, String msg) {
        if (listener != null)
            listener.onRequestError(requestCode, errorCode, msg);
    }

    protected void onRequestSuccess(int requestCode, Object obj, String msg) {
        if (listener != null)
            listener.onRequestSuccess(requestCode, obj, msg);
    }

    /**
     * 显示加载框
     */
    protected void showLoading() {
        if (showDialog) {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(context);
            }
            loadingDialog.show();
        }
    }

    /**
     * 隐藏加载框
     */
    protected void hideLoading() {
        if (loadingDialog != null)
            loadingDialog.dismiss();
    }

    public void updateLoadingMsg(String msg) {
        if (msg == null) {
            showDialog = false;
            return;
        }
        showDialog = true;
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(context);
            loadingDialog.init(msg);
        } else {
            loadingDialog.updateMsg(msg);
        }
    }

    protected String getString(int resId) {
        return context.getString(resId);
    }
}