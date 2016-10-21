package pervacio.com.wifisignalstrength.speedMeasurer;

import android.os.Handler;

import pervacio.com.wifisignalstrength.speedMeasurer.actions.WorkerTask;

/**
 * Class contains @link{{@link WorkerTask}} to determine connection rate test action
 * and @link{{@link android.os.Handler.Callback}} to determine result callback
 */
public class TaskAndHandlerWrapper {

    public WorkerTask mWorkerTask;
    public Handler.Callback mCallback;

    public TaskAndHandlerWrapper(WorkerTask workerTask, Handler.Callback callback) {
        this.mWorkerTask = workerTask;
        this.mCallback = callback;
    }

}
