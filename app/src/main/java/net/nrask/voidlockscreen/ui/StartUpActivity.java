package net.nrask.voidlockscreen.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.nrask.voidlockscreen.ApplicationSettings;
import net.nrask.voidlockscreen.R;

public class StartUpActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_up);

		Class act = ApplicationSettings.getInstance().isSetupCompleted()
				? LockscreenActivity.class
				: WelcomeActivity.class;

		startActivity(new Intent(getBaseContext(), act));
	}
}
