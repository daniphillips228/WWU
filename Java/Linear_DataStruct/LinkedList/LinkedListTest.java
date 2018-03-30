import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.*;
import org.junit.rules.Timeout;
import org.junit.runner.*;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.*;

public class LinkedListTest {

   private static class TestPhase {
      String name;
      int number;
      Class[] testClasses;
      
      TestPhase(String name, int number, Class... testClasses) {
         this.name = name;
         this.number = number;
         this.testClasses = testClasses;
      }
   }

   private static final TestPhase[] testPhases =
         {new TestPhase("Basic tests: constructor, isEmpty, size", 2,
               BasicTests.class), // Test construction of lists
          new TestPhase("Test add(value)", 6,
               AddTest.class),
          new TestPhase("Test get", 3,
               GetTest.class),
          new TestPhase("Test add(index, value)", 5,
               Add2Test.class),
          new TestPhase("Test remove", 6,
               RemoveTest.class),
         };
   
   public static void main(String[] args) {
      for (TestPhase phase : testPhases) {
         String tests = (phase.number == 1) ? "test" : "tests";
         System.out.println("Running " + phase.name + " tests (" + phase.number + " " + tests + ")");
         Boolean success = new TestRunner().run(phase.testClasses);
         System.out.println();
         if (!success) {
            System.out.println("Test failures: abandoning other phases.");
            System.exit(1);
         }
      }
      System.out.println("Congratulations! All tests passed.");
   }
   
   static class TestRunner {
      public boolean run(Class<?>... classes) {
          JUnitCore core = new JUnitCore();
          core.addListener(new TestListener(System.out));
          Result result = core.run(classes);
          printResult(System.out, result);
          return result.wasSuccessful();
      }
      
      public void printResult(PrintStream stream, Result result) {
         // Header
         stream.printf("Time: %.3f%n", result.getRunTime()/1000.0);

         // Print Failures
         List<Failure> failures = result.getFailures();
         if (failures.size() > 0) {
            stream.println();
            String format = (failures.size() == 1) ?
                  "There was %d failure:%n" :
                  "There were %d failures:%n";
            stream.printf(format, failures.size());
            int failNo = 0;
            for (Failure fail : failures) {
               stream.printf("%d) %s%n", ++failNo, fail.getTestHeader());
               Throwable ex = fail.getException();
               stream.println(ex);
               int ignored = 0;
               for (StackTraceElement elt : ex.getStackTrace()) {
                  String className = elt.getClassName();
                  if (className.startsWith("LString") || className.startsWith("org.junit.Assert")) {
                     if (ignored != 0) {
                        stream.printf("        ... %d more%n", ignored);
                        ignored = 0;
                     }
                     stream.println("        at " + elt);
                  } else
                     ignored++;
               }
               if (ignored != 0)
                  stream.printf("        ... %d more%n", ignored);
            }
            stream.println();
         }
         
         // Footer
         int runCount = result.getRunCount();
         String tests = (runCount == 1) ? "test" : "tests";
         int ignoreCount = result.getIgnoreCount();
         String ignoreTests = (ignoreCount == 1) ? "test" : "tests";
         if (runCount == 0) {
            if (ignoreCount == 0)
               stream.printf("No tests were run.");
            else
               stream.printf("No tests were run (%d %s ignored.)",
                  ignoreCount, ignoreTests);
         } else {
            if (result.wasSuccessful())
               stream.printf("OK! (%d %s passed", runCount, tests);
            else
               stream.printf("Test Failed! (%d of %d %s failed",
                     result.getFailureCount(), runCount, tests);
            if (result.getIgnoreCount() != 0)
               stream.printf(", %d %s ignored", ignoreCount, ignoreTests);
            stream.println(".)");
         }
      }
   }
   
   static class TestListener extends RunListener {
      private final PrintStream stream;
      private boolean testStarted = false;
      
      public TestListener(PrintStream stream) {
         this.stream = stream;
      }
        
      @Override
      public void testRunStarted(Description description) {
         stream.append("Starting tests: ");
         testStarted = false;
      }
      
      @Override
      public void testRunFinished(Result result) {
         stream.println();
      }
      
      @Override
      public void testStarted(Description description) {
         testStarted = true;
      }
      
      @Override
      public void testFailure(Failure failure) {
         stream.append('E');
         testStarted = false;
      }
      
      @Override
      public void testFinished(Description description) {
         if (testStarted)
            stream.append('.');
         testStarted = false;
      }
      
