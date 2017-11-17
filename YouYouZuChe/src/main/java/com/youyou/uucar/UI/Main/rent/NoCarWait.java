package com.youyou.uucar.UI.Main.rent;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.view.progress.AbCircleProgressBar;
import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.uu.client.bean.order.OrderFormInterface26;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.observer.ObserverListener;
import com.youyou.uucar.Utils.observer.ObserverManager;

import java.util.Timer;
import java.util.TimerTask;

//import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterOrderConfirmActivity;
//import com.youyou.uucar.UI.Main.MyStrokeFragment.RenterOrderConfirmActivity;

public class NoCarWait extends BaseActivity {
    public String tag = "NoCarWait";
    public Activity context;
    long TIME_COUNT = 1000 * 60 * 10;
    TextView times;
    AbCircleProgressBar mAbProgressBar;
    long start_time_long, end_time_long;
    TextView tip;
    boolean hasOwnerAgree = false;
    private ImageView circleBlueImageView, shadowImageView, secondImageView, circle_1, circle_2, circle_3, circle_4, location_1, location_2, location_3;
    private AnimatorSet onceAinimatorSet, repeatAnimatorSet;
    Timer timer;
    TimerTask timerTask;
    TextView num;

    public void onCreate(Bundle b) {
        super.onCreate(b);
//        getActionBar().hide();
        context = this;
        timer = new Timer();
        setContentView(R.layout.animation_no_car_wait);
        initView();
        initAnimation();
//        TextView text = new TextView(this);
//        text.setText("等push,别动");
//        setContentView(text);
        ObserverManager.addObserver(ObserverManager.QUICKRENTCARPUSH, getAgreeListener);
        getFindCarAgree();
    }

    ObserverListener getAgreeListener = new ObserverListener() {
        public void observer(String from, Object obj) {

            long timeCount = getIntent().getLongExtra("maxtime", TIME_COUNT);
            long passedTime = getIntent().getLongExtra("passedTime", 0);
            if (timeCount == 0) {
                timeCount = TIME_COUNT;
            }
            Intent intent = new Intent(context, FindCarAgreeActivity.class);
            intent.putExtra("maxtime", timeCount);
            intent.putExtra("passedTime", passedTime);
            startActivity(intent);
            finish();
            ObserverManager.removeObserver(ObserverManager.QUICKRENTCARPUSH);
        }
    };

