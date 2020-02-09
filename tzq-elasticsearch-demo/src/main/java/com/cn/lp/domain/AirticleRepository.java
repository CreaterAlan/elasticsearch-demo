package com.cn.lp.domain;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Description:定义ItemRepository 接口
 * @Param:
 * 	Item:为实体类
 * 	Long:为Item实体类中主键的数据类型
 * @Author: qirong
 * @Date: 2018/9/29 0:50
 */
public interface AirticleRepository extends ElasticsearchRepository<Airticle,Long> {


}
