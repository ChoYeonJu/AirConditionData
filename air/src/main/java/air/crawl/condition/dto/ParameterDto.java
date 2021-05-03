package air.crawl.condition.dto;

import org.springframework.stereotype.Repository;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Repository
@Data
@Getter
@Setter
public class ParameterDto {

	private String columName;
	private double value;
	private String guName;
	
}
