package com.youyou.uucar.UI.Renter.carinfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.youyou.uucar.R;
import com.youyou.uucar.UUAppCar;
import com.youyou.uucar.Utils.ImageView.BaseNetworkImageView;

/**
 * Created by taurusxi on 14-7-10.
 */
public class ImageFragment extends Fragment {

    private String imageStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        imageStr = bundle.getString("Image");
        BaseNetworkImageView imageView = new BaseNetworkImageView(getActivity());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        UUAppCar.getInstance().display(imageStr, imageView, R.drawable.add_car_youhou);
        return imageView;
    }

    public static Fragment newInstance(String image) {
        ImageFragment fragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Image", image);
        fragment.setArguments(bundle);
        return fragment;
    }
}
