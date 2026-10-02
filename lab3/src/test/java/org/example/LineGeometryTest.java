package org.example;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LineGeometryTest {

    @Test
    public void testIntersectionWithAxes() {
        Line2D line = new Line2D(RationalFraction.of(2), RationalFraction.of(-3), RationalFraction.of(6));

        Point2D ox = line.intersectWithOX();
        assertNotNull(ox);
        assertEquals(new RationalFraction(-3, 1), ox.x());
        assertEquals(RationalFraction.of(0), ox.y());

        Point2D oy = line.intersectWithOY();
        assertNotNull(oy);
        assertEquals(RationalFraction.of(0), oy.x());
        assertEquals(new RationalFraction(2, 1), oy.y());
    }

    @Test
    public void testLinesIntersection() {
        Line2D line1 = new Line2D(RationalFraction.of(1), RationalFraction.of(1), RationalFraction.of(-2));

        Line2D line2 = new Line2D(RationalFraction.of(1), RationalFraction.of(-1), RationalFraction.of(0));

        Point2D intersection = line1.intersectWith(line2);
        assertNotNull(intersection);
        assertEquals(RationalFraction.of(1), intersection.x());
        assertEquals(RationalFraction.of(1), intersection.y());
    }

    @Test
    public void testParallelLinesIntersection() {
        Line2D line1 = new Line2D(RationalFraction.of(1), RationalFraction.of(1), RationalFraction.of(0));
        Line2D line2 = new Line2D(RationalFraction.of(1), RationalFraction.of(1), RationalFraction.of(-2));

        assertTrue(line1.isParallelTo(line2));
        assertNull(line1.intersectWith(line2));
    }

    @Test
    public void testGroupingParallelLines() {
        Line2D l1 = new Line2D(RationalFraction.of(1), RationalFraction.of(1), RationalFraction.of(5));
        Line2D l2 = new Line2D(RationalFraction.of(2), RationalFraction.of(2), RationalFraction.of(1));

        Line2D l3 = new Line2D(RationalFraction.of(2), RationalFraction.of(-1), RationalFraction.of(0));
        Line2D l4 = new Line2D(RationalFraction.of(-4), RationalFraction.of(2), RationalFraction.of(3));

        Line2D l5 = new Line2D(RationalFraction.of(1), RationalFraction.of(0), RationalFraction.of(-3));

        List<Line2D> list = List.of(l1, l2, l3, l4, l5);
        List<List<Line2D>> groups = LineUtils.groupParallelLines(list);

        assertEquals(3, groups.size());
    }
}
