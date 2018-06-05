package pos.dongwang.dao;

import pos.dongwang.dto.TGoodsDto;
import pos.dongwang.dto.TopButtonDto;
import pos.dongwang.model.TGoods;
import pos.dongwang.util.JdbcUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lodi on 2017/11/11.
 */
public class TGoodsDao {



    /**
     * 查詢某按鍵下的菜品
     *
     */

   /* public static List<TGoodsDto> getTGoodsDto(String tbId) {
       *//* String sql = "select\n" +
                "  ID,\n" +
                "  NAME1,\n" +
                "  NAME2 \n" +
                " from TOP_BUTTON \n" ;
*//*

        String sql = "select g.GoodsNo as goodsNo,g.NAME1 as goodName1  from PAGE_CONT as pc inner join PAGE as p on p.ID = pc.PAGE_INDEX inner join T_goods g on g.GoodsNo = pc.GoodsNo where p.TB_ID = " + tbId;
        System.out.println(sql);
        List<TGoodsDto> tGoodsDtos = JdbcUtils.getInstance().findMoreRefResult(sql, null, TGoodsDto.class);
        return tGoodsDtos ;
    }*/


   /* public static List<TGoodsDto> getOutOfStockTGoodsDto() {

        String sql = "select g.GoodsNo as goodsNo,g.NAME1 as goodName1 from T_goods g where g.GoodsNo in (SELECT su.GoodsNo " +
        " FROM SUSPENDI su where su.auto_pause = 'TRUE' ) or g.GoodsNo in (select k.GoodsNo from ITEM_STK k where k.STOCK = 0.00 )";
        System.out.println(sql);
        List<TGoodsDto> tGoodsDtos = JdbcUtils.getInstance().findMoreRefResult(sql, null, TGoodsDto.class);
        return tGoodsDtos ;
    }
*/

/*    *//**
     * 驗證是否存在沽清表中
     * @param goodNo
     * @return
     *//*
    public static boolean existOutOfStockTGoodsDto(String goodNo) {

        String sql = "SELECT g.GoodsNo as goodsNo,g.NAME1 as goodName1  "+
        " FROM SUSPENDI su inner join T_goods g on g.GoodsNo = su.GoodsNo  where su.auto_pause = 'TRUE' and g.GoodsNo ='" + goodNo + "'";
        System.out.println(sql);
        List<TGoodsDto> tGoodsDtos = JdbcUtils.getInstance().findMoreRefResult(sql, null, TGoodsDto.class);
        if(tGoodsDtos != null && tGoodsDtos.size()>0){
          return true;
        }
        return false ;
    }*/
/*
    *//**
     * 執行沽清
     * @param goodNo
     * @return
     *//*
    public static boolean setOutOfStockTGoodsDto(String goodNo) {
        List<String> sqls = new ArrayList<>();
        sqls.add( "insert into SUSPENDI values('"+goodNo+"','"+"TRUE" + "',"+"GETDATE())");
        sqls.add("update T_goods set pause = 'TRUE' where GoodsNo='"+ goodNo  + "'");
        boolean flag = JdbcUtils.getInstance().updateBatch(sqls);
        if(flag){
            return true;
        }
        return false ;
    }*/



  /*  *//**
     * 執行啓用
     * @param goodNo
     * @return
     *//*
    public static boolean startGoodsDto(String goodNo) {
            List<String> sqls = new ArrayList<>();
            sqls.add("update SUSPENDI  set auto_pause= 'FALSE' where GoodsNo='"+goodNo + "'");
            sqls.add("update T_goods set pause = 'FALSE' where GoodsNo='"+ goodNo  + "'");
            boolean flag = JdbcUtils.getInstance().updateBatch(sqls);
            if(flag){
             return true;
            }
            return false ;
    }*/












}
