package mselasticsearch.mapper;

import mselasticsearch.domain.MsHouseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author camellia
* @description 针对表【prh_house_info(房源信息表)】的数据库操作Mapper
* @createDate 2025-03-03 17:55:56
* @Entity mselasticsearch.domain.MsHouseInfo
*/
@Mapper
public interface MsHouseInfoMapper extends BaseMapper<MsHouseInfo> {

}




