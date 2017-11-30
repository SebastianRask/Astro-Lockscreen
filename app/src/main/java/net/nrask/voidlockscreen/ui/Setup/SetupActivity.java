package net.nrask.voidlockscreen.ui.Setup;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import net.nrask.voidlockscreen.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.admin.DevicePolicyManager.ACTION_SET_NEW_PASSWORD;

public class SetupActivity extends AppCompatActivity implements SetupStepAdapter.Callback {
    private final int STEP_NOTIFICATIONS = 0,
            STEP_DISABLE_SYSTEM_LOCK = 1,
            STEP_DATA_ACCESS = 2;

    @BindView(R.id.steps_recyclerview)
    RecyclerView mStepsRecyclerView;

    private SetupStepAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        ButterKnife.bind(this);

        mAdapter = new SetupStepAdapter(constructSteps(), this);
        mStepsRecyclerView.setAdapter(mAdapter);
        mStepsRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case STEP_NOTIFICATIONS:
                break;
            case STEP_DISABLE_SYSTEM_LOCK:
                break;
            case STEP_DATA_ACCESS:
                break;
        }
    }

    @Override
    public void onStepClicked(SetupStep step) {
        mAdapter.nextStep();

//        try {
//            switch (step.getId()) {
//                case STEP_NOTIFICATIONS:
//                    showGiveNotificationAccessActivity();
//                    break;
//                case STEP_DISABLE_SYSTEM_LOCK:
//                    showDisableLockscreenActivity();
//                    break;
//                case STEP_DATA_ACCESS:
//                    showDataAccessActivity();
//                    break;
//                default:
//                    break;
//            }
//        } catch (ActivityNotFoundException e) {
//            FirebaseCrash.report(e);
//        }

    }

    private List<SetupStep> constructSteps() {
        return new ArrayList<>(Arrays.asList(
                new SetupStep(STEP_NOTIFICATIONS, "", ""),
                new SetupStep(STEP_DISABLE_SYSTEM_LOCK, "", ""),
                new SetupStep(STEP_DATA_ACCESS, "", "")
        ));
    }

    public void showGiveNotificationAccessActivity() throws ActivityNotFoundException {
        startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), STEP_NOTIFICATIONS);
    }

    public void showDisableLockscreenActivity() throws ActivityNotFoundException {
        startActivityForResult(new Intent(ACTION_SET_NEW_PASSWORD), STEP_DISABLE_SYSTEM_LOCK);
    }

    public void showDataAccessActivity() throws ActivityNotFoundException {
        startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), STEP_DATA_ACCESS);
    }


}
