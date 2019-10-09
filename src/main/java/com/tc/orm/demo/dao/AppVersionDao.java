package com.tc.orm.demo.dao;

import com.tc.orm.demo.entity.AppVersion;
import com.tc.orm.framework.BaseDaoSupport;
import javafx.scene.chart.PieChart;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taosh
 * @create 2019-10-08 14:57
 */
@Repository
public class AppVersionDao extends BaseDaoSupport {

    JdbcTemplate jdbcTemplate;

    @Override
    protected String getPKColumn() {
        return null;
    }

    @Override
    @Resource(name = "dataSource")
    public void setDataSource(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<AppVersion> selectAll(){
        final List<AppVersion> result = new ArrayList<AppVersion>();
        String sql = "select * from t_app_version";
        jdbcTemplate.query(sql, new RowMapper<Object>() {
            public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                AppVersion version = new AppVersion();

                resultSet.getLong(i);

                result.add(version);

                return version;
            }
        });

        return result;
    }

    @Override
    public boolean delete(Object entity) throws Exception {
        return false;
    }

    @Override
    public Object insertAndReturnId(Object entity) throws Exception {
        return null;
    }

    @Override
    public boolean insert(Object entity) throws Exception {
        return false;
    }

    @Override
    public boolean update(Object entity) throws Exception {
        return false;
    }
}
