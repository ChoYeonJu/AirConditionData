package air.crawl.condition.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig extends AsyncConfigurerSupport{

	
	
	@Bean(name="fooExecutor")
	public Executor getAsyncExecutor() {
		// TODO Auto-generated method stub
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);		// 기본적으로 실행을 대기하고 있는 thread 개수
		executor.setMaxPoolSize(20);		// 동시 동작 가능한 최대 thread 개수
		executor.setQueueCapacity(500);		// MaxPoolSize를 초과하는 요청이 thread 생성 요청시 해당 내용을 queue에 저장 사용 가능한 thread 여유자리가 발생하면 하나씩 꺼내져서 동작
		executor.setThreadNamePrefix("yjjoh-async-");	// 스레드의 접두사 지정
		executor.initialize();
		return executor;
	}

	
}
