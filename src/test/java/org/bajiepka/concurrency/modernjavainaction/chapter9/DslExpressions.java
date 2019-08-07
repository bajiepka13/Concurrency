package org.bajiepka.concurrency.modernjavainaction.chapter9;

import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DslExpressions {

    @Test
    public void test_01_dsl() {

        List<Car> cars = Arrays.asList(
                new Car(Brand.TOYOTA, Color.BLACK),
                new Car(Brand.TOYOTA, Color.ORANGE),
                new Car(Brand.HONDA, Color.PINK),
                new Car(Brand.HONDA, Color.ORANGE),
                new Car(Brand.HYUNDAI, Color.GRAY),
                new Car(Brand.HYUNDAI, Color.CYAN),
                new Car(Brand.KIA, Color.CYAN),
                new Car(Brand.KIA, Color.PINK)
        );

        Map<Color, Map<Brand, List<Car>>> carsByColorAndBrand =
                cars.stream().collect(
                        Collectors.groupingBy(
                                Car::getColor,
                                Collectors.groupingBy(
                                        Car::getModel,
                                        Collectors.toList())));

        System.out.println(carsByColorAndBrand);

    }

    @Test
    public void test_02_dsl_using_methods_chaining() {

        Order order = new MethodChainingOrderBuilder("McDonalds")
                .buy(100)
                .stock("dollar")
                .on("Central market")
                .at(1000.0)
                .sell(50)
                .stock("dollar")
                .on("e-shop")
                .at(1200)
                .end();

        System.out.println(order);

    }

    public void test_03_dsl_using_functions() {

    }

    //region Simple DSL

    enum Brand {
        TOYOTA, HONDA, HYUNDAI, KIA
    }

    enum Type {BUY, SELL}

    interface Vehicle {
        void rondondon();
    }

    //endregion

    //region Method-chaining-DSL

    class Car implements Vehicle {

        private Brand model;
        private Color color;

        public Car(Brand model, Color color) {
            this.model = model;
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public Brand getModel() {
            return model;
        }

        @Override
        public void rondondon() {
            System.out.println("Ron-don-don");
        }
    }

    class MethodChainingOrderBuilder {
        public final Order order = new Order();

        private MethodChainingOrderBuilder(String customer) {
            order.setCustomer(customer);
        }

        public MethodChainingOrderBuilder forCustomer(String customer) {
            return new MethodChainingOrderBuilder(customer);
        }

        public TradeBuilder buy(int quantity) {
            return new TradeBuilder(this, Type.BUY, quantity);
        }

        public TradeBuilder sell(int quantity) {
            return new TradeBuilder(this, Type.SELL, quantity);
        }

        public MethodChainingOrderBuilder addTrade(Trade trade) {
            order.addTrade(trade);
            return this;
        }

        public Order end() {
            return order;
        }
    }

    public class TradeBuilder {
        public final Trade trade = new Trade();
        private final MethodChainingOrderBuilder builder;

        private TradeBuilder(MethodChainingOrderBuilder builder,
                             Type type, int quantity) {
            this.builder = builder;
            trade.setType(type);
            trade.setQuantity(quantity);
        }

        public StockBuilder stock(String symbol) {
            return new StockBuilder(builder, trade, symbol);
        }
    }

    public class StockBuilder {
        private final MethodChainingOrderBuilder builder;
        private final Trade trade;
        private final Stock stock = new Stock();

        private StockBuilder(MethodChainingOrderBuilder builder,
                             Trade trade, String symbol) {
            this.builder = builder;
            this.trade = trade;
            stock.setSymbol(symbol);
        }

        public TradeBuilderWithStock on(String market) {
            stock.setMarket(market);
            trade.setStock(stock);
            return new TradeBuilderWithStock(builder, trade);
        }
    }

    public class TradeBuilderWithStock {
        private final MethodChainingOrderBuilder builder;
        private final Trade trade;

        public TradeBuilderWithStock(MethodChainingOrderBuilder builder,
                                     Trade trade) {
            this.builder = builder;
            this.trade = trade;
        }

        public MethodChainingOrderBuilder at(double price) {
            trade.setPrice(price);
            return builder.addTrade(trade);
        }
    }

    public class Stock {
        private String symbol;
        private String market;

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getMarket() {
            return market;
        }

        public void setMarket(String market) {
            this.market = market;
        }
    }

    class Trade {

        private Type type;
        private Stock stock;
        private int quantity;
        private double price;

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public Stock getStock() {
            return stock;
        }

        public void setStock(Stock stock) {
            this.stock = stock;
        }

        public double getValue() {
            return quantity * price;
        }
    }

    public class Order {

        private String customer;
        private List<Trade> trades = new ArrayList<>();

        public void addTrade(Trade trade) {
            trades.add(trade);
        }

        public String getCustomer() {
            return customer;
        }

        public void setCustomer(String customer) {
            this.customer = customer;
        }

        public double getValue() {
            return trades.stream().mapToDouble(Trade::getValue).sum();
        }
    }

    //endregion

}
