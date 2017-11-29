package net.nrask.voidlockscreen.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.google.android.apps.muzei.api.MuzeiContract;

import java.io.FileNotFoundException;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 29/12/16.
 */

public class FetchMuzeiWallpaper extends AsyncTask<Void, Void, Bitmap> {
	private FetchMuzeiWallpaperCallback mCallback;
	private Context context;

	public FetchMuzeiWallpaper(FetchMuzeiWallpaperCallback mCallback, Context context) {
		this.mCallback = mCallback;
		this.context = context;
	}

	@Override
	protected Bitmap doInBackground(Void... voids) {
		try {
			return MuzeiContract.Artwork.getCurrentArtworkBitmap(context);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		super.onPostExecute(bitmap);
		mCallback.wallpaperFetched(bitmap);
	}

	public interface FetchMuzeiWallpaperCallback {
		void wallpaperFetched(@Nullable Bitmap bitmap);
	}
}
