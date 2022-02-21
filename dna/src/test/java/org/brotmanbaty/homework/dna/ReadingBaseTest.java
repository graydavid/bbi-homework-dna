package org.brotmanbaty.homework.dna;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Test;

public class ReadingBaseTest {
    @Test
    public void accessorsReturnRightValues() {
        QualityScore score = QualityScore.ofBinary((byte) 32);
        ReadingBase reading = ReadingBase.of(Base.C, score);

        assertThat(reading.getBase(), is(Base.C));
        assertThat(reading.getQualityScore(), is(score));
    }

    @Test
    public void hashCodeObeysContract() {
        ReadingBase reading1 = ReadingBase.of(Base.G, QualityScore.ofBinary((byte) 0b00010000));
        ReadingBase reading2 = ReadingBase.of(Base.G, QualityScore.ofBinary((byte) 0b00010000));

        // Same object produces same hash code on multiple calls
        assertThat(reading1.hashCode(), is(reading1.hashCode()));
        // Equal objects have same hash code
        assertThat(reading1.hashCode(), is(reading2.hashCode()));
    }

    @Test
    public void equalsObeysContract() {
        ReadingBase reading1 = ReadingBase.of(Base.G, QualityScore.ofBinary((byte) 0b00010000));
        ReadingBase reading2 = ReadingBase.of(Base.G, QualityScore.ofBinary((byte) 0b00010000));
        ReadingBase differentBase = ReadingBase.of(Base.T, QualityScore.ofBinary((byte) 0b00010000));
        ReadingBase differentScore = ReadingBase.of(Base.G, QualityScore.ofBinary((byte) 0b00100000));

        // Reflexive
        assertThat(reading1, is(reading1));
        // Symmetric
        assertThat(reading1, not(differentBase));
        assertThat(differentBase, not(reading1));
        assertThat(reading1, not(differentScore));
        assertThat(differentScore, not(reading1));
        assertThat(reading1, equalTo(reading2));
        assertThat(reading2, equalTo(reading1));
    }

    @Test
    public void toStringReturnsReasonableRepresentation() {
        ReadingBase reading = ReadingBase.of(Base.C, QualityScore.ofBinary((byte) 32));

        assertThat(reading.toString(), is("<C, 32>"));
    }
}
