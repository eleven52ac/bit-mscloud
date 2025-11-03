package mselasticsearch.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 房源信息表
 * @TableName prh_house_info
 */
@TableName(value ="prh_house_info")
@Data
public class MsHouseInfo {
    /**
     * 房源ID
     */
    @TableId(type = IdType.AUTO)
    private Long houseId;

    /**
     * 房源CODE
     */
    private String houseCode;

    /**
     * 保障房源统一编号
     */
    private String houseUnitcode;

    /**
     * 房屋座落
     */
    private String located;

    /**
     * 项目ID
     */
    private String projectCode;

    /**
     * 小区名称
     */
    private String projectName;

    /**
     * 房屋性质：单位自管房、经济适用住房、房改房、廉租房、直管公房等
单位自管房、经济适用住房、房改房、廉租房、直管公房等
     */
    private String houseQuality;

    /**
     * 保障类型：1:公租房、2;廉租房、3;经济适用房等
     */
    private String protectedType;

    /**
     * 房屋类型 1期房、2现房 (经济适用房合同中 有用)
     */
    private String houseType;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 所在城区
     */
    private String counties;

    /**
     * 所属房管所
     */
    private String houseManage;

    /**
     * 建筑面积
     */
    private String area;

    /**
     * 套内面积
     */
    private String inArea;

    /**
     * 分摊面积
     */
    private String outArea;

    /**
     * 使用面积
     */
    private String useArea;

    /**
     * 幢号
     */
    private String buildingsNum;

    /**
     * 所在层数
     */
    private String floorNumber;

    /**
     * 单元号
     */
    private String unitNum;

    /**
     * 总层数
     */
    private String allFloorNum;

    /**
     * 房号
     */
    private String houseNum;

    /**
     * 车库面积
     */
    private String carroomArea;

    /**
     * 阁楼建筑面积
     */
    private String atticArea;

    /**
     * 储藏室面积
     */
    private String storeroomArea;

    /**
     * 数据来源：1导入；2业务产生
     */
    private String dataSource;

    /**
     * 入库时间
     */
    private Date dataTime;

    /**
     * 是否有效（0-无效，1-有效）
     */
    private Integer isValid;
}