package net.nrask.voidlockscreen.ui.lockscreen.notifications.viewholders;

import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.nrask.voidlockscreen.R;
import net.nrask.voidlockscreen.animations.ResizeHeightAnimation;

/**
 * Created by Sebastian on 29-11-2017.
 */
public class MaterialNotificationViewHolder extends RecyclerView.ViewHolder implements ExpandableCell {
    protected boolean isExpanded = false;
    protected int expandedHeight = 0, collapsedHeight = 0;
    protected CardView mCardView;
    protected TextView mNotificationTitle, mNotificationText, mNotificationWhenText, mNotificationSubText;
    protected TextView mNotificationTitleExpanded, mNotificationTextExpanded, mNotificationWhenTextExpanded, mNotificationSubTextExpanded;
    protected View mNotificationTextContainer, mNotificationExpandedTextContainer;
    protected RelativeLayout mContainer;
    public ImageView mNotificationIcon, mNotificationSmallIcon;

    public MaterialNotificationViewHolder(View itemView) {
        super(itemView);
        mCardView = itemView.findViewById(R.id.card_view);
        mContainer = itemView.findViewById(R.id.notification_container);
        mNotificationIcon = itemView.findViewById(R.id.notification_icon);
        mNotificationSmallIcon = itemView.findViewById(R.id.notification_small_icon);

        mNotificationTextContainer = itemView.findViewById(R.id.notification_text_container);
        mNotificationTitle = itemView.findViewById(R.id.notification_title);
        mNotificationText = itemView.findViewById(R.id.notification_text);
        mNotificationSubText = itemView.findViewById(R.id.notification_sub_text);
        mNotificationWhenText = itemView.findViewById(R.id.notification_when_text);

        mNotificationExpandedTextContainer = itemView.findViewById(R.id.notification_text_expanded_container);
        mNotificationTitleExpanded = itemView.findViewById(R.id.notification_title_expanded);
        mNotificationTextExpanded = itemView.findViewById(R.id.notification_text_expanded);
        mNotificationSubTextExpanded = itemView.findViewById(R.id.notification_sub_text_expanded);
        mNotificationWhenTextExpanded = itemView.findViewById(R.id.notification_when_text_expanded);

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

        View viewToAnimated = mContainer;
        ResizeHeightAnimation heightAnimation = new ResizeHeightAnimation(viewToAnimated, height);
        heightAnimation.setDuration(duration);
        viewToAnimated.startAnimation(heightAnimation);
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public View getContainer() {
        return mContainer;
    }

    public void setExpandedHeight(int expandedHeight) {
        this.expandedHeight = expandedHeight;
    }

    public void setCollapsedHeight(int collapsedHeight) {
        this.collapsedHeight = collapsedHeight;
    }

    @Override
    public int getCollapsedHeight() {
        return collapsedHeight;
    }

    @Override
    public int getExpandedHeight() {
        return expandedHeight;
    }

    @Override
    public View getExpandableView() {
        return this.itemView;
    }


}
