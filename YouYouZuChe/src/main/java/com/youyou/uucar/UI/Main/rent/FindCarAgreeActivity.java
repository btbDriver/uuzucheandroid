package com.youyou.uucar.UI.Main.rent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.view.progress.AbCircleProgressBar;
import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.umeng.analytics.MobclickAgent;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.common.UuCommon;
import com.uu.client.bean.order.OrderFormInterface26;
import com.uu.client.bean.order.common.OrderFormCommon;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Orderform.RenterOrderInfoActivity;
import com.youyou.uucar.UI.Renter.carinfo.OldCarInfoActivity;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.ImageView.BaseNetworkImageView;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Network.listen.OnClickNetworkListener;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.LocationListener;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FindCarAgreeActivity extends BaseActivity {
    public String tag = "OneZuCheList";
    public Activity context;
    @InjectView(R.id.root)
    RelativeLayout mRoot;
    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;
    private ListView mListView;
    ArrayList<OrderFormCommon.QuickRentCarAgreeCard> data = new ArrayList<OrderFormCommon.QuickRentCarAgreeCard>();
    //    TextView            cancel;
    TextView times;
    AbCircleProgressBar mAbProgressBar;
    long start_time_long, end_time_long;
    long TIME_COUNT = 1000 * 60 * 10;

    public void initNoteDataRefush() {
        TextView noDataRefush;
        noDataRefush = (TextView) mAllFramelayout.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAllFramelayout.noDataReloading();
                loadData();
            }
        });
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(context);
    }

    TextView num;
    long passedTime;

    // #FF514E
    public void onCreate(Bundle b) {
        super.onCreate(b);
        context = this;
        setContentView(R.layout.find_car_agree_activity);
        ButterKnife.inject(this);
        setTitle("一键约车");
        initNoteDataRefush();
        num = (TextView) findViewById(R.id.num);
        // 已有X位车主响应
        String tip2s = "已有0位车主响应了您的租车请求\n请选择车辆";
        SpannableStringBuilder style = new SpannableStringBuilder(tip2s);
        style.setSpan(new AbsoluteSizeSpan(Config.dip2px(context, 27), true), tip2s.indexOf("有") + 1, tip2s.indexOf("位车"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#55acef")), tip2s.indexOf("有") + 1, tip2s.indexOf("位车"), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        num.setText(style);
        mListView = (ListView) findViewById(R.id.list);
        adapter = new MyAdapter();
        mListView.setAdapter(adapter);
        Config.getCoordinates(context, new LocationListener() {
            @Override
            public void locationSuccess(final double lat, final double lng, String addr) {
                FindCarAgreeActivity.this.lat = lat;
                FindCarAgreeActivity.this.lng = lng;
                if (task == null) {
                    task = new Task();
                }
                if (timer == null) {
                    timer = new Timer();
                }
                timer.schedule(task, 0, 1000 * 20);
            }
        });
        times = (TextView) findViewById(R.id.time);
        TIME_COUNT = getIntent().getLongExtra("maxtime", 0);
        passedTime = getIntent().getLongExtra("passedTime", 0);
        start_time_long = System.currentTimeMillis() - passedTime;
        end_time_long = start_time_long + TIME_COUNT;
        MLog.e(tag, "maxtime = " + TIME_COUNT + "   passed = " + getIntent().getLongExtra("passedTime", 0));
        mAbProgressBar = (AbCircleProgressBar) findViewById(R.id.circleProgressBar);
        time = new TimeCount((TIME_COUNT - passedTime), 50);// 构造CountDownTimer对象
        time.start();
        ObserverManager.addObserver(ObserverManager.QUICKRENTCARPUSH, new ObserverListener() {
            @Override
            public void observer(String from, Object obj) {
                loadData();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nocar_wait_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel) {
            Dialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("您的快速约车请求已发送给多位车主,您真的要取消预约吗?");
            builder.setNegativeButton("对,取消预约", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelHire();
                }
            });
            builder.setNeutralButton("继续预约", null);
            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        }
        return true;
    }

    public OnClickListener cancelClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("您的快速约车请求已发送给多位车主,您真的要取消预约吗?");
            builder.setNegativeButton("对,取消预约", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelHire();
                }
            });
            builder.setNeutralButton("继续预约", null);
            builder.create().show();
        }
    };

    public void cancelHire() {
        showProgress(true, new Config.ProgressCancelListener() {
            @Override
            public void progressCancel() {
                NetworkUtils.cancleNetworkRequest("RenterCancelPreOrder");
            }
        });
        OrderFormInterface26.RenterCancelPreOrder.Request.Builder request = OrderFormInterface26.RenterCancelPreOrder.Request.newBuilder();
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.RenterCancelPreOrder_VALUE);
        task.setBusiData(request.build().toByteArray());
        task.setTag("RenterCancelPreOrder");
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        OrderFormInterface26.RenterCancelPreOrder.Response response = OrderFormInterface26.RenterCancelPreOrder.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0) {
                            ShowToast("取消成功");
                            Config.isUserCancel = true;
                            Config.isSppedIng = false;
//                            context.stopService(new Intent(context, RentingService.class));

                            getApp().quitRenting();
                            finish();
                        } else {
                            showToast("取消失败");
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(VolleyError errorResponse) {
                Config.showFiledToast(context);
            }

            @Override
            public void networkFinish() {
                dismissProgress();
            }
        });
    }

    double lat, lng;
    public MyAdapter adapter;
    public Timer timer;
    Task task;

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(context);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (time != null) {
            time.cancel();
        }
        time = null;
        timer = null;
        if (task != null) {
            task.cancel();
        }
        task = null;

        ObserverManager.removeObserver(ObserverManager.QUICKRENTCARPUSH);
    }

    boolean isFirst = true;

    public void loadData() {
        OrderFormInterface26.RenterQueryQuickRentAgreeList.Request.Builder request = OrderFormInterface26.RenterQueryQuickRentAgreeList.Request.newBuilder();
        UuCommon.LatlngPosition.Builder position = UuCommon.LatlngPosition.newBuilder();
        position.setLat(Config.lat);
        position.setLng(Config.lng);
        request.setPosition(position);
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.RenterQueryQuickRentAgreeList_VALUE);
        networkTask.setTag("RenterQueryQuickRentAgreeList");
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        OrderFormInterface26.RenterQueryQuickRentAgreeList.Response response = OrderFormInterface26.RenterQueryQuickRentAgreeList.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0) {
                            data.clear();
                            data.addAll(response.getAgreeCarListList());
                            String tip2s = "已有" + data.size() + "位车主响应了您的租车请求请选择车辆";
                            SpannableStringBuilder style = new SpannableStringBuilder(tip2s);
                            style.setSpan(new AbsoluteSizeSpan(25, true), tip2s.indexOf("有") + 1, tip2s.indexOf("位车"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            style.setSpan(new ForegroundColorSpan(Color.parseColor("#55acef")), tip2s.indexOf("有") + 1, tip2s.indexOf("位车"), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                            TIME_COUNT = response.getTotalWaitTime() * 1000L;
                            MLog.e(tag, "getTotalWaitTime:" + response.getTotalWaitTime());
                            passedTime = response.getPassedTime() * 1000L;
                            MLog.e(tag, "getPassedTime:" + response.getPassedTime());
                            start_time_long = System.currentTimeMillis() - passedTime;
                            end_time_long = start_time_long + TIME_COUNT;
                            if (time != null) {
                                time.cancel();
                                time = null;
                            }
                            time = new TimeCount((TIME_COUNT - passedTime), 50);// 构造CountDownTimer对象
                            time.start();
                            num.setText(style);
                            adapter.notifyDataSetChanged();
                            if (isFirst) {
                                isFirst = false;
                                mAllFramelayout.makeProgreeDismiss();
                            }
                        } else {
                            mAllFramelayout.makeProgreeNoData();
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                        mAllFramelayout.makeProgreeNoData();
                    }
                } else {
                    mAllFramelayout.makeProgreeNoData();
                }
            }

            @Override
            public void onError(VolleyError errorResponse) {
                mAllFramelayout.makeProgreeNoData();
            }

            @Override
            public void networkFinish() {

            }
        });
    }

    public class Task extends TimerTask {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    loadData();


                }
            });


        }
    }


    Handler handler = new Handler();

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        MyAdapter() {
            super();
            mInflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.find_car_agree_list_item, null);
            final OrderFormCommon.QuickRentCarAgreeCard item = data.get(position);
            final CarCommon.CarBriefInfo carInfo = item.getCarBriefInfo();
            BaseNetworkImageView img = (BaseNetworkImageView) convertView.findViewById(R.id.car_img);
            UUAppCar.getInstance().display(carInfo.getThumbImg(), img, R.drawable.list_car_img_def);
            TextView brand = (TextView) convertView.findViewById(R.id.brand);
            brand.setText(carInfo.getBrand() + carInfo.getCarModel());
            TextView dis = (TextView) convertView.findViewById(R.id.dis);
            String juli = "";

            float djuli = -1;
            if (carInfo.hasDistanceFromRenter()) {
                djuli = carInfo.getDistanceFromRenter();
            }
            if (djuli < 0) {
                djuli = 0;
            } else if (djuli < 0.1f) {
                djuli = 0.1f;
            }

