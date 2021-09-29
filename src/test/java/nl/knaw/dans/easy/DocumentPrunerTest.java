/*
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class DocumentPrunerTest {

    private static Document convertStringToDocument(String xmlStr) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        StringReader reader = new StringReader(xmlStr);
        try {
            builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(reader));
        }
        finally {
            reader.close();
        }
    }

    private static String convertDocumentToString(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        StringWriter writer = new StringWriter();
        try {
            tf.newTransformer().transform(new DOMSource(doc), new StreamResult(writer));
            return writer.getBuffer().toString();
        }
        finally {
            writer.close();
        }
    }

    @Test
    public void testPruneDocumentWithNoEmptyFields() throws Exception {
        String xmlInput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<person id=\"1\">" + "<name>foobar</name>" + "<age>-33</age>" + "</person>";

        Document input = convertStringToDocument(xmlInput);
        DocumentPruner pruner = new DocumentPruner(input);
        Document output = pruner.prune();
        String xmlOutput = convertDocumentToString(output);

        assertEquals(xmlInput, xmlOutput);
    }

    @Test
    public void testPruneDocumentWithEmptyField() throws Exception {
        String xmlInput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<person id=\"1\">" + "<name>foobar</name>" + "<age></age>" + "</person>";
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<person id=\"1\">" + "<name>foobar</name>" + "</person>";

        Document input = convertStringToDocument(xmlInput);
        DocumentPruner pruner = new DocumentPruner(input);
        Document output = pruner.prune();
        String xmlOutput = convertDocumentToString(output);

        assertEquals(expected, xmlOutput);
    }
}
