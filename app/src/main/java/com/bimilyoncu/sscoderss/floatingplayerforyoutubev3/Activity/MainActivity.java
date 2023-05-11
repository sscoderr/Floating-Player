package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;
import androidx.viewpager.widget.ViewPager;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.AdapterSearchVideo;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.CustomAdapterForFragments;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Connector.YoutubeConnector;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.NetControl;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.LoadSelectKey;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Floaties.Floaty;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Floaties.JSInterface;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Adapters.AdapterForSimilarVideos;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Result;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.ApiClient;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.ebanx.swipebtn.OnActiveListener;
import com.ebanx.swipebtn.SwipeButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.CheckService;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.CounterForSimilarVideos;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.checkRepeat;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.checkSuffle;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.currentVItem;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.mListForFloat;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.similarVideosList;


public class MainActivity extends AppCompatActivity implements OnScrollListener {
    private static final int NOTIFICATION_ID = 1500;
    private static final int PERMISSION_REQUEST_CODE = 16;

    private ProgressBar myPg;

    // mypg

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View myView;
    private Handler mHandler;

    private int[] tabIcons = {R.mipmap.trend_music_icon, R.mipmap.trend_video_icon, R.mipmap.favorite_video_icon};
    private int sizeOfMoreData = 1;
    private boolean isLoading = false;


    private YouTube.Videos.List queryTwo;
    private List<VideoItem> items = new ArrayList<>();
    private YouTube youtube;

    private NetControl netControl;
    private String txtValueFromServer="777";


    private List<com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Video> results;
    private List<com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Video> serviceSearchResults;

    public static int appVers = 0;
    public static int versionCode = 0;
    //private SeekBar mSeekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        TelephonyManager tm = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        MSettings.countryCode = tm.getNetworkCountryIso();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        CacheClear(this);
        Log.e("","");
        MSettings.activeActivity = MainActivity.this;
        MSettings.mainActivity = MainActivity.this;
        PackageManager manager = MSettings.activeActivity.getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(MSettings.activeActivity.getPackageName(), PackageManager.GET_ACTIVITIES);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // API'yi getAlertKeys metodunun içinde jsondan çekiyor.
        // YoutubeConnector.KEY = YoutubeConnector.myApiKeys[(new Random()).nextInt(YoutubeConnector.myApiKeys.length)];

