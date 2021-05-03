package air.crawl.condition.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import air.crawl.condition.dto.AirDto;
import air.crawl.condition.dto.ParameterDto;
import air.crawl.condition.mappers.AirMapper;

@Service
public class AirService{

	@Autowired
	private AirMapper airMapper;
	
	public void read() {
		
	}
	
	public List<AirDto> select() throws Exception {
		// TODO Auto-generated method stub
		return airMapper.select();
	}

	public String selectNow() throws Exception {
		// TODO Auto-generated method stub
		return airMapper.selectNow();
	}

	public void updateData(ParameterDto param) {
		// TODO Auto-generated method stub
		airMapper.updateData(param);
	}

	public List<String> getColumnList() {
		return airMapper.getColumnList();
	}
	
	public String test() {
		return "안녕하세요";
	}
}
