package pub.guoxin.protocol.conf;

/**
 * Create by guoxin on 2018/6/26
 */
public interface TypeConvert<T> {
    String encode(T t);
    T encode(String hex);
}
