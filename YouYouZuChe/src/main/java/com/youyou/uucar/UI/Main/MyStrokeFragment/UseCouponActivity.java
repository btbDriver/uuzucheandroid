package com.youyou.uucar.UI.Main.MyStrokeFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.order.OrderFormInterface26;
import com.uu.client.bean.user.common.UserCommon;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.SysConfig;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

//import com.ab.http.AbHttpUtil;
//import com.ab.http.AbRequestParams;
//import com.youyou.uucar.Utils.Support.MyAbStringHttpResponseListener;

/**
 * Created by 16515_000 on 2014/7/17.
 */
public class UseCouponActivity extends BaseActivity
{
    public String tag = "UseCouponActivity";
    //    public Activity context;
    public String rsn;
    @InjectView(R.id.list)
    ListView mList;
    @InjectView(R.id.button)
    TextView sure;
    List<CouponItem> data = new ArrayList<CouponItem>();
    Myadapter adapter;
    public  UUProgressFramelayout mAllFramelayout;
    private ArrayList<String>     intentCouponIds;
    Dialog dialog;
    @InjectView(R.id.root)
    RelativeLayout root;
    @InjectView(R.id.nodata_root)
    RelativeLayout noDataRoot;
    @OnClick(R.id.button)
    public void sureClick()
    {


        final ArrayList<CouponItem> couponSelectedList = new ArrayList<CouponItem>();
        final ArrayList<String> idList = new ArrayList<String>();
        boolean hasFrist = false;
        for (CouponItem item : data)
        {
            if (item.isSelect)
            {
                idList.add(item.coupon_id + "");
                couponSelectedList.add(item);
            }
            //首租类型
            if (item.couponType == 1)
            {
                hasFrist = true;
            }
        }
        if (hasFrist)
        {

            boolean hasFristSelected = false;
            for (CouponItem item : couponSelectedList)
            {

                if (item.couponType == 1)
                {
                    hasFristSelected = true;
                }
            }
            if (hasFristSelected)
            {

                Intent intent = new Intent();
                if (idList.size() > 0)
                {
                    intent.putStringArrayListExtra(SysConfig.COUPON_ID, idList);
                }
                setResult(RESULT_OK, intent);
                finish();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("您是第一次租车，建议您使用\"首次租车优惠券\"，避免浪费哦");
                builder.setNegativeButton("重新选择", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dia, int which)
                    {
                        return;
                    }
                }).setPositiveButton("就用这张", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dia, int whichButton)
                    {
                        Intent intent = new Intent();
                        if (idList.size() > 0)
                        {
                            intent.putStringArrayListExtra(SysConfig.COUPON_ID, idList);
                        }
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                dialog = builder.create();
                dialog.show();

            }
        }
        else
        {
            Intent intent = new Intent();
            if (idList.size() > 0)
            {
                intent.putStringArrayListExtra(SysConfig.COUPON_ID, idList);
            }
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        Config.setActivityState(this);
        setContentView(R.layout.use_coupon_activity);
        rsn = getIntent().getStringExtra(SysConfig.R_SN);
        intentCouponIds = getIntent().getStringArrayListExtra(SysConfig.COUPON_ID);
        ButterKnife.inject(this);
        mList = (ListView) findViewById(R.id.list);
        mAllFramelayout = (UUProgressFramelayout) findViewById(R.id.all_framelayout);
        adapter = new Myadapter();
        mList.setAdapter(adapter);
        initNoteDataRefush();
        onRefresh();

    }

    public void initNoteDataRefush()
    {
        TextView noDataRefush = (TextView) mAllFramelayout.findViewById(R.id.refush);
        noDataRefush.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (Config.isNetworkConnected(context))
                {
                    onRefresh();
                }
                else
                {
                    mAllFramelayout.makeProgreeNoData();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0)
        {
            onBackPressed();
            return false;
        }
        return true;
    }

