package org.brotmanbaty.homework.dna;

import java.util.Objects;

/** The confidence that the a DNA base scan is correct. As a number, it represents an integer in the range 0 to 63. */
public class QualityScore {
    private final byte binary;

    private QualityScore(byte binary) {
        this.binary = requireInRange(binary);
    }

    private static byte requireInRange(byte binary) {
        if (binary < 0) {
            throw new IllegalArgumentException("binary quality scores must be positive, but found " + binary);
        }

        if (binary > 63) {
            throw new IllegalArgumentException("binary quality scores must less than 64, but found " + binary);
        }
        return binary;
    }

    /**
     * Returns a quality score from its binary representation.
     * 
     * @throws IllegalArgumentException if binary < or binary > 64.
     */
    public static QualityScore ofBinary(byte binary) {
        return new QualityScore(binary);
    }

    public final byte toBinary() {
        return binary;
    }

    /**
     * Returns the FASTQ format for this score. This format is achieved by adding 33 to the integer score and casting
     * the result to a char.
     */
    public final char toFastq() {
        return (char) (binary + 33);
    }

    @Override
    public int hashCode() {
        return Objects.hash(binary);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof QualityScore) {
            QualityScore other = (QualityScore) object;
            return Objects.equals(this.binary, other.binary);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(binary);
    }
}
