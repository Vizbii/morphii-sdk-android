package com.morphii.morphiisampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.morphii.sdk.BasicView;
import com.morphii.sdk.Constants;
import com.morphii.sdk.MorphiiService;
import com.morphii.sdk.ReactionResultRecord;
import com.morphii.sdk.ReactionService;

import java.util.ArrayList;

public class SelectedMorphiiActivity extends AppCompatActivity implements BasicView.BasicViewDelegate{

    private ImageView mBackButton;
    private RelativeLayout mMorphiiView;
    private TextView mSubmitButton;
    private BasicView mBasicView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_morphii);

        connectUi();
    }

    private void connectUi() {
        mBackButton = (ImageView)findViewById(R.id.back_button);
        mMorphiiView = (RelativeLayout)findViewById(R.id.basic_view_bottom);
        mSubmitButton = (TextView)findViewById(R.id.submit_button);
        setListeners();
        setUpMorphiiView();
    }

    private void setListeners() {
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasicView.submit(mBasicView, new ReactionService.ReactionRequestCallback() {
                    @Override
                    public void reactionsSubmitted(ArrayList<ReactionResultRecord> arrayList) {
                        printReactionResults(arrayList);
                        ReactionResultRecord resultRecord = arrayList.get(0);
                        if (resultRecord.isSubmitted() && resultRecord.error() == null) {
                            Toast.makeText(SelectedMorphiiActivity.this, "Morphii was successfully submitted!", Toast.LENGTH_LONG).show();
                        }
                        else if (!resultRecord.isSubmitted() || resultRecord.error() != null) {
                            Toast.makeText(SelectedMorphiiActivity.this, "Error submitting Morphii. Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setUpMorphiiView(){
        mBasicView = MorphiiService.sharedInstance().add(DefaultApplication.mBasicViewConfiguration, SelectedMorphiiActivity.this, this);
        mMorphiiView.addView(mBasicView);
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
}
