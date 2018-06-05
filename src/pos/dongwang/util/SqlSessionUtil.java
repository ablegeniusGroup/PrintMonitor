package pos.dongwang.util;

import org.apache.ibatis.session.SqlSession;
import pos.dongwang.mybatis.MyBatisSqlSessionFactory;

/**
 * Created by lodi on 2017/12/30.
 */
public class SqlSessionUtil {

    private static SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
    public static SqlSession getSqlSession() {
        if (sqlSession == null) {
            sqlSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
        }
        sqlSession.commit(true);
        return sqlSession;
    }
}
