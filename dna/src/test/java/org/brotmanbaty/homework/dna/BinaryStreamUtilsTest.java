package org.brotmanbaty.homework.dna;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

public class BinaryStreamUtilsTest {

    @Test
    public void readReadingFragmentCanReadZeroBases() {
        InputStream stream = mock(InputStream.class);

        ReadingFragment fragment = BinaryStreamUtils.readReadingFragment(stream, 0);

        verifyNoInteractions(stream);
        assertThat(fragment.getReadingBases(), empty());
    }

    @Test
    public void readReadingFragmentReturnsNullIfEndOfStreamIsReachedImmediatelyWhenMultipleBasesRequested()
            throws IOException {
        InputStream stream = mock(InputStream.class);
        when(stream.read()).thenReturn(-1);

        ReadingFragment fragment = BinaryStreamUtils.readReadingFragment(stream, 1);

        assertNull(fragment);
    }

    @Test
    public void readReadingFragmentThrowsExceptionWhenEndOfStreamIsReachedAfterReadingAtLeastOneBaseButNotAllRequested()
            throws IOException {
        InputStream stream = mock(InputStream.class);
        when(stream.read()).thenReturn(0b01000010, -1);

        assertThrows(IllegalStateException.class, () -> BinaryStreamUtils.readReadingFragment(stream, 2));
    }

    @Test
    public void readReadingFragmentCanReadOneBase() throws IOException {
        InputStream stream = mock(InputStream.class);
        when(stream.read()).thenReturn(0b01000010, -1);
        ReadingBase expectedBase = ReadingBase.of(Base.C, QualityScore.ofBinary((byte) 0b00000010));
        ReadingFragment expectedFragment = ReadingFragment.of(List.of(expectedBase));

        ReadingFragment fragment = BinaryStreamUtils.readReadingFragment(stream, 1);

        assertThat(fragment, is(expectedFragment));
    }

    @Test
    public void readReadingFragmentCanReadMultipleBases() throws IOException {
        InputStream stream = mock(InputStream.class);
        when(stream.read()).thenReturn(0b01000010, 0b10000100, -1);
        ReadingBase expectedBase1 = ReadingBase.of(Base.C, QualityScore.ofBinary((byte) 0b00000010));
        ReadingBase expectedBase2 = ReadingBase.of(Base.G, QualityScore.ofBinary((byte) 0b00000100));
        ReadingFragment expectedFragment = ReadingFragment.of(List.of(expectedBase1, expectedBase2));

        ReadingFragment fragment = BinaryStreamUtils.readReadingFragment(stream, 2);

        assertThat(fragment, is(expectedFragment));
    }

    @Test
    public void readReadingBaseCorrectlyParsesTheBaseFromFirstTwoBits() throws IOException {
        InputStream stream = mock(InputStream.class);
        when(stream.read()).thenReturn(0b00000000, 0b01000001, 0b10001000, 0b11100000);

        assertThat(BinaryStreamUtils.readReadingBase(stream).getBase(), is(Base.A));
        assertThat(BinaryStreamUtils.readReadingBase(stream).getBase(), is(Base.C));
        assertThat(BinaryStreamUtils.readReadingBase(stream).getBase(), is(Base.G));
        assertThat(BinaryStreamUtils.readReadingBase(stream).getBase(), is(Base.T));
    }

    @Test
    public void readReadingBaseCorrectlyParsesTheScoreFromLastSixBits() throws IOException {
        InputStream stream = mock(InputStream.class);
        when(stream.read()).thenReturn(0b00000000, 0b01000001, 0b1000111, 0b11111101);

        assertThat(BinaryStreamUtils.readReadingBase(stream).getQualityScore(),
                is(QualityScore.ofBinary((byte) 0b00000000)));
        assertThat(BinaryStreamUtils.readReadingBase(stream).getQualityScore(),
                is(QualityScore.ofBinary((byte) 0b00000001)));
        assertThat(BinaryStreamUtils.readReadingBase(stream).getQualityScore(),
                is(QualityScore.ofBinary((byte) 0b00000111)));
        assertThat(BinaryStreamUtils.readReadingBase(stream).getQualityScore(),
                is(QualityScore.ofBinary((byte) 0b00111101)));
    }

    @Test
    public void readReadingBaseCorrectlyDifferentiatesBetweenValid255ReadingAndNegativeOneEndOfStream()
            throws IOException {
        InputStream stream = mock(InputStream.class);
        when(stream.read()).thenReturn(0b11111111);

        assertThat(BinaryStreamUtils.readReadingBase(stream),
                is(ReadingBase.of(Base.T, QualityScore.ofBinary((byte) 0b00111111))));
    }

