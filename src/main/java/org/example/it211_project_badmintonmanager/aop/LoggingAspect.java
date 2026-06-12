package org.example.it211_project_badmintonmanager.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // Tạo bộ ghi log chuyên nghiệp (thay vì dùng System.out.println)
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * @Around: Can thiệp vào TRƯỚC và SAU khi một hàm được chạy
     * "execution(* org.example...controller..*(..))": Bắt tất cả các hàm trong thư mục controller
     */
    @Around("execution(* org.example.it211_project_badmintonmanager.controller..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        // 1. Ghi nhận thời gian bắt đầu
        long start = System.currentTimeMillis();

        // 2. Cho phép cái API của bạn chạy (Ví dụ: chạy hàm Đổi mật khẩu, Đặt sân...)
        Object proceed = joinPoint.proceed();

        // 3. Sau khi API chạy xong, ghi nhận thời gian kết thúc
        long executionTime = System.currentTimeMillis() - start;

        // 4. In ra màn hình Console log thời gian chạy
        log.info("[LOG HỆ THỐNG] Hàm {} đã chạy xong trong {} ms",
                joinPoint.getSignature().toShortString(), executionTime);

        // Trả kết quả về cho Postman như bình thường
        return proceed;
    }
}