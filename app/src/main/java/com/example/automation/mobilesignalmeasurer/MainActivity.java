package com.example.automation.mobilesignalmeasurer;

import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.automation.mobilesignalmeasurer.speedtest.SimpleMobileInternetHandlerCallback;

import java.util.ArrayList;
import java.util.Collections;

import pervacio.com.signalmeasurer.PhoneSignalStateListener;
import pervacio.com.signalmeasurer.SignalCriteria;
import pervacio.com.wifisignalstrength.speedMeasurer.ConnectionRateTester;
import pervacio.com.wifisignalstrength.speedMeasurer.DefaultHandlerCallback;
import pervacio.com.wifisignalstrength.speedMeasurer.Router;
import pervacio.com.wifisignalstrength.speedMeasurer.TaskAndHandlerWrapper;
import pervacio.com.wifisignalstrength.speedMeasurer.actions.DefaultWorkerTask;
import pervacio.com.wifisignalstrength.speedMeasurer.actions.WorkerTask;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        PhoneSignalStateListener.SignalState,
        Router.LastListenerFinished {

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

    private WorkerTask mDownLoadTask;
    private WorkerTask mUploadTask;
    private Handler.Callback mDownloadCallback;
    private Handler.Callback mUploadCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setOnclickListeners();

        mPhoneStateListener = new PhoneSignalStateListener(this, this);
        ArrayList<TaskAndHandlerWrapper> listenerAndHandlers = new ArrayList<>(2);
        mDownLoadTask = new DefaultWorkerTask(DefaultWorkerTask.DOWNLOAD);
        mDownloadCallback = new SimpleMobileInternetHandlerCallback(new DefaultHandlerCallback.ViewSet(mWifiDownloadSpeedProgress, downloadRate, restartDownload), "Download");
        listenerAndHandlers.add(new TaskAndHandlerWrapper(mDownLoadTask, mDownloadCallback));
        mUploadTask = new DefaultWorkerTask(DefaultWorkerTask.UPLOAD);
        mUploadCallback = new SimpleMobileInternetHandlerCallback(new DefaultHandlerCallback.ViewSet(mWifiUploadSpeedProgress, uploadRate, restartUpload), "Update");
        listenerAndHandlers.add(new TaskAndHandlerWrapper(mUploadTask, mUploadCallback));
        ConnectionRateTester rateTester = new ConnectionRateTester(listenerAndHandlers, this);
        rateTester.startRateMeasurements();
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
                enableRestartButtons(false);
                new ConnectionRateTester(Collections.singletonList(new TaskAndHandlerWrapper(mDownLoadTask, mDownloadCallback)), this).startRateMeasurements();
                break;
            case R.id.restart_upload:
                enableRestartButtons(false);
                new ConnectionRateTester(Collections.singletonList(new TaskAndHandlerWrapper(mUploadTask, mUploadCallback)), this).startRateMeasurements();
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

    @Override
    public void onLastTaskCompleted() {
        enableRestartButtons(true);
        restartDownload.setImageResource(R.drawable.ic_replay_accent_48dp);
        restartUpload.setImageResource(R.drawable.ic_replay_accent_48dp);
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

}
