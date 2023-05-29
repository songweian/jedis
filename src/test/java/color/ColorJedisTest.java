package color;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public class ColorJedisTest {

  private static Jedis jedis;
  private final String appkey = "testapp:";

  @Before
  public void before() throws Exception {
    jedis = new Jedis("127.0.0.1", 6379);
  }

  @After
  public void after() {
    if (jedis != null) jedis.close();
  }

  @Test
  public void test() {
    jedis.setKeyPrefix(appkey);
    jedis.set("name".getBytes(StandardCharsets.UTF_8), "jack".getBytes(StandardCharsets.UTF_8));
    jedis.set("age", "234");
    Set<String> keys = jedis.keys("*");
    for (String key : keys) {
      String value = jedis.get(key.replace(appkey, ""));
      System.out.println(key + "=" + value);
    }
  }

}
