package pos.dongwang.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by nshg on 2017/11/19.
 */
public class MyBatisSqlSessionFactory {
    private static final SqlSessionFactory FACTORY;

    static {
        try {
            File file = new File(System.getProperty("user.dir") + File.separator + "config" + File.separator + "mybatis-config.xml");
            System.out.println("path:---------" + file.getAbsolutePath() );
            InputStream inputStream = new FileInputStream(file);
            //InputStream inputStream = Resources.getResourceAsStream("config/mybatis-config.xml");
            FACTORY = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (Exception e){
            throw new RuntimeException("Fatal Error.  Cause: " + e, e);
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return FACTORY;
    }
}
