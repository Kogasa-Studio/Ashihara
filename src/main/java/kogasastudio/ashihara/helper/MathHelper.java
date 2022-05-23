package kogasastudio.ashihara.helper;

public class MathHelper {
    public static double simplifyDouble(Double d, int bit) {
        return Math.round(d * Math.pow(10d, bit)) / Math.pow(10d, bit);
    }
}
