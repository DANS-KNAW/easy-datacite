import java.io.IOException;

import nl.knaw.dans.common.lang.util.StreamUtil;
import nl.knaw.dans.pf.language.ddm.api.Ddm2EmdCrosswalk;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkException;
import nl.knaw.dans.pf.language.xml.exc.XMLSerializationException;

public class DDM2EMD
{
    public void main(final String... args) throws CrosswalkException, IOException, XMLSerializationException
    {
        final String inputXML = new String(StreamUtil.getBytes(System.in));
        final EasyMetadata emd = new Ddm2EmdCrosswalk().createFrom(inputXML);
        final String outputXML = new EmdMarshaller(emd).getXmlString();
        System.out.print(outputXML);
    }
}
