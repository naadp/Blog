package com.yao.conf;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;


@Configuration
public class ConfigBean // boot -->spring applicationContext.xml --- @Configuration配置 ConfigBean =
						// applicationContext.xml
{


	//显示声明CommonsMultipartResolver为mutipartResolver
	@Bean(name = "multipartResolver")
	public MultipartResolver multipartResolver(){
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setDefaultEncoding("UTF-8");
		resolver.setResolveLazily(true);//resolveLazily属性启用是为了推迟文件解析，以在在UploadAction中捕获文件大小异常
		resolver.setMaxInMemorySize(40960);
		resolver.setMaxUploadSize(50*1024*1024);//上传文件大小 50M 50*1024*1024
		return resolver;
	}

	@Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return new EmbeddedServletContainerCustomizer(){

			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				 container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/errorPage"));
			}
        };
    }

	@Bean
	public ServletRegistrationBean dispatcherRegistration(DispatcherServlet dispatcherServlet) {
		ServletRegistrationBean reg = new ServletRegistrationBean(dispatcherServlet);
		reg.getUrlMappings().clear();
		reg.addUrlMappings("*.html");
		reg.addUrlMappings("*.do");
		return reg;
	}

}
