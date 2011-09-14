package eu.lod.ed.dbpedia;

import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class DbpEndpoint {
    private static final String PREFIXES = "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\n"
                                         + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
//                                         + "PREFIX bif:  <bif:contains>\n";

    private String sparqlEndpoint;
    
    public DbpEndpoint(String sparqlEndpoint) {
        this.sparqlEndpoint = sparqlEndpoint;
    }
    
    private static String addPrefixes(String query) {
        return PREFIXES + " " + query;
    }
    
    public List<DbpEntity> getDbpEntitiesByLabel(String label) {
        List<DbpEntity> entities = new LinkedList<DbpEntity>();
        String query = "SELECT ?uri ?label ?type WHERE { ?uri rdfs:label ?label. ?label <bif:contains> \"" + label + "\" OPTIONAL {?uri rdf:type ?type} }";
//        String query = "SELECT ?uri ?label WHERE { ?uri rdfs:label \"" + label + "\" } LIMIT 1";
        query = addPrefixes(query);
        System.out.println(query);
        System.out.println();

        Query sparqlQuery = QueryFactory.create(query);
        
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, sparqlQuery);

        ResultSet results = qexec.execSelect();
        
        DbpEntity entity;
        QuerySolution sol;
        while (results.hasNext()) {
            sol = results.nextSolution();
            entity = new DbpEntity();
            entity.setLabel(sol.get("label").toString());
            entity.setUri(sol.get("uri").toString());
            if (sol.get("type") != null)
                entity.setRdfType(sol.get("type").toString());
            entities.add(entity);
        }
        
        return entities;
    }
    
//    private String 

}
