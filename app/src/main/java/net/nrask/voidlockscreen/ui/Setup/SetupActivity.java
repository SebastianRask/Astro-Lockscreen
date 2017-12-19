package net.nrask.voidlockscreen.ui.setup;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.crash.FirebaseCrash;

import net.nrask.voidlockscreen.ApplicationSettings;
import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.ui.LockscreenActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.admin.DevicePolicyManager.ACTION_SET_NEW_PASSWORD;

public class SetupActivity extends AppCompatActivity implements net.nrask.voidlockscreen.ui.setup.SetupStepAdapter.Callback {
    private final int STEP_NOTIFICATIONS = 0,
            STEP_DISABLE_SYSTEM_LOCK = 1,
            STEP_DATA_ACCESS = 2;

    @BindView(R.id.steps_recyclerview)
    RecyclerView mStepsRecyclerView;

    private net.nrask.voidlockscreen.ui.setup.SetupStepAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        ButterKnife.bind(this);

        mAdapter = new net.nrask.voidlockscreen.ui.setup.SetupStepAdapter(constructSteps(), this);
        mStepsRecyclerView.setAdapter(mAdapter);
        mStepsRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mStepsRecyclerView.setItemAnimator(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAdapter.nextStep();

        switch (requestCode) {
            case STEP_DATA_ACCESS:
                completeSetup();
                break;
            default:
                break;
        }
    }

    @Override
    public void onStepClicked(SetupStep step) {
        try {
            switch (step.getId()) {
                case STEP_NOTIFICATIONS:
                    showGiveNotificationAccessActivity();
                    break;
                case STEP_DISABLE_SYSTEM_LOCK:
                    showDisableLockscreenActivity();
                    break;
                case STEP_DATA_ACCESS:
                    showDataAccessActivity();
                    break;
                default:
                    break;
            }
        } catch (ActivityNotFoundException e) {
            FirebaseCrash.report(e);
            mAdapter.nextStep();// Lets just get on with the next step
        }

    }

    private void completeSetup() {
        ApplicationSettings.getInstance().setSetupCompleted(true);
        startActivity(new Intent(getBaseContext(), LockscreenActivity.class));
        finish();
    }

    private List<SetupStep> constructSteps() {
        return new ArrayList<>(Arrays.asList(
                new SetupStep(STEP_NOTIFICATIONS, getString(R.string.setup_notification_title), getString(R.string.setup_notification_subtitle)),
                new SetupStep(STEP_DISABLE_SYSTEM_LOCK, getString(R.string.setup_system_lock_title), getString(R.string.setup_system_lock_subtitle)),
                new SetupStep(STEP_DATA_ACCESS, getString(R.string.setup_data_access_title), getString(R.string.setup_data_access_subtitle))
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
