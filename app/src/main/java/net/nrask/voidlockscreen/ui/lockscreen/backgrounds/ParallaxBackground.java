package net.nrask.voidlockscreen.ui.lockscreen.backgrounds;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.helpers.SRJHelper;
import net.nrask.voidlockscreen.tasks.FetchMuzeiWallpaper;

import java.io.IOException;

import jp.wasabeef.blurry.Blurry;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 23/12/16.
 */

public class ParallaxBackground extends LockscreenBackgroundController implements SensorEventListener {
	private SensorManager sensorManager;

	private final float PARALLAX_FACTOR = 0.03f;
	private float x = 0;

	private RelativeLayout lockscreenContainer;
	private ImageView backgroundImage;
	private HorizontalScrollView scrollView;

	public ParallaxBackground(RelativeLayout lockscreenContainer) {
		super(lockscreenContainer);
		this.lockscreenContainer = lockscreenContainer;

		Context context = lockscreenContainer.getContext();
		sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
		backgroundImage = lockscreenContainer.findViewById(R.id.background_image);
		scrollView = lockscreenContainer.findViewById(R.id.scrollView);

//		setMuzeiAsBackground(context);
		setSystemAsBackground(context);
	}

	@Override
	protected void resume() {
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void pause() {
		sensorManager.unregisterListener(this);
	}

	private void enableParallaxIfPossible(Context context, final int imageWidth) {
		Log.d(getClass().getSimpleName(), "Image width " + imageWidth);
		//If the width of the image is less than the width of the screen, then parallax is not possible. So remove the image from the scrollview and add it to the container instead.
		if (imageWidth < SRJHelper.getScreenWidth(context)) {
			scrollView.removeView(backgroundImage);
			lockscreenContainer.addView(backgroundImage);
			backgroundImage.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		} else {
			scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

					x = (backgroundImage.getWidth()/2 - scrollView.getWidth()/2)/ PARALLAX_FACTOR; // Center image
					scrollView.setScrollX((int) (x * PARALLAX_FACTOR));
				}
			});
		}
	}

	private void setSystemAsBackground(final Context context) {
		WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
		final Drawable wallpaperDrawable = wallpaperManager.getDrawable();

		final BitmapDrawable wallpaperBitmap = (BitmapDrawable) wallpaperDrawable;
		setBackgroundImage(context, wallpaperBitmap.getBitmap());
	}

	private void setMuzeiAsBackground(final Activity context) {
		boolean imageSet = false;
		Bitmap oldBitmap = null;
		try {
			oldBitmap = SRJHelper.getImageFromStorage(context.getString(R.string.muzei_wallpaper_storage_key), context);

			if (oldBitmap != null) {
				setBackgroundImage(context, oldBitmap);
				imageSet = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		final boolean finalImageSet = imageSet;
		final Bitmap finalOldBitmap = oldBitmap;
		new FetchMuzeiWallpaper(
				new FetchMuzeiWallpaper.FetchMuzeiWallpaperCallback() {
					@Override
					public void wallpaperFetched(@Nullable final Bitmap bitmap) {
						if (bitmap != null && (!finalImageSet || !bitmap.sameAs(finalOldBitmap))) {
							setBackgroundImage(context, bitmap);

							new Thread(new Runnable() {
								@Override
								public void run() {
									SRJHelper.saveImageToStorage(bitmap, context.getString(R.string.muzei_wallpaper_storage_key), context);
								}
							}).start();

						} else if (bitmap == null) {
							setSystemAsBackground(context);
						}
					}
				},
				context
		).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void setBackgroundImage(Context context, Bitmap image) {
		Blurry.with(context)
				.radius(15)
				.from(image)
				.into(backgroundImage);
		enableParallaxIfPossible(context, image.getWidth());
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			float xDiff = event.values[0];
			float minXDiff = 0.1f;

			if (Math.abs(xDiff) < minXDiff) {
				xDiff = xDiff < 0 ? -minXDiff : minXDiff;
			}

			x -= xDiff;

			int newX = (int) (x * PARALLAX_FACTOR);

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
