package com.tc.orm.demo.dao;

import com.tc.orm.demo.entity.Member;
import com.tc.orm.framework.BaseDaoSupport;
import com.tc.orm.framework.QueryRule;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

/**
 * Created by Tom.
 */
@Repository
public class MemberDao extends BaseDaoSupport<Member,Long> {

    @Override
    protected String getPKColumn() {
        return "id";
    }

    @Override
    @Resource(name="dataSource")
    public void setDataSource(DataSource dataSource){
        super.setDataSourceReadOnly(dataSource);
        super.setDataSourceWrite(dataSource);
    }


    public List<Member> selectAll() throws  Exception{
        QueryRule queryRule = QueryRule.getInstance();
        queryRule.andLike("name","Tom%");
        return super.select(queryRule);
    }
}
