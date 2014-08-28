package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;


public class LocalTimeWidget extends Widget {
	
	public String path = "";
	
	public LocalTimeWidget(String path){
		this.path = path;
	}

	private Promise<JsonNode> getTimeZone(Promise<String> jsonData) {
		// use the json response of dbpedia to query geonames for timezone
	    JsonNode result = Json.parse(jsonData.get(5000));
	    
	    String latitude = result.findPath("lat").get("value").asText();
	    String longitude = result.findPath("long").get("value").asText();
	    
	    String requestUri = "http://api.geonames.org/timezoneJSON";
	    
	    WSRequestHolder holder = WS.url(requestUri)
                .setQueryParameter("lat", latitude)
                .setQueryParameter("lng", longitude)
                .setQueryParameter("username", "andrei.ciortea");
	    
	    Promise<WSResponse> geoResponse = holder.get();
	    
	    return geoResponse.map(
	            new Function<WSResponse, JsonNode>() {
                    public JsonNode apply(WSResponse response) {
                        JsonNode result = Json.parse(response.getBody());
                        return result;
                    }
                }  
            );
	}
	
	private Promise<String> getTimeZoneId(Promise<String> jsonData){
		// get the TimeZoneId from the geoname response
		Promise<JsonNode> tz = getTimeZone(jsonData);
        return tz.map(
        		new Function<JsonNode, String>() {
        			public String apply(JsonNode node) {
        				return node.findValue("timezoneId").asText();
        			}
        		});
	}
	
	private Promise<String> getTimeZoneDst(Promise<String> jsonData){
		// get the DST value from the geoname response
		Promise<JsonNode> tz = getTimeZone(jsonData);
        return tz.map(
        		new Function<JsonNode, String>() {
        			public String apply(JsonNode node) {
        				return node.findValue("dstOffset").asText();
        			}
        		});
	}
	
	public Promise<String> getCoordinates() {
		// use the path to query dbpedia for geocoordinates
	    String resourceUri = "<http://dbpedia.org/resource/" + path + ">";
        String query = "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>"
                    + "SELECT ?lat ?long WHERE {"
                    + resourceUri + " geo:lat ?lat ."
                    + resourceUri + " geo:long ?long ."
                    + "}";
        
        WSRequestHolder holder = WS.url("http://dbpedia.org/sparql")
                .setQueryParameter("format", "json")
                .setQueryParameter("query", query);
        
        Promise<WSResponse> dbpediaResponse = holder.get();
        
        return dbpediaResponse.map(
                new Function<WSResponse, String>() {
                    public String apply(WSResponse response) {
                        return response.getBody();
                    }
                });
	}
	
	
	public String packJsonData(String timezoneId) {
		// preparing the content for the HTML browser
	    ObjectNode widgetData = Json.newObject();
	    
	    widgetData.put("name", "LocalTime");
	    widgetData.put("data", 
	            Json.newObject()
	                    .put("timezone", timezoneId)
            );
	    
	    return widgetData.toString();
	}
	
	public String packRdfData(String timezoneDst) {
		// preparing the content for the RDF browser
		return "http://dbpedia.org/resource/" + path 
				+ " dbpedia-owl:daylightSavingTimeZone <http://dbpedia.org/resource/UTC-"
				+ timezoneDst + "> .";
	    //some Jena magic would be nicer
	}
	
	@Override
	public Promise<String> getJsonData() {
		// this is called from the controller for HTML browsers
	    Promise<String> coordinatesPromise = getCoordinates();
	    Promise<String> timezoneIdPromise = getTimeZoneId(coordinatesPromise);
	    
	    return timezoneIdPromise.map(
	            new Function<String, String>() {
                    public String apply(String timezoneId) {
                        return packJsonData(timezoneId);
                    }
	            }
            );
	}
	
	@Override
	public Promise<String> getRdfData() {
		// this is called from the controller for RDF browsers
	    Promise<String> coordinatesPromise = getCoordinates();
	    Promise<String> timezoneDstPromise = getTimeZoneDst(coordinatesPromise);
	    
	    return timezoneDstPromise.map(
	            new Function<String, String>() {
                    public String apply(String timezoneDst) {
                        return packRdfData(timezoneDst);
                    }
	            }
            );
	}

}