        netControl = new NetControl(this);
        (MainActivity.this).getSupportActionBar().setElevation(0);
        AlertgetKeys();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            loadNotificationForAboveAndroidO();
        /*mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    MSettings.webView.loadUrl(String.format("javascript:seekTo(\"%s\");", new Object[]{progress}));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!CheckService){
            if (currentVItem != null){
                currentVItem.setId("aawdawddfgwdda");
                MSettings.webView.loadUrl(String.format("javascript:loadVideoById(\"%s\",\"highres\");", new Object[]{"sdfsdfwefgds"}));
                MSettings.floaty.stopService();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void loadNotificationForAboveAndroidO(){
        String NOTIFICATION_CHANNEL_ID = MSettings.activeActivity.getResources().getString(R.string.notification_channel_id);
        String channelName = "bgservice";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
    }

    public static void CacheClear(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);

            Log.d(".. MainActivity ..", "CasheClear Successful!!! ...");
        } catch (Exception e) {
            Log.e(".. MainActivity ..", "CasheClear Catch!!! ...");
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        try {
            if (dir != null && dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
                return dir.delete();
            } else if (dir != null && dir.isFile()) {
                return dir.delete();
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    SharedPreferences.Editor editor;
    private void showTwitterAlert() {
        SharedPreferences preferences = getSharedPreferences("twitterAlert", MODE_PRIVATE);
        try {

            editor = preferences.edit();
            if (!preferences.contains("twitterAlert")) {
                editor.putInt("twitterAlert", 0);
            } else if (preferences.getInt("twitterAlert", 0) >= 12) {
                editor.putInt("twitterAlert", 0);
                View checkBoxView = View.inflate(this, R.layout.twitter_dialog, null);
                final CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(MainActivity.this.getResources().getString(R.string.rate_title));
                builder.setMessage(MainActivity.this.getResources().getString(R.string.rate_message))
                        .setView(checkBoxView)
                        .setCancelable(false)
                        .setPositiveButton(MainActivity.this.getResources().getString(R.string.rate_now), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                editor.putInt("forTwitter", 11);
                                dialog.dismiss();
                                dialog.cancel();
                                openWebsite("https://play.google.com/store/apps/details?id=com.bimilyoncu.sscoderss.floatingplayerforyoutubev3", MSettings.activeActivity);
                                editor.commit();
                            }
                        })
                        .setNegativeButton(MainActivity.this.getResources().getString(R.string.rate_later), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (checkBox.isChecked()){
                                    editor.putInt("forTwitter", 11);
                                }
                                else editor.putInt("forTwitter", 22);
                                editor.commit();
                                dialog.dismiss();
                                dialog.cancel();
                            }
                        }).show();
            } else {
                int value = preferences.getInt("twitterAlert", 0);
                editor.putInt("twitterAlert", value + 1);
            }
            editor.commit();
        } catch (Exception e) {

        }
    }

    private void AlertgetKeys() {
        if (!netControl.isOnline()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.internetConnectionMessage))
                    .setNegativeButton(MainActivity.this.getString(R.string.internetConnectionQuit), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setPositiveButton(MainActivity.this.getString(R.string.internetConnectionTryAgain), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    AlertgetKeys();
                }
            }).setCancelable(false).create().show();
        } else {
            if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
                watchYoutubeVideo(getIntent().getAction());
            }

            SharedPreferences preferences = getSharedPreferences("getKeyFromServerCounter", MODE_PRIVATE);
            try {
                SharedPreferences.Editor editor = preferences.edit();
                if (!preferences.contains("getKeyFromServerCounter")) {
                    editor.putInt("getKeyFromServerCounter", 0);
                    new LoadSelectKey(MainActivity.this);
                } else {
                    int value = preferences.getInt("getKeyFromServerCounter", 0);
                    editor.putInt("getKeyFromServerCounter", value + 1);
                }
                editor.commit();
            } catch (Exception e) {
            }
            int counter = preferences.getInt("getKeyFromServerCounter", 0);
            if (counter > 5) {
                ReadFileTask tsk = new ReadFileTask();
                tsk.execute(MainActivity.this.getResources().getString(R.string.apiKeyUpdateLink));
            } else
                LoadKeysFromPreferences();

///            YoutubeConnector.KEY = "AIzaSyA-zwUZMDk91YccFjYT3W1AaEISxDr9KX0";

            mHandler = new MyHandler();

            /*IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            BroadcastReceiver mReceiver = new BroadcastPowerButton();
            registerReceiver(mReceiver, filter);*/
            LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myView = li.inflate(R.layout.loading_result, null);
            connectiveStart();
            try {
                SharedPreferences preferencesTwitter = getSharedPreferences("twitterAlert", MODE_PRIVATE);
                preferencesTwitter.edit();
                if (!preferencesTwitter.contains("forTwitter")) {
                    showTwitterAlert();
                } else if ((preferencesTwitter.getInt("forTwitter", 0) == 22))
                    showTwitterAlert();
            } catch (Exception e) {

            }

        }
    }

    private class ReadFileTask extends AsyncTask<String,Integer,Void> {

        protected Void doInBackground(String... params) {
            URL url;
            try {
                //create url object to point to the file location on internet
                url = new URL(params[0]);
                //make a request to server
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //get InputStream instance
                InputStream is = con.getInputStream();
                //create BufferedReader object
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                //read content of the file line by line
                txtValueFromServer=br.readLine();
                br.close();

            } catch (Exception e) {
                e.printStackTrace();
                //close dialog if error occurs
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SharedPreferences preferencesVersion = getSharedPreferences("keysList", MODE_PRIVATE);
            if (Integer.parseInt(txtValueFromServer)!=preferencesVersion.getInt("apiKeysVersion",0)) {
                new LoadSelectKey(MainActivity.this);
            }else {
                LoadKeysFromPreferences();
            }
            SharedPreferences preferences = getSharedPreferences("getKeyFromServerCounter", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("getKeyFromServerCounter", 0);
            editor.commit();
            super.onPostExecute(aVoid);
        }
    }

    protected void LoadKeysFromPreferences(){
        try {
            SharedPreferences preferences = getSharedPreferences("keysList", MODE_PRIVATE);
            Set<String> fetch = preferences.getStringSet("keysList", null);
            List<String> list=null;
            if (fetch!=null)
                list = new ArrayList<String>(fetch);
            if (list.size() > 0)
                YoutubeConnector.KEY = list.get((new Random()).nextInt(list.size()));
        }catch (Exception e){
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_mainactivity, menu);
        MenuItem item = menu.findItem(R.id.checkHighQuality);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (preferences.getBoolean("isHighQuality", false)) {
            item.setChecked(true);
        } else if (!preferences.getBoolean("isHighQuality", false)) {
            item.setChecked(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search_button) {
            Intent myIntent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(myIntent);
        }
        //PREMIUM
        /*else if (item.getItemId() == R.id.premium) {
        }*/else if (item.getItemId() == R.id.checkHighQuality) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            if (item.isChecked()) {
                editor.putBoolean("isHighQuality", false);
                item.setChecked(false);
            } else {
                item.setChecked(true);
                editor.putBoolean("isHighQuality", true);
            }
            editor.commit();
        } else if (item.getItemId() == R.id.rate_button) {
            Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
            }
        } else if (item.getItemId() == R.id.share_button) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } /*else if (item.getItemId() == R.id.help_button) {
            startActivity(new Intent(MainActivity.this, SlideOnboardingActivity.class));
        }*/
        else if (item.getItemId() == R.id.twitter_button) {
            openWebsite("https://twitter.com/sscoderr",MainActivity.this);
        }

        return super.onOptionsItemSelected(item);
    }
    private void openWebsite(String url, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    private void connectiveStart() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new CustomAdapterForFragments(getSupportFragmentManager()));
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //viewPager.setCurrentItem(tab.getPosition());/*Removed For Tab Problem*/
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Toast.makeText(MSettings.activeActivity, MSettings.activeActivity.getString(R.string.permissionMessage), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + MainActivity.this.getPackageName()));
                startActivityForResult(intent, PERMISSION_REQUEST_CODE);
            }
        }
        if (!MSettings.CheckService) {
            loadFloatWindow();
        }


        MSettings.mListForFloat.setOnScrollListener(this);
        addClickListener();
        new appVersionController().execute();
    }
    private boolean justLeaveFromPrevious=false;
    private boolean justLeaveFromNext=false;
    private void loadFloatWindow() {
        if (MSettings.head == null)
            MSettings.head = LayoutInflater.from(this).inflate(R.layout.float_head, null);
        if (MSettings.body == null)
            MSettings.body = LayoutInflater.from(this).inflate(R.layout.float_body, null);
        final ImageView imageViewClose = (ImageView) MSettings.body.findViewById(R.id.imageViewServiceClose);
        final ImageView imageViewMinimize = (ImageView) MSettings.body.findViewById(R.id.imageViewServiceMinimize);
        final ImageView imageViewHelp = (ImageView) MSettings.body.findViewById(R.id.imageViewServiceHelp);
        //mSeekbar=(SeekBar)MSettings.body.findViewById(R.id.seekBar);
        final ImageView imageViewHeadServiceClose = (ImageView) MSettings.head.findViewById(R.id.imageViewForClose);
        MSettings.imageViewMinimizeFP = (ImageView) MSettings.body.findViewById(R.id.imageViewMinimizeFP);

        MSettings.relOne = (RelativeLayout) MSettings.body.findViewById(R.id.relativeone);
        MSettings.relPowerSaver = (RelativeLayout) MSettings.body.findViewById(R.id.relative_power_saver);
        MSettings.linearOne = (LinearLayout) MSettings.body.findViewById(R.id.linearone);
        MSettings.linearTopControls = (LinearLayout) MSettings.body.findViewById(R.id.linear_top_controls);
        MSettings.linearTwo = (LinearLayout) MSettings.body.findViewById(R.id.lineartwo);
        MSettings.linearCizgi = (LinearLayout) MSettings.body.findViewById(R.id.cizgi);
        MSettings.linearControls = (LinearLayout) MSettings.body.findViewById(R.id.linearcontrols);

        SwipeButton swpButton = (SwipeButton) MSettings.body.findViewById(R.id.swipe_btn);
        MSettings.relPowerSaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MSettings.isPowerSaverMode) {
                    MSettings.floaty.params.screenBrightness = 10;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3100);
                                if (MSettings.activeActivity!=null)
                                    MSettings.activeActivity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            if (MSettings.isPowerSaverMode) {
                                                MSettings.floaty.params.screenBrightness = 0;
                                                MSettings.floaty.windowManager.updateViewLayout(MSettings.floaty.mLinearLayout, MSettings.floaty.params);
                                            }
                                        }
                                    });
                            } catch (Exception e) {
                            }
                        }
                    }).start();
                    MSettings.floaty.windowManager.updateViewLayout(MSettings.floaty.mLinearLayout, MSettings.floaty.params);
                }
            }
        });
        swpButton.setOnActiveListener(new OnActiveListener() {
            @Override
            public void onActive() {
                MSettings.isPowerSaverMode=false;
                MSettings.MaximizePlayerForPowerSaver();
            }
        });

        imageViewHeadServiceClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadFullScreenAds();
                MSettings.webView.loadUrl("javascript:stopVideo();");
                MSettings.webView.onPause();//yuklenen benzer videolara bak
                MSettings.floaty.stopService();
            }
        });

        imageViewMinimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MSettings.SmallPlayer();
                MSettings.floaty.params.x = MSettings.floaty.clickLocation[0];
                MSettings.floaty.params.y = MSettings.floaty.clickLocation[1] - 36;
                MSettings.floaty.params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                MSettings.floaty.params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                MSettings.floaty.params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                MSettings.floaty.mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
                MSettings.floaty.windowManager.updateViewLayout(MSettings.floaty.mLinearLayout, MSettings.floaty.params);
            }
        });

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MSettings.webView.loadUrl("about:blank");
                LoadFullScreenAds();
                MSettings.webView.loadUrl("javascript:stopVideo();");
                MSettings.webView.onPause();//yuklenen benzer videolara bak
                //MSettings.webView.pauseTimers();
                MSettings.floaty.stopService();
            }
        });

        imageViewHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MSettings.isPowerSaverMode=true;
                MSettings.MinimizePlayerForPowerSaver();
            }
        });

        MSettings.imageViewMinimizeFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MSettings.LoadSixTapAds();
                if (MSettings.floaty.getBody().getVisibility() != View.GONE) {
                    MSettings.floaty.getHead().setVisibility(View.VISIBLE);
                    MSettings.floaty.getBody().setVisibility(View.GONE);
                }
                MSettings.MinimizePlayer();
            }
        });

        final ImageView imgRepeat = (ImageView) MSettings.body.findViewById(R.id.img_repeat);
        final ImageView imgSuffle = (ImageView) MSettings.body.findViewById(R.id.img_suffle);
        final ImageView imageViewStopFinishVideo = (ImageView) MSettings.body.findViewById(R.id.img_stopingfinishvideo);
        imgRepeat.setImageResource(R.mipmap.repeat_icon_for_float2);
        imgRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkRepeat) {
                    checkRepeat = true;
                    imgRepeat.setImageResource(R.mipmap.repeat_black_icon_for_float2);
                    Toast.makeText(MainActivity.this, getString(R.string.repeatMessageOn), Toast.LENGTH_SHORT).show();
                } else {
                    checkRepeat = false;
                    MSettings.isLaterRepeate = true;
                    imgRepeat.setImageResource(R.mipmap.repeat_icon_for_float2);
                    Toast.makeText(MainActivity.this, getString(R.string.repeatMessageOff), Toast.LENGTH_SHORT).show();
                }


                MSettings.IsRetry = true;
                checkSuffle = false;
                imgSuffle.setImageResource(R.mipmap.suffle_icon_for_float2);
            }
        });
        imgSuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkSuffle) {
                    checkSuffle = true;
                    MSettings.suffleVideo();
                    imgSuffle.setImageResource(R.mipmap.suffle_black_icon_for_float2);
                    Toast.makeText(MainActivity.this, getString(R.string.suffleMessageOn), Toast.LENGTH_SHORT).show();
                } else {
                    checkSuffle = false;
                    CounterForSimilarVideos = 0;
                    imgSuffle.setImageResource(R.mipmap.suffle_icon_for_float2);
                    Toast.makeText(MainActivity.this, getString(R.string.suffleMessageOff), Toast.LENGTH_SHORT).show();
                }

                checkRepeat = false;
                imgRepeat.setImageResource(R.mipmap.repeat_icon_for_float2);
            }
        });
        imageViewStopFinishVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MSettings.videoFinishStopVideo) {
                    MSettings.videoFinishStopVideo = true;

                    imageViewStopFinishVideo.setImageResource(R.mipmap.autoplay_on2);
                    Toast.makeText(MainActivity.this, getString(R.string.stopfinishvideoOff), Toast.LENGTH_SHORT).show();
                } else {
                    MSettings.videoFinishStopVideo = false;

                    imageViewStopFinishVideo.setImageResource(R.mipmap.autoplay_off2);
                    Toast.makeText(MainActivity.this, getString(R.string.stopfinishvideoOn), Toast.LENGTH_SHORT).show();
                }
            }
        });

        MSettings.imagePlayPause = (ImageView) MSettings.body.findViewById(R.id.control_play_pause);
        final ImageView imageNext = (ImageView) MSettings.body.findViewById(R.id.control_forward);
        final ImageView imagePrevious = (ImageView) MSettings.body.findViewById(R.id.control_backward);

        MSettings.imagePlayPauseMainPlayer = (ImageView) MSettings.body.findViewById(R.id.player_play_pause);
        final ImageView imageNextMainPlayer = (ImageView) MSettings.body.findViewById(R.id.player_next);
        final ImageView imagePreviousMainPlayer = (ImageView) MSettings.body.findViewById(R.id.player_previous);
        MSettings.imagePlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MSettings.webView.loadUrl("javascript:playPause();");
            }
        });
        MSettings.imagePlayPauseMainPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MSettings.webView.loadUrl("javascript:playPause();");
            }
        });
        imageNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MSettings.similarVideosList.size()>=MSettings.CounterForSimilarVideos) {
                    if (justLeaveFromPrevious)
                        MSettings.LoadVideo();
                    MSettings.LoadVideo();
                    justLeaveFromPrevious = false;
                    justLeaveFromNext = true;
                }
            }
        });
        imagePrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MSettings.CounterForSimilarVideos>=2) {
                    if (justLeaveFromNext)
                        MSettings.currentVItem = MSettings.similarVideosList.get(MSettings.CounterForSimilarVideos -= 3);
                    else MSettings.currentVItem = MSettings.similarVideosList.get(MSettings.CounterForSimilarVideos -= 2);
                    justLeaveFromNext=false;
                    justLeaveFromPrevious=true;
                    MSettings.LoadVideo();
                }
            }
        });
        imageNextMainPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MSettings.similarVideosList.size()>=MSettings.CounterForSimilarVideos) {
                    if (justLeaveFromPrevious)
                        MSettings.LoadVideo();
                    MSettings.LoadVideo();
                    justLeaveFromPrevious = false;
                    justLeaveFromNext = true;
                }
            }
        });
        imagePreviousMainPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MSettings.CounterForSimilarVideos>=2) {
                    if (justLeaveFromNext)
                        MSettings.currentVItem = MSettings.similarVideosList.get(MSettings.CounterForSimilarVideos -= 3);
                    else MSettings.currentVItem = MSettings.similarVideosList.get(MSettings.CounterForSimilarVideos -= 2);
                    justLeaveFromNext=false;
                    justLeaveFromPrevious=true;
                    MSettings.LoadVideo();
                }
            }
        });

        final RelativeLayout developedYoutube = (RelativeLayout) MSettings.body.findViewById(R.id.relative_service_with_youtube);
        final RelativeLayout Similar = (RelativeLayout) MSettings.body.findViewById(R.id.relative_service_similar);
        final RelativeLayout Search = (RelativeLayout) MSettings.body.findViewById(R.id.relative_service_search);
        final RelativeLayout rlSimilar = (RelativeLayout) MSettings.body.findViewById(R.id.relativesimilar);
        final LinearLayout rlSearch = (LinearLayout) MSettings.body.findViewById(R.id.relativesearch);
        final TextView textViewServiceSimilar = (TextView) MSettings.body.findViewById(R.id.text_service_similar);
        final TextView textViewServiceSearch = (TextView) MSettings.body.findViewById(R.id.text_service_search);

        textViewServiceSimilar.setTypeface(Typeface.createFromAsset(MSettings.activeActivity.getAssets(), "VarelaRound-Regular.ttf"));
        textViewServiceSearch.setTypeface(Typeface.createFromAsset(MSettings.activeActivity.getAssets(), "VarelaRound-Regular.ttf"));

        developedYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchYoutubeVideo(MSettings.youtubeWatchURL + MSettings.activeVideo.getId());
            }
        });

        Similar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlSimilar.setVisibility(View.VISIBLE);
                rlSearch.setVisibility(View.GONE);

                Similar.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_custom_button_selected));
                Search.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_my_custom_button));

                textViewServiceSimilar.setTextColor(Color.parseColor("#FFFFFF"));
                textViewServiceSearch.setTextColor(Color.parseColor("#424242"));
            }
        });

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlSimilar.setVisibility(View.GONE);
                rlSearch.setVisibility(View.VISIBLE);

                Similar.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_my_custom_button));
                Search.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_custom_button_selected));

                textViewServiceSimilar.setTextColor(Color.parseColor("#424242"));
                textViewServiceSearch.setTextColor(Color.parseColor("#FFFFFF"));
            }
        });

        ServiceSearch();
        if (Floaty.notBuilder==null)
            Floaty.resetNotification(false);
        if (Floaty.notificationManager==null)
            Floaty.notificationManager = ((NotificationManager)MainActivity.this.getSystemService(NOTIFICATION_SERVICE));
        MSettings.floaty = Floaty.createInstance(MainActivity.this, MSettings.head, MSettings.body, NOTIFICATION_ID, Floaty.notBuilder.build());
        MSettings.mHandler = new Handler();
        MSettings.webView = (WebView) MSettings.body.findViewById(R.id.mWebView);
        MSettings.mListForFloat = (ListView) MSettings.body.findViewById(R.id.mListForFloat);
        MSettings.activeActivity = MainActivity.this;
        loadWebView(MSettings.webView);
        //getSimilarVideos();
    }

    private boolean isFLoad = false;
    EditText searchText;
    List<VideoItem> searchResults;

    private void ServiceSearch() {
        searchText = (EditText) MSettings.body.findViewById(R.id.editText_searchservice);
        final ListView listViewKey = (ListView) MSettings.body.findViewById(R.id.service_search_listview);
        final ListView listViewVideo = (ListView) MSettings.body.findViewById(R.id.service_searchvideo_listview);
        final ProgressBar progressBar = (ProgressBar) MSettings.body.findViewById(R.id.service_search_progressbar);

        searchText.setTypeface(Typeface.createFromAsset(MSettings.activeActivity.getAssets(), "VarelaRound-Regular.ttf"));
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (CheckService != null) {
                    if (!String.valueOf(charSequence).equals("")) {
                        MSettings.serviceSearchKey = String.valueOf(charSequence);
                        new MSettings.threadSearchKey().execute();

                        listViewKey.setVisibility(View.VISIBLE);
                        listViewVideo.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        ImageButton imageButtonSearch = (ImageButton) MSettings.body.findViewById(R.id.imageButtonServiceSearch);
        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckService != null) {
                    if (MSettings.serviceSearchKey != null) {
                        if (!MSettings.serviceSearchKey.equals("")) {
                            listViewVideo.setVisibility(View.GONE);
                            listViewKey.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);

                            listViewVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    if (searchResults.get(i).getId() != null) {
                                        MSettings.CounterForSimilarVideos = 1;
                                        MSettings.currentVItem = searchResults.get(i);
                                        MainActivity mainActivity = new MainActivity();
                                        mainActivity.getSimilarVideos(String.valueOf(searchResults.get(i).getId()), false, false, false, new String[]{});
                                        MSettings.IsRetry = false;
                                        MSettings.videoFinishStopVideoClicked = true;
                                        MSettings.LoadVideo();
                                        MSettings.LoadSixTapAds();
                                    }
                                }
                            });

                            com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search.ApiInterface apiService = ApiClient.getClient().create(com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search.ApiInterface.class);
                            Call<Result> call = apiService.getResult(searchText.getText().toString(),"video");
                            call.enqueue(new Callback<Result>() {
                                @Override
                                public void onResponse(Call<Result> call, Response<Result> response) {
                                    serviceSearchResults = response.body().getVideo();
                                    searchResults=new ArrayList<>();
                                    for (com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Video vd:serviceSearchResults) {
                                        VideoItem itm=new VideoItem();
                                        itm.setId(vd.getId());
                                        itm.setTitle(vd.getTitle());
                                        itm.setChanelTitle(vd.getUsername(),false);
                                        itm.setThumbnailURL(vd.getThumbnailSrc());
                                        searchResults.add(itm);
                                    }
                                    AdapterForSimilarVideos mAdapter = new AdapterForSimilarVideos(MSettings.activeActivity,serviceSearchResults);
                                    listViewVideo.setAdapter(null);
                                    listViewVideo.setAdapter(mAdapter);

                                    progressBar.setVisibility(View.GONE);
                                    listViewKey.setVisibility(View.GONE);
                                    listViewVideo.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onFailure(Call<Result> call, Throwable t) {
                                    Log.e("Error", t.toString());
                                }
                            });

                            /*new Thread() {
                                public void run() {
                                    ServiceSearchConnector yc = new ServiceSearchConnector(MSettings.activeActivity, "relevance", "video", false, "", false, false);
                                    searchResults = yc.search(MSettings.serviceSearchKey, true, false, false);
                                    mHandler.post(new Runnable() {
                                        public void run() {
                                            try {
                                                AdapterServiceSearchVideo adapter = new AdapterServiceSearchVideo(MSettings.activeActivity, searchResults, "");
                                                listViewVideo.setAdapter(adapter);

                                                progressBar.setVisibility(View.GONE);
                                                listViewKey.setVisibility(View.GONE);
                                                listViewVideo.setVisibility(View.VISIBLE);
                                            } catch (Exception e) {
                                                e.printStackTrace();

                                                if (!(new NetControl(MSettings.activeActivity)).isOnline()) {
                                                    Toast.makeText(MSettings.activeActivity, MSettings.activeActivity.getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    });
                                }
                            }.start();*/
                        }
                    }
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void startFloatyForAboveAndroidL() {
        if (!Settings.canDrawOverlays(MSettings.activeActivity)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + MSettings.activeActivity.getPackageName()));
            startActivityForResult(intent, PERMISSION_REQUEST_CODE);
        } else {
            MSettings.floaty.startService();
        }
    }

    public void loadWebView(final WebView wv) {
        /*if (Build.VERSION.SDK_INT >= PERMISSION_REQUEST_CODE) {
            wv.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }*/
        /*wv.setClickable(false);
        wv.setFocusable(false);
        wv.clearCache(true);
        WebSettings settings = wv.getSettings();
        if (Build.VERSION.SDK_INT >= 17)
            settings.setMediaPlaybackRequiresUserGesture(false);
        if (Build.VERSION.SDK_INT >= 19)/*Kapatılabibilr
            wv.setWebContentsDebuggingEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        settings.setBuiltInZoomControls(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDisplayZoomControls(false);
        settings.setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.117 Safari/537.36");*/
        /*****************************/
        /*if (Build.VERSION.SDK_INT >= 17)
            wv.getSettings().setMediaPlaybackRequiresUserGesture(false);
        if (Build.VERSION.SDK_INT >= 19)
            wv.setLayerType(2,null);
        else wv.setLayerType(1,null);*/
        if (Build.VERSION.SDK_INT >= 17) {
            wv.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        wv.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebChromeClient(new WebChromeClient());
        wv.getSettings().setSupportZoom(false);
        wv.setHorizontalScrollBarEnabled(false);
        wv.setVerticalScrollBarEnabled(false);
        wv.setWebViewClient(new WebViewClient() {
                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                                    Log.e(".........", String.valueOf(MainActivity.this));

                                    watchYoutubeVideo(url);
                                    return true;
                                }

                                @Override
                                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                    Log.e("Started Load","uri:"+url);
                                    super.onPageStarted(view, url, favicon);
                                }

                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    Log.e("Finished Load","uri:"+url);
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MSettings.activeActivity);
                                    if (preferences != null) {
                                        if (preferences.getBoolean("isHighQuality", false)) {
                                            if (MSettings.webView != null && MSettings.currentVItem != null) {
                                                MSettings.webView.loadUrl(String.format("javascript:loadVideoById(\"%s\",\"highres\");", new Object[]{MSettings.currentVItem.getId()}));
                                            }
                                        } else {
                                            if (MSettings.webView != null && MSettings.currentVItem != null) {
                                                MSettings.webView.loadUrl(String.format("javascript:loadVideoById(\"%s\",\"small\");", new Object[]{MSettings.currentVItem.getId()}));
                                            }
                                        }
                                    }

                                    super.onPageFinished(view, url);
                                }
                            }
        );
        //wv.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
        //wv.getSettings().setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.117 Safari/537.36");
        wv.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.135 Mobile Safari/537.36");
        wv.addJavascriptInterface(new JSInterface(wv), "WebPlayerInterface");
        wv.loadUrl(MSettings.URL);
    }

    private void watchYoutubeVideo(final String url) {
        try {
            final String urlHead = "https://www.youtube.com";

            if (url.substring(0, urlHead.length()).equals(urlHead)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(getString(R.string.openVideoonYouTube));
                builder.setTitle(getString(R.string.AreYouSure));
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MSettings.activeActivity.startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(url)));
                            }
                        });

                builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
                MSettings.MinimizePlayer();
            }
        } catch (Exception e) {
            e.printStackTrace();

            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            intent.setAction(url);
            startActivity(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                MSettings.floaty.startService();
            } else {
                Spanned message = Html.fromHtml("Please allow this permission, so <b>Floaties</b> could be drawn.");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }*/
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        /*if (view.getLastVisiblePosition() == totalItemCount - 1 && MSettings.mListForFloat.getCount() >= 4 && isLoading == false && sizeOfMoreData != 0 && !MSettings.isUserVideo) {
            if (mListForFloat.getCount()<60) {
                isLoading = true;
                Thread thread = new MainActivity.ThreadGetMoreData();
                thread.start();
            }
        }*/
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    MSettings.mListForFloat.addFooterView(myView);
                    break;
                case 1:
                    MSettings.mAdapter.addListItemToAdapter((ArrayList<VideoItem>) msg.obj);
                    MSettings.mListForFloat.removeFooterView(myView);
                    isLoading = false;
                    break;
                default:
                    break;
            }
        }
    }

    public class ThreadGetMoreData extends Thread {
        @Override
        public void run() {
            try {
                mHandler.sendEmptyMessage(0);
                YoutubeConnector yc = new YoutubeConnector(MSettings.activeActivity, MSettings.currentVItem.getId(), "", false, "", true, false);
                List<VideoItem> list = yc.search("", false, false, true);
                sizeOfMoreData = list.size();
                ArrayList<VideoItem> lstResult = (ArrayList<VideoItem>) list;
                Message msg = mHandler.obtainMessage(1, lstResult);
                mHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();

                if (!netControl.isOnline())
                    MSettings.activeActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MSettings.activeActivity, MSettings.activeActivity.getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                        }
                    });
            }
        }
    }
    private JsonObject getResAsJson(String response) {
        return new JsonParser().parse(response).getAsJsonObject();
    }
    public void getSimilarVideos(final String vId, final boolean isPlaylist, final boolean isChannel, final boolean isYoutubeUserVideo, final String[] userYoutubeVideosId) {
        MSettings.isUserVideo = false;
        MSettings.similarVideosIsLoaded = false;
        if (MSettings.mListForFloat != null) {
            MSettings.mListForFloat.setAdapter(null);
        }
        myPg = (ProgressBar) MSettings.body.findViewById(R.id.myPBForPlayedVideosList);
        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myPg.setVisibility(View.VISIBLE);
                    }
                });
                MSettings.isPlayedVideo = true;
                similarVideosList = new ArrayList<>();
                try {
                        /*ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        Call<Result> call = apiService.getResult(vId);
                        call.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                results = response.body().getVideo();
                                for (com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Video vd:results) {
                                    VideoItem itm=new VideoItem();
                                    itm.setId(vd.getId());
                                    itm.setTitle(vd.getTitle());
                                    itm.setChanelTitle(vd.getUsername(),false);
                                    itm.setThumbnailURL(vd.getThumbnailSrc());
                                    similarVideosList.add(itm);
                                }
                                MSettings.similarVideosIsLoaded = true;
                                AdapterForSimilarVideos mAdapter = new AdapterForSimilarVideos(MSettings.activeActivity,results);
                                mListForFloat.setAdapter(null);
                                mListForFloat.setAdapter(mAdapter);
                                myPg.setVisibility(View.INVISIBLE);//new
                            }

                            @Override
                            public void onFailure(Call<Result> call, Throwable t) {
                                Log.e("Error", t.toString());
                            }
                        });*/

                    /*OkHttpClient okHttp = new OkHttpClient();
                    Request request = new Request.Builder().url(MSettings.youtubeWatchURL+"8DyVhDvurgk").build();
                    Document dc = Jsoup.parse(okHttp.newCall(request).execute().body().string());*/
                    /*Document dc = Jsoup.connect(MSettings.youtubeWatchURL+vId).ignoreContentType(true).get();*/
                    if (MSettings.currentVItem != null) {
                        similarVideosList.add(MSettings.currentVItem);
                    }
                    String info;
                    JsonObject obj = null;
                    Connection.Response response= Jsoup.connect(MSettings.youtubeWatchURL+vId)
                            .ignoreContentType(true)
                            .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                            .referrer("http://www.google.com")
                            .timeout(20000)
                            .followRedirects(true)
                            .execute();
                    Document dc = response.parse();
                    Elements element = dc.getElementsByTag("script");
                    for (int i = 0; i < element.size(); i++){
                        Element e = element.get(i);
                        if (e.toString().contains("ytInitialData")) {
                            info = e.toString();
                            info = info.substring(info.indexOf("ytInitialData")+16,info.indexOf("};")+1).trim();
                            obj = getResAsJson(info);
                            break;
                        }
                    }
                    JsonObject mjsonobj1 = (JsonObject) obj.get("contents");
                    JsonObject mjsonobj2 = (JsonObject) mjsonobj1.get("twoColumnWatchNextResults");
                    JsonObject mjsonobj3 = (JsonObject) mjsonobj2.get("secondaryResults");
                    JsonObject mjsonobj4 = (JsonObject) mjsonobj3.get("secondaryResults");
                    JsonArray jsonArray = (JsonArray) mjsonobj4.get("results");
                    for (int i = 0; i<jsonArray.size(); i++){
                        JsonObject jsonobj_1 = (JsonObject) jsonArray.get(i);
                        if(jsonobj_1.get("compactVideoRenderer")!=null) {
                            VideoItem item = new VideoItem();
                            JsonObject jsonobj_2 = (JsonObject) jsonobj_1.get("compactVideoRenderer");
                            item.setId(jsonobj_2.get("videoId").getAsString());
                            JsonObject jsonobj_3 = (JsonObject) jsonobj_2.get("title");
                            item.setTitle(jsonobj_3.get("simpleText").getAsString());
                            JsonObject jsonobj_4 = (JsonObject) jsonobj_2.get("thumbnail");
                            JsonArray thumbarr = (JsonArray) jsonobj_4.get("thumbnails");
                            JsonObject jsonobj5 = (JsonObject) thumbarr.get(1);
                            item.setThumbnailURL(jsonobj5.get("url").getAsString());
                            JsonObject jsonobj6 = (JsonObject) jsonobj_2.get("lengthText");
                            item.setDurationForNewApi(jsonobj6.get("simpleText").getAsString());
                            JsonObject jsonobj7 = (JsonObject) jsonobj_2.get("viewCountText");
                            item.setViewsForNewApi(jsonobj7.get("simpleText").getAsString());
                            JsonObject jsonobj8 = (JsonObject) jsonobj_2.get("longBylineText");
                            JsonArray  channelNameArr = (JsonArray) jsonobj8.get("runs");
                            JsonObject jsonobj9 = (JsonObject) channelNameArr.get(0);
                            item.setChanelTitle(jsonobj9.get("text").getAsString(),false);
                            similarVideosList.add(item);
                        }
                    }

                }  catch (Exception e) {
                    e.printStackTrace();
                    netControl=new NetControl(MainActivity.this);
                    if (!netControl.isOnline())
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MSettings.activeActivity, MSettings.activeActivity.getResources().getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                            }
                        });
                }

                /*MSettings.mHandler.post(new Runnable() {
                    public void run() {
                        MSettings.similarVideosIsLoaded = true;
                        MSettings.mAdapter = new AdapterSearchVideo(MSettings.activeActivity, similarVideosList, "");
                        mListForFloat.setAdapter(null);
                        mListForFloat.setAdapter(MSettings.mAdapter);
                        myPg.setVisibility(View.INVISIBLE);//Changed
                    }
                });*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MSettings.similarVideosIsLoaded = true;
                        MSettings.mAdapter = new AdapterSearchVideo(MSettings.activeActivity, similarVideosList, "");
                        mListForFloat.setAdapter(null);
                        mListForFloat.setAdapter(MSettings.mAdapter);
                        myPg.setVisibility(View.INVISIBLE);//Changed
                    }
                });

            }
        }.start();
    }
    public void getSimilarVideos(List<VideoItem> vidList,boolean isUserVid){
        MSettings.isUserVideo = isUserVid;
        MSettings.similarVideosIsLoaded = false;
        if (MSettings.mListForFloat != null) {
            MSettings.mListForFloat.setAdapter(null);
        }
        myPg = (ProgressBar) MSettings.body.findViewById(R.id.myPBForPlayedVideosList);
        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myPg.setVisibility(View.VISIBLE);
                    }
                });
                MSettings.isPlayedVideo = true;
                similarVideosList = new ArrayList<>();
                similarVideosList=vidList;
                MSettings.mHandler.post(new Runnable() {
                    public void run() {
                        MSettings.mAdapter = new AdapterSearchVideo(MSettings.activeActivity, similarVideosList, "");
                        MSettings.mListForFloat.setAdapter(null);
                        MSettings.mListForFloat.setAdapter(MSettings.mAdapter);
                        myPg.setVisibility(View.INVISIBLE);
                    }
                });
                MSettings.similarVideosIsLoaded = true;
            }
        }.start();
    }

    public List<VideoItem> getVideoItems(String[] matrix) {
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                String packageName = MSettings.activeActivity.getPackageName();
                String SHA1 = MSettings.getSHA1(packageName, MSettings.activeActivity);

                request.getHeaders().set("X-Android-Package", packageName);
                request.getHeaders().set("X-Android-Cert", SHA1);
            }
        }).setApplicationName(MSettings.activeActivity.getString(R.string.app_name)).build();
        try {
            queryTwo = youtube.videos().list("snippet");
            queryTwo.setKey(YoutubeConnector.KEY);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            for (int i = 0; i < matrix.length; i++) {
                if (matrix[i] != null) {
                    queryTwo.setId(matrix[i]);
                    VideoListResponse response = queryTwo.execute();
                    List<Video> results = response.getItems();
                    try {
                        VideoItem item = new VideoItem();
                        if (results.get(0).getId() != null) {
                            item.setTitle(results.get(0).getSnippet().getTitle());
                            item.setThumbnailURL(results.get(0).getSnippet().getThumbnails().getMedium().getUrl());
                            item.setId(results.get(0).getId());
                            item.setChanelTitle(results.get(0).getSnippet().getChannelTitle(), false);
                        }
                        items.add(item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return items;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addClickListener() {
        MSettings.mListForFloat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                if (similarVideosList.get(pos).getId().length() == 11) {
                    MSettings.currentVItem = similarVideosList.get(pos);
                    MSettings.IsRetry = false;
                    MSettings.videoFinishStopVideoClicked = true;
                    MSettings.LoadVideo();
                    MSettings.activeActivity = MainActivity.this;
                    MSettings.CounterForSimilarVideos = pos + 1;
                } else
                    Toast.makeText(MSettings.activeActivity, "Getting an Error", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void LoadFullScreenAds() {
        //MobileAds.initialize(MainActivity.this, "ca-app-pub-2609460348345466~9478052192");
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("5C6AACA525A3BCD06EAF02A4E2E59627")
                //.addTestDevice("C351F406A4D495796C53E9B2FD34AC62")
                /*.addTestDevice("D840C07DDBAA5E0897B010411FABE6AC")
                .addTestDevice("778ADE18482DD7E44193371217202427")
                .addTestDevice("6AFA29CB9314195950E590C9BEACC344")
                .addTestDevice("0CEA9CA5F2DAED70F0678D8F2D8669A3")*/.build();
        final InterstitialAd interstitial = new InterstitialAd(MainActivity.this);
        interstitial.setAdUnitId(getString(R.string.admob_interstitial_id_close_service));
        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                if (interstitial.isLoaded())
                    interstitial.show();
            }

            public void onAdClosed() {

            }
        });
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*MobileAds.initialize(MSettings.activeActivity, "ca-app-pub-5808367634056272~8476127349");
        AdRequest adRequest = new AdRequest.Builder()
                    *//*.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("6EE0EC7A08848B41A3A8B3C52624F39A")
                    .addTestDevice("D840C07DDBAA5E0897B010411FABE6AC")
                    .addTestDevice("778ADE18482DD7E44193371217202427")
                    .addTestDevice("6AFA29CB9314195950E590C9BEACC344")
                    .addTestDevice("0CEA9CA5F2DAED70F0678D8F2D8669A3")*//*.build();
        final InterstitialAd interstitial = new InterstitialAd(MSettings.activeActivity);
        interstitial.setAdUnitId(MSettings.activeActivity.getString(R.string.admob_interstitial_id_close_service));
        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                if (interstitial.isLoaded())
                    interstitial.show();
            }

            public void onAdClosed() {

            }
        });*/

