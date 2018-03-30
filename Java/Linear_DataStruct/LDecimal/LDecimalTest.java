import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static java.lang.Math.signum;
import static java.lang.Math.random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.HelpFormatter;
import org.junit.*;
import org.junit.rules.Timeout;
import org.junit.runner.*;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.*;

public class LDecimalTest {

   private static class TestPhase {
      String name;
      String description;
      int number;
      Class[] testClasses;
      
      TestPhase(String name, String desc, int number, Class... testClasses) {
         this.name = name;
         this.description = desc;
         this.number = number;
         this.testClasses = testClasses;
      }
   }
   
   // Test Phase objects
   private static TestPhase basicPhase = new TestPhase(
         "basic", "constructor, digits, signum, toString", 39,
         ZeroLDecimalTest.class, // Test construction of zero
         LDecimalFromInt.class); // Test construction of LDecimal from ints
   private static TestPhase comparePhase = new TestPhase(
         "compare", "compareTo and equals", 18,
         LDecimalCompare.class); // Test compareTo and equals for LStrings
   private static TestPhase addPhase = new TestPhase(
         "add", "add and subtract", 76,
         LDecimalAdd.class, // Test add
         LDecimalSubtract.class, // Test subtract
         LDecimalAddSpecial.class); // Special adds
   private static TestPhase multiplyPhase = new TestPhase(
         "multiply", "multiply and divide", 43,
         LDecimalMultiply.class, // Test multiply
         LDecimalDivide.class, // Test divide
         LDecimalZeroDivide.class); // Test divide by zero
   private static TestPhase torturePhase = new TestPhase(
         "torture", "torture tests", 2,
         LDecimalTorture.class);

   private static final TestPhase[] testPhases =
         {basicPhase, comparePhase, addPhase, multiplyPhase, torturePhase};
         
   // If true, there are no timeouts for tests, allowing tests to run
   // to completion
   private static boolean debugMode = false;
   
   // It true, failures in one test phase to not stop later phases from running
   private static boolean continueTests = false;
   
   private static final String COMMAND_LINE = "java LDecimalTest [options]";
   
   public static void main(String[] args) {
      TestPhase[] phases = processArgs(args);
      if (phases != null) {
         for (TestPhase phase : phases) {
            String tests = (phase.number == 1) ? "test" : "tests";
            System.out.printf("Running phase %s: %s (%d %s)%n",
                  phase.name, phase.description, phase.number, tests);
            Boolean success = new TestRunner().run(phase.testClasses);
            System.out.println();
            if (!continueTests && !success) {
               System.out.println("Test failures: abandoning other phases.");
               System.exit(1);
            }
         }
         System.out.println("Congratulations! All tests passed.");
      }
   }
   
   public static TestPhase[] processArgs(String[] args) {
      Options options = new Options();
      options.addOption("d", "debug", false, "Debugging mode (no timeouts)");
      options.addOption("c", "continue", false, "Continue testing after failures");
      options.addOption("h", "help", false, "Print help message");
 
      CommandLine command;
      try {
         command = new GnuParser().parse(options, args);
      }
      catch (ParseException ex) {
         printUsage(ex.getMessage(), options);
         return null;
      }
      
      if (command.hasOption("help")) {
         printUsage(null, options);
         return null;
      }

      TestPhase[] cmdPhases;
      String[] commandArgs = command.getArgs();
      if (commandArgs.length > 0) {
         cmdPhases = new TestPhase[commandArgs.length];
         int phaseno = 0;
         for (String name : commandArgs) {
            TestPhase phase = findPhase(name);
            if (phase == null) {
               printUsage("Unrecognized phase name: " + name, options);
               return null;
            }
            cmdPhases[phaseno++] = phase;
         }
      } else {
         cmdPhases = testPhases;
      }
      
      if (command.hasOption("debug")) {
         debugMode = true;
         standardTimeout = null;
      }
      if (command.hasOption("continue")) {
         continueTests = true;
      }
      return cmdPhases;
   }
   
   static TestPhase findPhase(String name) {
      for (TestPhase phase : testPhases) {
         if (phase.name.equals(name)) {
            return phase;
         }
      }
      return null;
   }
   
