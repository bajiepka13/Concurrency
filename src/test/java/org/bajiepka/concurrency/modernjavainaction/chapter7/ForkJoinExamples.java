package org.bajiepka.concurrency.modernjavainaction.chapter7;

import org.junit.Test;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

public class ForkJoinExamples {

    @Test
    public void test_01_java7_forkjoin() {

        System.out.println(Runtime.getRuntime().availableProcessors());

        long[] numbers = LongStream.rangeClosed(1, 100).toArray();
        System.out.println(new ForkJoinPool().invoke(new ForkJoinSumCalculator(numbers)));
    }

    class ForkJoinSumCalculator extends RecursiveTask<Long> {

        public static final long THRESHOLD = 10;
        private final long[] numbers;
        private final int start;
        private final int end;

        public ForkJoinSumCalculator(long[] numbers) {
            this(numbers, 0, numbers.length);
        }

        private ForkJoinSumCalculator(long[] numbers, int start, int end) {
            this.numbers = numbers;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {

            int length = end - start;
            if (length <= THRESHOLD) {
                return computeSequentially();
            }

            ForkJoinSumCalculator left = new ForkJoinSumCalculator(numbers, start, start + length / 2);
            left.fork();

            ForkJoinSumCalculator right = new ForkJoinSumCalculator(numbers, start + length / 2, end);

            Long rightResult = right.compute();
            Long leftResult = left.join();

            return leftResult + rightResult;
        }

        private Long computeSequentially() {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += numbers[i];
            }
            return sum;
        }
    }
}
