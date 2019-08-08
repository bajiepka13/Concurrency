package org.bajiepka.concurrency.modernjavainaction.chapter11;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class OptionalTests {

    private final static Map<String, String> values = new HashMap<>();

    static {
        values.putIfAbsent("one", String.valueOf(1));
        values.putIfAbsent("two", String.valueOf(2));
        values.putIfAbsent("three", String.valueOf(3));
        values.putIfAbsent("four", String.valueOf(4));
        values.putIfAbsent("five", String.valueOf(5));
    }

    @Test
    public void test_01_optional() {

        //region Simple student optional

        Student nonameStudent = new Student(null);
        System.out.println(nonameStudent.getName());

        Student namedStudent = new Student("Валерчик");
        System.out.println(namedStudent.getName());

        //endregion

        //region Chained optional test

        /*  Полноценный chaining */
        Insurance insurance = new Insurance("1111-77261-2291");
        Car car = new Car(insurance);
        Person person = new Person(car);

        Optional<Person> optPerson = Optional.of(person);
        String insuranceNumber = optPerson
                .flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getNumber)
                .orElse("У этого владельца нет страхового свидетельства.");

        String message = String.format("Номер страхового свидетельства: %s", insuranceNumber);
        System.out.println(message);

        /*  Testing */
        assertEquals("У транспортного средства есть страховое свидетельство",
                "1111-77261-2291",
                insuranceNumber);

        /*  Неполноценный chaining */
        Car insurancelessCar = new Car(null);
        Person anotherPerson = new Person(insurancelessCar);
        Optional<Person> optAnotherPerson = Optional.of(anotherPerson);

        String nonexistingInsuranceNumber = optAnotherPerson
                .flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getNumber)
                .orElse("У этого владельца нет страхового свидетельства.");

        message = String.format("Номер страхового свидетельства: %s", nonexistingInsuranceNumber);
        System.out.println(message);

        /*  Testing */
        assertEquals("У транспортного средства нету страхового свидетельства",
                "У этого владельца нет страхового свидетельства.",
                nonexistingInsuranceNumber);

        //endregion

        //region Chained stream optional test
        Insurance insurance100 = new Insurance("100-228381812");
        Insurance insurance200 = new Insurance("200-747263627");
        Insurance insurance300 = new Insurance("300-947367271");

        Car firstCar = new Car(insurance100);
        Car secondCar = new Car(insurance200);
        Car thirdCar = new Car(insurance300);

        Person firstPerson = new Person(firstCar);
        Person secondPerson = new Person(secondCar);
        Person thirdPerson = new Person(thirdCar);

        List<Person> persons = Arrays.asList(firstPerson, secondPerson, thirdPerson);

        Set<String> insurances = persons.stream()
                .map(Person::getCar)
                .map(oCar -> oCar.flatMap(Car::getInsurance))
                .map(oIns -> oIns.map(Insurance::getNumber))
                .flatMap(Optional::stream)
                .sorted(new StringComparator())
                .peek(System.out::println)
                .limit(5)
                .collect(Collectors.toSet());

        Map<String, Optional<Insurance>> insuranceMap = new HashMap<>();
        insuranceMap.putIfAbsent("oxford", Optional.of(insurance100));
        insuranceMap.putIfAbsent("cambridge", Optional.of(insurance200));
        insuranceMap.putIfAbsent("sussex", Optional.of(insurance300));

        Optional<Insurance> oxfordInsurance = insuranceMap.get("oxford");
        oxfordInsurance.filter(f -> f.getNumber().equals("100-228381812")).ifPresent(s -> System.out.println(s));

        //endregion

    }

    @Test
    public void test_02_optionalOfNullables() {
        System.out.println(OptionalUtils.readNumber("three"));
    }

    static class OptionalUtils {
        public static int readNumber(String number) {
            return Optional.ofNullable(values.get(number))
                    .map(Integer::parseInt)
                    .filter(n -> n < 4)
                    .orElse(0);
        }
    }

    class StringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {

            int first = Integer.parseInt(o1.substring(5, 13));
            int second = Integer.parseInt(o2.substring(5, 13));

            return second - first;
        }
    }

    class Student {

        private Optional<String> name;

        public Student(String name) {
            this.name = Optional.ofNullable(name);
        }

        public String getName() {
            return this.name.orElse("noname");
        }
    }

    class Person {
        private Car car;
        private int age;

        public Person(Car car) {
            this.car = car;
        }

        public Person(Car car, int age) {
            this.car = car;
            this.age = age;
        }

        public int getAge() {
            return age;
        }

        public Optional<Car> getCar() {
            return Optional.ofNullable(car);
        }
    }

    class Car {
        private Insurance insurance;

        public Car(Insurance insurance) {
            this.insurance = insurance;
        }

        public Optional<Insurance> getInsurance() {
            return Optional.ofNullable(insurance);
        }
    }

    class Insurance {
        private String number;

        public Insurance(String number) {
            this.number = number;
        }

        public String getNumber() {
            return number;
        }
    }

}
