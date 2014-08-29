package models;

import java.text.DecimalFormat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;

public class LocalWeatherWidget extends Widget {
	
	public String path = "";
	
	public LocalWeatherWidget(String path){
		this.path = path;
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
	
	public Promise<JsonNode> getWeather(){
		// use the json response of dbpedia to query openweathermap
		Promise<String> jsonData = getCoordinates();
		JsonNode result = Json.parse(jsonData.get(20000));
	    
	    String latitude = result.findPath("lat").get("value").asText();
	    String longitude = result.findPath("long").get("value").asText();
	    
		String requestUri = "http://api.openweathermap.org/data/2.5/weather";
		WSRequestHolder holder = WS.url(requestUri)
                .setQueryParameter("lat", latitude)
                .setQueryParameter("lon", longitude)
                .setQueryParameter("APPID", "703b5ab6ba987ae43181390329315f3f");
		
		Promise<WSResponse> weatherResponse = holder.get();
	    
	    return weatherResponse.map(
	            new Function<WSResponse, JsonNode>() {
                    public JsonNode apply(WSResponse response) {
                        JsonNode result = Json.parse(response.getBody());
                        return result;
                    }
                }  
            );
	}

	@Override
	public String getJsonData() {
		// this is called from the controller for HTML browsers
		JsonNode jsonData = getWeather().get(20000);

		String icon_id = jsonData.findValue("icon").asText();
		double celsius = Double.parseDouble(jsonData.findValue("temp").asText()) - 273.15;
		
		ObjectNode jsonOutput = Json.newObject();
		jsonOutput.put("name", "LocalWeather");
		jsonOutput.put("data", 
	            Json.newObject()
	            	.put("city", this.path)
					.put("icon", "http://openweathermap.org/img/w/" + icon_id + ".png")
					.put("description", jsonData.findValue("description").asText())
					.put("temp", new DecimalFormat("#.00").format(celsius))
					);
	    
		return jsonOutput.toString();
	}

	@Override
	public String getRdfData() {
		// this is called from the controller for RDF browsers
		JsonNode jsonData = getWeather().get(20000);

		String icon_id = jsonData.findValue("icon").asText();
		double celsius = Double.parseDouble(jsonData.findValue("temp").asText()) - 273.15;
		
		String rdfOutput = "";
		rdfOutput += "<http://dbpedia.org/resource/" + path + "> <http://exemple.com#hasIcon> "
				+ "<http://openweathermap.org/img/w/" + icon_id + ".png> .\n"
				+ "<http://dbpedia.org/resource/" + path + "> <http://exemple.com#hasDescription> \""
				+ jsonData.findValue("description").asText() + "\" .\n"
				+ "<http://dbpedia.org/resource/" + path + "> <http://exemple.com#hasTemp> \""
				+ new DecimalFormat("#.00").format(celsius) + "\" .";
		
		return rdfOutput;
	}

}