//            if (djuli > 1) {
            juli = String.format("%.1f", djuli) + " km";
//            } else {
//                juli = ((int) (djuli * 1000)) + " m";
//            }
            if (djuli == 0) {
                dis.setVisibility(View.GONE);
            } else {
                dis.setVisibility(View.VISIBLE);

                dis.setText(juli);
            }
            TextView price_day = (TextView) convertView.findViewById(R.id.price_day);
            price_day.setText(String.format("%.2f", (float) carInfo.getPricePerDay()) + "元");
            TextView gearbox = (TextView) convertView.findViewById(R.id.gearbox_text);

            ImageView gearbox_icon = (ImageView) convertView.findViewById(R.id.gearbox_icon);

            if (carInfo.getTransmissionType().equals(CarCommon.CarTransmissionType.AUTO)) {
                gearbox.setText("自动挡");
                gearbox_icon.setBackgroundResource(R.drawable.find_car_list_gearbox_a_icon);
            } else {
                gearbox.setText("手动挡");
                gearbox_icon.setBackgroundResource(R.drawable.find_car_list_gearbox_m_icon);
            }
            TextView address = (TextView) convertView.findViewById(R.id.address_text);
            address.setText(carInfo.getAddress());
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OldCarInfoActivity.class);
                    intent.putExtra("islist", false);
                    intent.putExtra(SysConfig.CAR_SN, carInfo.getCarId());
                    intent.putExtra(SysConfig.R_SN, item.getPreOrderId());
                    intent.putExtra("index", position);
                    intent.putExtra("speed", true);
                    startActivityForResult(intent, 165);

                }
            });
            LinearLayout rent_button = (LinearLayout) convertView.findViewById(R.id.rent_button);
            rent_button.setOnClickListener(new OnClickNetworkListener() {

                @Override
                public void onNetworkClick(View v) {
                    showProgress(false);
                    OrderFormInterface26.ConfirmPreOrder.Request.Builder request = OrderFormInterface26.ConfirmPreOrder.Request.newBuilder();
                    request.setPreOrderId(item.getPreOrderId());
                    request.setAgree(true);
                    NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.ConfirmPreOrder_VALUE);
                    networkTask.setTag("ConfirmPreOrder");
                    networkTask.setBusiData(request.build().toByteArray());
                    NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                        @Override
                        public void onSuccessResponse(UUResponseData responseData) {
                            if (responseData.getRet() == 0) {
                                try {


                                    OrderFormInterface26.ConfirmPreOrder.Response response = OrderFormInterface26.ConfirmPreOrder.Response.parseFrom(responseData.getBusiData());
                                    String msg = response.getMsg();
                                    if (response.getRet() == 0 || response.getRet() == 1) {
                                        Intent intent = new Intent(context, RenterOrderInfoActivity.class);
                                        intent.putExtra(SysConfig.R_SN, response.getOrderId());
                                        startActivity(intent);
//                                        stopService(new Intent(context, RentingService.class));

                                        getApp().quitRenting();
                                        setResult(RESULT_OK);
                                        finish();
                                    } else if (response.getRet() == -1) {
                                        Config.showFiledToast(context);
                                    } else if (response.getRet() == -2) {
                                        Dialog dialog;
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
//                                        builder1.setMessage("订单冲突");

                                        if (msg == null || msg.length() == 0 || msg.equals("null")) {
                                            msg = "订单冲突";
                                        }
                                        builder1.setMessage(msg);
                                        builder1.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        });
                                        dialog = builder1.create();
                                        dialog.show();
                                        dialog.setCanceledOnTouchOutside(false);
                                        dialog.setCancelable(false);
//                                        Config.showFiledToast(context);
                                    } else if (response.getRet() == -3)//这辆车被别人租了
                                    {
                                        if (data.size() > 1)//如果这时候有大于1辆车同意的时候
                                        {
                                            Dialog dialog;
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                            if (msg == null || msg.length() == 0 || msg.equals("null")) {
                                                msg = "这辆车被别人抢啦，选个别的吧";
                                            }
                                            builder1.setMessage(msg);
                                            builder1.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    data.remove(position);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                            dialog = builder1.create();
                                            dialog.show();
                                            dialog.setCanceledOnTouchOutside(false);
                                            dialog.setCancelable(false);
                                        } else//如果这时候只有一辆车同意的时候
                                        {
                                            //先取消约车请求
                                            showProgress(true, new Config.ProgressCancelListener() {
                                                @Override
                                                public void progressCancel() {
                                                    NetworkUtils.cancleNetworkRequest("RenterCancelPreOrder");
                                                }
                                            });
                                            OrderFormInterface26.RenterCancelPreOrder.Request.Builder request = OrderFormInterface26.RenterCancelPreOrder.Request.newBuilder();
                                            NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.RenterCancelPreOrder_VALUE);
                                            task.setBusiData(request.build().toByteArray());
                                            task.setTag("RenterCancelPreOrder");
                                            NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
                                                @Override
                                                public void onSuccessResponse(UUResponseData responseData) {
                                                    if (responseData.getRet() == 0) {
                                                        try {
                                                            OrderFormInterface26.RenterCancelPreOrder.Response response = OrderFormInterface26.RenterCancelPreOrder.Response.parseFrom(responseData.getBusiData());
                                                            if (response.getRet() == 0) {
                                                                //取消成功的时候弹窗
                                                                Dialog dialog;
                                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);


                                                                builder1.setMessage("这辆车被别人抢啦，可重新试试一键约车");
                                                                builder1.setNegativeButton("先不约了", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        Intent intent = new Intent(context, MainActivityTab.class);
                                                                        intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
                                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                                        startActivity(intent);
                                                                    }
                                                                });
                                                                builder1.setNeutralButton("一键约车", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        finish();
                                                                        startActivity(new Intent(context, SpeedRentCarActivity.class));
                                                                    }
                                                                });
                                                                dialog = builder1.create();
                                                                dialog.show();
                                                                dialog.setCanceledOnTouchOutside(false);
                                                                dialog.setCancelable(false);
                                                            } else//取消失败的话就无视
                                                            {
//                                                                showToast("取消失败");
                                                            }
                                                        } catch (InvalidProtocolBufferException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onError(VolleyError errorResponse) {
                                                    Config.showFiledToast(context);
                                                }

                                                @Override
                                                public void networkFinish() {
                                                    dismissProgress();
                                                }
                                            });
                                        }


                                    } else if (response.getRet() == -4) {
                                        Dialog dialog;
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                        builder1.setMessage("有未支付的订单,不能预约");
                                        builder1.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        });
                                        dialog = builder1.create();
                                        dialog.show();
                                        dialog.setCanceledOnTouchOutside(false);
                                        dialog.setCancelable(false);
                                    } else {

                                        Config.showFiledToast(context);
                                    }
                                } catch (InvalidProtocolBufferException e) {
                                    e.printStackTrace();
                                    Config.showFiledToast(context);
                                }

                            }
                        }

                        @Override
                        public void onError(VolleyError errorResponse) {
                            dismissProgress();
                            Config.showFiledToast(context);
                        }

                        @Override
                        public void networkFinish() {
                            dismissProgress();
                        }
                    });

                }
            });
            return convertView;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
