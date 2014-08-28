package models;

import play.libs.F.Promise;

public abstract class Widget {
	
	public abstract Promise<String> getJsonData();
	
	public abstract Promise<String> getRDFData();

}
