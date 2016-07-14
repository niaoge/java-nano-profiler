# java-nano-profiler



Introduction
---

- git clone https://github.com/niaoge/java-nano-profiler.git
- have fun.

as run demo ,
by use aspectj,you can get all method profile(s) like this ,just copy to excel.
用aspectj, 可对执行链上一组方法集合找出情能瓶颈,不仅仅对spring bean，对任何.class文件均对测出实时性能，日志如下，复制到excel内即能自动对齐。

method	invokes 	duration(total ms)	max(ms)	min(ms)	avg(ms)	duration-self(ms)	max-self(ms)	min-self	avg-self	% of self	% of total
com.niaoge.web.controller.WelcomeController.printWelcome(...)	10	1262	130	118	126	0	0	0	0	0.01	0.01
 com.niaoge.service.HelloWorldService.sayHello(...)	10	1262	130	118	126	262	30	18	26	20.81	20.81
  com.niaoge.service.HellowWorldDao.sayHello(...)	10	999	100	99	99	999	100	99	99	100	79.16



