package com.morphii.morphiisampleapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.morphii.sdk.AuthenticationResults;
import com.morphii.sdk.BasicView;
import com.morphii.sdk.BasicViewConfiguration;
import com.morphii.sdk.Comment;
import com.morphii.sdk.Constants;
import com.morphii.sdk.MorphiiConfiguration;
import com.morphii.sdk.MorphiiSelectionView;
import com.morphii.sdk.MorphiiService;
import com.morphii.sdk.Options;
import com.morphii.sdk.Project;
import com.morphii.sdk.ReactionResultRecord;
import com.morphii.sdk.ReactionService;
import com.morphii.sdk.Target;
import com.morphii.sdk.User;

import java.util.ArrayList;
import java.util.HashMap;

import static com.morphii.sdk.MorphiiService.sharedInstance;

public class MainActivity extends AppCompatActivity implements BasicView.BasicViewDelegate, MorphiiSelectionView.MorphiiSelectionCallback {
    private BasicView mBasicView;
    private Target mTarget;
    private Project mProject;
    private Options mOptions;
    private User mUser;
    private RelativeLayout mCommentContainer;
    private BasicView mCommentBasicView;
    private RelativeLayout mSelectionViewLayout;
    private Button mResetButton;
    private Button mSubmitButton;
    private Button mPngButton;
    private ImageView mPngImageView;
    private TextView mTabTwo;
    private TextView mTabThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectUi();

        sharedInstance().setUp(MainActivity.this);
        sharedInstance().authenticate("<user-name>", "<user-password>", "<account-id>", new MorphiiService.AuthenticationCallback() {
            @Override
            public void authenticated(AuthenticationResults authenticationResults) {
                if (authenticationResults.isAuthenticated()) {
                    addBasicView();
                }
                else {
                    Log.d(Constants.TAG, "Authentication failed");
                    Log.d(Constants.TAG, "error code: " + authenticationResults.error().code());
                    Log.d(Constants.TAG, "error message: " + authenticationResults.error().message());
                }
            }
        });
    }

    private void connectUi() {
        mCommentContainer = (RelativeLayout)findViewById(R.id.basic_view_bottom);
        mSelectionViewLayout = (RelativeLayout) findViewById(R.id.selection_view_layout);
        mResetButton = (Button)findViewById(R.id.reset_button);
        mSubmitButton = (Button)findViewById(R.id.submit_button);
        mPngImageView = (ImageView)findViewById(R.id.png_image);
        mPngButton = (Button)findViewById(R.id.get_png_button);
        mTabTwo = (TextView)findViewById(R.id.tab_two);
        mTabThree = (TextView)findViewById(R.id.tab_three);
        setListeners();
    }

    private void setListeners() {
        mTabTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentContainer.removeAllViews();
                MorphiiService.sharedInstance().resetAll(true);
                Intent i = new Intent(MainActivity.this, Tab2.class);
                startActivity(i);
            }
        });
        mTabThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Tab3.class);
                startActivity(i);
            }
        });
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MorphiiService.sharedInstance().resetAll(true);
            }
        });
        mPngButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap pngBitmap = mCommentBasicView.png();
                mPngImageView.setImageBitmap(pngBitmap);
            }
        });
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasicView.submit(mCommentBasicView, new ReactionService.ReactionRequestCallback() {
                    @Override
                    public void reactionsSubmitted(ArrayList<ReactionResultRecord> arrayList) {
                        printReactionResults(arrayList);
                        if (arrayList.size() > 0) {
                            ReactionResultRecord resultRecord = arrayList.get(0);
                            if (resultRecord.isSubmitted()) {
                                Toast.makeText(MainActivity.this, "Morphii was successfully submitted!", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(MainActivity.this, "There was an error submitting the morphii. Please try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
    }

    private void printReactionResults(ArrayList<ReactionResultRecord> records) {
        for (ReactionResultRecord resultRecord:records) {
            Log.d(Constants.TAG, "is submitted: " + resultRecord.isSubmitted());
            Log.d(Constants.TAG, "view id: " + resultRecord.viewId());
            Log.d(Constants.TAG, "target id: " + resultRecord.targetId());
            if (resultRecord.isSubmitted()) {
                Log.d(Constants.TAG, "reaction id: " + resultRecord.reactionId());
                if (resultRecord.morphii() != null) {
                    Log.d(Constants.TAG, "morphii id: " + resultRecord.morphii().id());
                    Log.d(Constants.TAG, "morphii name: " + resultRecord.morphii().name());
                    Log.d(Constants.TAG, "morphii display name: " + resultRecord.morphii().displayName());
                    Log.d(Constants.TAG, "morphii intensity: " + resultRecord.morphii().intensity());
                    Log.d(Constants.TAG, "morphii weight: " + resultRecord.morphii().weight());
                }

                if (resultRecord.comment() != null) {
                    Log.d(Constants.TAG, "comment text: " + resultRecord.comment().text());
                    Log.d(Constants.TAG, "comment locale: " + resultRecord.comment().locale());
                }
                else {
                    Log.d(Constants.TAG, "No comment data");
                }
            }
            else {
                Log.d(Constants.TAG, "Failed to submit data.");
            }
            Log.d(Constants.TAG, "=================================================");
        }
    }

    public void addBasicView() {
        // Morphii configuration
        MorphiiConfiguration morphiiConfig = new MorphiiConfiguration(true);
        morphiiConfig.add("6202173597872553984", null, 1);
        morphiiConfig.add("6202173599353143296", null, 2);
        morphiiConfig.add("6202184384594837504", null, 3);
        morphiiConfig.add("6202173597625090048", "Name-4", 4);
        morphiiConfig.add("6202173597771890688", null, 5);
        morphiiConfig.add("6202173599499943936", null, 6);
        morphiiConfig.add("6202185110939238400", null, 7);

        // Create a selection list view. Set initial intensity to 1.
        MorphiiSelectionView morphiiSelectionView = MorphiiService.sharedInstance().addSelectionView(MainActivity.this, morphiiConfig, 1.0, mSelectionViewLayout.getHeight(), this);
        mSelectionViewLayout.addView(morphiiSelectionView);
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

    @Override
    public void selectedMorphii(MorphiiConfiguration morphiiConfiguration) {
        Log.d(Constants.TAG, "SELECTED_MORPHII_CONFIGURATION: "+morphiiConfiguration);
        // Create Project
        mProject = new Project("my-project-id", "My Project Description");
        sharedInstance().mProject = mProject;

        // Create Target
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("key1", "value-1");
        metaData.put("key2", "value-2");
        metaData.put("key3", "value-3");
        mTarget = new Target("my-target-id-1", "question", metaData);

        // Create User
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("email", "user@mailinator.com");
        properties.put("key2", "value-2");
        properties.put("key3", "value-3");
        mUser = new User("user-id", "external", properties);

        // Options configuration
        mOptions = new Options("test", 0.5);
        mCommentContainer.removeAllViews();
        BasicViewConfiguration commentConfig = new BasicViewConfiguration(morphiiConfiguration, mTarget, mProject, new Comment(true, false, 200, "Post", "Comment"), mOptions, mUser);
        mCommentBasicView = MorphiiService.sharedInstance().add(commentConfig, this, this);
        mCommentContainer.addView(mCommentBasicView);
    }
}
