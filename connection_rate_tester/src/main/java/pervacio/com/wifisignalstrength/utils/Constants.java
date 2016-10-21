package pervacio.com.wifisignalstrength.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Constants {
    //Connection constants
    public static final String SPEED_TEST_SERVER_HOST = "2.testdebit.info";
    public static final int SPEED_TEST_SERVER_PORT = 80;
    public static final String SPEED_TEST_SERVER_URI_DL = "/fichiers/1000Mo.dat";
    public static final int FILE_SIZE = 30_000_000;
    public static final int SPEED_TEST_MAX_DURATION = 10000;
    public static final int SPEED_TEST_REPORT_INTERVAL = 500;

    //Loading status
    public static final int START = 1;
    public static final int PROGRESS = 2;
    public static final int FINISH = 3;
    public static final int ERROR = 4;

    @IntDef({START, PROGRESS, FINISH, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoadingStatus {
    }

}
