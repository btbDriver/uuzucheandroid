package com.youyou.uucar.UI.Main.my;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Network;
import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.banner.OperateInterface;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;

import org.w3c.dom.Text;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * desc:意见反馈activity
 * author:lc
 */
public class Feedback extends BaseActivity {
    public static final String TAG = Feedback.class.getSimpleName();

    @InjectView(R.id.feedback_content_edit)
    EditText feedContent; // 意见反馈功能

    @InjectView(R.id.feedback_contact_edit)
    EditText feedContact; // 联系方式

    @InjectView(R.id.feedback_ok_text)
    TextView feedOk; // 提交按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //判断提交按钮是否可用
        setButton();
        //为意见反馈信息添加内容更改监听事件
        feedContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !s.toString().trim().equals("")) {
                    feedOk.setEnabled(true);
                } else {
                    feedOk.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @OnClick(R.id.feedback_ok_text)
    public void submitContent() {
        showProgress(false);
        OperateInterface.CreateFeedbackRequest.Builder request = OperateInterface.CreateFeedbackRequest.newBuilder();
        String feedContentStr = feedContent.getText().toString().trim();
        String feedContactStr = feedContact.getText().toString().trim();
        request.setFeedback(feedContentStr);
        request.setMobile(feedContactStr);
        NetworkTask task = new NetworkTask(CmdCodeDef.CmdCode.CreateFeedback_VALUE);
        task.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(task, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData responseData) {
                if (responseData.getRet() == 0) {
                    try {
                        showResponseCommonMsg(responseData.getResponseCommonMsg());
                        OperateInterface.CreateFeedbackResponse response = OperateInterface.CreateFeedbackResponse.parseFrom(responseData.getBusiData());
                        if (response.getRet() == 0) {
                            Feedback.this.finish();
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

    /**
     * desc:判断提交按钮是否可用
     */
    public void setButton() {
        if (feedContent.getText().toString() != null && !feedContent.getText().toString().trim().equals("")) {
            feedOk.setEnabled(true);
        } else {
            feedOk.setEnabled(false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        }
        return true;
    }
}
