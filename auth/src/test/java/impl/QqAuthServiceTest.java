package impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.r.base.auth.pojo.ThirdPartyInfoBO;
import org.r.base.auth.service.impl.QqAuthServiceImpl;
import org.r.base.auth.service.impl.WechatAuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/auth-context.xml")
public class QqAuthServiceTest {

    @Autowired
    private QqAuthServiceImpl qqAuthService;
    @Autowired
    private WechatAuthServiceImpl wechatAuthService;

    @Test
    public void login() {

        ThirdPartyInfoBO login = wechatAuthService.login("061Xm9yL1bPQw81OfNuL1f27yL1Xm9ym");
        System.out.println(login);
    }
}