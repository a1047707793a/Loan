# Requirement 1 迭代说明

不重写系统，只把“会变化的业务规则”从现有类里抽成接口，让 `Loan` 和 `Main` 改为依赖接口/实现，从而满足审计对可维护性的要求。

## 阅读重点

- 明确当前哪些地方属于“业务规则”
- 明确哪些内容应该抽到 Interface
- 明确哪些类需要改、哪些尽量不动
- 明确最小改动顺序与风险控制

## 一、我对 Requirement 1 的理解

这条需求的核心不是“为了用 Interface 而用 Interface”，而是：

> 把未来可能变化的银行政策 / 业务规则，与现有类分离。

这样以后政策变了，比如：

- 姓名校验规则变了
- 贷款金额规则变了
- 是否允许超额付款变了
- 贷款状态判定规则变了

你不需要直接去改 `Loan` 这种核心类，而是换一个规则实现类就行。

## 二、当前系统里“业务规则”在哪里

根据你现在的代码结构，业务规则主要散落在这几个地方：

### 1. `Loan.java`

这是当前最核心的业务规则承载者，里面包括：

- 构造函数里的输入合法性校验
  - 姓名是否合法
  - `loanAmount` 是否大于 `0`
  - `paidAmount` 是否非负
- 初始已付款超过贷款金额时如何处理
- `calculateRemainingBalance()` 的剩余金额规则
- `getLoanStatus()` 的状态判定规则
- `makePayment()` 的还款合法性规则
  - 付款必须大于 `0`
  - 已完成贷款不能继续付款
  - 超额付款时如何截断

### 2. `Main.java`

这里本来应该主要负责控制台交互，但现在也掺杂了一部分规则：

- 录入姓名时做了姓名合法性校验
- 输入 repayment 时做了金额格式校验
- 只列出未还清贷款供用户还款
- 已完成贷款不能再付款

### 3. `ValidationPatterns.java`

这个类现在只是“规则片段”的集中地，保存了 regex 常量，但它不是完整的业务规则抽象。

## 三、Requirement 1 最适合的最小改法

### 结论先说

最小改动方案：**先引入 `1` 个接口 + `1` 个默认实现类。**

我建议第一步不要拆很多接口，否则对你这个项目来说会显得过度设计。

最稳妥的是：

- 一个接口：`LoanRules`
- 一个实现类：`DefaultLoanRules`

这样最符合你“不能推倒重来、只能最小修改”的约束。

## 四、建议新增的 Interface 设计

### 1. 接口：`LoanRules`

职责：定义所有“银行政策级”的规则。

这个接口里可以包含这类规则能力：

- 校验 customer name
- 校验 loan amount
- 校验 initial paid amount
- 校验 repayment amount
- 处理 overpayment（超额付款）
- 计算 remaining balance
- 判定 loan status

它的意义是：

- `Loan` 不再自己“决定规则是什么”
- `Loan` 只负责“持有数据 + 调用规则”

### 2. 默认实现：`DefaultLoanRules`

职责：把你当前 `Loan.java` 里面的现有逻辑搬进去，保持现有行为不变。

也就是说：

- 现在怎么判断合法，以后默认实现还是怎么判断
- 现在怎么判定 `Completed / Outstanding`，以后默认实现还是一样
- 现在超额付款如何截断，也保持一致

这样做的好处是：

- 满足 Requirement 1
- 不影响原功能
- 改动风险最小

## 五、现有类应该怎么改

### A. `Loan.java` 应该怎么改

`Loan` 应该继续保留为实体类 / 核心业务对象，不要重写，不要删字段，不要改外部调用方式太多。

#### `Loan` 里保留的内容

- 字段：
  - `loanId`
  - `customerName`
  - `loanAmount`
  - `paidAmount`
- 现有 getter
- ID 自增逻辑
- 对外公开的方法名尽量保留：
  - `calculateRemainingBalance()`
  - `getLoanStatus()`
  - `makePayment(...)`

#### `Loan` 里要改变的内容

把原来内部写死的规则，改为调用 `LoanRules`。

也就是说：

