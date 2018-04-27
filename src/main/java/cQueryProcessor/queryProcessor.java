package cQueryProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class queryProcessor {
    static  private Stem stemmer;
    private HashMap<String,Boolean>stopWords = new HashMap<>();
    private String recievedQuery;
    private String query;
    private boolean phSearch;
    queryProcessor(String q){
        this.recievedQuery = q;
        this.query = q;
        phSearch = false;
    }

    private String ignoreStopWords(){

        File file = new File("/home/mina/IdeaProjects/QueryProcessor/src/main/java/cQueryProcessor/stopwords.txt");
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            stopWords.put(line,Boolean.TRUE);
        }
        sc.close();

        String res = "";
        String arr[] = this.query.split(" ");
        for (int i = 0; i < arr.length; ++i){
            if(stopWords.get(arr[i]) == null){
                if(!res.isEmpty()) res = res + " " + arr[i];
                else res = arr[i];
            }
        }

        return res;
    }

    private boolean isPhraseSearch(String q){
        boolean fch = false;
        char lstch=' ';
        for (int i = 0; i < q.length(); ++i){
            if(q.charAt(i) == ' ')continue;
            if(q.charAt(i) != '"' && !fch)return false;
            else if(q.charAt(i) == '"'){
                fch = true;
            }
            lstch = q.charAt(i);
        }
        return  (lstch == '"');
    }

    private String stem(String q){
        String res = "";
        String arr[] = q.split(" ");
        for (int i = 0; i < arr.length; ++i){
            if(stopWords.get(arr[i]) == null){
                if(!res.isEmpty()) res = res + " " + stemmer.stemWord(arr[i].toLowerCase());
                else  res = stemmer.stemWord(arr[i].toLowerCase());
            }
        }
        return res;
    }


    public String run(){
        stemmer = new Stem();
        this.query = this.query.toLowerCase();
        phSearch = isPhraseSearch(this.query);
        this.query = this.query.replaceAll("[^a-zA-Z']+", " ");
        this.query = ignoreStopWords();
        if(!phSearch) this.query = stem(this.query);
        return  this.query;
    }

    public boolean isPhSearch()
    {
        return  phSearch;
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();
        queryProcessor qp = new queryProcessor(str);
        qp.run();
        System.out.println(qp.query);
    }


}
