package mselasticsearch;

/**
 * @Datetime: 2025年01月17日15:57
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: mselasticsearch
 * @Project: camellia-mscloud
 * @Description:
 */
public class Constant {

    public static final String INDEX_HOTEL_NAME = "hotel";

    public static final String MAPPING_TEMPLATE = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"name\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"address\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"price\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"score\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"brand\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"city\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"starName\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"business\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"location\":{\n" +
            "        \"type\": \"geo_point\"\n" +
            "      },\n" +
            "      \"pic\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"all\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    public static final String INDEX_HOUSE_NAME = "house";


    public static final String HOUSE_MAPPING_TEMPLATE = "{"
            + "  \"settings\": {"
            + "    \"number_of_shards\": 3,"
            + "    \"number_of_replicas\": 1"
            + "  },"
            + "  \"mappings\": {"
            + "    \"properties\": {"
            + "      \"houseId\": { \"type\": \"long\" },"
            + "      \"houseCode\": { \"type\": \"keyword\" },"
            + "      \"houseUnitcode\": { \"type\": \"keyword\" },"
            + "      \"located\": { \"type\": \"text\", \"analyzer\": \"ik_max_word\" },"
            + "      \"projectCode\": { \"type\": \"keyword\" },"
            + "      \"projectName\": { \"type\": \"text\", \"analyzer\": \"ik_max_word\" },"
            + "      \"houseQuality\": { \"type\": \"keyword\" },"
            + "      \"protectedType\": { \"type\": \"keyword\" },"
            + "      \"houseType\": { \"type\": \"keyword\" },"
            + "      \"city\": { \"type\": \"keyword\" },"
            + "      \"counties\": { \"type\": \"keyword\" },"
            + "      \"houseManage\": { \"type\": \"keyword\" },"
            + "      \"area\": { \"type\": \"float\" },"
            + "      \"inArea\": { \"type\": \"float\" },"
            + "      \"outArea\": { \"type\": \"float\" },"
            + "      \"useArea\": { \"type\": \"float\" },"
            + "      \"buildingsNum\": { \"type\": \"keyword\" },"
            + "      \"floorNumber\": { \"type\": \"integer\" },"
            + "      \"unitNum\": { \"type\": \"keyword\" },"
            + "      \"allFloorNum\": { \"type\": \"integer\" },"
            + "      \"houseNum\": { \"type\": \"keyword\" },"
            + "      \"carroomArea\": { \"type\": \"float\" },"
            + "      \"atticArea\": { \"type\": \"float\" },"
            + "      \"storeroomArea\": { \"type\": \"float\" },"
            + "      \"dataSource\": { \"type\": \"keyword\" },"
            + "      \"dataTime\": { \"type\": \"date\", \"format\": \"yyyy-MM-dd HH:mm:ss\" },"
            + "      \"isValid\": { \"type\": \"integer\" }"
            + "    }"
            + "  }"
            + "}";



}
