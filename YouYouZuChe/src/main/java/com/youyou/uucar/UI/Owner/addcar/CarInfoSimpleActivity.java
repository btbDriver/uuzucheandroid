package com.youyou.uucar.UI.Owner.addcar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.youyou.uucar.PB.TaskTool;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Common.Car.CarInfoAndLocationActivity;
import com.youyou.uucar.UI.Renter.carinfo.ImageFragment;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;
import com.youyou.uucar.Utils.View.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by taurusxi on 14-7-8.
 */
public class CarInfoSimpleActivity extends BaseActivity
{
    //    public String tag = CarInfoSimpleActivity.class.getSimpleName();
    Context                 context;
    CarCommon.CarDetailInfo carContentModel;
    ImageAdapter            adapter;
    CirclePageIndicator     mIndicator;
    private String sId, carSn;
    private List<String> imageList;
//    private TextView mPrice;

    // 实时位置，租期设置，交车地点，设置价格，车辆描述
    private RelativeLayout btn_realtime_loc, btn_rent_time_set, carLocation, carPrice, carDesc;

    private TextView tv_car_name, tv_car_price, jiaochedidian, shezhijiage, cheliangmiaoshu;

    @InjectView(R.id.car_share)
    RelativeLayout car_share;

//    private ToggleButton renterTb;

    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch       poiSearch;// POI搜索

    private void share()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.share_pop, null);
        LinearLayout weixin = (LinearLayout) view.findViewById(R.id.wx);
        LinearLayout friend = (LinearLayout) view.findViewById(R.id.friend);
        LinearLayout weibo = (LinearLayout) view.findViewById(R.id.wb);
        LinearLayout tx_weibo = (LinearLayout) view.findViewById(R.id.txwb);
        tx_weibo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mController.setShareContent(carContentModel.getShareWord());
                mController.setShareImage(new UMImage(context, R.drawable.get_friend_icon));
                mController.postShare(context, TENCENT_MEDIA, new SocializeListeners.SnsPostListener()
                {
                    @Override
                    public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2)
                    {
                        Toast.makeText(context, "分享完成", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStart()
                    {
                        Toast.makeText(context, "开始分享", Toast.LENGTH_SHORT).show();
                    }
                });
                shareDialog.dismiss();
            }
        });
        weixin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (Config.isAvilible(context, "com.tencent.mm"))
                {

                    WXWebpageObject webpage = new WXWebpageObject();
                    webpage.webpageUrl = carContentModel.getShareWord().substring(carContentModel.getShareWord().indexOf("http:"));
                    WXMediaMessage msg = new WXMediaMessage(webpage);
                    msg.title = "友友私家车共享平台";
                    msg.description = carContentModel.getShareWord().substring(0, carContentModel.getShareWord().indexOf("http:"));
                    msg.setThumbImage(BitmapFactory.decodeResource(getResources(), R.drawable.get_friend_icon));
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = String.valueOf(System.currentTimeMillis());
                    req.message = msg;
                    req.scene = SendMessageToWX.Req.WXSceneSession;
                    api.sendReq(req);
                }
                shareDialog.dismiss();
            }
        });
        friend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (Config.isAvilible(context, "com.tencent.mm"))
                {

                    WXWebpageObject webpage = new WXWebpageObject();
                    webpage.webpageUrl = carContentModel.getShareWord().substring(carContentModel.getShareWord().indexOf("http:"));
                    WXMediaMessage msg = new WXMediaMessage(webpage);
                    msg.title = carContentModel.getShareWord().substring(0, carContentModel.getShareWord().indexOf("http:"));
                    msg.description = carContentModel.getShareWord().substring(0, carContentModel.getShareWord().indexOf("http:"));
                    msg.setThumbImage(BitmapFactory.decodeResource(getResources(), R.drawable.get_friend_icon));
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = String.valueOf(System.currentTimeMillis());
                    req.message = msg;
                    req.scene = SendMessageToWX.Req.WXSceneTimeline;
                    api.sendReq(req);
                }

                shareDialog.dismiss();
            }
        });
        weibo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mController.setShareContent(carContentModel.getShareWord());
                mController.setShareImage(new UMImage(context, R.drawable.get_friend_icon));
                mController.postShare(context, mTestMedia, new SocializeListeners.SnsPostListener()
                {
                    @Override
                    public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2)
                    {
                        Toast.makeText(context, "分享完成", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStart()
                    {
                        Toast.makeText(context, "开始分享", Toast.LENGTH_SHORT).show();
                    }
                });
                shareDialog.dismiss();
            }
        });
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                shareDialog.dismiss();
            }
        });
        builder.setView(view);

        shareDialog = builder.create();
        shareDialog.show();
    }

    // sdk controller
    private UMSocialService mController = null;

    public IWXAPI api;

    public static final String      DESCRIPTOR    = "com.umeng.share";
    public final        String      APP_ID        = "wx9abfa08f7da32b30";
    private final       SHARE_MEDIA mTestMedia    = SHARE_MEDIA.SINA;
    private final       SHARE_MEDIA TENCENT_MEDIA = SHARE_MEDIA.TENCENT;
    public              String      tag           = "OldCarInfoActivity";

    Dialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_info_simple_activity);
        Config.setActivityState(this);
        context = this;
        sId = getIntent().getStringExtra(SysConfig.S_ID);
        carSn = getIntent().getStringExtra(SysConfig.CAR_SN);
        imageList = new ArrayList<String>();
        ButterKnife.inject(this);
        initNoteDataRefush();
        mController = UMServiceFactory.getUMSocialService(DESCRIPTOR, RequestType.SOCIAL);
        // 添加新浪和QQ空间的SSO授权支持
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

