package org.bajiepka.concurrency.modernjavainaction.chapter12;

import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;

import static org.junit.Assert.assertTrue;

public class DateTimeApiTests {

    @Test
    public void test_01_dateTests() {

        LocalDate date = LocalDate.now();
        LocalDate yesterday = LocalDate.of(2019, 8, 7);
        int yesterdayYear = yesterday.getYear();

        assertTrue("Сейчас 2019 год, как и записано в дате", yesterdayYear == 2019);
        assertTrue("Сейчас 2019 год, как и записано в дате", yesterday.get(ChronoField.YEAR) == 2019);
        assertTrue("Вчера была среда", DayOfWeek.WEDNESDAY.equals(yesterday.getDayOfWeek()));
        assertTrue("В августе 31 день", yesterday.lengthOfMonth() == 31);

    }

    @Test
    public void test_02_timeTests() {

        LocalTime time = LocalTime.of(12, 00, 59);

    }

    @Test
    public void test_03_dateTimeTests() {

        LocalDateTime dateTime = LocalDateTime.of(2019, Month.DECEMBER, 30, 12, 45, 31);
        LocalDate date = dateTime.toLocalDate();
    }

    @Test
    public void test_04_instantTests() {

        System.out.println(Instant.ofEpochSecond(0));
        System.out.println(Instant.ofEpochSecond(100));

        System.out.println(Instant.now().get(ChronoField.DAY_OF_MONTH));

    }

    @Test
    public void test_05_durationTests() {

        LocalDate yesterday = LocalDate.of(2019, Month.AUGUST, 07);
        LocalDateTime start = yesterday.atTime(8, 0, 0);
        LocalDateTime finish = yesterday.atTime(18, 11, 29);
        Duration workTime = Duration.between(start, finish);

        System.out.println(workTime.toString());

        Period vacation = Period.between(
                LocalDate.of(2019, 9, 1),
                LocalDate.of(2019, 9, 30));

        System.out.println(vacation.toString());

        LocalDateTime now = LocalDateTime.from(start);

    }

    @Test
    public void test_06_temporalAdjustmentsTests() {

        LocalDate augustFirstDay = LocalDate.of(2019, 8, 1);
        LocalDate nextMondayAfter = augustFirstDay.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        System.out.println(nextMondayAfter);

        LocalDate lastDayOfAugust = nextMondayAfter.with(TemporalAdjusters.lastDayOfMonth());
        System.out.println(lastDayOfAugust);

        LocalDate firstOfSeptember = lastDayOfAugust.plusDays(1);
        System.out.println(firstOfSeptember);

    }
}
