package com.farmora.controller;

import com.farmora.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/overview")
    public Map<String, Object> overview() {
        return adminService.overview();
    }
}