//        mPrice = (TextView) findViewById(R.id.price);
        btn_realtime_loc = (RelativeLayout) findViewById(R.id.btn_realtime_loc);
        btn_rent_time_set = (RelativeLayout) findViewById(R.id.btn_rent_time_set);
        carPrice = (RelativeLayout) findViewById(R.id.car_price);
        carDesc = (RelativeLayout) findViewById(R.id.car_desc);
        carLocation = (RelativeLayout) findViewById(R.id.car_location);

        tv_car_name = (TextView) findViewById(R.id.tv_car_name);
        tv_car_price = (TextView) findViewById(R.id.tv_car_price);

        jiaochedidian = (TextView) findViewById(R.id.jiaochedidian);
        shezhijiage = (TextView) findViewById(R.id.shezhijiage);
        cheliangmiaoshu = (TextView) findViewById(R.id.cheliangmiaoshu);

        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);

        car_share.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                share();
            }
        });

        carPrice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent();
                intent.putExtra(SysConfig.CAR_SN, carSn);
                intent.putExtra(SysConfig.S_ID, sId);
//                intent.putExtra("showSelfPrice",true);
                intent.setClass(context, PriceActivity.class);
                startActivity(intent);
            }
        });

        btn_realtime_loc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.putExtra(SysConfig.S_ID, sId);
                intent.putExtra(SysConfig.CAR_SN, carSn);
                intent.setClass(context, CarInfoAndLocationActivity.class);
                context.startActivity(intent);
            }
        });
