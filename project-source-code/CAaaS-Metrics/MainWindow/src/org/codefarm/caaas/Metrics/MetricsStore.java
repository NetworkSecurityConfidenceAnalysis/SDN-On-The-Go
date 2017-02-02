package org.codefarm.caaas.Metrics;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class MetricsStore {

   private static final Logger log = Logger.getLogger(MetricsStore.class.getName());

   private Document opInventoryNodes = null;

   public MetricsStore() {
   }

   public static List<String> getNodeNames(Document nodes) {
      List<String> nodeNames = new ArrayList<String>();

      return nodeNames;
   }

   public static void getNodeById(Document document) {
      NodeList nodeList = document.getDocumentElement().getChildNodes();

      for (int i = 0; i < nodeList.getLength(); i++) {
         Node node = nodeList.item(i);
         if (node instanceof Element) {
            String id = node.getAttributes().getNamedItem("id").getNodeValue();
            System.out.println("value for node id: " + id);
            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
               Node cNode = childNodes.item(j);

               if (cNode instanceof Element) {
                  String content = cNode.getLastChild().getTextContent().trim();
                  switch (cNode.getNodeName()) {
                     case "OpenFlow:1":
                        System.out.println("found OpenFlow:1");
                        break;
                     case "OpenFlow:2":
                        System.out.println("found OpenFlow:2");
                        break;
                     case "OpenFlow:3":
                        System.out.println("found OpenFlow:3");
                        break;
                     case "OpenFlow:4":
                        System.out.println("found OpenFlow:4");
                        break;
                  }
               }
            }
         }
      }
 //     return new Node();
   }

   public static Document getOpInventoryNodes() {
      String opInventoryNodesUrl = "http://10.0.0.8:8181/restconf/operational/opendaylight-inventory:nodes/";
      CloseableHttpClient httpclient = HttpClients.createDefault();
      Document document = null;

      HttpGet httpget = new HttpGet(opInventoryNodesUrl);
      httpget.setHeader("Accept", "application/xml; charset=utf-8");
      System.out.println("Executing request " + httpget.getRequestLine());

      ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
         @Override
         public String handleResponse(
                 final HttpResponse response) throws ClientProtocolException, IOException {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
               HttpEntity entity = response.getEntity();
               return entity != null ? EntityUtils.toString(entity) : null;
            } else {
               throw new ClientProtocolException("Unexpected response status: " + status);
            }
         }
      };

      try {
         String responseBody = httpclient.execute(httpget, responseHandler);
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         InputSource is = new InputSource();
         is.setCharacterStream(new StringReader(responseBody));
         document = dBuilder.parse(is);
         document.getDocumentElement().normalize();
         httpclient.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return document;
   }

//   public static Document getNodes(Document document) {
//      String nodesUrl = "http://10.0.0.8:8181/restconf/operational/opendaylight-inventory:nodes/";
//   }
//   public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
   public static void printDocument(Document doc, OutputStream out) {
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer = null;
      try {
         transformer = tf.newTransformer();
      } catch (TransformerConfigurationException ex) {
         Exceptions.printStackTrace(ex);
      }
      if (transformer != null) {
         transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
         transformer.setOutputProperty(OutputKeys.METHOD, "xml");
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
         transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

         try {
            transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(out, "UTF-8")));
         } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
         } catch (TransformerException ex) {
            Exceptions.printStackTrace(ex);
         }
      }
   }

}
//       String urlString = null;
//       urlString = "http://10.0.0.8:8181/restconf/operational/opendaylight-inventory:nodes/";
//       urlString = "http://10.0.0.8:8181/restconf/operational/opendaylight-inventory:nodes/node/openflow:1";
//       String out = doGet(urlString);
//       JSONObject jsonObj = getJSONObjectFromURL(urlString);
//       Map<String, String> map = getMapFromJSONObject(jsonObj);
//
//
//
//   private String doGet(String urlString) {
//      CloseableHttpClient httpclient = HttpClients.createDefault();
//      try {
//         HttpGet httpget = new HttpGet(urlString);
//         httpget.setHeader("Accept", "application/xml; charset=utf-8");
//         System.out.println("Executing request " + httpget.getRequestLine());
//
//         ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
//            @Override
//            public String handleResponse(
//                    final HttpResponse response) throws ClientProtocolException, IOException {
//               int status = response.getStatusLine().getStatusCode();
//               if (status >= 200 && status < 300) {
//                  HttpEntity entity = response.getEntity();
//                  return entity != null ? EntityUtils.toString(entity) : null;
//               } else {
//                  throw new ClientProtocolException("Unexpected response status: " + status);
//               }
//            }
//         };
//         String responseBody = httpclient.execute(httpget, responseHandler);
//         System.out.println("----------------------------------------");
//         System.out.println(responseBody);
//
//         try {
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            InputSource is = new InputSource();
//            is.setCharacterStream(new StringReader(responseBody));
//
//            Document document = dBuilder.parse(is);
//            document.getDocumentElement().normalize();
//            printDocument(document, System.out);
//         } catch (Exception e) {
//            e.printStackTrace();
//         }
//
//         httpclient.close();
//      } catch (IOException ex) {
//         Exceptions.printStackTrace(ex);
//      }
//      String str = "";
//      return str;
//   }
//responseBody = "<employees>\n" +
//"  <employee id=\"111\">\n" +
//"    <firstName>Rakesh</firstName>\n" +
//"    <lastName>Mishra</lastName>\n" +
//"    <location>Bangalore</location>\n" +
//"  </employee>\n" +
//"  <employee id=\"112\">\n" +
//"    <firstName>John</firstName>\n" +
//"    <lastName>Davis</lastName>\n" +
//"    <location>Chennai</location>\n" +
//"  </employee>\n" +
//"  <employee id=\"113\">\n" +
//"    <firstName>Rajesh</firstName>\n" +
//"    <lastName>Sharma</lastName>\n" +
//"    <location>Pune</location>\n" +
//"  </employee>\n" +
//"</employees>";
//
//   public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
//      TransformerFactory tf = TransformerFactory.newInstance();
//      Transformer transformer = tf.newTransformer();
//      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
//      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
//      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
//
//      transformer.transform(new DOMSource(doc),
//              new StreamResult(new OutputStreamWriter(out, "UTF-8")));
//   }
//private void doGet(String urlString) {
//      CloseableHttpClient httpclient = HttpClients.createDefault();
//      HttpGet httpGet = new HttpGet(urlString);
//      try {
//         CloseableHttpResponse response1 = httpclient.execute(httpGet);
//         System.out.println(eol + "doGet Response status line" + eol);
//         System.out.println(response1.getStatusLine());
//         HttpEntity entity1 = response1.getEntity();
//         System.out.println(eol + "doGet Response" + eol);
//         System.out.println(entity1.getContent());
//
//         EntityUtils.consume(entity1);
//         if (response1 != null) {
//            response1.close();
//         }
//      } catch (IOException ex) {
//         Exceptions.printStackTrace(ex);
//      }
//   }
