package air.crawl.condition.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import air.crawl.condition.dto.TestDto;

@Mapper
public interface TestMapper {

	List<TestDto> select() throws Exception;
	
	String selectNow() throws Exception;
}
