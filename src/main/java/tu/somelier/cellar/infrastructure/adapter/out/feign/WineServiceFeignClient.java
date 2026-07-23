package tu.somelier.cellar.infrastructure.adapter.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "wine-service", url = "${wine.service.url:http://wine-service}")
public interface WineServiceFeignClient {

    @GetMapping("/wines/{id}")
    WineDetailsDto getWineById(@PathVariable("id") UUID id);
}
