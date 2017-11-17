package com.youyou.uucar.UI.Main;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.user.UserInterface;
import com.uu.client.bean.user.common.UserCommon;
import com.youyou.uucar.DB.Model.OpenCityModel;
import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.DB.Model.UserModel;
import com.youyou.uucar.DB.Service.UserService;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.FindCarFragment.FindCarListFragment;
import com.youyou.uucar.UI.Main.FindCarFragment.FindCarMapFragment;
import com.youyou.uucar.UI.Main.Login.LoginActivity;
import com.youyou.uucar.UI.Main.Login.NoPasswordLogin;
import com.youyou.uucar.UI.Main.MyStrokeFragment.MyStrokeFragment;
import com.youyou.uucar.UI.Main.MyStrokeFragment.OrderHistoryActivity;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterRegisterVerify;
import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterRegisterVerifyError;
import com.youyou.uucar.UI.Main.fragment.CarManagerFragment;
import com.youyou.uucar.UI.Main.my.My;
import com.youyou.uucar.UI.Main.rent.FindCarAgreeActivity;
import com.youyou.uucar.UI.Main.rent.NoCarWait;
import com.youyou.uucar.UI.Main.rent.OneToOneWaitActivity;
import com.youyou.uucar.UI.Orderform.RenterOrderInfoActivity;
import com.youyou.uucar.UI.Owner.addcar.AddCarBrandActivity;
import com.youyou.uucar.UI.Owner.addcar.OwnerCarInfoActivity;
import com.youyou.uucar.UI.Owner.help.OwnerHelpManager;
import com.youyou.uucar.UI.Renter.Register.RenterRegisterIDActivity;
import com.youyou.uucar.UI.Renter.carinfo.OldCarInfoActivity;
import com.youyou.uucar.UI.Renter.filter.RentFilterActivity;
import com.youyou.uucar.UI.operate.OperatePopActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.MyColorAnimationView;
import com.youyou.uucar.Utils.View.MyLineAnimationView;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;
import com.youyou.uucar.Utils.socket.SocketCommunication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivityTab extends BaseActivity {

    public static final int LOGIN = 100;
    public static final int GOTO_MY = 200;

    public static final String GOTO_RENTER_VERIFY = "renter_verify";
    public static final String GOTO_RENTER_VERIFY_ERROR = "renter_verify_error";
    public static final String GOTO_RENTER_STROKE = "renter_stroke";
    public static final String GOTO_OWNER_CAR_MANAGER = "owner_car_manager";
    public static final String GOTO_OWNER_ADDCAR = "addcar";
    public static final String GOTO_RENTER_FIND_CAR = "find_car";
    public static final String GOTO_SPEED_RENT = "speed";
    public static final String GOTO_FAST_RENT = "speed_click";
    public static final String GOTO_RENTER_ORDERINFO = "renter_order";

    //活动弹窗
    public static final String GOTO_OPERATE_POP = "operate_pop";

    //新增 客户端应该支持点击通知栏直接跳转某个页面
//    public static final String GOTO_MAIN = "GOTO_MAIN"; // 首页
    public static final String GOTO_CAR_DETAIL = "CAR_DETAIL"; // 车辆详情
    //    public static final String GOTO_STROKE = "GOTO_STROKE"; // 行程
    public static final String GOTO_RENTER_ORDER = "GOTO_RENTER_ORDER"; // 订单详情（租客）
    public static final String GOTO_OWNER_ORDER = "GOTO_OWNER_ORDER"; // 订单详情（车主）
    public static final String GOTO_WAITING_FAST = "GOTO_WAITING_FAST"; // 约车等待（一件约车）
    public static final String GOTO_WAITING_ONE = "GOTO_WAITING_ONE"; // 约车等待（普通约车）
    //    public static final String GOTO_CAR_MANAGER = "GOTO_CAR_MANAGER"; //  车辆管理
    public static final String GOTO_ADD_CAR = "GOTO_ADD_CAR"; // 添加车辆
//    public static final String GOTO_BEGIN_REVIEW = "GOTO_BEGIN_REVIEW"; // 租客开始审核
//    public static final String GOTO_AUDIT_FAILURE = "GOTO_AUDIT_FAILURE"; // 审核失败
//    public static final String GOTO_MY = "GOTO_MY"; // 我的


    public static boolean openMenu = false;
    public final int FINDCAR_MODE_LIST = 0;
    public int findcar_Mode = FINDCAR_MODE_LIST;
    public final int FINDCAR_MODE_MAP = 1;
    public final int BRAND_RESULT = 900;
    public String tag = "MainActivityTab";
    // 一对一约车列表,下面点击进入快速约车
    //取车位置TITLE,取车位置比例尺

    // 车辆详情评论每次刷新都清空一下
    //选择地点,展开的时候点击列表把他收起来
    //租客订单详情,点击车图片进入车辆详情
    public Uri getIntentData(Intent inte) {
        String gotoStr = inte.getStringExtra("goto");
//        String gotoStr = getIntent().getStringExtra("goto");
        Uri thisUri = FindCarListFragment.URI;
        if (gotoStr != null) {
            if (gotoStr.equals(GOTO_RENTER_VERIFY)) {
                startActivity(new Intent(context, RenterRegisterVerify.class));
            } else if (gotoStr.equals(GOTO_RENTER_VERIFY_ERROR)) {
                startActivity(new Intent(context, RenterRegisterVerifyError.class));
            } else if (gotoStr.equals(GOTO_SPEED_RENT)) {
                Intent intent = new Intent(context, FindCarAgreeActivity.class);
                intent.putExtra("maxtime", (long) (600000));
                intent.putExtra("speed", true);
                intent.putExtra("passedTime", (long) (0));
                startActivity(intent);
            } else if (gotoStr.equals(GOTO_FAST_RENT)) {
                speedClick();
            } else if (gotoStr.equals(GOTO_RENTER_ORDERINFO)) {
                Intent intent = new Intent(Config.currentContext, RenterOrderInfoActivity.class);
                intent.putExtra(SysConfig.R_SN, getIntent().getStringExtra(SysConfig.R_SN));
                Config.currentContext.startActivity(intent);
                Config.currentContext.finish();
//                ObserverManager.dialogBool.set(false);
            } else if (gotoStr.equals(GOTO_RENTER_STROKE)) {
                findCar.setSelected(false);
                order.setSelected(true);
                owner.setSelected(false);
                my.setSelected(false);
                thisUri = MyStrokeFragment.URI;
            } else if (gotoStr.equals(GOTO_OWNER_CAR_MANAGER)) {
                thisUri = CarManagerFragment.URI;
                findCar.setSelected(false);
                order.setSelected(false);
                owner.setSelected(true);
                my.setSelected(false);
            } else if (gotoStr.equals(GOTO_RENTER_FIND_CAR)) {
                thisUri = FindCarListFragment.URI;
                findCar.setSelected(true);
                order.setSelected(false);
                owner.setSelected(false);
                my.setSelected(false);
            } else if (gotoStr.equals(GOTO_CAR_DETAIL)) {
                Intent intent = new Intent(context, OldCarInfoActivity.class);
                intent.putExtra(SysConfig.CAR_SN, getIntent().getStringExtra(SysConfig.CAR_SN));
                startActivity(intent);
            } else if (gotoStr.equals(GOTO_RENTER_ORDER)) {
                Intent intent = new Intent(context, RenterOrderInfoActivity.class);
                intent.putExtra(SysConfig.R_SN, getIntent().getStringExtra(SysConfig.R_SN));
                startActivity(intent);
            } else if (gotoStr.equals(GOTO_OWNER_ORDER)) {
                Intent intent = new Intent(context, OwnerCarInfoActivity.class);
                intent.putExtra(SysConfig.R_SN, getIntent().getStringExtra(SysConfig.R_SN));
                startActivity(intent);
            } else if (gotoStr.equals(GOTO_ADD_CAR)) {
                Intent intent = new Intent(context, AddCarBrandActivity.class);
                startActivity(intent);
            } else if (gotoStr.equals(GOTO_WAITING_FAST)) {
                Intent intent = new Intent(context, NoCarWait.class);
                startActivity(intent);
            } else if (gotoStr.equals(GOTO_WAITING_ONE)) {
                Intent intent = new Intent(context, OneToOneWaitActivity.class);
                startActivity(intent);
            } else if (gotoStr.equals(GOTO_OPERATE_POP)) {
                Intent intent = new Intent(context, OperatePopActivity.class);
                intent.putExtra("canClose", inte.getBooleanExtra("canClose", false));
                intent.putExtra("wording", inte.getStringExtra("wording"));
                intent.putExtra("actionUrl", inte.getStringExtra("actionUrl"));
                intent.putExtra("imgUrl", inte.getStringExtra("imgUrl"));
                startActivity(intent);
            }
        }
        return thisUri;
    }

    @InjectView(R.id.content_frame)
    FrameLayout mContentFrame;
    @InjectView(R.id.tab_root)
    LinearLayout mTabRoot;
//    @InjectView(R.id.findcar)
//    RelativeLayout mFindcar;
//    @InjectView(R.id.order)
//    RelativeLayout mOrder;
//    @InjectView(R.id.owner)
//    RelativeLayout mOwner;
//    @InjectView(R.id.my)
//    RelativeLayout mMy;
//    @InjectView(R.id.speed)
//    RelativeLayout mSpeed;


    /**
     *
     */
    @OnClick(R.id.speed)
    public void speedClick() {
        if (Config.isNetworkConnected(this)) {
            if (Config.isGuest(context)) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("goto", NoPasswordLogin.RENTER_REGISTER);
                startActivity(intent);
            } else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_NEW)) {
                Intent intent = new Intent(context, RenterRegisterIDActivity.class);
                intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
                startActivity(intent);
            } else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_ALL)) {
                Intent intent = new Intent(context, RenterRegisterVerify.class);
                startActivity(intent);
            } else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_ZUKE_NO)) {
                Intent intent = new Intent(context, RenterRegisterVerifyError.class);
                intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
                startActivity(intent);
            } else {
                if (Config.isSppedIng) {
                    if (Config.speedHasAgree) {
                        Intent intent = new Intent(context, FindCarAgreeActivity.class);
                        intent.putExtra("maxtime", (long) (600000));
                        intent.putExtra("speed", true);
                        intent.putExtra("passedTime", (long) (0));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, NoCarWait.class);
                        intent.putExtra("maxtime", (long) (600000));
                        intent.putExtra("passedTime", (long) (0));
                        startActivity(intent);
                    }

                } else if (Config.isOneToOneIng) {
                    Intent intent = new Intent(context, OneToOneWaitActivity.class);
                    intent.putExtra("maxtime", (long) (600000));
                    intent.putExtra("passedTime", (long) (0));
                    startActivity(intent);
                } else if (Config.hasPayOrder) {
                    Intent intent = new Intent(context, RenterOrderInfoActivity.class);
                    intent.putExtra(SysConfig.R_SN, Config.waitPayOrderId);
                    startActivity(intent);
                } else {
                    //startActivity(new Intent(context, SpeedRentCarActivity.class));
                    Intent intent = new Intent(context, RentFilterActivity.class);
                    intent.putExtra("mult", true);//一对多流程
                    startActivity(intent);
                }
            }
        } else {
            Config.showFiledToast(context);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        MainActivityTab.instance = this;
        MLog.e(tag, "MainActivity___onNewIntent" + intent.getStringExtra("goto"));
        updateContent(getIntentData(intent));
        invalidateOptionsMenu();
    }


    public static MainActivityTab instance;
    public Activity context;
    ActionBar actionBar;
    public String[] citys;
    public ObserverListener changeCityObserverListener = new ObserverListener() {
        @Override
        public void observer(String from, Object obj) {
            int index = 0;
            for (int i = 0; i < Config.openCity.size(); i++) {
                if (obj.toString().indexOf(Config.openCity.get(i).getName()) != -1) {
                    index = i;
                    actionBar.setSelectedNavigationItem(index);
                    break;
                }
            }
        }
    };
    public ObserverListener loginListener = new ObserverListener() {
        @Override
        public void observer(String from, Object obj) {

            order.needRefush = true;
            owner.needRefush = true;
            order.currentRefush = true;
            order.cancelRefush = true;
            order.finishRefush = true;

            invalidateOptionsMenu();

        }
    };
    public ObserverListener logoutListener = new ObserverListener() {
        @Override
        public void observer(String from, Object obj) {

            order.needRefush = true;
            owner.needRefush = true;
            order.currentRefush = true;
            order.cancelRefush = true;
            order.finishRefush = true;
            Config.isOneToOneIng = false;
            Config.isSppedIng = false;
            Config.speedHasAgree = false;
            Config.hasPayOrder = false;
            speed.name.setText("我要约车");
            setPublicSetTabNum();
        }
    };
    public ObserverListener tabNumListener = new ObserverListener() {
        @Override
        public void observer(String from, Object obj) {

            if (obj.equals("showFinishNews")) {
                isShowFinishNews = true;
            } else {
                isShowFinishNews = false;
            }
            setTabNum();
        }
    };
    public TabModel findCar, order, owner, my, speed;
    NavigatIonAdapter navigatIonAdapter;
    Handler handler = new Handler();

    boolean isShowFinishNews;

    //强制更新dialog
    public AlertDialog forceUpdateDialog;
    //更新更新内容
    public TextView umeng_update_content;
    //立即更新按钮
    public Button umeng_update_id_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.currentContext = this;
        instance = this;
        context = this;
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.show();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        ObserverManager.addObserver("mainTabChangeCity", changeCityObserverListener);
        ObserverManager.addObserver(ObserverManager.MAINLOGIN, loginListener);
        ObserverManager.addObserver(ObserverManager.MAINLOGOUT, logoutListener);
        ObserverManager.addObserver(ObserverManager.MAINTABNUM, tabNumListener);
        citysp = context.getSharedPreferences("selectcity", Context.MODE_PRIVATE);
        citys = new String[Config.openCity.size()];
        for (int i = 0; i < citys.length; i++) {
            citys[i] = Config.openCity.get(i).getName();
        }
        int index = 0;
        for (int i = 0; i < Config.openCity.size(); i++) {
            if (citysp.getString("city", "北京").equals(Config.openCity.get(i).getName())) {
                index = i;
                break;
            }
        }
        navigatIonAdapter = new NavigatIonAdapter();
        actionBar.setListNavigationCallbacks(navigatIonAdapter, navigationListener);
        actionBar.setSelectedNavigationItem(index);
        setContentView(R.layout.animation_main_tab);
        ButterKnife.inject(this);
        initAnimation();
        /*UmengUpdateAgent.setDefault();
        UmengUpdateAgent.setUpdateAutoPopup(true);
        UmengUpdateAgent.update(getApplicationContext());
        UmengUpdateAgent.setUpdateOnlyWifi(false);*/
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                    //判断是否有新版本需要更新
                    case UpdateStatus.Yes: // has update
                        try {
                            //在线读取更新参数
                            String value = MobclickAgent.getConfigParams(MainActivityTab.this, "FORCE_UPDATE_MIXVERSION");
                            if (value != null && !value.trim().equals("")) {
                                int versionCode = Config.changeVersionNameToCode(value);
                                if (versionCode != 0) {
                                    String localVersionName = getVersionName();
                                    int localVersionCode = Config.changeVersionNameToCode(localVersionName);
                                    //判断当前版本号于友盟中的最低版本号，若当前版本号小于最低版本号，则强制更新，否则非强制更新
                                    if (localVersionCode <= versionCode) {
                                        UmengUpdateAgent.setUpdateAutoPopup(false);

                                        LayoutInflater inflater = LayoutInflater.from(MainActivityTab.this);
                                        View view = inflater.inflate(R.layout.umeng_update_dialog_force, null);
                                        umeng_update_content = (TextView) view.findViewById(R.id.umeng_update_content);
                                        umeng_update_id_ok = (Button) view.findViewById(R.id.umeng_update_id_ok);

                                        final UpdateResponse updateInfos = updateInfo;
                                        umeng_update_id_ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                UmengUpdateAgent.startDownload(MainActivityTab.this, updateInfos);
                                            }
                                        });

                                        StringBuffer content = new StringBuffer("最新版本：" + updateInfo.version + "\n\n");
                                        content.append("更新内容\n" + updateInfo.updateLog + "\n");
                                        umeng_update_content.setText(content.toString());
                                        forceUpdateDialog = new AlertDialog.Builder(MainActivityTab.this).create();
                                        forceUpdateDialog.setCancelable(false);
                                        forceUpdateDialog.setView(view);
                                        forceUpdateDialog.show();
                                    } else {
                                        UmengUpdateAgent.setUpdateAutoPopup(true);
                                        UmengUpdateAgent.showUpdateDialog(context, updateInfo);
                                    }
                                } else {
                                    UmengUpdateAgent.setUpdateAutoPopup(true);
                                    UmengUpdateAgent.showUpdateDialog(context, updateInfo);
                                }
                            } else {
                                UmengUpdateAgent.setUpdateAutoPopup(true);
                                UmengUpdateAgent.showUpdateDialog(context, updateInfo);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    case UpdateStatus.No: // has no update
                        //showToast("您当前使用的友友租车已是最新版本");
                        break;
                }
            }
        });
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.forceUpdate(context);

        ObserverManager.addObserver("Miss", new ObserverListener() {
            @Override
            public void observer(String from, final Object obj) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (!isOrderHasNum) {
                            order.tv_num.setVisibility(View.VISIBLE);
                            if (Config.orderTipMsg != null) {
                                UuCommon.TipsMsg strokeTip = Config.orderTipMsg;
                                if (strokeTip.getType().equals(UuCommon.PromptType.RedPoint)) {
                                    order.tv_num.setWidth(Config.dip2px(context, 11));
                                    order.tv_num.setHeight(Config.dip2px(context, 11));
                                    order.tv_num.setText("");
                                } else {
                                    order.tv_num.setWidth(Config.dip2px(context, 20));
                                    order.tv_num.setHeight(Config.dip2px(context, 20));
                                    order.tv_num.setText(strokeTip.getNum() + "");
                                }
                                ObserverManager.getObserver("进行中").observer("", "show");
                            } else if (obj != null && obj.equals("show")) {
                                order.tv_num.setWidth(Config.dip2px(context, 11));
                                order.tv_num.setHeight(Config.dip2px(context, 11));
                                order.tv_num.setText("");
                            }
//                            else
//                            {
//
//                                ObserverManager.getObserver("进行中").observer("", "");
//                            }

                        }
                    }
                });
            }
        });
