package nl.knaw.dans.easy.sword;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/** TODO rather test the public methods and make the other methods private */
public class TestFederativeAuthentication
{

    private static final String ILLEGAL_TRADITIONAL_ID = "...";

    private static final String HASH_KEY = "d49bcb3d-ffb6-4748-aef4-8ca6319f3afb";
    
    /** TODO is this a sensible name? */
    private static final String TOKEN = "f33bbf238a3157b0db8ab45088cc77d1d10bb640";
    
    private static final String EPPN = "richardzijdeman@SURFguest.nl";

    @Test
    public void calculateHashTest() throws Exception
    {
        String hash = FederativeAuthentication.calculateHash(EPPN, HASH_KEY);
        System.out.println("Hash=" + hash);
        // compare with known value
        assertEquals(hash, TOKEN);
        // TODO more tests
    }

    @Test
    public void extractUserIdTest() throws Exception
    {
    
        String id = new FederativeAuthentication(ILLEGAL_TRADITIONAL_ID, EPPN+TOKEN).getUserId();
        System.out.println("Id=" + id);
        assertEquals(id, EPPN);
    }

    @Test
    public void extractUserIdWithWrongTokenTest() throws Exception
    {
        String id = null;
        
        id = new FederativeAuthentication(ILLEGAL_TRADITIONAL_ID, "").getUserId();
        assertNull(id);
        
        id = new FederativeAuthentication(ILLEGAL_TRADITIONAL_ID, "rrrrrrrrrrrrrrrrrrrrrrrrrrrr" + TOKEN).getUserId();
        assertNull(id);
        
        id = new FederativeAuthentication(ILLEGAL_TRADITIONAL_ID, EPPN + "ffffffffffffffffffffffffffffffffffffffff").getUserId();
        assertNull(id);
    
        id = new FederativeAuthentication(ILLEGAL_TRADITIONAL_ID, "#" + EPPN + TOKEN).getUserId();
        assertNull(id);
    }

}
