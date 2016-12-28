package net.nrask.voidlockscreen.notifications;

import android.app.Activity;
import android.app.Notification;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.helpers.SRJHelper;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 27/12/16.
 */

public class MaterialDesignNotifications extends LockscreenNotificationsView {


	public MaterialDesignNotifications(RelativeLayout lockscreenContainer, final Activity activity) {
		super(lockscreenContainer, activity);
		View.inflate(activity, R.layout.notification_material, lockscreenContainer);
		RecyclerView mNotificationsRecyclerView = (RecyclerView) lockscreenContainer.findViewById(R.id.notifications_recyclerview);
		final TextClock mClock = (TextClock) lockscreenContainer.findViewById(R.id.text_clock);

		mNotificationsRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
		mNotificationsRecyclerView.setAdapter(mAdapter = new RecyclerView.Adapter<MaterialNotificationViewHolder>() {
			@Override
			public MaterialNotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
				View itemView = LayoutInflater
						.from(parent.getContext())
						.inflate(R.layout.notification_cell_material, parent, false);
				return new MaterialNotificationViewHolder(itemView);
			}

			@Override
			public void onBindViewHolder(MaterialNotificationViewHolder holder, int position) {
				StatusBarNotification statusBarNotification = mNotifications.get(position);

				long when = statusBarNotification.getNotification().when;

				Bundle extras = statusBarNotification.getNotification().extras;
				CharSequence notificationTitle = extras.getCharSequence(Notification.EXTRA_TITLE);
				CharSequence notificationTitleExpanded = extras.getCharSequence(Notification.EXTRA_TITLE_BIG);

				CharSequence notificationText = extras.getCharSequence(Notification.EXTRA_TEXT);
				CharSequence notificationTextExpanded = extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
				CharSequence[] notificationLines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);

				CharSequence notificationSubText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT); //Sub text might be the email address and should always be placed at the bottom
				CharSequence notificationSummary = extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT);

				Bitmap notificationLargeIcon = ((Bitmap) extras.getParcelable(Notification.EXTRA_LARGE_ICON));
				int notificationSmallIconRes = extras.getInt(Notification.EXTRA_SMALL_ICON);


				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(when);


				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					holder.mNotificationSmallIcon.setColorFilter(statusBarNotification.getNotification().color);
				}

				holder.mNotificationWhenText.setText(
						mClock.is24HourModeEnabled()
								? SRJHelper.numberToClockTime(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + SRJHelper.numberToClockTime(calendar.get(Calendar.MINUTE))
								: SRJHelper.numberToClockTime(calendar.get(Calendar.HOUR)) + ":" + SRJHelper.numberToClockTime(calendar.get(Calendar.MINUTE))  + " " + (calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault()))
				);
				holder.mNotificationTitle.setText(notificationTitle);
				holder.mNotificationText.setVisibility((notificationText == null || notificationText.length() == 0) ? View.GONE : View.VISIBLE);
				holder.mNotificationText.setText(notificationText);
				holder.mNotificationSubText.setVisibility((notificationSubText == null || notificationSubText.length() == 0) ? View.GONE : View.VISIBLE);
				holder.mNotificationSubText.setText(notificationSubText);


				try {
					Bitmap bmp = notificationLargeIcon;
					Drawable icon = activity.createPackageContext(statusBarNotification.getPackageName(), 0).getResources().getDrawable(notificationSmallIconRes);
					if(bmp == null && icon != null) {
						bmp = ((BitmapDrawable) icon).getBitmap();

						int paddingPx = SRJHelper.dpToPixels(activity, 8);
						holder.mNotificationIcon.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
					}

					holder.mNotificationIcon.setImageBitmap(bmp);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public int getItemCount() {
				return mNotifications == null ? 0 : mNotifications.size();
			}
		});
		mNotificationsRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
				super.getItemOffsets(outRect, view, parent, state);
				if (parent.getChildLayoutPosition(view)== 0) {
					outRect.top = (int) (SRJHelper.getScreenHeight(activity)/2.7);
				}
			}
		});

	}

	private class MaterialNotificationViewHolder extends ViewHolder {
		protected CardView mCardView;
		protected ImageView mNotificationIcon, mNotificationSmallIcon;
		protected TextView mNotificationTitle, mNotificationText, mNotificationWhenText, mNotificationSubText;

		public MaterialNotificationViewHolder(View itemView) {
			super(itemView);
			mCardView = (CardView) itemView.findViewById(R.id.card_view);
			mNotificationIcon = (ImageView) itemView.findViewById(R.id.notification_icon);
			mNotificationSmallIcon = (ImageView) itemView.findViewById(R.id.notification_small_icon);
			mNotificationTitle = (TextView) itemView.findViewById(R.id.notification_title);
			mNotificationText = (TextView) itemView.findViewById(R.id.notification_text);
			mNotificationSubText = (TextView) itemView.findViewById(R.id.notification_sub_text);
			mNotificationWhenText = (TextView) itemView.findViewById(R.id.notification_when_text);
		}
	}
}
