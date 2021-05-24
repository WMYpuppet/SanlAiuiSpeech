package com.example.administrator.sanlaiuispeech.ability;

import android.graphics.Bitmap;

import android.graphics.RectF;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;


import com.example.administrator.sanlaiuispeech.data.EventMessage;
import com.example.administrator.sanlaiuispeech.data.Point;
import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.action.IMoveAction;
import com.slamtec.slamware.action.ISweepMoveAction;
import com.slamtec.slamware.discovery.DeviceManager;
import com.slamtec.slamware.exceptions.ConnectionFailException;
import com.slamtec.slamware.exceptions.ConnectionTimeOutException;
import com.slamtec.slamware.exceptions.RequestFailException;
import com.slamtec.slamware.exceptions.UnauthorizedRequestException;
import com.slamtec.slamware.exceptions.UnsupportedCommandException;
import com.slamtec.slamware.geometry.PointF;
import com.slamtec.slamware.robot.CompositeMap;
import com.slamtec.slamware.robot.GridMap;
import com.slamtec.slamware.robot.Location;
import com.slamtec.slamware.robot.Map;
import com.slamtec.slamware.robot.MapKind;
import com.slamtec.slamware.robot.MapLayer;
import com.slamtec.slamware.robot.MapType;
import com.slamtec.slamware.robot.MoveOption;
import com.slamtec.slamware.robot.Pose;
import com.slamtec.slamware.sdp.CompositeMapHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import static android.graphics.Bitmap.Config.ARGB_8888;

/**
 * @author lisiyu
 * @version 1.0
 * @copyright 3lrobot Co.,Ltd
 * @date 2019/6/18
 * @emial 732603278@qq.com
 * @function
 */
public class Chassis {

    //TAG
    public static final String TAG = "Chassis";

    //Handler
    private Handler handler = new Handler();

    //静态工厂方法
    private static Chassis chassis = null;

    public static IMoveAction moveAction;

    String ip = "192.168.11.1";
    public static AbstractSlamwarePlatform robotPlatform;
    private final static String USAGE_EXPLORE = "explore";
    private String runningStatus = "";


    private com.slamtec.slamware.robot.Map map;

    //底盘
    public interface OnResultListener {
        void onMoveStatus(String result);
    }

    private OnResultListener onStatusListener = null;//实时底盘状态监听


    public static Chassis getInstance() {
        if (chassis == null) {
            chassis = new Chassis();
        }
        return chassis;
    }

    public void connectToSlamtec(String ip, int port) {
        try {
            robotPlatform = DeviceManager.connect(ip, 1445);

            if (robotPlatform == null) {
                Log.d(TAG, "连接失败:" + "robotPlatform 未初始化");
                EventMessage message = new EventMessage(9,"连接失败");
                EventBus.getDefault().post(message);
            } else {
                //连接成功，开启回调
               // handler.postDelayed(runnable, 100);
                EventMessage message = new EventMessage(9,"连接成功");
                EventBus.getDefault().post(message);
                //载入地图
                // loadMap();
            }

        } catch (Exception e) {
            e.printStackTrace();
            EventMessage message = new EventMessage(9,"连接失败");
            EventBus.getDefault().post(message);
            Log.d(TAG, "连接失败:" + e.getMessage());
        }
    }

