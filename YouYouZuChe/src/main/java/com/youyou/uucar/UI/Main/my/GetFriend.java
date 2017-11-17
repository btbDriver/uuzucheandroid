package com.youyou.uucar.UI.Main.my;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.MultiStringReplacerEx;
import com.youyou.uucar.Utils.View.UUProgressFramelayout;
import com.youyou.uucar.Utils.empty.EmptyAnimationListener;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by 16515_000 on 2014/8/7.
 */
public class GetFriend extends BaseActivity
{
    public String tag = "GetFriend";
    public Activity context;

    public static final String      DESCRIPTOR    = "com.umeng.share";
    public final        String      APP_ID        = "wx9abfa08f7da32b30";
    private final       SHARE_MEDIA mTestMedia    = SHARE_MEDIA.SINA;
    private final       SHARE_MEDIA TENCENT_MEDIA = SHARE_MEDIA.TENCENT;
    @InjectView(R.id.tip)
    TextView       tip;
    @InjectView(R.id.code)
    TextView       code;
    @InjectView(R.id.wx)
    RelativeLayout mWx;
    @InjectView(R.id.pyq)
    RelativeLayout mPyq;
    @InjectView(R.id.wb)
    RelativeLayout mWb;
    @InjectView(R.id.tx)
    RelativeLayout mTx;

    // sdk controller
    private UMSocialService mController = null;

    public IWXAPI api;


    public void reqToWx()
    {
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);
    }

    @InjectView(R.id.all_framelayout)
    UUProgressFramelayout mAllFramelayout;

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

    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        Config.setActivityState(this);
        context = this;
        setContentView(R.layout.get_friend_activity);
        ButterKnife.inject(this);
        initNoteDataRefush();
        reqToWx();
        mController = UMServiceFactory.getUMSocialService(DESCRIPTOR, RequestType.SOCIAL);
        // 添加新浪和QQ空间的SSO授权支持
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

        mWb.setOnClickListener(layoutClick);
        mTx.setOnClickListener(layoutClick);
        mPyq.setOnClickListener(layoutClick);
        mWx.setOnClickListener(layoutClick);
    }

    @InjectView(R.id.root)
    LinearLayout root;

    @Override
    public void onResume()
    {
        super.onResume();
        getData();
    }

    private void makeProgressDismiss(final FrameLayout mProgress)
    {
        Animation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(400);
        animation.setAnimationListener(new EmptyAnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation animation)
            {
                mProgress.setVisibility(View.GONE);
                mProgress.clearAnimation();
                root.setVisibility(View.VISIBLE);
            }
        });
        mProgress.startAnimation(animation);

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

    String shareText;
    String shareTitle;
    String shareUrl;

    public void copy(String content)
    {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

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
//                            word = word.replaceAll("\\n", "\n");
                            tip.setText(word);

                            code.setText("邀请码：" + response.getInvitationCode());
                            code.setOnLongClickListener(new View.OnLongClickListener()
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


    public View.OnClickListener layoutClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.tx:
                    mController.setShareContent(shareTitle + shareText + shareUrl);
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
                    break;
                case R.id.wx:
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
                        req.scene = SendMessageToWX.Req.WXSceneSession;
                        api.sendReq(req);
                    }
                    break;
                case R.id.pyq:
                    if (Config.isAvilible(context, "com.tencent.mm"))
                    {
                        WXWebpageObject webpage2 = new WXWebpageObject();
                        webpage2.webpageUrl = shareUrl;
                        WXMediaMessage msg2 = new WXMediaMessage(webpage2);
                        msg2.title = shareTitle;
                        msg2.description = shareText;
                        msg2.setThumbImage(BitmapFactory.decodeResource(getResources(), R.drawable.get_friend_icon));
                        SendMessageToWX.Req req2 = new SendMessageToWX.Req();
                        req2.transaction = String.valueOf(System.currentTimeMillis());
                        req2.message = msg2;
                        req2.scene = SendMessageToWX.Req.WXSceneTimeline;
                        boolean is = api.sendReq(req2);
//                        if(is)
//                        {
//
//                            Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
//                        }
//                        else
//                        {
//
//                            Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
//                        }
                    }

                    break;
                case R.id.wb:
                    mController.setShareContent(shareTitle + shareText + shareUrl);
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
                    break;
            }
        }
    };
}
