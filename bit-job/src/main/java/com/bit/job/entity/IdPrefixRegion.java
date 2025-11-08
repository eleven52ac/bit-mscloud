package com.bit.job.entity;

import lombok.Data;

import java.util.Date;

/**
 * 
 * @TableName ID_PREFIX_REGION
 */
@Data
public class IdPrefixRegion {
    /**
     * 
     */
    private String idPrefix;

    /**
     * 
     */
    private String regionName;

    /**
     * 
     */
    private String city;

    /**
     * 
     */
    private String area;

    /**
     * 
     */
    private String countyTown;

    /**
     * 
     */
    private String isDelete;

    /**
     * 
     */
    private Date createdAt;

    /**
     * 
     */
    public String getIdPrefix() {
        return idPrefix;
    }

    /**
     * 
     */
    public void setIdPrefix(String idPrefix) {
        this.idPrefix = idPrefix;
    }

    /**
     * 
     */
    public String getRegionName() {
        return regionName;
    }

    /**
     * 
     */
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    /**
     * 
     */
    public String getCity() {
        return city;
    }

    /**
     * 
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 
     */
    public String getArea() {
        return area;
    }

    /**
     * 
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * 
     */
    public String getCountyTown() {
        return countyTown;
    }

    /**
     * 
     */
    public void setCountyTown(String countyTown) {
        this.countyTown = countyTown;
    }

    /**
     * 
     */
    public String getIsDelete() {
        return isDelete;
    }

    /**
     * 
     */
    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    /**
     * 
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        IdPrefixRegion other = (IdPrefixRegion) that;
        return (this.getIdPrefix() == null ? other.getIdPrefix() == null : this.getIdPrefix().equals(other.getIdPrefix()))
            && (this.getRegionName() == null ? other.getRegionName() == null : this.getRegionName().equals(other.getRegionName()))
            && (this.getCity() == null ? other.getCity() == null : this.getCity().equals(other.getCity()))
            && (this.getArea() == null ? other.getArea() == null : this.getArea().equals(other.getArea()))
            && (this.getCountyTown() == null ? other.getCountyTown() == null : this.getCountyTown().equals(other.getCountyTown()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getIdPrefix() == null) ? 0 : getIdPrefix().hashCode());
        result = prime * result + ((getRegionName() == null) ? 0 : getRegionName().hashCode());
        result = prime * result + ((getCity() == null) ? 0 : getCity().hashCode());
        result = prime * result + ((getArea() == null) ? 0 : getArea().hashCode());
        result = prime * result + ((getCountyTown() == null) ? 0 : getCountyTown().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", idPrefix=").append(idPrefix);
        sb.append(", regionName=").append(regionName);
        sb.append(", city=").append(city);
        sb.append(", area=").append(area);
        sb.append(", countyTown=").append(countyTown);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", createdAt=").append(createdAt);
        sb.append("]");
        return sb.toString();
    }
}