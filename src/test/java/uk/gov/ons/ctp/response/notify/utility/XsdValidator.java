package uk.gov.ons.ctp.response.notify.utility;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.notify.endpoint.ManualTestEndpoint;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

import static uk.gov.ons.ctp.response.notify.endpoint.ManualTestEndpoint.buildTestData;

/**
 * A utility class to quickly validate a Java object versus .xsd
 *
 * If there is a validation issue, an exception will be thrown.
 */
public class XsdValidator {
  public static void main(String[] args) throws JAXBException, SAXException {
    File locationOfMySchema = new File("/tmp/test/actionInstruction.xsd");

    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = schemaFactory.newSchema(locationOfMySchema);

    JAXBContext jc = JAXBContext.newInstance(ActionInstruction.class);
    Marshaller marshaller = jc.createMarshaller();
    marshaller.setSchema(schema);
    marshaller.marshal(ManualTestEndpoint.buildActionInstruction(buildTestData(), true), new DefaultHandler());
  }
}