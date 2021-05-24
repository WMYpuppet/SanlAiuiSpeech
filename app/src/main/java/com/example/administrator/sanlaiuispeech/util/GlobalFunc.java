package com.example.administrator.sanlaiuispeech.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.administrator.sanlaiuispeech.SpeechApp;
import com.iflytek.mobileXCorebusiness.base.utils.LogUtil;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * function  全局通用方法
 * Created by zhimei on 2017/1/110.
 */

public class GlobalFunc {

    /**
     * 定义变量阻止连续点击
     */
    private static long lastClickTime = 0l;

    /**
     * 当前点击的按钮
     */
    private static String currentClickBtnName = "";

    /**
     * 判断是否第一次启动
     */
    public static boolean checkIsFirstStartup(Context context) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preference.getBoolean("application_has_startup", false)) {
            SharedPreferences.Editor editor = preference.edit();
            editor.putBoolean("application_has_startup", true);
            editor.commit();
            return true;
        }
        return false;
    }

    /**
     * Function  :   封装请求体信息
     * Param     :   params请求体内容，encode编码格式
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    /**
     * http数据请求
     *
     * @param params
     * @return
     */
    public static String sendDataRequest(String[] params) {
        String resultData = "";
        try {
            URL url = new URL(params[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setConnectTimeout(2000);     //设置连接超时时间

            con.setRequestMethod("POST");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Connection", "Keep-Alive");

            /**设置请求体的类型是文本类型*/
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置请求体的长度
            con.setRequestProperty("Content-Length", String.valueOf(params[1].getBytes().length));
            //获得输出流，向服务器写入数据
            OutputStream outputStream = con.getOutputStream();
            outputStream.write(params[1].getBytes());

            int response = con.getResponseCode();            //获得服务器的响应码
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] datas = new byte[1024];
                int len = 0;
                try {
                    while ((len = inputStream.read(datas)) != -1) {
                        byteArrayOutputStream.write(datas, 0, len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                resultData = new String(byteArrayOutputStream.toByteArray());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultData;
    }


    /**
     * 去除多音字重复数据
     *
     * @param theStr
     * @return
     */
    private static List<Map<String, Integer>> discountTheChinese(String theStr) {
        // 去除重复拼音后的拼音列表
        List<Map<String, Integer>> mapList = new ArrayList<Map<String, Integer>>();
        // 用于处理每个字的多音字，去掉重复
        Map<String, Integer> onlyOne = null;
        String[] firsts = theStr.split(" ");
        // 读出每个汉字的拼音
        for (String str : firsts) {
            onlyOne = new Hashtable<String, Integer>();
            String[] china = str.split(",");
            // 多音字处理
            for (String s : china) {
                Integer count = onlyOne.get(s);
                if (count == null) {
                    onlyOne.put(s, new Integer(1));
                } else {
                    onlyOne.remove(s);
                    count++;
                    onlyOne.put(s, count);
                }
            }
            mapList.add(onlyOne);
        }
        return mapList;
    }

    /**
     * 解析并组合拼音，对象合并方案(推荐使用)
     *
     * @return
     */
    private static String parseTheChineseByObject(
            List<Map<String, Integer>> list) {
        Map<String, Integer> first = null; // 用于统计每一次,集合组合数据
        // 遍历每一组集合
        for (int i = 0; i < list.size(); i++) {
            // 每一组集合与上一次组合的Map
            Map<String, Integer> temp = new Hashtable<String, Integer>();
            // 第一次循环，first为空
            if (first != null) {
                // 取出上次组合与此次集合的字符，并保存
                for (String s : first.keySet()) {
                    for (String s1 : list.get(i).keySet()) {
                        String str = s + s1;
                        temp.put(str, 1);
                    }
                }
                // 清理上一次组合数据
                if (temp != null && temp.size() > 0) {
                    first.clear();
                }
            } else {
                for (String s : list.get(i).keySet()) {
                    String str = s;
                    temp.put(str, 1);
                }
            }
            // 保存组合数据以便下次循环使用
            if (temp != null && temp.size() > 0) {
                first = temp;
            }
        }
        String returnStr = "";
        if (first != null) {
            // 遍历取出组合字符串
            for (String str : first.keySet()) {
                returnStr += (str + ",");
            }
        }
        if (returnStr.length() > 0) {
            returnStr = returnStr.substring(0, returnStr.length() - 1);
        }
        return returnStr;
    }


    /**
     * 获取屏幕高度
     *
     * @param activity
     * @return
     */
    public static int getWindowHeight(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        return height;
    }

    /**
     * 从res获取String
     *
     * @param resId
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static String getResString(@StringRes int resId, Context ctx) {
        try {
            if (null != ctx && !((Activity) ctx).isDestroyed() && !((Activity) ctx).isFinishing()) {
                return ((Activity) ctx).getString(resId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * 从res获取String
     *
     * @param resId
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static String getResString(@StringRes int resId, String formatValue, Context ctx) {
        try {
            if (null != ctx && !((Activity) ctx).isDestroyed() && !((Activity) ctx).isFinishing()) {
                return ((Activity) ctx).getString(resId, formatValue);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * 按钮保护
     *
     * @param limitTime 限制点击的时间
     * @return
     */
    public static boolean preventCnontinuityClick(Long limitTime, String btnName) {
        if (!btnName.equals(currentClickBtnName) || Math.abs(lastClickTime - System.currentTimeMillis()) > limitTime) {
            currentClickBtnName = btnName;
            lastClickTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }


    /**
     * 获取随机应答语
     */
    public static String getAnswerRandomly(Context context, int arrayID) {
        Resources res = context.getResources();
        String[] arr = res.getStringArray(arrayID);
        java.util.Random random = new java.util.Random();// 定义随机类
        return arr[random.nextInt(arr.length)];
    }

    /**
     * 获取网络在线时间（年）
     */
    public static String getOnlineYear() {
        try {
            URL url = new URL("http://www.baidu.com");
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.connect();
            long time = conn.getDate();
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String res = sdf.format(date).substring(0, 4);
            int year = Integer.valueOf(res);
            //FIXME 使用更好的方式获取网络事件
            if (year < 2017) {
                res = String.valueOf(2017);
            }
            return res;
        } catch (MalformedURLException e) {
            return "-1";
        } catch (IOException e) {
            return "-1";
        }
    }

    /**
     * 把图片保存成jpg格式
     *
     * @param path
     * @param bmp
     */
    public static boolean saveJPEG(String path, Bitmap bmp) {
        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            FileOutputStream os = new FileOutputStream(path);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断是否手机号
     *
     * @param phoneNum
     * @return
     */
    public static boolean isMobilePhone(String phoneNum) {
        Pattern p = null;
        Matcher m = null;
        boolean flag = false;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(phoneNum);
        flag = m.matches();
        return flag;
    }

    /**
     * 将汉字字符串转换成数字字符串
     *
     * @param str
     * @return
     */
    public static String getNumberFromString(String str) {
        String hanzi[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "幺"};
        String alabo[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "1"};
        if (str == null || "".equals(str)) {
            return null;
        }
        //将字符串中中文数字改成阿拉伯数字
        for (int i = 0; i < hanzi.length; i++) {
            str = str.replaceAll(hanzi[i], alabo[i]);
        }
        try {
            Long.valueOf(str);//把字符串强制转换为数字
            return str;//如果是数字，返回
        } catch (Exception e) {
            return null;//如果抛出异常，返回null
        }
    }

    /**
     * 将汉字字符串转换成数字字符串
     *
     * @param str
     * @return
     */
    public static String getStringFromNumber(String str) {
        String hanzi[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "幺"};
        String alabo[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "1"};
        if (str == null || "".equals(str)) {
            return null;
        }
        //将字符串中中文数字改成阿拉伯数字
        for (int i = 0; i < hanzi.length; i++) {
            str = str.replaceAll(alabo[i], hanzi[i]);
        }

        return str;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：com.beidian.test.service.BasicInfoService ）
     * @return
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager manager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = manager.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    /**
     * 获取字典第一项
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> V getFirstOrNull(Map<K, V> map) {
        V obj = null;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            obj = entry.getValue();
            if (obj != null) {
                break;
            }
        }
        return obj;
    }

    /**
     * 判断应用 是在前台还是在后台
     *
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                } else {
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 需要权限:android.permission.GET_TASKS
     *
     * @param context
     * @param className
     * @return
     */
    public static boolean isActivityInFront(Context context, String className) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            String topClass = topActivity.getClassName();
            if (TextUtils.equals(topClass, className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * encodeBase64File:(将文件转成base64 字符串). <br/>
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     * @author guhaizhou@126.com
     * @since JDK 1.6
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    /**
     * List 转 String
     *
     * @param list
     * @return
     */
    public static String listToString(List<String> list, String split) {
        if (list == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean first = true;
        //第一个前面不拼接","
        for (String string : list) {
            if (first) {
                first = false;
            } else {
                result.append(split);
            }
            result.append(string);
        }
        return result.toString();
    }


    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }


    /**
     * 文本文件写入
     *
     * @param ctx
     * @param filePath
     * @param message
     */
    public static void writeFileData(Context ctx, String filePath, String message) {

        SimpleDateFormat df_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        message = df_time.format(new Date()) + "###" + message + "\r\n";

        try {
            File file = new File(filePath);

            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    return;
                }
            }

            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return;
                }
            }

            FileOutputStream f_out = new FileOutputStream(filePath, true);
            BufferedWriter bfWrite = new BufferedWriter(new OutputStreamWriter(f_out));
            bfWrite.write(message);
            bfWrite.flush();
            bfWrite.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件删除
     */
    public static void deleteFileInDisk(String filePath, Context ctx) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }

            file.getAbsoluteFile().delete();
            System.gc();

            Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            media.setData(contentUri);
            ctx.sendBroadcast(media);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 拉起第三方app
     *
     * @param activity
     * @param packagename
     */
    public static void doStartApplicationWithPackageName(Context activity, String packagename) {

        PackageInfo packageinfo = null;

        try {
            packageinfo = activity.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (packageinfo == null) {
            return;
        }

        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        List<ResolveInfo> resolveinfoList = activity.getPackageManager().queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            String packageName = resolveinfo.activityInfo.packageName;
            String className = resolveinfo.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            activity.startActivity(intent);
        }
    }

    /**
     * 异常信息转换为字符串
     *
     * @param ex
     * @return
     */
    public static String getStackMsg(Exception ex) {
        try {
            StringBuffer sb = new StringBuffer();
            StackTraceElement[] stackArray = ex.getStackTrace();
            for (int i = 0; i < stackArray.length; i++) {
                StackTraceElement element = stackArray[i];
                sb.append(element.toString() + "\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void adjustVoiceLevel(int adjust) {
        AudioManager am = (AudioManager) SpeechApp.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }

    /**
     * 获取MP3 路径
     */
    public static String getMp3Path() {
        java.util.Random random = new java.util.Random();// 定义随机类
        switch (random.nextInt(4)) {
            case 0:
                return MP3_PATH.SONG_0;
            case 1:
                return MP3_PATH.SONG_1;
            case 2:
                return MP3_PATH.SONG_2;
            case 3:
                return MP3_PATH.SONG_3;
            case 4:
                return MP3_PATH.SONG_4;
            default:
                return MP3_PATH.SONG_0;
        }
    }

    /**
     * MP3 播放路径
     */
    public interface MP3_PATH {
        String SONG_0 = "/mnt/sdcard/mp3/mp3_0.mp3";
        String SONG_1 = "/mnt/sdcard/mp3/mp3_1.mp3";
        String SONG_2 = "/mnt/sdcard/mp3/mp3_2.mp3";
        String SONG_3 = "/mnt/sdcard/mp3/mp3_3.mp3";
        String SONG_4 = "/mnt/sdcard/mp3/mp3_4.mp3";
    }

    /**
     * 判断网络是否连接
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

}
