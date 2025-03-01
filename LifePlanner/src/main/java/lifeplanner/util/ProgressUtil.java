package lifeplanner.util;

public class ProgressUtil {
    public static String computeProgressBar(int milestoneCount, int total, int originalPercentage) {
        int fireCount = milestoneCount;  // number of emojis for milestones reached
        int windCount = total - fireCount;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fireCount; i++) {
            sb.append("🔥");
        }
        for (int i = 0; i < windCount; i++) {
            sb.append("💨");
        }
        sb.append(" ").append(originalPercentage).append("%");
        return sb.toString();
    }
}