package org.brotmanbaty.homework.dna;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringWriter;
import java.util.List;

import org.junit.jupiter.api.Test;

public class FastqWriterUtilsTest {
    private final StringWriter writer = new StringWriter();

    @Test
    public void writeReadingFragmentThrowsExceptionIfFragmenNumberIsNotPositive() {
        ReadingFragment fragment = ReadingFragment
                .of(List.of(ReadingBase.of(Base.A, QualityScore.ofBinary((byte) 0b00000000))));

        assertThrows(IllegalArgumentException.class, () -> FastqWriterUtils.writeReadingFragment(writer, fragment, -1));
        assertThrows(IllegalArgumentException.class, () -> FastqWriterUtils.writeReadingFragment(writer, fragment, 0));
    }

    @Test
    public void writeReadingFragmentCanWriteAFragmentWithNoBases() {
        ReadingFragment fragment = ReadingFragment.of(List.of());

        FastqWriterUtils.writeReadingFragment(writer, fragment, 123);

        String expectedOutput = "@READ_123\n" + "\n" + "+READ_123\n" + "\n";
        assertThat(writer.toString(), is(expectedOutput));
    }

    @Test
    public void writeReadingFragmentCanWriteAFragmentWithOneBase() {
        ReadingFragment fragment = ReadingFragment
                .of(List.of(ReadingBase.of(Base.A, QualityScore.ofBinary((byte) 0b00000000))));

        FastqWriterUtils.writeReadingFragment(writer, fragment, 123);

        String expectedOutput = "@READ_123\n" + "A\n" + "+READ_123\n" + "!\n";
        assertThat(writer.toString(), is(expectedOutput));
    }

    @Test
    public void writeReadingFragmentCanWriteAFragmentWithMultipleBases() {
        ReadingBase readingBase1 = ReadingBase.of(Base.A, QualityScore.ofBinary((byte) 0b00000000));
        ReadingBase readingBase2 = ReadingBase.of(Base.C, QualityScore.ofBinary((byte) 0b00000000));
        ReadingFragment fragment = ReadingFragment.of(List.of(readingBase1, readingBase2));

        FastqWriterUtils.writeReadingFragment(writer, fragment, 123);

        String expectedOutput = "@READ_123\n" + "AC\n" + "+READ_123\n" + "!!\n";
        assertThat(writer.toString(), is(expectedOutput));
    }

    @Test
    public void writeReadingFragmentCorrectlyConvertsTheBaseToTheRightString() {
        ReadingBase readingBaseA = ReadingBase.of(Base.A, QualityScore.ofBinary((byte) 0b00000000));
        ReadingBase readingBaseC = ReadingBase.of(Base.C, QualityScore.ofBinary((byte) 0b00000000));
        ReadingBase readingBaseG = ReadingBase.of(Base.G, QualityScore.ofBinary((byte) 0b00000000));
        ReadingBase readingBaseT = ReadingBase.of(Base.T, QualityScore.ofBinary((byte) 0b00000000));
        ReadingFragment fragment = ReadingFragment.of(List.of(readingBaseA, readingBaseC, readingBaseG, readingBaseT));

        FastqWriterUtils.writeReadingFragment(writer, fragment, 123);

        String expectedOutput = "@READ_123\n" + "ACGT\n" + "+READ_123\n" + "!!!!\n";
        assertThat(writer.toString(), is(expectedOutput));
    }

    @Test
    public void writeReadingBaseCorrectlyConvertsTheScoreToLastSixBits() {
        ReadingBase readingBase0 = ReadingBase.of(Base.A, QualityScore.ofBinary((byte) 0b00000000));
        ReadingBase readingBase1 = ReadingBase.of(Base.A, QualityScore.ofBinary((byte) 0b00000001));
        ReadingBase readingBase8 = ReadingBase.of(Base.A, QualityScore.ofBinary((byte) 0b00001000));
        ReadingBase readingBase60 = ReadingBase.of(Base.A, QualityScore.ofBinary((byte) 0b00111100));
        ReadingFragment fragment = ReadingFragment.of(List.of(readingBase0, readingBase1, readingBase8, readingBase60));

        FastqWriterUtils.writeReadingFragment(writer, fragment, 123);

        String expectedOutput = "@READ_123\n" + "AAAA\n" + "+READ_123\n" + "!\")]\n";
        assertThat(writer.toString(), is(expectedOutput));
    }

    @Test
    public void writeReadingBaseCorrectlyHandlesFullByteOfOnes() {
        ReadingBase readingBaseFull = ReadingBase.of(Base.T, QualityScore.ofBinary((byte) 0b00111111));
        ReadingFragment fragment = ReadingFragment.of(List.of(readingBaseFull));

        FastqWriterUtils.writeReadingFragment(writer, fragment, 123);

        String expectedOutput = "@READ_123\n" + "T\n" + "+READ_123\n" + "`\n";
        assertThat(writer.toString(), is(expectedOutput));
    }
}
