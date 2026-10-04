package by.flower.factory;

import by.flower.entity.Flower;
import by.flower.entity.Tulip;
import java.util.Map;

public class TulipFactory extends FlowerFactory {
    @Override
    public Flower createFlower(Map<String, String> params) {
        double price = Double.parseDouble(params.getOrDefault("price", "3.0"));
        int freshness = Integer.parseInt(params.getOrDefault("freshness", "1"));
        double stemLength = Double.parseDouble(params.getOrDefault("stemLength", "40.0"));
        String color = params.getOrDefault("color", "Red");
        return new Tulip(price, freshness, stemLength, color);
    }
}
