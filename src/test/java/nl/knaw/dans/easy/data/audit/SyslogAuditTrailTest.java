package nl.knaw.dans.easy.data.audit;

import java.io.IOException;

import nl.knaw.dans.common.lang.os.OS;

import org.junit.Test;

public class SyslogAuditTrailTest
{

    @Test
    public void testSyslogOnCentos5()
    {
        SyslogAuditTrail syslog = new SyslogAuditTrail();
        syslog.test("you should see this message in");
        syslog.test("whatever your /etc/syslog.conf");
        syslog.test("is pointing the facility");
        syslog.test("that is mentioned in your log4j configuration.");
        syslog.test("------------- details for Centos5 ----------------------");
        syslog.test("1. Edit /etc/syslog.conf, add line:");
        syslog.test("     local1.*<tab><tab>/var/log/easy-audit.log");
        syslog.test("     (replace <tab> with real tab ;-)");
        syslog.test("2. Restart syslog:");
        syslog.test("     # service syslog restart");
        syslog.test("3. Stop syslog:");
        syslog.test("     # service syslog stop");
        syslog.test("4. Restart syslogd with the -r option:");
        syslog.test("     syslogd -r");
        syslog.test("5. Run this test");
        syslog.test("6. Verify that this message is in /var/log/easy-audit.log");
        syslog.test("------------------- END OF TEST -------------------------");

        try
        {
            OS.execAndWait("cat /var/log/easy-audit.log", System.out, System.err);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
