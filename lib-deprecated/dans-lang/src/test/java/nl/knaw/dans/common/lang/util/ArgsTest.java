package nl.knaw.dans.common.lang.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

public class ArgsTest
{

    @Test
    public void serializeDeserialize() throws Exception
    {
        Args vmArgs = new Args();
        assertFalse(vmArgs.isReadOnly());

        vmArgs.setConfigurationClassName("nl.knaw.dans.foo.BarConfiguration");
        vmArgs.setApplicationContext("/cfg/some-application.context.xml");
        vmArgs.setUsername("\"madelief pieters\"");
        vmArgs.setProcessName("ingest");
        vmArgs.setLogConfigFile("/cfg/log/log4j.xml");
        vmArgs.setLoggingToConsole(true);

        vmArgs.put("foo.bar", true);
        vmArgs.put(null, true);

        //vmArgs.printArguments(System.out);
        String argsAsString = vmArgs.asString();
        String[] args = vmArgs.asStringArray();

        Args vmArgs2 = new Args(args);
        assertTrue(vmArgs2.isReadOnly());
        assertEquals(argsAsString, vmArgs2.asString());

        assertTrue(vmArgs2.getBooleanValue("null"));
        assertTrue(vmArgs2.getBooleanValue("foo.bar"));
    }

    @Test
    public void singleValues() throws Exception
    {
        String[] args = {"single value", "this=that", "foo=bar", "other single value"};
        Args vmArgs = new Args(args);

        //vmArgs.printArguments(System.out);

        String argsAsString = vmArgs.asString();

        Args vmArgs2 = new Args(vmArgs.asStringArray());
        assertTrue(vmArgs2.isReadOnly());
        assertEquals(argsAsString, vmArgs2.asString());

        //vmArgs2.printArguments(System.out);
    }

    @Test
    public void readFromPropertiesFile() throws Exception
    {
        String propFileName = createPropertiesFile();
        String[] args = {Args.propFileName(propFileName)};

        Args vmArgs = new Args(args);

        vmArgs.printArguments(System.out);
        assertEquals("target/test-args.properties", vmArgs.getPropFileName());
        assertEquals("nl.knaw.dans.foo.BarConfiguration", vmArgs.getConfigurationClassName());
    }

    private String createPropertiesFile() throws IOException
    {
        String propFileName = "target/test-args.properties";

        Args vmArgs = new Args();
        vmArgs.setConfigurationClassName("nl.knaw.dans.foo.BarConfiguration");
        vmArgs.setApplicationContext("/cfg/some-application.context.xml");
        vmArgs.setUsername("\"madelief pieters\"");
        vmArgs.setProcessName("ingest");
        vmArgs.setLogConfigFile("/cfg/log/log4j.xml");
        vmArgs.setLoggingToConsole(true);

        FileWriter out = null;
        try
        {
            out = new FileWriter(propFileName);
            vmArgs.printArguments(out);
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
        }

        return propFileName;
    }

}
