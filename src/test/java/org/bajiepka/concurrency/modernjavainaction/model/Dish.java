package org.bajiepka.concurrency.modernjavainaction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Dish implements Soldable {

    String name;
    Integer weight;
    DishType type;

    public Boolean isVegeterian() {
        return this.getType().equals(DishType.VEGAN);
    }

    /**
     * простая ерализация Builder с помощью внутреннего класса
     */

    public Integer getWeight() {
        return this.weight;
    }

    @Override
    public String toString() {
        return name + "(" + weight + ")";
    }
}
