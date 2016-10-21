package pervacio.com.wifisignalstrength.speedMeasurer;

import pervacio.com.wifisignalstrength.speedMeasurer.speedListeners.AbstractSpeedListener;

public interface ISpeedListenerFinishCallback {
    void onSpeedListenerFinish(AbstractSpeedListener speedListener);
}