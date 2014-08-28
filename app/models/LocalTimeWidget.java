package models;

import java.util.ArrayList;
import java.util.List;

public class LocalTimeWidget extends Widget {
	
	public String path = "";
	
	public LocalTimeWidget(String path){
		this.path = path;
		String q = "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>" + 
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
	
	@Override
	public String getJsonData() {
		// TODO Auto-generated method stub
		return null;
	}

}
