package pervacio.com.wifisignalstrength.speedMeasurer;

import android.os.HandlerThread;

import java.util.List;

/**
 * The type ConnectionRateTester is a worker thread uses to execute connection test tasks.
 */
public class ConnectionRateTester extends HandlerThread {

    private static final String TAG = ConnectionRateTester.class.getSimpleName();

    private List<TaskAndHandlerWrapper> mListenerAndHandlers;
    private Router.LastListenerFinished mLastListenerFinished;

    public ConnectionRateTester(List<TaskAndHandlerWrapper> listenerAndHandlers, Router.LastListenerFinished lastListenerFinished) {
        super(TAG);
        mListenerAndHandlers = listenerAndHandlers;
        mLastListenerFinished = lastListenerFinished;
    }

    /**
     * Router object creates in @link{(ConnectionRateTester.onLooperPrepared())}.
     */
    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        Router router = new Router(mListenerAndHandlers, mLastListenerFinished);
        router.route();
    }

    public void startRateMeasurements(){
        super.start();
    }

    @Override
    @Deprecated
    public synchronized void start() {
        super.start();
    }

}
