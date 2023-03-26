# 请求异步隔离组件

## 背景

- 因为大部分情况，请求的处理非常依赖web容器的线程池。
- 而生产上因为一些后端服务的编码或者中间件的稳定性问题会导致web容器的线程持续阻塞，甚至会导致机器实例的资源分配出现瓶颈
- 这些都会导致web容器的可用性降低，尤其是并发高之后，无论是高性能，还是高可用都很有挑战

## 思路

- （这里tomcat举例）因为tomcat线程囊括了接收到acceptor线程分配之后的一切操作，包括
  - CoyoteAdapter的请求解析
  - valve，filter，servlet的处理
  - spring web的servlet单入口处理
  - 我们自己的业务处理
- 考虑将一个线程里面做的这么多事，使用隔离的思路去分割
- 二来借助servlet3的异步特性，在服务端单服务内来做超时，控制细粒度的业务处理时间
- 使用业务线程池去接管业务自身的操作，方便灵活操作，无论是
  - 日志
  - 核心/非核心业务的二级隔离
  - 方便降级
  - 参数监控
- 实际使用的时候，可以参考实际场景，因为隔离目的是高可用，可以提升高并发之下的吞吐，但是对于响应时间并不会减少，甚至并发高之后，会有一定的上升

## 组成分析

### BizAsyncContext

- 业务异步处理容器，负责提供对外的入口，传入标准请求和业务处理。
- 标准实现CustomizedAsyncContext
- 初始化业务线程池，默认参数取自tomcat线程池

### BizCallableAdapter

- 用于业务处理核心逻辑返回对象的处理
- 支持业务处理返回Demo.AsyncResult/String（代表一般串行业务返回对象或者字符串直接返回）或者CompletableFuture（代表后续并发任务衔接）

### DefaultAsyncListener

- 默认实现的异步请求监听器
- 可以进行超时/错误提醒或者降级

### DefaultRejectedExecutionHandler

- 默认线程池拒绝策略
- 可以进行提醒或者降级

### WrappedCallable

- 包括核心逻辑执行，参考feign的线程池执行策略，传递异步请求上下文
- 这里不做公共的关闭上下文的操作，因为需要支持可能的异步衔接操作

### AsyncResultReturnValueHandler

- 与spring web处理请求生命周期对接，避免再次写响应报错

### AsyncPoolAspect

- 提供的业务对接`BizAsyncContext`的方式，也可以显示的调用