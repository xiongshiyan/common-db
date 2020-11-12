# common-db
数据库、SQL查询一些方法的提取.
## 使用方式
下载本项目，gradle clean build得到的jar包引入工程即可。本项目依赖于[utils](https://gitee.com/xxssyyyyssxx/utils)

version:1.8.5

#### 1.直接导入 
compile 'top.jfunc.common:common-db:${version}'
#### 2.其中的 Pagination和PageConverter是针对spring和hibernate环境的，不需要的话可以排除
compile ('top.jfunc.common:common-db:${version}'){
        exclude group:'org.springframework.data'
        exclude group:'org.hibernate'
    }

### 1.QueryHelper

一个巨好用的SQL拼写器，让你从SQL拼写的泥沼中解脱出来，不用管空格，顺序，参数处理。还支持方法连缀。

### 2.SQLFormatter

可以对SQL语句进行美化。

### 4.QueryWithFilter

查询表数据，复杂的过滤在Java应用层做，SQL中只写很容易过滤的条件，基于一个事实：数据库过滤复杂条件效率不高。方法接受一个`RecordFilter`,实现对Record的过滤。

```java
@FunctionalInterface
public interface Filter<T> extends Predicate<T>{
    /**
     * 看一条记录是否满足条件
     * @param record 一条记录
     * @return 是否满足
     */
    @Override
    boolean test(T record);
    /**
     * 过滤记录
     * @param records 源记录，同时也是过滤后的
     * @return 满足条件的记录条数
     */
    default List<T> filter(List<T> records){
        Objects.requireNonNull(records);
        return records.parallelStream().filter(this::test).collect(Collectors.toList());

        /*Iterator<Record> iterator = records.iterator();
        while (iterator.hasNext()) {
            Record record = iterator.next();
            boolean acc = test(record);
            if(!acc){
                //不满足的删除
                iterator.remove();
            }
        }*/
    }
}
```

### 4.Pagination

处理JPA环境下的复杂SQL查询,配合QueryHelper有着奇妙的化学反应