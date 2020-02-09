package com.cn.lp;

import com.cn.lp.domain.Airticle;
import com.cn.lp.domain.AirticleRepository;
import com.cn.lp.domain.Item;
import com.cn.lp.domain.ItemRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Api(tags = "测试API")
@RestController
public class TestController {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private AirticleRepository airticleRepository;

    @ApiOperation(value = "测试保存", notes = "测试保存")
    @GetMapping("/test")
    public String testSave() {
        List<Item> items = new ArrayList<>();
        items.add(new Item(1L, "小米手机7", "手机",
            "小米", 3499.00, "http://image.baidu.com/13123.jpg"
        ));
        items.add(new Item(2L, "小米手机9", "手机",
            "小米", 4000.00, "http://image.baidu.com/13123.jpg"
        ));
        items.add(new Item(3L, "华为pro", "手机",
            "华为", 2000.00, "http://image.baidu.com/13123.jpg"
        ));
        items.add(new Item(4L, "魅族", "手机",
            "魅族", 3000.00, "http://image.baidu.com/13123.jpg"
        ));
        itemRepository.saveAll(items);
        //elasticsearchTemplate.createIndex("ok");
        return "success";
    }


    @ApiOperation(value = "测试保存", notes = "测试保存")
    @GetMapping("/testAirticleSave")
    public String testAirticleSave(String title,String content) {
        List<Airticle> articles = new ArrayList<>();

        articles.add(new Airticle(1L,"空间1","库1","标签1","标题啦啦啦1哈哈嘿嘿哦哦","123"));
        articles.add(new Airticle(2L,"空间2","库1","标签1","标题啦啦啦2哈哈嘿嘿哦哦","345"));
        articles.add(new Airticle(3L,"空间2","库2","标签2","标题啦啦啦3哈哈嘿嘿哦哦","567"));
        articles.add(new Airticle(4L,"空间1","库3","标签1","标题啦啦啦2哈哈嘿嘿哦哦","678"));
        articles.add(new Airticle(5L,"空间1","库1","标签3","标题啦啦啦3哈哈嘿嘿哦哦","456"));
        articles.add(new Airticle(6L,"空间4","库4","标签4","标题啦啦啦4哈哈嘿嘿哦哦","456"));
        airticleRepository.saveAll(articles);

        return "success";
    }

    @ApiOperation(value = "测试保存单个", notes = "测试保存")
    @GetMapping("/testAirticleSaveSingle")
    public String testAirticleSaveSingle(String title,String content) {
        List<Airticle> articles = new ArrayList<>();

        articles.add(new Airticle(1L,"空间1","库1","标签1",title,content));
        airticleRepository.saveAll(articles);

        return "success";
    }


    @ApiOperation(value = "测试搜索", notes = "测试搜索")
    @GetMapping("/testFind")
    public String testFind() {
        Optional<Item> optional = itemRepository.findById(1L);
        return optional.isPresent() ? "id : " + optional.get().getId() + "" : "null";
    }

    @ApiOperation(value = "测试搜索", notes = "测试搜索")
    @GetMapping("/testAirticleFind")
    public String testAirticleFind() {
        Optional<Airticle> optional = airticleRepository.findById(1L);
        return optional.isPresent() ? "id : " + optional.get().getId() + "" : "null";
    }

    @ApiOperation(value = "测试匹配", notes = "测试匹配")
    @GetMapping("/testMatch")
    public String testMatchQuery() {
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(matchQuery("title", "小米手机"));
        // 搜索，获取结果
        Page<Item> items = this.itemRepository.search(queryBuilder.build());
        // 总条数
        long total = items.getTotalElements();

        System.out.println("--------------Match查询-------------------");
        for (Item item : items) {
            System.out.println("id :" + item.getId());
            System.out.println("title :" + item.getTitle());
        }
        System.out.println("---------------------------------");

        return "total = " + total;
    }

    @ApiOperation(value = "文章测试匹配", notes = "测试匹配")
    @GetMapping("/testAirticleMatchQuery")
    public Page<Airticle> testAirticleMatchQuery(String content) {
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(matchQuery("tag", content));
        QueryBuilders.multiMatchQuery(
                "标签",     // Text you are looking for
                "title", "content"       // Fields you query on
        );
        // 搜索，获取结果
        Page<Airticle> airticles= this.airticleRepository.search(queryBuilder.build());
        // 总条数
        long total = airticles.getTotalElements();

        System.out.println("--------------Match查询-------------------");
        for (Airticle airticle : airticles) {
            System.out.println("id :" + airticle.getId());
            System.out.println("title :" + airticle.getTitle());
        }
        System.out.println("---------------------------------");

        return airticles;
    }
    /**
     * @Description: termQuery:功能更强大，除了匹配字符串以外，还可以匹配
     * int/long/double/float/....
     */
    @ApiOperation(value = "测试termQuery", notes = "测试termQuery")
    @GetMapping("/testTerm")
    public String testTermQuery() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(QueryBuilders.termQuery("price", 3499.00));
        // 查找
        Page<Item> page = this.itemRepository.search(builder.build());

