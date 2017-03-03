package com.morphii.morphiisampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.morphii.sdk.BasicView;
import com.morphii.sdk.BasicViewConfiguration;
import com.morphii.sdk.Comment;
import com.morphii.sdk.Constants;
import com.morphii.sdk.MorphiiConfiguration;
import com.morphii.sdk.MorphiiSelectionView;
import com.morphii.sdk.MorphiiService;
import com.morphii.sdk.Options;
import com.morphii.sdk.Project;
import com.morphii.sdk.Target;
import com.morphii.sdk.User;

import java.util.HashMap;

import static com.morphii.sdk.MorphiiService.sharedInstance;

public class Tab2 extends AppCompatActivity implements BasicView.BasicViewDelegate, MorphiiSelectionView.MorphiiSelectionCallback{

    private RelativeLayout mSelectionViewLayout;
    private Target mTarget;
    private Project mProject;
    private Options mOptions;
    private User mUser;
    private TextView mTabThree;
    private TextView mTabOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab2);

        mSelectionViewLayout = (RelativeLayout)findViewById(R.id.view_layout);
        connectUi();
        setUpMorphiiView();
    }

    private void connectUi() {

        mTabThree = (TextView)findViewById(R.id.tab_three);
        mTabOne = (TextView)findViewById(R.id.tab_one);
        setListeners();
        setUpBasicViewConfiguration();
    }

    private void setListeners() {
        mTabOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Tab2.this, MainActivity.class);
                startActivity(i);
            }
        });
        mTabThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Tab2.this, Tab3.class);
                startActivity(i);
            }
        });
    }

    private void setUpMorphiiView() {
        // Morphii configuration
        final MorphiiConfiguration morphiiConfig = new MorphiiConfiguration(false);
        morphiiConfig.add("6202173599617384448", null, 1);
        morphiiConfig.add("6202184384670334976", null, 2);
        morphiiConfig.add("6202184384427065344", "Morphii-3", 3);
        morphiiConfig.add("6202173597625090048", "Morphii-4", 4);
        morphiiConfig.add("6202173597771890688", null, 5);
        morphiiConfig.add("6202173599499943936", null, 6);
        morphiiConfig.add("6202185110939238400", null, 7);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(Constants.TAG, "mSelectionViewLayout's height is: "+mSelectionViewLayout.getHeight());
                MorphiiSelectionView morphiiSelectionView = MorphiiService.sharedInstance().addSelectionView(Tab2.this, morphiiConfig, 1.0, mSelectionViewLayout.getHeight(), Tab2.this);
                mSelectionViewLayout.addView(morphiiSelectionView);
            }
        }, 100);
    }

    private void setUpBasicViewConfiguration(){
        // Create Project
        mProject = new Project("my-project-id", "My Project Description");
        sharedInstance().mProject = mProject;

        // Create Target
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("key1", "value-1");
        metaData.put("key2", "value-2");
        metaData.put("key3", "value-3");
        mTarget = new Target("my-target-id-2", "question", metaData);

        // Create User
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("email", "user@mailinator.com");
        properties.put("key2", "value-2");
        properties.put("key3", "value-3");
        mUser = new User("user-id", "external", properties);

        // Options configuration
        mOptions = new Options("test", 0.5);
    }

    @Override
    public void selectedMorphii(MorphiiConfiguration morphiiConfiguration) {
        DefaultApplication.mBasicViewConfiguration = new BasicViewConfiguration(morphiiConfiguration, mTarget, mProject, new Comment(true, false, 200, "Post", "Comment"), mOptions, mUser);
        Intent i = new Intent(Tab2.this, SelectedMorphiiActivity.class);
        startActivity(i);
    }

    @Override
    public void commentChange(String type, boolean commentRequiredValid, BasicView.BasicViewObject basicViewObject) {
        Log.d(Constants.TAG, "Event type: " + type);
        Log.d(Constants.TAG, "viewId: " + basicViewObject.id());
        if (basicViewObject.comment() != null) {
            Log.d(Constants.TAG, "comment required: " + basicViewObject.comment().required());
            Log.d(Constants.TAG, "comment length: " + basicViewObject.comment().length());
            Log.d(Constants.TAG, "comment value: " + basicViewObject.comment().value());
        }
    }
}
