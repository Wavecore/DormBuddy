package com.cs477.dormbuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CycleSettingsActivity extends AppCompatActivity {
    public enum WASHER_SOIL {LIGHT, MEDIUM, HEAVY, SMALL_LOAD};
    public enum WASHER_CYCLE {NORMAL, PERM_PRESS, DELICATES};
    public enum WASHER_TEMPERATURE {HOT, WARM, COLD};
    public enum WASHER_SMALL_LOAD {YES, NO};
    public enum DRYER_TEMPERATURE {HIGH, MEDIUM, LOW, DELICATES, NO_HEAT};
    public enum DRYER_DELICATES {YES, NO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle_settings);
    }

    public void onAddPreferenceClicked(View v) {
        AddPreferenceFragment newFragment = AddPreferenceFragment.newInstance();
        newFragment.show(getSupportFragmentManager(),"AddPreferenceFragment");
    }
}
