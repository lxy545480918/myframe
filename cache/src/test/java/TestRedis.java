import com.liu.cache.RedisUtil;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestRedis {

    @Test
    public void testRedis() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-redis.xml");
        RedisUtil redisUtil = (RedisUtil) context.getBean("redisUtil");
//        redisUtil.set("name3","liuxy");
        String name = (String) redisUtil.get("name3");
        System.out.println(name);


    }
}