//        renterTb = (ToggleButton) findViewById(R.id.auto_rent);
//        renterTb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                updataStatus();
//            }
//        });

    }

    public void initNoteDataRefush()
    {
        TextView noDataRefush;
        noDataRefush = (TextView) mAllFramelayout.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (Config.isNetworkConnected(context))
                {
                    mAllFramelayout.noDataReloading();
                    initData();
                }
                else
                {
                    mAllFramelayout.makeProgreeNoData();
                }
            }
        });
    }

    private void initViewPager()
    {
        adapter = new ImageAdapter(getSupportFragmentManager());
        adapter.setCount(imageList);
//        float picScale = 0.60f;
//        int width = (int) (DisplayUtil.screenWidthDip * DisplayUtil.density);
//        int height = (int) (width * picScale);
        ViewPager mViewpager = (ViewPager) findViewById(R.id.viewpager);
//        FrameLayout.LayoutParams viewPaLayoutParams = new FrameLayout.LayoutParams(width, height);
//        mViewpager.setLayoutParams(viewPaLayoutParams);
        mViewpager.setAdapter(adapter);
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewpager);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initData();
    }

    private boolean isRent;

    @InjectView(R.id.tv_rent_time_content)
    TextView tv_rent_time_content;

    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;

    private void initData()
    {
        final UUAppCar app = (UUAppCar) getApplication();
        TaskTool.getCarDetailInfo(carSn, "", app, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override public void onSuccessResponse(UUResponseData responseData)
            {
                try
                {
                    final CarInterface.GetCarDetailInfo.Response response = CarInterface.GetCarDetailInfo.Response.parseFrom(responseData.getBusiData());

                    if (response.getRet() != 0)
                    {
                        mAllFramelayout.noDataReloading();
                        initData();
                        return;
                    }
                    carContentModel = response.getCarDetailInfo();
                    tv_car_name.setText(response.getCarDetailInfo().getBrand() + response.getCarDetailInfo().getCarModel());
                    tv_car_price.setText("￥" + (int) response.getCarDetailInfo().getPriceByDay() + "");
                    shezhijiage.setText((int) response.getCarDetailInfo().getPriceByDay() + "元/天");
                    cheliangmiaoshu.setText(response.getCarDetailInfo().getCarDesc());
                    setTitle(response.getCarDetailInfo().getLicensePlate());

                    carDesc.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {

                            Intent intent = new Intent();
                            intent.putExtra(SysConfig.CAR_SN, carSn);
                            intent.putExtra(SysConfig.S_ID, sId);
                            intent.putExtra("car_desc",cheliangmiaoshu.getText().toString());
                            intent.setClass(context, AddCarDescActivity.class);
                            startActivity(intent);
                        }
                    });

                    jiaochedidian.setText(response.getCarDetailInfo().getAddress());

                    carLocation.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent();
                            intent.putExtra(SysConfig.CAR_SN, carSn);
                            intent.putExtra(SysConfig.S_ID, sId);
                            intent.putExtra("lat", response.getCarDetailInfo().getPosition().getLat());
                            intent.putExtra("lng", response.getCarDetailInfo().getPosition().getLng());
                            intent.setClass(context, CarLocationActivity.class);
                            startActivity(intent);
                        }
                    });

                    List<CarCommon.CarImg> imglist = response.getCarDetailInfo().getCarImgsList();
                    String pre = response.getCarDetailInfo().getCarImgUrlPrefix();
                    imageList.clear();
                    for (CarCommon.CarImg img : imglist)
                    {
                        String imgUrl = pre + img.getImgThumb();
                        imageList.add(imgUrl);
                    }
                    initViewPager();
                    TaskTool.queryLeaseTerm(carSn, app, new HttpResponse.NetWorkResponse<UUResponseData>()
                    {
                        @Override public void onSuccessResponse(UUResponseData responseData)
                        {
                            try
                            {
                                CarInterface.QueryLeaseTermResponse response1 = CarInterface.QueryLeaseTermResponse.parseFrom(responseData.getBusiData());

                                if (response1.getRet() == -1)
                                {
                                    mAllFramelayout.noDataReloading();
                                    initData();
                                    return;
                                }
                                if (response1.getRefuseRentType() == 1)
                                {
                                    isRent = true;
                                    tv_rent_time_content.setText("");
                                }
                                else
                                {
                                    isRent = false;
                                    tv_rent_time_content.setText("暂时不可租");
                                }
                                btn_rent_time_set.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        Intent i = new Intent();
                                        i.putExtra(SysConfig.CAR_NAME, carSn);
                                        i.putExtra("IS_RENT", isRent);
                                        i.setClass(CarInfoSimpleActivity.this, SetTimeLimitActivity.class);
                                        startActivity(i);
                                    }
                                });
                            }
                            catch (InvalidProtocolBufferException e)
                            {
                                e.printStackTrace();
                            }

                        }

                        @Override public void onError(VolleyError errorResponse)
                        {

                        }

                        @Override public void networkFinish()
                        {
                            mAllFramelayout.makeProgreeDismiss();

                        }
                    });
                }
                catch (InvalidProtocolBufferException e)
                {
                    e.printStackTrace();
                }
            }

            @Override public void onError(VolleyError errorResponse)
            {
                mAllFramelayout.makeProgreeNoData();
            }

            @Override public void networkFinish()
            {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_car_preview, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home || item.getItemId() == 0)
        {
            onBackPressed();
            return false;
        }
        else if (id == R.id.preview)
        {
            if (Config.isNetworkConnected(context))
            {
                Intent intent = new Intent();
                intent.putExtra(SysConfig.CAR_SN, carSn);
                intent.putExtra("hide", true);
                intent.setClass(context, OwnerCarInfoActivity.class);
                context.startActivity(intent);
            }
            else
            {
                Config.showToast(context, context.getResources().getString(R.string.network_error));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void updataStatus() {
//        Config.showProgressDialog(context, false, null);
//        String statusStr = "1";
//        final boolean checked = renterTb.isChecked();
//        if (renterTb.isChecked()) {
//            statusStr = CarCommon.CarStatus.SUSPEND_RENT_VALUE + "";
//
//        } else {
//            statusStr = CarCommon.CarStatus.CAN_RENT_VALUE + "";
//        }
//        CarInterface.UpdateCarInfo.Request.Builder request = CarInterface.UpdateCarInfo.Request.newBuilder();
//        request.setCarId(carSn);
//        List<CarInterface.UpdateCarInfo.UpodateCarParams> list = new ArrayList<CarInterface.UpdateCarInfo.UpodateCarParams>();
//        list.add(CarInterface.UpdateCarInfo.UpodateCarParams.newBuilder().setKey("carStatus").setValue(statusStr).build());
//        request.addAllParams(list);
//        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.UpdateCarInfo_VALUE);
//        networkTask.setBusiData(request.build().toByteArray());
//        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
//            @Override
//            public void onSuccessResponse(UUResponseData responseData) {
//
//                if (responseData.getRet() == 0) {
//
//                    try {
//                        CarInterface.UpdateCarInfo.Response response = CarInterface.UpdateCarInfo.Response.parseFrom(responseData.getBusiData());
//
//                        if (response.getRet() == 0) {
//                            renterTb.setChecked(checked);
//
//                        } else if (response.getRet() == -1) {
//                            renterTb.setChecked(!checked);
//
//                        } else if (response.getRet() == -2) {
//                            renterTb.setChecked(!checked);
//                            //将车辆设置为可租状态
////                            if (response.has)
//                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                            builder.setMessage("您的定价低于指导价50%，已被自动下架，需要修改价格后才可重新上架！");
//                            builder.setNegativeButton("修改价格", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Intent intent = new Intent();
//                                    intent.putExtra(SysConfig.CAR_SN, carSn);
//                                    intent.setClass(context, PriceActivity.class);
//                                    startActivity(intent);
//                                }
//                            });
//                            builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                            builder.create().show();
//                        }
//
//
//                    } catch (InvalidProtocolBufferException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onError(VolleyError errorResponse) {
//
//            }
//
//            @Override
//            public void networkFinish() {
//                Config.dismissProgress();
//            }
//        });
//    }


    //TODO 加载 图片 ViewPager 报null
    class ImageAdapter extends FragmentPagerAdapter
    {

        private List<String> mImageCount;

        public ImageAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return ImageFragment.newInstance(mImageCount.get(position));
        }

        @Override
        public int getCount()
        {
            return mImageCount == null ? 0 : mImageCount.size();
        }

        public void setCount(List<String> count)
        {
            if (count != null && count.size() > 0)
            {
                mImageCount = count;
                notifyDataSetChanged();
            }
        }
    }


}
