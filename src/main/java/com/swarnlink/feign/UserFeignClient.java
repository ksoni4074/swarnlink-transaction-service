package com.swarnlink.feign;


import com.swarnlink.config.FeignUserAuthConfig;
import com.swarnlink.dtos.UserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

@FeignClient(name = "user-service", url = "${user.service.url}", configuration = FeignUserAuthConfig.class)
public interface UserFeignClient {

    @PostMapping("/api/v1/user/internal/users/info")
    List<UserInfoDto> getUserInfo(@RequestBody Set<Long> userIds);
}