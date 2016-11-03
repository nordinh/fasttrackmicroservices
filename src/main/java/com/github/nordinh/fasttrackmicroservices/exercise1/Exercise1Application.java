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
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.client.Entity.entity;

public class Exercise1Application {

    private static Logger logger = Logger.getLogger(Exercise1Application.class.getName());
    private static Feature feature = new LoggingFeature(logger, Level.INFO, null, null);

    public static final JerseyClient CLIENT = JerseyClientBuilder.createClient(new ClientConfig()).register(feature);
    public static final String PAYMENT_REL = "http://relations.restbucks.com/payment";

    public static void main(String[] args) throws Exception {
        new Exercise1Application().run();
    }

    public void run() throws Exception {
        Response orderResponse = orderCoffee();

        String responseAsString = orderResponse.readEntity(String.class);
        logger.info(responseAsString);

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

        logger.info(paymentResponse.readEntity(String.class));
    }

    private String getCost(Document orderResponse) {
        return orderResponse
                .getDocumentElement()
                .getElementsByTagNameNS("http://schemas.restbucks.com", "cost")
                .item(0)
                .getTextContent();
    }

    private Link getPaymentLink(Response orderResponse) {
        return orderResponse.getLink(PAYMENT_REL);
    }

    private String getPaymentURI(Document orderResponse) {
        NodeList links = orderResponse
                .getDocumentElement()
                .getElementsByTagNameNS("http://schemas.restbucks.com/dap", "link");

        for (int i = 0; i < links.getLength(); i++) {
            Node link = links.item(i);
            if (link.getAttributes().getNamedItem("rel").getTextContent().equals(PAYMENT_REL)) {
                return link.getAttributes().getNamedItem("uri").getTextContent();
            }
        }
        throw new IllegalStateException();
    }

    private Document toDocument(String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
    }

}
