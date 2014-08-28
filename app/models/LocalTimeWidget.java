package models;

public class LocalTimeWidget extends Widget {
	
	public String path = "";
	
	public void LocalTimeWidget(String path){
		this.path = path;
		String q = "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>" + 
				"SELECT ?lat ?long WHERE {" + 
				"<http://dbpedia.org/resource/Lyon> geo:lat ?lat ." +
				"<http://dbpedia.org/resource/Lyon> geo:long ?long ." +
				"}";
	}
	
	@Override
	public String getJsonData() {
		// TODO Auto-generated method stub
		return null;
	}

}
