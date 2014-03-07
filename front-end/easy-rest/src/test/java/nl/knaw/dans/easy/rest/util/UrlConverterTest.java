package nl.knaw.dans.easy.rest.util;

import org.junit.Test;

public class UrlConverterTest
{

    @Test(expected = AssertionError.class)
    public void notInstantiable()
    {
        new UrlConverter();
    }

}