    private void loadMap() {
        try {
            CompositeMapHelper compositeMapHelper = new CompositeMapHelper();
            CompositeMap compositeMap = compositeMapHelper.loadFile(Environment.getExternalStorageDirectory() + "/DCIM/test/0614test.stcm");
            Log.d(TAG, Environment.getExternalStorageDirectory() + "/DCIM/test/0614test.stcm");
            if (compositeMap == null) {
                Log.d(TAG, "loadMap 失败:");
                return;
            }

    /*        RectF rectF = new RectF(0, 0, 0, 0);
            IMoveAction moveActioA, moveActionB;
            RecoverLocalizationOptions rlo=new RecoverLocalizationOptions();
            rlo.setMaxRecoverTimeInMilliSeconds(1000);
            rlo.setRecoverMovementType(RecoverLocalizationMovement.Any);
            robotPlatform.setCompositeMap(compositeMap, robotPlatform.getPose());
            moveActioA = robotPlatform.recoverLocalization(rectF,rlo );
            Log.d(TAG,moveActioA.getStatus()+"");*/


            ArrayList<MapLayer> maps = compositeMap.getMaps();
            for (MapLayer ml : maps) {
                if (USAGE_EXPLORE.equals(ml.getUsage())) {
                    GridMap gm = (GridMap) ml;
                    Location origin = gm.getOrigin();
                    map = new Map(new PointF(origin.getX(), origin.getY()), gm.getDimension(), gm.getResolution(), 0, gm.getMapData());
                    break;
                }
            }

            //   mapView.setMap(map);
            EventMessage message = new EventMessage(2,map);
            EventBus.getDefault().post(message);

        } catch (Exception e) {
            Log.d(TAG, "e:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 移动到指定锚点位置
     */
    public void moveTo(final String xPos, final String yPos, OnResultListener mListener) {

        onStatusListener = mListener;
        try {
            moveAction = robotPlatform.getCurrentAction();
            if (moveAction != null)
                moveAction.cancel();

            float x = Float.parseFloat(xPos);
            float y = Float.parseFloat(yPos);

            MoveOption moveOption = new MoveOption();
            moveOption.setPrecise(true);

    /*      setMilestone  用于决定SLAMWARE是规划路径到一系列节点还是直接前往。
            当这个参数为true时，机器人会将上述点视作关键点，通过路径搜索的方式前往目的地；
            当参数为false时，会被视作普通点，不会启用路径搜索功能。*/
            moveOption.setMilestone(false);
            Log.d(TAG, "Move To");

            moveAction = robotPlatform.moveTo(new Location(x, y, 0), moveOption, 0);
            //  moveAction.waitUntilDone();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        try {

            if (null == moveAction) {
                Log.d(TAG, "     moveAction=null");
                return;
            }
            Log.d(TAG, "     moveAction.cancel();");
            moveAction.cancel();
        } catch (RequestFailException e) {
            e.printStackTrace();
        } catch (ConnectionFailException e) {
            e.printStackTrace();
        } catch (ConnectionTimeOutException e) {
            e.printStackTrace();
        } catch (UnauthorizedRequestException e) {
            e.printStackTrace();
        } catch (UnsupportedCommandException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "cancel() exception:" + e.getMessage());
            e.printStackTrace();
        }
    }


    public void pause() {
        try {
            if (null == moveAction) {
                return;
            }
            ((ISweepMoveAction) moveAction).pause();
        } catch (RequestFailException e) {
            e.printStackTrace();
        } catch (ConnectionFailException e) {
            e.printStackTrace();
        } catch (ConnectionTimeOutException e) {
            e.printStackTrace();
        } catch (UnauthorizedRequestException e) {
            e.printStackTrace();
        } catch (UnsupportedCommandException e) {
            e.printStackTrace();
        }
    }


    public void resume() {
        try {
            if (null == moveAction) {
                return;
            }
            ((ISweepMoveAction) moveAction).resume();
        } catch (RequestFailException e) {
            e.printStackTrace();
        } catch (ConnectionFailException e) {
            e.printStackTrace();
        } catch (ConnectionTimeOutException e) {
            e.printStackTrace();
        } catch (UnauthorizedRequestException e) {
            e.printStackTrace();
        } catch (UnsupportedCommandException e) {
            e.printStackTrace();
        }
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            this.update();
            handler.postDelayed(this, 100);// 间隔33ms
        }

        void update() {
            try {
                // *//* 刷新Pose *//*
                Pose pose = robotPlatform.getPose();
             /*   current_location_x.setText(Float.toString(pose.getX()));
                current_location_y.setText(Float.toString(pose.getY()));
                current_location_yaw.setText(Float.toString(pose.getYaw()));*/
                //  *//* 获取地图并刷新 *//*
                EventMessage messagePos = new EventMessage(3,new Point(pose.getX(), pose.getY()));
                EventBus.getDefault().post(messagePos);
                int mapWidth = 0;
                int mapHeight = 0;
                RectF knownArea = robotPlatform.getKnownArea(MapType.BITMAP_8BIT, MapKind.EXPLORE_MAP);
                map = robotPlatform.getMap(MapType.BITMAP_8BIT, MapKind.EXPLORE_MAP, knownArea);
                mapWidth = map.getDimension().getWidth();
                mapHeight = map.getDimension().getHeight();
                Bitmap bitmap = Bitmap.createBitmap(mapWidth, mapHeight, ARGB_8888);
                for (int posY = 0; posY < mapHeight; ++posY) {
                    for (int posX = 0; posX < mapWidth; ++posX) {
                        // get map pixel
                        byte[] data = map.getData();
                        // (-128, 127) to (0, 255)
                        int rawColor = data[posX + posY * mapWidth];
                        rawColor += 127;
                        // fill the bitmap data
                        bitmap.setPixel(posX, posY, rawColor | rawColor << 8 | rawColor << 16 | 0xC0 << 24);
                    }
                }
                //  BitmapDrawable bmpDraw=new BitmapDrawable(bitmap);
                // imageView.setImageDrawable(bmpDraw);
                EventMessage message = new EventMessage(1,bitmap);
                EventBus.getDefault().post(message);

                /* 更新机器人运动状态 */
                if (moveAction != null) {
                    if (moveAction.isEmpty()) {
                        runningStatus = "IDLE";
                    } else {
                        switch (moveAction.getStatus()) {
                            case WAITING_FOR_START:
                                runningStatus = "WAITING_FOR_START";
                                break;
                            case RUNNING:
                                runningStatus = "RUNNING";
                                break;
                            case ERROR:
                                runningStatus = "ERROR" + moveAction.getReason();
                                break;
                            case FINISHED:
                                runningStatus = "FINISHED";
                                break;
                            case PAUSED:
                                runningStatus = "PAUSED";
                                break;
                            case STOPPED:
                                runningStatus = "STOPPED";
                                break;
                        }
                    }
                } else {
                    runningStatus = "IDLE";
                }
                if (null != onStatusListener) {
                    onStatusListener.onMoveStatus(runningStatus);
                }

                /*          *//* 获取版本信息 *//*
                tv_sdk_version.setText(robotPlatform.getSDKVersion());
                *//* 获取Slamware ID *//*
                tv_device_id.setText(robotPlatform.getDeviceId());*/
                /*                *//* 获取电源状态相关信息 *//*
                PowerStatus powerStatus = robotPlatform.getPowerStatus();

                *//* 是否正在充电 *//*
                if (powerStatus.isCharging()) {
                    tv_charging_state.setText("正在充电");
                } else {
                    tv_charging_state.setText("未在充电");
                }

                *//* 是否DC connected *//*
                if (powerStatus.isDCConnected()) {
                    tv_dc_link.setText("已连接");
                } else {
                    tv_dc_link.setText("未连接");
                }*/

                /*                *//* 剩余电池电量 *//*
                current_battery_percentage.setText(powerStatus.getBatteryPercentage() + "%");

                *//* 是否回到充电桩 *//*
                if (powerStatus.getDockingStatus() == DockingStatus.OnDock) {
                    tv_pile_state.setText("已回桩");
                } else if (powerStatus.getDockingStatus() == DockingStatus.NotOnDock) {
                    tv_pile_state.setText("未回桩");
                } else {
                    tv_pile_state.setText("未知状态");
                }*/
                /*
                 *//* 睡眠状态 *//*
                switch (powerStatus.getSleepMode()) {
                    case Awake:
                        tv_sleep_mode.setText("Awake");
                        break;
                    case WakingUp:
                        tv_sleep_mode.setText("WakingUp");
                        break;
                    case Asleep:
                        tv_sleep_mode.setText("Asleep");
                        break;
                    case Unknown:
                        tv_sleep_mode.setText("Unknown");
                        break;
                    default:
                        break;*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /*
     * 断开底盘服务
     *
     * */
    public void destory() {
        if (null != handler) {
            handler.removeCallbacks(runnable); //停止刷新
        }
        if (null != robotPlatform) {
            robotPlatform.disconnect();
        }
    }
}
