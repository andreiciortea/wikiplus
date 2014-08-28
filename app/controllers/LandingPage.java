package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.landing;

public class LandingPage extends Controller {

	public static Result landing() {
		return ok(landing.render());
	}

}
