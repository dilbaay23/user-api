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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

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






}
