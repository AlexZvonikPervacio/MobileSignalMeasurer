package pervacio.com.signalmeasurer;

import android.graphics.Color;

import java.nio.charset.UnsupportedCharsetException;

/**
 * The type Signal criteria.
 */
public class SignalCriteria {

    private int mAsu;
    private String mTitle;
    private int mColor;

    /**
     * Instantiates a new Signal criteria.
     *
     * @param asu   the asu
     * @param title the title
     * @param color the color
     */
    public SignalCriteria(int asu, String title, int color) {
        this.mAsu = asu;
        this.mTitle = title;
        this.mColor = color;
    }

    /**
     * Instantiates a new Signal criteria.
     *
     * @param rawCriteria the raw criteria. Parameters joined with '|' in one string
     */
    public SignalCriteria(String rawCriteria) {
        final String[] criteria = rawCriteria.split("\\|");
        if (criteria.length != 3){
            throw new UnsupportedCharsetException("Wrong format. length = "  +criteria.length + " " + rawCriteria);
        }
        this.mAsu = Integer.parseInt(criteria[0]);
        this.mColor = Color.parseColor(criteria[1]);
        this.mTitle = criteria[2];
    }

    /**
     * Gets asu level.
     *
     * @return the asu
     */
    public int getAsu() {
        return mAsu;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Gets color.
     *
     * @return the color
     */
    public int getColor() {
        return mColor;
    }

    @Override
    public String toString() {
        return "SignalCriteria{" +
                "Asu=" + mAsu +
                ", Title='" + mTitle + '\'' +
                ", Color=" + mColor +
                '}';
    }

}