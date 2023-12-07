package com.yog.androidarena.util;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class Utils {
     public static void showNoInternetBox(Context context)
     {
         AlertDialog.Builder builder = new AlertDialog.Builder(context);
         builder.setTitle("No Internet")
                 .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                         if(!General.INSTANCE.hasInternetConnection(context))
                             showNoInternetBox(context);

                     }
                 })
                  .setCancelable(false);

         //Creating dialog box
         AlertDialog alert = builder.create();
         alert.show();
     }
}