      @Override
      public void testIgnored(Description description) {
         stream.append('I');
      }
   }
   
   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class BasicTests {   
   
      // Maximum 10 milliseconds for all tests
      @Rule public Timeout timeout = new Timeout(10);

      @Test public void t01EmptyConstructor() {
         assertEquals("Empty constructor gives non-empty list.",
            true, new LinkedList<String>().isEmpty());
      }
      
      @Test public void t02EmptyConstructorSize() {
         assertEquals("Empty constructor gives non-zero size.",
            0, new LinkedList<String>().size());
      }
   }

   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class AddTest {   
   
      // Maximum 10 milliseconds for all tests
      @Rule public Timeout timeout = new Timeout(10);
   
      @Test public void t11OneItemList() {
         LinkedList<String> list = new LinkedList<String>();
         list.add("value #1");
         assertEquals("get(0) on one element list is wrong.",
            false, list.isEmpty());
      }
   
      @Test public void t12OneItemListSize() {
         LinkedList<String> list = new LinkedList<String>();
         list.add("value #1");
         assertEquals("One element list has wrong size.",
            1, list.size());
      }
   
      @Test public void t13TwoItemList() {
         LinkedList<String> list = new LinkedList<String>();
         list.add("value #1");
         list.add("Value #2");
         assertEquals("Two element list is empty.",
            false, list.isEmpty());
      }
   
      @Test public void t14TwoItemListSize() {
         LinkedList<String> list = new LinkedList<String>();
         list.add("value #1");
         list.add("Value #2");
         assertEquals("Two element list has wrong size.",
            2, list.size());
      }
   
      @Test public void t15ThreeItemList() {
         LinkedList<String> list = new LinkedList<String>();
         list.add("value #1");
         list.add("Value #2");
         list.add("And a third value");
         assertEquals("Three element is empty.",
            false, list.isEmpty());
      }
   
      @Test public void t11ThreeItemListSize() {
         LinkedList<String> list = new LinkedList<String>();
         list.add("value #1");
         list.add("Value #2");
         list.add("And a third value");
         assertEquals("Three element list has wrong size.",
            3, list.size());
      }
   }

   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class GetTest {   
   
      // Maximum 10 milliseconds for all tests
      @Rule public Timeout timeout = new Timeout(10);
   
      @Test public void t21OneItemList() {
         LinkedList<String> list = new LinkedList<String>();
         list.add("value #1");
         assertEquals("get(0) on one element list is wrong.",
            "value #1", list.get(0));
      }
   
   
      @Test public void t22TwoItemList() {
         LinkedList<String> list = new LinkedList<String>();
         list.add("value #1");
         list.add("Value #2");
         assertEquals("get(0) on two element list is wrong.",
            "value #1", list.get(0));
         assertEquals("get(1) on two element list is wrong.",
            "Value #2", list.get(1));
      }
   
      @Test public void t23ThreeItemList() {
         LinkedList<String> list = new LinkedList<String>();
         list.add("value #1");
         list.add("Value #2");
         list.add("And a third value");
         assertEquals("get(0) on three element list is wrong.",
            "value #1", list.get(0));
         assertEquals("get(1) on three element list is wrong.",
            "Value #2", list.get(1));
         assertEquals("get(2) on three element list is wrong.",
            "And a third value", list.get(2));
      }
   }

   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class Add2Test {   
   
      // Maximum 10 milliseconds for all tests
      @Rule public Timeout timeout = new Timeout(10);
   
      LinkedList<Integer> nullList = null;
      LinkedList<Integer> threeList = null;
   
      @Before public void setUp() {
         nullList = new LinkedList<Integer>();
         threeList = new LinkedList<Integer>();
         threeList.add(2);
         threeList.add(4);
         threeList.add(6);
      }

      @Test public void t31AddtoEmptyList() {
         nullList.add(0, 4);
         assertEquals("one element list is empty.",
            false, nullList.isEmpty());
         assertEquals("one element list has wrong size.",
            1, nullList.size());
         assertEquals("get(0) on one element list is wrong.",
            new Integer(4), nullList.get(0));
      }
         
      @Test public void t32AddtoThreeItemList0() {
         threeList.add(0, 1);
         assertEquals("four element list is empty.",
            false, threeList.isEmpty());
         assertEquals("four element list has wrong size.",
            4, threeList.size());
         assertEquals("get(0) on four element list is wrong.",
            new Integer(1), threeList.get(0));
         assertEquals("get(1) on four element list is wrong.",
            new Integer(2), threeList.get(1));
      }
   
      @Test public void t33AddtoThreeItemList1() {
         threeList.add(1, 3);
         assertEquals("four element list is empty.",
            false, threeList.isEmpty());
         assertEquals("four element list has wrong size.",
            4, threeList.size());
         assertEquals("get(0) on four element list is wrong.",
            new Integer(2), threeList.get(0));
         assertEquals("get(1) on four element list is wrong.",
            new Integer(3), threeList.get(1));
         assertEquals("get(2) on four element list is wrong.",
            new Integer(4), threeList.get(2));
      }
   
      @Test public void t34AddtoThreeItemList2() {
         threeList.add(2, 5);
         assertEquals("four element list is empty.",
            false, threeList.isEmpty());
         assertEquals("four element list has wrong size.",
            4, threeList.size());
         assertEquals("get(0) on four element list is wrong.",
            new Integer(4), threeList.get(1));
         assertEquals("get(1) on four element list is wrong.",
            new Integer(5), threeList.get(2));
         assertEquals("get(1) on four element list is wrong.",
            new Integer(6), threeList.get(3));
      }
   
      @Test public void t35AddtoThreeItemList3() {
         threeList.add(3, 7);
         assertEquals("four element list is empty.",
         false, threeList.isEmpty());
         assertEquals("four element list has wrong size.",
         4, threeList.size());
         assertEquals("get(1) on four element list is wrong.",
            new Integer(6), threeList.get(2));
         assertEquals("get(1) on four element list is wrong.",
            new Integer(7), threeList.get(3));
      }
   }   

   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class RemoveTest {   
   
      // Maximum 10 milliseconds for all tests
      @Rule public Timeout timeout = new Timeout(10);
   
      LinkedList<Character> oneList = null;
      LinkedList<Character> threeList = null;
   
      @Before public void setUp() {
         oneList = new LinkedList<Character>();
         oneList.add('a');
         threeList = new LinkedList<Character>();
         threeList.add('x');
         threeList.add('y');
         threeList.add('z');
      }

      @Test public void t41RemoveFromOneItemList() {
         assertEquals("Wrong item removed from One ItemList",
            new Character('a'), oneList.remove());
         assertEquals("zero element list is not empty.",
            true, oneList.isEmpty());
         assertEquals("zero element list has wrong size.",
            0, oneList.size());
      }

      @Test public void t42RemoveFromOneItemList() {
         assertEquals("Wrong item removed from One ItemList",
            new Character('a'), oneList.remove(0));
         assertEquals("zero element list is not empty.",
            true, oneList.isEmpty());
         assertEquals("zero element list has wrong size.",
            0, oneList.size());
      }

      @Test public void t42RemoveFromThreeItemList() {
         assertEquals("Wrong item removed from One ItemList",
            new Character('x'), threeList.remove());
         assertEquals("two element list is empty.",
            false, threeList.isEmpty());
         assertEquals("two element list has wrong size.",
            2, threeList.size());
         assertEquals("get(0) on two element list is wrong.",
            new Character('y'), threeList.get(0));
      }

      @Test public void t43RemoveFromThreeItemList0() {
         assertEquals("Wrong item removed from One ItemList",
            new Character('x'), threeList.remove(0));
         assertEquals("two element list is empty.",
            false, threeList.isEmpty());
         assertEquals("two element list has wrong size.",
            2, threeList.size());
         assertEquals("get(0) on two element list is wrong.",
            new Character('y'), threeList.get(0));
      }

      @Test public void t44RemoveFromThreeItemList1() {
         assertEquals("Wrong item removed from One ItemList",
            new Character('y'), threeList.remove(1));
         assertEquals("two element list is empty.",
            false, threeList.isEmpty());
         assertEquals("two element list has wrong size.",
            2, threeList.size());
         assertEquals("get(0) on two element list is wrong.",
            new Character('x'), threeList.get(0));
         assertEquals("get(1) on two element list is wrong.",
            new Character('z'), threeList.get(1));
      }

      @Test public void t45RemoveFromThreeItemList2() {
         assertEquals("Wrong item removed from One ItemList",
            new Character('z'), threeList.remove(2));
         assertEquals("two element list is empty.",
            false, threeList.isEmpty());
         assertEquals("two element list has wrong size.",
            2, threeList.size());
         assertEquals("get(1) on two element list is wrong.",
            new Character('y'), threeList.get(1));
      }
   }
}