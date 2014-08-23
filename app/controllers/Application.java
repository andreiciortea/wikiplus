package controllers;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import play.*;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.XML;
import play.libs.XPath;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.*;
import scala.Console;
import views.html.*;

public class Application extends Controller {

    public static String cleanifyWikiPage(String pageBody) {
        // we create an XML Document from the existing Wikipedia page
        Document old_page_xml = XML.fromString(pageBody);
        
        // we identify the two notes we want to keep (<title> and <div id="content">)
        Node title_xml = XPath.selectNode("/html/head/title", old_page_xml).cloneNode(true);
        Node content_xml = XPath.selectNode("/html/body/div[@id='content']", old_page_xml).cloneNode(true);
        
        // we create a new XML Document for the new HTML page
        Document new_page_xml = XML.fromString("<!DOCTYPE html>" +
        "<html lang=\"en\" dir=\"ltr\" class=\"client-nojs\">" +
        "<head><meta charset=\"UTF-8\" />" +
        "<link rel=\"stylesheet\" media=\"screen\" href=\"/assets/stylesheets/bootstrap.min.css\"></link>" +
        "<link rel=\"stylesheet\" media=\"screen\" href=\"/assets/stylesheets/results.css\"></link>" +
        "<script src=\"/assets/javascripts/jquery-2.1.1.min.js\"></script>" +
        "<script src=\"/assets/javascripts/main.js\"></script></head>" +
        "<body></body></html>");
        
        // we add the nodes from existing Wikipedia page
        new_page_xml.adoptNode(title_xml);
        XPath.selectNode("/html/head", new_page_xml).appendChild(title_xml);
        new_page_xml.adoptNode(content_xml);
        XPath.selectNode("/html/body", new_page_xml).appendChild(content_xml);
        
        // some magic trick to change XML into text
        try {
              Transformer transformer = TransformerFactory.newInstance().newTransformer();
              StreamResult result = new StreamResult(new StringWriter());
              DOMSource source = new DOMSource(new_page_xml);
              transformer.transform(source, result);
              // here we return the text
              return result.getWriter().toString();
        } catch(TransformerException ex) {
          ex.printStackTrace();
          return null;
        }
    }
    
    public static Promise<Result> index(String path) {
        return WS.url("http://en.wikipedia.org/wiki/" + path).get().map(
                new Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                        String cleanWikiPage = cleanifyWikiPage(response.getBody());
                        
                        if (cleanWikiPage == null) {
                            return internalServerError();
                        }
                        
                        return ok(cleanWikiPage).as("text/html");
                    }
                }
            );
    }

}
