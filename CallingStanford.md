# Calling Stanford NER from Java #

```
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;


String input = "The store offers BlackBerry device models from AT&T, T-Mobile, Verizon, and Sprint.";
String serializedClassifier = "classifiers/ner-eng-ie.crf-3-all2008.ser.gz";
AbstractSequenceClassifier classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
String out = classifier.classifyToString(s1);
System.out.println(out);
```

# Downloading models #
See SVN