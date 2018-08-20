package top.jfunc.common.db.sqlfilter;

import top.jfunc.common.db.bean.Record;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author 熊诗言 2017/01/07
 * Record过滤器抽象类，提供一些通用的比较方法
 */
public abstract class AbstractFilter implements Filter<Record> {
    /**
     * 模拟LIKE
     */
    protected boolean attrContains(Record record,String attr,String except){
        return null != record.getStr(attr) && record.getStr(attr).contains(except);
    }
    protected boolean attrRegexMatch(Record record,String attr,String regex){
        return null != record.getStr(attr) && record.getStr(attr).matches(regex);
    }

    /**
     * 比较相等
     */
    protected boolean attrEquals(Record record,String attr,String except){
        return null != record.getStr(attr) && except.equals(record.getStr(attr));
    }
    protected boolean attrEquals(Record record,String attr,int except){
        return null != record.getInt(attr) && except == record.getInt(attr);
    }
    protected boolean attrEquals(Record record,String attr,long except){
        return null != record.getLong(attr) && except == record.getLong(attr);
    }
    protected boolean attrEquals(Record record,String attr,float except){
        return null != record.getFloat(attr) && except == record.getFloat(attr);
    }
    protected boolean attrEquals(Record record,String attr,double except){
        return null != record.getDouble(attr) && except == record.getDouble(attr);
    }
    protected boolean attrEquals(Record record,String attr,Date except){
        return null != record.getDate(attr) && except.getTime() == record.getDate(attr).getTime();
    }
    protected boolean attrEquals(Record record,String attr,Time except){
        return null != record.getTime(attr) && except.getTime() == record.getTime(attr).getTime();
    }
    protected boolean attrEquals(Record record,String attr,Timestamp except){
        return null != record.getTimestamp(attr) && except.compareTo(record.getTimestamp(attr)) == 0;
    }

    /**
     * Greater than or Equals 比较 >=，reverse就是 <
     */
    protected boolean attrGE(Record record,String attr,int except){
        return null != record.getInt(attr) && record.getInt(attr) >= except;
    }
    protected boolean attrGE(Record record,String attr,long except){
        return null != record.getLong(attr) && record.getLong(attr) >= except;
    }
    protected boolean attrGE(Record record,String attr,float except){
        return null != record.getFloat(attr) && record.getFloat(attr) >= except;
    }
    protected boolean attrGE(Record record,String attr,double except){
        return null != record.getDouble(attr) && record.getDouble(attr) >= except;
    }
    protected boolean attrGE(Record record,String attr,Date except){
        return null != record.getDate(attr) && record.getDate(attr).getTime() >= except.getTime();
    }
    protected boolean attrGE(Record record,String attr,Time except){
        return null != record.getTime(attr) && record.getTime(attr).getTime() >= except.getTime();
    }
    protected boolean attrGE(Record record,String attr,Timestamp except){
        return null != record.getTimestamp(attr) && record.getTimestamp(attr).getTime() >= except.getTime();
    }

    /**
     * Less than or Equals 比较 <=，reverse就是 >
     */
    protected boolean attrLE(Record record,String attr,int except){
        return null != record.getInt(attr) && record.getInt(attr) <= except;
    }
    protected boolean attrLE(Record record,String attr,long except){
        return null != record.getLong(attr) && record.getLong(attr) <= except;
    }
    protected boolean attrLE(Record record,String attr,float except){
        return null != record.getFloat(attr) && record.getFloat(attr) <= except;
    }
    protected boolean attrLE(Record record,String attr,double except){
        return null != record.getDouble(attr) && record.getDouble(attr) <= except;
    }
    protected boolean attrLE(Record record,String attr,Date except){
        return null != record.getDate(attr) && record.getDate(attr).getTime() <= except.getTime();
    }
    protected boolean attrLE(Record record,String attr,Time except){
        return null != record.getTime(attr) && record.getTime(attr).getTime() <= except.getTime();
    }
    protected boolean attrLE(Record record,String attr,Timestamp except){
        return null != record.getTimestamp(attr) && record.getTimestamp(attr).getTime() <= except.getTime();
    }

    /*@Override
    public List<Record> test(List<Record> records) {
         //从后往前遍历删除，移动的元素更少，效率更高
        for (int i = records.size()-1; i >= 0 ; i--) {
            boolean acc = test(records.get(i));
            if(!acc){
                records.remove(i);
            }
        }
        return records;
    }*/
}