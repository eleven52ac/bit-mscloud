package com.bit.test.rpc.consumer;


import com.bit.rpc.core.config.RpcConfig;
import com.bit.rpc.core.proxy.ServiceProxyFactory;
import com.bit.rpc.core.utils.ConfigUtils;
import com.bit.test.rpc.api.user.UserService;
import com.bit.test.rpc.api.user.model.User;

/**
 * 服务消费者示例
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public class ConsumerExample {

    public static void main(String[] args) {

        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
        // 服务提供者初始化
        //ConsumerBootstrap.init();

        // 获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("yupi");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
        System.out.println(userService.getNumber());
    }
}
