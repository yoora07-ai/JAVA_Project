import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import org.junit.Test;

import components.sortingmachine.SortingMachine;

/**
 * JUnit test fixture for {@code SortingMachine<String>}'s constructor and
 * kernel methods.
 *
 * @author Yoora Choi
 *
 */
public abstract class SortingMachineTest {

    /**
     * Invokes the appropriate {@code SortingMachine} constructor for the
     * implementation under test and returns the result.
     *
     * @param order
     *            the {@code Comparator} defining the order for {@code String}
     * @return the new {@code SortingMachine}
     * @requires IS_TOTAL_PREORDER([relation computed by order.compare method])
     * @ensures constructorTest = (true, order, {})
     */
    protected abstract SortingMachine<String> constructorTest(Comparator<String> order);

    /**
     * Invokes the appropriate {@code SortingMachine} constructor for the
     * reference implementation and returns the result.
     *
     * @param order
     *            the {@code Comparator} defining the order for {@code String}
     * @return the new {@code SortingMachine}
     * @requires IS_TOTAL_PREORDER([relation computed by order.compare method])
     * @ensures constructorRef = (true, order, {})
     */
    protected abstract SortingMachine<String> constructorRef(Comparator<String> order);

    /**
     *
     * Creates and returns a {@code SortingMachine<String>} of the
     * implementation under test type with the given entries and mode.
     *
     * @param order
     *            the {@code Comparator} defining the order for {@code String}
     * @param insertionMode
     *            flag indicating the machine mode
     * @param args
     *            the entries for the {@code SortingMachine}
     * @return the constructed {@code SortingMachine}
     * @requires IS_TOTAL_PREORDER([relation computed by order.compare method])
     * @ensures <pre>
     * createFromArgsTest = (insertionMode, order, [multiset of entries in args])
     * </pre>
     */
    private SortingMachine<String> createFromArgsTest(Comparator<String> order,
            boolean insertionMode, String... args) {
        SortingMachine<String> sm = this.constructorTest(order);
        for (int i = 0; i < args.length; i++) {
            sm.add(args[i]);
        }
        if (!insertionMode) {
            sm.changeToExtractionMode();
        }
        return sm;
    }

    /**
     *
     * Creates and returns a {@code SortingMachine<String>} of the reference
     * implementation type with the given entries and mode.
     *
     * @param order
     *            the {@code Comparator} defining the order for {@code String}
     * @param insertionMode
     *            flag indicating the machine mode
     * @param args
     *            the entries for the {@code SortingMachine}
     * @return the constructed {@code SortingMachine}
     * @requires IS_TOTAL_PREORDER([relation computed by order.compare method])
     * @ensures <pre>
     * createFromArgsRef = (insertionMode, order, [multiset of entries in args])
     * </pre>
     */
    private SortingMachine<String> createFromArgsRef(Comparator<String> order,
            boolean insertionMode, String... args) {
        SortingMachine<String> sm = this.constructorRef(order);
        for (int i = 0; i < args.length; i++) {
            sm.add(args[i]);
        }
        if (!insertionMode) {
            sm.changeToExtractionMode();
        }
        return sm;
    }

    /**
     * Comparator<String> implementation to be used in all test cases. Compare
     * {@code String}s in lexicographic order.
     */
    private static class StringLT implements Comparator<String> {

        @Override
        public int compare(String s1, String s2) {
            return s1.compareToIgnoreCase(s2);
        }

    }

    /**
     * Comparator instance to be used in all test cases.
     */
    private static final StringLT ORDER = new StringLT();

    /*
     * Sample test cases.
     */

    @Test
    public final void testConstructor() {
        SortingMachine<String> m = this.constructorTest(ORDER);
        SortingMachine<String> mExpected = this.constructorRef(ORDER);
        assertEquals(mExpected, m);
    }

    // TODO - add test cases for add, changeToExtractionMode, removeFirst,
    // isInInsertionMode, order, and size