- 构造器中的校验 → 调用 `LoanRules`
- 初始 overpayment 处理 → 调用 `LoanRules`
- `calculateRemainingBalance()` → 调用 `LoanRules`
- `getLoanStatus()` → 调用 `LoanRules`
- `makePayment(...)` → 调用 `LoanRules`

#### 这样做的好处

外部代码几乎不用感知大变化，还是像以前一样用 `Loan`，只是 `Loan` 内部不再写死规则。

### B. `Main.java` 应该怎么改

`Main` 不需要大改。

因为 Requirement 1 的目标不是重写交互层，而是把规则抽离。

#### `Main` 建议保留

- 菜单逻辑
- 输入循环
- 数组存储 `Loan[] loans`
- 所有控制台输出流程

#### `Main` 需要小改的地方

把当前直接写死的业务规则判断，也尽量改成调接口：

- `readValidName(...)` 不直接绑死 regex，而是通过规则对象判断
- repayment 输入校验，也尽量通过规则对象判断
- 如果有“只展示 outstanding loan”的规则，也可以保留在 `Main`，但判定依据应该来自 `loan.calculateRemainingBalance()` 或 `loan.getLoanStatus()`，不要额外发明一套新规则

#### 重点

`Main` 不需要变成完美架构，只需要：

- 少写业务规则
- 多调用规则接口和 `Loan` 的现有行为

## 六、Requirement 1 第一轮迭代结果

已完成 Requirement 1 的第一轮迭代，按“最小改动、不中断现有功能”的原则落地了接口分离。

### 本轮处理结果

- 新增业务规则接口，形成未来政策变更的扩展点
- 新增默认规则实现，承接当前系统既有逻辑
- 让 `Loan` 改为委托接口处理业务规则
- 让 `Main` 的重复校验复用同一套规则
- 编译通过
- 多组流程回归验证通过

## 七、这次具体改了什么

### 1. 新增接口 `src/LoanRules.java`

把“会变化的业务规则”抽象出来，包括：

- 姓名校验
- 还款输入校验
- 贷款金额校验
- 初始已付款校验
- 已付款归一化（超额截断）
- 剩余金额计算
- 状态判定
- 还款应用规则

这一步的意义是：

> 以后如果银行政策变化，不必直接改 `Loan`，可以优先替换规则实现类。

### 2. 新增默认实现 `src/DefaultLoanRules.java`

把你当前系统里已有的规则逻辑，完整搬到默认实现中，保持现有行为不变：

- 姓名必须符合当前规则
- 贷款金额必须大于 `0`
- 初始已付款必须非负
- 超额初始付款会被截断到贷款金额
- 剩余金额不会小于 `0`
- 状态仍然是 `Completed / Outstanding`
- 已完成贷款不能继续付款
- 超额还款仍然会被截断

### 3. 修改 `src/Loan.java`

`Loan` 现在不再自己硬编码主要业务规则，而是：

- 默认依赖 `DefaultLoanRules`
- 构造函数里的校验改为调用 `LoanRules`
- `calculateRemainingBalance()` 改为委托规则对象
- `getLoanStatus()` 改为委托规则对象
- `makePayment()` 改为委托规则对象

同时我保留了：

- 原有字段
- 原有 ID 自增逻辑
- 原有主要公开方法名
- 原有超额支付提示机制的外部行为

所以从外部使用角度，系统基本没变。

### 4. 修改 `src/Main.java`

把 `Main` 里原本直接写死的一些规则判断，也改为通过同一个规则对象复用：

- `isValidName()` 现在走 `LoanRules`
- repayment 输入格式校验现在走 `LoanRules`

这样做的好处是：

- 输入层和实体层不会各自维护不同规则
- 业务规则有统一来源
- 更符合审计要求里“规则抽离”的意思

## 八、这轮改动为什么符合“最小变更”

这版实现刻意避免了大改：

- 没有重写系统
- 没有改菜单结构
- 没有改存储方式
- 没有删原有功能
- 没有大规模拆分类层次

只是增加：

- 一个接口 `LoanRules`
- 一个默认实现 `DefaultLoanRules`

然后让现有 `Loan / Main` 接上去。