        System.out.println("--------------termQuery查询-------------------");
        for (Item item : page) {
            System.out.println("id :" + item.getId());
            System.out.println("title :" + item.getTitle());
        }
        System.out.println("---------------------------------");

        return "total = " + page.getTotalElements();
    }

    /**
     * @Description:布尔查询
     */
    @ApiOperation(value = "测试布尔查询", notes = "测试布尔查询")
    @GetMapping("/testBoolean")
    public String testBooleanQuery() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        builder.withQuery(
            QueryBuilders.boolQuery().must(matchQuery("title", "华为"))
                .must(matchQuery("brand", "华为"))
        );

        // 查找
        Page<Item> page = this.itemRepository.search(builder.build());

        System.out.println("--------------布尔查询-------------------");
        for (Item item : page) {
            System.out.println("id :" + item.getId());
            System.out.println("title :" + item.getTitle());
        }
        System.out.println("---------------------------------");

        return "total = " + page.getTotalElements();
    }

    /**
     * @Description:模糊查询
     */
    @ApiOperation(value = "测试模糊查询", notes = "测试模糊查询")
    @GetMapping("/testFuzzy")
    public String testFuzzyQuery() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(QueryBuilders.fuzzyQuery("title", "小米"));
        Page<Item> page = this.itemRepository.search(builder.build());
        System.out.println("--------------模糊查询-------------------");
        for (Item item : page) {
            System.out.println("id :" + item.getId());
            System.out.println("title :" + item.getTitle());
        }
        System.out.println("---------------------------------");

        return "total = " + page.getTotalElements();
    }

    /**
     * @Description:模糊查询
     */
    @ApiOperation(value = "测试模糊查询", notes = "测试模糊查询")
    @GetMapping("/testAirticleFuzzy")
    public Page<Airticle> testAirticleFuzzy() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        builder.withQuery(QueryBuilders.fuzzyQuery("title", "标题"));
        Page<Airticle> page = this.airticleRepository.search(builder.build());
        System.out.println("--------------模糊查询-------------------");
        for (Airticle item : page) {
            System.out.println("id :" + item.getId());
            System.out.println("title :" + item.getTitle());
        }
        System.out.println("---------------------------------");

        return page;
    }

    /**
     * @Description:分页查询
     */
    @ApiOperation(value = "测试分页查询", notes = "测试分页查询")
    @GetMapping("/testPage")
    public String searchByPage() {
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.termQuery("category", "手机"));
        // 分页：
        int page = 0;
        int size = 2;
        queryBuilder.withPageable(PageRequest.of(page, size));

        // 搜索，获取结果
        Page<Item> items = this.itemRepository.search(queryBuilder.build());
        // 总条数
        long total = items.getTotalElements();
        System.out.println("总条数 = " + total);
        // 总页数
        System.out.println("总页数 = " + items.getTotalPages());
        // 当前页
        System.out.println("当前页：" + items.getNumber());
        // 每页大小
        System.out.println("每页大小：" + items.getSize());

        System.out.println("--------------分页查询-------------------");
        for (Item item : items) {
            System.out.println("id :" + item.getId());
            System.out.println("title :" + item.getTitle());
        }
        System.out.println("---------------------------------");

        return "total = " + items.getTotalElements();
    }

    /**
     * @Description:排序查询
     */
    @ApiOperation(value = "测试排序查询", notes = "测试排序查询")
    @GetMapping("/testSort")
    public String searchAndSort() {
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.termQuery("category", "手机"));

        // 排序
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC));

        // 搜索，获取结果
        Page<Item> items = this.itemRepository.search(queryBuilder.build());
        // 总条数
        long total = items.getTotalElements();
        System.out.println("总条数 = " + total);

        System.out.println("---------------排序查询------------------");
        for (Item item : items) {
            System.out.println("id :" + item.getId());
            System.out.println("title :" + item.getTitle());
            System.out.println("price :" + item.getPrice());
        }
        System.out.println("---------------------------------");
        return "total = " + items.getTotalElements();
    }

    /**
     * @Description:按照品牌brand进行分组
     */
    @ApiOperation(value = "测试按照品牌brand进行分组", notes = "测试按照品牌brand进行分组")
    @GetMapping("/testAgg")
    public String testAgg() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        queryBuilder.addAggregation(
            AggregationBuilders.terms("brands").field("brand"));
        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage<Item> aggPage = (AggregatedPage<Item>) this.itemRepository.search(queryBuilder.build());
        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
        System.out.println("----------------分组-----------------");
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称
            System.out.println("品牌 :" + bucket.getKeyAsString());
            // 3.5、获取桶中的文档数量
            System.out.println("数量 : " + bucket.getDocCount());
        }
        System.out.println("---------------------------------");
        return "total = " + buckets.size();
    }

    /**
     * @Description:嵌套聚合，求平均值
     */
    @ApiOperation(value = "测试嵌套聚合，求平均值", notes = "测试嵌套聚合，求平均值")
    @GetMapping("/testSubAgg")
    public String testSubAgg() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        queryBuilder.addAggregation(
            AggregationBuilders.terms("brands").field("brand")
                .subAggregation(AggregationBuilders.avg("priceAvg").field("price")) // 在品牌聚合桶内进行嵌套聚合，求平均值
        );
        // 2、查询,需要把结果强转为AggregatedPage类型
        AggregatedPage<Item> aggPage = (AggregatedPage<Item>) this.itemRepository.search(queryBuilder.build());
        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
        System.out.println("--------------嵌套聚合-------------------");
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称  3.5、获取桶中的文档数量
            System.out.println("品牌 :" + bucket.getKeyAsString() + "，共" + bucket.getDocCount() + "台");

            // 3.6.获取子聚合结果：
            InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("priceAvg");
            System.out.println("平均售价：" + avg.getValue());
        }
        System.out.println("---------------------------------");
        return "total = " + buckets.size();

    }

    /**
     * 从es检索数据
     *
     * @param content  搜索关键字
     * @param pageNum  页
     * @param pageSzie 条
     * @return
     */
    @ApiOperation(value = "高亮查询", notes = "高亮查询")
    @GetMapping("/getIdeaListBySrt")
    public AggregatedPage<Airticle> getIdeaListBySrt(String content, Integer pageNum, Integer pageSzie) {
        Pageable pageable = PageRequest.of(pageNum, pageSzie);

        String preTag = "<font color='#dd4b39'>";//google的色值
        String postTag = "</font>";
        //设计被检索的字段
        String [] fileds = {"title", "content"};
        QueryBuilder mutiQueryBuilder = QueryBuilders.multiMatchQuery(content, fileds);
        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(mutiQueryBuilder).
                withHighlightFields(
                        new HighlightBuilder.Field("title").preTags(preTag).postTags(postTag),
                        new HighlightBuilder.Field("content").preTags(preTag).postTags(postTag)
                ).withPageable(pageable).build();

        // 不需要高亮直接return ideas
        // AggregatedPage<Idea> ideas = elasticsearchTemplate.queryForPage(searchQuery, Idea.class);

        // 高亮字段
        AggregatedPage<Airticle> ideas = elasticsearchTemplate.queryForPage(searchQuery, Airticle.class, new SearchResultMapper() {

            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<Airticle> result = new ArrayList<>();

                SearchHits hits = searchResponse.getHits();
                for(SearchHit searchHit : hits) {
                    if (hits.getHits().length <= 0) {
                        return null;
                    }

                    Airticle airticle = new Airticle();

                    //设置ID
                    airticle.setId(Long.parseLong(searchHit.getId()));

                    //设置sex
                    String box = (String) searchHit.getSourceAsMap().get("box");
                    airticle.setBox(box);

                    //设置高亮的name
                    HighlightField nameHighlight = searchHit.getHighlightFields().get("title");
                    if (nameHighlight != null) {
                        airticle.setTitle(nameHighlight.fragments()[0].toString());
                    } else {
                        //没有高亮的name
                        String name = (String)searchHit.getSourceAsMap().get("title");
                        airticle.setTitle(name);
                    }

                    //设置高亮的content
                    HighlightField addressHighlight = searchHit.getHighlightFields().get("content");
                    if (addressHighlight != null) {
                        airticle.setContent(addressHighlight.fragments()[0].toString());
                    } else {
                        //没有高亮的address
                        String address = (String)searchHit.getSourceAsMap().get("content");
                        airticle.setContent(address);
                    }

                    result.add(airticle);
                }

                if (result.size() > 0) {
                    return new AggregatedPageImpl<>((List<T>) result);
                }

                return null;
            }
        });
        return ideas;
    }

}
