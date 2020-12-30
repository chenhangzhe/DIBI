package cn.suozhi.DiBi.common.util;

import com.activeandroid.Model;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import cn.suozhi.DiBi.home.model.QuoteEntity;

/**
 * 数据库工具类
 */
public class DataBaseUtil {

    public static Class clsQuote = QuoteEntity.class;

    public static void addSearch(String symbol) {
        QuoteEntity search = selectSingle(clsQuote, "symbol", symbol);
        if (search != null) {
            search.delete();
        }
        new QuoteEntity().setSymbol(symbol).save();
    }

    public static <T extends Model> List<T> select(Class cls) {
        return new Select().from(cls).execute();
    }

    /**
     * 倒序输出
     */
    public static <T extends Model> List<T> selectDesc(Class cls) {
        return new Select().from(cls).orderBy("id desc").execute();
    }

    public static <T extends Model> T selectSingle(Class cls, String clause, Object arg) {
        return new Select().from(cls)
                .where(clause + "=?", arg)
                .executeSingle();
    }

    public static void delete(Class cls, String clause, Object arg) {
        new Delete().from(cls)
                .where(clause + "=?", arg)
                .execute();
    }

    /**
     * 清空
     */
    public static void clear() {
        clear(clsQuote);
    }

    public static void clear(Class cls) {
        new Delete().from(cls).execute();
    }
}
