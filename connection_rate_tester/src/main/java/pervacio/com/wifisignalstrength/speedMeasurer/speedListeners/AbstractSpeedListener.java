package pervacio.com.wifisignalstrength.speedMeasurer.speedListeners;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import pervacio.com.wifisignalstrength.speedMeasurer.ISpeedListenerFinishCallback;
import pervacio.com.wifisignalstrength.speedMeasurer.SpeedListenerHandler;
import pervacio.com.wifisignalstrength.speedMeasurer.StopWaiter;
import pervacio.com.wifisignalstrength.utils.Constants;

import static pervacio.com.wifisignalstrength.utils.Constants.FINISH;


/**
 * The class uses to handle {@link fr.bmartel.speedtest.inter.ISpeedTestListener} callbacks
 * feeded to {@link fr.bmartel.speedtest.SpeedTestSocket}.
 */
public abstract class AbstractSpeedListener implements
        ISpeedTestListener,
        StopWaiter.OnTimerStop {

    private static final String TAG = AbstractSpeedListener.class.getSimpleName();

    private List<Float> mList;
    private SpeedListenerHandler mHandler;
    private long realStartTime;
    private ISpeedListenerFinishCallback mOnFinish;
    private StopWaiter mStopWaiter;

    protected AbstractSpeedListener(SpeedListenerHandler handler, ISpeedListenerFinishCallback onFinish) {
        mHandler = handler;
        mOnFinish = onFinish;
        mList = new ArrayList<>();
        mStopWaiter = new StopWaiter(this);
    }

    @Override
    public void onDownloadFinished(SpeedTestReport report) {
    }

    @Override
    public void onDownloadProgress(float percent, SpeedTestReport report) {
    }

    @Override
    public void onDownloadError(SpeedTestError speedTestError, String errorMessage) {
    }

    @Override
    public void onUploadFinished(SpeedTestReport report) {

    }

    @Override
    public void onUploadError(SpeedTestError speedTestError, String errorMessage) {
    }

    @Override
    public void onUploadProgress(float percent, SpeedTestReport report) {
    }

    @Override
    public void onInterruption() {
    }

    @Override
    public void onTimerStop() {
        Log.d(TAG, "onTimerStop onStop() called");
        onStop();
    }


    /**
     * Method publish onStop state to the handler.
     *
     * @param report the {@link SpeedTestReport} report returns form {@link fr.bmartel.speedtest.inter.ISpeedTestListener}
     */
    protected void onUpdate(SpeedTestReport report) {
        if (mHandler == null) {
            return;
        }
        if (realStartTime == 0) {
            realStartTime = report.getReportTime();
            mHandler.publish(Constants.START);
            mStopWaiter.start();
        } else if (report.getProgressPercent() == 100) {
            onStop();
        } else {
            float rate = report.getTransferRateBit().floatValue() / 1024 / 1024;
            mStopWaiter.updateTimer();
            if (rate != 0) {
                mList.add(rate);
                int progress = (int) ((report.getReportTime() - realStartTime) / 100) + 1;
                mHandler.publish(Constants.PROGRESS, new PublishProgress(progress, rate));
            }
        }
    }

    /**
     * Method publish onStop state to the handler.
     */
    protected void onStop() {
        if (mHandler != null) {
            if (mList.size() != 0) {
                mHandler.publish(FINISH, mList.get(mList.size() - 1));
            } else {
                mHandler.publish(Constants.ERROR);
            }
        }
        if (mOnFinish != null) {
            mOnFinish.onSpeedListenerFinish();
        }
    }

    /**
     * The type uses to wrap progress state to transfer to the handler
     */
    public static class PublishProgress {

        public int progress;
        public float rate;

        public PublishProgress(int progress, float rate) {
            this.progress = progress;
            this.rate = rate;
        }
    }

}
