package com.pitang.car_users_backend.dto;

import java.time.LocalDate;
import java.util.List;

public class UserEntityDTO {
    public String firstName;
    public String lastName;
    public String email;
    public LocalDate birthday;
    public String login;
    public String phone;
    public List<CarDTO> cars;
}
