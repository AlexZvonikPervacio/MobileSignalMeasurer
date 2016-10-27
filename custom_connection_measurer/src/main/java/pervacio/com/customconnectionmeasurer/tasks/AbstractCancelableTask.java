package pervacio.com.customconnectionmeasurer.tasks;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.CheckResult;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import pervacio.com.customconnectionmeasurer.IConnectionTypeChecker;
import pervacio.com.customconnectionmeasurer.TaskException;
import pervacio.com.customconnectionmeasurer.utils.MeasuringUnits;


public abstract class AbstractCancelableTask {

    public static final String TAG = AbstractCancelableTask.class.getSimpleName();
    public static final int CHUNK_SIZE = 4 * 1024;

    private final AtomicBoolean mCancelled = new AtomicBoolean();

    private long mDuration;
    private int mUpdatePeriod;
    private MeasuringUnits mMeasuringUnit;
    private IConnectionTypeChecker mChecker;
    //TODO get rid of or change architecture
//    private LifeCycleCallback mLifeCycleCallback;
    protected Handler mUiHandler;

    public AbstractCancelableTask(long duration, int updatePeriod, MeasuringUnits measuringUnit,
                                  IConnectionTypeChecker checker) {
        mDuration = duration;
        mUpdatePeriod = updatePeriod;
        mMeasuringUnit = measuringUnit;
        mChecker = checker;
        mUiHandler = new Handler(Looper.getMainLooper());
    }

    public final boolean isCancelled() {
        return mCancelled.get();
    }

    public final void cancel() {
        mCancelled.set(true);
    }

    public float startAction() {
        initBeforeStart();
        final String message = mChecker.check();
        if (message != null) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    onError(message);
                }
            });
            cancel();
            return 0f;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(mDuration);
                mCancelled.set(true);
            }
        }).start();
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                onStart();
            }
        });
        try {
            final float result = performAction();
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    onFinish(result);
                }
            });
        } catch (final TaskException e) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {onError(e.getmMessage());

                }
            });
        }
        return 0f;
    }

    @CheckResult
    protected float readBytes(InputStream inputStream, OutputStream outputStream) throws TaskException {

        long totalBytes = 0;
        byte[] buffer = new byte[CHUNK_SIZE];
        long startTime = System.currentTimeMillis();
        //////////////////////////////////////////////////
        final long period = 300;
        //////////////////////////////////////////////////
        Timer timer = new Timer(true);
        AAA timerTask = new AAA((int) (mDuration / period)) {
            @Override
            public void run() {
                float convertBytes = mMeasuringUnit.convertBytes(mLoadedBytes, period);
                mPrevResults.add(convertBytes);
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onProgress(getMean());
                    }
                });
                mLoadedBytes = 0;
            }
        };
        timer.schedule(timerTask, period, period);

        try {
            int bytesRead;
            int counter = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                if (isCancelled()) {
                    inputStream.close();
                    timerTask.cancel();
                    timer.cancel();
                    timer.purge();
                    final float rate = mMeasuringUnit.convertBytes(totalBytes, System.currentTimeMillis() - startTime);
                    Log.e("readBytes", "rate = " + rate + ", totalBytes = " + totalBytes);
                    return rate;
                }
                totalBytes += bytesRead;
                timerTask.addBytes(bytesRead);
                outputStream.write(buffer, 0, bytesRead);
//                if (counter % 50 == 0) {
//                    onProgress(totalBytes);
//                }
//                counter++;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            throw new TaskException(e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ignored) {
                Log.e("readBytes", "ignored = " + ignored.getMessage());
            }
            timerTask.cancel();
            timer.cancel();
            timer.purge();
        }
        final float rate = mMeasuringUnit.convertBytes(totalBytes, System.currentTimeMillis() - startTime);
        Log.e("readBytes", "rate = " + rate + ", totalBytes = " + totalBytes);
        return rate;
    }

    static abstract class AAA extends TimerTask {

        /* volatile*/ long mAverage;
        /* volatile*/ long mLoadedBytes;
        private int mArraySize;
        protected List<Float> mPrevResults;

        public AAA(int arraySize) {
            mArraySize = arraySize;
            mPrevResults = new ArrayList<>(mArraySize);
        }

        public void addBytes(long bytes) {
            mLoadedBytes += bytes;
        }

        public float getMean() {
            if (mPrevResults.size() == 0) {
                return Float.NaN;
            }
            return sum(mPrevResults) / mPrevResults.size();
        }

        private static float sum(List<Float> floats) {
            float sum = 0.0f;
            for (Float aFloat : floats) {
                sum += aFloat;
            }
            return sum;
        }

    }

    protected void initBeforeStart() {
    }

    abstract float performAction() throws TaskException;

    abstract void onStart();

    abstract void onProgress(float result);

    abstract void onFinish(float result);

    abstract void onError(String message);

    public Callable<Float> getCallable() {
        return new Callable<Float>() {
            @Override
            public Float call() throws Exception {
                return startAction();
            }
        };
    }

    public static Callable<String> getEmptyCallable(final String message) {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                return message;
            }
        };
    }

}
