package com.noqapp.android.merchant.views.activities;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.common.api.Status;
import com.noqapp.android.common.beans.ErrorEncounteredJson;
import com.noqapp.android.common.beans.JsonAdvertisementList;
import com.noqapp.android.common.beans.JsonProfessionalProfileTVList;
import com.noqapp.android.common.fcm.data.JsonAlertData;
import com.noqapp.android.common.fcm.data.JsonClientData;
import com.noqapp.android.common.fcm.data.JsonClientOrderData;
import com.noqapp.android.common.fcm.data.JsonTopicOrderData;
import com.noqapp.android.common.fcm.data.JsonTopicQueueData;
import com.noqapp.android.common.model.types.FirebaseMessageTypeEnum;
import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.presenter.AdvertisementPresenter;
import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.model.AdvertisementApiCalls;
import com.noqapp.android.merchant.model.ClientInQueueApiCalls;
import com.noqapp.android.merchant.presenter.ClientInQueuePresenter;
import com.noqapp.android.merchant.presenter.ProfessionalProfilesPresenter;
import com.noqapp.android.merchant.presenter.beans.JsonQueueTV;
import com.noqapp.android.merchant.presenter.beans.JsonQueueTVList;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;
import com.noqapp.android.merchant.presenter.beans.body.QueueDetail;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.utils.Constants;
import com.noqapp.android.merchant.utils.ErrorResponseHandler;
import com.noqapp.android.merchant.utils.UserUtils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity
        extends BaseActivity
        implements ClientInQueuePresenter, AdvertisementPresenter, ProfessionalProfilesPresenter {

    protected static final String INTENT_EXTRA_CAST_DEVICE = "CastDevice";
    private MediaRouter mediaRouter;
    private CastDevice castDevice;
    private int currentPage = 0;
    private Timer timer;
    private final long DELAY_MS = 1000;//delay in milliseconds before task is to be executed
    private final long PERIOD_MS = 5 * 1000;
    private HashMap<String, JsonTopic> topicHashMap = new HashMap<>();
    protected BroadcastReceiver broadcastReceiver;
    private JsonAdvertisementList jsonAdvertisementList;
    private JsonProfessionalProfileTVList jsonProfessionalProfileTVList;
    private boolean isNotification = false;
    private DetailFragment detailFragment;
    private List<TopicAndQueueTV> topicAndQueueTVList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);
        setProgressMessage("Fetching data...");
        setupMediaRouter();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    Bundle extras = intent.getExtras();
                    ArrayList<JsonTopic> topics = LaunchActivity.merchantListFragment.getTopics();
                    if (extras != null) {
                        String codeQR = intent.getStringExtra(Constants.QRCODE);
                        String status = intent.getStringExtra(Constants.STATUS);
                        String current_serving = intent.getStringExtra(Constants.CURRENT_SERVING);
                        String lastNumber = intent.getStringExtra(Constants.LASTNO);
                        String payload = intent.getStringExtra(Constants.FIREBASE_TYPE);
                        Object object = intent.getSerializableExtra("object");

                        if (object instanceof JsonTopicQueueData) {
                            Log.e("onReceiveJsonTopicQdata", ((JsonTopicQueueData) object).toString());
                            try {
                                for (int i = 0; i < topics.size(); i++) {
                                    JsonTopic jt = topics.get(i);
                                    if (jt.getCodeQR().equalsIgnoreCase(codeQR)) {
                                        if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.M.getName())) {
                                            /* Update only from merchant msg. */
                                            jt.setQueueStatus(QueueStatusEnum.valueOf(status));
                                        }
                                        if (QueueStatusEnum.valueOf(status).equals(QueueStatusEnum.S) || QueueStatusEnum.valueOf(status).equals(QueueStatusEnum.N)) {
                                            jt.setToken(Integer.parseInt(lastNumber));
                                            jt.setServingNumber(Integer.parseInt(current_serving));
                                        } else {
                                            if (Integer.parseInt(lastNumber) >= jt.getToken()) {
                                                jt.setToken(Integer.parseInt(lastNumber));
                                            }
                                        }
                                        topics.set(i, jt);
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (object instanceof JsonClientData) {
                            Log.e("onReceiveJsonClientData", ((JsonClientData) object).toString());
                        } else if (object instanceof JsonAlertData) {
                            Log.e("onReceiveJsonAlertData", ((JsonAlertData) object).toString());
                        } else if (object instanceof JsonTopicOrderData) {
                            Log.e("onReceiveJsonTopicData", ((JsonTopicOrderData) object).toString());
                            try {
                                for (int i = 0; i < topics.size(); i++) {
                                    JsonTopic jt = topics.get(i);
                                    if (jt.getCodeQR().equalsIgnoreCase(codeQR)) {
                                        if (StringUtils.isNotBlank(payload) && payload.equalsIgnoreCase(FirebaseMessageTypeEnum.M.getName())) {
                                            /* Update only from merchant msg. */
                                            jt.setQueueStatus(QueueStatusEnum.valueOf(status));
                                        }
                                        if (QueueStatusEnum.valueOf(status).equals(QueueStatusEnum.S) || QueueStatusEnum.valueOf(status).equals(QueueStatusEnum.N)) {
                                            jt.setToken(Integer.parseInt(lastNumber));
                                            jt.setServingNumber(Integer.parseInt(current_serving));
                                        } else {
                                            if (Integer.parseInt(lastNumber) >= jt.getToken()) {
                                                jt.setToken(Integer.parseInt(lastNumber));
                                            }
                                        }
                                        topics.set(i, jt);
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (object instanceof JsonClientOrderData) {
                            Log.e("JsonClientOrderData", ((JsonClientOrderData) object).toString());
                        }
                    }
                    updateTv(topics);
                }

            }
        };
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            showProgress();
            QueueDetail queueDetail = getQueueDetails(LaunchActivity.merchantListFragment.getTopics());
            ClientInQueueApiCalls clientInQueueApiCalls = new ClientInQueueApiCalls(this);
            clientInQueueApiCalls.toBeServedClients(
                    UserUtils.getDeviceId(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(), queueDetail);

            AdvertisementApiCalls advertisementApiCalls = new AdvertisementApiCalls();
            advertisementApiCalls.setAdvertisementPresenter(this);
            advertisementApiCalls.setProfessionalProfilesPresenter(this);
            advertisementApiCalls.getAllAdvertisements(UserUtils.getDeviceId(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth());
            advertisementApiCalls.professionalProfiles(UserUtils.getDeviceId(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isRemoteDisplaying()) {
            if (castDevice != null) {
                startCastService(castDevice);
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));
    }

    @Override
    public void onDestroy() {
        if (mediaRouter != null) {
            mediaRouter.removeCallback(mMediaRouterCallback);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.action_cast);
        return true;
    }

    private void setupMediaRouter() {
        mediaRouter = MediaRouter.getInstance(getApplicationContext());
        MediaRouteSelector mediaRouteSelector = new MediaRouteSelector.Builder().addControlCategory(CastMediaControlIntent.categoryForCast(getString(R.string.app_cast_id))).build();
        if (isRemoteDisplaying()) {
            this.castDevice = CastDevice.getFromBundle(mediaRouter.getSelectedRoute().getExtras());
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                castDevice = extras.getParcelable(INTENT_EXTRA_CAST_DEVICE);
            }
        }
        mediaRouter.addCallback(mediaRouteSelector, mMediaRouterCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    private boolean isRemoteDisplaying() {
        return CastRemoteDisplayLocalService.getInstance() != null;
    }

    private final MediaRouter.Callback mMediaRouterCallback = new MediaRouter.Callback() {
        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
            castDevice = CastDevice.getFromBundle(info.getExtras());
            Toast.makeText(getApplicationContext(), getString(R.string.cast_connected_to) + info.getName(), Toast.LENGTH_LONG).show();
            startCastService(castDevice);
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
            if (isRemoteDisplaying()) {
                CastRemoteDisplayLocalService.stopService();
            }
            castDevice = null;
        }
    };

    private void startCastService(final CastDevice castDevice) {
        new Handler().post(() -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent notificationPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

            CastRemoteDisplayLocalService.NotificationSettings settings =
                    new CastRemoteDisplayLocalService.NotificationSettings.Builder()
                            .setNotificationPendingIntent(notificationPendingIntent).build();

            CastRemoteDisplayLocalService.startService(MainActivity.this, PresentationService.class,
                    getString(R.string.app_cast_id), castDevice, settings,
                    new CastRemoteDisplayLocalService.Callbacks() {
                        @Override
                        public void onServiceCreated(CastRemoteDisplayLocalService service) {
                            ((PresentationService) CastRemoteDisplayLocalService.getInstance()).professionalProfilesResponse(jsonProfessionalProfileTVList);
                            ((PresentationService) CastRemoteDisplayLocalService.getInstance()).setAdvertisementList(jsonAdvertisementList, topicAndQueueTVList.size());
                            ((PresentationService) service).setTopicAndQueueTV(topicAndQueueTVList);
                        }

                        @Override
                        public void onRemoteDisplaySessionStarted(CastRemoteDisplayLocalService service) {
                        }

                        @Override
                        public void onRemoteDisplaySessionError(Status errorReason) {
                            initError();

                            MainActivity.this.castDevice = null;
                            MainActivity.this.finish();
                        }

                        @Override
                        public void onRemoteDisplaySessionEnded(CastRemoteDisplayLocalService castRemoteDisplayLocalService) {
                        }
                    });

        });
    }

    private void initError() {
        Toast toast = Toast.makeText(getApplicationContext(), R.string.toast_connection_error, Toast.LENGTH_SHORT);
        if (mediaRouter != null) {
            mediaRouter.selectRoute(mediaRouter.getDefaultRoute());
        }
        toast.show();
    }

    @NonNull
    private QueueDetail getQueueDetails(ArrayList<JsonTopic> jsonTopics) {
        QueueDetail queueDetail = new QueueDetail();
        List<String> tvObjects = new ArrayList<>();
        topicHashMap.clear();
        for (int i = 0; i < jsonTopics.size(); i++) {
            JsonTopic jsonTopic = jsonTopics.get(i);
            String start = Formatter.convertMilitaryTo24HourFormat(jsonTopic.getHour().getStartHour());
            String end = Formatter.convertMilitaryTo24HourFormat(jsonTopic.getHour().getEndHour());
            if (isTimeBetweenTwoTime(start, end)) {
                tvObjects.add(jsonTopics.get(i).getCodeQR());
                topicHashMap.put(jsonTopics.get(i).getCodeQR(), jsonTopics.get(i));
            }
        }
        queueDetail.setCodeQRs(tvObjects);
        return queueDetail;
    }

    @Override
    public void clientInResponse(JsonQueueTVList jsonQueueTVList) {
        if (null != jsonQueueTVList && null != jsonQueueTVList.getQueues()) {
            Log.v("TV Data", jsonQueueTVList.getQueues().toString());
            topicAndQueueTVList.clear();
            for (int i = 0; i < jsonQueueTVList.getQueues().size(); i++) {
                JsonQueueTV jsonQueueTV = jsonQueueTVList.getQueues().get(i);
                topicAndQueueTVList.add(new TopicAndQueueTV().setJsonTopic(topicHashMap.get(jsonQueueTV.getCodeQR())).setJsonQueueTV(jsonQueueTV));
            }
            if (topicAndQueueTVList.size() == 0) {
                topicAndQueueTVList.add(new TopicAndQueueTV().setJsonTopic(null).setJsonQueueTV(null));
            }
            if (null == detailFragment) {
                detailFragment = new DetailFragment();
            }
            if (!isFinishing()) {
                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_layout, detailFragment, "NewFragmentTag");
                ft.commitAllowingStateLoss();
            }

            if (!isNotification) {
                if (CastRemoteDisplayLocalService.getInstance() != null) {
                    ((PresentationService) CastRemoteDisplayLocalService.getInstance()).professionalProfilesResponse(jsonProfessionalProfileTVList);
                    ((PresentationService) CastRemoteDisplayLocalService.getInstance()).setAdvertisementList(jsonAdvertisementList, topicAndQueueTVList.size());
                    ((PresentationService) CastRemoteDisplayLocalService.getInstance()).setTopicAndQueueTV(topicAndQueueTVList, true);
                }
                final Handler handler = new Handler();
                final Runnable Update = () -> {
                    try {
                        if (currentPage >= topicAndQueueTVList.size()) {
                            currentPage = 0;
                            // size check for greater also
                        }
                        detailFragment = DetailFragment.newInstance(topicAndQueueTVList.get(currentPage));
                        final FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                        ft1.replace(R.id.frame_layout, detailFragment, "NewFragmentTag");
                        ft1.commitAllowingStateLoss();
                        currentPage++;
                    }catch (IndexOutOfBoundsException e){
                        e.printStackTrace();
                        currentPage = 0;
                    }
                };

                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                timer = new Timer(); // This will create a new Thread
                timer.schedule(new TimerTask() { // task to be scheduled

                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, DELAY_MS, PERIOD_MS);
            }

        }
        dismissProgress();
        isNotification = false;
    }

    @Override
    public void authenticationFailure() {
        dismissProgress();
        AppUtils.authenticationProcessing();
        isNotification = false;
        //finish();
    }

    @Override
    public void responseErrorPresenter(int errorCode) {
        dismissProgress();
        new ErrorResponseHandler().processFailureResponseCode(this, errorCode);
        isNotification = false;
    }

    @Override
    public void responseErrorPresenter(ErrorEncounteredJson eej) {
        dismissProgress();
        if (null != eej) {
            new ErrorResponseHandler().processError(this, eej);
        }
        isNotification = false;
    }

    public void updateTv(ArrayList<JsonTopic> topics) {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            isNotification = true;
            showProgress();
            QueueDetail queueDetail = getQueueDetails(topics);
            ClientInQueueApiCalls clientInQueueApiCalls = new ClientInQueueApiCalls(this);
            clientInQueueApiCalls.toBeServedClients(
                    UserUtils.getDeviceId(),
                    LaunchActivity.getLaunchActivity().getEmail(),
                    LaunchActivity.getLaunchActivity().getAuth(), queueDetail);
        }
    }

    @Override
    public void advertisementResponse(JsonAdvertisementList jsonAdvertisementList) {
        Log.v("data", jsonAdvertisementList.toString());
        this.jsonAdvertisementList = jsonAdvertisementList;
    }

    @Override
    public void professionalProfilesResponse(JsonProfessionalProfileTVList jsonProfessionalProfileTVList) {
        Log.v("profile data", jsonProfessionalProfileTVList.toString());
        this.jsonProfessionalProfileTVList = jsonProfessionalProfileTVList;
    }

    public static boolean isTimeBetweenTwoTime(String initialTime, String finalTime) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = new Date();
            String currentTime = formatter.format(date);
            //Start Time
            //all times are from java.util.Date
            Date inTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(initialTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(inTime);

            //Current Time
            Date checkTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(currentTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(checkTime);

            //End Time
            Date finTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(finalTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(finTime);

            if (finalTime.compareTo(initialTime) < 0) {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            java.util.Date actualTime = calendar3.getTime();
            if ((actualTime.after(calendar1.getTime()) ||
                    actualTime.compareTo(calendar1.getTime()) == 0) &&
                    actualTime.before(calendar2.getTime())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }
}