   static void printUsage(String msg, Options options) {
      if (msg != null) {
         System.out.println(msg);
      }
      new HelpFormatter().printHelp(COMMAND_LINE, options);
   }
   
   static class TestRunner {
      public boolean run(Class<?>... classes) {
          JUnitCore core = new JUnitCore();
          core.addListener(new TestListener(System.out));
          Request req = Request.classes(classes);
          req = req.filterWith(Filter.ALL);
          Result result = core.run(req);
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
                  if (className.startsWith("LDecimal")
                        && !className.equals("LDecimalTest$TestRunner")
                        && !(className.equals("LDecimalTest")
                              && elt.getMethodName().equals("main"))) {
                     if (ignored != 0) {
                        stream.printf("        ... %d more%n", ignored);
                        ignored = 0;
                     }
                     stream.println("        at " + elt);
                  } else
                     ignored++;
               }
               // if (ignored != 0)
               //    stream.printf("        ... %d more%n", ignored);
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
   
   public static int width(int n) {
      int w = 0;
      while (n != 0) {
         n /= 10;
         w++;
      }
      return w;
   }
   
   
   public static final int DEFAULT_TIMEOUT = 20;
   
   public static Timeout standardTimeout = new Timeout(DEFAULT_TIMEOUT);
   
   public static LDecimal makeTestNum(Integer n) {
      return (n != null) ? new LDecimal(n.intValue()) : new LDecimal();
   }
   
   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class ZeroLDecimalTest {
      @Rule public Timeout timeout = standardTimeout;

      @Test public void t1EmptyConstructorLength() {
         assertEquals("LDecimal().digits() is not 0.",
               0, new LDecimal().digits());
      }
      
      @Test public void t2EmptyConstructorSign() {
         assertEquals("LDecimal().signum() is not 0.",
               0, new LDecimal().signum());
      }
      
      @Test public void t3EmptyConstructortToString() {
         assertEquals("LDecimal(0).toString() is not \"0\".",
               "0", new LDecimal(0).toString());
      }
      
      @Test public void t4ZeroConstructorLength() {
         assertEquals("LDecimal(0).digits() is not 0.",
               0, new LDecimal(0).digits());
      }
      
      @Test public void t5ZeroConstructorSign() {
         assertEquals("LDecimal(0).signum() is not 0.",
               0, new LDecimal(0).signum());
      }
      
      @Test public void t6ZeroConstructortToString() {
         assertEquals("LDecimal(0).toString() is not \"0\".",
               "0", new LDecimal(0).toString());
      }
   }
    
   @RunWith(Parameterized.class)
   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class LDecimalFromInt {
      @Rule public Timeout timeout = standardTimeout;

      @Parameters
      public static Collection<Object[]> data() {
         return Arrays.asList(new Object[][] {
               {0}, {1}, {-1}, {9}, {-9}, {10}, {-10},
               {123456789}, {-123456789},
               {Integer.MAX_VALUE}, {Integer.MIN_VALUE}});
      }
      
      @Parameter
      public Integer testValue;

      public int testSign;
      public int testLength;
      
      @Before public void setUp() {
         int value = testValue;
         testSign = (value == 0) ? 0 : ((value > 0) ? 1 : -1);
         testLength = width(value);
      }      
      
      @Test public void t1IntLength() {
         assertEquals("wrong LDecimal(" + testValue + ").digits()",
               testLength, new LDecimal(testValue).digits());
      }
      
      @Test public void t2IntSign() {
         assertEquals("wrong LDecimal(" + testValue + ").signum()",
               testSign, new LDecimal(testValue).signum());
      }
      
      @Test public void t3IntToString() {
         assertEquals("wrong LDecimal(" + testValue + ").toString()",
               Integer.toString(testValue), new LDecimal(testValue).toString());
      }
   }

