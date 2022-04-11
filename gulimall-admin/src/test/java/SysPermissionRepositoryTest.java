import com.laughingather.gulimall.admin.entity.SysPermissionEntity;
import com.laughingather.gulimall.admin.repository.SysPermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author：laughingather
 * @create：2021-12-02 2021/12/2
 */
@Slf4j
@SpringBootTest(classes = com.laughingather.gulimall.admin.GulimallAdminApplication.class)
public class SysPermissionRepositoryTest {

    @Resource
    private SysPermissionRepository sysPermissionRepository;


    @Test
    public void testFindAll() {
        List<SysPermissionEntity> all = sysPermissionRepository.findAll();
        log.info(all.toString());
    }

}
