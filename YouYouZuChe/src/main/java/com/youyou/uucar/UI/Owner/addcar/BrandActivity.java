package com.youyou.uucar.UI.Owner.addcar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youyou.uucar.DB.Model.CarBrandModel;
import com.youyou.uucar.R;
import com.youyou.uucar.UI.Common.BaseActivity;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.silent.handle.CharacterParser;
import com.youyou.uucar.Utils.silent.handle.PinyinComparator;
import com.youyou.uucar.Utils.silent.handle.SideBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BrandActivity extends BaseActivity {
    public static final String TAG = BrandActivity.class.getSimpleName();
    private ListView sortListView;
    private TextView dialog;
    private BrandAdapter adapter;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<CarBrandModel> SourceDateList;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    RelativeLayout brand_root, xinghao_root;
    ListView xinghao;
    List<XinghaoItem> xinghaodata = new ArrayList<XinghaoItem>();
    XinghaoAdapter xinghaoadapter;
    public String temp_brand;
    public String temp_xinghao;
    private SideBar sideBar;
    private boolean clicked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brand);
        brand_root = (RelativeLayout) findViewById(R.id.brand_root);
        xinghao_root = (RelativeLayout) findViewById(R.id.xinghao_root);
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }
            }
        });
        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                final int position;
//                if (getIntent().getBooleanExtra("isFilter", false)) {
                position = pos;
//                } else {
//                    position = pos + 1;
//                }
                if (position == 0 && getIntent().getBooleanExtra("isFilter", false)) {
                    Intent intent = new Intent();
                    intent.putExtra("brand", "任意");
                    intent.putExtra("xinghao", "任意");
                    setResult(RESULT_OK, intent);
                    finish();
                    return;
                }
                brand_root.setVisibility(View.GONE);
                try {
                    clicked = true;
                    xinghaodata.clear();
                    brand = SourceDateList.get(position).getName();
                    if (getIntent().getBooleanExtra("isFilter", false)) {
                        XinghaoItem item2 = new XinghaoItem();
                        item2.name = "任意";
                        xinghaodata.add(item2);
                    }
                    int position_for = 0;
                    if (getIntent().getBooleanExtra("isFilter", false)) {
                        position_for = position - 1;
                    } else {
                        position_for = position;
                    }
                    for (int i = 0; i < brand_json.getJSONArray("content").getJSONObject(position_for).getJSONArray("content").length(); i++) {
                        XinghaoItem item = new XinghaoItem();
                        item.name = brand_json.getJSONArray("content").getJSONObject(position_for).getJSONArray("content").getJSONObject(i).getString("xinghao");
                        xinghaodata.add(item);
                    }
                    temp_brand = ((CarBrandModel) adapter.getItem(position_for)).getName();
                    xinghaoadapter.notifyDataSetChanged();
                    xinghao_root.setVisibility(View.VISIBLE);
                    setTitle(brand);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        readTxt();
        try {
            SourceDateList = filledData(brand_json.getJSONArray("content"));
            if (SourceDateList != null) {
                if (getIntent().getBooleanExtra("isFilter", false)) {

                    CarBrandModel allCarBrandModel = new CarBrandModel();
                    allCarBrandModel.setName("任意");
                    allCarBrandModel.setSortLetters("任意");
                    SourceDateList.add(0, allCarBrandModel);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new BrandAdapter(context, SourceDateList);
        sortListView.setAdapter(adapter);
        xinghao = (ListView) findViewById(R.id.xinghao);
        xinghaoadapter = new XinghaoAdapter();
        xinghao.setAdapter(xinghaoadapter);
    }

    public String brand, xh;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            onBackPressed();
            return false;
        }
        return true;
    }

    /**
     * 为ListView填充数据
     *
     * @return
     */
    private List<CarBrandModel> filledData(JSONArray json) {
        try {
            List<CarBrandModel> mSortList = new ArrayList<CarBrandModel>();
            for (int i = 0; i < json.length(); i++) {
                CarBrandModel sortModel = new CarBrandModel();
                sortModel.setName(json.getJSONObject(i).getString("name"));
                // 汉字转换成拼音
                String pinyin = characterParser.getSelling(json.getJSONObject(i).getString("index"));
                String sortString = pinyin.substring(0, 1).toUpperCase();
                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    sortModel.setSortLetters(sortString.toUpperCase());
                } else {
                    sortModel.setSortLetters("#");
                }
                mSortList.add(sortModel);
            }
            return mSortList;
        } catch (Exception e) {
        }
        return null;
    }

    JSONObject brand_json;

    public void readTxt() {
        try {
            SharedPreferences sp = context.getSharedPreferences("brand", Context.MODE_PRIVATE);
            brand_json = new JSONObject(sp.getString("brand", new JSONObject().toString()));
        } catch (Exception e) {
            MLog.e(tag, "readText error = " + e.getMessage());
            e.printStackTrace();
        }
    }

    public class XinghaoAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return xinghaodata.size();
        }

        @Override
        public Object getItem(int position) {
            return xinghaodata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = context.getLayoutInflater().inflate(R.layout.brand_xinghao_item, null);
            final TextView name = (TextView) convertView.findViewById(R.id.title);
            name.setText(xinghaodata.get(position).name);
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    temp_xinghao = xinghaodata.get(position).name;
                    // brand_root.setVisibility(View.VISIBLE);
                    // xinghao_root.setVisibility(View.GONE);
                    // adapter.notifyDataSetChanged();
                    Intent intent = new Intent();
                    intent.putExtra("brand", brand);
                    intent.putExtra("xinghao", xinghaodata.get(position).name);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            return convertView;
        }
    }

    public class XinghaoItem {
        public String name = "";
    }

    @Override
    public void onBackPressed() {
        if (clicked) {
            brand_root.setVisibility(View.VISIBLE);
            clicked = false;
            xinghao_root.setVisibility(View.GONE);
            setTitle("品牌车型");
        } else {
            finish();
        }
    }
}