   @RunWith(Parameterized.class)
   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class LDecimalCompare {
      @Rule public Timeout timeout = standardTimeout;

      @Parameters
      public static Collection<Object[]> data() {
         return Arrays.asList(new Object[][] {
               {0, 0, 0}, {null, null, 0}, {null, 0, 0},
               {1, 0, 1}, {1, 1, 0}, {1, -1, 1},
               {123457, 123456, 1}, {23456, 100000, -1}, {123456789, 123456789, 0}});
      }
      
      @Parameter(0) public Integer testInt1;
      
      @Parameter(1) public Integer testInt2;
      
      @Parameter(2) public int result;
            
      @Test public void t1TestCompareTo() {
         LDecimal test1 = makeTestNum(testInt1);
         LDecimal test2 = makeTestNum(testInt2);
         assertEquals("compareTo of " + testInt1 + " and " + testInt2 + " wrong",
               result, (int)signum(test1.compareTo(test2)));
         assertEquals("compareTo of " + testInt2 + " and " + testInt1 + " wrong",
               -result, (int)signum(test2.compareTo(test1)));
      }

      @Test public void t2TestEquals() {
         LDecimal test1 = makeTestNum(testInt1);
         LDecimal test2 = makeTestNum(testInt2);
         assertEquals("equals of " + testInt1 + " and " + testInt2 + " wrong",
               result == 0, test1.equals(test2));
         assertEquals("equals of " + testInt2 + " and " + testInt1 + " wrong",
               result == 0, test2.equals(test1));
      }
   }        

   @RunWith(Parameterized.class)
   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class LDecimalAdd {
      @Rule public Timeout timeout = standardTimeout;

      @Parameters
      public static Collection<Object[]> data() {
         return Arrays.asList(new Object[][] {
               {0, 0}, {1, 0}, {1, -1}, {1, 1}, {-12345678, 12345678},
               {1, 9}, {-1, -9}, {100, 1}, {999999999, 1},
               {999999990, 10}, {10, -1}, {100, -10}, {-10,1}, {-100, 10},
               {1000000000, -1}, {-1000000000, 1},
               {1000000009, -10}, {-1000000009, 10}});
      }
      
      @Parameter(0) public Integer testInt1;
      
      @Parameter(1) public Integer testInt2;
      
      @Test public void t1TestAdd() {
         LDecimal test1 = makeTestNum(testInt1);
         LDecimal test2 = makeTestNum(testInt2);
         int sum = testInt1 + testInt2;
         LDecimal testSum = makeTestNum(sum);
         LDecimal result = test1.add(test2);
         assertEquals(testInt1 + " + " + testInt2 + " wrong",
               testSum, result);
         assertEquals("signum of " + testInt1 + " + " + testInt2 + " wrong",
               Integer.signum(sum), result.signum());
         assertEquals("digits of " + testInt1 + " + " + testInt2 + " wrong",
               width(sum), result.digits());
         assertEquals("toString of " + testInt1 + " + " + testInt2 + " wrong",
               Integer.toString(sum), result.toString());
      }
   
      @Test public void t2TestReverseAdd() {
         LDecimal test1 = makeTestNum(testInt1);
         LDecimal test2 = makeTestNum(testInt2);
         int sum = testInt1 + testInt2;
         LDecimal testSum = makeTestNum(sum);
         LDecimal result = test2.add(test1);
         assertEquals(testInt2 + " + " + testInt1 + " wrong",
               testSum, result);
         assertEquals("signum of " + testInt2 + " + " + testInt1 + " wrong",
               Integer.signum(sum), result.signum());
         assertEquals("digits of " + testInt2 + " + " + testInt1 + " wrong",
               width(sum), result.digits());
         assertEquals("toString of " + testInt2 + " + " + testInt1 + " wrong",
               Integer.toString(sum), result.toString());
      }
   }        
   
