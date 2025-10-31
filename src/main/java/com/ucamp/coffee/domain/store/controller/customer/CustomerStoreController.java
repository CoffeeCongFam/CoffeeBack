package com.ucamp.coffee.domain.store.controller.customer;

import com.ucamp.coffee.domain.store.service.helper.StoreHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stores/customers")
@RequiredArgsConstructor
public class CustomerStoreController {
    private final StoreHelperService service;
}
