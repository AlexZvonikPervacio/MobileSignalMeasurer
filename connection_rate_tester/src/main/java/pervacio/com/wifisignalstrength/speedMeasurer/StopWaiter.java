package pervacio.com.wifisignalstrength.speedMeasurer;

import android.os.SystemClock;

import java.util.concurrent.atomic.AtomicLong;

import static pervacio.com.wifisignalstrength.utils.Constants.SPEED_TEST_REPORT_INTERVAL;

/**
 * The class performs waiting thread to stop listening connection rate callbacks after file
 */
public class StopWaiter extends Thread {

    private AtomicLong atomicLong = new AtomicLong();
    private OnTimerStop mOnTimerStop;

    public StopWaiter(OnTimerStop onTimerStop) {
        this.mOnTimerStop = onTimerStop;
    }

    @Override
    public void run() {
        atomicLong.set(System.currentTimeMillis());
        long atomicLongGet = atomicLong.get();
        long currentTimeMillis = System.currentTimeMillis();
        while (Math.abs(atomicLongGet - currentTimeMillis) < SPEED_TEST_REPORT_INTERVAL * 1.5) {
            SystemClock.sleep(SPEED_TEST_REPORT_INTERVAL / 2);
            atomicLongGet = atomicLong.get();
            currentTimeMillis = System.currentTimeMillis();
        }
        if (mOnTimerStop != null) {
            mOnTimerStop.onTimerStop();
        }
    }

    public void updateTimer() {
        atomicLong.set(System.currentTimeMillis());
    }

    public interface OnTimerStop {
        void onTimerStop();
    }

}