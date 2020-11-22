package com.ifelseco.userapi.model;

import lombok.Data;

@Data
public class UpdateModel {

        private BaseResponseModel responseModel;

        private String firstName;
        private String lastName;
        private String userName;
        private String email;

    }


