package com.ifelseco.userapi.service.impl;

import com.ifelseco.userapi.config.ConstApp;
import com.ifelseco.userapi.entity.ConfirmUserToken;
import com.ifelseco.userapi.entity.User;
import com.ifelseco.userapi.entity.UserRole;
import com.ifelseco.userapi.model.EmailModel;
import com.ifelseco.userapi.repository.RoleRepository;
import com.ifelseco.userapi.repository.UserRepository;
import com.ifelseco.userapi.service.ConfirmUserService;
import com.ifelseco.userapi.service.EmailService;
import com.ifelseco.userapi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;


@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG= LoggerFactory.getLogger(UserServiceImpl.class);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfirmUserService confirmUserService;

    @Transactional
    public User createUser(User user, Set<UserRole> userRoles) {

        User savedUser=userRepository.findByUsername(user.getUsername());

        if (savedUser!=null) {
            LOG.info("User with username {} already exist."+user.getUsername());

        }else{
            for (UserRole ur : userRoles) {
                roleRepository.save(ur.getRole());
            }
            user.getUserRoles().addAll(userRoles);


            savedUser=userRepository.save(user);

            if(savedUser.getId()>-1) {
                try {
                    sendConfirmEmail(savedUser,emailService);
                }catch(Exception e) {
                    LOG.error("Email error {} "+e.getMessage());
                }
            }


        }

        return savedUser;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    private void sendConfirmEmail(User savedUser, EmailService emailService) {

        // create confirm token
        // send it to user via link

        ConfirmUserToken confirmUserToken=new ConfirmUserToken();
        confirmUserToken.setToken(UUID.randomUUID().toString());
        confirmUserToken.setExpiryDate(60*24);
        confirmUserToken.setUser(savedUser);
        confirmUserService.save(confirmUserToken);



        Map<String, Object> model = new HashMap<>();
        model.put("firstName",savedUser.getFirstname());
        model.put("confirmUrl", ConstApp.WEB_URL+"/user/confirm-email?uuid="+confirmUserToken.getToken());
        model.put("signature","User-Api");

        EmailModel emailModel=new EmailModel(ConstApp.FROM_EMAIL,savedUser.getEmail(),"User-Api: E-posta doğrulama",model);

        emailService.sendEmail(emailModel);

    }

    @Override
    public User findByUsername(String username) {
        // TODO Auto-generated method stub
        return userRepository.findByUsername(username);
    }

//    @Override
//    public Optional<User> findById(Long id) {
//        return userRepository.findById(id);
//    }

    @Override
    public User findByEmail(String email) {

        return userRepository.findByEmail(email);
    }



}
