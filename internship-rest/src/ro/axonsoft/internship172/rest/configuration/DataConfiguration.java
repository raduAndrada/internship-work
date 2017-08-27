package ro.axonsoft.internship172.rest.configuration;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * Clasa cu configurarile pentru legatura la baza de date
 *
 * @author Andrada
 *
 */
@Configuration
public class DataConfiguration {

	/* *//**
			 * Setare dataSource si inregistrarea bean-urilor care mapeaza fisierele xml pe
			 * interfatele mapper
			 *//*
				 * 
				 * @Bean public PlatformTransactionManager transactionManager(final DataSource
				 * dataSource) { final DataSourceTransactionManager transactionManager = new
				 * DataSourceTransactionManager(); transactionManager.setDataSource(dataSource);
				 * return transactionManager; }
				 */

	@Bean
	public SqlSessionFactory sqlSessionFactory(final DataSource dataSource) throws Exception {
		final SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);
		sessionFactoryBean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
		return sessionFactoryBean.getObject();
	}

	@Bean
	public MapperScannerConfigurer mybatisMapperScannerConfigurer() {
		final MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
		mapperScannerConfigurer.setBasePackage("ro.axonsoft.internship172.data");
		return mapperScannerConfigurer;
	}

}
