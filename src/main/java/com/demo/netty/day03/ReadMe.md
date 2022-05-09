###功能
FixedLengthFrameDecoder的类是ByteToMessageDecoder的实现，它用于产生固定大小的Frame。如果没有足够的数据，它将等待下一个数据块的到来，并再次检查是否能够产生一个新的Frame。

###使用
```shell script
//启动server
java PlainOioServer

//启动client
java Client 
```