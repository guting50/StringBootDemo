package com.gt.core.config;

import com.gt.core.intercepors.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.charset.Charset;
import java.util.List;

@Configuration
@EnableTransactionManagement
public class WebConfigurer extends WebMvcConfigurationSupport {

    // 这个方法是用来配置静态资源的，比如html，js，css，等等
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/resources/**");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");

        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    // 这个方法用来注册拦截器，我们自己写好的拦截器需要通过这里添加注册才能生效
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(new LoginInterceptor());
        //需要配置2：----------- 告知拦截器：/static/admin/** 与 /static/user/** 不需要拦截 （配置的是 路径）
        registration.addPathPatterns("/**");
        //排除不需要验证登录用户操作权限的请求
        registration.excludePathPatterns("/user/login**");
        registration.excludePathPatterns("/**.html/**");
        registration.excludePathPatterns("/swagger-resources/**", "/webjars/**", "/swagger-ui.html/**", "/doc.html/**");
    }

//    @Bean
//    public ViewResolver getJspViewResolver() {
//        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
//        internalResourceViewResolver.setPrefix("/WEB-INF/jsp/");//前缀
//        internalResourceViewResolver.setSuffix(".jsp");//后缀
//        internalResourceViewResolver.setOrder(0);//优先级
//        return internalResourceViewResolver;
//    }
//
//    /**
//     * 添加对Freemarker支持
//     */
//    @Bean
//    public FreeMarkerViewResolver getFreeMarkerViewResolver() {
//        FreeMarkerViewResolver freeMarkerViewResolver = new FreeMarkerViewResolver();
//        freeMarkerViewResolver.setCache(false);
//        freeMarkerViewResolver.setPrefix("/WEB-INF/jsp/");//前缀
//        freeMarkerViewResolver.setSuffix(".html");//后缀
//        freeMarkerViewResolver.setRequestContextAttribute("request");
//        freeMarkerViewResolver.setOrder(1);//优先级
//        freeMarkerViewResolver.setContentType("text/html;charset=UTF-8");
//        return freeMarkerViewResolver;
//    }

    // ----start---  解决springboot Controller直接返回String类型带来的乱码
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        converters.add(responseBodyConverter());
        // 这里必须加上加载默认转换器，不然bug玩死人，并且该bug目前在网络上似乎没有解决方案
        addDefaultHttpMessageConverters(converters);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }

    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        return new StringHttpMessageConverter(Charset.forName("UTF-8"));
    }
    // ----end---  解决springboot Controller直接返回String类型带来的乱码
}
