package eu.lod.ed.dbpedia;

import java.util.List;

public class Test {
    
    public static void main(String[] args) {
        DbpEndpoint endpoint = new DbpEndpoint("http://live.dbpedia.org/sparql");
        List<DbpEntity> entities = endpoint.getDbpEntitiesByLabel("Paris Wisconsin");
        
        for (DbpEntity entity : entities) {
            System.out.println(entity.getLabel() + " " + entity.getUri() + " " + entity.getDescription());
        }
    }
    
}
