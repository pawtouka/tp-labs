package org.example;

import java.util.*;

public class LineUtils {
    public static List<List<Line2D>> groupParallelLines(List<Line2D> lines) {
        if (lines == null || lines.isEmpty()) return Collections.emptyList();

        List<List<Line2D>> groups = new ArrayList<>();

        for (Line2D line : lines) {
            boolean added = false;
            for (List<Line2D> group : groups) {
                if (group.get(0).isParallelTo(line)) {
                    group.add(line);
                    added = true;
                    break;
                }
            }
            if (!added) {
                List<Line2D> newGroup = new ArrayList<>();
                newGroup.add(line);
                groups.add(newGroup);
            }
        }
        return groups;
    }
}
