package com.youyou.uucar.UI.Main.rent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
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
import android.widget.RatingBar;
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
import com.uu.client.bean.user.UserInterface;
import com.uu.client.bean.user.common.UserCommon;
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
import butterknife.OnClick;

public class OneToOneWaitActivity extends BaseActivity implements ObserverListener
{
    public String tag = "OneZuCheList";
    public Activity context;
    @InjectView(R.id.progress)
    UUProgressFramelayout mProgress;
    private ListView mListView;
    ArrayList<OrderFormInterface26.RenterQueryRentList.PreOrderCard> data = new ArrayList<OrderFormInterface26.RenterQueryRentList.PreOrderCard>();
    //    TextView            cancel;
    TextView            times;
    AbCircleProgressBar mAbProgressBar;
    long                start_time_long, end_time_long;
    long TIME_COUNT = 1000 * 60 * 20;

    //是否有正在进行的意向单
    public static boolean isSppedIng     = false;
    //是否正在一对一或者一对多约车
    public static boolean isOneToOneIng  = false;
    //快速约车并且已经有人同意了
    public static boolean speedHasAgree  = false;
    /**
     * 是否有待支付订单
     */
    public static boolean hasPayOrder    = false;
    /**
     * 待支付订单号
     */
    public        String  waitPayOrderId = "";

    public void onPause()
    {
        super.onPause();
        MobclickAgent.onPause(context);
        ObserverManager.removeObserver(ObserverManager.RENTERORDERFAILURE);
        ObserverManager.removeObserver(ObserverManager.RENTERTOPAY);
    }

    long passedTime;

    @OnClick(R.id.num_root)
    public void SpeedClick()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("您已发出预约请求,正在等待车主回应.若进行新的预约将中断之前的请求,是否继续预约附近的车?");
        builder.setNegativeButton("返回", null);
        builder.setNeutralButton("一键约车", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                showProgress(false);
                cancelHire("", true, "");
            }
        });
        builder.create().show();

    }

    @InjectView(R.id.num)
    TextView num;

    public void initNoteDataRefush()
    {
        TextView noDataRefush;
        noDataRefush = (TextView) mProgress.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mProgress.noDataReloading();
                getData();
            }
        });
    }

    // #FF514E
    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        context = this;
        setContentView(R.layout.onetoone_wait_activity);
        ButterKnife.inject(this);
        initNoteDataRefush();
        num.setText(Html.fromHtml("<u>等着急了,试试一键约车</u>"));
        mListView = (ListView) findViewById(R.id.list);
        adapter = new MyAdapter();
        mListView.setAdapter(adapter);
        ObserverManager.addObserver(ObserverManager.RENTERTOPAY, this);
        ObserverManager.addObserver(ObserverManager.RENTERORDERFAILURE, this);
//        Config.getCoordinates(context, new LocationListener() {
//            @Override
//            public void locationSuccess(final double lat, final double lng, String addr) {
//                OneToOneWaitActivity.this.lat = lat;
//                OneToOneWaitActivity.this.lng = lng;
//
//
//            }
//        });

        if (task == null)
        {
            task = new Task();
        }
        if (timer == null)
        {
            timer = new Timer();
        }
        timer.schedule(task, 1 * 1000, 1000 * 20);
        times = (TextView) findViewById(R.id.time);
//        TIME_COUNT = getIntent().getLongExtra("maxtime", 0);
//        passedTime = getIntent().getLongExtra("passedTime", 0);
//        start_time_long = System.currentTimeMillis() - passedTime;
//        end_time_long = start_time_long + TIME_COUNT;
//        MLog.e(tag, "maxtime = " + TIME_COUNT + "   passed = " + getIntent().getLongExtra("passedTime", 0));
        mAbProgressBar = (AbCircleProgressBar) findViewById(R.id.circleProgressBar);
