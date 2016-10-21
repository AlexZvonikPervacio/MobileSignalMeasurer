package pervacio.com.wifisignalstrength.speedMeasurer.actions;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import fr.bmartel.speedtest.SpeedTestSocket;
import pervacio.com.wifisignalstrength.speedMeasurer.ISpeedListenerFinishCallback;
import pervacio.com.wifisignalstrength.speedMeasurer.SpeedListenerHandler;
import pervacio.com.wifisignalstrength.speedMeasurer.speedListeners.DownloadSpeedListener;
import pervacio.com.wifisignalstrength.speedMeasurer.speedListeners.UploadSpeedListener;

import static pervacio.com.wifisignalstrength.utils.Constants.FILE_SIZE;
import static pervacio.com.wifisignalstrength.utils.Constants.SPEED_TEST_MAX_DURATION;
import static pervacio.com.wifisignalstrength.utils.Constants.SPEED_TEST_REPORT_INTERVAL;
import static pervacio.com.wifisignalstrength.utils.Constants.SPEED_TEST_SERVER_HOST;
import static pervacio.com.wifisignalstrength.utils.Constants.SPEED_TEST_SERVER_PORT;
import static pervacio.com.wifisignalstrength.utils.Constants.SPEED_TEST_SERVER_URI_DL;

/**
 * The type uses to define default upload/download connection test.
 */
public class DefaultWorkerTask implements WorkerTask {

    public static final int DOWNLOAD = 1;
    public static final int UPLOAD = 2;

    @TaskType
    private int mType;

    public DefaultWorkerTask(@TaskType int type) {
        mType = type;
    }


    @Override
    public void execute(SpeedTestSocket speedTestSocket, SpeedListenerHandler handler, ISpeedListenerFinishCallback onFinish) {
        if (DOWNLOAD == mType) {
            speedTestSocket.addSpeedTestListener(new DownloadSpeedListener(handler, onFinish));
            speedTestSocket.startFixedDownload(
                    SPEED_TEST_SERVER_HOST,
                    SPEED_TEST_SERVER_PORT,
                    SPEED_TEST_SERVER_URI_DL,
                    SPEED_TEST_MAX_DURATION,
                    SPEED_TEST_REPORT_INTERVAL);
        } else if (UPLOAD == mType) {
            speedTestSocket.addSpeedTestListener(new UploadSpeedListener(handler, onFinish));
            speedTestSocket.startFixedUpload(
                    SPEED_TEST_SERVER_HOST,
                    SPEED_TEST_SERVER_PORT,
                    SPEED_TEST_SERVER_URI_DL,
                    FILE_SIZE,
                    SPEED_TEST_MAX_DURATION,
                    SPEED_TEST_REPORT_INTERVAL);
        }
    }

    @IntDef({DOWNLOAD, UPLOAD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TaskType {

    }

}