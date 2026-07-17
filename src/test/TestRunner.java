package test;

public class TestRunner {
    public static void main(String[] args) throws Exception {
        int failed = 0;
        int totalSuites = 3;

        failed += runSuite("LoanDomainTests", new LoanDomainTests());
        failed += runSuite("LoanRulesTests", new LoanRulesTests());
        failed += runSuite("MainFlowTests", new MainFlowTests());

        int passed = totalSuites - failed;
        System.out.println();
        System.out.println("Test summary: " + passed + "/" + totalSuites + " test suites passed.");

        if (failed > 0) {
            throw new AssertionError("Some tests failed.");
        }
    }

    private static int runSuite(String suiteName, Object suite) {
        try {
            suite.getClass().getDeclaredMethod("runAll").invoke(suite);
            System.out.println("[PASS] " + suiteName);
            return 0;
        } catch (Exception ex) {
            Throwable cause = ex.getCause() == null ? ex : ex.getCause();
            System.out.println("[FAIL] " + suiteName + " -> " + cause.getMessage());
            cause.printStackTrace(System.out);
            return 1;
        }
    }
}

