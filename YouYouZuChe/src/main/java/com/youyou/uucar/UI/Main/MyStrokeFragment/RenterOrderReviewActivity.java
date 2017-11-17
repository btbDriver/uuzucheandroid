package com.youyou.uucar.UI.Main.MyStrokeFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.order.OrderFormInterface;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.SysConfig;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 16515_000 on 2014/7/15.
 */
public class RenterOrderReviewActivity extends BaseActivity
{
    public String tag = RenterOrderReviewActivity.class.getSimpleName();
    public Activity context;
    public String   rSn;
    boolean isStarSucces;
    @InjectView(R.id.rating_title)
    TextView  mRatingTitle;
    @InjectView(R.id.star)
    RatingBar mStar;
    @InjectView(R.id.to_owner_review)
    EditText  mToOwnerReview;
    @InjectView(R.id.sure)
    TextView  mSure;

    @Override
    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        Config.setActivityState(this);
        context = this;
        setContentView(R.layout.activity_renter_order_review);
        ButterKnife.inject(this);
        mStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
            {
                if (rating == 0)
                {
                    isStarSucces = false;
                }
                else
                {
                    isStarSucces = true;
                }
            }
        });
        rSn = getIntent().getStringExtra(SysConfig.R_SN);

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

    @OnClick(R.id.sure)
    public void submit()
    {

        if (!isStarSucces)
        {
            Toast.makeText(RenterOrderReviewActivity.this, "请评分后再提交", Toast.LENGTH_SHORT).show();
            return;
        }
        OrderFormInterface.AppraiseOrder.Request.Builder request = OrderFormInterface.AppraiseOrder.Request.newBuilder();
        request.setOrderId(rSn);
        request.setStars(mStar.getRating());

        String note = mToOwnerReview.getText().toString();
        if (note != null && !note.trim().equals(""))
        {
            request.setNote(note);
        }
        else
        {
            request.setNote(mToOwnerReview.getHint().toString());
        }
        request.setNote2("");
        request.setNote3("");
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.AppraiseOrder_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override
            public void onSuccessResponse(UUResponseData response)
            {
                Config.showToast(context, response.getToastMsg());
                if (response.getRet() == 0)
                {
                    try
                    {
                        OrderFormInterface.AppraiseOrder.Response data = OrderFormInterface.AppraiseOrder.Response.parseFrom(response.getBusiData());

                        showToast(response.getResponseCommonMsg().getMsg());
                        if (data.getRet() == 0)
                        {
                            //评价成功
                            setResult(RESULT_OK);
                            finish();
                            Intent intent = new Intent(context, OrderReviewFinishActivity.class);
                            if (data.hasMsg())
                            {
                                intent.putExtra("tip", data.getMsg());
                            }
                            startActivity(intent);
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

            }
        });

    }

}
