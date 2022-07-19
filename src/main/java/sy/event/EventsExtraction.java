package sy.event;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
import sy.utils.Segment;
import sy.utils.CollectionUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author sy
 * @date 2022/7/19 19:36
 */
public class EventsExtraction {
    List<Pattern> butPattern;
    List<Pattern> seqPattern;
    List<Pattern> morePattern;
    List<Pattern> conditionPattern;

    public EventsExtraction(List<String> pathList) {
        this.butPattern = this.createPattern(this.getPattern(pathList.get(0)));
        this.seqPattern = this.createPattern(this.getPattern(pathList.get(1)));
        this.morePattern = this.createPattern(this.getPattern(pathList.get(2)));
        this.conditionPattern = this.createPattern(this.getPattern(pathList.get(3)));
    }

    /**
     * 转折，顺承，并列，条件
     * @param butPath
     * @return
     */
    public List<Pair<List<String>, List<String>>> getPattern(String butPath) {
        List<Pair<List<String>, List<String>>> pairList = CollectionUtil.newArrayList();
        try(Stream<String> stream = Files.lines(Paths.get(butPath))) {
            stream.forEach(line -> {
                String[] tokens = line.split(";");
                String[] preTokens = tokens[0].split(",");
                String[] posToknes = tokens[1].split(",");
                pairList.add(Pair.of(Arrays.asList(preTokens), Arrays.asList(posToknes)));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pairList;
    }

    /**
     * 构建模板
     * @param pairList
     * @return
     */
    public List<Pattern> createPattern(List<Pair<List<String>, List<String>>> pairList) {
        List<Pattern> patternList = CollectionUtil.newArrayList();
        for(Pair<List<String>, List<String>> pair : pairList) {
            List<String> pre = pair.getLeft();
            List<String> pos = pair.getRight();
            Pattern pattern = Pattern.compile("(" + String.join("|", pre) + ")(.*)(" + String.join("|", pos) + ")([^？?！!。；;：:,，]*)");
            patternList.add(pattern);
        }
        return patternList;
    }

    /**
     * 模式匹配
     * @param patterns
     * @param sent
     * @return
     */
    public Map<String, String> patternMatch(List<Pattern> patterns, String sent) {
        Map<String, String> dataMaps = null;
        int max = 0;
        for(Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(sent);
            while (matcher.find()) {
                String preWd = matcher.group(1);
                String prePart = matcher.group(2);
                String postWd = matcher.group(3);
                String postPart = matcher.group(4);
                Map<String, String> dataMap = ImmutableMap.of("preWd", preWd, "prePart", prePart,
                        "postWd", postWd, "postPart", postPart);
                int wdLen = (preWd + postWd).length();
                if(wdLen > max) {
                    dataMaps = dataMap;
                    max = wdLen;
                }
            }
        }
        return dataMaps;
    }

    /**
     * 抽取
     * @param sent
     * @return
     */
    public List<Map<String, String>> extractTuples(String sent) {
        Map<String, String> butTuples = this.patternMatch(this.butPattern, sent);
        Map<String, String> seqTuples = this.patternMatch(this.seqPattern, sent);
        Map<String, String> moreTuples = this.patternMatch(this.morePattern, sent);
        Map<String, String> conditionTuples = this.patternMatch(this.conditionPattern, sent);
        List<Map<String, String>> tupleList = CollectionUtil.newArrayList();
        tupleList.add(butTuples);
        tupleList.add(seqTuples);
        tupleList.add(moreTuples);
        tupleList.add(conditionTuples);
        return tupleList;
    }

    /**
     * 抽取事件
     * @param content
     * @return
     */
    public List<Map<String, String>> extractMain(String content) {
        List<String> sents = Segment.splitSentence(content);
        List<Map<String, String>> dataList = CollectionUtil.newArrayList();
        for(String sent : sents) {
            Map<String, String> dataMap = CollectionUtil.newHashMap();
            dataMap.put("sent", sent);
            List<Map<String, String>> tupleList = this.extractTuples(sent);
            Map<String, String> butTuples = tupleList.get(0);
            Map<String, String> seqTuples = tupleList.get(1);
            Map<String, String> moreTuples = tupleList.get(2);
            Map<String, String> conditionTuples = tupleList.get(3);
            if(Optional.ofNullable(butTuples).isPresent() && butTuples.size() != 0) {
                dataMap.put("type", "but");
                dataMap.put("tuples", JSON.toJSONString(butTuples));
            }
            if(Optional.ofNullable(seqTuples).isPresent() && seqTuples.size() != 0) {
                dataMap.put("type", "seq");
                dataMap.put("tuples", JSON.toJSONString(seqTuples));
            }
            if(Optional.ofNullable(moreTuples).isPresent() && moreTuples.size() != 0) {
                dataMap.put("type", "more");
                dataMap.put("tuples", JSON.toJSONString(moreTuples));
            }
            if(Optional.ofNullable(conditionTuples).isPresent() && conditionTuples.size() != 0) {
                dataMap.put("type", "condition");
                dataMap.put("tuples", JSON.toJSONString(conditionTuples));
            }
            if(dataMap.containsKey("type")) {
                dataList.add(dataMap);
            }
        }
        return dataList;
    }

}


