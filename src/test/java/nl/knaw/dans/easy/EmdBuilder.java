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

import java.io.File;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.binding.EmdUnmarshaller;

import org.apache.commons.io.FileUtils;

public class EmdBuilder {

    private static final String DEFAULT_EMD = "emd.xml";
    private String xml;

    public EmdBuilder() throws Exception {
        this(DEFAULT_EMD);
    }

    public EmdBuilder(String fileName) throws Exception {
        xml = FileUtils.readFileToString(new File(EmdBuilder.class.getResource("/" + fileName).toURI()), "UTF-8");
    }

    public EasyMetadata build() throws Exception {
        return new EmdUnmarshaller<EasyMetadata>(EasyMetadataImpl.class).unmarshal(xml);
    }

    public EmdBuilder replaceAll(String search, String replace) throws Exception {
        xml = xml.replaceAll(search, replace);
        return this;
    }
}
