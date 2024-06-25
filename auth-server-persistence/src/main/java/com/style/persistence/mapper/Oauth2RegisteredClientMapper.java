package com.style.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.style.persistence.model.Oauth2RegisteredClient;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


/**
 * register client mapper
 *
 */
@Mapper
@Repository
public interface Oauth2RegisteredClientMapper extends BaseMapper<Oauth2RegisteredClient> {
}
