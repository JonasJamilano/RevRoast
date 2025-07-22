import java.util.HashMap;
import java.util.Map;

public class currency {
    public enum CurrencyType {
        PHP, USD, YEN
    }

    private static CurrencyType currentCurrency = CurrencyType.PHP;

    private static final Map<CurrencyType, Double> conversionRates = new HashMap<>();

    static {
        // Example conversion rates: 1 PHP is base
        conversionRates.put(CurrencyType.PHP, 1.0);
        conversionRates.put(CurrencyType.USD, 0.018); // 1 PHP = 0.018 USD
        conversionRates.put(CurrencyType.YEN, 2.65);  // 1 PHP = 2.65 YEN
    }

    public static void setCurrency(CurrencyType currency) {
        currentCurrency = currency;
    }

    public static CurrencyType getCurrentCurrency() {
        return currentCurrency;
    }

    public static double convert(double phpAmount) {
        return phpAmount * conversionRates.get(currentCurrency);
    }

    public static String getCurrencySymbol() {
        switch (currentCurrency) {
            case USD: return "$";
            case YEN: return "¥";
            default: return "₱";
        }
    }
}