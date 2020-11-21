package com.ifelseco.userapi.controller;

import com.ifelseco.userapi.config.SecurityUtility;
import com.ifelseco.userapi.entity.Role;
import com.ifelseco.userapi.entity.User;
import com.ifelseco.userapi.entity.UserRole;
import com.ifelseco.userapi.model.RegisterModel;
import com.ifelseco.userapi.repository.UserRepository;
import com.ifelseco.userapi.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(path = "/register")
public class RegisterController {

    private UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity register(@RequestBody RegisterModel registerModel) throws Exception {

        ModelMapper modelMapper = new ModelMapper();


        // is email already register?

        if(userService.findByEmail(registerModel.getEmail())!=null) {
            return new ResponseEntity("Email has already registered", HttpStatus.BAD_REQUEST);
        }else if(userService.findByUsername(registerModel.getUsername())!=null)  {
            return new ResponseEntity("Username has already registered", HttpStatus.BAD_REQUEST);
        }else {

            User savingUser=modelMapper.map(registerModel,User.class);
            savingUser.setPassword(SecurityUtility.passwordEncoder().encode(savingUser.getPassword()));

            Role role=new Role();
            role.setRoleId(1);
            role.setName("ROLE_USER");

            Set<UserRole> userRoles=new HashSet<>();
            userRoles.add(new UserRole(savingUser,role));

            try {
                savingUser=userService.createUser(savingUser,userRoles);

                return new ResponseEntity("User registered successfully, userId: "+savingUser.getId(), HttpStatus.OK);

            }catch(Exception e) {
                return new ResponseEntity("Db Error", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }



    }

}
