package pervacio.com.wifisignalstrength.speedMeasurer;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import pervacio.com.wifisignalstrength.utils.Constants;

/**
 * A handler created from the {@link android.os.Handler.Callback}.
 * transfers to the {@link Router} class in {@link TaskAndHandlerWrapper} wrapper class
 */
public class SpeedListenerHandler extends Handler {

    public SpeedListenerHandler(Callback callback) {
        super(Looper.getMainLooper(), callback);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
    }


    public void publish(@Constants.LoadingStatus int loadingStatus) {
        publish(loadingStatus, null);
    }

    /**
     * Method uses to publish result to
     *
     * @param loadingStatus the loading status one of the {@link Constants.LoadingStatus}
     * @param object        the object contains additional parameters like a progress or a measured rate
     */
    public void publish(@Constants.LoadingStatus int loadingStatus, Object object) {
        Message message = new Message();
        message.arg1 = loadingStatus;
        message.obj = object;
        sendMessage(message);
    }

}