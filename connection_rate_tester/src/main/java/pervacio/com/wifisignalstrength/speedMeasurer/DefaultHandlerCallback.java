package pervacio.com.wifisignalstrength.speedMeasurer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import pervacio.com.wifisignalstrength.R;
import pervacio.com.wifisignalstrength.speedMeasurer.speedListeners.AbstractSpeedListener;
import pervacio.com.wifisignalstrength.utils.CommonUtils;

import static pervacio.com.wifisignalstrength.utils.Constants.ERROR;
import static pervacio.com.wifisignalstrength.utils.Constants.FINISH;
import static pervacio.com.wifisignalstrength.utils.Constants.PROGRESS;
import static pervacio.com.wifisignalstrength.utils.Constants.START;

public abstract class DefaultHandlerCallback implements Handler.Callback {

   protected static final int WIFI = CommonUtils.WIFI;
   protected static final int MOBILE = CommonUtils.MOBILE;

    private ProgressBar mProgressBar;
    private TextView mRateText;
    private ImageView mRestartButton;

    private String mTaskName;
    private Context mContext;
    @TargetConnectionType
    private final int mConnectionType;

    public DefaultHandlerCallback(ViewSet viewSet, String taskName, @TargetConnectionType int connectionType) {
        mContext = viewSet.mProgressBar.getContext();
        mProgressBar = viewSet.mProgressBar;
        mRateText = viewSet.mRateText;
        mRestartButton = viewSet.mRestartButton;
        mTaskName = taskName;
        mConnectionType = connectionType;
    }

    @TargetConnectionType
    public int getConnectionType() {
        return mConnectionType;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.arg1) {
            case START:
                mProgressBar.setVisibility(View.VISIBLE);
                mRestartButton.setVisibility(View.GONE);
                mRateText.setText(mContext.getString(R.string.measurement_started, mTaskName));
                break;
            case PROGRESS:
                AbstractSpeedListener.PublishProgress publishProgress = (AbstractSpeedListener.PublishProgress) message.obj;
                mRateText.setText(mContext.getString(R.string.rate_message, mTaskName, publishProgress.rate));
                break;
            case FINISH:
                final Float rate = (Float) message.obj;
                mRateText.setText(mContext.getString(R.string.rate_message, mTaskName, rate));
                mProgressBar.setVisibility(View.INVISIBLE);
                mProgressBar.setIndeterminate(false);
                mRestartButton.setVisibility(View.VISIBLE);
                break;
            case ERROR:
                int messageResId;
                Integer resId = (Integer) message.obj;
                if (resId != null){
                    messageResId = resId;
                }else {
                    messageResId = R.string.default_error_message;
                }
                mRateText.setText(messageResId);
                mProgressBar.setVisibility(View.INVISIBLE);
                mProgressBar.setIndeterminate(false);
                mRestartButton.setVisibility(View.VISIBLE);
                break;
        }
        return true;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({WIFI, MOBILE})
    protected @interface TargetConnectionType {

    }

    public static class ViewSet {

        private ProgressBar mProgressBar;
        private TextView mRateText;
        private ImageView mRestartButton;

        public ViewSet(@NonNull ProgressBar progressBar, @NonNull TextView rateText, @NonNull ImageView restartButton) {
            this.mProgressBar = progressBar;
            this.mRateText = rateText;
            this.mRestartButton = restartButton;
        }
    }

}