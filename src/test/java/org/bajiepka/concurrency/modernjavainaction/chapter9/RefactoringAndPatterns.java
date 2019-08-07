package org.bajiepka.concurrency.modernjavainaction.chapter9;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class RefactoringAndPatterns {

    final static Map<String, Supplier<Bond>> map = new HashMap<>();

    static {
//        map.put("bond", Bond::new);
    }

    @Test
    public void test_01_lambda_insteadOf_strategyPattern() {

        String numericExpression = "aaaa";
        String lowercaseExpression = "abcd";

        /*  Классическое решение  */
        Validator numericValidator = new Validator(new IsNumeric());
        boolean isNumberValue = numericValidator.validate(numericExpression);
        System.out.println("(Классический валидатор выражений (число)) " + isNumberValue);

        Validator lowercaseValidator = new Validator(new IsAllLowercase());
        boolean isLowercaseValue = lowercaseValidator.validate(lowercaseExpression);
        System.out.println("(Классический валидатор выражений (строка малыми буквами)) " + isLowercaseValue);

        /*  Решение с использованием лямбда-выражений  */
        System.out.println("(Лямбда-валидатор выражений (число)) " +
                new Validator(s -> s.matches("\\d+")).validate(numericExpression));

        System.out.println("(Лямбда-валидатор выражений (строка малыми буквами)) " +
                new Validator(s -> s.matches("[a-z]+")).validate(lowercaseExpression));


    }

    @Test
    public void test_02_lambda_insteadOf_templatePattern() {

        /*OnlineBanking banking = new OnlineBankingLambda();
        banking.processCustomer("Новый покупатель", c -> System.out.println("действие"));*/

    }

    @Test
    public void test_03_lambda_insteadOf_observerPattern() {

        Feed f = new Feed();
        f.registerObserver(new NYTimes());
        f.registerObserver(new Guardian());
        f.registerObserver(new LeMonde());

        f.registerObserver((String tweet) -> {
            if (tweet != null && tweet.contains("Java")) {
                System.out.println("Custom observer Java! " + tweet);
            }
        });

        f.notifyObservers("The queen said her favourite book is Modern Java in Action!");

    }

    @Test
    public void test_04_lambda_insteadOf_chainOfResponsibilityPattern() {

        /*  Классическое решение  */
        ProcessingObject<String> p1 = new HeaderTextProcessing();
        ProcessingObject<String> p2 = new SpellCheckerProcessing();
        p1.setSuccessor(p2);

        String result = p1.handle("Aren't labdas really sexy?!!");
        System.out.println(result);

        /*  С помощью лямбда-выражений  */
        UnaryOperator<String> headerProcessing = (String text) -> "From Raoul, Mario and Alan: " + text;
        UnaryOperator<String> spellingProcessing = (String text) -> text.replaceAll("labda", "lambda");

        Function<String, String> pipeline = headerProcessing.andThen(spellingProcessing);
        pipeline.apply("Aren't labdas really sexy?!!");

    }

    @Test
    public void test_05_lambda_insteadOf_factoryPattern() {

    }

    //region  Шаблон "Стратегия" Strategy pattern ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public interface ValidationStrategy {
        boolean execute(String strategy);
    }

    interface Observer {
        void notify(String tweet);
    }

    interface Subject {
        void registerObserver(Observer o);

        void notifyObservers(String tweet);
    }

    public class IsAllLowercase implements ValidationStrategy {

        @Override
        public boolean execute(String strategy) {
            return strategy.matches("[a-z]+");
        }
    }

    //endregion

    //region  Шаблон "Шаблон" Template pattern ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public class IsNumeric implements ValidationStrategy {

        @Override
        public boolean execute(String strategy) {
            return strategy.matches("\\d+");
        }
    }

    public class Validator {

        private final ValidationStrategy strategy;

        public Validator(ValidationStrategy strategy) {
            this.strategy = strategy;
        }

        public boolean validate(String string) {
            return strategy.execute(string);
        }
    }

    abstract class OnlineBankingLambda {

        public void processCustomer(int id) {
            Customer customer = new Customer();
            makeCustomerHappy(customer);
        }

        void processCustomer(String name, Consumer<Customer> makeCustomerHappy) {
            Customer customer = new Customer();
            makeCustomerHappy.accept(customer);
        }

        protected abstract void makeCustomerHappy(Customer customer);

    }

    //endregion

    //region  Шаблон "Обозреватель" Observer pattern ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    abstract class OnlineBanking {

        public void processCustomer(int id) {
            Customer customer = new Customer();
            makeCustomerHappy(customer);
        }

        protected abstract void makeCustomerHappy(Customer customer);
    }

    class Customer {
        private String name;
    }

    class NYTimes implements Observer {

        @Override
        public void notify(String tweet) {
            if (tweet != null && tweet.contains("money")) {
                System.out.println("Breaking news in NY! " + tweet);
            }
        }
    }

    class Guardian implements Observer {

        @Override
        public void notify(String tweet) {
            if (tweet != null && tweet.contains("queen")) {
                System.out.println("Yet more news from London! " + tweet);
            }
        }
    }

    class LeMonde implements Observer {

        @Override
        public void notify(String tweet) {
            if (tweet != null && tweet.contains("wine")) {
                System.out.println("Today cheese, wine and news! " + tweet);
            }
        }
    }

    class Feed implements Subject {

        private final List<Observer> observers = new ArrayList<>();

        @Override
        public void registerObserver(Observer o) {
            this.observers.add(o);
        }

        @Override
        public void notifyObservers(String tweet) {
            observers.forEach(o -> o.notify(tweet));
        }
    }

    //endregion~~~~~~~~~

    //region  Шаблон "Цепочка ответственности" Chain of responsibility pattern ~~~~~~~~~~~

    interface Product {

    }

    abstract class ProcessingObject<T> {

        protected ProcessingObject<T> successor;

        public void setSuccessor(ProcessingObject<T> successor) {
            this.successor = successor;
        }

        public T handle(T input) {
            T r = handleWork(input);
            if (successor != null) {
                return successor.handle(r);
            }
            return r;
        }

        abstract protected T handleWork(T input);

    }

    class HeaderTextProcessing extends ProcessingObject<String> {

        @Override
        protected String handleWork(String input) {
            return "From Raoul, Mario and Alan: " + input;
        }
    }

    //endregion

    //region  Шаблон "Фабрика" Factory pattern ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    class SpellCheckerProcessing extends ProcessingObject<String> {

        @Override
        protected String handleWork(String input) {
            return input.replaceAll("labda", "lambda");
        }
    }

    class Bond implements Product {
    }

    class Bill {
    }
    //endregion

}


