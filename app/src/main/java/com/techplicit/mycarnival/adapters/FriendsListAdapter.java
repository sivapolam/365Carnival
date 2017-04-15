package com.techplicit.mycarnival.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.techplicit.carnivalcommons.utils.UtilityCommon;
import com.techplicit.mycarnival.Listener;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.data.model.FriendsPojo;
import com.techplicit.mycarnival.ui.fragments.WhoAreMyFriendsFragment;
import com.techplicit.mycarnival.utils.BandsDateFormatsConverter;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.ImageLoader;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by pnaganjane001 on 14/11/15.
 */
public class FriendsListAdapter extends BaseAdapter implements Constants {

    private static final String TAG = FriendsListAdapter.class.getName();
    private final SharedPreferences sharedPreferences;

    private Activity context;
    LayoutInflater inflater;
    private ArrayList<FriendsPojo> bandsPojoArrayList;
    ImageLoader imageLoader;
    private FriendsPojo bandsPojo;
    private View rootView;
    private ViewHolder holder;
    Listener mCallbackListener;
    private ListView mFriendsListView;
    private TextView mEmptyText;

    public FriendsListAdapter(Activity context, ArrayList<FriendsPojo> carnivalsPojoArrayList, ListView listFriends, TextView textEmpty) {
        this.context = context;
        this.bandsPojoArrayList = carnivalsPojoArrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
        sharedPreferences = context.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        mFriendsListView = listFriends;
        mEmptyText = textEmpty;

        try {
            mCallbackListener = (Listener) new WhoAreMyFriendsFragment();
        } catch (Exception e) {
            Log.e(TAG, "mCallbackListener--> " + e.toString());
        }

    }

