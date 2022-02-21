package org.brotmanbaty.homework.dna;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

public class FastqWriterUtils {
    private FastqWriterUtils() {}


    /**
     * Writes the fragmentNumber-th ReadingFragment to the given writer.
     * 
     * @throws IllegalArgumentException if fragmentNumber <= 0
     * @throws UncheckedIOException if writing the stream throws an IOException, where the cause of the
     *         UncheckedIOException is the IOException.
     */
    public static void writeReadingFragment(Writer writer, ReadingFragment fragment, long fragmentNumber) {
        if (fragmentNumber <= 0) {
            throw new IllegalArgumentException("fragmentNumber must be greater than 0, but found " + fragmentNumber);
        }

        StringBuilder output = new StringBuilder();
        output.append("@READ_" + fragmentNumber + "\n");
        appendLine2(output, fragment);
        output.append("+READ_" + fragmentNumber + "\n");
        appendLine4(output, fragment);
        uncheckedWriterWriteLine(writer, output.toString());
    }

    private static void appendLine2(StringBuilder output, ReadingFragment fragment) {
        fragment.getReadingBases().stream().map(rb -> rb.getBase().toString()).forEach(output::append);
        output.append("\n");
    }

    private static void appendLine4(StringBuilder output, ReadingFragment fragment) {
        fragment.getReadingBases()
                .stream()
                .map(rb -> String.valueOf(rb.getQualityScore().toFastq()))
                .forEach(output::append);
        output.append("\n");
    }

    public static void uncheckedWriterWriteLine(Writer writer, String line) {
        try {
            writer.write(line);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }
}
