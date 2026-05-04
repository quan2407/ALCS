package com.example.coreservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class GoogleUserInfo {
    private String email;
    private String name;
    private String picture;
    private String sub;

    public GoogleUserInfo(Map<String, Object> data) {
        this.email = (String) data.get("email");
        this.name = (String) data.get("name");
        this.picture = (String) data.get("picture");
        this.sub = (String) data.get("id");
    }
}

