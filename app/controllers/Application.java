package controllers;

import java.util.ArrayList;
import java.util.List;

import models.LocalTimeWidget;
import models.Widget;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import play.*;
import play.libs.ws.WSRequestHolder;
import play.libs.Json;
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

    private static List<String> extractTypesJson(String jsonStr) {
        JsonNode results = Json.parse(jsonStr);
        
        return results.findPath("results").findPath("bindings").findValuesAsText("value");
    }
    
    public static Promise<List<String>> extractTypes(String path) {
        String resourceUri = "<http://dbpedia.org/resource/" + path + ">";
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT ?type WHERE { " + resourceUri + " rdf:type ?type . }";
        
        WSRequestHolder holder = WS.url("http://dbpedia.org/sparql")
                .setQueryParameter("format", "json")
                .setQueryParameter("query", query);
        
        Promise<WSResponse> dbpediaResponse = holder.get();
        
        return dbpediaResponse.map(
                new Function<WSResponse, List<String>>() {
                    public List<String> apply(WSResponse response) {
                        return extractTypesJson(response.getBody());
                    }
                }
            );
    }

    public static List<Widget> getApplicableWidgets(List<String> types) {
        // TODO: add the actual implementation
    	List<Widget> list = new ArrayList<Widget>();
    	list.add(new LocalTimeWidget("Lyon"));
    	
        return list;
    }
    
    public static Promise<String> getJsonData(List<Widget> widgets) {
        // TODO: add the actual implementation
        return widgets.get(0).getJsonData().map(
                        new Function<String, String>() {
                            public String apply(String jsonObj) {
                                return "[" + jsonObj + "]";
                            }
                        }
                    );
    }
    
    public static Promise<String> getWidgetsData(String path) {
        Promise<List<Widget>> widgets = extractTypes(path).map(
                                                new Function<List<String>, List<Widget>>() {
                                                    public List<Widget> apply(List<String> types) {
                                                        return getApplicableWidgets(types);
                                                    }
                                                }
                                            );
        
        Promise<String> jsonData = widgets.flatMap(
                new Function<List<Widget>, Promise<String>>() {
                    public Promise<String> apply(List<Widget> widgets) {
                        return getJsonData(widgets);
                    }
                }
            );
        
        return jsonData;
    }
    
    public static Promise<String> wrapJsonData(Promise<String> jsonData) {
        return jsonData.map(
                new Function<String, String>() {
                    public String apply(String jsonData) {
                        return "<script id=\"widgets\" type=\"application/json\">"
                                + "var jsonData = " + jsonData + ";"
                                + "</script>";
                    }
                }
            );
    }
    
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
    
    public static Result index(String path) {

        Promise<String> jsonData = getWidgetsData(path);
        Promise<String> script = wrapJsonData(jsonData);
        
        String jsScript = script.get(5000);
        
        Promise<String> wikiPage = WS.url("http://en.wikipedia.org/wiki/" + path).get().map(
                new Function<WSResponse, String>() {
                    public String apply(WSResponse response) {
                        String cleanWikiPage = cleanifyWikiPage(response.getBody());
                        return cleanWikiPage;
                    }
                }
            );

        String page = wikiPage.get(5000).replace("</body>", jsScript + "</body>");
        
        if (page == null) {
            return internalServerError();
        }
        
        return ok(page).as("text/html");
    }

}