    public void onRefresh()
    {

        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.RenterQueryCouponsByOrder_VALUE);
        OrderFormInterface26.RenterQueryCouponsByOrder.Request.Builder request = OrderFormInterface26.RenterQueryCouponsByOrder.Request.newBuilder();
        request.setOrderId(rsn);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override
            public void onSuccessResponse(UUResponseData responseData)
            {
                if (responseData.getRet() == 0)
                {
                    try
                    {
                        OrderFormInterface26.RenterQueryCouponsByOrder.Response dataResponce = OrderFormInterface26.RenterQueryCouponsByOrder.Response.parseFrom(responseData.getBusiData());
                        if (dataResponce.getRet() == 0)
                        {
                            if (data != null)
                            {
                                data.clear();
                            }
                            int couponsCount = dataResponce.getCouponsCount();
                            if (couponsCount > 0)
                            {
                                List<UserCommon.Coupon> couponsList = dataResponce.getCouponsList();
                                for (int i = 0; i < couponsList.size(); i++)
                                {
                                    CouponItem item = new CouponItem();
                                    item.fromProtoBuf(couponsList.get(i));
                                    data.add(item);
                                }
                            }

                            if (couponsCount == 0)
                            {
//                                listview.removeFooterView(mLoadingFooter.getView());
//                                if (page == 1)
//                                {
                                    root.setVisibility(View.GONE);
                                    noDataRoot.setVisibility(View.VISIBLE);
//                                }
//                                progressFramelayout.makeProgreeDismiss();
                            }
                            else
                            {
                                root.setVisibility(View.VISIBLE);
                                noDataRoot.setVisibility(View.GONE);
//                                if (dataResponse.getPageResult().getHasMore())
//                                {
////                                    if(listview.getFooterViewsCount()>0)
////                                    {
////                                        listview.removeFooterView(mLoadingFooter.getView());
////                                    }
//                                    if (listview.getFooterViewsCount() == 0)
//                                    {
//                                        listview.addFooterView(mLoadingFooter.getView());
//                                    }
//                                }
                            }
                            adapter.notifyDataSetChanged();
                            mAllFramelayout.makeProgreeDismiss();

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
                mAllFramelayout.makeProgreeNoData();
            }

            @Override
            public void networkFinish()
            {

            }
        });

//        AbHttpUtil ab = AbHttpUtil.getInstance(this);
//        AbRequestParams params = new AbRequestParams();
//        params.put("sid", Config.getUser(context).sid);
//        params.put("r_sn", rsn);
//        ab.post(ServerMutualConfig.getUseCoupon, params, new MyAbStringHttpResponseListener(context) {
//            @Override
//            public void onSuccess(int statusCode, String content) {
//                super.onSuccess(statusCode, content);
//                try {
//                    JSONObject json = new JSONObject(content);
//                    MLog.e(tag, "json = " + json.toString());
//                    if (json.getString("status").equals("1")) {
//                        data.clear();
//                        for (int i = 0; i < json.getJSONArray("content").length(); i++) {
//                            CouponItem item = new CouponItem();
//                            item.fromJson(json.getJSONArray("content").getJSONObject(i));
//                            data.add(item);
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                super.onFinish();
//                Config.dismissProgress();
//            }
//
//            @Override
//            public void onFailure(int statusCode, String content, Throwable error) {
//
//                super.onFailure(statusCode, content, error);
//                Config.showFiledToast(context);
//            }
//        });
    }

    public void itemClick(CouponItem item)
    {

        if (item.isSelect)
        {
            item.isSelect = false;
        }
        else
        {
            //可叠加
            if (item.isdouble)
            {
                for (CouponItem item2 : data)
                {
                    if (item2.isSelect)
                    {
                        if (!item2.isdouble)
                        {
                            item2.isSelect = false;
                        }
                        break;
                    }
                }
            }
            //不可叠加
            else
            {
                for (CouponItem item2 : data)
                {
                    item2.isSelect = false;
                }
            }

            item.isSelect = true;
        }

    }