    private void initAnimation() {

        repeatAnimatorSet = new AnimatorSet();
        ObjectAnimator circleAlpha_1 = ObjectAnimator.ofFloat(this.circle_1, "alpha", 0f, 1.0f);
        circleAlpha_1.setDuration(400L);
        circleAlpha_1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                circle_1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        repeatAnimatorSet.play(circleAlpha_1);
        ObjectAnimator circleAlpha_2 = ObjectAnimator.ofFloat(this.circle_2, "alpha", 0f, 1.0f);
        circleAlpha_2.setDuration(400L);
        circleAlpha_2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                circle_2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        repeatAnimatorSet.play(circleAlpha_2).after(circleAlpha_1);
        ObjectAnimator circleAlpha_3 = ObjectAnimator.ofFloat(this.circle_3, "alpha", 0f, 1.0f);
        circleAlpha_3.setDuration(400L);
        circleAlpha_3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                circle_3.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        repeatAnimatorSet.play(circleAlpha_3).after(circleAlpha_2);
        ObjectAnimator circleAlpha_4 = ObjectAnimator.ofFloat(this.circle_4, "alpha", 0f, 1.0f);
        circleAlpha_4.setDuration(400L);
        circleAlpha_4.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                circle_4.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        repeatAnimatorSet.play(circleAlpha_4).after(circleAlpha_3);

        ObjectAnimator locationAlpha_1 = ObjectAnimator.ofFloat(this.location_1, "alpha", 0f, 1.0f, 0.f);
        locationAlpha_1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                location_1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        locationAlpha_1.setDuration(800L);
        repeatAnimatorSet.play(locationAlpha_1).after(1000L);
        ObjectAnimator locationAlpha_2 = ObjectAnimator.ofFloat(this.location_2, "alpha", 0f, 1.0f, 0.f);
        locationAlpha_2.setDuration(800L);
        locationAlpha_2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                location_2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        repeatAnimatorSet.play(locationAlpha_2).after(locationAlpha_1);

        ObjectAnimator locationAlpha_3 = ObjectAnimator.ofFloat(this.location_3, "alpha", 0f, 1.0f, 0.f);
        locationAlpha_3.setDuration(800L);
        locationAlpha_3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                location_3.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        repeatAnimatorSet.play(locationAlpha_3).after(locationAlpha_2);
        ObjectAnimator goneCircle_1 = ObjectAnimator.ofFloat(this.circle_1, "alpha", 1.0f, 0.5f, 0.0f);
        goneCircle_1.setDuration(800L);
        goneCircle_1.setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator goneCircle_2 = ObjectAnimator.ofFloat(this.circle_2, "alpha", 1.0f, 0.5f, 0.0f);
        goneCircle_2.setDuration(800L);
        goneCircle_2.setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator goneCircle_3 = ObjectAnimator.ofFloat(this.circle_3, "alpha", 1.0f, 0.5f, 0.0f);
        goneCircle_3.setDuration(800L);
        goneCircle_3.setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator goneCircle_4 = ObjectAnimator.ofFloat(this.circle_4, "alpha", 1.0f, 0.5f, 0.0f);
        goneCircle_4.setDuration(800L);
        goneCircle_4.setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator goneCircle_5 = ObjectAnimator.ofFloat(this.circle_4, "alpha", 0.0f, 0.0f);
        goneCircle_4.setDuration(400L);
        goneCircle_4.setInterpolator(new AccelerateDecelerateInterpolator());
        repeatAnimatorSet.play(goneCircle_1).after(2800L);
        repeatAnimatorSet.play(goneCircle_2).after(3200L);
        repeatAnimatorSet.play(goneCircle_3).after(3600L);
        repeatAnimatorSet.play(goneCircle_4).after(4000L);
        repeatAnimatorSet.play(goneCircle_5).after(goneCircle_4);

        repeatAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                repeatAnimatorSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        onceAinimatorSet = new AnimatorSet();
        ObjectAnimator localObjectAnimator1 = ObjectAnimator.ofFloat(this.circleBlueImageView, "scaleX", new float[]{0f, 1.0F});
        ObjectAnimator localObjectAnimator2 = ObjectAnimator.ofFloat(this.circleBlueImageView, "scaleY", new float[]{0f, 1.0F});
        onceAinimatorSet.play(localObjectAnimator1).with(localObjectAnimator2);
        localObjectAnimator1.setDuration(200L);
        localObjectAnimator2.setDuration(200L);
        localObjectAnimator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                circleBlueImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ObjectAnimator localObjectAnimator3 = ObjectAnimator.ofFloat(this.circleBlueImageView, "scaleX", new float[]{1.0f, 1.2F});
        ObjectAnimator localObjectAnimator4 = ObjectAnimator.ofFloat(this.circleBlueImageView, "scaleY", new float[]{1.0f, 1.2F});
        localObjectAnimator3.setDuration(100L);
        localObjectAnimator4.setDuration(100L);

        onceAinimatorSet.play(localObjectAnimator3).with(localObjectAnimator4).after(localObjectAnimator1);
        ObjectAnimator localObjectAnimator5 = ObjectAnimator.ofFloat(this.circleBlueImageView, "scaleX", new float[]{1.2f, 0.8F});
        ObjectAnimator localObjectAnimator6 = ObjectAnimator.ofFloat(this.circleBlueImageView, "scaleY", new float[]{1.2f, 0.8F});
        localObjectAnimator5.setDuration(200L);
        localObjectAnimator6.setDuration(200L);
        onceAinimatorSet.play(localObjectAnimator5).with(localObjectAnimator6).after(localObjectAnimator3);
        ObjectAnimator localObjectAnimator7 = ObjectAnimator.ofFloat(this.circleBlueImageView, "scaleX", new float[]{0.8f, 1.0F});
        ObjectAnimator localObjectAnimator8 = ObjectAnimator.ofFloat(this.circleBlueImageView, "scaleY", new float[]{0.8f, 1.0F});
        localObjectAnimator7.setDuration(100L);
        localObjectAnimator8.setDuration(100L);
        onceAinimatorSet.play(localObjectAnimator7).with(localObjectAnimator8).after(localObjectAnimator5);
        ObjectAnimator secondAnimator1 = ObjectAnimator.ofFloat(this.secondImageView, "scaleX", new float[]{0f, 1.0F});
        ObjectAnimator secondAnimator2 = ObjectAnimator.ofFloat(this.secondImageView, "scaleY", new float[]{0f, 1.0F});
        onceAinimatorSet.play(secondAnimator1).with(secondAnimator2).after(localObjectAnimator7);
        secondAnimator1.setDuration(200L);
        secondAnimator2.setDuration(200L);
        secondAnimator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                secondImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ObjectAnimator secondAnimator3 = ObjectAnimator.ofFloat(this.secondImageView, "scaleX", new float[]{1.0f, 1.2F});
        ObjectAnimator secondAnimator4 = ObjectAnimator.ofFloat(this.secondImageView, "scaleY", new float[]{1.0f, 1.2F});
        secondAnimator3.setDuration(100L);
        secondAnimator3.setInterpolator(new AccelerateDecelerateInterpolator());
        secondAnimator4.setInterpolator(new AccelerateDecelerateInterpolator());
        secondAnimator4.setDuration(100L);
        onceAinimatorSet.play(secondAnimator3).with(secondAnimator4).after(secondAnimator1);
        ObjectAnimator secondAnimator5 = ObjectAnimator.ofFloat(this.secondImageView, "scaleX", new float[]{1.2f, 0.8F});
        ObjectAnimator secondAnimator6 = ObjectAnimator.ofFloat(this.secondImageView, "scaleY", new float[]{1.2f, 0.8F});
        secondAnimator5.setDuration(200L);
        secondAnimator6.setDuration(200L);
        secondAnimator5.setInterpolator(new AccelerateDecelerateInterpolator());
        secondAnimator6.setInterpolator(new AccelerateDecelerateInterpolator());
        onceAinimatorSet.play(secondAnimator5).with(secondAnimator6).after(secondAnimator3);
        ObjectAnimator secondAnimator7 = ObjectAnimator.ofFloat(this.secondImageView, "scaleX", new float[]{0.8f, 1.0F});
        ObjectAnimator secondAnimator8 = ObjectAnimator.ofFloat(this.secondImageView, "scaleY", new float[]{0.8f, 1.0F});
        secondAnimator7.setDuration(100L);
        secondAnimator8.setDuration(100L);
        secondAnimator8.setInterpolator(new AccelerateDecelerateInterpolator());
        secondAnimator7.setInterpolator(new AccelerateDecelerateInterpolator());
        onceAinimatorSet.play(secondAnimator7).with(secondAnimator8).after(secondAnimator5);
        ObjectAnimator shadowAnimator = ObjectAnimator.ofFloat(this.shadowImageView, "alpha", 0f, 1.0f);
        shadowAnimator.setDuration(200L);
        shadowAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        shadowAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                shadowImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        onceAinimatorSet.play(shadowAnimator).after(secondAnimator7);

        onceAinimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                repeatAnimatorSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        onceAinimatorSet.start();


    }

