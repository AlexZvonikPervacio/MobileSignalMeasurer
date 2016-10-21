package pervacio.com.wifisignalstrength.speedMeasurer.actions;

import fr.bmartel.speedtest.SpeedTestSocket;
import pervacio.com.wifisignalstrength.speedMeasurer.ISpeedListenerFinishCallback;
import pervacio.com.wifisignalstrength.speedMeasurer.SpeedListenerHandler;

/**
 * Interface contains {@link WorkerTask} to determine specific connection test action for @link{{@link SpeedTestSocket}}
 */
public interface WorkerTask {
    /**
     * @param speedTestSocket the speed test socket
     * @param handler         the handler uses to process and view data in main thread
     * @param onFinish        callback that detects task completion
     */
    void execute(SpeedTestSocket speedTestSocket, SpeedListenerHandler handler, ISpeedListenerFinishCallback onFinish);
}