package com.javarush.island.voetsky.Model.animals.Herbivores;

import com.javarush.island.voetsky.Model.animals.Animal;
import com.javarush.island.voetsky.Model.plants.Grass;
public class Horse extends Animal {
    public Horse() {
        this(40);
    }

    public Horse(int maxAge) {
        this.eatMap.put(Grass.class, 80);
        this.maxAge = maxAge;
        this.weight = 10;
        this.maxMovementSpeed = 3;
        this.maxNatureCount = 20;
    }
    @Override
    public String toString() {
        return "\uD83D\uDC0E";
    }
}
