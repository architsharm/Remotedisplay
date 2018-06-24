package com.nexterp.NLP1;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.cast.CastPresentation;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 30-06-2017.
 */

public class DetailPresentation extends CastPresentation {
    @Bind(R.id.ad_title) public TextView title;
    @Bind(R.id.ad_price) public TextView price;
    @Bind(R.id.ad_image) public ImageView image;

    public DetailPresentation(Context context, Display display) {
        super(context, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_detail);
        ButterKnife.bind(this);
        updateAdDetail(RemoteDisplayService2.getadViewModel());
    }

    public void updateAdDetail(AdViewModel adViewModel) {
        title.setText(adViewModel.getTitle());
        price.setText(adViewModel.getPrice());
        if (!adViewModel.getImage().isEmpty()) {
            Glide.with(getContext())
                    .load(adViewModel.getImage())
                    .centerCrop()
                    .placeholder(R.drawable.ic_cast_grey)
                    .crossFade()
                    .into(image);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.unbind(this);
    }
}