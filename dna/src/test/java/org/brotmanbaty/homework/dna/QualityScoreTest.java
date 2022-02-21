package org.brotmanbaty.homework.dna;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class QualityScoreTest {
    @Test
    public void ofBinaryThrowsExceptionGivenOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> QualityScore.ofBinary((byte) 64));
        assertThrows(IllegalArgumentException.class, () -> QualityScore.ofBinary((byte) -1));
    }

    @Test
    public void ofBinaryAllowsValidInRange() {
        QualityScore.ofBinary((byte) 0);
        QualityScore.ofBinary((byte) 15);
        QualityScore.ofBinary((byte) 63);
    }

    @Test
    public void toBinaryReturnsInternalRepresentation() {
        assertThat(QualityScore.ofBinary((byte) 0).toBinary(), is((byte) 0));
        assertThat(QualityScore.ofBinary((byte) 15).toBinary(), is((byte) 15));
        assertThat(QualityScore.ofBinary((byte) 63).toBinary(), is((byte) 63));
    }

    @Test
    public void toFastqBinaryReturnsTransformedRepresentation() {
        assertThat(QualityScore.ofBinary((byte) 0).toFastq(), is('!'));
        assertThat(QualityScore.ofBinary((byte) 15).toFastq(), is('0'));
        assertThat(QualityScore.ofBinary((byte) 63).toFastq(), is('`'));
    }

    @Test
    public void hashCodeObeysContract() {
        QualityScore score1 = QualityScore.ofBinary((byte) 0b00010000);
        QualityScore score2 = QualityScore.ofBinary((byte) 0b00010000);

        // Same object produces same hash code on multiple calls
        assertThat(score1.hashCode(), is(score1.hashCode()));
        // Equal objects have same hash code
        assertThat(score1.hashCode(), is(score2.hashCode()));
    }

    @Test
    public void equalsObeysContract() {
        QualityScore score1 = QualityScore.ofBinary((byte) 0b00010000);
        QualityScore score2 = QualityScore.ofBinary((byte) 0b00010000);
        QualityScore different = QualityScore.ofBinary((byte) 0b00100000);

        // Reflexive
        assertThat(score1, is(score1));
        // Symmetric
        assertThat(score1, not(different));
        assertThat(different, not(score1));
        assertThat(score1, equalTo(score2));
        assertThat(score2, equalTo(score1));
    }

    @Test
    public void toStringReturnsDecimalStringRepresentationOfBinary() {
        assertThat(QualityScore.ofBinary((byte) 15).toString(), is("15"));
    }
}
