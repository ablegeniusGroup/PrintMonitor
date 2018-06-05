package pos.dongwang.mapper;

import org.apache.ibatis.annotations.Param;
import pos.dongwang.dto.TGoodsDto;

import java.util.List;

/**
 * Created by lodi on 2017/11/21.
 */
public interface TGoodsDtoMapper {

    public  List<TGoodsDto> getTGoodsDto(@Param("tbId") String tbId);

    public List<TGoodsDto> getOutOfStockTGoodsDto();

    public  List<TGoodsDto>  existOutOfStockTGoodsDto(@Param("goodNo") String goodNo);

    public  void insertOutOfStockTGoodsDto(@Param("goodNo") String goodNo);

    public void  updateTGoodsPause(@Param("goodNo") String goodNo,@Param("state") String state);

    public void  updateSUSPENDIPause(@Param("goodNo") String goodNo,@Param("state") String state);


}
