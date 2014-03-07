package nl.knaw.dans.easy.domain.deposit.discipline;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EasyMetadataFactory;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;

import org.junit.Test;

public class ArchisCollectorOnlineTest
{

    // @Ignore("Archis server returns an errorpage (wich isn't even xhtml).")
    @Test
    public void collectInfo() throws Exception
    {
        EasyMetadata emd = EasyMetadataFactory.newEasyMetadata(MetadataFormat.ARCHAEOLOGY);
        ArchisCollector collector = new ArchisCollector(emd);
        collector.collectInfo(new BasicIdentifier("123"));

        System.out.println(new EmdMarshaller(emd).getXmlString());
        // String expected =
        // "<?xml version=\"1.0\" standalone=\"yes\"?><emd:easymetadata xmlns:emd=\"http://easy.dans.knaw.nl/easy/easymetadata/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:eas=\"http://easy.dans.knaw.nl/easy/easymetadata/eas/\" emd:version=\"0.1\"><emd:creator><dc:creator>RAAP Archeologisch Adviesbureau</dc:creator></emd:creator><emd:subject><dc:subject>Archeologisch: geofysisch onderzoek</dc:subject></emd:subject><emd:description><dc:description>Archeologisch haalbaarheidsonderzoek op een deel van De Hoge Woerd. Aan de westzijde wordt het terrein begrensd door de Castellumlaan, aan de zuidzijde door de bebouwde kom van De Meern en aan de noordzijde door de Groene Dijk. Het gebied is op dit moment in gebruik als laagstam-boomgaard. Binnen de begrenzing van het onderzoeksgebied ligt de begrenzing van het Romeinse castellum, en de resten van de vicus. Het archeologisch onderzoek is uitgevoerd ten behoeve van de inpassing van de archeologische resten in de inrichtingsplannen. Het bestond uit geofysisch onderzoek en het zetten van boringen. Literatuur: Jager, D.H. de, 1999: Castellum Hoge Woerd, gemeente Vleuten-De Meern; archeologisch haalbaarheidsonderzoek. RAAP-briefrapport 1999-1479/MW.</dc:description></emd:description><emd:date><eas:created eas:scheme=\"W3CDTF\" eas:format=\"DAY\">1999-06-01T00:00:00.000+02:00</eas:created></emd:date><emd:identifier><dc:identifier eas:scheme=\"Archis OMG_NR\" eas:identification-system=\"http://archis2.archis.nl\">3000</dc:identifier></emd:identifier><emd:coverage><dcterms:spatial>31H</dcterms:spatial><dcterms:spatial>De Hoge Woerd</dcterms:spatial><dcterms:spatial>De Meern</dcterms:spatial><dcterms:spatial>Utrecht</dcterms:spatial><dcterms:spatial>Utrecht</dcterms:spatial><eas:spatial><eas:place>Utrecht</eas:place><eas:point eas:scheme=\"RD\"><eas:x>131397.0</eas:x><eas:y>455419.0</eas:y></eas:point></eas:spatial></emd:coverage></emd:easymetadata>";
        // assertEquals(expected, emd.asXMLString());
    }

}
