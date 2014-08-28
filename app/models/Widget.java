package models;

import java.util.List;

import play.libs.F.Promise;

public abstract class Widget {
	
	//public abstract List<String> getAcceptedTypes();

	public abstract Promise<String> getJsonData();

}
