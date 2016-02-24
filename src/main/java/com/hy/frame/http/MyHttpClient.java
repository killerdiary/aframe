package com.hy.frame.http;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.utils.FieldUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hy.frame.R;
import com.hy.frame.bean.ResultInfo;
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
    private int qid;//队列ID

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

    public Context getContext() {
        return context;
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
     * 设置队列ID
     *
     * @param qid 队列ID
     */
    public void setQid(int qid) {
        this.qid = qid;
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
            sb.append("/");
        }
        sb.append(path);
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
    public <T> void post(int requestCode, String url, AjaxParams params, final Class<T> cls, final boolean list) {
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
        final ResultInfo result = new ResultInfo();
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
        MyAjaxCallBack callback = new MyAjaxCallBack(result) {

            @Override
            public void onSuccess(String json) {
                hideLoading();
                MyLog.d("onSuccess | " + json);
                if (HyUtil.isNoEmpty(json)) {
                    json = json.trim();
                    if (cls == null) {
                        getResult().setObj(json);
                        onRequestSuccess(getResult());
                    } else if (!TextUtils.equals(json.substring(0, 1), "{") || !TextUtils.equals(json.substring(json.length() - 1), "}")) {
                        getResult().setMsg(getString(R.string.API_FLAG_DATA_ERROR));
                        onRequestError(getResult());
                    } else {
                        if (json.contains("[]"))
                            json = json.replaceAll("\\[\\]", "null");
                        if (json.contains("\"\""))
                            json = json.replaceAll("\"\"", "null");
                        doSuccess(getResult(), json, cls, list);
                    }
                } else {
                    getResult().setMsg(getString(R.string.API_FLAG_NO_RESPONSE));
                    onRequestError(getResult());
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                hideLoading();
                MyLog.e("onFailure | " + t.toString() + " | " + errorNo + " | " + strMsg);
                int code = R.string.API_FLAG_CON_EXCEPTION;
                if (strMsg != null) {
                    String thr = strMsg.toLowerCase(Locale.CHINA);
                    if (strMsg.contains("broken pipe"))
                        code = R.string.API_FLAG_CON_BROKEN;
                    else if (strMsg.contains("timed out"))
                        code = R.string.API_FLAG_CON_TIMEOUT;
                    else if (strMsg.contains("unknownhostexception"))
                        code = R.string.API_FLAG_CON_UNKNOWNHOSTEXCEPTION;
                } else {
                    String thr = t.toString().toLowerCase(Locale.CHINA);
                    if (thr.contains("brokenpipe"))
                        code = R.string.API_FLAG_CON_BROKEN;
                    else if (thr.contains("timedout"))
                        code = R.string.API_FLAG_CON_TIMEOUT;
                }
                result.setMsg(getString(code));
                onRequestError(result);
                hideLoading();
            }
        };
        if (isGet)
            fh.get(url, params, contentType, callback);
        else
            fh.post(url, params == null ? null : params.getEntity(), contentType, callback);
//        Volley.newRequestQueue(context);
//        RequestQueue queue = Volley.newRequestQueue(context);
//        //queue.cancelAll();
//        StringRequest request = new StringRequest("", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                return super.getParams();
//            }
//        };
//        queue.add(request);
    }

    protected <T> void doSuccess(ResultInfo result, String json, Class<T> cls, boolean list) {
        JSONObject obj;
        try {
            obj = new JSONObject(json);
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
                if (!HyUtil.isEmpty(data)) {
                    if (list) {
                        // List<T> rlt = gson.fromJson(data, newTypeToken<T>() {}.getType());
                        List<T> beans = null;
                        if (data.indexOf("[]") != 0) {
                            JSONArray rlt = new JSONArray(data);
                            if (rlt.length() > 0) {
                                int size = rlt.length();
                                beans = new ArrayList<>();
                                for (int i = 0; i < size; i++) {
                                    // String str = gson.toJson(rlt.get(i));
                                    String str = rlt.getString(i);
                                    T t = gson.fromJson(str, cls);
                                    beans.add(t);
                                }
                            }
                            result.setObj(beans);
                        }
                        onRequestSuccess(result);
                    } else {
                        if (cls == String.class) {
                            result.setObj(data);
                        } else if (cls == int.class || cls == Integer.class) {
                            result.setObj(Integer.parseInt(data));
                        } else if (cls == float.class || cls == Float.class) {
                            result.setObj(Float.parseFloat(data));
                        } else if (cls == double.class || cls == Double.class) {
                            result.setObj(Double.parseDouble(data));
                        } else if (cls == long.class || cls == Long.class) {
                            result.setObj(Long.parseLong(data));
                        } else if (cls == java.util.Date.class || cls == java.sql.Date.class) {
                            result.setObj(FieldUtils.stringToDateTime(data));
                        } else if (cls == boolean.class || cls == Boolean.class) {
                            result.setObj(Boolean.parseBoolean(data));
                        } else {
                            T t = gson.fromJson(data, cls);
                            result.setObj(t);
                        }
                        onRequestSuccess(result);
                    }
                } else
                    onRequestSuccess(result);
                return;
            } else {
                onRequestError(result);
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result.setErrorCode(ResultInfo.CODE_ERROR_DECODE);
        result.setMsg(getString(R.string.API_FLAG_ANALYSIS_ERROR));
        onRequestError(result);
    }

    protected void onRequestError(ResultInfo result) {
        if (listener != null)
            listener.onRequestError(result);
    }

    protected void onRequestSuccess(ResultInfo result) {
        result.setErrorCode(0);
        if (listener != null)
            listener.onRequestSuccess(result);
    }

    public void setLoadingDialog(LoadingDialog loadingDialog) {
        this.loadingDialog = loadingDialog;
        //this.showDialog = true;
    }

    /**
     * 显示加载框
     */
    protected void showLoading() {
        if (showDialog) {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(context, null);
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
            loadingDialog = new LoadingDialog(context, msg);
        } else {
            loadingDialog.updateMsg(msg);
        }
    }

    public void onDestroy() {
        //在这里销毁所有当前请求
        MyLog.d(getClass(), "onDestroy");
    }

    protected String getString(int resId) {
        return context.getString(resId);
    }
}