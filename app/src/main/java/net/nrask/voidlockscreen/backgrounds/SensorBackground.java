package net.nrask.voidlockscreen.backgrounds;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.helpers.SRJHelper;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 23/12/16.
 */

public class SensorBackground extends LockscreenBackground implements SensorEventListener {
	private SensorManager sensorManager;

	public static int x;
	public static int y;

	private View backgroundImageContainer;
	private ImageView backgroundImage;
	//private HorizontalScrollView scrollView;


	public SensorBackground(Activity activity, RelativeLayout lockscreenContainer) {
		super(activity, lockscreenContainer);
		sensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);

		// Register this class as a listener for the accelerometer sensor
		//sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

		View.inflate(activity, R.layout.background_sensor_parallax, lockscreenContainer);
		backgroundImage = (ImageView) lockscreenContainer.findViewById(R.id.background_image);
		//scrollView = (HorizontalScrollView) lockscreenContainer.findViewById(R.id.scrollView);

		backgroundImage.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.phonewallpaper));
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			x -= (int) event.values[0];

			//scrollView.scrollBy(x, 0);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {}

	public class CustomDrawableView extends ImageView {

		public CustomDrawableView(Context context) {
			super(context);
		}

		protected void onDraw(Canvas canvas) {
			invalidate();
		}
	}
}
