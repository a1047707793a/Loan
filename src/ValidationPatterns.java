import java.util.regex.Pattern;

public final class ValidationPatterns {
    private ValidationPatterns() {
        // Utility class; prevent instantiation.
    }

    // Shared name validation rule used across UI and domain layers.
    public static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}]+(?: [\\p{L}]+)*$");

    // Shared positive decimal rule that rejects zero values like 0 or 0.00.
    public static final Pattern POSITIVE_AMOUNT_PATTERN = Pattern.compile("^(?!0+(?:\\.0+)?$)\\d+(?:\\.\\d+)?$");
}

