package com.youyou.uucar.UI.Owner.addcar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.youyou.uucar.R;
import com.youyou.uucar.UI.Main.MainActivityTab;
import com.youyou.uucar.UUAppCar;

/**
 * Created by taurusxi on 14-7-18.
 */
public class FinishAddCarActivity extends Activity {
    private Context context;
    private TextView goToFindCarTv, continueReleaseCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        UUAppCar.getInstance().addActivity(this);
        context = this;
        setContentView(R.layout.finish_add_car_info);
        goToFindCarTv = (TextView) findViewById(R.id.go_find_car);
        goToFindCarTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivityTab.class);
                intent.putExtra("goto", MainActivityTab.GOTO_OWNER_CAR_MANAGER);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                FinishAddCarActivity.this.finish();

            }
        });

        continueReleaseCar = (TextView) findViewById(R.id.continue_release_car);
        continueReleaseCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddCarBrandActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
                FinishAddCarActivity.this.finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
    }
}
