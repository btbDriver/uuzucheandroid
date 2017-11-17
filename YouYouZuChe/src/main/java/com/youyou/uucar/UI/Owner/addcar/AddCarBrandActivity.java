package com.youyou.uucar.UI.Owner.addcar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ReplacementTransformationMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.car.CarInterface;
import com.uu.client.bean.car.common.CarCommon;
import com.uu.client.bean.cmdcode.CmdCodeDef;
import com.youyou.uucar.API.ServerMutualConfig;
import com.youyou.uucar.DB.Model.Car;
import com.youyou.uucar.DB.Model.User;
import com.youyou.uucar.DB.Service.UserService;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UI.Main.my.URLWebView;
import com.youyou.uucar.UI.Owner.calculate.CalculatePriceActivity;
import com.youyou.uucar.Utils.Network.HttpResponse;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.NetworkUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * 添加车辆--选择车辆的车牌号和车的品牌类型界面
 */
public class AddCarBrandActivity extends BaseActivity {
    public static String[] cityArray;
    public final int GOTO_WAIT_APPLY_SERVICE = 800;
    public final int BRAND_RESULT = 900;
    public String tag = "AddCarBrand";
    public Activity context;
    @InjectView(R.id.city)
    TextView city;
    @InjectView(R.id.num)
    EditText num;
    @InjectView(R.id.brand_root)
    RelativeLayout brand_root;
    @InjectView(R.id.brand)
    TextView brand;
    @InjectView(R.id.applyService)
    TextView applyService;
    @InjectView(R.id.releasecar)
    TextView releaseCar;
    @InjectView(R.id.checkbox)
    CheckBox mCheckbox;
    @InjectView(R.id.owner_xieyi)
    TextView mOwnerXieYi;
    UserService userService;
    SharedPreferences citysp;
    boolean numSuccess, brandSuccess, openCitySuccess, gearboxSuccess, yearSuccess;
    @InjectView(R.id.open_city)
    TextView mOpenCity;
    @InjectView(R.id.gearbox)
    TextView mGearbox;
    @InjectView(R.id.gearbox_root)
    RelativeLayout mGearboxRoot;
    @InjectView(R.id.year)
    TextView mYear;
    @InjectView(R.id.year_root)
    RelativeLayout mYearRoot;

    @OnClick(R.id.open_city_root)
    public void openCityClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final NumberPicker city_picker = new NumberPicker(context);
        builder.setView(city_picker);
        city_picker.setMinValue(0);
        city_picker.setMaxValue(Config.openCity.size() - 1);
        city_picker.setDisplayedValues(releaseCityName);
        for (int i = 0; i < city_picker.getChildCount(); i++) {
            city_picker.getChildAt(i).setFocusable(false);
        }
        city_picker.setWrapSelectorWheel(false);
        closeInputMethod(city_picker);
        builder.setNegativeButton("取消", null);
        builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cityWhich = city_picker.getValue();
                mOpenCity.setText(releaseCityName[city_picker.getValue()]);
                openCitySuccess = true;
                setButton();
            }
        });
        builder.create().show();
    }

    private void closeInputMethod(View v) {
        MLog.e("closeInputMethod", "hideSoftInputFromWindow");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        boolean isOpen = imm.isActive();
//        if (isOpen) {
//            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
//            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    Dialog dialog;

    @OnClick(R.id.year_root)
    public void yearClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setSingleChoiceItems(years, yearWhich, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                yearWhich = which;
                mYear.setText(years[yearWhich]);
                dialog.dismiss();
                yearSuccess = true;
                setButton();
            }
        });
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    @OnClick(R.id.gearbox_root)
    public void gearClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.gearbox_items), gearboxWhich, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gearboxWhich = which;
                mGearbox.setText(getResources().getStringArray(R.array.gearbox_items)[gearboxWhich]);
                dialog.dismiss();
                gearboxSuccess = true;
                setButton();
            }
        });
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public static boolean isAcronym(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    String[] years = new String[11];
    Calendar calendar;
    int yearWhich = -1;
    int gearboxWhich = -1;
    int cityWhich = -1;
    public String s_brand, s_xinghao;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        context = this;
        Config.setActivityState(this);
        setContentView(R.layout.addcar_brand);
        ButterKnife.inject(this);
        releaseShortNameCity = Config.cityShortName;
        releaseCityName = new String[Config.openCity.size()];
//        for (int i = 0; i < Config.openCity.size(); i++) {
//            releaseShortNameCity[i] = Config.openCity.get(i).getShortName();
//        }
//        releaseCityName = new String[Config.openCity.size()];
        for (int i = 0; i < Config.openCity.size(); i++) {
            releaseCityName[i] = Config.openCity.get(i).getName();
        }

        citysp = getSharedPreferences("selectcity", Context.MODE_PRIVATE);
        userService = new UserService(this);
        setButton();

        calendar = Calendar.getInstance();
        for (int i = 0; i < years.length; i++) {
            years[i] = ((calendar.get(Calendar.YEAR) - i) + "年");
        }

        yearWhich = getIntent().getIntExtra("year", -1);
        cityWhich = getIntent().getIntExtra("cityIndex", -1);
        gearboxWhich = getIntent().getIntExtra("gearbox", -1);
        if (yearWhich != -1) {
            mYear.setText(years[yearWhich]);
            yearSuccess = true;
        }
        if (cityWhich != -1) {
            mOpenCity.setText(releaseCityName[cityWhich]);
            openCitySuccess = true;

        }
        if (gearboxWhich == 0) {
            mGearbox.setText("自动挡");
            gearboxSuccess = true;
        } else if (gearboxWhich == 1) {
            mGearbox.setText("手动挡");
            gearboxSuccess = true;
        }
        if (getIntent().hasExtra("brand") && getIntent().hasExtra("xinghao")) {
            s_brand = getIntent().getStringExtra("brand");
            s_xinghao = getIntent().getStringExtra("xinghao");
            brand.setText(getIntent().getExtras().getString("brand", "") + getIntent().getExtras().getString("xinghao", ""));
            brandSuccess = true;
        }
        mCheckbox.setChecked(true);
        mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setButton();
            }
        });

        mOwnerXieYi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, URLWebView.class);
                intent.putExtra("url", ServerMutualConfig.owner_help);
                intent.putExtra(SysConfig.TITLE, "车主服务协议");
                AddCarBrandActivity.this.startActivityForResult(intent, 165);
            }
        });

        String regEx = "([a-zA-Z0-9])";
        final Pattern pattern = Pattern.compile(regEx);

        num.setTransformationMethod(new AllCapTransformationMethod());
