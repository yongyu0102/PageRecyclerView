# PageRecyclerView 
## 1  需求分析

最近公司项目要实现一个需求要满足以下功能：

1）显示一个 list 列表， item 数量不固定。

2）实现翻页功能，一次翻一页。

3）实现翻至某一页功能。

下面介绍通过 RecyclerView 实现该需求的实现过程（效果图如下）。

 ![showimage](https://github.com/yongyu0102/WeeklyBlogImages/blob/master/phase10/showimage.gif?raw=true)



#Usage

```java
  PagingScrollHelper scrollHelper = new PagingScrollHelper();
  scrollHelper.setUpRecycleView(recyclerView);
```
**Note**

由于使用了 RecyclerView 的 OnFlingListener，所以 RecycleView 的版本必须要 recyclerview-v7:25.0.0 以上。



