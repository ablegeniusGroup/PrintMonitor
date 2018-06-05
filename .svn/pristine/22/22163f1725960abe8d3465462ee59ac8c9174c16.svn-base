package pos.dongwang.dao;

import com.sun.deploy.util.SessionProperties;
import org.apache.ibatis.session.SqlSession;
import pos.dongwang.dto.BranchDto;
import pos.dongwang.dto.TGoodsDto;
import pos.dongwang.mapper.BranchMapper;
import pos.dongwang.mybatis.MyBatisSqlSessionFactory;
import pos.dongwang.util.AppUtils;
import pos.dongwang.util.JdbcUtils;
import pos.dongwang.util.SqlSessionUtil;

import java.util.List;

/**
 * Created by lodi on 2017/11/17.
 */
public class BranchDao {

      public static String  getOutlineByOutlet(String outlet) {
        SqlSession session = SqlSessionUtil.getSqlSession();
        BranchMapper branchMapper = session.getMapper(BranchMapper.class);
        String line = branchMapper.getLineByBarcode(outlet);
        if(AppUtils.isNotBlank(line)){
            return line;
        }
        return null ;
    }
}
