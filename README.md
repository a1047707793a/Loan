# Loan Repayment Calculator

A Java console application for managing simple loan repayment records. The project has been incrementally upgraded to keep the original behavior while improving maintainability, exception handling, and automated testing.

---

## Current Status

This project now includes:

- **Requirement 1 — Interface-based business rules**
  - Core business rules are separated behind `interfaces.LoanRules`
  - `DefaultLoanRules` provides the current default implementation
- **Requirement 2 — Exception handling**
  - Invalid business operations use semantic custom exceptions
  - The console flow handles invalid input safely without crashing
- **Requirement 3 — Automated testing**
  - A lightweight test package under `src/test`
  - Coverage for normal cases, boundary cases, invalid input, and exception handling

---

## Features

- Add a loan customer with:
  - customer name
  - loan amount
  - initial paid amount
- Make repayment for an existing customer by `Customer ID`
- Display all loan records with:
  - customer ID
  - customer name
  - loan amount
  - paid amount
  - remaining balance
  - status
- Auto-handle overpayment and show refund notice
- Validate user input in both input layer and business layer
- Handle invalid input safely without terminating the program unexpectedly

---

## Project Structure

```text
Loan/
├─ README.md
└─ src/
   ├─ Main.java
   ├─ Loan.java
   ├─ DefaultLoanRules.java
   ├─ ValidationPatterns.java
   ├─ interfaces/
   │  └─ LoanRules.java
   ├─ exceptions/
   │  ├─ LoanProcessingException.java
   │  ├─ InvalidCustomerNameException.java
   │  ├─ InvalidAmountException.java
   │  ├─ InvalidCustomerIdException.java
   │  └─ LoanAlreadyCompletedException.java
   ├─ test/
   │  ├─ TestAssertions.java
   │  ├─ ReflectionSupport.java
   │  ├─ LoanDomainTests.java
   │  ├─ LoanRulesTests.java
   │  ├─ MainFlowTests.java
   │  └─ TestRunner.java
   └─ requirement*.md
```

---

## Core Design

### 1. Entity layer

`Loan.java` represents the loan record itself and keeps the object in a valid state.

Main responsibilities:

- store loan data
- expose loan information through getters
- delegate rule checks to `LoanRules`
- update repayment state safely

### 2. Rule layer

`interfaces.LoanRules` defines the business rule contract.

`DefaultLoanRules.java` implements the current loan policy, including:

- customer name validation
- loan amount validation
- initial paid amount validation
- repayment validation
- remaining balance calculation
- loan status calculation
- overpayment capping

### 3. Exception layer

Business exceptions are now separated into dedicated classes:

- `LoanProcessingException`
- `InvalidCustomerNameException`
- `InvalidAmountException`
- `InvalidCustomerIdException`
- `LoanAlreadyCompletedException`

This makes error handling clearer and improves testability.

### 4. Validation utility

`ValidationPatterns.java` centralizes shared regex patterns, including:

- `NAME_PATTERN`
- `POSITIVE_AMOUNT_PATTERN`

---

## Input Rules

### Customer name

- letters and spaces only
- validated in both the input layer and the rules layer

### Amounts

- loan amount: must be a positive number
- initial paid amount: must be a non-negative number
- repayment amount: must be a positive number

### Menu and customer selection

- menu options must stay in the valid range
- repayment uses `Customer ID`
- invalid customer IDs are handled safely through exception handling

---

## Overpayment Behavior

If the paid amount exceeds the loan amount:

- paid amount is capped to the loan amount
- the system shows:

`Excess payment has been refunded.`

This applies to:

- initial paid amount during loan creation
- repayment operations

---

## Exception Handling Behavior

The current program safely handles situations such as:

- blank customer names
- negative or zero invalid amounts
- invalid customer IDs
- invalid repayment values
- repayment on a completed loan
- exhausted input stream during console execution

Instead of crashing, the program either:

- asks the user to re-enter input, or
- prints a clear error message and continues safely, or
- exits cleanly if the input stream ends

---

## How to Run

From the project root:

```powershell
javac src\interfaces\*.java src\exceptions\*.java src\*.java
java -cp src Main
```

---

## How to Run Tests

From the project root:

```powershell
javac src\interfaces\*.java src\exceptions\*.java src\*.java src\test\*.java
java -cp src test.TestRunner
```

Current automated test suites:

- `LoanDomainTests`
- `LoanRulesTests`
- `MainFlowTests`

These tests cover:

- **Normal Case**
- **Boundary Case**
- **Invalid Input**
- **Exception Handling**

---

## Current Limitations

- in-memory storage only
- fixed-capacity array storage (`Loan[5]`)
- console-based UI only
- no persistence after exit

---

## Suggested Next Steps

1. Replace the fixed array with a dynamic collection such as `ArrayList<Loan>`
2. Add search/filter by ID or name
3. Add persistence via file or database
4. Move default-package classes into named packages for cleaner Java project structure
5. Optionally migrate lightweight tests to JUnit if a build tool is introduced

---

# 贷款还款计算器

这是一个基于 Java 控制台的贷款还款管理程序。项目在保留原有功能的前提下，已经逐步完成了业务规则抽离、异常处理增强以及自动化测试补充。

---

