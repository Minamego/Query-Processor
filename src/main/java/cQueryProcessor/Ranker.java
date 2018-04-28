package cQueryProcessor;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.print.Doc;
import java.util.*;

public class Ranker {
    static final int EPS = 3;
    static final int MAXLEN = 300;
    static class Pair {
        public Integer id;
        public Double priority;
        Pair(Integer pId, Double pKey) {
            id = pId;
            priority = pKey;
        }
    }
    static class Word {
        static class  Url{
            ArrayList<Integer> pos = new ArrayList<>();
            int id;
        }
        ArrayList<Url>urls = new ArrayList<>();
    }
    private  static  ArrayList<Integer> Sort(ArrayList<Pair>urls) {
        ArrayList<Integer> ret = new ArrayList<Integer>();

        for(int i = 0; i < urls.size(); ++i) {
            int j = i, k = (i - 1) / 2;
            while(j > 0) {
                Pair a = urls.get(k);
                Pair b = urls.get(j);
                if(a.priority < b.priority) {
                    Pair t = new Pair(a.id, a.priority);
                    urls.set(k, new Pair(b.id, b.priority));
                    urls.set(j, new Pair(t.id, t.priority));
                    j = k;
                    k = (k - 1) / 2;
                }
                else break;
            }
        }
        int n = urls.size();
        while (n > 0) {
            n = n - 1;
            Pair a = urls.get(0);
            Pair b = urls.get(n);
            Pair t = new Pair(a.id, a.priority);
            urls.set(0, new Pair(b.id, b.priority));
            urls.set(n, new Pair(t.id, t.priority));
            int j, i = 0;
            while((j = i * 2 + 1) < n) {
                if(j + 1 < n && urls.get(j).priority < urls.get(j + 1).priority)j = j + 1;
                if(urls.get(i).priority < urls.get(j).priority){
                    a = urls.get(i);
                    b = urls.get(j);
                    t = new Pair(a.id, a.priority);
                    urls.set(i, new Pair(b.id, b.priority));
                    urls.set(j, new Pair(t.id, t.priority));
                    i = j;
                }
                else break;
            }
        }
        for(int i = urls.size() - 1; i >= 0; --i)ret.add(urls.get(i).id);
        return  ret;
    }

    public static boolean getGoodUrls(ArrayList<Word> words, int curWord , int curUrl , int curPos , ArrayList<Integer> ans)
    {
        if(curWord == words.size())
        {
            ans.add(curUrl);
            return true;
        }
        int l = 0 , r = words.get(curWord).urls.size()-1;
        while(l < r)
        {
            int mid = l + (r-l)/2;
            if(words.get(curWord).urls.get(mid).id >= curUrl) r = mid;
            else l = mid+1;
        }
        if(words.get(curWord).urls.get(r).id != curUrl) return true;
        int idx = r;
        l = 0 ;
        r = words.get(curWord).urls.get(idx).pos.size()-1;
        while(l < r)
        {
            int mid = l + (r-l)/2;
            if(words.get(curWord).urls.get(idx).pos.get(mid) >= curPos) r = mid;
            else l = mid+1;
        }
        int x = words.get(curWord).urls.get(idx).pos.get(r);
        if(x < curPos) return true;
        if(x - curPos > EPS) return false;
        return getGoodUrls(words , curWord+1 , curUrl , x+1 , ans);
    }

    public static void normalSearch( ArrayList<Document> mWords, MongoCollection<Document> mDocs , MongoCollection<Document> mLinks,
                                     ArrayList<String> retUrls, ArrayList<String> retPars, ArrayList<String> retTitles, ArrayList<Boolean> retInterested ,
                                     String username) {
        long mDocumentsCnt = mDocs.count();
        Map<Integer, Double> mPriorities = new HashMap<>();
        Map<Integer, Integer> mUrlWordsCnt = new HashMap<>();
        Map<Integer, Double> mUrlRank = new HashMap<>();
        Map<Integer, String> mUrlId = new HashMap<>();
        Map<Integer, Document> mUrlDoc = new HashMap<>();

        for (Document word : mWords) {
            if(word == null) continue;
            Document details = (Document) word.get("details");
            Set<String> urls = details.keySet();
            for (String urlID : urls) {
                int key = Integer.parseInt(urlID);
                if (!mUrlWordsCnt.containsKey(key)) {
                    mPriorities.put(key, 0.0);
                    String link = mLinks.find(new BasicDBObject("id", key)).first().getString("url");
                    Document doc = mDocs.find(new BasicDBObject("url", link)).first();
                    int cnt = doc.getInteger("numOfWords");
                    mUrlWordsCnt.put(key, cnt);
                    double pr = doc.getDouble("page_rank");
                    mUrlRank.put(key, pr);
                    mUrlId.put(key, link);
                    mUrlDoc.put(key , doc);
                }
            }
        }
        Map<Integer , Integer> goodPos = new HashMap<>();

        for (Document word : mWords) {
            if(word == null) continue;;
            Document details = (Document) word.get("details");
            Set<String> urls = details.keySet();
            double IDF = Math.log(1.0 * mDocumentsCnt / urls.size());
            for (String urlID : urls) {
                Document url = (Document) details.get(urlID);
                ArrayList<Integer> tags = (ArrayList<Integer>) url.get("tag");
                int key = Integer.parseInt(urlID);
                double TF = 1.0 * tags.size() / mUrlWordsCnt.get(key);
                if (TF > 0.5) continue;
                if(goodPos.get(key) == null) goodPos.put(key ,( (ArrayList<Integer>) url.get("positions")).get(0));
                double tag = 0.0;
                for (int i = 0; i < tags.size(); ++i) {
                    tag += tags.get(i);
                }
                tag /= tags.size();
                double priority = IDF * tag * TF;
                mPriorities.put(key, mPriorities.get(key) + priority);
            }
        }
        ArrayList<Pair> urls = new ArrayList<Pair>();
        for (Map.Entry<Integer, Double> url : mPriorities.entrySet()) {
            Pair tmp = new Pair(url.getKey(), url.getValue() * mUrlRank.get(url.getKey()));
            urls.add(tmp);
        }
        if(urls.isEmpty()) return;
        ArrayList<Integer> results = Sort(urls);


        for (int k = 0; k < results.size(); k++) {
            String url = mUrlId.get(results.get(k));
            retUrls.add(url);

            Document doc = mUrlDoc.get(results.get(k));
            retTitles.add(doc.getString("title"));

            ArrayList<String> data = (ArrayList<String>) doc.get("url_data");
            ArrayList<String> users = (ArrayList<String>) doc.get("interested");
            int sum = 0;
            int pos = goodPos.get(results.get(k));
            for (int j = 0; j < data.size(); ++j) {
                sum += data.get(j).split(" ").length;
                if (sum >= pos) {
                    String par = "";
                    par += data.get(j);
                    int a = j;
                    j++;
                    a--;
                    while (par.length() < MAXLEN && j < data.size()) par += " " +  data.get(j++);
                    while (par.length() < MAXLEN && a >= 0) par = data.get(a--) +" "+ par;
                    retPars.add(par);
                    break;
                }
            }
            if(users.contains(username)) retInterested.add(true);
            else retInterested.add(false);

        }
    }