    private void initView() {
        circleBlueImageView = (ImageView) findViewById(R.id.citcle_blue);
        shadowImageView = (ImageView) findViewById(R.id.shadow);
        secondImageView = (ImageView) findViewById(R.id.second);
        circle_1 = (ImageView) findViewById(R.id.circle_1);
        circle_2 = (ImageView) findViewById(R.id.circle_2);
        circle_3 = (ImageView) findViewById(R.id.circle_3);
        circle_4 = (ImageView) findViewById(R.id.circle_4);
        location_1 = (ImageView) findViewById(R.id.location_1);
        location_2 = (ImageView) findViewById(R.id.location_2);
        location_3 = (ImageView) findViewById(R.id.location_3);
        times = (TextView) findViewById(R.id.time);
        mAbProgressBar = (AbCircleProgressBar) findViewById(R.id.circleProgressBar);
        num = (TextView) findViewById(R.id.num);
        String tip2s = "已有" + 0 + "位车主抢单";
        SpannableStringBuilder style = new SpannableStringBuilder(tip2s);
        style.setSpan(new AbsoluteSizeSpan(25, true), tip2s.indexOf("有") + 1, tip2s.indexOf("位车"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#55acef")), tip2s.indexOf("有") + 1, tip2s.indexOf("位车"), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        num.setText(style);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nocar_wait_menu, menu);
        menu.findItem(R.id.cancel).setTitle("取消预约");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        } else if (item.getItemId() == R.id.cancel) {
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
        }
        return true;
    }

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
//                            context.stopService(new Intent(context, RentingService.class));

                            getApp().quitRenting();
                            Config.isSppedIng = false;
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
//    boolean refuse = false;
//    public ObserverListener newMessageListener = new ObserverListener() {
//        @Override
//        public void observer(String from, Object obj) {
//            AbHttpUtil ab = AbHttpUtil.getInstance(context);
//            AbRequestParams params = new AbRequestParams();
//            params.put("sid", Config.getUser(context).sid);
//            ab.post(ServerMutualConfig.getnew, params, new MyAbStringHttpResponseListener(ServerMutualConfig.getnew, params, context) {
//                @Override
//                public void onSuccess(int statusCode, String content) {
//                    super.onSuccess(statusCode, content);
//                    try {
//                        JSONObject json = new JSONObject(content);
//                        JSONObject renter = json.getJSONObject("content").getJSONObject("renter_reservation");
//                        MLog.e(tag, "renter = " + renter.toString());
//                        int agree = Integer.parseInt(renter.getString("agree_car_count"));
//                        int car_count = Integer.parseInt(renter.getString("car_count"));
//                        int refuse_car_count = Integer.parseInt(renter.getString("refuse_car_count"));
//                        String car_sn = renter.getString("car_sn");
//                        final String r_sn = renter.getString("r_sn");
//                        SharedPreferences hireSp = context.getSharedPreferences("hire", Context.MODE_PRIVATE);
//                        Editor edit = hireSp.edit();
//                        edit.putInt("agree", agree);
//                        edit.commit();
//                        if (agree > 0) {
//                            // 只约了1辆车
//                            if (car_count == 1) {
//                                final AbHttpUtil fh = AbHttpUtil.getInstance(context);
//                                Config.showProgressDialog(context, false, new Config.ProgressCancelListener() {
//                                    @Override
//                                    public void progressCancel() {
//                                    }
//                                });
//                                AbRequestParams params = new AbRequestParams();
//                                params.put("sid", Config.getUser(context).sid);
//                                params.put("car_sn", car_sn);
//                                params.put("r_sn", r_sn);
//                                fh.post(ServerMutualConfig.confirmreservation, params, new MyAbStringHttpResponseListener(ServerMutualConfig.confirmreservation, params, context) {
//                                    @Override
//                                    public void onSuccess(int statusCode, String content) {
//                                        super.onSuccess(statusCode, content);
//                                        MLog.e(tag, "hire json = " + content);
//                                        try {
//                                            JSONObject json = new JSONObject(content);
//                                            if (json.getInt("status") == 1) {
//                                                Intent intent = new Intent(context, RenterOrderConfirmActivity.class);
//                                                intent.putExtra(SysConfig.R_SN, r_sn);
//                                                startActivity(intent);
//                                                setResult(RESULT_OK);
//                                                ObserverManager.removeObserver(PushService.SCENE_RENTER_NO_CAR_WAIT);
//                                                finish();
//                                            }
//                                            Toast.makeText(context, json.getString("msg"), Toast.LENGTH_SHORT).show();
//                                        } catch (JSONException e) {
//                                            //
//                                            e.printStackTrace();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFinish() {
//                                        super.onFinish();
//                                        Config.dismissProgress();
//                                    }
//
//                                    @Override
//                                    public void onFailure(int statusCode, String content, Throwable error) {
//                                        super.onFailure(statusCode, content, error);
//                                    }
//                                });
//                            }
//                            // 多车逻辑
//                            else {
//                                if (Integer.parseInt(renter.getString("car_count")) > 1) {
//                                    Toast.makeText(context, getString(R.string.goto_list_tip), Toast.LENGTH_SHORT).show();
//                                }
//                                time.cancel();
//                                ObserverManager.removeObserver(PushService.SCENE_RENTER_NO_CAR_WAIT);
//                                startActivity(new Intent(context, FindCarAgreeActivity.class));
//                                finish();
//                            }
//                        }
//                        // 一辆车 被拒绝 或者选择的车全部悲剧
//                        if ((refuse_car_count == 1 && car_count == 1) || refuse_car_count == car_count) {
//                            time.cancel();
//                            cancelHire();
//                            isTipCancel = true;
//                            refuse = true;
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFinish() {
//                    super.onFinish();
//                }
//
//                @Override
//                public void onFailure(int statusCode, String content, Throwable error) {
//                    super.onFailure(statusCode, content, error);
//                }
//            });
//        }
//
//    };
//    public OnClickListener cancelClick = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setMessage("您真的要取消本次预约请求吗?");
//            builder.setNegativeButton("确定取消", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    cancelHire();
//                }
//            });
//            builder.setNeutralButton("放弃取消", null);
//            builder.create().show();
//        }
//    };
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        if (time != null) {
//            time.cancel();
//        }
//    }
//


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }


    public void getFindCarAgree() {
        OrderFormInterface26.RenterQueryQuickRentAgreeList.Request.Builder request = OrderFormInterface26.RenterQueryQuickRentAgreeList.Request.newBuilder();
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
                            TIME_COUNT = response.getTotalWaitTime() * 1000L;
                            long passedTime = response.getPassedTime() * 1000L;
                            if (time != null) {
                                time.cancel();
                                time = null;
                            }
                            start_time_long = System.currentTimeMillis() - passedTime;
                            end_time_long = start_time_long + TIME_COUNT;
                            if (time != null) {
                                time.cancel();
                                time = null;
                            }
                            time = new TimeCount((TIME_COUNT - passedTime), 50);// 构造CountDownTimer对象
                            time.start();
                            if (response.getAgreeCarListCount() > 0) {
                                Intent intent = new Intent(context, FindCarAgreeActivity.class);
                                intent.putExtra("maxtime", TIME_COUNT);
                                intent.putExtra("passedTime", passedTime);
                                startActivity(intent);
                                finish();
                                ObserverManager.removeObserver(ObserverManager.QUICKRENTCARPUSH);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (time != null) {
            time.cancel();
        }
        time = null;
        if (timer != null) {
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }

        if (onceAinimatorSet != null) {
            onceAinimatorSet.cancel();
        }
        if (repeatAnimatorSet != null) {
            repeatAnimatorSet.cancel();
        }
        ObserverManager.removeObserver(ObserverManager.QUICKRENTCARPUSH);
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

            if (timer != null) {
                timer.cancel();
            }
//            if (task != null) {
//                task.cancel();
//            }
            Dialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(NoCarWait.this);
            builder.setMessage("车主没有接受约车请求，可重新试试一键约车");
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
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

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
