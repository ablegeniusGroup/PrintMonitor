package pos.dongwang.dao;

import org.apache.ibatis.session.SqlSession;
import pos.dongwang.dto.IsBilledDto;
import pos.dongwang.dto.OrderHangDto;
import pos.dongwang.dto.PosOrderDto;
import pos.dongwang.dto.TopButtonDto;
import pos.dongwang.enums.OrderType;
import pos.dongwang.enums.PrintStateEnum;
import pos.dongwang.mapper.OrderHangListMapper;
import pos.dongwang.mapper.PosOrderDtoMapper;
import pos.dongwang.mapper.TopButtonDtoMapper;
import pos.dongwang.model.PosOrder;
import pos.dongwang.mybatis.MyBatisSqlSessionFactory;
import pos.dongwang.util.AppUtils;
import pos.dongwang.util.SqlSessionUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wenjing on 2017/10/20.
 */
public  class OrderHangListDao {


    public static  void addOrderHangList(String refNum,Date hangTime,Date releaseTime,Long longDate){
        SqlSession sqlSession = SqlSessionUtil.getSqlSession();
        sqlSession.getMapper(OrderHangListMapper.class).addOrderHangList(refNum,hangTime,releaseTime,longDate);
        sqlSession.commit();
    }

    public Long getOrderHangListCountByDate(Date nowDate){
        SqlSession sqlSession = SqlSessionUtil.getSqlSession();
        Long count =  sqlSession.getMapper(OrderHangListMapper.class).getOrderHangListCountByDate(nowDate);
        sqlSession.commit();
        return count;
    }

    public static  void deleteOrderHangListByRefNum( SqlSession sqlSession,String refNum,Long hangDate){
        sqlSession.getMapper(OrderHangListMapper.class).deleteOrderHangListByRefNum(refNum,hangDate);
        sqlSession.commit();
    }

    public static  void autoDeleteOrderHangListByRefNum( SqlSession sqlSession,Long hangDate){
        sqlSession.getMapper(OrderHangListMapper.class).autoDeleteOrderHangListByRefNum(hangDate);
        sqlSession.commit();
    }

    public List<OrderHangDto> getOrderHangList(Integer page,Integer pageSize){
        SqlSession sqlSession = SqlSessionUtil.getSqlSession();
        List<OrderHangDto> orderHangDtos =  sqlSession.getMapper(OrderHangListMapper.class).getOrderHangList(page,pageSize);
        sqlSession.commit();
        return  orderHangDtos;
    }

}
