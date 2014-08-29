package controllers;

import java.util.ArrayList;
import java.util.List;

import models.LocalTimeWidget;
import models.LocalWeatherWidget;
import models.Widget;

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
import views.html.*;

public class Application extends Controller {

    public static Promise<List<String>> extractTypes(String path) {
        String resourceUri = "<http://dbpedia.org/resource/" + path + ">";
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT ?type WHERE { " 
                        + resourceUri + " rdf:type ?type . }";
        
        WSRequestHolder holder = WS.url("http://dbpedia.org/sparql")
                .setQueryParameter("format", "json")
                .setQueryParameter("query", query);
        
        Promise<WSResponse> dbpediaTypesPromise = holder.get();
        
        return dbpediaTypesPromise.map(
                new Function<WSResponse, List<String>>() {
                    public List<String> apply(WSResponse response) {
                        try {
                            return Json.parse(response.getBody()).findPath("results")
                                            .findPath("bindings")
                                            .findValuesAsText("value");
                        } catch (Exception e) {
                            return new ArrayList<String>();
                        }
                    }
                }
            );
    }

    public static List<Widget> getFilteredWidgetList(String path, List<String> types) {
    	List<Widget> list = new ArrayList<Widget>();

    	for (String t: types) {
    	    if (t.compareTo("http://dbpedia.org/ontology/PopulatedPlace") == 0) {
    	        list.add(new LocalTimeWidget(path));
    	        list.add(new LocalWeatherWidget(path));
    	    }
    	}
    	
        return list;
    }
    
    public static List<Widget> getWidgetsForResource(String path) {
        List<String> types = extractTypes(path).get(20000);
        return getFilteredWidgetList(path, types);
    }
    
    public static String getWidgetDataAsJson(List<Widget> widgets) {
        
        if (widgets.size() == 0) return "[]";
        
        String jsonDataStr = "[";
        
        for (Widget w : widgets) {
            jsonDataStr += w.getJsonData() + ",";
        }

        return jsonDataStr = jsonDataStr.substring(0, jsonDataStr.length() - 1) + "]";
    }
    
    public static String getWidgetDataAsTurtle(List<Widget> widgets) {
        String turtleStr = "";
        
        for (Widget w : widgets) {
            turtleStr += w.getRdfData() + "\n";
        }
        
        return turtleStr;
    }
    
    public static String wrapWidgetDataInJSScript(String jsonData) {
        return "<script id=\"widgets\">"
                + "var jsonData = " + jsonData + ";"
                + "</script>";
    }
    
    // TODO: refactor using Play
    public static String cleanifyWikiPage(String pageBody) {
        // we create an XML Document from the existing Wikipedia page
        Document oldPageXml = XML.fromString(pageBody);
        
        // we identify the two notes we want to keep (<title> and <div id="content">)
        Node titleXml = XPath.selectNode("/html/head/title", oldPageXml).cloneNode(true);
        Node contentXml = XPath.selectNode("/html/body/div[@id='content']", oldPageXml).cloneNode(true);
        
        // we create a new XML Document for the new HTML page
        Document newPageXml = XML.fromString("<!DOCTYPE html>" +
            "<html lang=\"en\" dir=\"ltr\" class=\"client-nojs\">" +
            "<head><meta charset=\"UTF-8\" />" +
            "<link rel=\"stylesheet\" media=\"screen\" href=\"/assets/stylesheets/bootstrap.min.css\"></link>" +
            "<link rel=\"stylesheet\" media=\"screen\" href=\"/assets/stylesheets/results.css\"></link>" +
            "<link rel=\"stylesheet\" media=\"screen\" href=\"/assets/stylesheets/wikimedia.css\"></link>" +
            "<link rel=\"stylesheet\" media=\"screen\" href=\"/assets/stylesheets/wikimediaBits.css\"></link>" +
            "<script src=\"/assets/javascripts/jquery-2.1.1.min.js\"></script>" +
            "<script src=\"/assets/javascripts/moment.min.js\"></script>" +
            "<script src=\"/assets/javascripts/moment-timezone.js\"></script>" +
            "<script src=\"/assets/javascripts/main.js\"></script>" +
            "<script src=\"/assets/javascripts/weatherWidget.js\"></script>" +
            "<script src=\"/assets/javascripts/clockWidget.js\"></script></head>" +
            "<body></body></html>");
        
        // we add the nodes from existing Wikipedia page
        newPageXml.adoptNode(titleXml);
        XPath.selectNode("/html/head", newPageXml).appendChild(titleXml);
        newPageXml.adoptNode(contentXml);
        XPath.selectNode("/html/body", newPageXml).appendChild(contentXml);
        
        
        
        // some magic trick to change XML into text
        try {
              Transformer transformer = TransformerFactory.newInstance().newTransformer();
              StreamResult result = new StreamResult(new StringWriter());
              DOMSource source = new DOMSource(newPageXml);
              transformer.transform(source, result);
              // here we return the text
              return result.getWriter().toString();
        } catch(TransformerException ex) {
          ex.printStackTrace();
          return null;
        }
    }

    // TODO: async
    public static Result index(String path) {

        if (request().accepts("text/html")) {
            // Get widget data as a serialized JSON array.
            String widgetJsonArray = getWidgetDataAsJson(getWidgetsForResource(path));
            // Wrap it up for injection in the HTML representation.
            String widgetDataScript = wrapWidgetDataInJSScript(widgetJsonArray);
            
            Promise<String> wikiPage = WS.url("http://en.wikipedia.org/wiki/" + path).get().map(
                    new Function<WSResponse, String>() {
                        public String apply(WSResponse response) {
                            String cleanWikiPage = cleanifyWikiPage(response.getBody());
                            return cleanWikiPage;
                        }
                    }
                );
            
            // TODO: catch timeout exception
            String wikiPlusPage = wikiPage.get(20000).replace("</body>", widgetDataScript + "</body>");
            
            if (wikiPlusPage == null) {
                return internalServerError();
            }
            
            // Add the right MIME type.
            return ok(wikiPlusPage).as("text/html");
            
        } else if (request().accepts("text/turtle")) {
            return ok(getWidgetDataAsTurtle(getWidgetsForResource(path))).as("text/turtle");
        }
        
        return badRequest();
    }

}
