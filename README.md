# Loan Repayment Calculator

A Java console app for managing loan repayment records with strong input validation and clear customer identification.

## Features

- Add a loan customer with:
  - customer name
  - loan amount
  - initial paid amount
- Make repayment for an existing customer
- Display all loan records with status
- Auto-handle overpayment and show refund notice
- Validate user input to prevent common mistakes

## Run

From `src`:

```powershell
javac Main.java Loan.java
java Main
```

## Input Rules

### Customer name

- Letters and spaces only
- Validated by regex in input layer and domain layer

### Amounts

- Loan amount: positive number
- Initial paid amount: non-negative number
- Repayment amount: positive number (regex-validated)

### Menu and selection

- Menu option must be in valid range
- Customer selection for repayment uses `Customer ID`

## Overpayment Behavior

If paid amount exceeds loan amount:

- paid amount is capped to loan amount
- notice is shown:

`Excess payment has been refunded.`

This applies to both:

- initial paid amount when adding customer
- repayment operation

## Iteration History

### Iteration 1: Fix overpayment notice timing and duplicate output

- Removed print side effects from balance calculation flow
- Moved refund notice to actual overpayment handling points
- Result: notice appears at repayment time, not duplicated during display

### Iteration 2: Handle overpayment in initial paid amount

- Added cap + refund notice in constructor when initial paid amount is too high

### Iteration 3: Improve robustness

- Added safe input parsing loops
- Added validation for menu, index, name, and amounts
- Added domain-level validation in `Loan` to prevent invalid state

### Iteration 4: Resolve duplicate-name confusion

- Added unique `Customer ID` per loan record
- Show ID after creation and in customer lists
- Repayment now selects by `Customer ID` instead of raw index

### Iteration 5: Switch checks to regex where requested

- Customer name validation switched to regex
- Repayment amount validation switched to regex
- Domain name validation aligned to regex

## Current Limitations

- In-memory storage only (no persistence after exit)
- Fixed capacity array (`Loan[5]`)

## Suggested Next Steps

1. Replace fixed array with `ArrayList<Loan>`
2. Add search by ID/name
3. Add save/load from file or database
4. Add unit tests for validation and overpayment logic

---------------------------------------------------------------------------------------------------------------

# 贷款还款计算器

一个基于 Java 控制台的贷款还款管理程序，具备较强的输入校验能力，并通过唯一客户标识避免用户混淆。

## 功能

- 新增贷款客户，包含：
  - 客户姓名
  - 贷款金额
  - 初始已还金额
- 为已有客户进行还款
- 展示全部贷款记录与状态
- 自动处理超额还款并提示退款
- 对用户输入进行校验，避免常见错误

## 运行方式

在 `src` 目录下执行：

```powershell
javac Main.java Loan.java
java Main
```

## 输入规则

### 客户姓名

- 仅允许字母和空格
- 在输入层与领域层均使用正则校验

### 金额

- 贷款金额：必须为正数
- 初始已还金额：必须为非负数
- 还款金额：必须为正数（正则校验）

### 菜单与选择

- 菜单选项必须在有效范围内
- 还款时按 `Customer ID` 选择客户

## 超额还款行为

当已还金额超过贷款金额时：

- 已还金额会被截断到贷款金额
- 系统会提示：

`Excess payment has been refunded.`

该规则适用于：

- 新增客户时输入的初始已还金额
- 还款操作

## 迭代记录

### 迭代 1：修复超额还款提示时机与重复输出

- 去除余额计算流程中的打印副作用
- 将退款提示移动到实际发生超额处理的位置
- 结果：提示在还款时出现，展示记录时不再重复

### 迭代 2：处理新增客户时初始已还金额超额

- 在构造函数中加入超额截断与退款提示

### 迭代 3：提升健壮性

- 增加安全输入解析与循环重试
- 增加菜单、索引、姓名、金额校验
- 在 `Loan` 中增加领域层校验，防止对象进入非法状态

### 迭代 4：解决同名用户混淆

- 为每条贷款记录增加唯一 `Customer ID`
- 新增成功后显示 ID，并在客户列表中展示 ID
- 还款从按索引改为按 `Customer ID` 选择

### 迭代 5：按需求将校验改为正则

- 客户姓名校验改为正则
- 还款金额校验改为正则
- 领域层姓名校验同步改为正则

## 当前限制

- 仅内存存储（程序退出后数据丢失）
- 固定容量数组（`Loan[5]`）

## 后续建议

1. 将固定数组替换为 `ArrayList<Loan>`
2. 增加按 ID/姓名搜索
3. 增加文件或数据库持久化
4. 为校验与超额还款逻辑补充单元测试
