/**
 * Copyright (C) 2014 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.easy;

import org.junit.Test;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import static org.junit.Assert.fail;

/**
 * Created by linda on 23-04-17. just add emd files as needed. make sure to run the tests when changing the xslt file
 */
public class DataciteXmlValidatorTest {

    private static final DataciteServiceConfiguration dsConfig = new DataciteServiceConfiguration();
    private static final String version = dsConfig.getXslVersion();
    private static final String XSL_EMD2DATACITE = String.format("xslt-files/EMD_doi_datacite_v%s.xsl", version);

    @Test
    public void validate_emd_66365() throws Exception {
        validateSingleEmdConversion("emd_66365.xml");
    }

    @Test
    public void validate_emd_62559() throws Exception {
        validateSingleEmdConversion("emd_62559.xml");
    }

    @Test
    public void validate_incomplete_relations_emd() throws Exception {
        validateSingleEmdConversion("incomplete-relations-emd.xml");
    }

    @Test
    public void validate_maxi_emd() throws Exception {
        validateSingleEmdConversion("maxi-emd.xml");
    }

    /*
     * Convert an emdfile to datacite-format, and validate. Fail when the validation fails according to the xsd specified in schemaFile.
     */
    private void validateSingleEmdConversion(String emdfile) throws Exception {
        String dataciteXmltoBeValidated = new DataciteResourcesBuilder(XSL_EMD2DATACITE, new URL("http://some.domain/and/path"))
                .getEmd2DataciteXml(new EmdBuilder(emdfile).build());

        URL schemaFile = new URL(String.format("http://schema.datacite.org/meta/kernel-%s/metadata.xsd", version));
        Source xmlFile = new StreamSource(new StringReader(dataciteXmltoBeValidated));

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            validator.validate(xmlFile);
        }
        catch (SAXException e) {
            fail(emdfile + " conversion is NOT valid reason:" + e);
        }
        catch (IOException e) {
            fail(emdfile + " conversion is NOT valid reason:" + e);
        }
    }

}
