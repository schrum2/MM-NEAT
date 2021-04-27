package jackson.serial;

import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

public class Example {
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Tester {

        int[][] stuff;
    }

    public static void main(String[] args) throws JAXBException, JsonProcessingException {
        Tester tester = new Tester();

        tester.stuff = new int[][]{{1, 2}, {3, 4}};

        StringWriter writer = new StringWriter();

        XmlMapper xmlMapper = new XmlMapper();
        ObjectMapper objectMapper = new ObjectMapper();
        xmlMapper.registerModule(new JaxbAnnotationModule());

        System.out.println(xmlMapper.writeValueAsString(tester));
    }
}