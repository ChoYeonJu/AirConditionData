package air.crawl.condition.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import air.crawl.condition.dto.AirDto;
import air.crawl.condition.dto.ParameterDto;

@Mapper
public interface AirMapper {
	
	List<AirDto> select();
	
	String selectNow();
	
	// void updateData(String columName, double value, String guName);
	void updateData(ParameterDto param);
	
	List<String> getColumnList();
	
}