    @Test
    public void readReadingBaseReturnsNullIfAtEndOfStream() throws IOException {
        InputStream stream = mock(InputStream.class);
        when(stream.read()).thenReturn(-1);

        assertNull(BinaryStreamUtils.readReadingBase(stream));
    }

    @Test
    public void writeReadingFragmentCanWriteZeroBases() {
        OutputStream stream = mock(OutputStream.class);

        BinaryStreamUtils.writeReadingFragment(stream, ReadingFragment.of(List.of()));

        verifyNoInteractions(stream);
    }

    @Test
    public void writeReadingFragmentCanWriteOneBase() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ReadingBase expectedBase = ReadingBase.of(Base.C, QualityScore.ofBinary((byte) 0b00000010));
        ReadingFragment expectedFragment = ReadingFragment.of(List.of(expectedBase));

        BinaryStreamUtils.writeReadingFragment(outputStream, expectedFragment);

        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ReadingFragment fragment = BinaryStreamUtils.readReadingFragment(inputStream, 1);
        assertThat(fragment, is(expectedFragment));
    }

    @Test
    public void writeReadingFragmentCanWriteMultipleBases() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ReadingBase expectedBase1 = ReadingBase.of(Base.C, QualityScore.ofBinary((byte) 0b00000010));
        ReadingBase expectedBase2 = ReadingBase.of(Base.G, QualityScore.ofBinary((byte) 0b00000100));
        ReadingFragment expectedFragment = ReadingFragment.of(List.of(expectedBase1, expectedBase2));

        BinaryStreamUtils.writeReadingFragment(outputStream, expectedFragment);

        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ReadingFragment fragment = BinaryStreamUtils.readReadingFragment(inputStream, 2);
        assertThat(fragment, is(expectedFragment));
    }

    @Test
    public void writeReadingBaseCorrectlyConvertsTheBaseToFirstTwoBits() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ReadingBase readingBaseA = ReadingBase.of(Base.A, QualityScore.ofBinary((byte) 0b00000000));
        ReadingBase readingBaseC = ReadingBase.of(Base.C, QualityScore.ofBinary((byte) 0b00000000));
        ReadingBase readingBaseG = ReadingBase.of(Base.G, QualityScore.ofBinary((byte) 0b00000000));
        ReadingBase readingBaseT = ReadingBase.of(Base.T, QualityScore.ofBinary((byte) 0b00000000));

        BinaryStreamUtils.writeReadingBase(outputStream, readingBaseA);
        BinaryStreamUtils.writeReadingBase(outputStream, readingBaseC);
        BinaryStreamUtils.writeReadingBase(outputStream, readingBaseG);
        BinaryStreamUtils.writeReadingBase(outputStream, readingBaseT);

        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        assertThat(BinaryStreamUtils.readReadingBase(inputStream), is(readingBaseA));
        assertThat(BinaryStreamUtils.readReadingBase(inputStream), is(readingBaseC));
        assertThat(BinaryStreamUtils.readReadingBase(inputStream), is(readingBaseG));
        assertThat(BinaryStreamUtils.readReadingBase(inputStream), is(readingBaseT));
    }

    @Test
    public void writeReadingBaseCorrectlyConvertsTheScoreToLastSixBits() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ReadingBase readingBase0 = ReadingBase.of(Base.A, QualityScore.ofBinary((byte) 0b00000000));
        ReadingBase readingBase1 = ReadingBase.of(Base.C, QualityScore.ofBinary((byte) 0b00000001));
        ReadingBase readingBase8 = ReadingBase.of(Base.G, QualityScore.ofBinary((byte) 0b00001000));
        ReadingBase readingBase60 = ReadingBase.of(Base.T, QualityScore.ofBinary((byte) 0b00111100));

        BinaryStreamUtils.writeReadingBase(outputStream, readingBase0);
        BinaryStreamUtils.writeReadingBase(outputStream, readingBase1);
        BinaryStreamUtils.writeReadingBase(outputStream, readingBase8);
        BinaryStreamUtils.writeReadingBase(outputStream, readingBase60);

        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        assertThat(BinaryStreamUtils.readReadingBase(inputStream), is(readingBase0));
        assertThat(BinaryStreamUtils.readReadingBase(inputStream), is(readingBase1));
        assertThat(BinaryStreamUtils.readReadingBase(inputStream), is(readingBase8));
        assertThat(BinaryStreamUtils.readReadingBase(inputStream), is(readingBase60));
    }

    @Test
    public void writeReadingBaseCorrectlyHandlesFullByteOfOnes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ReadingBase readingBaseFull = ReadingBase.of(Base.T, QualityScore.ofBinary((byte) 0b00111111));

        BinaryStreamUtils.writeReadingBase(outputStream, readingBaseFull);

        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        assertThat(BinaryStreamUtils.readReadingBase(inputStream), is(readingBaseFull));
    }
}
