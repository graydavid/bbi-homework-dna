package org.brotmanbaty.homework.dna;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/** A set of utility methods for reading/writing fragments from/into a binary format. */
public class BinaryStreamUtils {
    private BinaryStreamUtils() {}

    /**
     * Reads numReadingBases from the given stream to create a ReadingFragment. If no bases are requested, then an empty
     * fragment is returned. If the end of the stream is reached before any bases are read, then null is returned.
     * 
     * @throws IllegalStateException if the stream reaches its end before numReadingBases are read.
     * @throws RuntimeException if reading the stream throws an IOException, where the cause of the RuntimeException is
     *         the IOException.
     */
    public static ReadingFragment readReadingFragment(InputStream stream, int numReadingBases) {
        List<ReadingBase> readingBases = new ArrayList<>(numReadingBases);
        for (int i = 0; i < numReadingBases; ++i) {
            ReadingBase readingBase = readReadingBase(stream);
            if (readingBase == null) {
                break;
            }
            readingBases.add(readingBase);
        }

        // If there was nothing there to begin with, quit
        if (readingBases.isEmpty() & numReadingBases != 0) {
            return null;
        }

        // Check if read as much as requested
        if (readingBases.size() < numReadingBases) {
            String message = String.format(
                    "Expected to find %s base readings in stream, but only found %s before end of stream.",
                    numReadingBases, readingBases.size());
            throw new IllegalStateException(message);
        }

        return ReadingFragment.of(readingBases);
    }

    /**
     * Reads the next base from the given input stream, returning null if the stream is already at the end.
     * 
     * @throws RuntimeException if reading the stream throws an IOException, where the cause of the RuntimeException is
     *         the IOException.
     */
    public static ReadingBase readReadingBase(InputStream stream) {
        int intInput = uncheckedCall(() -> stream.read());

        if (intInput == -1) {
            return null;
        }

        // Must wait until after the check above to convert to byte, otherwise, values of 255 (a valid reading) and -1
        // (end of stream) would be ambiguous
        byte byteInput = (byte) intInput;
        Base base = convertBinaryToBase(byteInput);
        QualityScore score = convertBinaryToQualityScore(byteInput);
        return ReadingBase.of(base, score);
    }

    private static <T> T uncheckedCall(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Base convertBinaryToBase(byte binary) {
        // Need to mask the bits due to int promotion: https://stackoverflow.com/a/3948303
        int binaryWithCorrectBitsAsInt = binary & 0xFF;
        byte firstTwoBits = (byte) (binaryWithCorrectBitsAsInt >>> 6);
        switch (firstTwoBits) {
            case 0b00000000:
                return Base.A;
            case 0b00000001:
                return Base.C;
            case 0b00000010:
                return Base.G;
            case 0b00000011:
                return Base.T;
            default:
                throw new IllegalStateException("Encountered unexpected value for first two bits: " + firstTwoBits);
        }
    }

    private static QualityScore convertBinaryToQualityScore(byte binary) {
        byte lastSixBits = (byte) (binary & 0b00111111);
        return QualityScore.ofBinary(lastSixBits);
    }

    /**
     * Writes a ReadingFragment to the given output stream. If the fragment has no bases, then nothing is written.
     * 
     * @throws RuntimeException if writing the stream throws an IOException, where the cause of the RuntimeException is
     *         the IOException.
     */
    public static void writeReadingFragment(OutputStream stream, ReadingFragment fragment) {
        fragment.getReadingBases().forEach(base -> writeReadingBase(stream, base));
    }

    /**
     * Writes a single base to the given output stream.
     * 
     * @throws NullPointerException if readingBase is null.
     * @throws RuntimeException if writing the stream throws an IOException, where the cause of the RuntimeException is
     *         the IOException.
     */
    public static void writeReadingBase(OutputStream stream, ReadingBase readingBase) {
        byte baseByte = convertBaseToBinary(readingBase.getBase());
        byte scoreByte = readingBase.getQualityScore().toBinary();
        byte overallByte = (byte) (baseByte | scoreByte);
        uncheckedCall(() -> {
            stream.write(overallByte);
            return null;
        });
    }

    private static byte convertBaseToBinary(Base base) {
        switch (base) {
            case A:
                return 0b00000000;
            case C:
                return 0b01000000;
            case G:
                return (byte) 0b10000000;
            case T:
                return (byte) 0b11000000;
            default:
                throw new IllegalStateException("Encountered unexpected value for base: " + base);
        }
    }
}
