package mselasticsearch.mapper;

import mselasticsearch.domain.MsHotel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author camellia
* @description 针对表【tb_hotel】的数据库操作Mapper
* @createDate 2025-01-20 11:23:05
* @Entity mselasticsearch.domain.MsHotel
*/
@Mapper
public interface MsHotelMapper extends BaseMapper<MsHotel> {

}




