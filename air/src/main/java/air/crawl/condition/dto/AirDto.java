package air.crawl.condition.dto;

import org.springframework.stereotype.Repository;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Repository
@Data
@Getter
@Setter
public class AirDto {

	private Integer num;
	private String located;
	private String located2;
	private String moni_address;
	private double lon;
	private double lat;
	private String stat;
	private String st_code;
	private double pm2_5;
	private double pm10;
	private double o3;
	private double no2;
	private double co;
	private double so2;
	
	//private geom

}
