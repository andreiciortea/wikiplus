package models;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import scala.Console;


public class LocalTimeWidget extends Widget {
	
	public String path = "";
	
	public LocalTimeWidget(String path){
		this.path = path;
		String queryGeo = "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>" + 
				"SELECT ?lat ?long WHERE {" + 
				"<http://dbpedia.org/resource/Lyon> geo:lat ?lat ." +
				"<http://dbpedia.org/resource/Lyon> geo:long ?long ." +
				"}";
	}

	/*@Override
	public static List<String> getAcceptedTypes() {
		// TODO Auto-generated method stub
		List<String> acceptedTypes = new ArrayList();
		acceptedTypes.add("PopulatedPlace");
		return acceptedTypes;
	}*/
	
	private Promise<String> getTimeZoneId(String jsonData) {
	    JsonNode result = Json.parse(jsonData);
	    
	    String latitude = result.findPath("lat").get("value").asText();
	    String longitude = result.findPath("long").get("value").asText();
	    
//	    Console.println("lat: " + latitude);
//	    Console.println("long: " + longitude);
	    
	    String requestUri = "http://api.geonames.org/timezoneJSON"; //?lat=45.759701&lng=4.842200&username=andrei.ciortea"
	    
	    WSRequestHolder holder = WS.url(requestUri)
                .setQueryParameter("lat", latitude)
                .setQueryParameter("lng", longitude)
                .setQueryParameter("username", "andrei.ciortea");
	    
	    Promise<WSResponse> geoResponse = holder.get();
	    
	    return geoResponse.map(
	            new Function<WSResponse, String>() {
                    public String apply(WSResponse response) {
                        JsonNode result = Json.parse(response.getBody());
                        
                        String id = result.findValue("timezoneId").asText();
                        
//                        Console.println("Timezone Id: " + id);
                        
                        return id;
                    }
                }  
            );
	}
	
	public String packJsonData(String timezoneId) {
	    ObjectNode widgetData = Json.newObject();
	    
	    widgetData.put("name", "LocalTime");
	    widgetData.put("data", 
	            Json.newObject()
	                    .put("timezone", timezoneId)
            );
	    
	    return widgetData.toString();
	}
	
	@Override
	public Promise<String> getJsonData() {
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
        
        return dbpediaResponse.flatMap(
                new Function<WSResponse, Promise<String>>() {
                    public Promise<String> apply(WSResponse response) {
                        return getTimeZoneId(response.getBody()).map(
                                new Function<String, String>() {
                                    public String apply(String timezoneId) {
                                        
//                                        Console.println("Timezone Id: " + timezoneId);
                                        
                                        return packJsonData(timezoneId);
                                    }
                                }
                            );
                    }
                }
            );
	}

}
