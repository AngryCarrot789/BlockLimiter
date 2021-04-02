package reghzy.blocklimiter.limiting.utils;

import reghzy.blocklimiter.helpers.MathsHelper;

public class IntegerRange {
    public int min;
    public int max;

    public IntegerRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public IntegerRange(String formatted) {
        int minIndex = formatted.indexOf("to");
        this.min = Integer.parseInt(formatted.substring(0, minIndex).trim());
        this.max = Integer.parseInt(formatted.substring(minIndex + 2).trim());
    }

    public boolean isBetween(int value) {
        return MathsHelper.between(value, this.min, this.max);
    }

    @Override
    public int hashCode() {
        return getHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IntegerRange) {
            return ((IntegerRange) obj).hashCode() == this.hashCode();
        }
        return false;
    }

    public static int getHashCode(int min, int max) {
        return min + (max << 15);
    }

    public static int getHashCode(IntegerRange range) {
        return range.min + (range.max << 15);
    }
}
