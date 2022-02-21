package org.brotmanbaty.homework.dna;

import java.util.List;
import java.util.Objects;

/** An ordered grouping/sequence of ReadingBases. */
public class ReadingFragment {
    private final List<ReadingBase> readingBases;

    private ReadingFragment(List<ReadingBase> readingBases) {
        this.readingBases = List.copyOf(readingBases);
    }

    public static ReadingFragment of(List<ReadingBase> readingBases) {
        return new ReadingFragment(readingBases);
    }

    public List<ReadingBase> getReadingBases() {
        return readingBases;
    }

    @Override
    public int hashCode() {
        return Objects.hash(readingBases);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ReadingFragment) {
            ReadingFragment other = (ReadingFragment) object;
            return Objects.equals(this.readingBases, other.readingBases);
        }
        return false;
    }

    @Override
    public String toString() {
        return readingBases.toString();
    }
}
