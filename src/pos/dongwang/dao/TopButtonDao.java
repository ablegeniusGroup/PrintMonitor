package pos.dongwang.dao;

import org.apache.ibatis.session.SqlSession;
import pos.dongwang.dto.PosOrderDto;
import pos.dongwang.dto.TopButtonDto;
import pos.dongwang.mapper.PosOrderDtoMapper;
import pos.dongwang.mapper.TopButtonDtoMapper;
import pos.dongwang.model.TopButton;
import pos.dongwang.mybatis.MyBatisSqlSessionFactory;
import pos.dongwang.util.JdbcUtils;
import pos.dongwang.util.SqlSessionUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lodi on 2017/11/10.
 */
public class TopButtonDao {


    /**
     * 查詢该分店所有按键
     *
    */

    public static List<TopButtonDto> getTopButtons() {
       /* String sql = "select\n" +
                "  ID,\n" +
                "  NAME1,\n" +
                "  NAME2 \n" +
                " from TOP_BUTTON \n" ;
        System.out.println(sql);
        List<TopButtonDto> topButtons = JdbcUtils.getInstance().findMoreRefResult(sql, null, TopButtonDto.class);
*/
        SqlSession sqlSession = SqlSessionUtil.getSqlSession();
        List<TopButtonDto> topButtonDtos = sqlSession.getMapper(TopButtonDtoMapper.class).getTopButtons();
        sqlSession.commit();
        return topButtonDtos;
    }










}
