package org.jasig.portal;

import junit.framework.*;
import java.sql.*;


/**
 * Tests the sequence generator.
 * @author: Dan Ellentuck
 */
public class SequenceGeneratorTester extends TestCase {
    private int numTestCounters;
    private String[] testCounterNames;
	private ReferenceSequenceGenerator generator;
	
    protected class Tester implements Runnable 
    {
	    protected ReferenceSequenceGenerator generator = null;
	    protected int numTests = 0;
	    protected String[] counterValues = null;
	    
        protected Tester(ReferenceSequenceGenerator gen, int tests) 
        {
            super();
            generator = gen;
            numTests = tests;
            counterValues = new String[numTests];
        }
        public void run() {
            for (int i=0; i<numTests; i++) 
            {
                String ctrValue = null;
                for (int j=0; ctrValue == null && j<10; j++)
                {
                    try { ctrValue = generator.getNext(testCounterNames[0]); }
                    catch (Exception e) 
                        { print("Caught Exception: " + e.getMessage()); }
                }
	                counterValues[i] = (ctrValue != null) 
                        ? ctrValue 
                        : System.currentTimeMillis() + "";
            }
        }
    } 
	
/**
 * EntityLockTester constructor comment.
 */
public SequenceGeneratorTester(String name) {
	super(name);
}
/**
 * @return org.jasig.portal.concurrency.locking.IEntityLockStore
 */
private ReferenceSequenceGenerator getGenerator() {
    if (generator == null)
        { generator = new ReferenceSequenceGenerator(); }
    return generator;
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) throws Exception
{
	String[] mainArgs = {"org.jasig.portal.SequenceGeneratorTester"};
    print("START TESTING SEQUENCE GENERATOR");
    printBlankLine();
    junit.swingui.TestRunner.main(mainArgs);
    printBlankLine();    
    print("END TESTING SEQUENCE GENERATOR");
    
}
/**
 * @param msg java.lang.String
 */
private static void print(String msg) 
{
    java.sql.Timestamp ts = new java.sql.Timestamp(System.currentTimeMillis());
    System.out.println(ts + " : " + msg);
}
/**
 * @param msg java.lang.String
 */
private static void printBlankLine() 
{
    System.out.println("");
}
/**
 */
protected void setUp() 
{
	Connection conn = null;
	Statement  stmt = null;
	
    try {
        String sql;
        int idx;
        numTestCounters = 5;
        testCounterNames = new String[numTestCounters];
        
        print("Creating test counters.");
        
        for (idx=0; idx<numTestCounters; idx++)
            { testCounterNames[idx] = "TEST_COUNTER_" + idx; }

        conn = RDBMServices.getConnection();
        stmt = conn.createStatement();
        
        // Delete any left over counters that could interfere. 
        for (idx=0; idx<testCounterNames.length; idx++)
        {
            sql = "DELETE FROM UP_SEQUENCE WHERE SEQUENCE_NAME = " + 
              "'" + testCounterNames[idx] + "'";
            stmt.executeUpdate(sql);
        }
        // create some test counters:
        for (idx=0; idx<numTestCounters; idx++)
        {
            sql = "INSERT INTO UP_SEQUENCE (SEQUENCE_NAME, SEQUENCE_VALUE) " + 
		          "VALUES (" + "'" + testCounterNames[idx] + "', 0)";
            stmt.executeUpdate(sql);
        }
    }
    catch (Exception ex) { print("SequenceGeneratorTester.setUp(): " + ex.getMessage());}
    finally
    {
        if (stmt != null) { try { stmt.close(); } catch (SQLException sqle) {} }
        if (conn != null) { RDBMServices.releaseConnection(conn); }
    }
 }
/**
 * @return junit.framework.Test
 */
public static junit.framework.Test suite() {
    TestSuite suite = new TestSuite();

    suite.addTest(new SequenceGeneratorTester("testGetUniqueSequenceNumbers"));
    suite.addTest(new SequenceGeneratorTester("testCreateNewCounters"));
    suite.addTest(new SequenceGeneratorTester("testSetCounterValues"));
    suite.addTest(new SequenceGeneratorTester("testConcurrentAccess"));

//	Add more tests here.
//  NB: Order of tests is not guaranteed.

	return suite;
}
/**
 */
protected void tearDown() 
{
	Connection conn = null;
	Statement stmt = null;
    try {
        // delete the test counters:
        print("Deleting test counters.");
        String sql;
        int idx;
        conn = RDBMServices.getConnection();
        stmt = conn.createStatement();
        for (idx=0; idx<testCounterNames.length; idx++)
        {
            sql = "DELETE FROM UP_SEQUENCE WHERE SEQUENCE_NAME = " + 
              "'" + testCounterNames[idx] + "'";
            stmt.executeUpdate(sql);
        }
    }
    catch (Exception ex) { print("SequenceGeneratorTester.tearDown(): " + ex.getMessage());}
    finally
    {
        if (stmt != null) { try {stmt.close();} catch (SQLException sqle) {} }
        if (conn != null) { RDBMServices.releaseConnection(conn); }
    }
}
/**
 */
public void testConcurrentAccess() throws Exception
{
    ReferenceSequenceGenerator g1 = new ReferenceSequenceGenerator();
    ReferenceSequenceGenerator g2 = new ReferenceSequenceGenerator();
    int numTests = 100;
    String msg = null;

    print("Setting up testing Threads.");
    Tester tester1 = new Tester(g1, numTests);
    Tester tester2 = new Tester(g2, numTests);
    Thread thread1 = new Thread(tester1);
    Thread thread2 = new Thread(tester2);
   
    print("Starting testing Threads.");
    thread1.start();
    thread2.start();

    
    print("Now sleeping for 10 seconds");
    Thread.sleep(10000);
    
    msg = "Checking counter values for uniqueness.";
    print(msg);

    String tester1Value, tester2Value;
     
    for (int idx1=0; idx1<numTests; idx1++)
    {
        tester1Value = tester1.counterValues[idx1];
        for (int idx2=0; idx2<numTests; idx2++)
        {
	        tester2Value = tester2.counterValues[idx2];
            assertTrue( msg, (! tester1Value.equals(tester2Value)) );
        }
    }
        
}
/**
 */
public void testCreateNewCounters() throws Exception
{
	    String msg = null;
        int numNewCounters = 5;
        int idx;
        int counterValue;

        String[] counterNames = new String[numNewCounters];
        for (idx=0; idx<numNewCounters; idx++)
            { counterNames[idx] = "NEW_CTR_" + idx; }

        print("Creating new counter(s)."); 
        for (idx=0; idx<numNewCounters; idx++)
        {
            getGenerator().createCounter(counterNames[idx]);
        }
        
        msg = "Getting sequence value from new counter(s).";
        print(msg);
        for (idx=0; idx<numNewCounters; idx++)
        {
            counterValue = getGenerator().getNextInt(counterNames[idx]);
            assertEquals(msg, 1, counterValue);
        }
        

        print("Deleting new counter(s).");
	    Connection conn = null;
	    Statement stmt = null;
        try {
            String sql;
            conn = RDBMServices.getConnection();
            stmt = conn.createStatement();
            for (idx=0; idx<counterNames.length; idx++)
            {
                sql = "DELETE FROM UP_SEQUENCE WHERE SEQUENCE_NAME = " + 
                  "'" + counterNames[idx] + "'";
                stmt.executeUpdate(sql);
            }
        }
        catch (Exception ex) { print("SequenceGeneratorTester.testCreateNewCounters(): " + ex.getMessage());}
        finally
        {
            if (stmt != null) { try {stmt.close();} catch (SQLException sqle) {} }
            if (conn != null) { RDBMServices.releaseConnection(conn); }
        }

}
/**
 */
public void testGetUniqueSequenceNumbers() throws Exception
{
    String msg = null;
    int numTestValues = 10;
    int idx;
    String[][] testValues = new String[numTestCounters][numTestValues];

    print("Getting sequence values."); 
    for (idx=0; idx<numTestCounters; idx++)
    {
        for(int i=0; i<numTestValues; i++)
        {
            testValues[idx][i] = getGenerator().getNext(testCounterNames[idx]);
        }

    }
        
    msg = "Testing sequence values for uniqueness.";
    print(msg);
    boolean assertionValue;
    for (idx=0; idx<numTestCounters; idx++)
    {
        for(int i=1; i<numTestValues; i++)
        {
            assertionValue = testValues[idx][i-1].equals( testValues[idx][i] );
            assertTrue(msg, ! assertionValue);
        }

    }
}
/**
 */
public void testSetCounterValues() throws Exception
{
    int idx, testValue, nextCounterValue;
        
    print("Setting sequence values."); 
    for (idx=0; idx<numTestCounters; idx++)
    {
        testValue = idx * 999;
        getGenerator().setCounter(testCounterNames[idx], testValue);
        nextCounterValue = getGenerator().getNextInt(testCounterNames[idx]);
        nextCounterValue--;
        assertEquals(testValue, nextCounterValue);
    }
        
}
}
