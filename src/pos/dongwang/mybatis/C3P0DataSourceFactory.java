package pos.dongwang.mybatis;


import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

/**
 * Mybatis使用C3P0需要自行继承UnpooledDataSourceFactory，然后指定dataSource 为ComboPooledDataSource。
 * ComboPooledDataSource就是c3p0的數据源。
 */
public class C3P0DataSourceFactory extends UnpooledDataSourceFactory {
	    public C3P0DataSourceFactory(){
	       this.dataSource =new ComboPooledDataSource();
	    }
}


