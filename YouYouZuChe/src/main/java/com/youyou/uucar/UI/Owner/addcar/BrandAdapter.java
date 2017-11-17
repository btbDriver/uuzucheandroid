package com.youyou.uucar.UI.Owner.addcar;

import java.util.List;

import org.json.JSONException;

import com.youyou.uucar.DB.Model.CarBrandModel;
import com.youyou.uucar.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class BrandAdapter extends BaseAdapter implements SectionIndexer
{
    private List<CarBrandModel> list = null;
    private Activity mContext;

    public BrandAdapter(Activity context, List<CarBrandModel> list)
    {
        this.mContext = context;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<CarBrandModel> list)
    {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount()
    {
        return this.list.size();
    }

    public Object getItem(int position)
    {
        return list.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        TextView tvLetter;
        TextView tvTitle = null;
        final CarBrandModel mContent = list.get(position);
        view = LayoutInflater.from(mContext).inflate(R.layout.brand_item, null);
        tvTitle = (TextView) view.findViewById(R.id.title);
        tvLetter = (TextView) view.findViewById(R.id.catalog);
        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section))
        {
            tvLetter.setVisibility(View.VISIBLE);
            tvLetter.setText(mContent.getSortLetters());
        }
        else
        {
            tvLetter.setVisibility(View.GONE);
        }
        tvTitle.setText(this.list.get(position).getName());
        return view;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position)
    {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section)
    {
        for (int i = 0; i < getCount(); i++)
        {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section)
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str)
    {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]"))
        {
            return sortStr;
        }
        else
        {
            return "#";
        }
    }

    @Override
    public Object[] getSections()
    {
        return null;
    }
}
