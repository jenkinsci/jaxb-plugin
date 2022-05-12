package io.jenkins.plugins.jaxb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.RealJenkinsRule;

public class JAXBContextTest {

    @Rule public RealJenkinsRule rr = new RealJenkinsRule();

    @Test
    public void smokes() throws Throwable {
        rr.then(JAXBContextTest::_smokes);
    }

    private static void _smokes(JenkinsRule r) throws Throwable {
        Book book = new Book();
        book.setId(1L);
        book.setName("Guide to JAXB");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JAXBContext context = getJAXBContext(Book.class);
        context.createMarshaller().marshal(book, baos);
        String xml = baos.toString(StandardCharsets.US_ASCII.name());
        assertThat(xml, containsString("<book id=\"1\"><title>Guide to JAXB</title></book>"));
        Book book2 = (Book) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(xml.getBytes(StandardCharsets.US_ASCII)));
        assertEquals(book.getId(), book2.getId());
        assertEquals(book.getName(), book2.getName());
    }

    private static JAXBContext getJAXBContext(Class<?>... classesToBeBound) throws JAXBException {
        Thread t = Thread.currentThread();
        ClassLoader orig = t.getContextClassLoader();
        t.setContextClassLoader(RealJenkinsRule.Endpoint.class.getClassLoader());
        try {
            return JAXBContext.newInstance(classesToBeBound);
        } finally {
            t.setContextClassLoader(orig);
        }
    }

    @XmlRootElement(name = "book")
    @XmlType(propOrder = {"id", "name"})
    static class Book {
        private Long id;
        private String name;

        @XmlAttribute
        public void setId(Long id) {
            this.id = id;
        }

        @XmlElement(name = "title")
        public void setName(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
