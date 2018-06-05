package pos.dongwang.mapper;

import org.apache.ibatis.annotations.Param;
import pos.dongwang.dto.IsBilledDto;
import pos.dongwang.dto.PosOrderDto;
import pos.dongwang.enums.PrintStateEnum;
import pos.dongwang.model.PosOrder;

import java.util.List;
import java.util.Map;

/**
 * Created by lodi on 2017/11/21.
 */
public interface PosOrderDtoMapper {

    public List<PosOrderDto> getPosOrderList(Map<String, Object> map);

    public   List<PosOrderDto> getPosOrderListInit(Map<String, Object> map);

    public  void updatePrintState(@Param("newPrintStatEnum") String newPrintStatEnum, @Param("oldPrintStatEnum") String oldPrintStatEnum, @Param("bill_no") String bill_no, @Param("order_idx") long order_idx, @Param("type") String type);

    public void updatePrintStateWithoutOldPrintState(@Param("newPrintStatEnum") String newPrintStatEnum, @Param("bill_no") String bill_no, @Param("order_idx") long order_idx, @Param("type") String type);

    public  void pauseItem(@Param("posOrder") PosOrder posOrder);

    public  IsBilledDto isBilled(@Param("bill_no") String bill_no,@Param("type") String type,@Param("sub_no") String sub_no);

    public  Long getOrderCountsByBillNo(@Param("bill_no")String bill_no);

    public List<PosOrderDto> getHangPosOrderList(Map<String,Object> map);


    public String  getTableNumByOrderIdx(Long orderIdx);

    public  String  checkBilled(@Param("bill_no") String bill_no,@Param("type") String type,@Param("sub_no") String sub_no);

}
