###功能
 - FixedLengthFrameDecoder类是ByteToMessageDecoder的实现，它用于产生固定大小的Frame。如果没有足够的数据，它将等待下一个数据块的到来，并再次检查是否能够产生一个新的Frame。
 - AbsIntegerEncoder类是MessageToMessageEncoder的实现，它用于将channel中的数字转换成其绝对值。

###使用
直接执行src/test/java下面的com.demo.netty.day03下的测试类