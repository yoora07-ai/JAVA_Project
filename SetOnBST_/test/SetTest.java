import static org.junit.Assert.assertEquals;

import org.junit.Test;

import components.set.Set;

/**
 * JUnit test fixture for {@code Set<String>}'s constructor and kernel methods.
 *
 * @author Put your name here
 *
 */
public abstract class SetTest {

    /**
     * Invokes the appropriate {@code Set} constructor for the implementation
     * under test and returns the result.
     *
     * @return the new set
     * @ensures constructorTest = {}
     */
    protected abstract Set<String> constructorTest();

    /**
     * Invokes the appropriate {@code Set} constructor for the reference
     * implementation and returns the result.
     *
     * @return the new set
     * @ensures constructorRef = {}
     */
    protected abstract Set<String> constructorRef();

    /**
     * Creates and returns a {@code Set<String>} of the implementation under
     * test type with the given entries.
     *
     * @param args
     *            the entries for the set
     * @return the constructed set
     * @requires [every entry in args is unique]
     * @ensures createFromArgsTest = [entries in args]
     */
    private Set<String> createFromArgsTest(String... args) {
        Set<String> set = this.constructorTest();
        for (String s : args) {
            assert !set.contains(s) : "Violation of: every entry in args is unique";
            set.add(s);
        }
        return set;
    }

    /**
     * Creates and returns a {@code Set<String>} of the reference implementation
     * type with the given entries.
     *
     * @param args
     *            the entries for the set
     * @return the constructed set
     * @requires [every entry in args is unique]
     * @ensures createFromArgsRef = [entries in args]
     */
    private Set<String> createFromArgsRef(String... args) {
        Set<String> set = this.constructorRef();
        for (String s : args) {
            assert !set.contains(s) : "Violation of: every entry in args is unique";
            set.add(s);
        }
        return set;
    }

    // TODO - add test cases for constructor, add, remove, removeAny, contains, and size

    /*
     * Test for default constructor
     */
    @Test
    public final void testDefaultConstructor() {

        // Create instance
        Set<String> s = this.constructorTest();
        Set<String> sExpected = this.constructorTest();

        // Check equality
        assertEquals(sExpected, s);
    }

    /*
     * Test for Add: add an empty set to add an element.
     */
    @Test
    public final void testAddEmpty() {
        Set<String> s = this.createFromArgsTest();
        Set<String> sExpected = this.createFromArgsRef("1");

        s.add("1");

        assertEquals(sExpected, s);
    }

    /*
     * Test for Add: add non empty set to an element.
     */
    @Test
    public final void testAddNonEmpty1() {
        Set<String> s = this.createFromArgsTest("a", "b", "c");
        Set<String> sExpected = this.createFromArgsTest("a", "b", "c", "d");

        s.add("d");

        assertEquals(sExpected, s);
    }

    /*
     * Test for Add: add non empty set to an element.
     */
    @Test
    public final void testAddNonEmpty2() {
        Set<String> s = this.createFromArgsTest("aab", "abb", "acb");
        Set<String> sExpected = this.createFromArgsTest("aab", "abb", "acb", "adb");

        s.add("adb");

        assertEquals(sExpected, s);
    }

    /*
     * Test for Add: add non empty set to an element.
     */
    @Test
    public final void testAddNonEmpty3() {
        Set<String> s = this.createFromArgsTest("Dsl", "Fsl", "Hsl");
        Set<String> sExpected = this.createFromArgsTest("Dsl", "Fsl", "Hsl", "Zsl");

        s.add("Zsl");

        assertEquals(sExpected, s);
    }

    /*
     * Test for Add: add non empty set to an element.
     */
    @Test
    public final void testAddNonEmpty4() {
        Set<String> s = this.createFromArgsTest("Ball", "Floor", "House", "Joy");
        Set<String> sExpected = this.createFromArgsTest("Ball", "Floor", "House", "Joy",
                "Zoo");

        s.add("Zoo");

        assertEquals(sExpected, s);
    }

    /*
     * Test for remove : remove an element from the set with only one element
     */
    @Test
    public final void testRemoveOne() {
        Set<String> s = this.createFromArgsTest("1");
        Set<String> sExpected = this.createFromArgsRef();
        String rExpected = "1";

        String r = s.remove("1");

        assertEquals(sExpected, s);
        assertEquals(rExpected, r);
    }

    /*
     * Test for remove : remove an element from the set with multiple elements
     */
    @Test
    public final void testRemoveMulti1() {
        Set<String> s = this.createFromArgsTest("a", "b", "c", "d");
        Set<String> sExpected = this.createFromArgsTest("a", "b", "c");
        String rExpected = "d";

        String r = s.remove("d");

        assertEquals(sExpected, s);
        assertEquals(rExpected, r);
    }

    /*
     * Test for remove : remove an element from the set with multiple elements
     */
    @Test
    public final void testRemoveMulti2() {
        Set<String> s = this.createFromArgsTest("aab", "abb", "acb", "adb");
        Set<String> sExpected = this.createFromArgsTest("aab", "abb", "acb");
        String rExpected = "adb";

        String r = s.remove("adb");

        assertEquals(sExpected, s);
        assertEquals(rExpected, r);
    }

    /*
     * Test for remove : remove an element from the set with multiple elements
     */
    @Test
    public final void testRemoveMulti3() {
        Set<String> s = this.createFromArgsTest("Dsl", "Fsl", "Hsl", "Zsl");
        Set<String> sExpected = this.createFromArgsTest("Dsl", "Fsl", "Hsl");
        String rExpected = "Zsl";

        String r = s.remove("Zsl");

        assertEquals(sExpected, s);
        assertEquals(rExpected, r);
    }

    /*
     * Test for remove : remove an element from the set with multiple elements
     */
    @Test
    public final void testRemoveMulti4() {
        Set<String> s = this.createFromArgsTest("Ball", "Floor", "House", "Joy", "Zoo");
        Set<String> sExpected = this.createFromArgsTest("Ball", "Floor", "House", "Joy");
        String rExpected = "Zoo";

        String r = s.remove("Zoo");

        assertEquals(sExpected, s);
        assertEquals(rExpected, r);
    }

}
