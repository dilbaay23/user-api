package com.ifelseco.userapi.controller;


import com.ifelseco.userapi.config.SecurityUtility;
import com.ifelseco.userapi.entity.ConfirmUserToken;
import com.ifelseco.userapi.entity.Role;
import com.ifelseco.userapi.entity.User;
import com.ifelseco.userapi.entity.UserRole;
import com.ifelseco.userapi.model.BaseResponseModel;
import com.ifelseco.userapi.model.RegisterModel;
import com.ifelseco.userapi.model.UpdateModel;
import com.ifelseco.userapi.service.ConfirmUserService;
import com.ifelseco.userapi.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    private ConfirmUserService confirmUserService;
    private UserService userService;

    @Autowired
    public UserController(ConfirmUserService confirmUserService,UserService userService) {
        this.confirmUserService = confirmUserService;
        this.userService=userService;
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<BaseResponseModel> confirmUserEmail(@RequestParam("uuid") String uuid) {

        BaseResponseModel responseModel=new BaseResponseModel();

        try {
            ConfirmUserToken confirmUserToken=confirmUserService.findByToken(uuid);

            if(confirmUserToken==null) {
                // set response message

                responseModel.setResponseCode(400);
                responseModel.setResponseMessage("Geçersiz token...");
                return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
            }else if(confirmUserToken.isExpired()){
                // set response message
                responseModel.setResponseCode(400);
                responseModel.setResponseMessage("Onay maili zaman aşınmına uğradı.");
                return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
            }else {

                try {
                    User user=userService.findByEmail(confirmUserToken.getUser().getEmail());

                    if(user==null) {
                        responseModel.setResponseCode(400);
                        responseModel.setResponseMessage("Geçersiz token...");
                        return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
                    }else {
                        user.setEnabled(true);
                        userService.save(user);
                        responseModel.setResponseCode(200);
                        responseModel.setResponseMessage("Kullanıcı onaylandı. Artık giriş yapabilirsiniz.");
                        return new ResponseEntity<>(responseModel, HttpStatus.OK);
                    }
                }catch(Exception e) {
                    responseModel.setResponseCode(500);
                    responseModel.setResponseMessage("Sistem hatası...");
                    return new ResponseEntity<>(responseModel, HttpStatus.INTERNAL_SERVER_ERROR);
                }


            }

        }catch (Exception e) {
            responseModel.setResponseCode(500);
            responseModel.setResponseMessage("Sistem hatası...");
            return new ResponseEntity<>(responseModel, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    //Update User
    @PutMapping
    public ResponseEntity updateUserInfo(@RequestBody HashMap<String, Object> mapper
    ) throws Exception{

        String email = (String) mapper.get("email");
        String firstName = (String) mapper.get("firstName");
        String lastName = (String) mapper.get("lastName");



        User currentUser = userService.findByEmail(email);

        // is email exist?
        if(currentUser == null) {
            return new ResponseEntity("Email is not found", HttpStatus.BAD_REQUEST);
        }
//        if(currentUser.isEnabled() == false) {
//            return new ResponseEntity("Not Confirmed User", HttpStatus.BAD_REQUEST);
//        }



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
