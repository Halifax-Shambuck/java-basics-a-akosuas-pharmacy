package com.akosua.pharmacy.app;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GoldenOutputTest {

    @Test
    void outputMatchesOriginalBaseline() throws Exception {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
            PharmacyTracker.main(new String[0]);
        } finally {
            System.setOut(originalOut); // always give the console back
        }

        String actual = normalise(captured.toString(StandardCharsets.UTF_8));

        String expected;
        try (InputStream in = GoldenOutputTest.class.getResourceAsStream("/baseline-output.txt")) {
            assertNotNull(in, "baseline-output.txt not found in src/test/resources");
            expected = normalise(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        }

        assertEquals(expected, actual);
    }

    // Ignore Windows/Linux line-ending differences, a leading BOM, and trailing
    // blank lines.
    private static String normalise(String text) {
        return text.replace("\uFEFF", "").replace("\r\n", "\n").strip();
    }
}