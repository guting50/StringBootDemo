package com.gt.core.config;

import com.google.gson.Gson;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

@Aspect
@Component
public class WebLogAspect {

    private final static Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

    /**
     * 以 controller 包下定义的所有请求为切入点
     */
    @Pointcut("execution(public * *..Controller..*.*(..))")
    public void webLog() {
    }

    /**
     * 在切点之前织入
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 打印请求相关参数
        logger.info("========================================== Start >>>==========================================");
        // 打印 Http method
        logger.info("HTTP Method    : {}", request.getMethod());
        // 打印请求 url
        logger.info("URL            : {}", request.getRequestURL().toString());
        // 打印请求入参
        List<Object> args = new ArrayList(Arrays.asList(joinPoint.getArgs()));
        Iterator iterator = args.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (HttpServletRequest.class.isAssignableFrom(obj.getClass())
                    || HttpServletResponse.class.isAssignableFrom(obj.getClass())
                    || MultipartFile.class.isAssignableFrom(obj.getClass())) {
                iterator.remove();
            }
        }
        String[] methodNames = getFieldsName(joinPoint);
        if (methodNames != null) {
            Map map = new HashMap();
            for (int i = 0; i < args.size(); i++) {
                map.put(methodNames[i], args.get(i));
            }
            logger.info("Request Args   : {}", new Gson().toJson(map));
        } else
            logger.info("Request Args   : {}", new Gson().toJson(args));
        // 打印调用 controller 的全路径以及执行方法
        logger.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        // 打印请求的 IP
        logger.info("IP             : {}", request.getRemoteAddr());
    }

    /**
     * 在切点之后织入
     *
     * @throws Throwable
     */
    @After("webLog()")
    public void doAfter() throws Throwable {
        logger.info("=========================================== End >>>===========================================");
        // 每个请求之间空一行
        logger.info("");
    }

    /**
     * 环绕
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 打印出参
        logger.info("========================================== Start <<<==========================================");
        // 打印请求 url
        logger.info("URL            : {}", request.getRequestURL().toString());
        logger.info("Response Args  : {}", new Gson().toJson(result));
        // 执行耗时
        logger.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        logger.info("=========================================== End <<<===========================================");
        logger.info("");
        return result;
    }

    //返回方法的参数名
    private static String[] getFieldsName(JoinPoint joinPoint) {
        try {
            String classType = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();
            Class<?>[] classes = new Class[args.length];
            for (int k = 0; k < args.length; k++) {
                if (!args[k].getClass().isPrimitive()) {
                    //获取的是封装类型而不是基础类型
                    String result = args[k].getClass().getName();
                    Class s = map.get(result);
                    Class ss = s == null ? args[k].getClass() : s;
                    if (HttpServletRequest.class.isAssignableFrom(ss)) {
                        ss = HttpServletRequest.class;
                    }
                    if (HttpServletResponse.class.isAssignableFrom(ss)) {
                        ss = HttpServletResponse.class;
                    }
                    if (MultipartFile.class.isAssignableFrom(ss)) {
                        ss = MultipartFile.class;
                    }
                    classes[k] = ss;
                }
            }
            ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
            //获取指定的方法，第二个参数可以不传，但是为了防止有重载的现象，还是需要传入参数的类型
            Method method = Class.forName(classType).getMethod(methodName, classes);
            String[] parameterNames = pnd.getParameterNames(method);
            return parameterNames;
        } catch (Exception e) {
        }
        return null;
    }

    private static HashMap<String, Class> map = new HashMap<String, Class>() {
        {
            put("java.lang.Integer", int.class);
            put("java.lang.Double", double.class);
            put("java.lang.Float", float.class);
            put("java.lang.Long", long.class);
            put("java.lang.Short", short.class);
            put("java.lang.Boolean", boolean.class);
            put("java.lang.Char", char.class);
        }
    };
}
