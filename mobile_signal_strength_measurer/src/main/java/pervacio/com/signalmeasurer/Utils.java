package pervacio.com.signalmeasurer;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**
     * Gets background font and text for signal level.
     *
     * @param context the context
     * @return the signal criterion
     */
    public static List<SignalCriteria> getSignalCriterion(Context context) {
        final ArrayList<SignalCriteria> signalCriteriaList = new ArrayList<>();
        final String[] criteria = context.getResources().getStringArray(R.array.criteria);
        for (String item : criteria){
            signalCriteriaList.add(new SignalCriteria(item));
        }
        return signalCriteriaList;
    }

}
