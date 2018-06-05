package pos.dongwang.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import pos.dongwang.mapper.BranchMapper;
import pos.dongwang.mapper.RtLogMapper;
import pos.dongwang.util.AppUtils;
import pos.dongwang.util.SqlSessionUtil;

/**
 * Created by lodi on 2017/11/17.
 */
public class RtLogDao {

      public static void insertLog(String outlet,  String opDate,  String opTime, String staff,
                                      String logT,  String type,  String tranIndex,  String newRef, String tableNum,String remark1) {
        SqlSession session = SqlSessionUtil.getSqlSession();
        RtLogMapper rtLogMapper = session.getMapper(RtLogMapper.class);
        rtLogMapper.insertTLog(outlet, opDate, opTime, staff, logT, type,tranIndex,newRef,tableNum,remark1);
    }
}
