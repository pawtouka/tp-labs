package by.flower.service;

import by.flower.entity.Accessory;
import by.flower.entity.Bouquet;
import by.flower.entity.Flower;
import by.flower.entity.Rose;
import by.flower.entity.Tulip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BouquetServiceTest {
    private BouquetService service;
    private Bouquet bouquet;

    @BeforeEach
    public void setUp() {
        service = new BouquetService();
        bouquet = new Bouquet();

        bouquet.addFlower(new Rose(10.0, 3, 50.0, true)); // увядает быстрее
        bouquet.addFlower(new Tulip(5.0, 1, 40.0, "Yellow")); // свежий
        bouquet.addAccessory(new Accessory("Лента", 2.5));
    }

    @Test
    public void testCalculateCost() {
        double expected = 10.0 + 5.0 + 2.5;
        assertEquals(expected, service.calculateCost(bouquet), 0.001);
    }

    @Test
    public void testSortByFreshness() {
        service.sortByFreshness(bouquet);
        assertEquals("Тюльпан", bouquet.getFlowers().get(0).getName());
    }

    @Test
    public void testFindFlowersByStemLength() {
        List<Flower> result = service.findFlowersByStemLength(bouquet, 45.0, 55.0);
        assertEquals(1, result.size());
        assertEquals("Роза", result.get(0).getName());
    }
}
