# Requirement 3 — Unit Testing 说明

## 目标

`test` 文件夹用于为当前贷款系统提供自动化测试，满足 `Requirement 3 — Unit Testing` 的要求。

本轮测试重点覆盖以下四类场景：

- 正常情况（Normal Case）
- 边界情况（Boundary Case）
- 错误输入（Invalid Input）
- Exception Handling

这些测试的目标是：

- 验证核心业务逻辑正确
- 验证异常输入不会破坏系统行为
- 验证异常会被正确抛出或正确处理
- 为后续部署提供回归保障

---

## test 文件夹中的文件说明

### 1. `TestAssertions.java`

这是测试辅助类，用来提供简单断言能力，避免引入额外测试框架依赖。

主要提供：

- `assertEquals(...)`：比较普通对象是否相等
- `assertBigDecimalEquals(...)`：比较 `BigDecimal` 数值是否相等
- `assertTrue(...)`：断言布尔条件
- `assertContains(...)`：断言输出中包含指定文本
- `assertThrows(...)`：断言某段逻辑会抛出指定异常

设计思路：

- 让测试可以直接运行，不依赖 JUnit
- 保持项目最小改动
- 让测试更适合当前作业 / 审计场景

---

### 2. `ReflectionSupport.java`

这是测试反射工具类。

因为当前主程序类和部分业务类位于默认包，而测试类位于 `test` 包中，Java 不允许测试类直接引用默认包类，因此这里通过反射来访问：

- `Loan`
- `DefaultLoanRules`
- `Main`

主要提供：

- `newLoan(...)`：通过反射创建 `Loan` 对象
- `getDefaultLoanRules()`：获取默认规则对象
- `invoke(...)` / `invokeNoArgs(...)`：调用目标方法
- `invokeMain(...)`：执行主程序入口

设计思路：

- 不重写原有代码结构
- 不强行迁移现有类到新包中
- 在“最小修改”前提下，让测试依然可以自动运行

---

### 3. `LoanDomainTests.java`

这个测试类主要针对 `Loan` 实体类的核心业务行为进行测试。

#### 已覆盖样例

##### Normal Case

- 创建一个正常贷款对象，例如：
  - customer name = `Alice`
  - loan amount = `1000`
  - paid amount = `200`
- 验证：
  - 姓名是否保存正确
  - 贷款金额是否正确
  - 已还金额是否正确
  - 剩余金额是否正确
  - 状态是否为 `Outstanding`

##### Boundary Case

- 初始已还金额超过贷款金额，例如：
  - loan amount = `1000`
  - paid amount = `1200`
- 验证：
  - 已还金额是否被截断为 `1000`
  - 是否标记为发生过调整
  - 状态是否为 `Completed`

- 还款时发生超额支付，例如：
  - 初始 paid amount = `950`
  - 再支付 `100`
- 验证：
  - 是否返回“已调整”标记
  - paid amount 是否被封顶到 loan amount
  - remaining balance 是否为 `0`

##### Invalid Input

- 空白姓名是否抛出 `InvalidCustomerNameException`
- 负数贷款金额是否抛出 `InvalidAmountException`

##### Exception Handling

- 已完成贷款再次付款时，是否抛出 `LoanAlreadyCompletedException`

设计思路：

- 优先验证 `Loan` 这个核心业务对象
- 覆盖构造、计算、状态、还款和异常分支
- 确保业务规则变更后，最先发现回归问题

---

### 4. `LoanRulesTests.java`

这个测试类主要针对 `DefaultLoanRules` 中的业务规则实现进行测试。

#### 已覆盖样例

##### Normal Case

- 验证合法客户姓名（如 `Alice Smith`）可以通过校验

##### Boundary Case

- 验证 `normalizePaidAmount(...)` 在超额时，会把已付金额截断到贷款总额

##### Invalid Input

- 验证非法 repayment 输入（如 `0`）会被判定为无效
- 验证非法客户姓名会抛出 `InvalidCustomerNameException`
- 验证 `applyRepayment(...)` 对 `0` 金额还款抛出 `InvalidAmountException`