    public static  void phraseSearch(ArrayList<Document> mWords, MongoCollection<Document> mDocs , MongoCollection<Document> mLinks,
                                     ArrayList<String> retUrls, ArrayList<String> retPars, ArrayList<String> retTitles, ArrayList<Boolean> retInterested ,
                                     String username)
    {
        Map<Integer, String> mUrlId = new HashMap<>();
        Map<Integer, Document> mUrlDoc = new HashMap<>();
        ArrayList<Word> words = new ArrayList<>();
        int i = 0;
        for(Document word : mWords)
        {
            if(word == null) return;
            Document details = (Document)word.get("details");
            Set<String> urlsStr = details.keySet();
            ArrayList<Integer> urls = new ArrayList<>();
            for(String urlID : urlsStr)
            {
                urls.add(Integer.parseInt(urlID));
            }
            Collections.sort(urls);
            words.add(new Word());
            for(Integer urlID : urls) {
                Word.Url url = new Word.Url();
                url.id = urlID;
                Document cur = (Document) details.get(urlID.toString());
                url.pos = (ArrayList<Integer>) cur.get("positions");
                Collections.sort(url.pos);
                words.get(i).urls.add(url);
            }
            i++;
        }
        if(mWords.isEmpty()) return ;
        ArrayList<Integer> ans = new ArrayList<>();
        Map<Integer , Integer> goodPos = new HashMap<>();
        for(int a = 0 ; a<words.get(0).urls.size() ; a++)
        {
            for(int b = 0 ; b<words.get(0).urls.get(a).pos.size() ; b++)
            {
                if(getGoodUrls(words ,1 , words.get(0).urls.get(a).id , words.get(0).urls.get(a).pos.get(b) , ans ))
                {
                    goodPos.put( words.get(0).urls.get(a).id , words.get(0).urls.get(a).pos.get(b) );
                    break;
                }
            }
        }
        if(ans.isEmpty()) new ArrayList<>();
        // sort by page rank
        ArrayList<Pair> urls = new ArrayList<>();
        for(int k = 0 ; k <ans.size() ; k++) {
            String link = mLinks.find(new BasicDBObject("id", ans.get(k))).first().getString("url");
            Document doc = mDocs.find(new BasicDBObject("url", link)).first();
            double val = doc.getDouble("page_rank");
            Pair tmp = new Pair(ans.get(k) , val );
            urls.add(tmp);
            mUrlId.put(ans.get(k) , link);
            mUrlDoc.put(ans.get(k) , doc);
        }
        ArrayList<Integer> results =   Sort(urls);

        for(int k = 0 ; k<results.size() ; k++)
        {
            String url = mUrlId.get(results.get(k));
            retUrls.add(url);

            Document doc = mUrlDoc.get(results.get(k));
            retTitles.add(doc.getString("title"));

            ArrayList<String> data = (ArrayList<String>) doc.get("url_data");
            ArrayList<String> users = (ArrayList<String>) doc.get("interested");
            int sum = 0;
            int pos = goodPos.get(results.get(k));
            String par = "";
            for(int j = 0 ; j<data.size() ; ++j)
            {
                sum +=  data.get(j).split(" ").length;
                if(sum > pos)
                {

                    par += data.get(j);
                    int a = j;
                    j++;
                    a--;
                    while(par.length() < MAXLEN && j<data.size())  par += " " + data.get(j++);
                    while(par.length() < MAXLEN && a>=0)  par =  data.get(a--) +" " +  par;

                    break;
                }
            }
            retPars.add(par);
            if(users.contains(username)) retInterested.add(true);
            else retInterested.add(false);
        }
    }

}