//        num.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//                if (s.length() == 5) {
//                    numSuccess = true;
//                } else {
//                    numSuccess = false;
//                }
//                setButton();
//
//            }
//        });
        num.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                MLog.e("afterTextChanged", s.length() + "");
                if (s.length() == 5) {
                    numSuccess = true;
                    for (int i = 0; i < 5; i++) {
                        char c = s.charAt(i);
                        Matcher matcher = pattern.matcher(String.valueOf(c));
                        MLog.e("afterTextChanged", String.valueOf(c) + "");
                        if (!matcher.find()) {
                            numSuccess = false;
                        }
                    }
                } else {
                    numSuccess = false;
                }
                setButton();
                MLog.e("afterTextChanged", numSuccess + "");
            }
        });

        if (getIntent().hasExtra("num") && getIntent().getStringExtra("num") != null) {
            num.setText(getIntent().getStringExtra("num"));
        }
        setButton();
    }

    public boolean isChineseChar(String str) {
        boolean temp = false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            temp = true;
        }
        return temp;
    }

    @OnClick(R.id.applyService)
    public void applyServiceClick() {

        if (isChineseChar(num.getText().toString())) {
            showToast("车牌号不能输入中文");
            return;
        }
        if (num.getText().toString().indexOf(" ") != -1) {
            showToast("车牌号不能输入空格");
            return;
        }
        if (num.getText().toString().length() < 5) {
            showToast("请填写车牌号");
            return;
        }

        Builder builder = new Builder(context);
        builder.setMessage(getString(R.string.applyservice_dialog));
        builder.setNegativeButton("取消", null);
        builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Config.showProgressDialog(context, false, null);
//                User userModel = Config.getUser(AddCarBrandActivity.this);
//                String brand_xinghao[] = brand.getText().toString().split(" ");
                String cityStr = city.getText().toString();
                String cityResult = cityStr.replaceAll(" ", "");
                String cityCode = "";

                for (int i = 0; i < Config.openCity.size(); i++) {
                    if (Config.openCity.get(i).getName().indexOf(citysp.getString("city", "北京")) != -1) {
                        cityCode = Config.openCity.get(i).getCityId();
                        break;
                    }
                }

                CarInterface.AddCarWithAssistance.Request.Builder request = CarInterface.AddCarWithAssistance.Request.newBuilder();
                request.setLicensePlate(cityResult + num.getText().toString());// 汽车牌照
                request.setBrand(s_brand);// 汽车品牌
                request.setCarModel(s_xinghao);// 汽车型号
                request.setCityId(cityCode);// cityId
//                request.setCarId(); // 车辆id，有就传
                NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.AddCarWithAssistance_VALUE);
                networkTask.setBusiData(request.build().toByteArray());
                NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
                    @Override
                    public void onSuccessResponse(UUResponseData response) {
                        if (response.getRet() == 0) {
                            try {
                                CarInterface.AddCarWithAssistance.Response data = CarInterface.AddCarWithAssistance.Response.parseFrom(response.getBusiData());
                                if (data.getRet() == 0) {

                                    User user = Config.getUser(context);
                                    if (user.carStatus.equals("1")) {
                                        user.carStatus = "2";
                                        userService.modifyModel(user);
                                    }
                                    startActivityForResult(new Intent(context, WaitApplyServiceActivity.class), GOTO_WAIT_APPLY_SERVICE);
                                    finish();
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
//                startActivity(new Intent(context, WaitApplyServiceActivity.class));
            }
        });
        builder.create().show();
    }

    @OnClick(R.id.releasecar)
    public void releaseCarClick() {

        if (isChineseChar(num.getText().toString())) {
            showToast("车牌号不能输入中文");
            return;
        }
        if (num.getText().toString().indexOf(" ") != -1) {
            showToast("车牌号不能输入空格");
            return;
        }
        SharedPreferences sp = context.getSharedPreferences("car", Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putString("city", city.getText().toString());
        edit.putString("number", num.getText().toString());
        edit.putString("brand", s_brand);
        edit.putString("xinghao", s_xinghao);
        edit.commit();
        Config.getCar(context, city.getText().toString(), num.getText().toString().trim()).put(Car.KEY_CITY, city.getText().toString());
        Config.getCar(context, city.getText().toString(), num.getText().toString().trim()).put(Car.KEY_NUMBER, num.getText().toString());
        Config.getCar(context, city.getText().toString(), num.getText().toString().trim()).put("brand", s_brand);
        Config.getCar(context, city.getText().toString(), num.getText().toString().trim()).put("xinghao", s_xinghao);
        Config.showProgressDialog(context, false, null);

        final String pinpai = s_brand;
        final String xinghao = s_xinghao;
        String cityStr = city.getText().toString();
//        String cityResult = cityStr.replaceAll(" ", "");
//        String cityCode = "";
//        for (int i = 0; i < Config.openCity.size(); i++) {
//            if (Config.openCity.get(i).getName().indexOf(citysp.getString("city", "北京")) != -1) {
//                cityCode = Config.openCity.get(i).getCityId();
//                break;
//            }
//        }
        CarInterface.AddCar.Request.Builder request = CarInterface.AddCar.Request.newBuilder();
        request.setLicensePlate(cityStr + num.getText().toString());// 汽车牌照
        request.setBrand(s_brand);// 汽车品牌
        request.setCarModel(s_xinghao);// 汽车型号
        request.setCityId(Config.openCity.get(cityWhich).getCityId());// cityId
        if (gearboxWhich == 0) {
            request.setTransmissionType(CarCommon.CarTransmissionType.AUTO);
        } else {
            request.setTransmissionType(CarCommon.CarTransmissionType.HAND);
        }
        request.setByYear(Integer.parseInt(years[yearWhich].substring(0, 4)));


//                request.setCarId(); // 车辆id，有就传
        NetworkTask networkTask = new NetworkTask(CmdCodeDef.CmdCode.AddCar_VALUE);
        networkTask.setBusiData(request.build().toByteArray());
        NetworkUtils.executeNetwork(networkTask, new HttpResponse.NetWorkResponse<UUResponseData>() {
            @Override
            public void onSuccessResponse(UUResponseData response) {
                if (response.getRet() == 0) {
                    try {
                        CarInterface.AddCar.Response data = CarInterface.AddCar.Response.parseFrom(response.getBusiData());
                        if (data.getRet() == 0) {
                            User user = Config.getUser(context);
                            if (user.carStatus.equals("1")) {
                                user.carStatus = "2";
                                userService.modifyModel(user);
                            }
                            Intent intent = new Intent();
                            intent.setClass(context, ReleaseCarActivity.class);
                            String cityStr = city.getText().toString();
                            String cityResult = cityStr.replaceAll(" ", "");
                            String plateNumber = cityResult + num.getText().toString();
                            intent.putExtra(SysConfig.PLATE_NUMBER, plateNumber);
                            intent.putExtra(SysConfig.CAR_SN, data.getCarId());
                            intent.putExtra(SysConfig.CAR_TYPE, pinpai);
                            intent.putExtra(SysConfig.CAR_NAME, xinghao);
                            intent.putExtra(SysConfig.CITY, "");
                            startActivity(intent);
                            finish();
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onError(VolleyError errorResponse) {
                MLog.e(tag, "error " + errorResponse.getMessage() + "    " + errorResponse.getLocalizedMessage());
                Config.showFiledToast(context);
            }

            @Override
            public void networkFinish() {
                Config.dismissProgress();
            }
        });
    }

    String[] releaseShortNameCity;
    String[] releaseCityName;

    @OnClick(R.id.city_root)
    public void cityClick() {
        Builder builder = new Builder(context);
        View view = getLayoutInflater().inflate(R.layout.car_num_title_dialog, null);
        builder.setView(view);
        final NumberPicker city_picker = (NumberPicker) view.findViewById(R.id.city);
        city_picker.setMinValue(0);
        city_picker.setMaxValue(releaseShortNameCity.length - 1);

        city_picker.setDisplayedValues(releaseShortNameCity);
        for (int i = 0; i < city_picker.getChildCount(); i++) {
            city_picker.getChildAt(i).setFocusable(false);
        }
        city_picker.setWrapSelectorWheel(false);
        final NumberPicker word = (NumberPicker) view.findViewById(R.id.word);
        word.setDisplayedValues(Config.word);
        word.setMinValue(0);
        word.setMaxValue(Config.word.length - 1);
        for (int i = 0; i < word.getChildCount(); i++) {
            word.getChildAt(i).setFocusable(false);
        }
        word.setWrapSelectorWheel(false);
        builder.setNegativeButton("取消", null);
        builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                city.setText(releaseShortNameCity[city_picker.getValue()] + " " + Config.word[word.getValue()]);
            }
        });
        builder.create().show();
    }

    @OnClick(R.id.brand_root)
    public void brandClick() {
        startActivityForResult(new Intent(context, BrandActivity.class), BRAND_RESULT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_car_brand, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        } else if (item.getItemId() == R.id.calculate) {
            Intent intent = new Intent(context, CalculatePriceActivity.class);
            intent.putExtra("cityIndex", cityWhich);
            intent.putExtra("brand", s_brand);
            intent.putExtra("xinghao", s_xinghao);
            intent.putExtra("year", yearWhich);
            intent.putExtra("gearbox", gearboxWhich);
            intent.putExtra("num", num.getText().toString());
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("fromCalculate", false)) {
            finish();
        } else {

            Intent intent = new Intent(this, MainActivityTab.class);
            intent.putExtra("goto", MainActivityTab.GOTO_OWNER_CAR_MANAGER);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BRAND_RESULT) {
                String brandStr = data.getStringExtra("brand");
                if (brandStr != null && brandStr.indexOf("任意") != -1) {
                    brandSuccess = false;
                    setButton();
                    return;
                }
                s_brand = data.getStringExtra("brand");
                s_xinghao = data.getStringExtra("xinghao");
                brand.setText(data.getStringExtra("brand") + " " + data.getStringExtra("xinghao"));
                brandSuccess = true;
                setButton();
            }
            if (requestCode == GOTO_WAIT_APPLY_SERVICE) {
                String fragmentStr = data.getStringExtra(SysConfig.UPDATE_FRAGMENT);
                Intent intent = new Intent();
                intent.putExtra(SysConfig.UPDATE_FRAGMENT, fragmentStr);
                AddCarBrandActivity.this.setResult(RESULT_OK, intent);
                AddCarBrandActivity.this.finish();
            }
        }
        if (requestCode == SysConfig.RELEASE_WAIT_SERVICE) {
            String fragmentStr = data.getStringExtra(SysConfig.UPDATE_FRAGMENT);
            Intent intent = new Intent();
            intent.putExtra(SysConfig.UPDATE_FRAGMENT, fragmentStr);
            AddCarBrandActivity.this.setResult(RESULT_OK, intent);
            AddCarBrandActivity.this.finish();
        }
    }

    public void setButton() {
        if (brandSuccess && numSuccess && mCheckbox.isChecked() && gearboxSuccess && yearSuccess && openCitySuccess) {
            applyService.setEnabled(true);
            releaseCar.setEnabled(true);
        } else {
            applyService.setEnabled(false);
            releaseCar.setEnabled(false);
        }
    }

    class AllCapTransformationMethod extends ReplacementTransformationMethod {

        @Override
        protected char[] getOriginal() {
            char[] aa = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
            return aa;
        }

        @Override
        protected char[] getReplacement() {
            char[] cc = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
            return cc;
        }

    }
}
