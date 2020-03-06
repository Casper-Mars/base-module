package org.r.base.payment.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author casper
 * @date 19-12-24 上午11:55
 **/
@Aspect
public class ConfigCheckAop {

    @Pointcut("within(org.r.base.payment.service.PaymentService+)")
    private void pointCut() {

    }

    /**
     * 拦截检查配置信息是否为空
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "pointCut()")
    public Object arount(ProceedingJoinPoint joinPoint) throws Throwable {
//        Field declaredField = joinPoint.getTarget().getClass().getSuperclass().getDeclaredFields()[1];
//        declaredField.setAccessible(true);
//        Object o = declaredField.get(joinPoint.getTarget());
//        if (o == null) {
//            System.out.println("is null");
//        }


        return joinPoint.proceed();
    }


}
