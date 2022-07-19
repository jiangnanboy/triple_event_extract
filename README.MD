#### EventExtraction & TriplesExtraction

一.中文复合事件抽取，包括条件事件、因果事件、顺承事件、反转事件等事件抽取

   examples/ExtractEvent
   ```
    String butPath = ExtractEvent.class.getClassLoader().getResource(PropertiesReader.get("but")).getPath().replaceFirst("/", "");
    String seqPath = ExtractEvent.class.getClassLoader().getResource(PropertiesReader.get("seq")).getPath().replaceFirst("/", "");
    String morePath = ExtractEvent.class.getClassLoader().getResource(PropertiesReader.get("more")).getPath().replaceFirst("/", "");
    String conditionPath = ExtractEvent.class.getClassLoader().getResource(PropertiesReader.get("condition")).getPath().replaceFirst("/", "");
    List<String> pathList = Arrays.asList(new String[]{butPath, seqPath, morePath, conditionPath});
    EventsExtraction eventsExtraction = new EventsExtraction(pathList);
    List<Map<String, String>> dataList = eventsExtraction.extractMain("虽然这件事很难，但我们完全能克服它。如果这件事不难，我们就没必要处理它了。");
    System.out.println(dataList);

    // result
    [{type=but, tuples={"preWd":"虽然","prePart":"这件事很难，","postWd":"但","postPart":"我们完全能克服它"}, sent=虽然这件事很难，但我们完全能克服它。}, {type=condition, tuples={"preWd":"如果","prePart":"这件事不难，我们","postWd":"就","postPart":"没必要处理它了"}, sent=如果这件事不难，我们就没必要处理它了。}]
   ```

二.基于依存句法的三元组抽取

examples/ExtractTriple
    
   ```
    String content = "新快报记者从广州警方获悉，2002年1月7日，广州番禺警方接到群众报警，称其朋友卢某（男）于1月6日凌晨失踪。民警随后在番禺区市桥街一出租屋内找到卢某，当时卢某已经死亡，身上财物丢失。案发后没多久，番禺警方就将涉嫌参与抢劫杀害卢某的其中三名嫌疑人耿某、胡某以及翁某（女）抓获归案，另有一名嫌疑人力天佑负案在逃。\n" +
                                      "据嫌疑人交代，2002年元旦过后，力天佑找到耿某和胡某，告知两人有一个“发财”的机会：力天佑发现卢某很有钱，密谋由翁某将卢某带回翁某租住的出租屋，力天佑等三人伺机进入出租屋抢劫。\n" +
                                      "案发当天，力天佑带着耿某和胡某先行进入翁某租住的出租屋内等待。晚上22时许，翁某带着卢某回到出租屋，一进入屋内，力天佑等三人合力将卢某推倒在床上，用手捂住卢某嘴巴，用绳索绑住卢某手脚。一番拳打脚踢之后，力天佑从卢某身上搜出两台手机和一个钱包，将其中一台手机给了耿某，又给了胡某一千元钱。眼见卢某因窒息而死，四人逃离了出租屋。\n" +
                                      "卢某的家人和朋友因为一直无法联系上卢某，多方找寻未果，向番禺警方报警。警方很快将翁某、耿某和胡某三人抓获，但狡猾的力天佑一直潜逃在外。";
    TriplesMine triplesMine = new TriplesMine();
    List<List<String>> triplesList = triplesMine.extractSpo(content);
    System.out.println(triplesList);

    // result
[[记者, 获悉, 接到], [警方, 接到, 报警], [民警, 找到, 卢某], [力天佑, 找到, 耿某], [两人, 有, 机会], [力天佑, 发现, 有钱], [人, 进入, 出租屋], [卢某, 回到, 出租屋], [一, 进入, 屋内], [人, 推倒, 床上], [力天佑, 搜出, 手机], [手机, 给, 耿某], [卢某, 逃离, 出租屋], [人, 逃离, 出租屋], [家人, 联系, 卢某]]
   ```

##### Reference
https://github.com/liuhuanyong/ComplexEventExtraction

https://github.com/jiangnanboy/text_grapher