##### Exception Handling

- 验证对已完成贷款再次调用 `applyRepayment(...)` 时，会抛出 `LoanAlreadyCompletedException`

设计思路：

- 将“规则层”和“实体层”分开测试
- 确保 `Requirement 1` 引入的接口化规则具有独立可验证性
- 让未来银行政策变化时，可以单独回归规则实现

---

### 5. `MainFlowTests.java`

这个测试类主要验证主程序流程中对异常输入的处理是否安全。

该类通过模拟控制台输入 / 输出，测试 `Main.main(...)` 的运行结果。

#### 已覆盖样例

##### Invalid Input

- 输入非法客户编号，例如：
  - 先创建一个正常贷款
  - 然后输入不存在的 customer ID（如 `99`）
- 验证：
  - 是否输出 `Error: Invalid customer ID.`
  - 程序是否继续运行而不是终止

- 输入空白姓名后再改成合法姓名
- 验证：
  - 是否提示 `Invalid name. Use letters and spaces only.`
  - 是否仍然可以成功创建贷款

##### Exception Handling

- 输入流提前结束时，是否安全退出
- 验证：
  - 是否输出 `Input stream ended. Exiting program...`
  - 是否避免程序崩溃或死循环

设计思路：

- 不只测试“方法抛异常”，还测试“主程序是否正确处理异常”
- 覆盖 `Requirement 2` 中关于错误输入不能导致系统终止的要求
- 让测试更贴近用户实际操作场景

---

### 6. `TestRunner.java`

这是测试入口类，用于统一执行所有测试套件。

当前会执行：

- `LoanDomainTests`
- `LoanRulesTests`
- `MainFlowTests`

输出形式为：

- `[PASS] ...`
- `[FAIL] ...`
- 最终汇总通过情况

设计思路：

- 提供一个简单的自动化入口
- 不依赖外部测试框架
- 方便在当前项目中直接编译运行

---

## 整体测试思路

本次测试方案遵循以下原则：

### 1. 最小依赖

没有引入 JUnit、Maven、Gradle 等额外依赖，而是使用纯 Java 测试入口，目的是：

- 降低当前项目改动范围
- 保证在现有结构上快速补齐自动化测试
- 满足“最小修改”的审计要求

### 2. 分层测试

测试按职责分成三层：

- **实体层测试**：验证 `Loan` 的行为
- **规则层测试**：验证 `DefaultLoanRules` 的业务规则
- **主流程测试**：验证 `Main` 对输入和异常的处理

这样可以更清晰地定位问题来源。

### 3. 覆盖 Requirement 3 要求的四大类

#### Normal Case

- 正常创建贷款
- 正常还款
- 合法姓名通过规则校验

#### Boundary Case

- 初始付款超额
- 还款超额
- 余额被封顶为 `0`

#### Invalid Input

- 空白姓名
- 负数贷款金额
- 非法 repayment 输入
- 非法 customer ID

#### Exception Handling

- 已完成贷款再次付款
- 输入流提前结束
- 主流程对错误信息的安全处理

---

## 为什么采用这种测试方案

当前项目有两个现实约束：

1. 系统不能推倒重写
2. 现有主类位于默认包，不适合直接接入标准测试框架结构

因此本次选择：

- 保留现有代码结构
- 在 `src/test` 下新增测试包
- 通过反射与辅助断言完成自动化测试

这种方式虽然比标准 JUnit 工程更轻量，但非常适合当前项目阶段，因为它：

- 能快速落地
- 覆盖核心需求
- 不破坏现有代码结构
- 能满足 Unit Testing 的基本审计要求

---

## 当前结论

`test` 文件夹已经提供了一套可运行的自动化测试，覆盖了：

- 核心业务行为
- 边界处理逻辑
- 非法输入校验
- 异常处理路径
- 主程序控制台流程

这套测试的主要作用是：

> 在不重写项目结构的前提下，为当前系统提供一组可执行、可回归、可审计的自动化测试样例。

