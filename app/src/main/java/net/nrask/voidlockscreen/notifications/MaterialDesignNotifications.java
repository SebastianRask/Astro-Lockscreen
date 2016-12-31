package net.nrask.voidlockscreen.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.activities.LockscreenActivity;
import net.nrask.voidlockscreen.animations.ResizeHeightAnimation;
import net.nrask.voidlockscreen.helpers.SRJHelper;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Sebastian Rask Jepsen (SRJ@Idealdev.dk) on 27/12/16.
 */

public class MaterialDesignNotifications extends LockscreenNotificationsView {
	private boolean isExpanded = false;
	private final int MAX_NOTIFICATIONS_UNEXPANDED = 3;
	private TextClock mClock;

	public MaterialDesignNotifications(RelativeLayout lockscreenContainer, final LockscreenActivity activity) {
		super(lockscreenContainer, activity);
		View.inflate(activity, R.layout.notification_material, lockscreenContainer);
		RecyclerView mNotificationsRecyclerView = (RecyclerView) lockscreenContainer.findViewById(R.id.notifications_recyclerview);
		mClock = (TextClock) lockscreenContainer.findViewById(R.id.text_clock);

		mNotificationsRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
		mNotificationsRecyclerView.setAdapter(mAdapter = new MaterialNotificationAdapter());
		mNotificationsRecyclerView.addOnItemTouchListener((RecyclerView.OnItemTouchListener) mAdapter);
		mNotificationsRecyclerView.setTranslationY((int) (SRJHelper.getScreenHeight(activity)/2.7));
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
					viewHolder.collapsedHeight = (int) activity.getResources().getDimension(R.dimen.notification_cell_material_height);
					viewHolder.expandedHeight = viewHolder.itemView.getHeight();
					viewHolder.itemView.getLayoutParams().height = viewHolder.collapsedHeight;
					viewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}
			});
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
						activity.unlockNoTouch(); //ToDo Dont directly unlock. Show security screen if possible
					} catch (PendingIntent.CanceledException e) {
						e.printStackTrace();
					}
				}
			});

			holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					Log.d("Adapter", "Item long clicked");
					if (holder.isExpanded) {
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
							? SRJHelper.numberToClockTime(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + SRJHelper.numberToClockTime(calendar.get(Calendar.MINUTE))
							: SRJHelper.numberToClockTime(calendar.get(Calendar.HOUR)) + ":" + SRJHelper.numberToClockTime(calendar.get(Calendar.MINUTE))  + " " + (calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault()))

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

	private class MaterialNotificationViewHolder extends ViewHolder {
		protected boolean isExpanded = false;
		protected int expandedHeight = 0, collapsedHeight = 0;
		protected CardView mCardView;
		protected ImageView mNotificationIcon, mNotificationSmallIcon;
		protected TextView mNotificationTitle, mNotificationText, mNotificationWhenText, mNotificationSubText;
		protected TextView mNotificationTitleExpanded, mNotificationTextExpanded, mNotificationWhenTextExpanded, mNotificationSubTextExpanded;
		protected View mNotificationTextContainer, mNotificationExpandedTextContainer;

		public MaterialNotificationViewHolder(View itemView) {
			super(itemView);
			mCardView = (CardView) itemView.findViewById(R.id.card_view);
			mNotificationIcon = (ImageView) itemView.findViewById(R.id.notification_icon);
			mNotificationSmallIcon = (ImageView) itemView.findViewById(R.id.notification_small_icon);

			mNotificationTextContainer = itemView.findViewById(R.id.notification_text_container);
			mNotificationTitle = (TextView) itemView.findViewById(R.id.notification_title);
			mNotificationText = (TextView) itemView.findViewById(R.id.notification_text);
			mNotificationSubText = (TextView) itemView.findViewById(R.id.notification_sub_text);
			mNotificationWhenText = (TextView) itemView.findViewById(R.id.notification_when_text);

			mNotificationExpandedTextContainer = itemView.findViewById(R.id.notification_text_expanded_container);
			mNotificationTitleExpanded = (TextView) itemView.findViewById(R.id.notification_title_expanded);
			mNotificationTextExpanded = (TextView) itemView.findViewById(R.id.notification_text_expanded);
			mNotificationSubTextExpanded = (TextView) itemView.findViewById(R.id.notification_sub_text_expanded);
			mNotificationWhenTextExpanded = (TextView) itemView.findViewById(R.id.notification_when_text_expanded);

			mNotificationTextContainer.setAlpha(isExpanded ? 0f : 1f);
			mNotificationExpandedTextContainer.setAlpha(isExpanded ? 1f : 0f);
		}

		public void setTitle(CharSequence title, @Nullable CharSequence titleExpanded) {
			mNotificationTitle.setText(title);
			mNotificationTitleExpanded.setText(titleExpanded == null ? title : titleExpanded);
		}

		public void setText(CharSequence text, @Nullable CharSequence textExpanded, @Nullable CharSequence[] lines) {
			int visibility = (text == null || text.length() == 0) ? View.GONE : View.VISIBLE;

			mNotificationText.setVisibility(visibility);
			mNotificationTextExpanded.setVisibility(visibility);

			mNotificationText.setText(text);
			mNotificationTextExpanded.setText(textExpanded == null ? text : textExpanded);
		}

		public void setWhenText(CharSequence whenText) {
			mNotificationWhenText.setText(whenText);
			mNotificationWhenTextExpanded.setText(whenText);
		}

		public void setSubText(CharSequence subText) {
			int visibility = (subText == null || subText.length() == 0) ? View.GONE : View.VISIBLE;

			mNotificationSubText.setVisibility(visibility);
			mNotificationSubTextExpanded.setVisibility(visibility);

			mNotificationSubText.setText(subText);
			mNotificationSubTextExpanded.setText(subText);
		}

		public void expand() {
			isExpanded = true;
			stateChange(expandedHeight, 0f, 1f);
		}

		public void collapse() {
			isExpanded = false;
			stateChange(collapsedHeight, 1f, 0f);
		}

		private void stateChange(int height, float collapsedTextAlpha, float expandedTextAlpha) {
			int duration = 340;

			mNotificationTextContainer.animate().alpha(collapsedTextAlpha).setDuration(duration).start();
			mNotificationExpandedTextContainer.animate().alpha(expandedTextAlpha).setDuration(duration).start();

			ResizeHeightAnimation heightAnimation = new ResizeHeightAnimation(itemView, height);
			heightAnimation.setDuration(duration);
			itemView.startAnimation(heightAnimation);
		}

	}
}
