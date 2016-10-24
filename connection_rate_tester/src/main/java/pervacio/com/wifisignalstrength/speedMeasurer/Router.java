package pervacio.com.wifisignalstrength.speedMeasurer;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;

import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.model.UploadStorageType;
import pervacio.com.wifisignalstrength.R;
import pervacio.com.wifisignalstrength.speedMeasurer.actions.WorkerTask;
import pervacio.com.wifisignalstrength.utils.CommonUtils;
import pervacio.com.wifisignalstrength.utils.Constants;

import static pervacio.com.wifisignalstrength.speedMeasurer.DefaultHandlerCallback.MOBILE;
import static pervacio.com.wifisignalstrength.speedMeasurer.DefaultHandlerCallback.WIFI;

/**
 * The type Router used to configure one by one execution listening callbacks.
 */
public class Router implements ISpeedListenerFinishCallback {

    private List<TaskAndHandlerWrapper> mListenerAndHandlers;
    private SpeedTestSocket mSpeedTestSocket;
    private LastListenerFinished mLastListenerFinished;
    private int mSerialNumber;

    /**
     * Instantiates a new Router.
     *
     * @param listenerAndHandlers  the listener and handlers
     * @param lastListenerFinished the last listener finished
     */
    Router(List<TaskAndHandlerWrapper> listenerAndHandlers, LastListenerFinished lastListenerFinished) {
        mListenerAndHandlers = listenerAndHandlers;
        mSpeedTestSocket = new SpeedTestSocket();
        mSpeedTestSocket.setUploadStorageType(UploadStorageType.FILE_STORAGE);
        mLastListenerFinished = lastListenerFinished;
    }

    /**
     * Calls when task finishes
     */
    @Override
    public void onSpeedListenerFinish() {
        Log.w("Router", "onSpeedListenerFinish");
        if (mListenerAndHandlers.size() == mSerialNumber) {
            if (mLastListenerFinished != null) {
                Log.d("Router", "onSpeedListenerFinish() called  mListenerAndHandlers.size() == mSerialNumber " + (mListenerAndHandlers.size() == mSerialNumber));
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mLastListenerFinished.onLastTaskCompleted();
                    }
                });
            }
        } else {
            startTask(mSerialNumber++);
        }
    }

    /**
     * Start from the first task
     */
    void route() {
        startTask(mSerialNumber++);
    }

    /**
     * Create handlers from callbacks and start tasks one by one
     *
     * @param serialNumber number of the task
     */
    private void startTask(int serialNumber) {
        if (mListenerAndHandlers.size() > serialNumber) {
            TaskAndHandlerWrapper listenerAndHandler = mListenerAndHandlers.get(serialNumber);

            WorkerTask mWorkerTask = listenerAndHandler.mWorkerTask;
//            Handler.Callback mCallback = listenerAndHandler.mCallback;
            DefaultHandlerCallback mCallback = (DefaultHandlerCallback) listenerAndHandler.mCallback;
            SpeedListenerHandler handler = new SpeedListenerHandler(mCallback);
            //////////////////////////////////////////
            Integer messageResId = null;
            if (!CommonUtils.hasInternetAccess(mCallback.getContext())) {
                messageResId = R.string.no_internet_connection;
            } else if (WIFI == mCallback.getConnectionType() && CommonUtils.typeConnection(mCallback.getContext()) != WIFI) {
                messageResId = R.string.wifi_not_connected;
            } else if (MOBILE == mCallback.getConnectionType() && CommonUtils.typeConnection(mCallback.getContext()) != MOBILE) {
                messageResId = R.string.mobile_internet_not_connected;
            }
            if (messageResId != null) {
                handler.publish(Constants.ERROR, messageResId);
                onSpeedListenerFinish();
            } else {
                mWorkerTask.execute(mSpeedTestSocket, handler, this);
            }
        }
    }

    /**
     * The interface LastListenerFinishedFinished used to indicate last task completed.
     */
    public interface LastListenerFinished {
        void onLastTaskCompleted();
    }

}
