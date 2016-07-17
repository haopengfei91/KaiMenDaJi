package com.faymax.kaimendaji;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by HPF on 2016/7/9.
 */
public class Util {

    private static AlertDialog alertDialog;

    public static View getView(Context context, int layoutId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(layoutId, null);
        return layout;
    }

    public static void startActivity(Context context, Class dest) {
        Intent intent = new Intent();
        intent.setClass(context, dest);
        context.startActivity(intent);

        ((Activity)context).finish();
    }

    public static void showDialog(final Context context, String message, final IAlertDialogButtonListener listener) {
        View dialogView = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_Transparent);
        dialogView = getView(context, R.layout.dialog);
        ImageButton btnOk = (ImageButton) dialogView.findViewById(R.id.id_btn_ok);
        ImageButton btnCancel = (ImageButton) dialogView.findViewById(R.id.id_btn_cancel);
        TextView tvMessage = (TextView) dialogView.findViewById(R.id.id_tv_message);
        tvMessage.setText(message);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertDialog != null) {
                    alertDialog.cancel();
                }
                if (listener != null) {
                    listener.onClick();
                }
                MyPlayer.playTone(context, Const.INDEX_TONE_ENTER);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertDialog != null) {
                    alertDialog.cancel();
                }
                MyPlayer.playTone(context, Const.INDEX_TONE_CANCEL);
            }
        });
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * 数据保存
     * @param context
     * @param stageIndex
     * @param coins
     */
    public static void saveData(Context context, int stageIndex, int coins) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(Const.FILE_NAME_SAVE, Context.MODE_PRIVATE);
            DataOutputStream dos = new DataOutputStream(fos);

            dos.writeInt(stageIndex);
            dos.writeInt(coins);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取数据
     * @param context
     * @return
     */
    public static int[] loadData(Context context) {
        FileInputStream fis = null;
        int[] datas = {-1, Const.TOTAL_COINS};
        try {
            fis = context.openFileInput(Const.FILE_NAME_SAVE);
            DataInputStream dis = new DataInputStream(fis);
            datas[Const.INDEX_LOAD_STAGE] = dis.readInt();
            datas[Const.INDEX_LOAD_COINS] = dis.readInt();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return datas;
    }

}

