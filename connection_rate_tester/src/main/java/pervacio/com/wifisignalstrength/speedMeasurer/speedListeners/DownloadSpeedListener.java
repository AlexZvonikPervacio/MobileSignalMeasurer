package pervacio.com.wifisignalstrength.speedMeasurer.speedListeners;

import fr.bmartel.speedtest.SpeedTestReport;
import pervacio.com.wifisignalstrength.speedMeasurer.ISpeedListenerFinishCallback;
import pervacio.com.wifisignalstrength.speedMeasurer.SpeedListenerHandler;

/**
 * The concrete realization to observe download callbacks.
 */
public class DownloadSpeedListener extends AbstractSpeedListener {

    public DownloadSpeedListener(SpeedListenerHandler handler, ISpeedListenerFinishCallback mOnFinish) {
        super(handler, mOnFinish);
    }

    @Override
    public void onDownloadProgress(float percent, SpeedTestReport report) {
        onUpdate(report);
    }

    @Override
    public void onDownloadFinished(SpeedTestReport report) {
        onStop();
    }

}
