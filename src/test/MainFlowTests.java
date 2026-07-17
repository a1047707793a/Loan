package test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

final class MainFlowTests {
    void runAll() throws Exception {
        testInvalidCustomerIdHandledSafely();
        testInputStreamEndsSafely();
        testBlankNameHandledWithoutCrash();
    }

    private void testInvalidCustomerIdHandledSafely() throws Exception {
        String output = runMainWithInput("1\nAlice\n1000\n0\n2\n99\n4\n");

        TestAssertions.assertContains(output, "Customer loan added successfully.",
                "Main flow should still add a valid loan before testing invalid ID.");
        TestAssertions.assertContains(output, "Error: Invalid customer ID.",
                "Invalid customer IDs should be handled gracefully.");
        TestAssertions.assertContains(output, "Exiting program...",
                "Program should continue running after invalid customer ID input.");
    }

    private void testInputStreamEndsSafely() throws Exception {
        String output = runMainWithInput("1\nAlice\n1000\n0");

        TestAssertions.assertContains(output, "Input stream ended. Exiting program...",
                "Exhausted input should end the program safely instead of crashing.");
    }

    private void testBlankNameHandledWithoutCrash() throws Exception {
        String output = runMainWithInput("1\n   \nAlice\n1000\n0\n4\n");

        TestAssertions.assertContains(output, "Invalid name. Use letters and spaces only.",
                "Blank names should be rejected safely in the main flow.");
        TestAssertions.assertContains(output, "Customer loan added successfully.",
                "Program should continue after rejecting a blank name.");
    }

    private String runMainWithInput(String input) throws Exception {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayInputStream fakeInput = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream fakeOutput = new ByteArrayOutputStream();
        PrintStream capture = new PrintStream(fakeOutput, true, StandardCharsets.UTF_8);

        try {
            System.setIn(fakeInput);
            System.setOut(capture);
            ReflectionSupport.invokeMain();
            return fakeOutput.toString(StandardCharsets.UTF_8);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
            capture.close();
        }
    }
}

