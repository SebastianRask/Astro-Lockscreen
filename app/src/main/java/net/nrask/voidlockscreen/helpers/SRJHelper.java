package net.nrask.voidlockscreen.helpers;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import net.nrask.voidlockscreen.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by Sebastian Rask on 21-12-2016.
 */

public class SRJHelper {

	/**
	 * Vibrate the device
	 * @param duration the duration in ms
	 * @param context
	 */
	public static void vibrate(int duration, Context context) {
		Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(duration);
	}

	/**
	 * Creates and returns an intent that navigates the user to the Google Play landing page for the app
	 * @return The intent
	 */
	public static Intent getPlayStoreIntent(String bundleIdentifier) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=" + bundleIdentifier));
		return intent;
	}

	/**
	 * Checks if two calendar objects have the same day of the year
	 * @param one I think it's pretty obvious
	 * @param two what these two objects are for
	 * @return True if the day is the same, otherwise false
	 */
	public static boolean isCalendarSameDay(Calendar one, Calendar two) {
		return one.get(Calendar.YEAR) == two.get(Calendar.YEAR) && one.get(Calendar.DAY_OF_YEAR) == two.get(Calendar.DAY_OF_YEAR);
	}


	/**
	 * Converts Double to time. f.eks. 4.5 becomes "04"
	 */
	public static String numberToClockTime(double time) {
		int timeInt = ((int) Math.floor(time));

		if (timeInt < 10) {
			return "0" + timeInt;
		} else {
			return "" + timeInt;
		}
	}

	/**
	 * Creates a bitmap with rounded corners.
	 * @param bitmap The bitmap
	 * @param i the corner radius in pixels
	 * @return The bitmap with rounded corners
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int i) {
		if (bitmap == null) {
			return Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888);
		}

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = i;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * Animates the background color of a view from one color to another color.
	 * @param v The view to animate
	 * @param toColor The To Color
	 * @param fromColor The From Color
	 * @param duration The Duration of the animation
	 * @return the animator
	 */
	public static Animator animateBackgroundColorChange(View v, int toColor, int fromColor, int duration) {
		ObjectAnimator colorFade = ObjectAnimator.ofObject(v, "backgroundColor", new ArgbEvaluator(), fromColor, toColor);
		colorFade.setDuration(duration);
		colorFade.start();
		return colorFade;
	}

	/**
	 * Finds and returns an attribute color. If it was not found the method returns the default color
	 */
	public static int getColorAttribute(@AttrRes int attribute, @ColorRes int defaultColor, Context context) {
		TypedValue a = new TypedValue();
		context.getTheme().resolveAttribute(attribute, a, true);
		if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
			return a.data;
		} else {
			return ContextCompat.getColor(context, defaultColor);
		}
	}

	/**
	 *
	 * @param view The view to get the color from
	 * @param defaultColor The color to return if the view's background isn't a ColorDrawable
	 * @return The color
	 */
	public static int getBackgroundColorFromView(View view, int defaultColor) {
		int color = defaultColor;
		Drawable background = view.getBackground();
		if (background instanceof ColorDrawable) {
			color = ((ColorDrawable) background).getColor();
		}

		return color;
	}

	/**
	 * Decodes a byte array to a bitmap and returns it.
	 */
	public static Bitmap getBitmapFromByteArray(byte[] bytes) {
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

	/**
	 * Creates a byte-array for a drawable and returns it.
	 * This is useful for sending images with intents.
	 */
	public static byte[] getDrawableByteArray(Drawable aDrawable) {
		Bitmap bitmap = drawableToBitmap(aDrawable);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * Converts a drawable to a bitmap and returns it.
	 */
	public static Bitmap drawableToBitmap (Drawable drawable) {
		Bitmap bitmap = null;

		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			if(bitmapDrawable.getBitmap() != null) {
				return bitmapDrawable.getBitmap();
			}
		}

		if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
			bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
		} else {
			bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		}

		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * Creates a string with a unicode emoticon.
	 * @param unicode
	 * @return
	 */
	public static String getEmijoByUnicode(int unicode){
		return new String(Character.toChars(unicode));
	}

	/**
	 * Hides the onscreen keyboard if it is visisble
	 */
	public static void hideKeyboard(Activity activity) {
		// Check if no view has focus:
		View view = activity.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/**
	 * Shows the soft keyboard
	 */
	public static void showKeyboard(Activity activity) {
		// Check if no view has focus:
		View view = activity.getCurrentFocus();
		if (view != null) {
			InputMethodManager inputMethodManager=(InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
		}
	}

	/**
	 * Gets the accent color from the current theme
	 */
	public static int getAccentColor(Context mContext) {
		TypedValue typedValue = new TypedValue();

		TypedArray a = mContext.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
		int color = a.getColor(0, 0);

		a.recycle();

		return color;
	}

	/**
	 * Returns a resized bitmap with a spcified factor to change the width and height with.
	 */
	public static Bitmap getResizedBitmap(Bitmap bm, float factorchange) {
		return getResizedBitmap(bm, (int) (bm.getWidth() * factorchange), (int) (bm.getHeight() * factorchange));
	}


	/**
	 * Creates a new resized bitmap with a specified width and height.
	 */
	public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		//bm.recycle();
		return resizedBitmap;
	}

	/**
	 * Method for increasing a Navigation Drawer's edge size.
	 */
	public static void increaseNavigationDrawerEdge(DrawerLayout aDrawerLayout, Context context) {
		// Increase the area from which you can open the navigation drawer.
		try {
			Field mDragger = aDrawerLayout.getClass().getDeclaredField("mLeftDragger");
			mDragger.setAccessible(true);
			ViewDragHelper draggerObj = (ViewDragHelper) mDragger.get(aDrawerLayout);

			Field mEdgeSize = draggerObj.getClass().getDeclaredField("mEdgeSize");
			mEdgeSize.setAccessible(true);
			int edgeSize = mEdgeSize.getInt(draggerObj) * 3;

			mEdgeSize.setInt(draggerObj, edgeSize); //optimal value as for me, you may set any constant in dp
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the device is connected to a valid network
	 * Can only be called on a thread
	 */
	public static boolean isNetworkConnectedThreadOnly(Context context){
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
			try {
				HttpURLConnection urlc = (HttpURLConnection)
						(new URL("http://clients3.google.com/generate_204")
								.openConnection());
				urlc.setRequestProperty("User-Agent", "Android");
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(1500);
				urlc.connect();
				return (urlc.getResponseCode() == 204 &&
						urlc.getContentLength() == 0);
			} catch (IOException e) {
				Log.e("SERVICE", "Error checking internet connection", e);
			}
		} else {
			Log.d("SERVICE", "No network available!");
		}

		return false;
	}

	/**
	 * Checks if the device is connected to a valid network
	 * Can be called on the UI thread
	 */
	public static boolean isNetWorkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();

		return networkInfo != null && networkInfo.isConnectedOrConnecting();
	}

	/**
	 * Does the opposite of the View.bringToFront() method
	 * @param v the view you want to send to the back
	 */
	public static void bringToBack(final View v) {
		final ViewGroup parent = (ViewGroup)v.getParent();
		if (null != parent) {
			parent.removeView(v);
			parent.addView(v, 0);
		}
	}

	/**
	 * Creates a bytestream from image and saves it to the device's internal storage with the specified key
	 * @param image The image to save
	 * @param key The key to save the image with
	 * @param context
	 */
	public static void saveImageToStorage(Bitmap image, String key, Context context) {
		try {
			// Create an ByteArrayOutputStream and feed a compressed bitmap image in it
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.PNG, 100, byteStream); // PNG as only format with transparency

			// Create a FileOutputStream with out key and set the mode to private to ensure
			// Only this app and read the file. Write out ByteArrayOutput to the file and close it
			FileOutputStream fileOut = context.openFileOutput(key, Context.MODE_PRIVATE);
			fileOut.write(byteStream.toByteArray());
			byteStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns an image from the device's internal storage with specified key.
	 * If the image does not exist an IOException will be thrown
	 * @param key The key the image have been saved with
	 * @param context
	 * @return The image
	 * @throws IOException
	 */
	public static Bitmap getImageFromStorage(String key, Context context) throws IOException {
		InputStream fileIn = context.openFileInput(key);
		return BitmapFactory.decodeStream(fileIn);
	}

	/**
	 * Check if a file exists in the device's internal storage with the specified key
	 * @param key The key the file might have been saved under
	 * @param context
	 * @return True if the file exists, otherwise false
	 */
	public static boolean doesStorageFileExist(String key, Context context){
		File file = context.getFileStreamPath(key);
		return file.exists();
	}

	/**
	 * Gets the navigation drawer toggle view from a toolbar
	 * @param toolbar The toolbar containing the navigation button
	 * @return The ImageButton
	 */
	public static ImageButton getNavButtonView(Toolbar toolbar) {
		try {
			Class<?> toolbarClass = Toolbar.class;
			Field navButtonField = toolbarClass.getDeclaredField("mNavButtonView");
			navButtonField.setAccessible(true);

			return (ImageButton) navButtonField.get(toolbar);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Returns the height of the device screen
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}

	public static boolean isVertical(Context aContext) {
		int orientation = aContext.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			return true;
		} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return false;
		}
		return true;
	}

	public static void setTopRounded(Bitmap workingBitmap, ImageView v, Context context, float cornerRadius) {
		int w = workingBitmap.getWidth();
		int h = workingBitmap.getHeight();
		Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bmp);
		Shader shader = new BitmapShader(workingBitmap, Shader.TileMode.MIRROR,
				Shader.TileMode.MIRROR);

		Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		paint.setAntiAlias(true);
		paint.setShader(shader);
		RectF rec = new RectF(0, 0, w, h - (h/3));
		c.drawRect(new RectF(0, (h/3), w, h), paint);
		c.drawRoundRect(rec, cornerRadius, cornerRadius, paint);
		v.setImageDrawable(new BitmapDrawable(context.getResources(), bmp));
		v.setImageBitmap(new BitmapDrawable(context.getResources(), bmp).getBitmap());
	}

	public static Bitmap makeTransparent(Bitmap bit, int transparentColor) {
		int width =  bit.getWidth();
		int height = bit.getHeight();
		Bitmap myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		int [] allpixels = new int [ myBitmap.getHeight()*myBitmap.getWidth()];
		bit.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
		myBitmap.setPixels(allpixels, 0, width, 0, 0, width, height);

		for(int i =0; i<myBitmap.getHeight()*myBitmap.getWidth();i++){
			if( allpixels[i] == transparentColor)
				allpixels[i] = Color.alpha(Color.TRANSPARENT);
		}

		myBitmap.setPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
		return myBitmap;
	}

	/**
	 * Get the amount of data received
	 * @return
	 */
	public static double getDataReceived() {
		return (double) TrafficStats.getUidRxBytes(android.os.Process
				.myUid()) / (1024 * 1024);
	}

	/**
	 * Convert dp to pixels
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int dpToPixels(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * Gets Bitmap from the specified URL
	 * Must not be called on Main UI Thread
	 */
	public static Bitmap getBitmapFromUrl(String url) {
		Bitmap bitmap = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.connect();
			InputStream input = connection.getInputStream();
			bitmap = BitmapFactory.decodeStream(input);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bitmap;
	}

}

