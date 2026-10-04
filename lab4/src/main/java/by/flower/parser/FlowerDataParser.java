package by.flower.parser;

import by.flower.entity.Flower;
import by.flower.factory.RoseFactory;
import by.flower.factory.TulipFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FlowerDataParser {
    private static final Logger logger = LogManager.getLogger(FlowerDataParser.class);

    public List<Flower> parseFlowersFromFile(String filePath) {
        List<Flower> flowers = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                try {
                    Flower flower = parseLine(line);
                    if (flower != null) flowers.add(flower);
                } catch (Exception e) {
                    logger.error("Критическая ошибка строки: {}. Строка пропущена.", line, e);
                }
            }
        } catch (IOException e) {
            logger.fatal("Не удалось прочитать файл данных!", e);
        }
        return flowers;
    }

    private Flower parseLine(String line) {
        String[] parts = line.split(";");
        if (parts.length < 2) {
            logger.warn("Некорректный формат строки: {}. Игнорируется.", line);
            return null;
        }

        String type = parts[0].trim().toLowerCase();
        Map<String, String> params = new HashMap<>();

        for (int i = 1; i < parts.length; i++) {
            String[] pair = parts[i].split("=");
            if (pair.length == 2) {
                params.put(pair[0].trim(), pair[1].trim());
            }
        }

        validateAndFixParams(params);

        switch (type) {
            case "rose":
                return new RoseFactory().createFlower(params);
            case "tulip":
                return new TulipFactory().createFlower(params);
            default:
                logger.warn("Неизвестный тип цветка: {}. Игнорируется.", type);
                return null;
        }
    }

    private void validateAndFixParams(Map<String, String> params) {
        try {
            double price = Double.parseDouble(params.get("price"));
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            logger.warn("Некорректная цена. Установлено дефолтное значение 3.5");
            params.put("price", "3.5");
        }

        try {
            int freshness = Integer.parseInt(params.get("freshness"));
            if (freshness < 1 || freshness > 10) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            logger.warn("Некорректная свежесть. Установлено дефолтное значение 1 (Свежий)");
            params.put("freshness", "1");
        }
    }
}
