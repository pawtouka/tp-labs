package by.flower.factory;

import by.flower.entity.Flower;
import by.flower.entity.Rose;
import java.util.Map;

public class RoseFactory extends FlowerFactory {
    @Override
    public Flower createFlower(Map<String, String> params) {
        double price = Double.parseDouble(params.getOrDefault("price", "5.0"));
        int freshness = Integer.parseInt(params.getOrDefault("freshness", "1"));
        double stemLength = Double.parseDouble(params.getOrDefault("stemLength", "50.0"));
        boolean hasThorns = Boolean.parseBoolean(params.getOrDefault("hasThorns", "true"));
        return new Rose(price, freshness, stemLength, hasThorns);
    }
}
