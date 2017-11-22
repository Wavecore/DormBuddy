package com.cs477.dormbuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CycleSettingsActivity extends AppCompatActivity {
    private enum washerSoil {LIGHT, MEDIUM, HEAVY, SMALL_LOAD};
    private enum washerCycle {NORMAL, PERM_PRESS, DELICATES};
    private enum washerTemp {HOT, WARM, COLD};
    private enum dryerTemp {HIGH, MED, LOW, DELICATES, NO_HEAT};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle_settings);
    }
}
