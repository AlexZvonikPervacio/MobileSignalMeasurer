package com.example.automation.mobilesignalmeasurer;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import pervacio.com.customconnectionmeasurer.TestRouter;
import pervacio.com.customconnectionmeasurer.callbacks.TaskCallbacks;
import pervacio.com.customconnectionmeasurer.utils.Constants;
import pervacio.com.customconnectionmeasurer.utils.MeasuringUnits;
import pervacio.com.signalmeasurer.PhoneSignalStateListener;
import pervacio.com.signalmeasurer.SignalCriteria;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        PhoneSignalStateListener.SignalState,
        TaskCallbacks {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressBar mWifiDownloadSpeedProgress;
    private ProgressBar mWifiUploadSpeedProgress;
    private TextView downloadRate;
    private TextView uploadRate;
    private ImageView restartDownload;
    private ImageView restartUpload;
    private TextView mOnRequestMeasurer;
    private TextView mRealTimeMeasurer;
    private GradientDrawable mRealTimeMeasurerDrawable;
    private PhoneSignalStateListener mPhoneStateListener;

    private TestRouter mTestRouter;
    private final MeasuringUnits unit = MeasuringUnits.KB_S;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setOnclickListeners();

        mPhoneStateListener = new PhoneSignalStateListener(this, this);

        mTestRouter = new TestRouter.Builder(this)
                .setNetworkType(Constants.WIFI)
                .setDuration(5_000)
                .setMeasuringUnit(unit)
                .setDownload(this)
                .setUpload(this)
                .create();
        mTestRouter.startRouting();
        mTestRouter.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTestRouter.startRouting();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTestRouter.finishRouting();
    }

    private void initViews() {
        mWifiDownloadSpeedProgress = (ProgressBar) findViewById(R.id.wifiDownloadSpeedProgress);
        mWifiUploadSpeedProgress = (ProgressBar) findViewById(R.id.wifiUploadSpeedProgress);
        downloadRate = (TextView) findViewById(R.id.download_rate);
        uploadRate = (TextView) findViewById(R.id.upload_rate);
        restartDownload = (ImageView) findViewById(R.id.restart_download);
        restartUpload = (ImageView) findViewById(R.id.restart_upload);

        mOnRequestMeasurer = (TextView) findViewById(R.id.signal_strength_on_request);
        mRealTimeMeasurer = (TextView) findViewById(R.id.signal_strength_real_time);
        mRealTimeMeasurerDrawable = (GradientDrawable) mRealTimeMeasurer.getBackground();
        mRealTimeMeasurerDrawable.setColor(0xFFFFFF);
    }

    private void setOnclickListeners() {
        restartDownload.setOnClickListener(this);
        restartUpload.setOnClickListener(this);
        mOnRequestMeasurer.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.restart_download:
                mTestRouter.addTaskAndStart(pervacio.com.customconnectionmeasurer.utils.Constants.DOWNLOAD, this);
                break;
            case R.id.restart_upload:
                mTestRouter.addTaskAndStart(pervacio.com.customconnectionmeasurer.utils.Constants.UPLOAD, this);
                break;
            case R.id.signal_strength_on_request:
                mOnRequestMeasurer.setText(getString(R.string.on_request_string, mPhoneStateListener.getAsu(), mPhoneStateListener.getDbm()));
                break;
        }
    }

    @Override
    public void onSignalChanged(SignalCriteria criteria) {
        mRealTimeMeasurer.setText(getString(R.string.real_time_string, criteria.getAsu(), criteria.getTitle()));
        mRealTimeMeasurerDrawable.setColor(criteria.getColor());
    }

    @Override
    public void onFailedToMeasure(String message) {
        mRealTimeMeasurer.setText(message);
    }

    /**
     * Disable buttons to prevent parallel measurements
     *
     * @param enable state flag
     */
    private void enableRestartButtons(boolean enable) {
        restartDownload.setEnabled(enable);
        restartUpload.setEnabled(enable);
        restartDownload.setClickable(enable);
        restartUpload.setClickable(enable);
    }

    @Override
    public void onStartRouting() {
        Log.w(TAG, "[onStartRouting]");
        enableRestartButtons(false);
    }

    @Override
    public void onDownloadStart() {
        Log.w(TAG, "[onDownloadStart]");
        mWifiDownloadSpeedProgress.setVisibility(View.VISIBLE);
        restartDownload.setVisibility(View.GONE);
        downloadRate.setText(getString(pervacio.com.wifisignalstrength.R.string.measurement_started, "Download"));
    }

    @Override
    public void onDownloadProgress(float progress) {
        Log.w(TAG, "[onDownloadProgress] : progress = " + progress);
        downloadRate.setText(getString(pervacio.com.wifisignalstrength.R.string.rate_message, "Download", progress, unit.getLabel()));
    }

    @Override
    public void onDownloadFinish(float result) {
        Log.w(TAG, "[onDownloadFinish] : result = " + result + " " + unit.getLabel());
        mWifiDownloadSpeedProgress.setVisibility(View.INVISIBLE);
        mWifiDownloadSpeedProgress.setIndeterminate(false);
        restartDownload.setVisibility(View.VISIBLE);
        downloadRate.setText(getString(pervacio.com.wifisignalstrength.R.string.rate_message, "Download", result, unit.getLabel()));
    }

    @Override
    public void onDownloadError(String message) {
        Log.w(TAG, "[onDownloadError] : message = " + message);
        downloadRate.setText(message);
        mWifiUploadSpeedProgress.setVisibility(View.INVISIBLE);
        mWifiUploadSpeedProgress.setIndeterminate(false);
        restartUpload.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUploadStart() {
        Log.w(TAG, "[onUploadStart]");
        mWifiUploadSpeedProgress.setVisibility(View.VISIBLE);
        restartUpload.setVisibility(View.GONE);
        uploadRate.setText(getString(pervacio.com.wifisignalstrength.R.string.measurement_started, "Upload"));
    }

    @Override
    public void onUploadProgress(float progress) {
        Log.w(TAG, "[onUploadProgress] : progress = " + progress);
        uploadRate.setText(getString(pervacio.com.wifisignalstrength.R.string.rate_message, "Upload", progress, unit.getLabel()));
    }

    @Override
    public void onUploadFinish(float result) {
        Log.w(TAG, "[onUploadFinish] : result = " + result + " " + unit.getLabel());
        uploadRate.setText(getString(pervacio.com.wifisignalstrength.R.string.rate_message, "Upload", result, unit.getLabel()));
        mWifiUploadSpeedProgress.setVisibility(View.INVISIBLE);
        mWifiUploadSpeedProgress.setIndeterminate(false);
        restartUpload.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUploadError(String message) {
        Log.w(TAG, "[onUploadError] : message = " + message);
        uploadRate.setText(message);
        mWifiDownloadSpeedProgress.setVisibility(View.INVISIBLE);
        mWifiDownloadSpeedProgress.setIndeterminate(false);
        restartDownload.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinishRouting() {
        Log.w(TAG, "[onFinishRouting]");
        enableRestartButtons(true);
        restartDownload.setImageResource(R.drawable.ic_replay_accent_48dp);
        restartUpload.setImageResource(R.drawable.ic_replay_accent_48dp);
    }

    @Override
    public void onHorribleError(String message) {
        Log.e(TAG, "[onHorribleError] : message = " + message);
        throw new RuntimeException(message);
    }

}
