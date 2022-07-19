package sy.examples;

import sy.event.EventsExtraction;
import sy.utils.PropertiesReader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author YanShi
 * @date 2022/7/19 21:35
 */
public class ExtractEvent {
    public static void main(String... args) {
        String butPath = ExtractEvent.class.getClassLoader().getResource(PropertiesReader.get("but")).getPath().replaceFirst("/", "");
        String seqPath = ExtractEvent.class.getClassLoader().getResource(PropertiesReader.get("seq")).getPath().replaceFirst("/", "");
        String morePath = ExtractEvent.class.getClassLoader().getResource(PropertiesReader.get("more")).getPath().replaceFirst("/", "");
        String conditionPath = ExtractEvent.class.getClassLoader().getResource(PropertiesReader.get("condition")).getPath().replaceFirst("/", "");
        List<String> pathList = Arrays.asList(new String[]{butPath, seqPath, morePath, conditionPath});
        EventsExtraction eventsExtraction = new EventsExtraction(pathList);
        List<Map<String, String>> dataList = eventsExtraction.extractMain("虽然这件事很难，但我们完全能克服它。如果这件事不难，我们就没必要处理它了。");
        System.out.println(dataList);
    }
}
