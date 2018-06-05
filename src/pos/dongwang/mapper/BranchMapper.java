package pos.dongwang.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * Created by lodi on 2017/11/21.
 */
public interface BranchMapper {

    public String getLineByBarcode(@Param("outlet") String outlet);
}
