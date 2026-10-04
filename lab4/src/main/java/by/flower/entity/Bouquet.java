package by.flower.entity;

import java.util.ArrayList;
import java.util.List;

public class Bouquet {
    private List<Flower> flowers = new ArrayList<>();
    private List<Accessory> accessories = new ArrayList<>();

    public void addFlower(Flower flower) { flowers.add(flower); }
    public void addAccessory(Accessory accessory) { accessories.add(accessory); }
    public List<Flower> getFlowers() { return flowers; }
    public List<Accessory> getAccessories() { return accessories; }
}
