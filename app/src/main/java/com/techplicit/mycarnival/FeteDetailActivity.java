package com.techplicit.mycarnival;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.techplicit.mycarnival.data.model.FeteDetailModel;
import com.techplicit.mycarnival.ui.activities.BaseActivity;
import com.techplicit.mycarnival.utils.ImageLoader;
import com.techplicit.mycarnival.utils.Utility;

/**
 * Created by FuGenX-50 on 19-01-2017.
 */

public class FeteDetailActivity extends BaseActivity {

    TextView tv_fd_title;
    TextView tv_fd_venue;
    TextView tv_fd_date;
    TextView tv_fd_date2;
    TextView tv_fd_time;
    TextView tv_fd_ticket_details;
    TextView tv_fd_det_details;
    TextView tv_fd_pc;
    TextView tv_fd_sc, tv_fd_sc_newline;
    ImageView iv_fd_image;

    FeteDetailModel feteDetailModel;
    ImageLoader imageLoader;
    private LinearLayout layout_time_row;
    private View v_str_c;
    private LinearLayout layout_fd_sc_newline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_fete_detail1, frameLayout);

        feteDetailModel = getIntent().getParcelableExtra("feteDetailModel");
        imageLoader = new ImageLoader(this);
        mContext = FeteDetailActivity.this;
        layout_time_row = (LinearLayout)findViewById(R.id.layout_time_row);
        layout_fd_sc_newline = (LinearLayout)findViewById(R.id.layout_fd_sc_newline);

        tv_fd_title = (TextView) findViewById(R.id.tv_fd_title);
        tv_fd_venue = (TextView) findViewById(R.id.tv_fd_venue);
        tv_fd_date = (TextView) findViewById(R.id.tv_fd_date);
        tv_fd_date2 = (TextView) findViewById(R.id.tv_fd_date2);
        tv_fd_time = (TextView) findViewById(R.id.tv_fd_time);
        tv_fd_pc = (TextView) findViewById(R.id.tv_fd_pc);
        tv_fd_sc = (TextView) findViewById(R.id.tv_fd_sc);
        tv_fd_sc_newline = (TextView) findViewById(R.id.tv_fd_sc_newline);
        tv_fd_ticket_details = (TextView) findViewById(R.id.tv_fd_ticket_details);
        tv_fd_det_details = (TextView) findViewById(R.id.tv_fd_det_details);
        iv_fd_image = (ImageView) findViewById(R.id.iv_fd_image);

        home_icon.setImageResource(R.drawable.home);
        titleHome.setVisibility(View.GONE);
        title.setText("Fete Detail");
        title.setVisibility(View.VISIBLE);

        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startHomeActivity(FeteDetailActivity.this);
                finish();
            }
        });

        ImageView backArrowCarnivalsList = (ImageView) findViewById(R.id.back_arrow_carnivals_list);
        backArrowCarnivalsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContext = FeteDetailActivity.this;
    }

    private void setData() {
        if (feteDetailModel != null) {
            if (feteDetailModel.getImage() != null) {
                Picasso.with(FeteDetailActivity.this).load(feteDetailModel.getImage()).into(iv_fd_image);

//                imageLoader.DisplayImage(feteDetailModel.getImage(), iv_fd_image);
                iv_fd_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentGenerator.startFullImageActivity(FeteDetailActivity.this, feteDetailModel.getImage());
                    }
                });
            }
            tv_fd_title.setText(feteDetailModel.getName());
            tv_fd_venue.setText(feteDetailModel.getVenue());
            tv_fd_date.setText(Utility.convertMilliSecsToDate(feteDetailModel.getStartDate()));
            tv_fd_date2.setText(Utility.convertMilliSecsToDate(feteDetailModel.getEndDate()));
            tv_fd_time.setText(feteDetailModel.getStartTime() + " - " + feteDetailModel.getEndTime());
            tv_fd_pc.setText(feteDetailModel.getPrimaryContact());
            tv_fd_sc.setText(feteDetailModel.getSecondaryContact());
            tv_fd_sc_newline.setText(feteDetailModel.getSecondaryContact());
            tv_fd_ticket_details.setText(feteDetailModel.getTicketTypes().getTicket());
            tv_fd_det_details.setText(feteDetailModel.getTicketTypes().getDetails());

            v_str_c = (View)findViewById(R.id.v_str_c);

            tv_fd_sc.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("Line count: ", tv_fd_sc.getLineCount()+"");
                    if (tv_fd_sc.getLineCount() > 1) {
                        tv_fd_sc.setVisibility(View.GONE);
                        layout_fd_sc_newline.setVisibility(View.VISIBLE);
                    } else {
                        tv_fd_sc.setVisibility(View.VISIBLE);
                        layout_fd_sc_newline.setVisibility(View.GONE);
                    }
                }
            });


//            populateText(layout_time_row, new View[]{tv_fd_time, tv_fd_pc, v_str_c, tv_fd_sc}, FeteDetailActivity.this);
        } else {
            Log.v("feteDetailModel", "feteDetailModel else " + feteDetailModel);
        }
    }

    private void populateText(LinearLayout ll, View[] views , Context mContext) {
        int widthSoFar = 0;
        Display display = getWindowManager().getDefaultDisplay();
//        ll.removeAllViews();
        int maxWidth = display.getWidth() - 20;

        /*LinearLayout.LayoutParams params;
        LinearLayout newLL = new LinearLayout(mContext);
        newLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newLL.setGravity(Gravity.LEFT);
        newLL.setOrientation(LinearLayout.HORIZONTAL);

        int widthSoFar = 0;*/

        for (int i = 0 ; i < views.length ; i++ ){
            /*LinearLayout LL = new LinearLayout(mContext);
            LL.setOrientation(LinearLayout.HORIZONTAL);
            LL.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
            LL.setLayoutParams(new ListView.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            //my old code
            //TV = new TextView(mContext);
            //TV.setText(textArray[i]);
            //TV.setTextSize(size);  <<<< SET TEXT SIZE
            //TV.measure(0, 0);
            views[i].measure(0,0);
            params = new LinearLayout.LayoutParams(views[i].getMeasuredWidth(),
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            //params.setMargins(5, 0, 5, 0);  // YOU CAN USE THIS
            //LL.addView(TV, params);
            ll.removeAllViews();
            LL.addView(views[i], params);
            LL.measure(0, 0);*/
            widthSoFar += views[i].getMeasuredWidth();// YOU MAY NEED TO ADD THE MARGINS
            if (widthSoFar >= maxWidth) {

                /*ll.addView(newLL);

                newLL = new LinearLayout(mContext);
                newLL.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                newLL.setOrientation(LinearLayout.HORIZONTAL);
                newLL.setGravity(Gravity.LEFT);
                params = new LinearLayout.LayoutParams(LL
                        .getMeasuredWidth(), LL.getMeasuredHeight());
                newLL.addView(LL, params);*/
                widthSoFar = ll.getMeasuredWidth();
                Log.e("Siva", "widthSoFar >= maxWidth");
            } else {
                tv_fd_sc.setVisibility(View.GONE);
                layout_fd_sc_newline.setVisibility(View.VISIBLE);
                Log.e("Siva", "widthSoFar >= maxWidth else");
//                newLL.addView(LL);
            }
        }
//        ll.addView(newLL);
    }


}
