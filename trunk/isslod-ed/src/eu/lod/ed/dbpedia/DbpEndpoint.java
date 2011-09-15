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
                                         + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                                         + "PREFIX dbpedia-owl:  <http://dbpedia.org/ontology/>\n";
//                                         + "PREFIX bif:  <bif:contains>\n";

    private String sparqlEndpoint;
    
    public DbpEndpoint(String sparqlEndpoint) {
        this.sparqlEndpoint = sparqlEndpoint;
    }
    
    private static String addPrefixes(String query) {
        return PREFIXES + " " + query;
    }
    
    public List<DbpEntity> getDbpEntitiesByLabel(String label) {
        String query = "SELECT DISTINCT ?uri WHERE { ?uri rdfs:label \"" + label + "\"@en. ?uri rdfs:label ?label. }";
        List<DbpEntity> entities = getQueryResult(query);
        System.out.println(query);
        
        return entities;
    }
    
    private DbpEntity addDescription(DbpEntity entity) {
        String query = "SELECT ?label WHERE { <" + entity.getUri() + "> rdfs:label ?label. FILTER(lang(?label) = \"en\") }";
        
        query = addPrefixes(query);
        Query sparqlQuery = QueryFactory.create(query);
        
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, sparqlQuery);

        ResultSet results = qexec.execSelect();
        
        QuerySolution solution;
        
        boolean first = true;
        while (results.hasNext()) {
            solution = results.nextSolution();
            if (first) {
                entity.setLabel(solution.get("label").toString());
                first = false;
            }
            String description = entity.getDescription();
            if (description == null)
                entity.setDescription(solution.get("label").toString());
            else
                entity.setDescription(description + solution.get("label").toString());
        }
        
        return entity;
    }

    private List<DbpEntity> getQueryResult(String query) {
        List<DbpEntity> entities = new LinkedList<DbpEntity>();

        query = addPrefixes(query);
        Query sparqlQuery = QueryFactory.create(query);
        
        QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, sparqlQuery);

        ResultSet results = qexec.execSelect();
        
        QuerySolution solution;
        while (results.hasNext()) {
            solution = results.nextSolution();
            entities.add(addDescription(solutionToEntity(solution)));
        }
        
        return entities;
    }
    
    private DbpEntity solutionToEntity(QuerySolution solution) {
        DbpEntity entity = new DbpEntity();
        if (solution.get("uri") != null)
            entity.setUri(solution.get("uri").toString());
        if (solution.get("label") != null)        
            entity.setLabel(solution.get("label").toString());
        if (solution.get("type") != null)
            entity.setRdfType(solution.get("type").toString());
        if (solution.get("description") != null)
            entity.setDescription(solution.get("description").toString());
        
        return entity;
    }

}
