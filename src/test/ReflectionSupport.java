package test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;

final class ReflectionSupport {
    private ReflectionSupport() {
    }

    static Object newLoan(String customerName, String loanAmount, String paidAmount) throws Exception {
        Class<?> loanClass = Class.forName("Loan");
        Constructor<?> constructor = loanClass.getConstructor(String.class, BigDecimal.class, BigDecimal.class);
        return constructor.newInstance(customerName, new BigDecimal(loanAmount), new BigDecimal(paidAmount));
    }

    static Object getDefaultLoanRules() throws Exception {
        Class<?> rulesClass = Class.forName("DefaultLoanRules");
        Method method = rulesClass.getMethod("getInstance");
        return method.invoke(null);
    }

    static void invokeMain(String... args) throws Exception {
        Class<?> mainClass = Class.forName("Main");
        Method method = mainClass.getMethod("main", String[].class);
        method.invoke(null, (Object) args);
    }

    static Object invoke(Object target, String methodName, Class<?>[] parameterTypes, Object... args) throws Exception {
        Method method = target.getClass().getMethod(methodName, parameterTypes);
        return method.invoke(target, args);
    }

    static Object invokeNoArgs(Object target, String methodName) throws Exception {
        return invoke(target, methodName, new Class<?>[0]);
    }
}

