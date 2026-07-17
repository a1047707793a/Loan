package test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Objects;

final class TestAssertions {
    private TestAssertions() {
    }

    interface ThrowingRunnable {
        void run() throws Exception;
    }

    static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " Expected: " + expected + ", but was: " + actual);
        }
    }

    static void assertBigDecimalEquals(String expected, Object actual, String message) {
        BigDecimal expectedValue = new BigDecimal(expected);
        if (!(actual instanceof BigDecimal actualValue) || expectedValue.compareTo(actualValue) != 0) {
            throw new AssertionError(message + " Expected: " + expectedValue + ", but was: " + actual);
        }
    }

    static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void assertContains(String text, String expectedFragment, String message) {
        if (text == null || !text.contains(expectedFragment)) {
            throw new AssertionError(message + " Missing fragment: " + expectedFragment + "\nActual output:\n" + text);
        }
    }

    static void assertThrows(Class<? extends Throwable> expectedType, ThrowingRunnable action, String message) {
        try {
            action.run();
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (expectedType.isInstance(cause)) {
                return;
            }
            throw new AssertionError(message + " Expected exception: " + expectedType.getSimpleName()
                    + ", but was: " + cause, cause);
        } catch (Throwable ex) {
            if (expectedType.isInstance(ex)) {
                return;
            }
            throw new AssertionError(message + " Expected exception: " + expectedType.getSimpleName()
                    + ", but was: " + ex, ex);
        }
        throw new AssertionError(message + " Expected exception: " + expectedType.getSimpleName() + ", but nothing was thrown.");
    }
}

