package nl.knaw.dans.common.ldap.management;

import org.junit.Test;

public class SchemaTest
{
    
    @Test
    public void printSchema() throws Exception
    {
        DANSSchema danss = new DANSSchema();
        danss.exportForOpenLdap();
        
        EasySchema easys = new EasySchema();
        easys.exportForOpenLdap();
        
        DCCDSchema dccds = new DCCDSchema();
        dccds.exportForOpenLdap();
    }

}
