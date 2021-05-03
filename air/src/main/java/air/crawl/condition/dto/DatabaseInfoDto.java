package air.crawl.condition.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DatabaseInfoDto {

	private String column_name;
	private String data_type;

}
