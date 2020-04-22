package com.yog.androidarena;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yog.androidarena.adapter.LinkAdapter;
import com.yog.androidarena.adapter.SomeMoreThingsAdapter;
import com.yog.androidarena.databinding.ActivityLibExpansionBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class LibExpansionActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityLibExpansionBinding activityLibExpansionBinding;
    private String libraryName;
    private HashMap<String,Object> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLibExpansionBinding= DataBindingUtil.setContentView(this,R.layout.activity_lib_expansion);
        //on Click
        /*activityLibExpansionBinding.sendGithubLink.setOnClickListener(this);
        activityLibExpansionBinding.sendLibraryLink.setOnClickListener(this);*/

        Timber.d("lib on create");

        map=(HashMap<String, Object>)getIntent().getSerializableExtra("map");
        //Log.i("cloudData",   " => " +map);

        //setting title
        /*map.get("0") contains Title of the library* */


        //activityLibExpansionBinding.includeToolbar.libNameTitle.setText(map.get("0").toString());

        //setting intro and subpoints(if any ex.(some more things,cons sections))
        /*map.get("1") contains List of What can Library get me*/

        //assert map != null;
        List introAndSectionsList= (List) map.get("1");
        /*introAndSectionsList.get(0) contains heading of section (Ex. Cons)*/
        /*introAndSectionsList.get(1) contains List of points inside (Ex. Cons)*/
        libraryName=map.get("0").toString();
        activityLibExpansionBinding.includeToolbar.libNameTitle.setText(libraryName);
        activityLibExpansionBinding.libDescTxt.setText(introAndSectionsList.get(0).toString());

        /*if introAndSectionsList.size()>1 means we have sub sections like (cons,someMoreThings)*/
        //introAndSectionsList[0] will always contain introduction to the lib

        if(introAndSectionsList.size()>1)
        {
            //means sections are available
            activityLibExpansionBinding.someMoreThingsRec.setVisibility(View.VISIBLE);
            activityLibExpansionBinding.someMoreThingsRec.setLayoutManager(new LinearLayoutManager(this));
            //In firebae cloud firestore we cannot create nested lists,that's why we're passing the complete
            //list to the adapter where we will remove the first elment from the list so remaining will be the
            //list o somemoresectioms.
            activityLibExpansionBinding.someMoreThingsRec.setAdapter(new SomeMoreThingsAdapter(this, (List<Map<String, Object>>) introAndSectionsList));
        }

        //How To Implement Section
        /*map.get("2") contains List of How to implement section*/
        /*activityLibExpansionBinding.howToImplementRec.setLayoutManager(new LinearLayoutManager(this));
        activityLibExpansionBinding.howToImplementRec.setAdapter(new Point_SnippetAdapter(this, (List) map.get("2")));*/


        //link section
        initLinkRec();
        /*//My Github Link
        *//*map.get("3") contains my github link*//*
        activityLibExpansionBinding.myGithubLink.setText(map.get("3").toString());

        //Library Link
        *//*map.get("4") contains Library github link*//*
        activityLibExpansionBinding.libraryLink.setText(map.get("4").toString());*/

        //Demo Img
        /*map.get("5") contains Demo img url*/
        Glide.with(this)
                .load(map.get("5").toString())
                //.placeholder(getResources().getDrawable(R.drawable.loading_gif))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //Log.i("img_res","Failed :"+e.getMessage());
                        Timber.d("Failed :"+e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //Log.i("img_res","ready");
                        Timber.d("ready");
                        return false;
                    }
                })
                .into(activityLibExpansionBinding.demoImg);

    }

    private void initLinkRec()
    {
        //map.get("3") contains List<String> of type of link (Library Link)ac
        //map.get("3") contains List<String> links
        //map.get("3") contains List<String> link Subject for mail
        if(!((List<String>)map.get("3")).get(0).equals("")) {
            Timber.i("linkRec");
            activityLibExpansionBinding.linkRec.setLayoutManager(new LinearLayoutManager(this));
            activityLibExpansionBinding.linkRec.setAdapter(new LinkAdapter(this, (List<String>) map.get("3"), (List<String>) map.get("4"), (List<String>) map.get("6")));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            /*case R.id.sendGithubLink:
                sendMail(libraryName+" Demo Project Link",String.valueOf(activityLibExpansionBinding.myGithubLink.getText()));
                break;
            case R.id.sendLibraryLink:
                sendMail(libraryName+" Library Link",String.valueOf(activityLibExpansionBinding.libraryLink.getText()));
                break;*/
        }
    }

    /*private void sendMail(String subject,String body)
    {
        SendMailAsynchronously sendMailAsynchronously=new SendMailAsynchronously();
        sendMailAsynchronously.execute(subject,body);
    }*/

    /*private class SendMailAsynchronously extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(LibExpansionActivity.this, "Wait We're sending mail", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                GMailSender sender = new GMailSender("anroidartsdevelopers@gmail.com",
                        "supercoder2@");
                sender.sendMail(strings[0], strings[1],
                        "anroidartsdevelopers@gmail.com", "rohannavlakhe2@gmail.com");
                return "Email Successfully sent";
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
                return "Email Send failed";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(LibExpansionActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }*/
}