## 当前版本状态

当前项目已经完成：

- **Requirement 1 — 使用 Interface 分离业务规则**
  - 通过 `interfaces.LoanRules` 抽离业务规则
  - 由 `DefaultLoanRules` 提供当前默认实现
- **Requirement 2 — Exception Handling**
  - 针对常见错误输入与非法业务状态使用语义化异常
  - 主程序能够安全处理错误而不轻易崩溃
- **Requirement 3 — Unit Testing**
  - 在 `src/test` 下建立轻量自动化测试包
  - 覆盖正常情况、边界情况、错误输入和异常处理

---

## 功能

- 新增贷款客户，包含：
  - 客户姓名
  - 贷款金额
  - 初始已还金额
- 按 `Customer ID` 为已有客户还款
- 展示全部贷款记录，包括：
  - 客户编号
  - 客户姓名
  - 贷款金额
  - 已还金额
  - 剩余金额
  - 状态
- 自动处理超额还款并提示退款
- 在输入层与业务层双重校验数据
- 对非法输入进行安全处理，尽量避免程序异常终止

---

## 项目结构

```text
Loan/
├─ README.md
└─ src/
   ├─ Main.java
   ├─ Loan.java
   ├─ DefaultLoanRules.java
   ├─ ValidationPatterns.java
   ├─ interfaces/
   │  └─ LoanRules.java
   ├─ exceptions/
   │  ├─ LoanProcessingException.java
   │  ├─ InvalidCustomerNameException.java
   │  ├─ InvalidAmountException.java
   │  ├─ InvalidCustomerIdException.java
   │  └─ LoanAlreadyCompletedException.java
   ├─ test/
   │  ├─ TestAssertions.java
   │  ├─ ReflectionSupport.java
   │  ├─ LoanDomainTests.java
   │  ├─ LoanRulesTests.java
   │  ├─ MainFlowTests.java
   │  └─ TestRunner.java
   └─ requirement*.md
```

---

## 核心设计

### 1. 实体层

`Loan.java` 表示单条贷款记录，负责保证对象状态合法。

主要职责：

- 保存贷款数据
- 提供查询 getter
- 将规则判断委托给 `LoanRules`
- 安全更新还款状态

### 2. 规则层

`interfaces.LoanRules` 定义业务规则接口。

`DefaultLoanRules.java` 实现当前业务规则，包括：

- 姓名校验
- 贷款金额校验
- 初始已还金额校验
- 还款金额校验
- 剩余金额计算
- 状态计算
- 超额支付截断

### 3. 异常层

当前已引入的业务异常包括：

- `LoanProcessingException`
- `InvalidCustomerNameException`
- `InvalidAmountException`
- `InvalidCustomerIdException`
- `LoanAlreadyCompletedException`

这样做可以让异常语义更清晰，也更利于测试。

### 4. 校验工具

`ValidationPatterns.java` 用于集中维护共享正则规则，包括：

- `NAME_PATTERN`
- `POSITIVE_AMOUNT_PATTERN`

---

## 输入规则

### 客户姓名

- 只允许字母和空格
- 在输入层与规则层都会进行校验

### 金额

- 贷款金额：必须为正数
- 初始已还金额：必须为非负数
- 还款金额：必须为正数

### 菜单与客户选择

- 菜单输入必须处于有效范围
- 还款时使用 `Customer ID`
- 非法客户编号会被安全处理，不会直接导致程序崩溃

---

## 超额还款行为

如果已还金额超过贷款金额：

- 已还金额会被截断到贷款金额
- 系统会提示：

`Excess payment has been refunded.`

该规则适用于：

- 新增贷款时输入的初始已还金额
- 后续还款操作

---

## 异常处理行为

当前系统可以安全处理以下情况：

- 空白姓名
- 非法金额（负数、零值等不符合规则的金额）
- 非法客户编号
- 非法还款金额
- 对已完成贷款继续还款
- 控制台输入流意外结束

系统会根据情况：

- 提示用户重新输入，或
- 输出清晰错误信息并继续运行，或
- 在输入流结束时安全退出

---

## 运行方式

在项目根目录执行：

```powershell
javac src\interfaces\*.java src\exceptions\*.java src\*.java
java -cp src Main
```

---

## 测试方式

在项目根目录执行：

```powershell
javac src\interfaces\*.java src\exceptions\*.java src\*.java src\test\*.java
java -cp src test.TestRunner
```

当前自动化测试套件包括：

- `LoanDomainTests`
- `LoanRulesTests`
- `MainFlowTests`

覆盖内容包括：

- **正常情况（Normal Case）**
- **边界情况（Boundary Case）**
- **错误输入（Invalid Input）**
- **异常处理（Exception Handling）**

---

## 当前限制

- 仅内存存储
- 使用固定容量数组（`Loan[5]`）
- 仅提供控制台界面
- 程序退出后数据不会保留

---

## 后续建议

1. 将固定数组替换为动态集合，例如 `ArrayList<Loan>`
2. 增加按 ID 或姓名搜索
3. 增加文件或数据库持久化
4. 将默认包中的类进一步整理到命名包中，优化 Java 项目结构
5. 如果后续引入构建工具，可将当前轻量测试迁移到 JUnit