//    @InjectView(R.id.order)
//    RelativeLayout mOrder;
//    @InjectView(R.id.owner)
//    RelativeLayout mOwner;
//    @InjectView(R.id.my)
//    RelativeLayout mMy;
//    @InjectView(R.id.speed)
//    RelativeLayout mSpeed;
        findCar = new TabModel();
        findCar.root = (RelativeLayout) findViewById(R.id.findcar);
        findCar.setSelected(true);
        findCar.root.setOnClickListener(tabClick);

        order = new TabModel();
        order.root = (RelativeLayout) findViewById(R.id.order);
        order.root.setOnClickListener(tabClick);
        order.tv_num = (TextView) findViewById(R.id.order_num);

        owner = new TabModel();
        owner.root = (RelativeLayout) findViewById(R.id.owner);
        owner.root.setOnClickListener(tabClick);
        owner.tv_num = (TextView) findViewById(R.id.owner_num);

        my = new TabModel();
        my.root = (RelativeLayout) findViewById(R.id.my);
        my.root.setOnClickListener(tabClick);
        my.tv_num = (TextView) findViewById(R.id.my_new);
        speed = new TabModel();
        speed.root = (RelativeLayout) findViewById(R.id.speed);
        speed.tv_num = (TextView) findViewById(R.id.speed_num);
        speed.name = (TextView) findViewById(R.id.speed_name);
        updateContent(getIntentData(getIntent()));

