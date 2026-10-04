package by.flower.service;

import by.flower.entity.Accessory;
import by.flower.entity.Bouquet;
import by.flower.entity.Flower;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BouquetService {

    public double calculateCost(Bouquet bouquet) {
        double flowerCost = bouquet.getFlowers().stream().mapToDouble(Flower::getPrice).sum();
        double accessoryCost = bouquet.getAccessories().stream().mapToDouble(Accessory::getPrice).sum();
        return flowerCost + accessoryCost;
    }

    public void sortByFreshness(Bouquet bouquet) {
        bouquet.getFlowers().sort(Comparator.comparingInt(Flower::getFreshness));
    }

    public List<Flower> findFlowersByStemLength(Bouquet bouquet, double min, double max) {
        return bouquet.getFlowers().stream()
                .filter(f -> f.getStemLength() >= min && f.getStemLength() <= max)
                .collect(Collectors.toList());
    }
}
