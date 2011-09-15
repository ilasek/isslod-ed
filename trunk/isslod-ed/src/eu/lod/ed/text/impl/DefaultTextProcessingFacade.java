package eu.lod.ed.text.impl;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.BeginPositionAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.EndPositionAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;
import eu.lod.ed.ner.StandfordEntity;
import eu.lod.ed.text.TextProcessingFacade;
import eu.lod.stemmer.PorterStemmer;
import eu.lod.stopwords.Stopwords;

public class DefaultTextProcessingFacade implements TextProcessingFacade {

	private AbstractSequenceClassifier classifier;
	private PorterStemmer stemmer;

	public DefaultTextProcessingFacade(InputStream classifierStream) {
		try {
			classifier = CRFClassifier.getClassifier(classifierStream);
		} catch (Exception e) {
			throw new RuntimeException(
					"Can't restore classifier from serialized state", e);
		} finally {
			try {
				classifierStream.close();
			} catch (IOException e) {
			}
		}
		// init stemmer
		stemmer = new PorterStemmer();
	}

	@Override
	public List<StandfordEntity> getEntities(String input) {
		List<StandfordEntity> resultList = new LinkedList<StandfordEntity>();

		List<List<CoreLabel>> sentences = classifier.classify(input);
		for (List<CoreLabel> sentence : sentences) {
			String prevEntityType = classifier.flags.backgroundSymbol;
			StandfordEntity prevEntity = null;

			for (CoreLabel label : sentence) {
				String guessedAnswer = label.get(AnswerAnnotation.class);
				if (guessedAnswer.equals(classifier.flags.backgroundSymbol)) {
					if (prevEntity != null) {
						resultList.add(prevEntity);
						prevEntity = null;
					}
				} else {
					if (!guessedAnswer.equals(prevEntityType)) {
						if (prevEntity != null) {
							resultList.add(prevEntity);
						}
						prevEntity = new StandfordEntity();
						prevEntity.setType(guessedAnswer);
						prevEntity.setStart(label
								.get(BeginPositionAnnotation.class));
						prevEntity.setEnd(label
								.get(EndPositionAnnotation.class));
						prevEntity.setName(label.word());
					} else {
						prevEntity.setEnd(label
								.get(EndPositionAnnotation.class));
						prevEntity.setName(prevEntity.getName() + ' '
								+ label.word());
					}
				}
				prevEntityType = guessedAnswer;
			}

			// include any entity at end of doc
			if (prevEntity != null) {
				resultList.add(prevEntity);
			}
		}
		return resultList;
	}

	@Override
	public List<String> listStems(String input) {
		StringReader inputReader = new StringReader(input);
		PTBTokenizer<Word> tokenizer = PTBTokenizer
				.newPTBTokenizer(inputReader);
		List<String> result = new LinkedList<String>();
		while (tokenizer.hasNext()) {
			Word curWord = tokenizer.next();
			String wordStr = curWord.word();
			if (wordStr == null || wordStr.trim().length() < 2) {
				continue;
			}
			if (Stopwords.isStopword(wordStr)) {
				continue;
			}
			result.add(stemmer.stemWord(wordStr));
		}
		return result;
	}

	public static void main(String[] args) {
		String input = "Carl Edward Sagan (November 9, 1934 – December 20, 1996) "
				+ "was an American astronomer, astrophysicist, cosmologist, author, science popularizer, "
				+ "and science communicator in the space and natural sciences.";
		try {
			InputStream is = new GZIPInputStream(
					new BufferedInputStream(
							new FileInputStream(
									"WebContent/classifiers/ner-eng-ie.crf-4-conll.ser.gz")));
			DefaultTextProcessingFacade tf = new DefaultTextProcessingFacade(is);
			System.out.println(tf.listStems(input));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * public static void main(String[] args) { try { String input =
	 * "Carl Edward Sagan (November 9, 1934 – December 20, 1996) " +
	 * "was an American astronomer, astrophysicist, cosmologist, author, science popularizer, "
	 * + "and science communicator in the space and natural sciences.";
	 * InputStream is = new GZIPInputStream(new BufferedInputStream( new
	 * FileInputStream( "classifiers/ner-eng-ie.crf-4-conll.ser.gz")));
	 * DefaultTextProcessingFacade tf = new DefaultTextProcessingFacade(is);
	 * 
	 * System.out.println(tf.getEntities(input)); } catch (Exception e) { throw
	 * new RuntimeException(e); }
	 * 
	 * }
	 */
}