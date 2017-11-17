package com.youyou.uucar.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.youyou.uucar.R;

import java.util.List;

/**
 * Created by wangyi on 12/29/14.
 */
public class WindowUtil {

    public static void confirmOrCancel(Context ctx, String title, String content, String cancel, String confirm, final CallBack callback) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setPositiveButton(confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onConfirm();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onCancel();
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();

    }

    public static void singleChoiceList(Context ctx, String title, String[] items, final CallBack callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onItemChoice(which);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static interface CallBack {
        void onConfirm();

        void onCancel();

        void onItemChoice(int position);
    }


}
