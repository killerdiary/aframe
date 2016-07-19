package com.hy.http;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hy.frame.R;
import com.hy.frame.bean.ResultInfo;
import com.hy.frame.util.Constant;
import com.hy.frame.util.HyUtil;
import com.hy.frame.util.MyLog;
import com.hy.frame.view.LoadingDialog;
import com.yolanda.nohttp.Binary;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.JsonObjectRequest;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;
import com.yolanda.nohttp.StringRequest;
import com.yolanda.nohttp.cache.CacheMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private boolean showDialog;// 显示加载对话框
    private LoadingDialog loadingDialog;
    private String signatures;// 密钥KEY
    private String host;// 域名
    //private final static int TIME_OUT = 60 * 1000;
    //private String contentType, userAgent, accept;
    private Map<String, String> headerParams;
    private int qid;//队列ID
    private boolean isDestroy;
    private RequestQueue requestQueue;
    private CacheMode cacheMode;
    private Request request;

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
        if (context == null || host == null) {
            MyLog.e("MyHttpClient init error!");
            return;
        }
        this.context = context;
        this.listeners = new ArrayList<>();
        this.listeners.add(listener);
        this.host = host;
        //this.contentType = contentType;
        //this.userAgent = userAgent;
        //this.accept = accept;
        // this.http = new FinalHttp();
        // this.http.configTimeout(TIME_OUT);
        this.requestQueue = NoHttp.newRequestQueue();
        if (accept != null)
            addHeader(Headers.HEAD_KEY_ACCEPT, accept);
        if (contentType != null)
            addHeader(Headers.HEAD_KEY_CONTENT_TYPE, contentType);
        if (userAgent != null)
            addHeader(Headers.HEAD_KEY_USER_AGENT, userAgent);
    }

    public void setListener(IMyHttpListener listener) {
        if (this.listeners == null)
            this.listeners = new ArrayList<>();
        else
            this.listeners.clear();
        this.listeners.add(listener);
    }

    public void addListener(IMyHttpListener listener) {
        if (this.listeners == null)
            this.listeners = new ArrayList<>();
        this.listeners.add(listener);
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Context getContext() {
        return context;
    }

    public void setContentType(String contentType) {
        addHeader(Headers.HEAD_KEY_CONTENT_TYPE, contentType);
    }

    public void setAccept(String accept) {
        addHeader(Headers.HEAD_KEY_ACCEPT, accept);
    }

    public void addHeader(String key, String value) {
        if (headerParams == null)
            headerParams = new HashMap<String, String>();
        headerParams.put(key, value);
    }

    /**
     * 设置队列ID
     *
     * @param qid 队列ID
     */
    public void setQid(int qid) {
        this.qid = qid;
    }

    public void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
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
     * @param showDialog 是否显示加载对话框
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

    public void get(int resId) {
        this.get(resId,  null,  null, false);
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
        if (isDestroy) return;
        ResultInfo result = new ResultInfo();
        result.setRequestCode(requestCode);
        result.setQid(qid);
        result.setErrorCode(ResultInfo.CODE_ERROR_DEFAULT);
        qid = 0;
        if (!HyUtil.isNetworkConnected(context)) {
            result.setErrorCode(ResultInfo.CODE_ERROR_NET);
            result.setMsg(getString(R.string.API_FLAG_NET_FAIL));
            onRequestError(result);
            return;
        }
        if (signatures != null) {
            if (params == null)
                params = new AjaxParams();
            params.put("signatures", signatures);
        }
        MyLog.d("request", url);
        if (params != null) {
            MyLog.d("request", params.getUrlParams().toString());
        }
//        FinalHttp fh = new FinalHttp();
//        fh.configTimeout(TIME_OUT);
//        if (HyUtil.isNoEmpty(userAgent))
//            fh.configUserAgent(userAgent);
//        if (HyUtil.isNoEmpty(accept))
//            fh.addHeader("Accept", accept);
//        if (maps != null) {
//            for (Entry<String, String> map : maps.entrySet()) {
//                fh.addHeader(map.getKey(), map.getValue());
//            }
//        }

//        if (isGet)
//            fh.get(url, params, contentType, callback);
//        else
//            fh.post(url, params == null ? null : params.getEntity(), contentType, callback);
        RequestMethod method = isGet ? RequestMethod.GET : RequestMethod.POST;
        if (request == null)
            request = NoHttp.createJsonObjectRequest(url, method);
//        if (HyUtil.isNoEmpty(accept))
//            fh.addHeader("Accept", accept);
        //request.set
        //new StringRequest(method, url, params, successListener, errorListener);
        request.setTag(result);
        if (headerParams != null) {
            for (Map.Entry<String, String> map : headerParams.entrySet()) {
                request.addHeader(map.getKey(), map.getValue());
            }
        }
        if (params != null) {
            request.add(params.getUrlParams());
            if (params.getFileParams() != null) {
                for (Map.Entry<String, Binary> map : params.getFileParams().entrySet()) {
                    request.add(map.getKey(), map.getValue());
                }
            }
        }
        if (cacheMode != null)
            request.setCacheMode(cacheMode);
        else
            request.setCacheMode(CacheMode.DEFAULT);
        OnResponseListener listener = null;
        if (request instanceof JsonObjectRequest) {
            listener = new OnResponseListener<JSONObject>() {
                @Override
                public void onStart(int what) {
                    MyLog.d("onStart", what);
                    showLoading();
                }

                @Override
                public void onSucceed(int what, Response<JSONObject> response) {
                    hideLoading();
                    // 接受请求结果
                    JSONObject data = response.get();
                    MyLog.d("onSucceed", what + "|" + data);
                    ResultInfo result = (ResultInfo) response.getTag();
                    // Bitmap imageHead = response.get(); // 如果是bitmap类型，都是同样的用法
                    if (data != null) {
                        if (cls == null) {
                            result.setObj(data);
                            onRequestSuccess(result);
                        } else {
                            //兼容
                            doSuccess(result, data, cls, list);
                            doSuccess(result, data.toString(), cls, list);
                        }
                    } else {
                        result.setMsg(getString(R.string.API_FLAG_NO_RESPONSE));
                        onRequestError(result);
                    }
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                    MyLog.d("onFailed", what + "|" + exception.getMessage());
                    hideLoading();
                    int code = R.string.API_FLAG_CON_EXCEPTION;
                    String msg = exception.getMessage();
                    if (msg != null) {
                        String thr = msg.toLowerCase(Locale.CHINA);
                        if (msg.contains("broken pipe"))
                            code = R.string.API_FLAG_CON_BROKEN;
                        else if (msg.contains("timed out"))
                            code = R.string.API_FLAG_CON_TIMEOUT;
                        else if (msg.contains("unknownhostexception"))
                            code = R.string.API_FLAG_CON_UNKNOWNHOSTEXCEPTION;
                    }
                    ResultInfo result = (ResultInfo) tag;
                    result.setMsg(getString(code));
                    onRequestError(result);
                }

                @Override
                public void onFinish(int what) {
                    MyLog.d("onFinish", what);
                    cacheMode = null;
                    request = null;
                }
            };
        } else if (request instanceof JSONArray) {
            listener = new OnResponseListener<JSONArray>() {
                @Override
                public void onStart(int what) {
                    MyLog.d("onStart", what);
                    showLoading();
                }

                @Override
                public void onSucceed(int what, Response<JSONArray> response) {
                    hideLoading();
                    // 接受请求结果
                    JSONArray data = response.get();
                    MyLog.d("onSucceed", what + "|" + data);
                    ResultInfo result = (ResultInfo) response.getTag();
                    if (data != null) {
                        if (cls == null) {
                            result.setObj(data);
                            onRequestSuccess(result);
                        } else {
                            //兼容
                            doSuccess(result, data, cls, list);
                            doSuccess(result, data.toString(), cls, list);
                        }
                    } else {
                        result.setMsg(getString(R.string.API_FLAG_NO_RESPONSE));
                        onRequestError(result);
                    }
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                    MyLog.d("onFailed", what + "|" + exception.getMessage());
                    hideLoading();
                    int code = R.string.API_FLAG_CON_EXCEPTION;
                    String msg = exception.getMessage();
                    if (msg != null) {
                        String thr = msg.toLowerCase(Locale.CHINA);
                        if (msg.contains("broken pipe"))
                            code = R.string.API_FLAG_CON_BROKEN;
                        else if (msg.contains("timed out"))
                            code = R.string.API_FLAG_CON_TIMEOUT;
                        else if (msg.contains("unknownhostexception"))
                            code = R.string.API_FLAG_CON_UNKNOWNHOSTEXCEPTION;
                    }
                    ResultInfo result = (ResultInfo) tag;
                    result.setMsg(getString(code));
                    onRequestError(result);
                }

                @Override
                public void onFinish(int what) {
                    MyLog.d("onFinish", what);
                    cacheMode = null;
                    request = null;
                }
            };
        } else if (request instanceof StringRequest) {
            listener = new OnResponseListener<String>() {
                @Override
                public void onStart(int what) {
                    MyLog.d("onStart", what);
                    showLoading();
                }

                @Override
                public void onSucceed(int what, Response<String> response) {
                    hideLoading();
                    // 接受请求结果
                    String data = response.get();
                    MyLog.d("onSucceed", what + "|" + data);
                    ResultInfo result = (ResultInfo) response.getTag();
                    if (data != null) {
                        result.setObj(data);
                        onRequestSuccess(result);
                    } else {
                        result.setMsg(getString(R.string.API_FLAG_NO_RESPONSE));
                        onRequestError(result);
                    }
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                    MyLog.d("onFailed", what + "|" + exception.getMessage());
                    hideLoading();
                    int code = R.string.API_FLAG_CON_EXCEPTION;
                    String msg = exception.getMessage();
                    if (msg != null) {
                        String thr = msg.toLowerCase(Locale.CHINA);
                        if (msg.contains("broken pipe"))
                            code = R.string.API_FLAG_CON_BROKEN;
                        else if (msg.contains("timed out"))
                            code = R.string.API_FLAG_CON_TIMEOUT;
                        else if (msg.contains("unknownhostexception"))
                            code = R.string.API_FLAG_CON_UNKNOWNHOSTEXCEPTION;
                    }
                    ResultInfo result = (ResultInfo) tag;
                    result.setMsg(getString(code));
                    onRequestError(result);
                }

                @Override
                public void onFinish(int what) {
                    MyLog.d("onFinish", what);
                    cacheMode = null;
                    request = null;
                }
            };
        }
        requestQueue.add(requestCode, request, listener);
    }

    @Deprecated
    protected <T> void doSuccess(ResultInfo result, String json, Class<T> cls, boolean list) {

    }

    @Deprecated
    protected <T> void doSuccess(ResultInfo result, JSONArray json, Class<T> cls, boolean list) {

    }

    protected <T> void doSuccess(ResultInfo result, JSONObject obj, Class<T> cls, boolean list) {
        if (isDestroy) return;
        try {
            int flag = 0;
            if (obj.has("state")) {
                flag = obj.getInt("state");
                result.setErrorCode(flag);
            }
            if (obj.has("msg")) {
                String msg = obj.getString("msg");
                result.setMsg(msg);
            }
            String data = null;
            if (obj.has("result")) {
                data = obj.getString("result");
            }
            // 成功
            if (flag == 1) {
                doSuccessData(result, data, cls, list);
            } else {
                onRequestError(result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result.setErrorCode(ResultInfo.CODE_ERROR_DECODE);
            result.setMsg(getString(R.string.API_FLAG_ANALYSIS_ERROR));
            onRequestError(result);
        }
    }

    protected <T> void doSuccessData(ResultInfo result, String data, Class<T> cls, boolean list) {
        if (HyUtil.isNoEmpty(data) && !TextUtils.equals(data, "[]")) {
            try {
                if (list) {
                    // List<T> rlt = gson.fromJson(data, newTypeToken<T>() {}.getType());
                    List<T> beans = null;
                    JSONArray rlt = new JSONArray(data);
                    if (rlt.length() > 0) {
                        int size = rlt.length();
                        beans = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            String str = rlt.getString(i);
                            T t = (T) doT(str, cls);
                            beans.add(t);
                        }
                    }
                    result.setObj(beans);
                    onRequestSuccess(result);
                } else {
                    result.setObj(doT(data, cls));
                    onRequestSuccess(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.setErrorCode(ResultInfo.CODE_ERROR_DECODE);
                result.setMsg(getString(R.string.API_FLAG_ANALYSIS_ERROR));
                onRequestError(result);
            }
        } else
            onRequestSuccess(result);
    }

    private <T> Object doT(String data, Class<T> cls) {
        if (cls == String.class) {
            return data;
        } else if (cls == int.class || cls == Integer.class) {
            return Integer.parseInt(data);
        } else if (cls == float.class || cls == Float.class) {
            return Float.parseFloat(data);
        } else if (cls == double.class || cls == Double.class) {
            return Double.parseDouble(data);
        } else if (cls == long.class || cls == Long.class) {
            return Long.parseLong(data);
        } else if (cls == java.util.Date.class || cls == java.sql.Date.class) {
            return stringToDateTime(data);
        } else if (cls == boolean.class || cls == Boolean.class) {
            return Boolean.parseBoolean(data);
        }
        return new Gson().fromJson(data, cls);
    }

    public static Date stringToDateTime(String strDate) {
        if (strDate != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return sdf.parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected void onRequestError(ResultInfo result) {
        if (isDestroy) return;
        if (listeners != null) {
            for (IMyHttpListener listener : listeners)
                listener.onRequestError(result);
        }
    }

    protected void onRequestSuccess(ResultInfo result) {
        if (isDestroy) return;
        result.setErrorCode(0);
        if (listeners != null) {
            for (IMyHttpListener listener : listeners)
                listener.onRequestSuccess(result);
        }
    }

    public void setLoadingDialog(LoadingDialog loadingDialog) {
        this.loadingDialog = loadingDialog;
        //this.showDialog = true;
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

    public void onDestroy() {
        //在这里销毁所有当前请求
        MyLog.d(getClass(), "onDestroy");
        if (requestQueue != null)
            requestQueue.stop();
        isDestroy = true;
    }

    protected String getString(int resId) {
        return context.getString(resId);
    }
}