//        Handler handler = new Handler()
//        {
//            @Override
//            public void handleMessage(Message msg)
//            {
//                super.handleMessage(msg);
//                if (msg.what == 1)
//                {
//                    startAnimation();
//                }
//                else if (msg.what == 2)
//                {
//                    endAnimation();
//                }
//
//            }
//        };
//        handler.sendEmptyMessageDelayed(1, 2000);
//        handler.sendEmptyMessageDelayed(2, 10000);

//        SocketCommunication.showNotification("友友租车", "111", GOTO_OWNER_CAR_MANAGER);
//        startActivity(new Intent(context, OperatePopActivity.class));
    }

    public String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        String version = packInfo.versionName;
        return version;
    }

    private MyColorAnimationView animationView;
    private MyLineAnimationView lineAnimation;
    private FrameLayout animatorFrameLayout;

    private void initAnimation() {
        animatorFrameLayout = (FrameLayout) findViewById(R.id.animator_framelayout);
        FrameLayout container = (FrameLayout) findViewById(R.id.container);
//        final View view1 = getLayoutInflater().inflate(R.layout.animation_framelayout, null, false);
        animationView = new MyColorAnimationView(this);
        lineAnimation = new MyLineAnimationView(this);
        container.addView(animationView);
//        container.addView(view1);
        container.addView(lineAnimation);

    }


    private void startAnimation() {
        animatorFrameLayout.setVisibility(View.VISIBLE);
        animationView.startAnimation();
        lineAnimation.startAnimation();
    }

    public void endAnimation() {
        animatorFrameLayout.setVisibility(View.GONE);
        animationView.clearColorAnimation();
        lineAnimation.clearLineAnimation();
    }


    SharedPreferences citysp;

    public void gotoFindCar() {
        findCar.setSelected(true);
        order.setSelected(false);
        owner.setSelected(false);
        my.setSelected(false);
        if (findcar_Mode == FINDCAR_MODE_LIST) {

            updateContent(FindCarListFragment.URI);
        } else {
            updateContent(FindCarMapFragment.URI);
        }
        invalidateOptionsMenu();
    }

    public void gotoStroke() {

        findCar.setSelected(false);
        order.setSelected(true);
        owner.setSelected(false);
        my.setSelected(false);
        updateContent(MyStrokeFragment.URI);
        invalidateOptionsMenu();
    }

    public void gotoOwner() {


        findCar.setSelected(false);
        order.setSelected(false);
        owner.setSelected(true);
        my.setSelected(false);
        updateContent(CarManagerFragment.URI);
        invalidateOptionsMenu();
    }

    public void gotoMy() {
        findCar.setSelected(false);
        order.setSelected(false);
        owner.setSelected(false);
        my.setSelected(true);
        updateContent(My.URI);
        invalidateOptionsMenu();
    }

    public class NavigatIonAdapter implements SpinnerAdapter {
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.action_bar_popup_item, null);
            ((TextView) convertView).setText(getItem(position).toString());

            int index = 0;
            for (int i = 0; i < Config.openCity.size(); i++) {
                if (citysp.getString("city", "北京").equals(Config.openCity.get(i).getName())) {
                    index = i;
                    break;
                }
            }
            if (index == position) {
                convertView.setBackgroundColor(getResources().getColor(R.color.c14));
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.c11));
            }

            return convertView;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return citys.length;
        }

        @Override
        public Object getItem(int position) {
            return citys[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.simple_spinner_item, null);
            ((TextView) convertView).setText("友友租车-" + getItem(position).toString());
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    boolean isFirst = true;//第一次进来的时候会默认调用一次
    public ActionBar.OnNavigationListener navigationListener = new ActionBar.OnNavigationListener() {
        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            MLog.e(tag, "navigationListenr select =  " + itemPosition);
            if (isFirst) {
                isFirst = false;
            } else {
                SharedPreferences.Editor edit = citysp.edit();
                edit.putString("city", citys[itemPosition]);
                edit.putBoolean("selectcity", true);
                edit.commit();
                ObserverManager.getObserver("rentlist").observer("filter", "selectCity");
                ObserverManager.getObserver("rentmap").observer("filter", "selectCity");
            }
            return false;
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        instance = this;
        if (!Config.missDontRefresh) {
            if (!Config.isGuest(this)) {
                setTabNum();
            } else {
                setPublicSetTabNum();
            }
        } else {
            if (!isOrderHasNum) {
                order.tv_num.setVisibility(View.VISIBLE);
                if (Config.orderTipMsg != null) {
                    UuCommon.TipsMsg strokeTip = Config.orderTipMsg;
                    if (strokeTip.getType().equals(UuCommon.PromptType.RedPoint)) {
                        order.tv_num.setWidth(Config.dip2px(context, 11));
                        order.tv_num.setHeight(Config.dip2px(context, 11));
                        order.tv_num.setText("");
                    } else {
                        order.tv_num.setWidth(Config.dip2px(context, 20));
                        order.tv_num.setHeight(Config.dip2px(context, 20));
                        order.tv_num.setText(strokeTip.getNum() + "");
                    }
                }
            }

        }
        if (SocketCommunication.mNotificationManager == null) {
            SocketCommunication.mNotificationManager = (NotificationManager) Config.currentContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (SocketCommunication.mNotificationManager != null) {
            SocketCommunication.mNotificationManager.cancel(1);
        }
        TestinAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TestinAgent.onStop(this);
    }

    //优惠券数量
    public static int couponNum = 0;

    //获取开放城市,当本地版本号和服务器不同步的时候调用
    public void getBrand(final UserInterface.StartQueryInterface.Response response) {
        //车辆品牌版本号
        SharedPreferences sp = getSharedPreferences("brand_version", Context.MODE_PRIVATE);
        try {
            if (!sp.getString("version", "2").equals(response.getCarBrandsVersion() + "")) {
                CarInterface.QueryCarBrands.Request.Builder request = CarInterface.QueryCarBrands.Request.newBuilder();
                NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.QueryCarBrands_VALUE);
                networkTask.setBusiData(request.build().toByteArray());
                NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                    @Override
                    public void onSuccessResponse(UUResponseData protoBuf) {
                        if (protoBuf.getRet() == 0) {
                            try {
                                CarInterface.QueryCarBrands.Response response2 = CarInterface.QueryCarBrands.Response.parseFrom(protoBuf.getBusiData());
                                if (response2.getRet() == 0) {
                                    SharedPreferences sp = context.getSharedPreferences("brand", Context.MODE_PRIVATE);
                                    final String jsonStr = response2.getCarBrandJsonStr();
                                    try {
                                        JSONObject jsonObject = new JSONObject(jsonStr);
                                        SharedPreferences.Editor edit = sp.edit();
//                                        MLog.e(tag, "jsonStr = " + jsonStr);
                                        edit.putString("brand", jsonStr);
                                        edit.commit();
                                        SharedPreferences.Editor version_edit = sp.edit();
                                        edit.putString("version", response.getCarBrandsVersion() + "");
                                        edit.commit();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(VolleyError errorResponse) {

                    }

                    @Override
                    public void networkFinish() {
                        Config.dismissProgress();

                    }
                });
            } else {
                SharedPreferences sp2 = context.getSharedPreferences("brand", Context.MODE_PRIVATE);
                if (sp2.getString("brand", "").equals("")) {
                    JSONObject brand_json = new JSONObject();
                    try {
                        // Return an AssetManager instance for your application's package
                        InputStream is = context.getAssets().open("brand.txt");
                        int size = is.available();
                        // Read the entire asset into a local byte buffer.
                        byte[] buffer = new byte[size];
                        is.read(buffer);
                        is.close();
                        // Convert the buffer into a string.
                        String text = new String(buffer, "UTF-8");
                        brand_json = new JSONObject(text);
                        MLog.e(tag, "brand_json = " + brand_json);
                    } catch (Exception e) {
                        // Should never happen!
                        throw new RuntimeException(e);
                    }
                    SharedPreferences.Editor edit = sp2.edit();
                    edit.putString("brand", brand_json.toString());
                    edit.commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCity(final UserInterface.StartQueryInterface.Response cityVersionResponse) {
        CarInterface.QueryAvailableCitys.Request.Builder request = CarInterface.QueryAvailableCitys.Request.newBuilder();
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.QueryAvailableCitys_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData protoBuf) {
                if (protoBuf.getRet() == 0) {
                    try {
                        CarInterface.QueryAvailableCitys.Response response = CarInterface.QueryAvailableCitys.Response.parseFrom(protoBuf.getBusiData());
                        if (response.getRet() == 0) {
                            List<String> cityList = new ArrayList<String>();
                            List<String> shortCityList = new ArrayList<String>();
                            List<CarInterface.QueryAvailableCitys.City> list = response.getCitysList();
                            for (int i = 0; i < list.size(); i++) {
                                CarInterface.QueryAvailableCitys.City jsonObj = list.get(i);
                                if (jsonObj.hasName()) {
                                    cityList.add(jsonObj.getName());
                                    shortCityList.add(jsonObj.getShortName());
                                }
                            }
                            Config.openCity.clear();
                            Config.openCity.addAll(response.getCitysList());
                            OpenCityModel.getInstance(context).setVersion(cityVersionResponse.getAvailableCitysVersion());
                            OpenCityModel.getInstance(context).setOpenCity(list);

                            citys = new String[Config.openCity.size()];
                            for (int i = 0; i < citys.length; i++) {
                                citys[i] = Config.openCity.get(i).getName();
                            }

                        }

                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(VolleyError errorResponse) {
            }

            @Override
            public void networkFinish() {

            }
        });

    }

    public void setPublicSetTabNum() {
        UserInterface.StartQueryInterface.Request.Builder builder = UserInterface.StartQueryInterface.Request.newBuilder();
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.PublicStartQueryInterface_VALUE);
        task.setBusiData(builder.build().toByteArray());
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        final UserInterface.StartQueryInterface.Response response = UserInterface.StartQueryInterface.Response.parseFrom(responseData.getBusiData());
                        MLog.e(tag, "current city version = " + OpenCityModel.getInstance(context).version + "   server city version = " + response.getAvailableCitysVersion());
                        //开放城市版本号
                        if (OpenCityModel.getInstance(context).version != response.getAvailableCitysVersion()) {
                            getCity(response);
                        }

                        //车辆列表逻辑处理
                        getBrand(response);
                        Config.startTimeCount = response.getMaxReserveCarDays();
                        Config.RentTimeCount = response.getMaxRentCarDays();
                        if (Config.startTimeCount == 0) {
                            Config.startTimeCount = 30;
                        }
                        if (Config.RentTimeCount == 0) {
                            Config.RentTimeCount = 7;
                        }
                        ObserverManager.getObserver("FinishTip").observer("", "");
                        ObserverManager.getObserver("已完成").observer("", "");
                        ObserverManager.getObserver(ObserverManager.MAINTABNUM).observer("", "");
                        if (order != null && order.tv_num != null && findCar != null /*&& findCar.tv_num!=null*/ && owner != null && owner.tv_num != null && my != null && my.tv_num != null && speed != null && speed.name != null) {
                            order.tv_num.setVisibility(View.GONE);
//                            findCar.tv_num.setVisibility(View.GONE);
                            owner.tv_num.setVisibility(View.GONE);
                            my.tv_num.setVisibility(View.GONE);
                            speed.name.setText("我要约车");
                            endAnimation();
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(VolleyError errorResponse) {

            }

            @Override
            public void networkFinish() {

            }
        });


    }

    //如果行程tab有票数字角标,设置为true,否则为false
    boolean isOrderHasNum = false;

    /**
     * 查询车辆品牌,当本地版本号和服务器不同步的时候调用
     */
    public void setTabNum() {
        UserInterface.StartQueryInterface.Request.Builder builder = UserInterface.StartQueryInterface.Request.newBuilder();
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.StartQueryInterface_VALUE);
        task.setBusiData(builder.build().toByteArray());
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        final UserInterface.StartQueryInterface.Response response = UserInterface.StartQueryInterface.Response.parseFrom(responseData.getBusiData());
                        MLog.e(tag, "current city version = " + OpenCityModel.getInstance(context).version + "   server city version = " + response.getAvailableCitysVersion());
                        //开放城市版本号
                        if (OpenCityModel.getInstance(context).version != response.getAvailableCitysVersion()) {
                            getCity(response);
                        }

                        /**
                         * 运控起租时间和租期
                         */
                        Config.startTimeCount = response.getMaxReserveCarDays();
                        Config.RentTimeCount = response.getMaxRentCarDays();
                        if (Config.startTimeCount == 0) {
                            Config.startTimeCount = 30;
                        }
                        if (Config.RentTimeCount == 0) {
                            Config.RentTimeCount = 7;
                        }
                        //车辆列表逻辑处理
                        getBrand(response);
                        //用户相关接口
                        UserCommon.UserStatus userStatus = response.getUserStatus();
                        if (userStatus.hasTrip() || userStatus.getTrip().getNum() > 0) {
                            order.tv_num.setVisibility(View.VISIBLE);
                            UuCommon.TipsMsg strokeTip = userStatus.getTrip();
                            if (strokeTip.getType().equals(UuCommon.PromptType.RedPoint)) {
                                order.tv_num.setWidth(Config.dip2px(context, 11));
                                order.tv_num.setHeight(Config.dip2px(context, 11));
                                order.tv_num.setText("");
                                ObserverManager.getObserver("进行中").observer("", "show");
                            } else {
                                order.tv_num.setWidth(Config.dip2px(context, 20));
                                order.tv_num.setHeight(Config.dip2px(context, 20));
                                order.tv_num.setText(strokeTip.getNum() + "");
                                ObserverManager.getObserver("进行中").observer("", strokeTip.getNum() + "");
                            }
                            order.needRefush = true;

                            isOrderHasNum = true;
                        } else {
                            order.needRefush = false;
                            if (!isShowFinishNews) {
                                order.tv_num.setVisibility(View.GONE);
                            }
                            isOrderHasNum = false;
                            ObserverManager.getObserver("进行中").observer("", "");
                        }
                        if (userStatus.hasCarOwner() || userStatus.getCarOwner().getNum() > 0) {
                            owner.tv_num.setVisibility(View.VISIBLE);
                            UuCommon.TipsMsg ownerTip = userStatus.getCarOwner();
                            if (ownerTip.getType().equals(UuCommon.PromptType.RedPoint)) {
                                owner.tv_num.setWidth(Config.dip2px(context, 11));
                                owner.tv_num.setHeight(Config.dip2px(context, 11));
                                owner.tv_num.setText("");
                            } else {
//                                owner.tv_num.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                                owner.tv_num.setWidth(Config.dip2px(context, 20));
                                owner.tv_num.setHeight(Config.dip2px(context, 20));
                                owner.tv_num.setText(ownerTip.getNum() + "");
                            }
                            owner.needRefush = true;
                        } else {
                            owner.needRefush = false;
                            owner.tv_num.setVisibility(View.GONE);
                        }

                        if (userStatus.hasMy() || userStatus.getMy().getNum() > 0) {
                            my.tv_num.setVisibility(View.VISIBLE);
                            UuCommon.TipsMsg myTip = userStatus.getMy();
                            if (myTip.getType().equals(UuCommon.PromptType.RedPoint)) {
                                my.tv_num.setWidth(Config.dip2px(context, 11));
                                my.tv_num.setHeight(Config.dip2px(context, 11));
                                my.tv_num.setText("");
                            } else {
                                my.tv_num.setWidth(Config.dip2px(context, 20));
                                my.tv_num.setHeight(Config.dip2px(context, 20));
                                my.tv_num.setText(myTip.getNum() + "");
                            }
                            my.needRefush = true;
                        } else {
                            my.needRefush = false;
                            my.tv_num.setVisibility(View.GONE);
                        }

                        if (userStatus.hasQuickRent() || userStatus.getQuickRent().getNum() > 0) {
                            speed.tv_num.setVisibility(View.VISIBLE);
                            UuCommon.TipsMsg speedTip = userStatus.getQuickRent();
                            if (speedTip.getType().equals(UuCommon.PromptType.RedPoint)) {
                                speed.tv_num.setWidth(Config.dip2px(context, 11));
                                speed.tv_num.setHeight(Config.dip2px(context, 11));
                                speed.tv_num.setText("");
                            } else {
//                                speed.tv_num.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                                speed.tv_num.setWidth(Config.dip2px(context, 20));
                                speed.tv_num.setHeight(Config.dip2px(context, 20));
                                speed.tv_num.setText(speedTip.getNum() + "");

                            }
                            speed.needRefush = true;
                        } else {
                            speed.needRefush = false;
                            speed.tv_num.setText("");
                            speed.tv_num.setVisibility(View.GONE);
                        }

                        Config.isSppedIng = false;
                        Config.isOneToOneIng = false;
                        Config.isUserCancel = false;
                        Config.speedHasAgree = false;
//                        stopService(new Intent(context, RentingService.class));
                        getApp().quitRenting();
                        if (userStatus.getHasPreOrdering()) {
                            switch (userStatus.getPreOrderType()) {
                                case 1:
                                    Config.isSppedIng = true;
                                    Config.isUserCancel = false;
//                                    startService(new Intent(context, RentingService.class));
                                    getApp().startRenting();
                                    break;
                                case 2:
                                    Config.isOneToOneIng = true;
                                    break;
                                case 3:
                                    Config.isSppedIng = true;
                                    Config.speedHasAgree = true;
                                    Config.isUserCancel = false;
//                                    startService(new Intent(context, RentingService.class));

                                    getApp().startRenting();
                                    break;
                            }
                        }
                        if (userStatus.getWaitPayOrderIdCount() > 0) {
                            Config.hasPayOrder = true;
                            Config.waitPayOrderId = userStatus.getWaitPayOrderId(0);
                        } else {
                            Config.hasPayOrder = false;
                        }
                        endAnimation();
                        speed.root.setBackgroundResource(R.drawable.main_tab_speed_bg);
                        if (Config.isSppedIng || Config.isOneToOneIng) {
                            speed.name.setText("约车中...");
                            //如果有数字显示,就不显示动画
                            if (!speed.tv_num.getText().toString().equals("")) {

                            } else {
                                startAnimation();
                            }
                        } else if (Config.hasPayOrder) {
                            speed.name.setText("约车成功");
                            speed.root.setBackgroundResource(R.drawable.main_tab_speed_success_bg);
                        } else {
                            speed.name.setText("我要约车");
                        }
                        //租客身份变更
                        if (!Config.getUser(context).userStatus.equals(userStatus.getUserStatus() + "")) {
                            getModel();
                            model.userStatus = userStatus.getUserStatus() + "";
                            saveModel();
                            String msg = "";
                            if (Config.outApp(context)) {
                                switch (userStatus.getUserStatus()) {
                                    case 3:
                                        msg = "您的租客身份已认证成功，点击开始找车。";
                                        SocketCommunication.showNotification(getString(R.string.app_name), msg, MainActivityTab.GOTO_RENTER_FIND_CAR);
                                        break;
                                    case 4:
                                        msg = "您的租客身份认证失败，点击查看详情。";
                                        SocketCommunication.showNotification(getString(R.string.app_name), msg, MainActivityTab.GOTO_RENTER_VERIFY_ERROR);
                                        break;
                                }
                            }
                        }
                        //车主身份变更
                        if (!Config.getUser(context).carStatus.equals(userStatus.getCarStatus() + "")) {
                            getModel();
                            model.carStatus = userStatus.getCarStatus() + "";
                            saveModel();
                            if (Config.outApp(context)) {
                                String msg = "";
                                switch (userStatus.getCarStatus()) {
                                    case 3:
                                        msg = "您的爱车已通过审核，可以出租赚钱啦。";
                                        break;
                                    case 4:
                                        msg = "您的爱车资料审核失败，点击查看详情。";
                                        break;
                                }
                                SocketCommunication.showNotification(getString(R.string.app_name), msg, MainActivityTab.GOTO_OWNER_CAR_MANAGER);
                            }
                        }

                        couponNum = userStatus.getCouponCount();
                        ObserverManager.getObserver(ObserverManager.COUPONNUM).observer("", "");
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(VolleyError errorResponse) {

            }

            @Override
            public void networkFinish() {

            }
        });
    }

    @Override
    public boolean
    onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            setMenu(currentUri, menu);
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(true);
            }
        }
        return super.onPrepareOptionsMenu(menu);

    }

    public void setMenu(Uri uri, Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            menu.removeItem(i);
        }
        menu.clear();
        if (uri.equals(FindCarListFragment.URI)) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.find_car_list, menu);

        } else if (uri.equals(My.URI)) {
            if (Config.getUser(context).sid.length() > 0) {
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.my, menu);
            } else {
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//                MenuInflater inflater = getMenuInflater();
//                inflater.inflate(R.menu.my, menu);
            }
        } else if (uri.equals(FindCarMapFragment.URI)) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.find_car_map, menu);
        } else if (uri.equals(MyStrokeFragment.URI)) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.my_stroke_menu, menu);
        } else if (uri.equals(CarManagerFragment.URI)) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.owner_menu, menu);
        } else {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                ObserverManager.getObserver(ObserverManager.MYLOGOUT).observer("", "");
                break;
            case R.id.history:
                if (!Config.isNetworkConnected(this)) {
                    Config.showFiledToast(context);
                }

                startActivity(new Intent(context, OrderHistoryActivity.class));
//                if (Config.isGuest(context)) {
//                    Intent intent = new Intent(context, RegisterPhone.class);
//                    intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
//                    startActivity(intent);
//                } else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_NEW)) {
//                    Intent intent = new Intent(context, RenterRegisterIDActivity.class);
//                    intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
//                    startActivity(intent);
//                } else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_ALL)) {
//                    Intent intent = new Intent(context, RenterRegisterVerify.class);
//                    startActivity(intent);
//                } else if (Config.getUser(context).userStatus.equals(User.USER_STATUS_ZUKE_NO)) {
//                    Intent intent = new Intent(context, RenterRegisterVerifyError.class);
//                    intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
//                    startActivity(intent);
//                } else {
//                }
                break;
            case R.id.filter:
                Intent intent2 = new Intent(context, RentFilterActivity.class);
                intent2.putExtra("type", "normalSearch");

                startActivity(intent2);
                break;
            case R.id.map:
                findcar_Mode = FINDCAR_MODE_MAP;
                updateContent(FindCarMapFragment.URI);
                invalidateOptionsMenu();
                break;
            case R.id.list:
                findcar_Mode = FINDCAR_MODE_LIST;
                updateContent(FindCarListFragment.URI);
                invalidateOptionsMenu();
                break;
//            case R.id.kefu:
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("拨打客服电话");
//                builder.setMessage(Config.kefuphone);
//                builder.setNegativeButton("拨打", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String inputStr = Config.kefuphone;
//                        if (inputStr.trim().length() != 0) {
//                            MobclickAgent.onEvent(context, "ContactService");
//                            Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + inputStr));
//                            startActivity(phoneIntent);
//                        }
//                    }
//                });
//                builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//                builder.create().show();
//                break;
            case R.id.owner_help:
                Intent intent = new Intent();
//                intent.putExtra("url", ServerMutualConfig.owner_help);
                intent.setClass(context, OwnerHelpManager.class);
//                intent.putExtra("title", "车主帮助");
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public View.OnClickListener tabClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            findCar.setSelected(false);
            order.setSelected(false);
            owner.setSelected(false);
            my.setSelected(false);
            switch (v.getId()) {
                case R.id.findcar:
                    if (findcar_Mode == FINDCAR_MODE_LIST) {
                        updateContent(FindCarListFragment.URI);
                    } else {
                        updateContent(FindCarMapFragment.URI);
                    }
                    findCar.setSelected(true);
                    break;
                case R.id.order:
                    Config.missDontRefresh = false;
                    Config.showPoint = false;
                    updateContent(MyStrokeFragment.URI);
                    order.setSelected(true);
                    break;
                case R.id.owner:
                    updateContent(CarManagerFragment.URI);
                    owner.setSelected(true);
                    break;
                case R.id.my:
                    updateContent(My.URI);
                    my.setSelected(true);
                    break;

            }
            invalidateOptionsMenu();

        }
    };


    Uri currentUri = FindCarListFragment.URI;
    Fragment currentFragment;
    private String currentContentFragmentTag = null;

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void updateContent(Uri uri) {
        final Fragment fragment;
        final String tag;
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction tr = fm.beginTransaction();
        if (!currentUri.equals(uri)) {
            final Fragment currentFragment = fm.findFragmentByTag(currentContentFragmentTag);
            if (currentFragment != null) {
                currentFragment.onPause();
                tr.hide(currentFragment);
            }
        } else {
            if (MyStrokeFragment.URI.equals(uri) && order.needRefush) {
                ObserverManager.getObserver(ObserverManager.MYSTROKEFRAGMENT).observer("", "");
            } else if (CarManagerFragment.URI.equals(uri) && owner.needRefush) {
                ObserverManager.getObserver(ObserverManager.CARMANAGERFRAGMENT).observer("", "");
            }
        }
        if (FindCarMapFragment.URI.equals(uri)) {
            tag = FindCarMapFragment.TAG;
            final Fragment foundFragment = fm.findFragmentByTag(tag);
            if (foundFragment != null) {
                fragment = foundFragment;
                fragment.onResume();
            } else {
                fragment = new FindCarMapFragment();
            }
        } else if (My.URI.equals(uri)) {
            tag = My.TAG;
            final Fragment foundFragment = fm.findFragmentByTag(tag);
            if (foundFragment != null) {
                fragment = foundFragment;
                fragment.onResume();
            } else {
                fragment = new My();
            }
        } else if (FindCarListFragment.URI.equals(uri)) {
            tag = FindCarListFragment.TAG;
            final Fragment foundFragment = fm.findFragmentByTag(tag);
            if (foundFragment != null) {
                fragment = foundFragment;
                fragment.onResume();
            } else {
                fragment = new FindCarListFragment();
            }
        } else if (MyStrokeFragment.URI.equals(uri)) {
            tag = MyStrokeFragment.TAG;
            final Fragment foundFragment = fm.findFragmentByTag(tag);
            if (foundFragment != null) {
                fragment = foundFragment;
                fragment.onResume();
            } else {
                fragment = new MyStrokeFragment();
            }
        } else if (CarManagerFragment.URI.equals(uri)) {
            tag = CarManagerFragment.TAG;
            final Fragment foundFragment = fm.findFragmentByTag(tag);
            if (foundFragment != null) {
                fragment = foundFragment;
                fragment.onResume();
            } else {
                fragment = new CarManagerFragment();
            }
        } else {
            return;
        }
        if (fragment.isAdded()) {
            tr.show(fragment);
        } else {
            tr.add(R.id.content_frame, fragment, tag);
        }
        currentFragment = fragment;
        MLog.e(tag, "Update fragment = " + fragment + "  tag=" + tag);
        tr.commit();
        currentUri = uri;
        currentContentFragmentTag = tag;
        setActionTitle(uri);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return Config.onKeyDown(keyCode, event, this);
    }

    public void setActionTitle(Uri uri) {
        if (uri.equals(FindCarListFragment.URI) || uri.equals(FindCarMapFragment.URI)) {
            setTitle("");
        } else if (uri.equals(MyStrokeFragment.URI)) {
            setTitle("行程");
        } else if (uri.equals(CarManagerFragment.URI)) {
            setTitle("车主");
        } else if (uri.equals(My.URI)) {
            setTitle("我的");
        }

    }

    public class TabModel {
        public RelativeLayout root;
        public String tag = "";
        public TextView tv_num;
        public boolean needRefush = true;
        public boolean currentRefush = true;
        public boolean cancelRefush = true;
        public boolean finishRefush = true;
        public boolean isSelect = false;
        public TextView name;

        public void setSelected(boolean select) {
            isSelect = select;
            root.setSelected(isSelect);
        }
    }

    UserModel model;
    UserService service;

    public UserModel getModel() {

        service = new UserService(context);
        List<UserModel> models = service.getModelList(UserModel.class);
        if (models.size() == 0) {
            model = new UserModel();
            model.phone = "";
            boolean flag = service.insModel(model);
            if (flag) {
                model = service.getModel(UserModel.class, new String[]{""});
            }
        } else {
            model = models.get(0);
        }
        return model;
    }

    private boolean saveModel() {
        boolean flag = service.modifyModel(model);
        return flag;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MLog.e(tag, "maintab__onActivityResult:requestCode" + requestCode + "__resultCode:" + resultCode);
    }
}