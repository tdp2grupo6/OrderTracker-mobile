package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AutenticacionResponse {

    private static final String USERNAME = "username";
    private static final String ROLES = "roles";
    private static final String TOKEN_TYPE = "token_type";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String EXPIRES_IN = "expires_in";
    private static final String REFRESH_TOKEN = "refresh_token";

    public JSONObject response;
    public String username;
    public JSONArray roles;
    public String tokenType;
    public int expiresIn;
    public String accessToken;
    public String refreshToken;

    public AutenticacionResponse() {
        super();
    }

    public AutenticacionResponse(String str) {
        super();
        this.desempaquetar(str);
    }

    public String getAutenticationHeaderKey() {
        return "Authorization";
    }

    public String getAutenticationHeaderValue() {
        return this.tokenType + " " + this.accessToken;
    }

    private void desempaquetar(String response) {
        try {
            /*
            {
                "username": "vendedor",
                "roles": [ "ROLE_VENDEDOR" ],
                "token_type": "Bearer",
                "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE0NjI0Nzg4MzEsInN1YiI6InZlbmRlZG9yIiwicHJpbmNpcGFsIjoiSDRzSUFBQUFBQUFBQUpWU1BVOFVRUmgrZHowQ0NVRStFa2tzc0FFN3M1ZG9lWTE4SENabWMwYzR6d0lUemR6dXl6b3dPN1BPekI1M2pibEtDZ3FJUUVMaVgrQ2ZhTU1QSUZyWVV0djZ6bkt3aHcxeHFzMDd6ejVmNzV4ZndaalI4Q0xSakFzVFpDSlB1QXhNcHJsTURFYTU1cllmNUFaMWpMWkF2Q3FBYlpyQTlmRjg4RUx3ZVd4aEx0eGhYVllWVENiVlptY0hJMXZyYVhpdWRESmszTllzeFQybGQ0TmI3a2hwdkNOUVVuc3ZmUmpmZ2xrV1JTcVh0cUZrdlpkeGpmRVd6SlN6VUVXN2J2UW9vaHVVbGpOaFJxSGpLRmxIWUJ6Q0pNdnRSMFdxSEkyRjZXdXp1ZVdpMmtKYkMyRWlZOGFRdTMrU3RLeXo3dTZkVFVrSlBzRm5xUFF5anc1MTk5UkJBOGNUckNvaEtEVlgwaXkxWmFwaXZzMmRPUEVQRnI1ZUhINGJ0SDBBNnVUWlwvZitVODhjck1QaitcL3MrVG9tZ3ZzakFcL1lyMkUxWG9adVprdG1kOW9kTXFYWnh2SHAxZjc3eDZRc2tPc1wvXC84K2xwYUh6ZlZYVlpveHphd2EyUkhSN2xYY041R3YzRTkrczRWKzBPSnBKcEJlbExRWTMwcVV4QlMzb3BXNDZkdkMxR1l6ckg5NFcyK3MxZGVhbTI0eTBVVVpZNncwYVQ4c29ydWRCYUdpalIzOFB2cHh1UGlUZUY3RFdKZUpIS241bVJMVXlOTU82aVwvbnB3dVRKNzhPaWh6RE4rM1wvQlJWTW5vWVhBd0FBIiwicm9sZXMiOlsiUk9MRV9WRU5ERURPUiJdLCJpYXQiOjE0NjI0NzUyMzF9.c2lYN9b5hxbdlAK9jwejYb9S3Se57qdRJax9ibj8rvA",
                "expires_in": 3600,
                "refresh_token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2ZW5kZWRvciIsInByaW5jaXBhbCI6Ikg0c0lBQUFBQUFBQUFKVlNQVThVUVJoK2R6MENDVUUrRWtrc3NBRTdzNWRvZVkxOEhDWm1jMGM0endJVHpkenV5em93TzdQT3pCNTNqYmxLQ2dxSVFFTGlYK0NmYU1NUElGcllVdHY2em5Ld2h3MXhxczA3eno1Zjc1eGZ3WmpSOENMUmpBc1RaQ0pQdUF4TXBybE1ERWE1NXJZZjVBWjFqTFpBdkNxQWJackE5ZkY4OEVMd2VXeGhMdHhoWFZZVlRDYlZabWNISTF2cmFYaXVkREprM05Zc3hUMmxkNE5iN2tocHZDTlFVbnN2ZlJqZmdsa1dSU3FYdHFGa3ZaZHhqZkVXekpTelVFVzdidlFvb2h1VWxqTmhScUhqS0ZsSFlCekNKTXZ0UjBXcUhJMkY2V3V6dWVXaTJrSmJDMkVpWThhUXUzK1N0S3l6N3U2ZFRVa0pQc0ZucVBReWp3NTE5OVJCQThjVHJDb2hLRFZYMGl5MVphcGl2czJkT1BFUEZyNWVISDRidEgwQTZ1VFpcL2YrVTg4Y3JNUGorXC9zK1RvbWd2c2pBXC9ZcjJFMVhvWnVaa3RtZDlvZE1xWFp4dkhwMWY3N3g2UXNrT3NcL1wvOCtscGFIemZWWFZab3h6YXdhMlJIUjdsWGNONUd2M0U5K3M0ViswT0pwSnBCZWxMUVkzMHFVeEJTM29wVzQ2ZHZDMUdZenJIOTRXMitzMWRlYW0yNHkwVVVaWTZ3MGFUOHNvcnVkQmFHaWpSMzhQdnB4dVBpVGVGN0RXSmVKSEtuNW1STFV5Tk1PNmlcL25wd3VUSjc4T2loekROKzNcL0JSVk1ub1lYQXdBQSIsInJvbGVzIjpbIlJPTEVfVkVOREVET1IiXSwiaWF0IjoxNDYyNDc1MjMxfQ.qiUwvHLi-E1n0kDrk2nR9ymeGsxOelTHVpHfqzKfA34"
            }
            */

            this.response = new JSONObject(response);
            this.username = this.response.getString(USERNAME);
            this.roles = this.response.getJSONArray(ROLES);
            this.tokenType = this.response.getString(TOKEN_TYPE);
            this.expiresIn = this.response.getInt(EXPIRES_IN);
            this.accessToken = this.response.getString(ACCESS_TOKEN);
            this.refreshToken = this.response.getString(REFRESH_TOKEN);

        } catch (Exception e) {

        }
    }

}
