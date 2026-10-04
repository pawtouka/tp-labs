package by.flower.factory;

import by.flower.entity.Flower;
import java.util.Map;

public abstract class FlowerFactory {
    public abstract Flower createFlower(Map<String, String> params);
}