//        time = new TimeCount((TIME_COUNT - passedTime), 50);// 构造CountDownTimer对象
//        time.start();
        getData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nocar_wait_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.cancel)
        {
            Dialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("您的预约请求已发送给多位车主,您真的要取消本次预约吗?");
            builder.setNegativeButton("对,取消预约", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    cancelHire("", false, "");
                }
            });
            builder.setCancelable(false);
            builder.setNeutralButton("继续预约", null);
            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        else if (item.getItemId() == android.R.id.home || item.getItemId() == 0)
        {
            onBackPressed();
            return false;
        }
        return true;
    }


    /**
     * 取消意向单,如果preOrderId = "" 则是全部取消,如果有数字,就取消单独的意向单
     * 如果取消后进入快速约车,传入isSpeed = true;
     *
     * @param preOrderId
     */
    public void cancelHire(final String preOrderId, final boolean isSpeed, final String carId)
    {

        showProgress(true, new Config.ProgressCancelListener()
        {
            @Override
            public void progressCancel()
            {
                NetworkUtils.cancleNetworkRequest("RenterCancelPreOrder");
            }
        });
        OrderFormInterface26.RenterCancelPreOrder.Request.Builder request = OrderFormInterface26.RenterCancelPreOrder.Request.newBuilder();
        if (!preOrderId.equals(""))
        {
            request.setPreOrderId(preOrderId);
        }
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.RenterCancelPreOrder_VALUE);
        task.setBusiData(request.build().toByteArray());
        task.setTag("RenterCancelPreOrder");
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override
            public void onSuccessResponse(UUResponseData responseData)
            {
                if (responseData.getRet() == 0)
                {
                    try
                    {
                        OrderFormInterface26.RenterCancelPreOrder.Response response = OrderFormInterface26.RenterCancelPreOrder.Response.parseFrom(responseData.getBusiData());
                        MLog.e(tag, "renter Cancel ret = " + response.getRet());
                        if (response.getRet() == 0)
                        {
                            ShowToast("取消成功");
                            if (!carId.equals(""))
                            {
                                ObserverManager.getObserver(carId).observer("", "");
                            }
                            if (preOrderId.equals(""))//如果取消全部预约,结束该页面
                            {
                                if (isSpeed)
                                {
                                    startActivity(new Intent(context, SpeedRentCarActivity.class));
                                }
                                Config.isOneToOneIng = false;
                                setResult(RESULT_OK);
                                finish();
                            }
                            else//如果只是取消单个的,刷新列表
                            {
                                if (cancelId != -1)
                                {
                                    data.remove(cancelId);
                                    cancelId = -1;
                                    if (data.size() == 0)//删除完了之后,没数据了
                                    {
                                        Config.isOneToOneIng = false;
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                    else
                                    {
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                        else
                        {
                            showToast("取消失败");
                            cancelId = -1;
                        }
                    }
                    catch (InvalidProtocolBufferException e)
                    {
                        e.printStackTrace();
                    }


                }

            }

            @Override
            public void onError(VolleyError errorResponse)
            {
                Config.showFiledToast(context);
            }

            @Override
            public void networkFinish()
            {
                dismissProgress();
            }
        });
//        Config.showProgressDialog(context, true, null);
//        ObserverManager.removeObserver("wait");
//        AbRequestParams params = new AbRequestParams();
//        params.put("sid", Config.getUser(context).sid);
//        SharedPreferences sp = context.getSharedPreferences("hire", Context.MODE_PRIVATE);
//        params.put("r_sn", sp.getString("r_sn", ""));
//        AbHttpUtil fh = AbHttpUtil.getInstance(context);
//        fh.post(ServerMutualConfig.cancelreservation, params, new MyAbStringHttpResponseListener(ServerMutualConfig.cancelreservation, params, context) {
//            @Override
//            public void onSuccess(int statusCode, String content) {
//                super.onSuccess(statusCode, content);
//
//                try {
//                    if (isCancelTip) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setMessage("抱歉，由于您20分钟没有选择合适的车辆租用，系统已自动取消您的预约。");
//                        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                setResult(RESULT_OK);
//                                finish();
//                            }
//                        });
//                        builder.create().show();
//                    } else {
//                        JSONObject json = new JSONObject(content);
//                        setResult(RESULT_OK);
//                        finish();
//                        Toast.makeText(context, json.getString("msg"), Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                super.onFinish();
//                MLog.e(tag, "dis 4");
//                Config.dismissProgress();
//            }
//
//            @Override
//            public void onFailure(int statusCode, String content, Throwable error) {
//                super.onFailure(statusCode, content, error);
//                Config.showFiledToast(context);
//            }
//        });
    }

    double lat, lng;
    public MyAdapter adapter;
    public Timer     timer;
    Task task;

    @Override
    public boolean
    onPrepareOptionsMenu(Menu menu)
    {

        for (int i = 0; i < menu.size(); i++)
        {
            menu.removeItem(i);
        }
        menu.clear();
        if (data.size() > 1)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.nocar_wait_menu, menu);
        }
        return super.onPrepareOptionsMenu(menu);

    }

    public void onResume()
    {
        super.onResume();
        MobclickAgent.onResume(context);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (timer != null)
        {
            timer.cancel();
        }
        if (task != null)
        {
            task.cancel();
        }
        if (time != null)
        {
            time.cancel();
        }
    }


    public void getData()
    {
        Config.getCoordinates(this, new LocationListener()
        {
            @Override
            public void locationSuccess(double lat, double lng, String addr)
            {
                UuCommon.LatlngPosition.Builder position = UuCommon.LatlngPosition.newBuilder();
                position.setLat(lat);
                position.setLng(lng);
                OrderFormInterface26.RenterQueryRentList.Request.Builder request = OrderFormInterface26.RenterQueryRentList.Request.newBuilder();
                request.setPosition(position);
                NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.RenterQueryRentList_VALUE);
                networkTask.setTag("RenterQueryRentList");
                networkTask.setBusiData(request.build().toByteArray());
                NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
                {
                    @Override
                    public void onSuccessResponse(UUResponseData responseData)
                    {
                        MLog.e(tag, "resopnseData ret = " + responseData.getRet());
                        if (responseData.getRet() == 0)
                        {
                            try
                            {
                                OrderFormInterface26.RenterQueryRentList.Response response = OrderFormInterface26.RenterQueryRentList.Response.parseFrom(responseData.getBusiData());
                                MLog.e(tag, "response ret = " + response.getRet());
                                if (response.getRet() == 0)
                                {
                                    data.clear();
                                    data.addAll(response.getCardListList());
                                    invalidateOptionsMenu();//如果多辆车,显示取消全部预约,如果是一辆车隐藏
                                    TIME_COUNT = response.getTotalWaitTime() * 1000;
                                    passedTime = response.getPassedTime() * 1000;
                                    start_time_long = System.currentTimeMillis() - passedTime;
                                    end_time_long = start_time_long + TIME_COUNT;
                                    if (time != null)
                                    {
                                        time.cancel();
                                        time = null;
                                    }
                                    time = new TimeCount((TIME_COUNT - passedTime), 50);// 构造CountDownTimer对象
                                    time.start();
                                    if (data.isEmpty())
                                    {
                                        Dialog dialog;
                                        AlertDialog.Builder builder = new AlertDialog.Builder(OneToOneWaitActivity.this);
                                        builder.setMessage("车主没有接受约车请求，可重新试试一键约车");
                                        builder.setNegativeButton("先不约了", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                Intent intent = new Intent(OneToOneWaitActivity.this, MainActivityTab.class);
                                                intent.putExtra("goto", MainActivityTab.GOTO_RENTER_FIND_CAR);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                startActivity(intent);

                                            }
                                        });
                                        builder.setNeutralButton("一键约车", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                setResult(RESULT_OK);
                                                finish();
                                                startActivity(new Intent(context, SpeedRentCarActivity.class));
                                            }
                                        });
                                        dialog = builder.create();
                                        dialog.setCancelable(false);
                                        dialog.setCanceledOnTouchOutside(false);
                                        dialog.show();
                                    }
                                    else
                                    {
                                        adapter.notifyDataSetChanged();
                                    }
                                    mProgress.makeProgreeDismiss();
                                }
                                else
                                {

                                    mProgress.makeProgreeNoData();
                                }
                            }
                            catch (InvalidProtocolBufferException e)
                            {
                                e.printStackTrace();
                                mProgress.makeProgreeNoData();
                            }
                        }
                        else
                        {

                            mProgress.makeProgreeNoData();
                        }
                    }

                    @Override
                    public void onError(VolleyError errorResponse)
                    {
                        mProgress.makeProgreeNoData();
                        Config.showFiledToast(context);
                    }

                    @Override
                    public void networkFinish()
                    {

                    }
                });
            }
        });


    }

    @Override
    public void observer(String from, Object obj)
    {
        if (timer != null)
        {
            timer.cancel();
        }
        if (task != null)
        {
            task.cancel();
        }
   /*     if (from != null && from.equals("agree")) {
//            if (ObserverManager.dialogBool.get() == false) {
//                ObserverManager.dialogBool.set(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(Config.currentContext);
                final OrderFormInterface26.NewOrderCreatedPush push = (OrderFormInterface26.NewOrderCreatedPush) obj;
                UuCommon.TipsMsg msg = push.getMsg();
                builder.setMessage(msg.getToastMsg());
                builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Config.currentContext.getClass().getName().equals(OneToOneWaitActivity.class.getName()))//如果当前在一对一约车页面去行程页,如果不是,就不动
                        {
                            MainActivityTab.instance.gotoStroke();
                            Config.currentContext.finish();
//                            ObserverManager.dialogBool.set(false);
                        }
                    }
                });
                builder.setNeutralButton("查看并支付", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(OneToOneWaitActivity.this, RenterOrderInfoActivity.class);
                        intent.putExtra(SysConfig.R_SN, push.getOrderId());
                        OneToOneWaitActivity.this.startActivity(intent);
                        OneToOneWaitActivity.this.finish();
//                        ObserverManager.dialogBool.set(false);
                    }
                });
                builder.create().show();
//            }
        } else*/
        if (from != null && from.equals("refuse"))
        {
            getData();
        }
    }

    public class Task extends TimerTask
    {
        @Override
        public void run()
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    UserInterface.StartQueryInterface.Request.Builder builder = UserInterface.StartQueryInterface.Request.newBuilder();
                    NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.StartQueryInterface_VALUE);
                    task.setBusiData(builder.build().toByteArray());
                    NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>()
                    {
                        @Override
                        public void onSuccessResponse(UUResponseData responseData)
                        {
                            if (responseData.getRet() == 0)
                            {
                                try
                                {
                                    final UserInterface.StartQueryInterface.Response response = UserInterface.StartQueryInterface.Response.parseFrom(responseData.getBusiData());

                                    UserCommon.UserStatus userStatus = response.getUserStatus();
                                    if (userStatus.getHasPreOrdering())
                                    {
                                        switch (userStatus.getPreOrderType())
                                        {
                                            case 1:
                                                isSppedIng = true;
                                                break;
                                            case 2:
                                                isOneToOneIng = true;
                                                break;

                                        }
                                    }
                                    else
                                    {
                                        isSppedIng = false;
                                        isOneToOneIng = false;
                                    }

                                    if (!isOneToOneIng)
                                    {

                                        if (userStatus.getWaitPayOrderIdCount() > 0)
                                        {
                                            hasPayOrder = true;
                                            waitPayOrderId = userStatus.getWaitPayOrderId(0);
                                        }
                                        else
                                        {
                                            hasPayOrder = false;
                                        }

                                        if (hasPayOrder)
                                        {


                                        }

                                    }


                                }
                                catch (InvalidProtocolBufferException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onError(VolleyError errorResponse)
                        {

                        }

                        @Override
                        public void networkFinish()
                        {

                        }
                    });

                }
            });


        }
    }


    Handler handler  = new Handler();
    int     cancelId = -1;

    public class MyAdapter extends BaseAdapter
    {
        private LayoutInflater mInflater;

        MyAdapter()
        {
            super();
            mInflater = getLayoutInflater();
        }

        @Override
        public int getCount()
        {
            return data.size();
        }

        @Override
        public Object getItem(int position)
        {
            return data.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            convertView = mInflater.inflate(R.layout.find_car_list_item, null);
            final OrderFormInterface26.RenterQueryRentList.PreOrderCard item = data.get(position);
            final CarCommon.CarBriefInfo carInfo = item.getCarBriefInfo();
            RelativeLayout titleRoot = (RelativeLayout) convertView.findViewById(R.id.title_root);
            titleRoot.setVisibility(View.VISIBLE);
            TextView tip = (TextView) convertView.findViewById(R.id.tip);
            tip.setText("等待" + item.getCarOwnerName() + "同意您的租车请求");
            BaseNetworkImageView img = (BaseNetworkImageView) convertView.findViewById(R.id.car_img);
            UUAppCar.getInstance().display(carInfo.getThumbImg(), img, R.drawable.list_car_img_def);
            TextView brand = (TextView) convertView.findViewById(R.id.brand);
            brand.setText(carInfo.getBrand() + carInfo.getCarModel());
            TextView dis = (TextView) convertView.findViewById(R.id.dis);
            String juli = "";
            float djuli = -1;
            if (carInfo.hasDistanceFromRenter())
            {
                djuli = carInfo.getDistanceFromRenter();
            }
            if (djuli < 0)
            {
                djuli = 0;
            }
            else if (djuli < 0.1f)
            {
                djuli = 0.1f;
            }

//            if (djuli > 1) {
            juli = String.format("%.1f", djuli) + " km";
//            } else {
//                juli = ((int) (djuli * 1000)) + " m";
//            }
            if (djuli == 0)
            {
                dis.setVisibility(View.GONE);
            }
            else
            {
                dis.setVisibility(View.VISIBLE);

                dis.setText(juli);
            }

            TextView price_day = (TextView) convertView.findViewById(R.id.price_day);
            price_day.setText("￥" + ((int) carInfo.getPricePerDay()));
            TextView gearbox = (TextView) convertView.findViewById(R.id.gearbox_text);
            if (carInfo.getTransmissionType().equals(CarCommon.CarTransmissionType.AUTO))
            {
                gearbox.setText("自动挡");
            }
            else
            {
                gearbox.setText("手动挡");
            }
            TextView address = (TextView) convertView.findViewById(R.id.address_text);
            // xianxing.setText();
            address.setText(carInfo.getAddress());

            if (carInfo.hasColoredAddress())
            {
                if (carInfo.getColoredAddress().hasTextHexColor())
                {
                    address.setTextColor(Color.parseColor(carInfo.getColoredAddress().getTextHexColor()));
                }
                if (carInfo.getColoredAddress().hasText())
                {
                    address.setText(carInfo.getColoredAddress().getText());
                }

            }
            TextView banDay = (TextView) convertView.findViewById(R.id.banday);
            if (carInfo.hasCarLimitedInfo())
            {
                banDay.setVisibility(View.VISIBLE);
                banDay.setText(carInfo.getCarLimitedInfo());
            }
            else
            {
                banDay.setVisibility(View.INVISIBLE);
            }
//            RatingBar rating = (RatingBar) convertView.findViewById(R.id.rating);
//            if (carInfo.hasStars()) {
//                rating.setVisibility(View.VISIBLE);
//                rating.setRating(carInfo.getStars());
//            } else {
//                rating.setVisibility(View.GONE);
//            }


            LinearLayout bottomRoot = (LinearLayout) convertView.findViewById(R.id.bottom_root);
            bottomRoot.setVisibility(View.VISIBLE);
            convertView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(context, OldCarInfoActivity.class);
                    intent.putExtra("islist", false);
                    intent.putExtra(SysConfig.CAR_SN, carInfo.getCarId());
                    intent.putExtra(SysConfig.R_SN, item.getPreOrderId());
                    intent.putExtra("from", "onetoone");
                    intent.putExtra("index", position);
                    startActivityForResult(intent, 165);
                }
            });
            TextView cancel = (TextView) convertView.findViewById(R.id.rent);
            cancel.setText("取消该预约");
            TextView toOwner = (TextView) convertView.findViewById(R.id.toowner);
            TextView desc = (TextView) convertView.findViewById(R.id.desc);
            convertView.findViewById(R.id.v1).setVisibility(View.GONE);
            desc.setVisibility(View.GONE);
            cancel.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("您的约车请求已发送给车主,您真的要取消本次预约吗?");
                    builder.setNegativeButton("对,取消预约", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            cancelId = position;
                            cancelHire(item.getPreOrderId(), false, carInfo.getCarId());
                        }
                    });
                    builder.setNeutralButton("继续预约", null);
                    builder.create().show();
                }
            });
            View view = convertView.findViewById(R.id.center_view);
            if (item.getCarOwnerPhone().length() == 0)
            {
                toOwner.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
            }
            else
            {
                view.setVisibility(View.VISIBLE);
                toOwner.setVisibility(View.VISIBLE);
                toOwner.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        MLog.e(tag, "owner Phone = " + item.getCarOwnerPhone());
                        String inputStr = item.getCarOwnerPhone();
                        if (inputStr.trim().length() != 0)
                        {
                            Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + inputStr));
                            startActivity(phoneIntent);
                        }
                    }
                });
            }

            return convertView;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK)
        {
//            startActivity(intent);
            finish();
        }
    }

    private TimeCount time;
    boolean isCancelTip = false;

    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer
    {
        public TimeCount(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish()
        {// 计时完毕时触发
            times.setText("00:00");
            isCancelTip = true;
//            cancelHire();
            if (timer != null)
            {
                timer.cancel();
            }
            if (task != null)
            {
                task.cancel();
            }
            if (OneToOneWaitActivity.this != null)
            {
//                if (OneToOneWaitActivity.this.isTaskRoot())
                {
                    Dialog dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("车主没有接受约车请求，可重新试试一键约车");
                    builder.setNegativeButton("先不约了", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                    builder.setNeutralButton("一键约车", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            setResult(RESULT_OK);
                            finish();
                            startActivity(new Intent(context, SpeedRentCarActivity.class));
                        }
                    });
                    dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            }
        }

        @Override
        public void onTick(long millisUntilFinished)
        {// 计时过程显示
            long day = millisUntilFinished / 86400000; // 以天数为单位取整
            long hour = millisUntilFinished % 86400000 / 3600000; // 以小时为单位取整
            long min = millisUntilFinished % 86400000 % 3600000 / 60000; // 以分钟为单位取整
            long se = millisUntilFinished % 86400000 % 3600000 % 60000 / 1000; // 以秒为单位取整
            mAbProgressBar.setProgress((int) ((float) ((float) (System.currentTimeMillis() - start_time_long) / (float) (end_time_long - start_time_long)) * 100));
            String string = "";
            if (min < 10)
            {
                string = "0" + min;
            }
            else
            {
                string = "" + min;
            }
            if (se < 10)
            {
                string += ":0" + se;
            }
            else
            {
                string += ":" + se;
            }
            times.setText(string);
        }
    }
}
