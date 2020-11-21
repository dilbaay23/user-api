package com.ifelseco.userapi.model;

import lombok.Data;

@Data
public class UpdateModel {

        private BaseResponseModel responseModel;
        private String email;
        private String firstname;
        private String lastname;
        private String username;

    }


