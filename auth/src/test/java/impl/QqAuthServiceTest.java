package impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.r.base.auth.enums.ThirdPartyTypeEnum;
import org.r.base.auth.pojo.ThirdPartyInfoBO;
import org.r.base.auth.service.AuthServiceProvider;
import org.r.base.auth.service.ThirdPartyAuthService;
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

        ThirdPartyAuthService thirdPartyAuthService = AuthServiceProvider.get(ThirdPartyTypeEnum.WEIXIN);


        ThirdPartyInfoBO login = thirdPartyAuthService.login("011P0XCZ1Ye6nV0GTLEZ1Yo7DZ1P0XCK");
        System.out.println(login);
    }
}