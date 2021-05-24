package com.example.administrator.sanlaiuispeech.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.administrator.sanlaiuispeech.R;
import com.example.administrator.sanlaiuispeech.ability.AIUIHelper;
import com.example.administrator.sanlaiuispeech.ability.ConsultationDataLoader;
import com.example.administrator.sanlaiuispeech.adapter.ChatAdapter;
import com.example.administrator.sanlaiuispeech.bean.InfoChat;
import com.example.administrator.sanlaiuispeech.data.AIUIQaDto;
import com.example.administrator.sanlaiuispeech.data.EventMessage;
import com.example.administrator.sanlaiuispeech.data.Point;
import com.example.administrator.sanlaiuispeech.fragment.Enter3LAnserFragment;
import com.example.administrator.sanlaiuispeech.fragment.Enter3LQuestionFragment;
import com.example.administrator.sanlaiuispeech.fragment.HomeFragment;
import com.example.administrator.sanlaiuispeech.fragment.ReceptionDetailFragment;
import com.example.administrator.sanlaiuispeech.fragment.ReceptionFragment;
import com.example.administrator.sanlaiuispeech.fragment.SchemeListFragment;
import com.example.administrator.sanlaiuispeech.util.GlobalFunc;
import com.example.administrator.sanlaiuispeech.work.ChatFragment;
import com.example.administrator.sanlaiuispeech.work.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Handler.Callback {
    private static String TAG = MainActivity.class.getSimpleName();
    //录音权限
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private SharedPreferences sp;
    ListView listView;
    ChatAdapter chatAdapter;
    List<InfoChat> list;
    Button btnStart, btnLast, btnNext, btnHomeBack;
    TextView tvHomeTitle;

    List<String> exhibitionList = new ArrayList<String>();

    String exhibitionAll = "";
    /**
     * 一级界面Fragmnet
     */
    public static final String FRAGMENT_HOME = "fragment_home";
    /**
     * 二级界面Fragmnet
     */
    public static final String FRAGMENT_SCHEME_lIST = "fragment_scheme_list";
    public static final String FRAGMENT_QUESTION = "fragment_question";
    /**
     * 三级界面
     */
    public static final String FRAGMENT_SCHEME_POINTS = "fragment_scheme_points";
    public static final String FRAGMENT_ANSWER = "fragment_answer";

    /**
     * 四级界面
     */
    public static final String FRAGMENT_RECEPTION_DETAIL = "fragment_reception_detail";


    public static final String FRAGMENT_CHAT = "fragment_chat";

    private Fragment currentFragment;
    private String currentTag;
    private String currentScene;

    int flag = 0;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLER_MSG_CODE.GET_CONSULTATION_QA:
                AIUIHelper.getInstance().startVoiceNlp();
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 应用消息编码
     */
    private interface HANDLER_MSG_CODE {
        int GET_APP_TOKEN = 100;
        int START_APP_SERVICES = 101;
        int SEND_APP_EXCEPTION = 102;
        int GET_ONLINE_DATA = 103;
        int GET_CONSULTATION_QA = 104;
        int GET_MEDIAL_DATA = 105;
    }

    private Handler mHandler;

    @SuppressLint("ShowToast")
    protected void onCreate(Bundle savedInstanceState) {
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initLayout();
        requestPermission();
        list = new ArrayList<>();
        chatAdapter = new ChatAdapter(MainActivity.this, list);
        listView.setAdapter(chatAdapter);
        mHandler = new Handler(this);
        getConsultationQaData();

        /*       Chassis.getInstance().connectToSlamtec("172.17.51.136", 1445);*/
    }

    /**
     * 获取咨询问题数据
     */
    private void getConsultationQaData() {
        ConsultationDataLoader.onEventListener listener = new ConsultationDataLoader.onEventListener() {
            @Override
            public void onFinish() {
                mHandler.sendEmptyMessage(HANDLER_MSG_CODE.GET_CONSULTATION_QA);
            }

            @Override
            public void onError(String error) {

            }
        };
        ConsultationDataLoader loader = new ConsultationDataLoader(this);
        loader.loadConsultationData(listener);
    }

    /**
     * 初始化Layout。
     */
    public void initLayout() {

        btnStart = (Button) findViewById(R.id.nlp_start);
        btnLast = (Button) findViewById(R.id.btn_last);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnHomeBack = (Button) findViewById(R.id.btn_home_back);
        tvHomeTitle = (TextView) findViewById(R.id.tv_home_title);
        listView = (ListView) findViewById(R.id.listView);
        btnHomeBack.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnLast.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        replaceFragment(FRAGMENT_HOME);

        exhibitionList.add(Constant.exhibition1);
        exhibitionList.add(Constant.exhibition2);
        exhibitionList.add(Constant.exhibition3);
        exhibitionList.add(Constant.exhibition4);
        exhibitionList.add(Constant.exhibition5);
        for (int i = 0; i < exhibitionList.size(); i++) {
            exhibitionAll = exhibitionAll + exhibitionList.get(i);
            //  Constant.exhibitions=exhibitionList.size();
        }

        EventBus.getDefault().register(this);
    }

    /**
     * Fragment替换逻辑
     *
     * @param tag
     */
    public void replaceFragment(String tag) {

        currentScene = tag;
        if (tag.equals(currentTag)) {
            return;
        }
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(currentFragment).commit();
        }
        currentFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (currentFragment == null) {
            switch (tag) {
                case FRAGMENT_CHAT:
                    currentFragment = new ChatFragment();
                    break;
                case FRAGMENT_HOME:
                    currentFragment = new HomeFragment();
                    break;
                case FRAGMENT_SCHEME_lIST:
                    currentFragment = new SchemeListFragment();
                    tvHomeTitle.setText("接待引导");
                    btnHomeBack.setBackgroundResource(R.mipmap.homeback);
                    break;
                case FRAGMENT_QUESTION:
                    currentFragment = new Enter3LQuestionFragment();
                    break;
                case FRAGMENT_SCHEME_POINTS:
                    currentFragment = new ReceptionFragment();

                    break;
                case FRAGMENT_RECEPTION_DETAIL:
                    currentFragment = new ReceptionDetailFragment();
                    btnStart.setVisibility(View.VISIBLE);
                    btnLast.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                    break;

                case FRAGMENT_ANSWER:
                    currentFragment = new Enter3LAnserFragment();
                    break;

            }
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, currentFragment, tag).commit();
            Log.d("forest", "getSupportFragmentManager().beginTransaction().add commit");
        } else {
            getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
            if (tag == FRAGMENT_RECEPTION_DETAIL) {
                btnStart.setVisibility(View.VISIBLE);
                btnLast.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.VISIBLE);
            } else {
                btnStart.setVisibility(View.INVISIBLE);
                btnNext.setVisibility(View.INVISIBLE);
                btnLast.setVisibility(View.INVISIBLE);
            }
            if (tag == FRAGMENT_HOME) {
                tvHomeTitle.setText("三联机器人欢迎你");
                btnHomeBack.setBackgroundResource(R.mipmap.home);
            } else if (tag == FRAGMENT_QUESTION) {
                tvHomeTitle.setText("走进三联");
                btnHomeBack.setBackgroundResource(R.mipmap.homeback);
            } else if (tag == FRAGMENT_SCHEME_lIST) {
                tvHomeTitle.setText("接待引导");
                btnHomeBack.setBackgroundResource(R.mipmap.homeback);
            }
        }
        currentTag = tag;
    }

    /***
     *back键返回
     *
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AIUIHelper.getInstance().stopSpeak();
            switch (currentTag) {
                case FRAGMENT_HOME:
                    //当前处于一级界面，直接退出
                    finish();
                    break;
                case FRAGMENT_SCHEME_lIST:
                    //当前处于二级界面，回退到一级界面
                    replaceFragment(FRAGMENT_HOME);
                    break;
                case FRAGMENT_ANSWER:
                    replaceFragment(FRAGMENT_QUESTION);
                    break;
                case FRAGMENT_SCHEME_POINTS:
                    //当前处于三级界面，回退到二级界面
                    replaceFragment(FRAGMENT_SCHEME_lIST);
                    break;
                case FRAGMENT_QUESTION:
                    replaceFragment(FRAGMENT_HOME);
                    break;
                case FRAGMENT_CHAT:
                    replaceFragment(FRAGMENT_HOME);
                    break;
                case FRAGMENT_RECEPTION_DETAIL:
                    replaceFragment(FRAGMENT_SCHEME_POINTS);
                    AIUIHelper.getInstance().startVoiceNlp();
                    break;
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //开始语音理解
            case R.id.btn_home_back:
                AIUIHelper.getInstance().stopSpeak();
                switch (currentTag) {
                    case FRAGMENT_HOME:
                        //当前处于一级界面，直接退出
                        finish();
                        break;
                    case FRAGMENT_SCHEME_lIST:
                        //当前处于二级界面，回退到一级界面
                        replaceFragment(FRAGMENT_HOME);
                        break;
                    case FRAGMENT_ANSWER:
                        replaceFragment(FRAGMENT_QUESTION);
                        break;
                    case FRAGMENT_SCHEME_POINTS:
                        //当前处于三级界面，回退到二级界面
                        replaceFragment(FRAGMENT_SCHEME_lIST);

                        break;
                    case FRAGMENT_QUESTION:
                        replaceFragment(FRAGMENT_HOME);
                        break;
                    case FRAGMENT_CHAT:
                        replaceFragment(FRAGMENT_HOME);
                        break;
                    case FRAGMENT_RECEPTION_DETAIL:
                        replaceFragment(FRAGMENT_SCHEME_POINTS);
                        AIUIHelper.getInstance().startVoiceNlp();
                        break;
                }
                break;
            case R.id.nlp_start:
//                if (currentScene == FRAGMENT_SCHEME_POINTS) {
//                    replaceFragment(FRAGMENT_RECEPTION_DETAIL);
//                    EventBus.getDefault().postSticky(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, exhibitionAll));
//                } else {
//                    EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, exhibitionAll));
//                }
                if (flag == 0) {
                    AIUIHelper.getInstance().speakPaused();
                    btnStart.setBackgroundResource(R.mipmap.resume);

                } else if (flag == 1) {
                    AIUIHelper.getInstance().speakResumed();
                    btnStart.setBackgroundResource(R.mipmap.pause);
                }
                flag = (flag + 1) % 2;

                break;
            case R.id.btn_last:
                if ((Constant.exhibitions) > 0 && (Constant.exhibitions != 100)) {
                    EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, exhibitionList.get(Constant.exhibitions - 1)));
                    Constant.exhibitions--;
                } else if (Constant.exhibitions == 0) {
                    EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, "已经是第一个展台"));
                } else if (Constant.exhibitions == 100) {
                    EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, "无法确定位置"));
                }
                break;
            case R.id.btn_next:
                if ((Constant.exhibitions < exhibitionList.size()-1) && (Constant.exhibitions != 100)) {
                    EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, exhibitionList.get(Constant.exhibitions + 1)));
                    Constant.exhibitions++;
                } else if (Constant.exhibitions == exhibitionList.size()-1) {
                    EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, "已经是最后一个展台"));
                } else if (Constant.exhibitions == 100) {
                    EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, "无法确定位置"));
                }
                break;

            default:
                break;
        }
    }

    //申请录音权限
    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            if (i != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 321);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 321) {
//            for (int i = 0; i < permissions.length; i++) {
//                if (grantResults[i] != PERMISSION_GRANTED) {
//                    this.finish();
//                }
//            }
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusClick(EventMessage message) {
        switch (message.msgType) {
            case Constant.SEMANTIC_CODE.AIUI_RESOURCE:
                AIUIQaDto qaBean = (AIUIQaDto) message.getObject();
                InfoChat infoQues = new InfoChat(qaBean.getQues(), 1);
                InfoChat infoStr = new InfoChat(qaBean.getAnswer(), 0);
                list.add(infoQues);
                list.add(infoStr);
                chatAdapter.notifyDataSetChanged();
                commandDistribute(qaBean);
                break;
            case Constant.SEMANTIC_CODE.HOME_UI_CHANGE:
                //  imageView.setMap((Map)message.getObject());
                if (message.object.toString().equals("PLAYING")) {

                    btnStart.setBackgroundResource(R.mipmap.pause);
                    btnStart.setEnabled(true);
                } else if (message.object.toString().equals("PLAYED")) {
                    btnStart.setBackgroundResource(R.mipmap.lose);
                    btnStart.setEnabled(false);
                } else {
                    tvHomeTitle.setText(message.object.toString());
                    btnHomeBack.setBackgroundResource(R.mipmap.homeback);
                }
                break;
            case 3:
                Point point = (Point) message.getObject();

                String x = new DecimalFormat("0.00").format(point.x);//format 返回的是字符串
                String y = new DecimalFormat("0.00").format(point.y);//format 返回的是字符串
                // textView.setText("x:" + x + "   y:" + y);
                break;

            case 9:
                ToastUtils.showLong(message.object.toString());
                //  textView.setText(message.object.toString());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 将语义结果指令进行解析分发
     *
     * @param qaBean 语义结果
     */
    public void commandDistribute(final AIUIQaDto qaBean) {
        if (ObjectUtils.isEmpty(qaBean)) {
            return;
        }
        String ques = qaBean.getQues();
        final String bean = qaBean.getAnswer();
        Log.d("forest", bean + " q:" + ques +
                "currentScene:" + currentScene);
        try {
            if (TextUtils.equals(ques, "返回") || TextUtils.equals(ques, "取消")) {
                AIUIHelper.getInstance().stopSpeak();
                replaceFragment(FRAGMENT_HOME);
            } else if (TextUtils.equals(ques, "闲聊") || TextUtils.equals(ques, "闲聊对话")) {
                replaceFragment(FRAGMENT_CHAT);
                EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.HOME_UI_CHANGE, ques));
                EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.CHAT_UI_CHANGE, "请问有什么可以帮你？"));
            } else if (TextUtils.equals(ques, "走进三联")) {
                replaceFragment(FRAGMENT_QUESTION);
                EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.HOME_UI_CHANGE, ques));
            } else if (TextUtils.equals(ques, "接待引导")) {
                replaceFragment(FRAGMENT_SCHEME_lIST);
            } else if (TextUtils.equals(ques, "开始移动")) {
                if (currentScene == FRAGMENT_SCHEME_POINTS) {
                    replaceFragment(FRAGMENT_RECEPTION_DETAIL);
                    EventBus.getDefault().postSticky(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, exhibitionAll));
                } else if (currentScene == FRAGMENT_RECEPTION_DETAIL) {
                    EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, exhibitionAll));
                }
            } else if ((TextUtils.equals(ques, "上一地点") || TextUtils.equals(ques, "上个展台")) && currentScene == FRAGMENT_RECEPTION_DETAIL) {
                if ((Constant.exhibitions) > 0 && (Constant.exhibitions != 100)) {
                    EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, exhibitionList.get(Constant.exhibitions - 1)));
                    Constant.exhibitions--;
                } else if (Constant.exhibitions == 100) {
                    EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, "无法确定位置"));
                } else if (Constant.exhibitions == 0) {
                    EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, "已经是第一展台"));
                }
            } else if ((TextUtils.equals(ques, "下一地点") || TextUtils.equals(ques, "下个展台")) && currentScene == FRAGMENT_RECEPTION_DETAIL) {
                if ((Constant.exhibitions < exhibitionList.size() - 1) && (Constant.exhibitions != 100)) {
                    EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, exhibitionList.get(Constant.exhibitions + 1)));
                    Constant.exhibitions++;
                } else if (Constant.exhibitions == 100) {
                    EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, "无法确定位置"));
                } else if (Constant.exhibitions == 4) {
                    EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.RECEPTION_DETAIL_UI_CHANGE, "已经是最后一个展台"));
                }
            } else if (TextUtils.equals(ques, "声音大一点") || TextUtils.equals(ques, "大点声")) {
                GlobalFunc.adjustVoiceLevel(AudioManager.ADJUST_RAISE);
            } else if (TextUtils.equals(ques, "声音小一点") || TextUtils.equals(ques, "小点声")) {
                GlobalFunc.adjustVoiceLevel(AudioManager.ADJUST_LOWER);
            } else {
                switch (currentScene) {
                    case FRAGMENT_CHAT:
                        EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.CHAT_UI_CHANGE, bean));
                        break;
                    case FRAGMENT_HOME:
                        replaceFragment(FRAGMENT_CHAT);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.CHAT_UI_CHANGE, bean));
                            }
                        }, 200);
                        break;
                    case FRAGMENT_QUESTION:
                        replaceFragment(FRAGMENT_ANSWER);

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new EventMessage(Constant.SEMANTIC_CODE.ANSWER_UI_CHANGE, qaBean));
                            }
                        }, 200);
                        break;
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