    /*
     * Test cases for add : add an empty set to an element.
     */
    @Test
    public final void testAddEmpty() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true);
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true, "apple");
        m.add("apple");
        assertEquals(mExpected, m);
    }

    /*
     * Test cases for add : add an empty set to multiple elements.
     */
    @Test
    public final void testAddEmptyMultiple() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true);
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true, "apple",
                "banana", "orange");

        m.add("apple");
        m.add("banana");
        m.add("orange");

        assertEquals(mExpected, m);
    }

    /*
     * Test cases for add : add non empty set to an element.
     */
    @Test
    public final void testAddNonEmpty() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "1", "2", "3");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true, "1", "2",
                "3", "4");
        m.add("4");

        assertEquals(mExpected, m);

    }

    /*
     * Test cases for add : add non empty set to multiple elements.
     */
    @Test
    public final void testAddNonEmptyMultiple() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "1", "2", "3");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true, "1", "2",
                "3", "4", "5", "6");

        m.add("4");
        m.add("5");
        m.add("6");

        assertEquals(mExpected, m);

    }

    /*
     * Test cases for changeToExtractionMode : on non-empty set with single
     * element.
     */
    @Test
    public final void testChangeToExtractionModeSingle() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "green");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, false, "green");

        m.changeToExtractionMode();

        assertEquals(mExpected, m);

    }

    /*
     * Test cases for changeToExtractionMode : on non-empty set with multiple
     * elements.
     */
    @Test
    public final void testChangeToExtractionModeMultiple() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "green", "red");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, false, "green",
                "red");

        m.changeToExtractionMode();

        assertEquals(mExpected, m);

    }

    /*
     * Test cases for removeFirst : with single element resulting the empty set.
     */
    @Test
    public final void testRemoveFirstSingle() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false, "a");
        SortingMachine<String> mExpected = this.createFromArgsTest(ORDER, false, "a");

        assertEquals(mExpected.removeFirst(), m.removeFirst());
        assertEquals(mExpected, m);
    }

    /*
     * Test cases for removeFirst : with multiple elements resulting the empty
     * set.
     */
    @Test
    public final void testRemoveFirstMultiple() {

        SortingMachine<String> m = this.createFromArgsTest(ORDER, false, "a", "b", "c");
        SortingMachine<String> mExpected = this.createFromArgsTest(ORDER, false, "a", "b",
                "c");

        assertEquals(mExpected.removeFirst(), m.removeFirst());
        assertEquals(mExpected.removeFirst(), m.removeFirst());
        assertEquals(mExpected.removeFirst(), m.removeFirst());

        assertEquals(mExpected, m);

    }

