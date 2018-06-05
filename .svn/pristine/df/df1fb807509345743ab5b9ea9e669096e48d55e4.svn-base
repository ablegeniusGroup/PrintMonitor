package pos.dongwang.mapper;

import org.apache.ibatis.annotations.Param;
import pos.dongwang.dto.OrderHangDto;

import java.util.Date;
import java.util.List;

/**
 * Created by lodi on 2017/11/21.
 */
public interface OrderHangListMapper {

    public int addOrderHangList(@Param("refNum") String refNum,@Param("hangTime") Date hangTime,@Param("releaseTime") Date releaseTime,@Param("longDate") Long longDate);

    public Long getOrderHangListCountByDate(@Param("nowDate") Date nowDate);

    public void deleteOrderHangListByRefNum(@Param("refNum") String refNum,@Param("longDate") Long longDate);

    public void autoDeleteOrderHangListByRefNum(@Param("longDate") Long longDate);

    public List<OrderHangDto> getOrderHangList(@Param("page") Integer page,@Param("pageSize") Integer pageSize);

}