   @RunWith(Parameterized.class)
   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class LDecimalSubtract {
      @Rule public Timeout timeout = standardTimeout;

      @Parameters
      public static Collection<Object[]> data() {
         return Arrays.asList(new Object[][] {
               {0, 0}, {1, 0}, {1, -1}, {1, 1}, {12345678, 12345678},
               {10, 1}, {-10, -1}, {100, -1}, {999999999, -1},
               {999999990, -10}, {10, 1}, {100, 10}, {-10, -1}, {-100, -10},
               {1000000000, 1}, {-1000000000, -1},
               {1000000009, 10}, {-1000000009, -10}});
      }
      
      @Parameter(0) public Integer testInt1;
      
      @Parameter(1) public Integer testInt2;
      
      @Test public void t1TestSubtract() {
         LDecimal test1 = makeTestNum(testInt1);
         LDecimal test2 = makeTestNum(testInt2);
         int diff = testInt1 - testInt2;
         LDecimal testDiff = makeTestNum(diff);
         LDecimal result = test1.subtract(test2);
         assertEquals(testInt1 + " - " + testInt2 + " wrong",
               testDiff, result);
         assertEquals("signum of " + testInt1 + " - " + testInt2 + " wrong",
               Integer.signum(diff), result.signum());
         assertEquals("digits of " + testInt1 + " - " + testInt2 + " wrong",
               width(diff), result.digits());
         assertEquals("toString of " + testInt1 + " - " + testInt2 + " wrong",
               Integer.toString(diff), result.toString());
      }
   
      @Test public void t2TestReverseSubtract() {
         LDecimal test1 = makeTestNum(testInt1);
         LDecimal test2 = makeTestNum(testInt2);
         int diff = testInt2 - testInt1;
         LDecimal testDiff = makeTestNum(diff);
         LDecimal result = test2.subtract(test1);
         assertEquals(testInt2 + " - " + testInt1 + " wrong",
               testDiff, result);
         assertEquals("signum of " + testInt2 + " - " + testInt1 + " wrong",
               Integer.signum(diff), result.signum());
         assertEquals("digits of " + testInt2 + " - " + testInt1 + " wrong",
               width(diff), result.digits());
         assertEquals("toString of " + testInt2 + " - " + testInt1 + " wrong",
               Integer.toString(diff), result.toString());
      }
   }        
   
   @RunWith(Parameterized.class)
   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class LDecimalAddSpecial {
      @Rule public Timeout timeout = standardTimeout;

      @Parameters
      public static Collection<Object[]> data() {
         return Arrays.asList(new Object[][] {
               {99, 0}, {-99, 0}});
      }
      
      @Parameter(0) public Integer testInt1;
      
      @Parameter(1) public Integer testInt2;
      
      @Test public void t1TestAddSame() {
         LDecimal test1 = makeTestNum(testInt1);
         LDecimal test2 = makeTestNum(testInt2);
         LDecimal result = test1.add(test2);
         assertSame(test1 + ".add(" + test2 + ") should be same LDecimal as " + test1,
               test1, result);
         result = test2.add(test1);
         assertSame(test2 + ".add(" + test1 + ") should be same LDecimal as " + test1,
               test1, result);
      }
      
      @Test public void t2TestSubtractSame() {
         LDecimal test1 = makeTestNum(testInt1);
         LDecimal test2 = makeTestNum(testInt2);
         LDecimal result = test1.subtract(test2);
         assertSame(test1 + ".subtract(" + test2 + ") should be same LDecimal as " + test1,
               test1, result);
      }
   }
   
   @RunWith(Parameterized.class)
   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class LDecimalMultiply {
      @Rule public Timeout timeout = standardTimeout;

      @Parameters
      public static Collection<Object[]> data() {
         return Arrays.asList(new Object[][] {
               {0, 0}, {1, 0}, {1, 2}, {-1, 0}, {-1, 1}, {-1, 2}, {-1, -2},
               {12345, 12346}, {-12345, 12346}, {-12345, -12346},
               {65536, 32767}});
      }
      
      @Parameter(0) public Integer testInt1;
      
      @Parameter(1) public Integer testInt2;
      
      @Test public void t1TestMultiply() {
         LDecimal test1 = makeTestNum(testInt1);
         int test2 = testInt2;
         int product = testInt1 * test2;
         LDecimal testProduct = makeTestNum(product);
         LDecimal result = test1.multiply(test2);
         assertEquals(testInt1 + " * " + test2 + " wrong",
               testProduct, result);
         assertEquals("signum of " + testInt1 + " * " + test2 + " wrong",
               Integer.signum(product), result.signum());
         assertEquals("digits of " + testInt1 + " * " + test2 + " wrong",
               width(product), result.digits());
         assertEquals("toString of " + testInt1 + " * " + test2 + " wrong",
               Integer.toString(product), result.toString());
      }
   
      @Test public void t2TestReverseMultiply() {
         int test1 = testInt1;
         LDecimal test2 = makeTestNum(testInt2);
         int product = test1 * testInt2;
         LDecimal testProduct = makeTestNum(product);
         LDecimal result = test2.multiply(test1);
         assertEquals(testInt2 + " * " + test1 + " wrong",
               testProduct, result);
         assertEquals("signum of " + testInt2 + " * " + test1 + " wrong",
               Integer.signum(product), result.signum());
         assertEquals("digits of " + testInt2 + " * " + test1 + " wrong",
               width(product), result.digits());
         assertEquals("toString of " + testInt2 + " * " + test1 + " wrong",
               Integer.toString(product), result.toString());
      }
   
   }        