    public class CouponItem
    {
        public String coupon_id = "";

        public String  name       = "";
        public String  desc       = "";
        public boolean isdouble   = false;
        public String  amount     = "";
        public String  time       = "";
        public String  isUse      = "";
        public int     couponType = -1;
        public boolean isSelect   = false;

//        public void fromJson(JSONObject json) throws JSONException {
//            name = json.getString("coupon_name");
//            desc = json.getString("description");
//            isdouble = json.getString("double");
//            amount = json.getString("amount");
//            time = json.getString("use_time");
//            coupon_id = json.get("coupon_id").toString();
//            double_code = json.get("double_code").toString();
//        }

        public void fromProtoBuf(UserCommon.Coupon json)
        {
//            name = json.getCouponName();
            desc = json.getDescription();
//            isdouble = json.getIsMulti() ? "可叠加" : "不可叠加";
//            float amountf = json.getAmount();
//            Float amountFloat = new Float(amountf);
//            int amoutInt = amountFloat.intValue();
//            amount = amoutInt + "";
//            time = json.getConsumeTime() + "";
//            coupon_id = json.getCouponId();
//            double_code = json.getIsMulti() ? "1" : "0";
            coupon_id = json.getCouponId();

            couponType = json.getCouponType();
            name = json.getCouponName();
//            desc = json.getDescription();
            isdouble = json.getIsMulti();
            amount = ((int) json.getAmount()) + "";
            long startTime = json.getValidStart() * 1000L;
            long endTime = json.getValidEnd() * 1000L;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            String start = formatter.format(startTime);
            String end = formatter.format(endTime);
            time = start + "-" + end;
            isUse = json.getState() + "";
            if (intentCouponIds != null)
            {
                for (String id : intentCouponIds)
                {
                    if (id.equals(coupon_id))
                    {
                        isSelect = true;
                        break;
                    }
                }
            }

        }
    }

    @Override
    public void onBackPressed()
    {
        ArrayList<String> idList = new ArrayList<String>();
        for (CouponItem item : data)
        {
            if (item.isSelect)
            {
                idList.add(item.coupon_id + "");
            }
        }
        Intent intent = new Intent();
        if (idList.size() > 0)
        {
            intent.putStringArrayListExtra(SysConfig.COUPON_ID, idList);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    public class Myadapter extends BaseAdapter
    {
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
        public View getView(int position, View convertView, ViewGroup parent)
        {
            convertView = getLayoutInflater().inflate(R.layout.use_coupon_item, null);
            TextView price = (TextView) convertView.findViewById(R.id.price);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView time = (TextView) convertView.findViewById(R.id.time);
            TextView used = (TextView) convertView.findViewById(R.id.used);
            TextView desc = (TextView) convertView.findViewById(R.id.desc);
            final CouponItem item = data.get(position);
            desc.setText(item.desc);
            price.setText(item.amount);
            name.setText(item.name);
            time.setText(item.time);
            ImageView isdouble = (ImageView) convertView.findViewById(R.id.isdouble);

            if (item.isdouble)
            {
                isdouble.setVisibility(View.VISIBLE);
            }
            else
            {
                isdouble.setVisibility(View.GONE);
            }
            if (item.isSelect)
            {
                used.setBackgroundResource(R.drawable.coupon_event_right_select_bg);
                used.setText("已选中");
                used.setTextColor(Color.parseColor("#FFFFFF"));
            }
            else
            {
                used.setBackgroundResource(R.drawable.coupon_item_use_bg);
                used.setText("未使用");
                used.setTextColor(Color.parseColor("#00a0e3"));
            }

            final View finalConvertView = convertView;
            convertView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    itemClick(item);
                    if (item.isSelect)
                    {
                        finalConvertView.setSelected(true);
                    }
                    else
                    {
                        finalConvertView.setSelected(false);

                    }
                    adapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }
}
