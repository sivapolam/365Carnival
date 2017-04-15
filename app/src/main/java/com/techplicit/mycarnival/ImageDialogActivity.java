package com.techplicit.mycarnival;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.techplicit.mycarnival.utils.ImageLoader;

/**
 * Created by FuGenX-test on 31-03-2016.
 */
public class ImageDialogActivity extends Activity {
    Activity activityRef;
    TouchImageView imageView;
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.offer_image_expned_dialog);
        activityRef = ImageDialogActivity.this;
        imageLoader = new ImageLoader(this);
        imageView = (TouchImageView) findViewById(R.id.tiv_image);
        ImageView back = (ImageView) findViewById(R.id.back_arrow_carnivals_list);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent() != null) {
            String url = getIntent().getStringExtra("imageUrl");
            if (url != null) {
//                imageLoader.DisplayImage(url, imageView);
                Picasso.with(ImageDialogActivity.this).load(url).into(imageView);

            }
        }
    }
}
