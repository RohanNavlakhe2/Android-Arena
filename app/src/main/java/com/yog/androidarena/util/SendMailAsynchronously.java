package com.yog.androidarena.util;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.yog.androidarena.java_mail_api.GMailSender;

import timber.log.Timber;

public class SendMailAsynchronously extends AsyncTask<String,String,String>
{
    private Context context;
    public SendMailAsynchronously(Context context)
    {
        this.context=context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context, "Wait We're sending mail", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... strings) {


        try {
            GMailSender sender = new GMailSender("anroidartsdevelopers@gmail.com",
                    "supercoder2@");
            sender.sendMail(strings[0], strings[1],
                    "anroidartsdevelopers@gmail.com",strings[2]);
            return "Email Successfully sent";
        } catch (Exception e) {
            Timber.e(e);
            return "Email Send failed";
        }




    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }
}
