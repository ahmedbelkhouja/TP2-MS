package org.example.restwebapp;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.print.attribute.standard.Media;

@Path("/calcul")
public class calculRest {
    private final Calculatrice calc = new Calculatrice();
    @GET
    @Path("/somme")
    @Produces(MediaType.TEXT_PLAIN)
    //@Produces(MediaType.APPLICATION_JSON)
    public Response somme(@QueryParam("a") int a, @QueryParam("b") int b) {
        int result = calc.somme(a, b);
        return Response.ok(result).build();
    }
    @GET
    @Path("/sous-traction")
    @Produces(MediaType.TEXT_PLAIN)
    //@Produces(MediaType.APPLICATION_JSON)

    public Response sousTraction(@QueryParam("a") int a, @QueryParam("b") int b) {
        int result = calc.soustraction(a, b);
        return Response.ok(result).build();
    }
    @GET
    @Path("/produit")
    @Produces(MediaType.TEXT_PLAIN)
    //@Produces(MediaType.APPLICATION_JSON)

    public Response produit(@QueryParam("a") int a, @QueryParam("b") int b) {
        int result = calc.multiply(a, b);
        return Response.ok(result).build();
    }
    @GET
    @Path("/division")
    @Produces(MediaType.TEXT_PLAIN)
    //@Produces(MediaType.APPLICATION_JSON)
    public Response division(@QueryParam("a") int a, @QueryParam("b") int b) {
        if(b==0){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Erreur : division par 0 impossible")
                    .build();
        }
        double result = calc.divide(a, b);
        return Response.ok(result).build();
    }
    @GET
    @Path("/produit/{a}/{b}")
    @Produces(MediaType.TEXT_PLAIN)
    //@Produces(MediaType.APPLICATION_JSON)
    public Response produitChemin(@PathParam("a") int a, @PathParam("b") int b) {
        int result = calc.multiply(a, b);
        return Response.ok(result).build();
    }


}
