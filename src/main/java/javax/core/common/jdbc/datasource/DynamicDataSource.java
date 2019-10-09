package javax.core.common.jdbc.datasource;

import lombok.Data;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author taosh
 * @create 2019-10-08 17:57
 */
@Data
public class DynamicDataSource extends AbstractRoutingDataSource {
    /**
     * entry的目的，主要是用来给每个数据源打标记
     */
    private DynamicDataSourceEntry dataSourceEntry;

    @Override
    protected Object determineCurrentLookupKey() {
        return this.dataSourceEntry.get();
    }
}
