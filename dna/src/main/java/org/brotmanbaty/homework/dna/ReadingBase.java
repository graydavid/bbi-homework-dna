package org.brotmanbaty.homework.dna;

import java.util.Objects;

/** A scanning/reading of one of a DNA moleculé's bases. */
public class ReadingBase {
    private final Base base;
    private final QualityScore score;

    private ReadingBase(Base base, QualityScore score) {
        this.base = Objects.requireNonNull(base);
        this.score = Objects.requireNonNull(score);
    }

    public static ReadingBase of(Base base, QualityScore score) {
        return new ReadingBase(base, score);
    }

    public Base getBase() {
        return base;
    }

    public QualityScore getQualityScore() {
        return score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, score);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ReadingBase) {
            ReadingBase other = (ReadingBase) object;
            return Objects.equals(this.base, other.base) && Objects.equals(this.score, other.score);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("<%s, %s>", base, score);
    }
}
