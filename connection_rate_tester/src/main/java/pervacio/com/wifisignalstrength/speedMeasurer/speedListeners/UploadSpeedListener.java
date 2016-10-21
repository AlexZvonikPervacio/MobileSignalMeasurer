package pervacio.com.wifisignalstrength.speedMeasurer.speedListeners;

import fr.bmartel.speedtest.SpeedTestReport;
import pervacio.com.wifisignalstrength.speedMeasurer.ISpeedListenerFinishCallback;
import pervacio.com.wifisignalstrength.speedMeasurer.SpeedListenerHandler;

/**
 * The concrete realization to observe upload callbacks.
 */
public class UploadSpeedListener extends AbstractSpeedListener {

    public UploadSpeedListener(SpeedListenerHandler handler, ISpeedListenerFinishCallback mOnFinish) {
        super(handler, mOnFinish);
    }

    @Override
    public void onUploadProgress(float percent, SpeedTestReport report) {
        onUpdate(report);
    }

    @Override
    public void onUploadFinished(SpeedTestReport report) {
        onStop();
    }

}