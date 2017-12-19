package net.nrask.voidlockscreen.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.helpers.SRJHelper;
import net.nrask.voidlockscreen.services.StartLockscreenService;
import net.nrask.voidlockscreen.ui.setup.SetupActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity implements TextWatcher {

	@BindView(R.id.btn_done)
	protected View mDoneButton;

	@BindView(R.id.input_name)
	protected EditText mNameInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		ButterKnife.bind(this);

		if (!SRJHelper.isServiceRunning(StartLockscreenService.class, getBaseContext())) {
			startService(new Intent(getBaseContext(), StartLockscreenService.class));
		}

		mNameInput.addTextChangedListener(this);
	}

	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		TransitionManager.beginDelayedTransition((ViewGroup) mDoneButton.getParent());
		mDoneButton.setVisibility(charSequence.length() > 0 ? View.VISIBLE : View.GONE);
	}

	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
	}

	@Override
	public void afterTextChanged(Editable editable) {
	}

	@OnClick(R.id.btn_done)
	public void onBtnDoneClicked() {
		startActivity(new Intent(getBaseContext(), SetupActivity.class));
	}
}
