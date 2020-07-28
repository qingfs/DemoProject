import com.example.Application;
import com.example.client.SendMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: DemoProject
 * @description:
 * @author: xuyj
 * @create: 2020-07-27 23:26
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class RpcTest {
    @Autowired
    SendMessage sendMessage;

    @Test
    public void test(){
        System.out.println(sendMessage.sendName("hello world"));
    }
}
