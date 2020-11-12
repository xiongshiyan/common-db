package db;

import org.junit.Test;
import top.jfunc.common.db.bean.Record;
import top.jfunc.common.db.sqlfilter.AbstractFilter;
import top.jfunc.common.db.sqlfilter.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiongshiyan at 2017/12/13
 */
public class DbTest {
    @Test
    public void testQueryRemove() throws Exception{

        List<Record> lists = new ArrayList<>(200);
        for (int i = 0; i < 200; i++) {
            Record record = new Record();
            record.set("id",i);
            lists.add(record);
        }

        Filter<Record> filter = new AbstractFilter() {
            @Override
            public boolean test(Record record) {
                return record.getInt("id") % 10 == 0;
            }
        };

        List<Record> accept = filter.filter(lists);
        System.out.println(accept.size());
        for (int i = 0,size = accept.size(); i < size; i++) {
            System.out.println(accept.get(i));
        }
    }
    @Test
    public void testQueryRemove2() throws Exception{

        List<Record> lists = new ArrayList<>(200);
        for (int i = 0; i < 200; i++) {
            Record record = new Record();
            record.set("id",i);
            lists.add(record);
        }

        Filter<Record> filter = record -> (record.getInt("id") % 10 == 0);

        List<Record> accept = filter.filter(lists);
        System.out.println(accept.size());
        for (int i = 0,size = accept.size(); i < size; i++) {
            System.out.println(accept.get(i));
        }
    }
}