    @Override
    public int getCount() {
        return this.bandsPojoArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        holder = new ViewHolder();

        rootView = inflater.inflate(R.layout.friends_list_item, null);

        holder.friendImage = (ImageView) rootView.findViewById(R.id.image_friend);
        holder.imageFollowing = (ImageView) rootView.findViewById(R.id.image_following);

        holder.friendName = (TextView) rootView.findViewById(R.id.name_friend);
        holder.statusFriend = (TextView) rootView.findViewById(R.id.status_friend);
        holder.friendEmail = (TextView) rootView.findViewById(R.id.email_friend);
        holder.friendStatus = (TextView) rootView.findViewById(R.id.friend_status_text);
        holder.btnStatus1 = (Button) rootView.findViewById(R.id.status_btn1);
        holder.btnStatus2 = (Button) rootView.findViewById(R.id.status_btn2);
        holder.btnStatus3 = (Button) rootView.findViewById(R.id.status_btn3);
        holder.btnStatusCancel = (Button) rootView.findViewById(R.id.status_cancel_btn);

        bandsPojo = (FriendsPojo) this.bandsPojoArrayList.get(position);

        holder.btnStatus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FriendsPojo friendsPojo = (FriendsPojo) bandsPojoArrayList.get(position);

                if (sharedPreferences.getString(EMAIL, null) != null && !sharedPreferences.getString(EMAIL, null).equalsIgnoreCase("")) {
                    String userEmail = sharedPreferences.getString(EMAIL, null);

                    if (friendsPojo.getEmail() != null && !friendsPojo.getEmail().equalsIgnoreCase("")) {
                        String friendEmail = friendsPojo.getEmail();
                        String action = "reject";

                        try {
                            JSONObject object = new JSONObject();
                            object.put(ACK_USER_EMAIL, userEmail);
                            object.put(ACK_FRIEND_EMAIL, friendEmail);
                            object.put(ACK_ACTION, action);

                            new FriendAckTask(context, object, position).execute();
                        } catch (Exception e) {
                            Log.e(TAG, "btnStatus2 error---> " + e.toString());
                        }

                    }
                }


            }
        });

        holder.btnStatus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FriendsPojo friendsPojo = (FriendsPojo) bandsPojoArrayList.get(position);

                if (sharedPreferences.getString(EMAIL, null) != null && !sharedPreferences.getString(EMAIL, null).equalsIgnoreCase("")) {
                    String userEmail = sharedPreferences.getString(EMAIL, null);

                    if (friendsPojo.getEmail() != null && !friendsPojo.getEmail().equalsIgnoreCase("")) {
                        String friendEmail = friendsPojo.getEmail();
                        String action = "reject";

                        try {
                            JSONObject object = new JSONObject();
                            object.put(ACK_USER_EMAIL, userEmail);
                            object.put(ACK_FRIEND_EMAIL, friendEmail);
                            object.put(ACK_ACTION, action);

                            new FriendAckTask(context, object, position).execute();
                        } catch (Exception e) {
                            Log.e(TAG, "btnStatus2 error---> " + e.toString());
                        }

                    }
                }


            }
        });

        holder.btnStatusCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FriendsPojo friendsPojo = (FriendsPojo) bandsPojoArrayList.get(position);

                if (sharedPreferences.getString(EMAIL, null) != null && !sharedPreferences.getString(EMAIL, null).equalsIgnoreCase("")) {
                    String userEmail = sharedPreferences.getString(EMAIL, null);

                    if (friendsPojo.getEmail() != null && !friendsPojo.getEmail().equalsIgnoreCase("")) {
                        String friendEmail = friendsPojo.getEmail();
                        String action = "reject";

                        try {
                            JSONObject object = new JSONObject();
                            object.put(ACK_USER_EMAIL, friendEmail);
                            object.put(ACK_FRIEND_EMAIL, userEmail);
                            object.put(ACK_ACTION, action);

                            new FriendAckTask(context, object, position).execute();
                        } catch (Exception e) {
                            Log.e(TAG, "btnStatus2 error---> " + e.toString());
                        }

                    }
                }


            }
        });

        holder.btnStatus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.friendName = (TextView) v.findViewById(R.id.name_friend);
                holder.friendEmail = (TextView) v.findViewById(R.id.email_friend);
                holder.friendStatus = (TextView) v.findViewById(R.id.friend_status_text);
                holder.btnStatus1 = (Button) v.findViewById(R.id.status_btn1);
                holder.btnStatus2 = (Button) v.findViewById(R.id.status_btn2);
                holder.btnStatus3 = (Button) v.findViewById(R.id.status_btn3);

                FriendsPojo friendsPojo = (FriendsPojo) bandsPojoArrayList.get(position);

                if (sharedPreferences.getString(EMAIL, null) != null && !sharedPreferences.getString(EMAIL, null).equalsIgnoreCase("")) {
                    String userEmail = sharedPreferences.getString(EMAIL, null);

                    if (friendsPojo.getEmail() != null && !friendsPojo.getEmail().equalsIgnoreCase("")) {
                        String friendEmail = friendsPojo.getEmail();
                        String action = "accept";

                        try {
                            JSONObject object = new JSONObject();
                            object.put(ACK_USER_EMAIL, userEmail);
                            object.put(ACK_FRIEND_EMAIL, friendEmail);
                            object.put(ACK_ACTION, action);

                            new FriendAckTask(context, object, position).execute();
                        } catch (Exception e) {
                            Log.e(TAG, "btnStatus2 error---> " + e.toString());
                        }

                    }
                }

            }
        });

        if (bandsPojo.getfName() != null && bandsPojo.getlName() != null) {
            holder.friendName.setText(bandsPojo.getfName() + " " + bandsPojo.getlName());
        }

        if (bandsPojo.getEmail() != null) {
            holder.friendEmail.setText(bandsPojo.getEmail());
        }

        if (bandsPojo.getStatusMsg() != null) {

            if (bandsPojo.getStatusMsg().equalsIgnoreCase("requested")) {
                holder.btnStatus1.setVisibility(View.INVISIBLE);
                holder.btnStatus2.setVisibility(View.INVISIBLE);
                holder.btnStatus3.setVisibility(View.INVISIBLE);
                holder.imageFollowing.setVisibility(View.GONE);
                holder.friendStatus.setVisibility(View.VISIBLE);
                holder.friendStatus.setText("REQUEST SENT");
                holder.btnStatusCancel.setVisibility(View.VISIBLE);
                holder.statusFriend.setVisibility(View.GONE);
            }

            if (bandsPojo.getStatusMsg().equalsIgnoreCase("pending")) {
                holder.btnStatus1.setVisibility(View.VISIBLE);
                holder.btnStatus2.setVisibility(View.VISIBLE);
                holder.btnStatus3.setVisibility(View.INVISIBLE);
                holder.imageFollowing.setVisibility(View.GONE);
                holder.friendStatus.setVisibility(View.VISIBLE);
                holder.friendStatus.setText("REQUEST RECEIVED");
                holder.btnStatusCancel.setVisibility(View.GONE);
                holder.statusFriend.setVisibility(View.GONE);
            }

            if (bandsPojo.getStatusMsg().equalsIgnoreCase("accepted")) {
                holder.btnStatus1.setVisibility(View.INVISIBLE);
                holder.btnStatus2.setVisibility(View.INVISIBLE);
                holder.btnStatus3.setVisibility(View.VISIBLE);
                holder.btnStatus3.setText("UNFRIEND");
                holder.imageFollowing.setVisibility(View.VISIBLE);
                holder.friendStatus.setVisibility(View.VISIBLE);
                holder.friendStatus.setText("FOLLOWING");
                holder.btnStatusCancel.setVisibility(View.GONE);
                holder.statusFriend.setVisibility(View.VISIBLE);

                if (bandsPojo.getPrivacy().equalsIgnoreCase("ON")) {
                    holder.statusFriend.setText("Privacy enabled");
                } else if (bandsPojo.getLastUpdated() == 0) {
                    holder.statusFriend.setText("Location Not Updated");
                } else if (bandsPojo.getPrivacy().equalsIgnoreCase("OFF") && bandsPojo.getLastUpdated() != 0) {
                    String lastAccessOn = UtilityCommon.getDate(Long.valueOf(bandsPojo.getLastUpdated()), "MM/dd/yyyy HH:mm:ss");

                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    Calendar c = Calendar.getInstance();
                    String currentDate = format.format(c.getTime());

                    Date d1 = new Date();
                    Date d2 = null;

                    try {
                        d1 = format.parse(currentDate);
                        d2 = format.parse(lastAccessOn);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // in milliseconds
                    long diff = d1.getTime() - d2.getTime();

                    String lastUpdateStatus = BandsDateFormatsConverter.printDateDifferenceInUIWithDefinedFormat(diff);

                    if (lastUpdateStatus != null && !lastUpdateStatus.equalsIgnoreCase("")) {
                        holder.statusFriend.setText("Last Updated : " + lastUpdateStatus);
                    }
                }

            }

        }

        rootView.setId(position);

        if (bandsPojo.getImage() != null) {
            Picasso.with(context).load(bandsPojo.getImage()).into(holder.friendImage);

//            imageLoader.DisplayImage(bandsPojo.getImage(), holder.friendImage);
        }

        return rootView;
    }

    public class ViewHolder {
        ImageView friendImage, imageFollowing;
        TextView friendName, statusFriend;
        TextView friendEmail;
        TextView friendStatus;
        Button btnStatus1, btnStatus2, btnStatus3, btnStatusCancel;
    }


    class FriendAckTask extends AsyncTask<String, String, String> implements Constants {
        private SharedPreferences sharedPreferences;
        ServiceHandler jsonParser = new ServiceHandler();

        private Activity mContext;
        private String responseStatus;
        private JSONObject object;
        private String inviteStatus;
        private int mPosition;
        private ProgressDialog pd;

        public FriendAckTask(Activity context, JSONObject jsonObject, int position) {
            mContext = context;
            object = jsonObject;
            sharedPreferences = mContext.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
            mPosition = position;

        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setMessage("Sending your response");
            pd.show();

        }

        @Override
        protected String doInBackground(String... args) {

            try {
                responseStatus = jsonParser.makePUTRequest(
                        REQUEST_ACKNOWLEDGE_URL, object);
                return responseStatus;
            } catch (Exception e) {
                e.printStackTrace();
                responseStatus = ERROR;
                return responseStatus;
            }

        }

        protected void onPostExecute(String responseStatus) {

            if (pd != null) {
                pd.dismiss();
            }

            Log.e(TAG, "responseStatus--> " + responseStatus);

            if (responseStatus != null) {

                if (responseStatus != null && !responseStatus.equals(ERROR)) {

                    try {
                        JSONObject jsonObject = new JSONObject(responseStatus);
                        inviteStatus = jsonObject.optString(STATUS_INVITE);
                        if (inviteStatus != null) {
                            if (!inviteStatus.equalsIgnoreCase("success")) {
                                JSONArray jsonArray = jsonObject.optJSONArray(EXPLANATION_INVITE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    inviteStatus = Utility.getErrorMessage(object);

                                    Utility.displayNetworkFailDialog(mContext, STATUS, "Error", inviteStatus);

                                }
                            } else {

                                String ackStatus = jsonObject.optString(EXPLANATION_INVITE);

                                if (ackStatus.equalsIgnoreCase("accepted")) {


                                } else if (ackStatus.equalsIgnoreCase("rejected")) {
                                    if (bandsPojoArrayList.size() > 0) {
                                        bandsPojoArrayList.remove(mPosition);
                                        FriendsListAdapter.this.notifyDataSetChanged();
                                        FriendsListAdapter.this.notifyDataSetInvalidated();
                                    }
                                }

                                String userEmail = sharedPreferences.getString(EMAIL, null);

                                mCallbackListener.onFriendAcceptedListener(mContext, userEmail, mFriendsListView, mEmptyText);

                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    Utility.displayNetworkFailDialog(mContext, ERROR, "", "");
                }

            }
        }

    }
}
