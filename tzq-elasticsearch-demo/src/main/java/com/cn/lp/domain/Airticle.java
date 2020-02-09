package com.cn.lp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 〈一句话功能简述〉
 *
 * @author tongziqi
 * @DATE 2020/2/8
 * @see
 * @since AgileCenter v7.0
 */
@Document(indexName = "airticle", type = "docs", shards = 1, replicas = 0)
public class Airticle {

    /**
     * @Description: @Id注解必须是springframework包下的
     * org.springframework.data.annotation.Id
     */
    @Id
    private Long id;
    @Field(type = FieldType.Keyword)
    String space;
    /**
     * 库
     */
    @Field(type = FieldType.Keyword)
    String box;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    String tag;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    String title;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    String content;
    public Airticle() {

    }
    public Airticle(Long id, String space, String box, String tag, String title, String content) {
        this.id = id;
        this.space = space;
        this.box = box;
        this.tag = tag;
        this.title = title;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getBox() {
        return box;
    }

    public void setBox(String box) {
        this.box = box;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
