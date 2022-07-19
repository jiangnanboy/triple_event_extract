package sy.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sy
 * @date 2022/7/19 19:36
 */
public class Segment {

    /**
     * split sentence by punctuation
     * @param text
     * @return
     */
    public static List<String> splitSentence(String text){
        List<String> sentences = CollectionUtil.newArrayList();
        String regEx = "[。！？；：?!:;\n\r]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(text);
        String[] sent = p.split(text);
        int sentLen = sent.length;
        if(sentLen > 0){
            int count = 0;
            while(count < sentLen){
                if(m.find()){
                    sent[count] += m.group();
                }
                count ++;
            }
        }
        for(String sentence : sent){
            sentence = sentence.replaceAll("(&rdquo;|&ldquo;|&mdash;|&lsquo;|&rsquo;|&middot;|&quot;|&darr;|&bull;)", "");
            sentences.add(sentence.trim());
        }
        return sentences;
    }

}

