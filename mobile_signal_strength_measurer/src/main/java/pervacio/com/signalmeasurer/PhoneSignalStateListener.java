package pervacio.com.signalmeasurer;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import java.util.List;

/**
 * Class used to transmit cell signal strength level
 */
public class PhoneSignalStateListener extends PhoneStateListener {

    private final List<SignalCriteria> SIGNAL_CRITERIA_LIST;

    private SignalState mSignalStateListener;
    private SignalStrength mLastSignalStrength;

    public PhoneSignalStateListener(SignalState signalStateListener, Context context) {
        this.mSignalStateListener = signalStateListener;
        SIGNAL_CRITERIA_LIST = Utils.getSignalCriterion(context);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        mLastSignalStrength = signalStrength;
        if (mSignalStateListener == null) {
            return;
        }
        int mSignalStrengthAsu = signalStrength.getGsmSignalStrength();
        if (mSignalStrengthAsu == 99) {
            mSignalStateListener.onFailedToMeasure("Unknown or not detectable");
        } else {
            mSignalStateListener.onSignalChanged(findCriteria(mSignalStrengthAsu));
        }
    }

    /**
     * Gets asu level.
     *
     * @return the asu
     */
    public int getAsu() {
        return mLastSignalStrength.getGsmSignalStrength();
    }

    /**
     * Gets dbm level.
     *
     * @return the dbm
     */
    public int getDbm() {
        return mLastSignalStrength.isGsm() ? 2 *
                mLastSignalStrength.getGsmSignalStrength() - 113 :
                mLastSignalStrength.getGsmSignalStrength() - 116;
    }

    private SignalCriteria findCriteria(final int asu) {
        SignalCriteria prevCriteria = SIGNAL_CRITERIA_LIST.get(0);
        for (SignalCriteria criteria : SIGNAL_CRITERIA_LIST) {
            if (asu < criteria.getAsu()) {
                return new SignalCriteria(asu, prevCriteria.getTitle(), prevCriteria.getColor());
            }
            prevCriteria = criteria;
        }
        prevCriteria = SIGNAL_CRITERIA_LIST.get(SIGNAL_CRITERIA_LIST.size() - 1);
        return new SignalCriteria(asu, prevCriteria.getTitle(), prevCriteria.getColor());
    }


    /**
     * The public interface uses to receive cell signal strength level.
     */
    public interface SignalState {
        /**
         * On signal changed.
         *
         * @param criteria the criteria
         */
        void onSignalChanged(SignalCriteria criteria);

        /**
         * On failed to measure.
         *
         * @param message the message
         */
        void onFailedToMeasure(String message);
    }

}