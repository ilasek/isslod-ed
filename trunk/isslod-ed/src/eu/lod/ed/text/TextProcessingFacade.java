package eu.lod.ed.text;

import java.util.List;

import eu.lod.ed.ner.StandfordEntity;

public interface TextProcessingFacade {

	List<StandfordEntity> getEntities(String input);

}