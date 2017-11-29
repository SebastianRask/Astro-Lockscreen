package net.nrask.voidlockscreen.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;

import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.activities.LockscreenActivity;
import net.nrask.voidlockscreen.helpers.SRJHelper;
import net.nrask.voidlockscreen.notifications.viewholders.MaterialNotificationViewHolder;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 27/12/16.
 */

public class MaterialDesignNotifications extends LockscreenNotificationsView {
	private boolean isExpanded = false;
	private final int MAX_NOTIFICATIONS_UNEXPANDED = 3; // TODO calculate how much space there are for notifications
	private TextClock mClock;

	public MaterialDesignNotifications(RelativeLayout lockscreenContainer, final LockscreenActivity activity) {
		super(lockscreenContainer, activity);
		View.inflate(activity, R.layout.notification_material, lockscreenContainer);
		RecyclerView mNotificationsRecyclerView = lockscreenContainer.findViewById(R.id.notifications_recyclerview);
		mClock = lockscreenContainer.findViewById(R.id.text_clock);

		mNotificationsRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
		mNotificationsRecyclerView.setAdapter(mAdapter = new MaterialNotificationAdapter());
		mNotificationsRecyclerView.addOnItemTouchListener((RecyclerView.OnItemTouchListener) mAdapter);
		mNotificationsRecyclerView.setTranslationY((int) (SRJHelper.getScreenHeight(activity)/2.7)); // TODO get position another way
		mNotificationsRecyclerView.setItemAnimator(null);
	}

	private class MaterialNotificationAdapter extends RecyclerView.Adapter<MaterialNotificationViewHolder> implements RecyclerView.OnItemTouchListener {

		@Override
		public MaterialNotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View itemView = LayoutInflater
					.from(parent.getContext())
					.inflate(R.layout.notification_cell_material, parent, false);
			final MaterialNotificationViewHolder viewHolder = new MaterialNotificationViewHolder(itemView);

			viewHolder.itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					viewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					viewHolder.setCollapsedHeight((int) activity.getResources().getDimension(R.dimen.notification_cell_material_height));
					viewHolder.setExpandedHeight(viewHolder.itemView.getHeight());

					viewHolder.getContainer().setLayoutParams(
							new FrameLayout.LayoutParams(viewHolder.getContainer().getLayoutParams().width, viewHolder.getCollapsedHeight())
					);

				}
			});

			viewHolder.itemView.setOnTouchListener(new DragToExpandListener(viewHolder));
			return viewHolder;
		}

		@Override
		public void onViewRecycled(MaterialNotificationViewHolder holder) {
			super.onViewRecycled(holder);
			holder.itemView.getLayoutParams().height = (int) activity.getResources().getDimension(R.dimen.notification_cell_material_height);
		}

		@Override
		public void onBindViewHolder(final MaterialNotificationViewHolder holder, int position) {
			final StatusBarNotification statusBarNotification = mNotifications.get(position);

			Bundle extras = statusBarNotification.getNotification().extras;

			Bitmap notificationLargeIcon = extras.getParcelable(Notification.EXTRA_LARGE_ICON);
			int notificationSmallIconRes = extras.getInt(Notification.EXTRA_SMALL_ICON);

			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					try {
						statusBarNotification.getNotification().contentIntent.send();
						activity.unlockNoTouch(); //ToDo Dont directly unlock. Show security screen if needed
					} catch (PendingIntent.CanceledException e) {
						e.printStackTrace();
					}
				}
			});


			holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					if (holder.isExpanded()) {
						holder.collapse();
					} else {
						holder.expand();
					}
					return true;
				}
			});


			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				holder.mNotificationSmallIcon.setColorFilter(statusBarNotification.getNotification().color);
			}
			holder.mNotificationSmallIcon.setVisibility(notificationLargeIcon == null ? View.VISIBLE : View.INVISIBLE);

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(statusBarNotification.getNotification().when);
			holder.setWhenText(
					mClock.is24HourModeEnabled()
							? SRJHelper.numberToClockFormat(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + SRJHelper.numberToClockFormat(calendar.get(Calendar.MINUTE))
							: SRJHelper.numberToClockFormat(calendar.get(Calendar.HOUR)) + ":" + SRJHelper.numberToClockFormat(calendar.get(Calendar.MINUTE))  + " " + (calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault()))

			);
			holder.setTitle(
					extras.getCharSequence(Notification.EXTRA_TITLE),
					extras.getCharSequence(Notification.EXTRA_TITLE_BIG)
			);
			holder.setSubText(
					extras.getCharSequence(Notification.EXTRA_SUB_TEXT)
			);
			holder.setText(
					extras.getCharSequence(Notification.EXTRA_TEXT),
					extras.getCharSequence(Notification.EXTRA_BIG_TEXT), //ToDo Fix
					extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
			);

			try {
				Bitmap bmp = notificationLargeIcon;
				Drawable icon = activity.createPackageContext(statusBarNotification.getPackageName(), 0).getResources().getDrawable(notificationSmallIconRes);
				if(bmp == null && icon != null) {
					bmp = ((BitmapDrawable) icon).getBitmap();

					int paddingPx = SRJHelper.dpToPixels(activity, 10);
					holder.mNotificationIcon.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
				}

				holder.mNotificationIcon.setImageBitmap(bmp);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public int getItemCount() {
			if (mNotifications == null) {
				return 0;
			} else if (isExpanded || mNotifications.size() <= MAX_NOTIFICATIONS_UNEXPANDED) {
				return mNotifications.size();
			} else {
				return MAX_NOTIFICATIONS_UNEXPANDED + 1;
			}
		}

		@Override
		public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
			return false;
		}

		@Override
		public void onTouchEvent(RecyclerView rv, MotionEvent e) {
			Log.d("Adapter", "Item Touched " + e.getAction());

		}

		@Override
		public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

		}
	}

}
