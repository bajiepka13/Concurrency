package org.bajiepka.concurrency.modernjavainaction.chapter7;

import org.junit.Test;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SpliteratosTests {

    @Test
    public void test_01_spliteratorExamples() {

        final String SENTENCE = " Nel mezzo del cammin di nostra vita " +
                "mi ritrovai in una selva oscura" +
                " ché la dritta via era smarrita ";

        System.out.println(String.format("Fount %d words in the sentence.",
                countWordsIteravely(SENTENCE)));

        Stream<Character> charStream = IntStream.range(0, SENTENCE.length()).mapToObj(SENTENCE::charAt);
        WordCounter wordCounter = charStream.reduce(
                new WordCounter(0, true),
                WordCounter::accumulate,
                WordCounter::combine);

        System.out.println("Используя custom collector мы подсчитали количество слов в предложении: " + wordCounter.getCounter());

    }

    public int countWordsIteravely(String line) {
        int counter = 0;
        boolean lastSpace = true;

        for (char c : line.toCharArray()) {
            if (Character.isWhitespace(c)) {
                lastSpace = true;
            } else {
                if (lastSpace) counter++;
                lastSpace = false;
            }
        }
        return counter;
    }

    class WordCounterSpliterator implements Spliterator<Character> {

        private final String string;
        private int currentChar = 0;

        public WordCounterSpliterator(String string) {
            this.string = string;
        }

        @Override
        public boolean tryAdvance(Consumer<? super Character> action) {
            action.accept(string.charAt(currentChar++));
            return currentChar < string.length();
        }

        @Override
        public Spliterator<Character> trySplit() {
            int currentSize = string.length() - currentChar;
            if (currentSize < 10) {
                return null;
            }
        }

        @Override
        public long estimateSize() {
            return 0;
        }

        @Override
        public int characteristics() {
            return 0;
        }
    }

    class WordCounter {

        private final int counter;
        private final boolean lastSpace;

        public WordCounter(int counter, boolean lastSpace) {
            this.counter = counter;
            this.lastSpace = lastSpace;
        }

        public WordCounter accumulate(Character c) {

            if (Character.isWhitespace(c)) {
                return lastSpace ? this : new WordCounter(counter, true);
            } else {
                return lastSpace ? new WordCounter(counter + 1, false) : this;
            }

        }

        public WordCounter combine(WordCounter wordCounter) {
            return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
        }

        public int getCounter() {
            return counter;
        }

    }

}