// Test

    /**
     * Testing SortingMachine isInInsertionMode() in extractionMode and empty.
     */
    @Test
    public final void testIsInsertionModeEmptyFalse() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false);
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, false);
        assertEquals(mExpected.isInInsertionMode(), m.isInInsertionMode());
        assertEquals(mExpected, m);
    }

    /**
     * Testing SortingMachine isInInsertionMode() in extractionMode and
     * non-empty.
     */
    @Test
    public final void testIsInsertionModeNonEmptyFalse() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false, "b", "d", "c",
                "a", "g", "e", "y");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, false, "b", "d",
                "c", "a", "g", "e", "y");
        assertEquals(mExpected.isInInsertionMode(), m.isInInsertionMode());
        assertEquals(mExpected, m);
    }

    /**
     * Testing SortingMachine isInInsertionMode() in insertionMode and empty.
     */
    @Test
    public final void testIsInsertionModeEmptyTrue() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true);
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true);
        assertEquals(mExpected.isInInsertionMode(), m.isInInsertionMode());
        assertEquals(mExpected, m);
    }

    /**
     * Testing SortingMachine isInInsertionMode() in insertionMode and
     * non-empty.
     */
    @Test
    public final void testIsInsertionModeNonEmptyTrue() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "b", "d", "c",
                "a", "g", "e", "y");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true, "b", "d",
                "c", "a", "g", "e", "y");
        assertEquals(mExpected.isInInsertionMode(), m.isInInsertionMode());
        assertEquals(mExpected, m);
    }

    /**
     * Testing SortingMachine isInInsertionMode() with test not in insertionMode
     * and reference in insertionMode and assert the opposite values are true.
     */
    @Test
    public final void testIsInInsertionModeNonEmptyOpposites() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false, "b", "d", "c",
                "a", "g", "e", "y");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true, "b", "d",
                "c", "a", "g", "e", "y");
        assertEquals(mExpected.isInInsertionMode(), !m.isInInsertionMode());
    }

    /**
     * Testing SortingMachine isInInsertionMode() with test not in insertionMode
     * and reference in insertionMode and assert the opposite values are true.
     */
    @Test
    public final void testIsInInsertionModeEmptyOpposites() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false);
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true);
        assertEquals(mExpected.isInInsertionMode(), !m.isInInsertionMode());
    }

    /**
     * Testing SortingMachine isInInsertionMode() with test not in insertionMode
     * and reference in insertionMode with one empty and the other non-empty and
     * assert the opposite values are true.
     */
    @Test
    public final void testIsInInsertionModeOpposites() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false);
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true, "b", "d",
                "c", "a", "g", "e", "y");
        assertEquals(mExpected.isInInsertionMode(), !m.isInInsertionMode());
    }

    /**
     * Testing SortingMachine comparator return order() in insertionMode and
     * empty.
     */
    @Test
    public final void testOrderEmptyTrue() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true);
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true);
        assertEquals(mExpected.order(), m.order());
        assertEquals(mExpected, m);
    }

    /**
     * Testing SortingMachine comparator return order() in insertionMode and
     * non-empty.
     */
    @Test
    public final void testOrderNonEmptyTrue() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "b", "d", "c",
                "a", "g", "e", "y");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true, "b", "d",
                "c", "a", "g", "e", "y");
        assertEquals(mExpected.order(), m.order());
        assertEquals(mExpected, m);
    }

    /**
     * Testing SortingMachine comparator return order() in extractionMode and
     * empty.
     */
    @Test
    public final void testOrderEmptyFalse() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false);
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, false);
        assertEquals(mExpected.order(), m.order());
        assertEquals(mExpected, m);
    }

    /**
     * Testing SortingMachine comparator return order() in extractionMode and
     * non-empty.
     */
    @Test
    public final void testOrderNonEmptyFalse() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false, "b", "d", "c",
                "a", "g", "e", "y");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, false, "b", "d",
                "c", "a", "g", "e", "y");
        assertEquals(mExpected.order(), m.order());
        assertEquals(mExpected, m);
    }

    /**
     * Testing SortingMachine with zero size and in insertionMode.
     */
    @Test
    public final void testSizeEmptyTrue() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true);
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true);
        assertEquals(mExpected.size(), m.size());
        assertEquals(mExpected, m);
    }

    /**
     * Testing SortingMachine with zero size and in extractionMode.
     */
    @Test
    public final void testSizeEmptyFalse() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false);
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, false);
        assertEquals(mExpected.size(), m.size());
        assertEquals(mExpected, m);
    }

    /**
     * Testing SortingMachine with non-zero size and in insertionMode.
     */
    @Test
    public final void testSizeNonEmptyTrue() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "b", "d", "c",
                "a", "g", "e", "y");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true, "b", "d",
                "c", "a", "g", "e", "y");
        assertEquals(mExpected.size(), m.size());
        assertEquals(mExpected, m);
    }

    /**
     * Testing SortingMachine with non-zero size and in extractionMode.
     */
    @Test
    public final void testSizeNonEmptyFalse() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false, "b", "d", "c",
                "a", "g", "e", "y");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, false, "b", "d",
                "c", "a", "g", "e", "y");
        assertEquals(mExpected.size(), m.size());
        assertEquals(mExpected, m);
    }

    /**
     * Testing SortingMachine with test being non-empty and reference being
     * empty.
     */
    @Test
    public final void testSizeOpposites() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false, "b", "d", "c",
                "a", "g", "e", "y");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, false);
        int s = m.size();
        int sExpected = mExpected.size();
        int diff = s - sExpected;
        assertEquals(sExpected + diff, s);
    }

}
