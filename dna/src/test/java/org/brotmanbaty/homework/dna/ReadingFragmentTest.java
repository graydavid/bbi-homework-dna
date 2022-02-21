package org.brotmanbaty.homework.dna;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ReadingFragmentTest {
    @Test
    public void accessorReturnsRightValues() {
        QualityScore score = QualityScore.ofBinary((byte) 32);
        ReadingBase readingBase1 = ReadingBase.of(Base.C, score);
        ReadingBase readingBase2 = ReadingBase.of(Base.G, score);
        ReadingFragment readingFragment = ReadingFragment.of(List.of(readingBase1, readingBase2));

        assertThat(readingFragment.getReadingBases(), is(List.of(readingBase1, readingBase2)));
    }

    @Test
    public void hashCodeObeysContract() {
        ReadingBase readingBase = ReadingBase.of(Base.G, QualityScore.ofBinary((byte) 0b00010000));
        ReadingFragment readingFragment1 = ReadingFragment.of(List.of(readingBase));
        ReadingFragment readingFragment2 = ReadingFragment.of(List.of(readingBase));

        // Same object produces same hash code on multiple calls
        assertThat(readingFragment1.hashCode(), is(readingFragment1.hashCode()));
        // Equal objects have same hash code
        assertThat(readingFragment1.hashCode(), is(readingFragment2.hashCode()));
    }

    @Test
    public void equalsObeysContract() {
        ReadingBase readingBase = ReadingBase.of(Base.G, QualityScore.ofBinary((byte) 0b00010000));
        ReadingFragment readingFragment1 = ReadingFragment.of(List.of(readingBase));
        ReadingFragment readingFragment2 = ReadingFragment.of(List.of(readingBase));
        ReadingFragment differentNumBases = ReadingFragment.of(List.of());
        ReadingBase differentBase = ReadingBase.of(Base.T, QualityScore.ofBinary((byte) 0b00100000));
        ReadingFragment differentBases = ReadingFragment.of(List.of(differentBase));

        // Reflexive
        assertThat(readingFragment1, is(readingFragment1));
        // Symmetric
        assertThat(readingFragment1, not(differentNumBases));
        assertThat(differentNumBases, not(readingFragment1));
        assertThat(readingFragment1, not(differentBases));
        assertThat(differentBases, not(readingFragment1));
        assertThat(readingFragment1, equalTo(readingFragment2));
        assertThat(readingFragment2, equalTo(readingFragment1));
    }

    @Test
    public void toStringReturnsReasonableRepresentation() {
        ReadingBase readingBase1 = ReadingBase.of(Base.C, QualityScore.ofBinary((byte) 32));
        ReadingBase readingBase2 = ReadingBase.of(Base.A, QualityScore.ofBinary((byte) 12));
        ReadingFragment fragment = ReadingFragment.of(List.of(readingBase1, readingBase2));

        assertThat(fragment.toString(), is("[<C, 32>, <A, 12>]"));
    }
}
