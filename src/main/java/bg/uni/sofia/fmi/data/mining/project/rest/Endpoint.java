package bg.uni.sofia.fmi.data.mining.project.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/")
public class Endpoint {

    @POST
    @Path("/sayHello")
    public String sayHello(@QueryParam("searchText") String searchText) {
        return searchText;
    }
}
