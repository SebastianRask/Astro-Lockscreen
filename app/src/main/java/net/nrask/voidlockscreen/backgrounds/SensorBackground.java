package net.nrask.voidlockscreen.backgrounds;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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

	private float x = 0;

	private RelativeLayout lockscreenContainer;
	private View backgroundImageContainer;
	private ImageView backgroundImage;
	private HorizontalScrollView scrollView;


	public SensorBackground(Activity activity, RelativeLayout lockscreenContainer) {
		super(activity, lockscreenContainer);
		this.lockscreenContainer = lockscreenContainer;

		sensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);

		View.inflate(activity, R.layout.background_sensor_parallax, lockscreenContainer);
		backgroundImage = (ImageView) lockscreenContainer.findViewById(R.id.background_image);
		scrollView = (HorizontalScrollView) lockscreenContainer.findViewById(R.id.scrollView);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.phonewallpaper4, options);
		//backgroundImage.setImageBitmap(bitmap);

		setSystemAsBackground(activity);
	}

	private double imageWidth;
	private void setSystemAsBackground(Context context) {
		final View background = lockscreenContainer;
		WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
		final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
		background.post(new Runnable() {
			@Override
			public void run() {
				//background.setBackground(wallpaperDrawable);
			}
		});

		backgroundImage.setImageDrawable(wallpaperDrawable);
		BitmapDrawable bd = (BitmapDrawable) wallpaperDrawable;
		double imageHeight = bd.getBitmap().getHeight();
		imageWidth = bd.getBitmap().getWidth();
		double aspect = imageWidth/imageHeight;
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		float parallaxFactor = 0.1f;
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			float xDiff = event.values[0];
			x -= xDiff;

			int newX = (int) (x * parallaxFactor);

			if (x < 0) {
				x = 0;
			} else if ((scrollView.getScrollX() + 1) < newX) { // We don't want X to get larger if we don't scroll
				x += xDiff;
			}

			scrollView.setScrollX(newX);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {}
}
