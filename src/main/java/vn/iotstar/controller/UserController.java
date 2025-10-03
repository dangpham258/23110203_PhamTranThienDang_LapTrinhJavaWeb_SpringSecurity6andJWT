package vn.iotstar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iotstar.dto.CreateUserRequest;
import vn.iotstar.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/new")
    public String addUser(@RequestBody CreateUserRequest req) {
        String[] roles = req.getRoles() != null && req.getRoles().length > 0
                ? req.getRoles()
                : new String[]{"ROLE_USER"};
        userService.createUser(req.getUsername(), req.getEmail(), req.getPassword(), roles);
        return "Thêm user thành công!";
    }
}
