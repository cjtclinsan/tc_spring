package com.tc.orm.demo.entity;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author taosh
 * @create 2019-10-07 15:02
 */
@Table(name="t_app_version")
@Data
public class AppVersion {

    @Column(name = "app_version_id")
    private Integer appVersionId;

    private Integer type;

    @Column(name = "version_code")
    private String versionCode;

    private String version;

    private String url;

    @Column(name = "file_name")
    private String fileName;

    private Integer isupdate;

    @Column(name = "create_by")
    private Integer createBy;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_by")
    private Integer updateBy;

    @Column(name = "update_time")
    private Date updateTime;

    private Integer isdelete;

    private String remark;

    @Override
    public String toString() {
        return "AppVersion{" +
                "appVersionId=" + appVersionId +
                ", type=" + type +
                ", versionCode='" + versionCode + '\'' +
                ", version='" + version + '\'' +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