//            startActivity(intent);
            setResult(RESULT_OK);
            finish();
        }
    }

    private TimeCount time;
    boolean isCancelTip = false;

    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            times.setText("00:00");
            isCancelTip = true;
//            cancelHire();
            Dialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("您没有及时选车,约车失效了，可重新试试一键约车");
            builder.setNegativeButton("先不约了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(context, MainActivityTab.class);
                    intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            });
            builder.setNeutralButton("一键约车", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setResult(RESULT_OK);
                    finish();
                    startActivity(new Intent(context, SpeedRentCarActivity.class));
                }
            });
            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);

            dialog.show();
            //倒计时结束了,就不在轮询了.
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            long day = millisUntilFinished / 86400000; // 以天数为单位取整
            long hour = millisUntilFinished % 86400000 / 3600000; // 以小时为单位取整
            long min = millisUntilFinished % 86400000 % 3600000 / 60000; // 以分钟为单位取整
            long se = millisUntilFinished % 86400000 % 3600000 % 60000 / 1000; // 以秒为单位取整
            mAbProgressBar.setProgress((int) ((float) ((float) (System.currentTimeMillis() - start_time_long) / (float) (end_time_long - start_time_long)) * 100));
            String string = "";
            if (min < 10) {
                string = "0" + min;
            } else {
                string = "" + min;
            }
            if (se < 10) {
                string += ":0" + se;
            } else {
                string += ":" + se;
            }
            times.setText(string);
        }
    }
}
