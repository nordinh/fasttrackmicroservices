package com.github.nordinh.fasttrackmicroservices.exercise1;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.client.Entity.entity;
import static org.glassfish.jersey.logging.LoggingFeature.Verbosity.PAYLOAD_ANY;

public class Exercise1Application {

    private static Logger logger = Logger.getLogger(Exercise1Application.class.getName());
    private static Feature feature = new LoggingFeature(logger, Level.INFO, PAYLOAD_ANY, null);

    public static final JerseyClient CLIENT = JerseyClientBuilder.createClient(new ClientConfig()).register(feature);
    public static final String PAYMENT_REL = "http://relations.restbucks.com/payment";

    public static void main(String[] args) throws Exception {
        new Exercise1Application().run();
    }

    public void run() throws Exception {
        Response orderResponse = orderCoffee();

        String responseAsString = orderResponse.readEntity(String.class);

        Document orderResponseDocument = toDocument(responseAsString);
        payCoffee(getCost(orderResponseDocument), getPaymentURI(orderResponseDocument));

    }

    private Response orderCoffee() {
        String order = "<order xmlns=\"http://schemas.restbucks.com\">\n" +
                "<item>\n" +
                "<milk>whole</milk>\n" +
                "<size>large</size>\n" +
                "<drink>\n" +
                "espresso\n" +
                "</drink>\n" +
                "</item>\n" +
                "<location>takeaway</location>\n" +
                "</order>";

        return CLIENT.target("http://172.16.5.55:8080/order")
                .request("application/vnd.restbucks+xml")
                .post(entity(order, "application/vnd.restbucks+xml"));
    }

    private void payCoffee(String cost, String paymentURI) {
        String payment = "<payment xmlns=\"http://schemas.restbucks.com\">\n" +
                "<amount>" + cost + "</amount>\n" +
                "<cardholderName>John</cardholderName>\n" +
                "<cardNumber>12345678</cardNumber>\n" +
                "<expiryMonth>10</expiryMonth>\n" +
                "<expiryYear>19</expiryYear>\n" +
                "</payment>";

        Response paymentResponse = CLIENT.target(paymentURI)
                .request("application/vnd.restbucks+xml")
                .put(entity(payment, "application/vnd.restbucks+xml"));
    }

    private String getCost(Document orderResponse) throws Exception {
        return (String) XPathFactory.newInstance().newXPath()
                .compile("//*[local-name()='order']/*[local-name()='cost']")
                .evaluate(orderResponse, XPathConstants.STRING);
    }

    private String getPaymentURI(Document orderResponse) throws Exception {
        Node link =  (Node) XPathFactory.newInstance().newXPath()
                .compile("//*[local-name()='order']/*[local-name()='link'][@*[local-name() = 'rel' and .='http://relations.restbucks.com/payment']]")
                .evaluate(orderResponse, XPathConstants.NODE);

        return link.getAttributes().getNamedItem("uri").getTextContent();
    }

    private Document toDocument(String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
    }

}
