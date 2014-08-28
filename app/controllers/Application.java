package controllers;

import play.*;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

    public static Promise<Result> index(String path) {
        return WS.url("http://en.wikipedia.org/" + path).get().map(
                new Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {
                        return ok(response.getBody()).as("text/html");
                    }
                }
            );
    }

}