//        MSettings.floaty.stopService();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        if (this.doubleBackToExitPressedOnce) {
            finish();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onUserLeaveHint() {
        if (MSettings.CheckService)
            MSettings.MinimizePlayer();
        super.onUserLeaveHint();
    }

    public static boolean active = false;
    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    private class appVersionController extends AsyncTask<Void, Void, Void> {
        private String version[] = new String[2];
        private String mUrl="";

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                versionController();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void versionController() {
            URL url;
            try {
                url = new URL("http://sscoderr.com/versionController/FloatingController/version-floating.txt");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                int i = 0;
                //ilk deger version ikincsi kilit ekrani mevzusu
                while((line = br.readLine())!=null) {
                    version[i] = line;
                    i++;
                }
                appVers = Integer.parseInt(version[1]);
                PackageManager manager = MainActivity.this.getPackageManager();
                PackageInfo info = manager.getPackageInfo(MainActivity.this.getPackageName(), PackageManager.GET_ACTIVITIES);
                br.close();
                if (info.versionCode<Integer.parseInt(version[0])) {
                    Intent intent = new Intent(MainActivity.this, SlideOnboardingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    intent.putExtra("uri",getLink());
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public String getLink() {
            URL url;
            try {
                url = new URL("http://sscoderr.com/versionController/FloatingController/url.txt");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                mUrl = br.readLine();
                br.close();
                return mUrl;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mUrl;
        }
        @Override
        protected void onPreExecute() {

        }
    }
}