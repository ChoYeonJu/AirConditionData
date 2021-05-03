package air.crawl.condition.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TestDto {

	private Integer num;
	private String located;
	private String located2;
	private String moni_address;
	private double lon;
	private double lat;
	private String stat;
	private String st_code;
	private Integer ps_data;
	//private geom

}
