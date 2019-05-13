# 问题记录
## 1. 当对同一个SubReactor线程中注册Channel时可能会阻塞MainReactor线程
- 问题原因：因为SubReactor线程调用自己内部维护的Selector的select()来监控Channel时，会提前获取Selector内部SelectionKey的锁，获取以后会挂起并且不释放。
此时MainReactor线程向SubReactor中的Selector注册Channel时，需要获取到Selector内部SelectionKey的锁，而锁被SubReactor持有，所以会阻塞MainReactor。
- 临时解决办法： 将select()设置为超时 => select(times)。不过还是有一定概率会阻塞一定时间