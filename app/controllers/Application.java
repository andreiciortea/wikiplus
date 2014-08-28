package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.LocalTimeWidget;
import models.Widget;

import com.fasterxml.jackson.databind.JsonNode;

import play.*;
import play.libs.ws.WSRequestHolder;
import play.libs.Json;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.*;
import scala.Console;
import views.html.*;

public class Application extends Controller {

    //Map<String, List<String>> widgetIndex = new HashMap<String, List<String>>();
    //widgetIndex.add('LocalTimeWidget', Arrays.asList("PopulatedPlace", "sup2", "sup3"));
    
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
//        List<String> types = extractTypes(path).get(5000);
        
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
                                + jsonData
                                + "</script>";
                    }
                }
            );
    }
    
    public static Result index(String path) {

        Promise<String> jsonData = getWidgetsData(path);
        Promise<String> script = wrapJsonData(jsonData);
        
        String jsScript = script.get(5000);
        
        Promise<String> wikiPage = WS.url("http://en.wikipedia.org/wiki/" + path).get().map(
                new Function<WSResponse, String>() {
                    public String apply(WSResponse response) {
//                        return ok(response.getBody()).as("text/html");
                        return response.getBody();
                    }
                }
            );

        String page = wikiPage.get(5000).replace("</body>", jsScript + "</body>");
        
        return ok(page).as("text/html");
    }

}
