package com.amit;

import com.amit.beans.definition.BeanDefinition;
import com.amit.beans.factory.support.DefaultBeanFactory;
import com.amit.constant.BeanType;
import com.amit.repository.UserRepository;
import com.amit.service.A;
import com.amit.service.B;
import com.amit.service.UserService;

import java.util.concurrent.CountDownLatch;

public class TestApp {
    public static void main(String[] args) throws InterruptedException {
        DefaultBeanFactory factory = new DefaultBeanFactory();

        factory.registerBeanDefinition(UserRepository.class,
                new BeanDefinition(UserRepository.class, BeanType.SINGLETON));
        factory.registerBeanDefinition(UserService.class,
                new BeanDefinition(UserService.class, BeanType.SINGLETON));

        factory.registerBeanDefinition(A.class,
                new BeanDefinition(A.class, BeanType.SINGLETON));
        factory.registerBeanDefinition(B.class,
                new BeanDefinition(B.class, BeanType.SINGLETON));
        int threadCount =10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        UserService[] instance = new UserService[threadCount];

        for (int i =0; i< threadCount; i++){
            int idx =i;
            new Thread(()-> {
                instance[idx] = factory.getBean(UserService.class);
                latch.countDown();
            }).start();
        }
        latch.await();

        for (int i = 1; i < threadCount; i++) {
            System.out.println(
                    instance[0] == instance[i]
            );
        }

        factory.getBean(A.class);

       /* UserService service = factory.getBean(UserService.class);
        service.process();*/


    }
}
