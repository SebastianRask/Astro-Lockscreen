package net.nrask.voidlockscreen.ui.setup;

import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.nrask.voidlockscreen.R;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Sebastian on 30-11-2017.
 */

public class SetupStepVH extends RecyclerView.ViewHolder {
    @BindView(R.id.txt_step_title)
    TextView mTitle;

    @BindView(R.id.txt_step_description)
    TextView mDescription;

    @BindView(R.id.card_selected_background)
    CardView mBackground;

    @BindView(R.id.content_container)
    FrameLayout mContentContainer;

    @BindDimen(R.dimen.setup_step_elevation)
    float SELECTED_ELEVATION;

    @BindDimen(R.dimen.default_margin)
    int CARD_PADDING;

    @BindDimen(R.dimen.default_margin_small)
    int CARD_PADDING_SMALL;

    public SetupStepVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindData(SetupStep step, boolean isCurrentStep) {
        mTitle.setText(step.getTitle());
        mDescription.setText(step.getSubtitle());

        @ColorRes int textColor = R.color.white_text_disabled;
        @ColorRes int backgroundColor = R.color.colorPrimary;
        float elevation = 0;
        int itemMargin = 0;

        if (isCurrentStep) {
            textColor = R.color.black_text;
            backgroundColor = R.color.white;

            elevation = SELECTED_ELEVATION;

            itemMargin = CARD_PADDING_SMALL;
        }

        mTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), textColor));
        mDescription.setTextColor(ContextCompat.getColor(itemView.getContext(), textColor));

        mBackground.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), backgroundColor));
        mBackground.setCardElevation(elevation);
        mBackground.setContentPadding(CARD_PADDING, CARD_PADDING, CARD_PADDING, CARD_PADDING);
        mBackground.setEnabled(isCurrentStep);

        itemView.setPadding(0, itemMargin, 0, itemMargin);

        mDescription.setVisibility(isCurrentStep ? View.VISIBLE : View.GONE);
    }
}
