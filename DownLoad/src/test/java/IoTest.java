import org.junit.Test;
import redis.clients.jedis.Jedis;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class IoTest {

    @Test
    public void tt() {
//        System.out.println(new File("./templates/SampleCode/DownLoad/images/DownLoadTrue.png").exists());
//        System.out.println(System.getProperty("user.dir"));
        //连接本地的 Redis 服务
//        Jedis jedis = new Jedis("localhost");
//        // 如果 Redis 服务设置来密码，需要下面这行，没有就不需要
//        // jedis.auth("123456");
//        System.out.println("连接成功");
//
//        //查看服务是否运行
//        System.out.println("服务正在运行: "+jedis.ping());
//        //获取数据长度
//        Long num = jedis.llen("taskList");
//        System.out.println(num);
//        //遍历
//        for(int i=0;i<num;i++){
//            List<String> values = jedis.lrange("taskList", i, i );
//            System.out.println(values);
//        }
        System.out.println(new File("./images/DownLoadTrue.png").exists());
    }
}
