package sy.triple;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.commons.lang3.StringUtils;
import sy.utils.CollectionUtil;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sy
 * @date 2022/7/19 19:36
 */
public class TriplesMine {

    /**
     * 抽取spo
     * @param text
     * @return
     */
    public List<List<String>> extractSpo(String text) {
        text = text.trim();
        if(StringUtils.isBlank(text)) {
            return null;
        }
        //分句
        List<String> sentences = cutText(text);

        //保存主谓宾短语
        List<List<String>> triples = CollectionUtil.newArrayList();

        for(String sentStr : sentences) {
            List<Term> wordsPostags = cutSentence(sentStr);
            List<String> subList = CollectionUtil.newArrayList();
            List<String> subPos = CollectionUtil.newArrayList();

            for(Term term : wordsPostags) {
                String word = term.word;
                String pos = term.nature.toString();
                subList.add(word); //词
                subPos.add(pos);//词性
            }
           // 抽取“主谓宾”
            List<List<String>> triple = extractTrples(subList, subPos, sentStr);

            if(0 == triple.size()) {
                continue;
            }
            triples.addAll(triple);
        }
        return triples;
    }

    /**
     * 抽取三元组
     * @return
     */
    public List<List<String>> extractTrples(List<String> words, List<String> pos, String sentence) {
        List<List<String>> svo = CollectionUtil.newArrayList();
        List<String> tuples = TextParser.syntaxParser(words, pos, sentence);

        Map<String, Map<String, List<String>>> childDictList = TextParser.buildParseChildDict(words, pos, tuples);
        for(String tuple : tuples) {
            String[] strSplit = tuple.split(";");
            String relation = strSplit[6];
            // spo
            if("主谓关系".equals(relation)) {
                String subj = strSplit[1];
                String verb = strSplit[3];
                String obj = completeVOB(verb, childDictList);
                if(StringUtils.isNotBlank(obj)) {
                    List<String> list = CollectionUtil.newArrayList();
                    list.add(subj); // 主
                    list.add(verb); // 谓
                    list.add(obj); // 宾
                    svo.add(list);
                }
            }
        }
        return svo;
    }

    /**
     * 根据"主谓关系"找"动宾关系" -> 完成“主谓宾”结构
     * @param verb
     * @param childDictList
     * @return
     */
    public String completeVOB(String verb, Map<String, Map<String, List<String>>> childDictList) {
        for(Map.Entry<String, Map<String, List<String>>> entry : childDictList.entrySet()) {
            String[] wordPosIndex = entry.getKey().split(";");
            String word = wordPosIndex[0];
            Map<String, List<String>> map = entry.getValue();//关系属性
            if(word.equals(verb)) {
                if(!map.containsKey("动宾关系")) {
                    continue;
                }
                String[] vob = map.get("动宾关系").get(0).split(";");
                String obj = vob[1];
                return obj;
            }
        }
        return "";
    }

    /**
     * 分词和词性
     * @param sent
     * @return
     */
    public List<Term> cutSentence(String sent) {
        List<Term> termList = CollectionUtil.newArrayList();
        CoNLLSentence sentence = HanLP.parseDependency(sent);
        CoNLLWord[] wordArray = sentence.getWordArray();
        for (int i =0; i < wordArray.length; i++)
        {
            CoNLLWord word = wordArray[i];
            String wordNmae = word.LEMMA;
            String pos = word.POSTAG;
            Term term = new Term(wordNmae, Nature.create(pos));
            termList.add(term);
        }
        return termList;
        // 去停词
        //return CoreStopWordDictionary.apply(segment.seg(sentence));
    }

    /**
     * 分句
     * @param text
     * @return
     */
    public List<String> cutText(String text) {
        List<String> sentences = CollectionUtil.newArrayList();
        String regEx="[!?。！？.;；]";
        Pattern p= Pattern.compile(regEx);
        Matcher m=p.matcher(text);
        String[] sent=p.split(text);
        int sentLen=sent.length;
        if(sentLen>0){
            int count=0;
            while(count<sentLen){
                if(m.find()){
                    sent[count]+=m.group();
                }
                count++;
            }
        }
        for(String sentence:sent){
            sentence=sentence.replaceAll("(&rdquo;|&ldquo;|&mdash;|&lsquo;|&rsquo;|&middot;|&quot;|&darr;|&bull;)", "");
            sentences.add(sentence.trim());
        }
        return sentences;
    }

}

