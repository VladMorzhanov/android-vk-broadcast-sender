package com.vkbroadcast.vladm.vkbroadcastsender;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FriendsActivity extends Activity {

    private RecyclerView recyclerView;

    private static ArrayList<String> friendsNames;

    private ArrayList<String> friendsPhotoUrls;

    private static ArrayList<Boolean> checkedFriends;

    private static ArrayList<Drawable> friendsPhotos;

    private LinearLayout friendsProgressLayout;

    private static Context friendsActivityContext;

    private static FriendsAdapter friendsAdapter;

    /**
     * application toolbar object
     */
    private static Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        friendsActivityContext = this;

        setContentView(R.layout.activity_friends);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            toolbar.setBackgroundColor(ContextCompat.getColor(getApplication(), R.color.main_color));

            toolbar.setTitle("Choose friends");

            toolbar.setTitleTextColor(ContextCompat.getColor(getApplication(), R.color.vk_white));

        }

        friendsProgressLayout = (LinearLayout) findViewById(R.id.friendsLoadProgressLayout);

        friendsNames = new ArrayList<>();

        friendsPhotoUrls = new ArrayList<>();

        friendsPhotos = new ArrayList<>();

        checkedFriends = new ArrayList<>();

        /**
         * get all friends data from json object
         */
        VKRequest friendsRequest = VKApi
                .friends()
                .get(VKParameters.from(VKApiConst.FIELDS, "id,first_name,last_name,sex,bdate,city, photo_50"));

        friendsRequest.unregisterObject();


        if (friendsRequest == null) return;
        friendsRequest.executeWithListener(mRequestListener);

    }

    private void fillFriendsLayout(VKResponse response) {
        JSONArray res = null;
        try {
            res = response.json.getJSONObject("response").getJSONArray("items");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < res.length(); ++i) {
            try {

                String fname = res.getJSONObject(i).getString("first_name");
                String lname = res.getJSONObject(i).getString("last_name");
                String photo_url = res.getJSONObject(i).getString("photo_50");

                friendsNames.add(fname + " " + lname);

                friendsPhotoUrls.add(photo_url);

                checkedFriends.add(false);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        new ImageLoadTask(friendsPhotoUrls).execute();
    }

    VKRequest.VKRequestListener mRequestListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {

            fillFriendsLayout(response);

        }

        @Override
        public void onError(VKError error) {
        }

        @Override
        public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded,
                               long bytesTotal) {
            // you can show progress of the request if you want
        }

        @Override
        public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
        }
    };

    public class ImageLoadTask extends AsyncTask<Void, Void, ArrayList<Drawable>> {

        private ArrayList<String> urls;

        public ImageLoadTask(ArrayList<String> urls) {
            this.urls = urls;
        }

        @Override
        protected ArrayList<Drawable> doInBackground(Void... params) {

            ArrayList<Drawable> res = new ArrayList<>();

            for (int i = 0; i < urls.size(); ++i) {
                try {
                    URL urlConnection = new URL(urls.get(i));
                    HttpURLConnection connection = (HttpURLConnection) urlConnection
                            .openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);

                    res.add(new BitmapDrawable(getResources(), myBitmap));

                } catch (Exception e) {
                    e.printStackTrace();

                    return null;
                }
            }

            return res;
        }

        @Override
        protected void onPostExecute(ArrayList<Drawable> result) {
            super.onPostExecute(result);

            friendsProgressLayout.post(new Runnable() {
                @Override
                public void run() {
                    friendsProgressLayout.setVisibility(View.INVISIBLE);
                }
            });

            friendsPhotos = result;

            friendsAdapter = new FriendsAdapter(FriendsActivity.this);

            ListView lvMain = (ListView) findViewById(R.id.friendsList);

            lvMain.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (!checkedFriends.get(position)) {
                        view.setBackgroundColor(ContextCompat
                                .getColor(view.getContext(), R.color.checked_friend_item));
                    } else {
                        view.setBackgroundColor(Color.TRANSPARENT);
                    }

                    checkedFriends.set(position, !checkedFriends.get(position));
                }
            });

            lvMain.setAdapter(friendsAdapter);
        }
    }

    class FriendsAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater lInflater;

        FriendsAdapter(Context context) {
            ctx = context;
            lInflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // кол-во элементов
        @Override
        public int getCount() {
            return friendsNames.size();
        }

        // элемент по позиции
        @Override
        public Object getItem(int position) {
            return friendsNames.get(position);
        }

        // id по позиции
        @Override
        public long getItemId(int position) {
            return position;
        }

        // пункт списка
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // используем созданные, но не используемые view
            View view = convertView;

            if (view == null) {
                view = lInflater.inflate(R.layout.list_item, parent, false);
            }

            // заполняем View в пункте списка данными из товаров: наименование, цена
            // и картинка
            ((TextView) view.findViewById(R.id.text1)).setText(friendsNames.get(position));
            ((ImageView) view.findViewById(R.id.avatar)).setImageDrawable(friendsPhotos.get(position));

            if(checkedFriends.get(position))
            {
                view.setBackgroundColor(ContextCompat
                        .getColor(view.getContext(), R.color.checked_friend_item));
            }
            else
            {
                view.setBackgroundColor(Color.TRANSPARENT);
            }

            return view;
        }
    }
}