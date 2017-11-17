package com.youyou.uucar.UI.Main.MyStrokeFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
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
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.user.UserInterface;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MultiStringReplacerEx;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderReviewFinishActivity extends BaseActivity
{

    @InjectView(R.id.tip)            TextView     mTip;
    @InjectView(R.id.yaoqingma_text) TextView     mYaoqingmaText;
    @InjectView(R.id.wx)             LinearLayout mWx;
    @InjectView(R.id.wb)             LinearLayout mWb;
    @InjectView(R.id.friend)         LinearLayout mFriend;
    @InjectView(R.id.txwb)           LinearLayout mTxwb;
    @InjectView(R.id.code)           TextView     mCode;

    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;


    private final SHARE_MEDIA mTestMedia    = SHARE_MEDIA.SINA;
    private final SHARE_MEDIA TENCENT_MEDIA = SHARE_MEDIA.TENCENT;

    private void share()
    {
        LinearLayout weixin = (LinearLayout) findViewById(R.id.wx);
        LinearLayout friend = (LinearLayout) findViewById(R.id.friend);
        LinearLayout weibo = (LinearLayout) findViewById(R.id.wb);
        LinearLayout tx_weibo = (LinearLayout) findViewById(R.id.txwb);
        tx_weibo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mController.setShareContent(shareText);
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
                    webpage.webpageUrl = shareUrl/*.substring(carContentModel.getShareWord().indexOf("http:"));*/;
                    WXMediaMessage msg = new WXMediaMessage(webpage);
                    msg.title = shareTitle;
                    msg.description = shareText;
                    msg.setThumbImage(BitmapFactory.decodeResource(getResources(), R.drawable.get_friend_icon));
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = String.valueOf(System.currentTimeMillis());
                    req.message = msg;
                    req.scene = SendMessageToWX.Req.WXSceneSession;
                    api.sendReq(req);
                }
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
                    webpage.webpageUrl = shareUrl;
                    WXMediaMessage msg = new WXMediaMessage(webpage);
                    msg.title = shareTitle;
                    msg.description = shareText;
                    msg.setThumbImage(BitmapFactory.decodeResource(getResources(), R.drawable.get_friend_icon));
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = String.valueOf(System.currentTimeMillis());
                    req.message = msg;
                    req.scene = SendMessageToWX.Req.WXSceneTimeline;
                    api.sendReq(req);
                }

            }
        });
        weibo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mController.setShareContent(shareText);
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
            }
        });
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
                mAllFramelayout.noDataReloading();
                getData();
            }
        });
    }

    // sdk controller
    private UMSocialService mController = null;

    public IWXAPI api;


    public void reqToWx()
    {
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);
    }

    public static final String DESCRIPTOR = "com.umeng.share";
    public final        String APP_ID     = "wx9abfa08f7da32b30";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_review_finish);
        ButterKnife.inject(this);
        if (getIntent().hasExtra("tip") && getIntent().getStringExtra("tip").trim().length() > 0)
        {
            mTip.setText(getIntent().getStringExtra("tip"));
        }
        else
        {
            mTip.setVisibility(View.INVISIBLE);
        }

        initNoteDataRefush();
        reqToWx();

        mController = UMServiceFactory.getUMSocialService(DESCRIPTOR, RequestType.SOCIAL);
        // 添加新浪和QQ空间的SSO授权支持
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
        getData();
        share();
    }

    public void copy(String content)
    {
        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    String shareText;
    String shareTitle;
    String shareUrl;

    public void getData()
    {
        UserInterface.UserShare.Request.Builder request = UserInterface.UserShare.Request.newBuilder();
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.UserShare_VALUE);
        task.setTag("UserShare");
        task.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>()
        {
            @Override
            public void onSuccessResponse(UUResponseData responseData)
            {

                if (responseData.getRet() == 0)
                {
                    try
                    {
                        final UserInterface.UserShare.Response response = UserInterface.UserShare.Response.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0)
                        {

                            String word = response.getWord();
                            MultiStringReplacerEx replacer = new MultiStringReplacerEx();
                            replacer.add("\\n", "\n");
                            word = replacer.replace(word);
//                            tip.setText(word);

                            mCode.setText("邀请码：" + response.getInvitationCode());
                            mCode.setOnLongClickListener(new View.OnLongClickListener()
                            {
                                @Override
                                public boolean onLongClick(View v)
                                {
                                    copy(response.getInvitationCode());
                                    showToast("邀请码复制成功");
                                    return false;
                                }
                            });
                            shareText = response.getShareContent();
                            shareTitle = response.getShareTitle();
                            shareUrl = response.getShareUrl();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        mAllFramelayout.makeProgreeNoData();
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
                mAllFramelayout.makeProgreeDismiss();
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
}
