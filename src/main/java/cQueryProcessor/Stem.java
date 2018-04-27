package cQueryProcessor;

import stem.SnowballStemmer;
import stem.ext.englishStemmer;

public class Stem {
    SnowballStemmer stemmer;
    Stem() {
        stemmer = new englishStemmer();
    }
    public  String stemWord(String word)
    {
        stemmer.setCurrent(word);
        stemmer.stem();
        return stemmer.getCurrent();
    }
}
