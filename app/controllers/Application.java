package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    Map<String, String> widgetIndex = new HashMap<String,String>();
    
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

    public static List<String> getApplicableWidgets(List<String> types) {
        return null;
    }
    
    public static String getJsonData(List<String> widgetIds) {
        return "";
    }
    
    public static Promise<String> getWidgetsData(String path) {
//        List<String> types = extractTypes(path).get(5000);
        
        Promise<List<String>> widgetIds = extractTypes(path).map(
                                                new Function<List<String>, List<String>>() {
                                                    public List<String> apply(List<String> types) {
                                                        return getApplicableWidgets(types);
                                                    }
                                                }
                                            );
        
        Promise<String> jsonData = widgetIds.map(
                new Function<List<String>, String>() {
                    public String apply(List<String> widgetIds) {
                        return getJsonData(widgetIds);
                    }
                }
            );
        
        return jsonData;
    }
    
    public static Promise<Result> index(String path) {
        
//        testJson();
        
        Promise<String> jsonData = getWidgetsData(path);
        
        Promise<Result> wikiPage = WS.url("http://en.wikipedia.org/wiki/" + path).get().map(
                new Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                        return ok(response.getBody()).as("text/html");
                    }
                }
            );
        
        return wikiPage; 
    }

}