   @RunWith(Parameterized.class)
   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class LDecimalDivide {
      @Rule public Timeout timeout = standardTimeout;

      @Parameters
      public static Collection<Object[]> data() {
         return Arrays.asList(new Object[][] {
               {0, 1}, {1, 1}, {2, 1}, {1, 2},
               {0, -1}, {1, -1}, {2, -1}, {1, -2},
               {-1, 1}, {-2, 1}, {-1, 2}, {-1, -1}, {-2, -1}, {-1, -2},
               {123456789, 9}, {123456788, 9}, {1, 123456789},
               {123456788, 123456789}, {123456789, 123456789},
               {123456790, 123456789}});               
      }
      
      @Parameter(0) public Integer testInt1;
      
      @Parameter(1) public Integer testInt2;
      
      @Test public void t1TestDivide() {
         LDecimal test1 = makeTestNum(testInt1);
         int test2 = testInt2;
         int quotient = testInt1 / test2;
         LDecimal testQuotient = makeTestNum(quotient);
         LDecimal result = test1.divide(test2);
         assertEquals(testInt1 + ".divide(" + test2 + ") wrong",
               testQuotient, result);
         assertEquals("signum of " + testInt1 + ".divide(" + test2 + ") wrong",
               Integer.signum(quotient), result.signum());
         assertEquals("digits of " + testInt1 + ".divide(" + test2 + ") wrong",
               width(quotient), result.digits());
         assertEquals("toString of " + testInt1 + ".divide(" + test2 + ") wrong",
               Integer.toString(quotient), result.toString());
      }
   }

   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class LDecimalZeroDivide {
      @Rule public Timeout timeout = standardTimeout;
      
      @Test(expected = ArithmeticException.class)
      public void t1TestZeroDivide() {
         LDecimal test1 = makeTestNum(1);
         LDecimal testQ = test1.divide(0);
         fail("Expected ArithmeticException missing, result == " + testQ);
      }
   }
   
   
   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class LDecimalTorture {
      @Rule public Timeout timeout = debugMode ? null : new Timeout(10000);
      
      @Test public void t1Fact2000() {
         // Compute 1000 factorial and then divide back to 1
         LDecimal fact = new LDecimal(1); // 0!
         for (int n = 1; n <= 2000; n++) {
            fact = fact.multiply(n);
         }
         LDecimal fact2000 = fact; // Save
         assertEquals("2000! wrong",
            "331627509245063324117539338057632403828111720810578039457193543706038077905600822400273230859732592255402352941225834109258084817415293796131386633526343688905634058556163940605117252571870647856393544045405243957467037674108722970434684158343752431580877533645127487995436859247408032408946561507233250652797655757179671536718689359056112815871601717232657156110004214012420433842573712700175883547796899921283528996665853405579854903657366350133386550401172012152635488038268152152246920995206031564418565480675946497051552288205234899995726450814065536678969532101467622671332026831552205194494461618239275204026529722631502574752048296064750927394165856283531779574482876314596450373991327334177263608852490093506621610144459709412707821313732563831572302019949914958316470942774473870327985549674298608839376326824152478834387469595829257740574539837501585815468136294217949972399813599481016556563876034227312912250384709872909626622461971076605931550201895135583165357871492290916779049702247094611937607785165110684432255905648736266530377384650390788049524600712549402614566072254136302754913671583406097831074945282217490781347709693241556111339828051358600690594619965257310741177081519922564516778571458056602185654760952377463016679422488444485798349801548032620829890965857381751888619376692828279888453584639896594213952984465291092009103710046149449915828588050761867924946385180879874512891408019340074625920057098729578599643650655895612410231018690556060308783629110505601245908998383410799367902052076858669183477906558544700148692656924631933337612428097420067172846361939249698628468719993450393889367270487127172734561700354867477509102955523953547941107421913301356819541091941462766417542161587625262858089801222443890248677182054959415751991701271767571787495861619665931878855141835782092601482071777331735396034304969082070589958701381980813035590160762908388574561288217698136182483576739218303118414719133986892842344000779246691209766731651433494437473235636572048844478331854941693030124531676232745367879322847473824485092283139952509732505979127031047683601481191102229253372697693823670057565612400290576043852852902937606479533458179666123839605262549107186663869354766108455046198102084050635827676526589492393249519685954171672419329530683673495544004586359838161043059449826627530605423580755894108278880427825951089880635410567917950974017780688782869810219010900148352061688883720250310665922068601483649830532782088263536558043605686781284169217133047141176312175895777122637584753123517230990549829210134687304205898014418063875382664169897704237759406280877253702265426530580862379301422675821187143502918637636340300173251818262076039747369595202642632364145446851113427202150458383851010136941313034856221916631623892632765815355011276307825059969158824533457435437863683173730673296589355199694458236873508830278657700879749889992343555566240682834763784685183844973648873952475103224222110561201295829657191368108693825475764118886879346725191246192151144738836269591643672490071653428228152661247800463922544945170363723627940757784542091048305461656190622174286981602973324046520201992813854882681951007282869701070737500927666487502174775372742351508748246720274170031581122805896178122160747437947510950620938556674581252518376682157712807861499255876132352950422346387878954850885764466136290394127665978044202092281337987115900896264878942413210454925003566670632909441579372986743421470507213588932019580723064781498429522595589012754823971773325722910325760929790733299545056388362640474650245080809469116072632087494143973000704111418595530278827357654819182002449697761111346318195282761590964189790958117338627206088910432945244978535147014112442143055486089639578378347325323595763291438925288393986256273242862775563140463830389168421633113445636309571965978466338551492316196335675355138403425804162919837822266909521770153175338730284610841886554138329171951332117895728541662084823682817932512931237521541926970269703299477643823386483008871530373405666383868294088487730721762268849023084934661194260180272613802108005078215741006054848201347859578102770707780655512772540501674332396066253216415004808772403047611929032210154385353138685538486425570790795341176519571188683739880683895792743749683498142923292196309777090143936843655333359307820181312993455024206044563340578606962471961505603394899523321800434359967256623927196435402872055475012079854331970674797313126813523653744085662263206768837585132782896252333284341812977624697079543436003492343159239674763638912115285406657783646213911247447051255226342701239527018127045491648045932248108858674600952306793175967755581011679940005249806303763141344412269037034987355799916009259248075052485541568266281760815446308305406677412630124441864204108373119093130001154470560277773724378067188899770851056727276781247198832857695844217588895160467868204810010047816462358220838532488134270834079868486632162720208823308727819085378845469131556021728873121907393965209260229101477527080930865364979858554010577450279289814603688431821508637246216967872282169347370599286277112447690920902988320166830170273420259765671709863311216349502171264426827119650264054228231759630874475301847194095524263411498469508073390080000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
            fact.toString());
            
         for (int n = 1; n <= 2000; n++) {
            fact = fact.divide(n);
         }
         assertEquals("2000!/2000! wrong", new LDecimal(1), fact);
      }
      
      private static final int TWO_TO_30 = 1073741824; // 2^30
      private static final int BASEP = 1000003; // A prime. o(TWO_TO_30) mod BASEP = 166667
      
      private static int mod(LDecimal n, int k) {
         LDecimal answer = n.subtract(n.divide(k).multiply(k));
         assert answer.signum() >= 0 && answer.digits() <= 9;
         try {
            return Integer.parseInt(answer.toString());
         } catch (NumberFormatException ex) {
            throw new RuntimeException("NumberFormatException: " + ex.getMessage());
         }
      }                
      
      @Test public void t1TwoTo75000() {
         int k = TWO_TO_30; // k == 2^30
         LDecimal kPowerN = new LDecimal(1); // k^n
         int kPowerNModP = 1; // k^n mod BASEP
         for (int n = 2; n <= 2500; n++) {
            kPowerN = kPowerN.multiply(k);
            int modulus = mod(kPowerN, BASEP); // k^n % BASEP
            kPowerNModP = (int)((((long)kPowerNModP) * k) % BASEP);
            assertEquals("Mod failure at n = " + n, kPowerNModP, modulus);
         }
      } 
   }
}
